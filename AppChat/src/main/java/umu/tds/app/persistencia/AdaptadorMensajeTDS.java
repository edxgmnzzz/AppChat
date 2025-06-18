package umu.tds.app.persistencia;

import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import beans.Entidad;
import beans.Propiedad;
import tds.driver.FactoriaServicioPersistencia;
import tds.driver.ServicioPersistencia;
import umu.tds.app.AppChat.Contacto;
import umu.tds.app.AppChat.ContactoIndividual;
import umu.tds.app.AppChat.Grupo;
import umu.tds.app.AppChat.Mensaje;
import umu.tds.app.AppChat.Usuario;

public class AdaptadorMensajeTDS implements MensajeDAO {
    private static AdaptadorMensajeTDS unicaInstancia = null;
    private static ServicioPersistencia sp;
    private static final String ENTIDAD_MENSAJE = "mensaje";
    private static final Logger LOGGER = Logger.getLogger(AdaptadorMensajeTDS.class.getName());

    private AdaptadorMensajeTDS() {
        sp = FactoriaServicioPersistencia.getInstance().getServicioPersistencia();
    }

    public static AdaptadorMensajeTDS getInstancia() {
        if (unicaInstancia == null) {
            unicaInstancia = new AdaptadorMensajeTDS();
        }
        return unicaInstancia;
    }

    @Override
    public void registrarMensaje(Mensaje mensaje) {
    	if (mensaje == null || mensaje.getCodigo() > 0) return;

        // Asegurar que el emisor esté persistido
        AdaptadorUsuarioTDS.getInstancia().registrarUsuario(mensaje.getEmisor());
        
        // Asegurar que el contacto receptor esté persistido
        if (mensaje.getReceptor() instanceof ContactoIndividual ci) {
            AdaptadorContactoIndividualTDS.getInstancia().registrarContacto(ci);
        } else if (mensaje.getReceptor() instanceof Grupo g) {
            AdaptadorGrupoTDS.getInstancia().registrarGrupo(g);
        }
        Entidad e = new Entidad();
        e.setNombre(ENTIDAD_MENSAJE);
        e.setPropiedades(List.of(
            new Propiedad("texto", mensaje.getTexto()),
            new Propiedad("hora", mensaje.getHora().toString()),
            new Propiedad("emoticono", String.valueOf(mensaje.getEmoticono())),
            new Propiedad("emisor", String.valueOf(mensaje.getEmisor().getId())),
            new Propiedad("receptor", String.valueOf(mensaje.getReceptor().getCodigo())),
            new Propiedad("grupo", String.valueOf(mensaje.getReceptor() instanceof Grupo))
        ));

        e = sp.registrarEntidad(e);
        mensaje.setCodigo(e.getId());
        
        // Guardamos en el pool
     	PoolDAO.getInstancia().addObjeto(mensaje.getCodigo(), mensaje);
        LOGGER.info("Mensaje registrado con ID: " + mensaje.getCodigo());
    }

    @Override
    public Mensaje recuperarMensaje(int codigo) {
        if (codigo <= 0) return null;

        // ✅ 1. Comprobar si ya está en el Pool
        if (PoolDAO.getInstancia().contiene(codigo)) {
            return (Mensaje) PoolDAO.getInstancia().getObjeto(codigo);
        }

        Entidad e = sp.recuperarEntidad(codigo);
        if (e == null) {
            LOGGER.warning("Entidad no encontrada para ID: " + codigo);
            return null;
        }

        try {
            String texto = sp.recuperarPropiedadEntidad(e, "texto");
            String horaTexto = sp.recuperarPropiedadEntidad(e, "hora");
            if (horaTexto == null || horaTexto.isBlank()) {
                LOGGER.severe("Mensaje con ID " + codigo + " no tiene fecha válida (hora nula o vacía)");
                return null;
            }

            LocalDateTime hora = LocalDateTime.parse(horaTexto);
            int emoticono = Integer.parseInt(sp.recuperarPropiedadEntidad(e, "emoticono"));

            // ❗ Crear mensaje sin emisor ni receptor aún (esto es clave)
            Mensaje mensaje = new Mensaje(texto, hora, null, null);
            mensaje.setCodigo(codigo);

            // ✅ 2. Guardar en el Pool ANTES de llamar a otros adaptadores
            PoolDAO.getInstancia().addObjeto(codigo, mensaje);

            // ✅ 3. Recuperar objetos relacionados
            int idEmisor = Integer.parseInt(sp.recuperarPropiedadEntidad(e, "emisor"));
            int codReceptor = Integer.parseInt(sp.recuperarPropiedadEntidad(e, "receptor"));
            boolean esGrupo = Boolean.parseBoolean(sp.recuperarPropiedadEntidad(e, "grupo"));

            Usuario emisor = AdaptadorUsuarioTDS.getInstancia().recuperarUsuario(idEmisor);
            Contacto receptor = esGrupo
                ? AdaptadorGrupoTDS.getInstancia().recuperarGrupo(codReceptor)
                : AdaptadorContactoIndividualTDS.getInstancia().recuperarContacto(codReceptor);

            if (emisor == null || receptor == null) {
                LOGGER.warning("❌ Emisor o receptor nulo al recuperar mensaje con ID " + codigo);
                return null;
            }

            // ✅ 4. Asignar emisor y receptor al mensaje
            mensaje.setEmisor(emisor);
            mensaje.setReceptor(receptor);

            LOGGER.info("📥 Mensaje recuperado: \"" + texto + "\" de " + emisor.getNombre() + " a " + receptor.getNombre());
            return mensaje;
        } catch (Exception ex) {
            LOGGER.severe("Error al recuperar mensaje ID " + codigo + ": " + ex.getMessage());
            return null;
        }
    }


    @Override
    public List<Mensaje> recuperarTodosMensajes() {
        LOGGER.info("Recuperando todos los mensajes desde la base de datos...");
        
        List<Mensaje> mensajes = sp.recuperarEntidades(ENTIDAD_MENSAJE).stream()
            .map(e -> recuperarMensaje(e.getId()))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

        LOGGER.info("Mensajes recuperados: " + mensajes.size());

        for (Mensaje m : mensajes) {
            String emisor = m.getEmisor() != null ? m.getEmisor().getNombre() : "null";
            String receptor = m.getReceptor() != null ? m.getReceptor().getNombre() : "null";
            LOGGER.info("📨 Mensaje de " + emisor + " a " + receptor + ": \"" + m.getTexto() + "\" [ID=" + m.getCodigo() + "]");
        }

        return mensajes;
    }


    @Override
    public void borrarMensaje(Mensaje mensaje) {
        if (mensaje == null || mensaje.getCodigo() <= 0) {
            LOGGER.warning("Mensaje nulo o sin código válido para borrar");
            return;
        }

        try {
            Entidad e = sp.recuperarEntidad(mensaje.getCodigo());
            if (e != null) {
                sp.borrarEntidad(e);
                LOGGER.info("Mensaje borrado con ID: " + mensaje.getCodigo());
            }
        } catch (Exception ex) {
            LOGGER.severe("Error al borrar mensaje ID " + mensaje.getCodigo() + ": " + ex.getMessage());
        }
        
        if (PoolDAO.getInstancia().contiene(mensaje.getCodigo()))
			PoolDAO.getInstancia().removeObjeto(mensaje.getCodigo());
    }

    @Override
    public void modificarMensaje(Mensaje mensaje) {
        if (mensaje == null || mensaje.getCodigo() <= 0) {
            LOGGER.warning("Mensaje nulo o sin código válido para modificar");
            return;
        }

        try {
            Entidad e = sp.recuperarEntidad(mensaje.getCodigo());
            if (e == null) {
                LOGGER.warning("Entidad no encontrada para mensaje ID " + mensaje.getCodigo());
                return;
            }

            sp.eliminarPropiedadEntidad(e, "texto");
            sp.anadirPropiedadEntidad(e, "texto", mensaje.getTexto());

            sp.eliminarPropiedadEntidad(e, "hora");
            sp.anadirPropiedadEntidad(e, "hora", mensaje.getHora().toString());

            sp.eliminarPropiedadEntidad(e, "emoticono");
            sp.anadirPropiedadEntidad(e, "emoticono", String.valueOf(mensaje.getEmoticono()));

            sp.eliminarPropiedadEntidad(e, "emisor");
            sp.anadirPropiedadEntidad(e, "emisor", String.valueOf(mensaje.getEmisor().getId()));

            sp.eliminarPropiedadEntidad(e, "receptor");
            sp.anadirPropiedadEntidad(e, "receptor", String.valueOf(mensaje.getReceptor().getCodigo()));

            sp.eliminarPropiedadEntidad(e, "grupo");
            sp.anadirPropiedadEntidad(e, "grupo", String.valueOf(mensaje.getReceptor() instanceof Grupo));

            LOGGER.info("Mensaje modificado con ID: " + mensaje.getCodigo());
        } catch (Exception ex) {
            LOGGER.severe("Error al modificar mensaje ID " + mensaje.getCodigo() + ": " + ex.getMessage());
        }
    }
}
