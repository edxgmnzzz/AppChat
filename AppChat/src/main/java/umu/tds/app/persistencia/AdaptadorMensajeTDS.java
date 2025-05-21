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
        if (mensaje == null || mensaje.getCodigo() > 0) {
            return;
        }

        LOGGER.info("Registrando mensaje: \"" + mensaje.getTexto() + "\" a " +
            (mensaje.getReceptor() != null ? mensaje.getReceptor().getNombre() : "receptor desconocido"));

        // Asegurar que emisor y receptor están registrados
        AdaptadorUsuarioTDS.getInstancia().registrarUsuario(mensaje.getEmisor());
        if (mensaje.getReceptor() instanceof Grupo g) {
            AdaptadorGrupoTDS.getInstancia().registrarGrupo(g);
        } else if (mensaje.getReceptor() instanceof ContactoIndividual c) {
            AdaptadorContactoIndividualTDS.getInstancia().registrarContacto(c);
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
        LOGGER.info("Mensaje registrado con ID: " + mensaje.getCodigo());
    }

    @Override
    public Mensaje recuperarMensaje(int codigo) {
        if (codigo <= 0) return null;

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
            int idEmisor = Integer.parseInt(sp.recuperarPropiedadEntidad(e, "emisor"));
            int codReceptor = Integer.parseInt(sp.recuperarPropiedadEntidad(e, "receptor"));
            boolean esGrupo = Boolean.parseBoolean(sp.recuperarPropiedadEntidad(e, "grupo"));

            Usuario emisor = AdaptadorUsuarioTDS.getInstancia().recuperarUsuario(idEmisor);
            Contacto receptor = esGrupo
                ? AdaptadorGrupoTDS.getInstancia().recuperarGrupo(codReceptor)
                : AdaptadorContactoIndividualTDS.getInstancia().recuperarContacto(codReceptor);

            if (emisor == null || receptor == null) {
                LOGGER.warning("Emisor o receptor no encontrados para mensaje ID " + codigo);
                return null;
            }

            Mensaje mensaje = new Mensaje(texto, hora, emisor, receptor);
            mensaje.setCodigo(codigo);
            LOGGER.info("Mensaje recuperado: \"" + texto + "\" de " + emisor.getName() + " a " + receptor.getNombre());
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
