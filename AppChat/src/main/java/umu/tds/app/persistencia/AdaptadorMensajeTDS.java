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

/**
 * Adaptador DAO para la entidad {@link Mensaje} usando la capa de persistencia
 * TDS. Permite registrar, recuperar, modificar y borrar mensajes, además de
 * gestionar la caché en memoria mediante PoolDAO.
 */
public class AdaptadorMensajeTDS implements MensajeDAO {

    private static AdaptadorMensajeTDS unicaInstancia = null;
    private static ServicioPersistencia sp;
    private static final String ENTIDAD_MENSAJE = "mensaje";
    private static final Logger LOGGER = Logger.getLogger(AdaptadorMensajeTDS.class.getName());

    /** Constructor privado (patrón Singleton). */
    private AdaptadorMensajeTDS() {
        sp = FactoriaServicioPersistencia.getInstance().getServicioPersistencia();
    }

    /**
     * Devuelve la instancia única del adaptador.
     *
     * @return {@code AdaptadorMensajeTDS} singleton.
     */
    public static AdaptadorMensajeTDS getInstancia() {
        if (unicaInstancia == null) {
            unicaInstancia = new AdaptadorMensajeTDS();
        }
        return unicaInstancia;
    }

    // ─────────────────────────── Registro ───────────────────────────

    /** {@inheritDoc} */
    @Override
    public void registrarMensaje(Mensaje mensaje) {
        if (mensaje == null || mensaje.getCodigo() > 0) return;

        // Asegurar emisor y receptor persistidos
        AdaptadorUsuarioTDS.getInstancia().registrarUsuario(mensaje.getEmisor());
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
        PoolDAO.getInstancia().addObjeto(mensaje.getCodigo(), mensaje);
    }

    // ─────────────────────────── Recuperación ───────────────────────────

    /** {@inheritDoc} */
    @Override
    public Mensaje recuperarMensaje(int codigo) {
        if (codigo <= 0) return null;

        if (PoolDAO.getInstancia().contiene(codigo)) {
            return (Mensaje) PoolDAO.getInstancia().getObjeto(codigo);
        }

        Entidad e = sp.recuperarEntidad(codigo);
        if (e == null) {
            LOGGER.warning("Entidad mensaje no encontrada para ID=" + codigo);
            return null;
        }

        try {
            String texto      = sp.recuperarPropiedadEntidad(e, "texto");
            String horaTexto  = sp.recuperarPropiedadEntidad(e, "hora");
            if (horaTexto == null || horaTexto.isBlank()) {
                LOGGER.severe("Mensaje ID " + codigo + " sin fecha válida");
                return null;
            }
            LocalDateTime hora = LocalDateTime.parse(horaTexto);

            Mensaje mensaje = new Mensaje(texto, hora, null, null);
            mensaje.setCodigo(codigo);
            PoolDAO.getInstancia().addObjeto(codigo, mensaje);

            int idEmisor      = Integer.parseInt(sp.recuperarPropiedadEntidad(e, "emisor"));
            int idReceptor    = Integer.parseInt(sp.recuperarPropiedadEntidad(e, "receptor"));
            boolean esGrupo   = Boolean.parseBoolean(sp.recuperarPropiedadEntidad(e, "grupo"));

            Usuario emisor = AdaptadorUsuarioTDS.getInstancia().recuperarUsuario(idEmisor);
            Contacto receptor = esGrupo
                    ? AdaptadorGrupoTDS.getInstancia().recuperarGrupo(idReceptor)
                    : AdaptadorContactoIndividualTDS.getInstancia().recuperarContacto(idReceptor);

            if (emisor == null || receptor == null) {
                LOGGER.warning("Emisor o receptor nulo al recuperar mensaje ID " + codigo);
                return null;
            }

            mensaje.setEmisor(emisor);
            mensaje.setReceptor(receptor);
            return mensaje;
        } catch (Exception ex) {
            LOGGER.severe("Error al recuperar mensaje ID " + codigo + ": " + ex.getMessage());
            return null;
        }
    }

    // ─────────────────────────── Listado ───────────────────────────

    /** {@inheritDoc} */
    @Override
    public List<Mensaje> recuperarTodosMensajes() {
        return sp.recuperarEntidades(ENTIDAD_MENSAJE).stream()
                .map(e -> recuperarMensaje(e.getId()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    // ─────────────────────────── Borrado ───────────────────────────

    /** {@inheritDoc} */
    @Override
    public void borrarMensaje(Mensaje mensaje) {
        if (mensaje == null || mensaje.getCodigo() <= 0) {
            LOGGER.warning("Intento de borrar mensaje nulo o sin ID válido");
            return;
        }
        try {
            Entidad e = sp.recuperarEntidad(mensaje.getCodigo());
            if (e != null) sp.borrarEntidad(e);
        } catch (Exception ex) {
            LOGGER.severe("Error al borrar mensaje ID " + mensaje.getCodigo() + ": " + ex.getMessage());
        }
        PoolDAO.getInstancia().removeObjeto(mensaje.getCodigo());
    }

    // ─────────────────────────── Modificación ───────────────────────────

    /** {@inheritDoc} */
    @Override
    public void modificarMensaje(Mensaje mensaje) {
        if (mensaje == null || mensaje.getCodigo() <= 0) {
            LOGGER.warning("Intento de modificar mensaje nulo o sin ID válido");
            return;
        }
        try {
            Entidad e = sp.recuperarEntidad(mensaje.getCodigo());
            if (e == null) return;

            reemplazarProp(e, "texto", mensaje.getTexto());
            reemplazarProp(e, "hora", mensaje.getHora().toString());
            reemplazarProp(e, "emoticono", String.valueOf(mensaje.getEmoticono()));
            reemplazarProp(e, "emisor", String.valueOf(mensaje.getEmisor().getId()));
            reemplazarProp(e, "receptor", String.valueOf(mensaje.getReceptor().getCodigo()));
            reemplazarProp(e, "grupo", String.valueOf(mensaje.getReceptor() instanceof Grupo));
        } catch (Exception ex) {
            LOGGER.severe("Error al modificar mensaje ID " + mensaje.getCodigo() + ": " + ex.getMessage());
        }
    }

    // ─────────────────────────── Utilidades ───────────────────────────

    /**
     * Reemplaza una propiedad en la entidad eliminando la anterior y añadiendo
     * la nueva.
     */
    private void reemplazarProp(Entidad e, String clave, String valor) {
        sp.eliminarPropiedadEntidad(e, clave);
        sp.anadirPropiedadEntidad(e, clave, valor);
    }
}
