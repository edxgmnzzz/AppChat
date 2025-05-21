package umu.tds.app.persistencia;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import beans.Entidad;
import beans.Propiedad;
import tds.driver.FactoriaServicioPersistencia;
import tds.driver.ServicioPersistencia;
import umu.tds.app.AppChat.ContactoIndividual;
import umu.tds.app.AppChat.Mensaje;
import umu.tds.app.AppChat.Usuario;

public class AdaptadorContactoIndividualTDS implements ContactoIndividualDAO {
    private static ServicioPersistencia sp;
    private static AdaptadorContactoIndividualTDS unicaInstancia = null;
    private static final String ENTIDAD_CONTACTO = "contacto";
    private static final Logger LOGGER = Logger.getLogger(AdaptadorContactoIndividualTDS.class.getName());

    private AdaptadorContactoIndividualTDS() {
        sp = FactoriaServicioPersistencia.getInstance().getServicioPersistencia();
    }

    public static AdaptadorContactoIndividualTDS getInstancia() {
        if (unicaInstancia == null)
            unicaInstancia = new AdaptadorContactoIndividualTDS();
        return unicaInstancia;
    }

    @Override
    public void registrarContacto(ContactoIndividual contacto) {
        if (contacto == null) {
            LOGGER.warning("Intento de registrar un contacto nulo.");
            return;
        }

        if (contacto.getCodigo() > 0) {
            LOGGER.info("Contacto ya persistido (ID=" + contacto.getCodigo() + "), no se registra: " + contacto.getNombre());
            return;
        }

        LOGGER.info("Registrando contacto: " + contacto.getNombre() + ", teléfono: " + contacto.getTelefono());

        String usuarioId = contacto.getUsuario() != null ? String.valueOf(contacto.getUsuario().getId()) : "";

        Entidad eContacto = new Entidad();
        eContacto.setNombre(ENTIDAD_CONTACTO);
        eContacto.setPropiedades(List.of(
            new Propiedad("nombre", contacto.getNombre()),
            new Propiedad("movil", String.valueOf(contacto.getTelefono())),
            new Propiedad("usuario", usuarioId),
            new Propiedad("mensajes", codigosMensajes(contacto.getMensajesEnviados()))
        ));

        eContacto = sp.registrarEntidad(eContacto);
        contacto.setCodigo(eContacto.getId());

        LOGGER.info("Contacto registrado con ID persistente: " + contacto.getCodigo());
    }

    @Override
    public ContactoIndividual recuperarContacto(int codigo) {
        LOGGER.info("Recuperando contacto con ID: " + codigo);

        Entidad e = sp.recuperarEntidad(codigo);
        if (e == null) {
            LOGGER.warning("Entidad de contacto no encontrada para ID: " + codigo);
            return null;
        }

        String nombre = sp.recuperarPropiedadEntidad(e, "nombre");
        String movil = sp.recuperarPropiedadEntidad(e, "movil");
        String usuarioProp = sp.recuperarPropiedadEntidad(e, "usuario");
        String codigosMensajes = sp.recuperarPropiedadEntidad(e, "mensajes");

        Usuario usuario = null;
        if (usuarioProp != null && !usuarioProp.isBlank()) {
            try {
                int usuarioId = Integer.parseInt(usuarioProp);
                usuario = AdaptadorUsuarioTDS.getInstancia().recuperarUsuario(usuarioId);
                if (usuario == null) {
                    LOGGER.warning("Usuario no encontrado al recuperar contacto " + nombre + ", ID usuario: " + usuarioId);
                }
            } catch (NumberFormatException ex) {
                LOGGER.warning("ID de usuario mal formado al recuperar contacto: " + usuarioProp);
            }
        }

        ContactoIndividual contacto = new ContactoIndividual(nombre, codigo, movil, usuario);
        obtenerMensajes(codigosMensajes).forEach(contacto::sendMensaje);

        LOGGER.info("Contacto recuperado: " + nombre + " (teléfono: " + movil + ")");
        return contacto;
    }

    private String codigosMensajes(List<Mensaje> mensajes) {
        return mensajes.stream()
                .map(m -> String.valueOf(m.getCodigo()))
                .collect(Collectors.joining(" "));
    }

    private List<Mensaje> obtenerMensajes(String codigos) {
        List<Mensaje> mensajes = new ArrayList<>();
        StringTokenizer st = new StringTokenizer(codigos);
        AdaptadorMensajeTDS adaptador = AdaptadorMensajeTDS.getInstancia();

        while (st.hasMoreTokens()) {
            try {
                int id = Integer.parseInt(st.nextToken());
                Mensaje m = adaptador.recuperarMensaje(id);
                if (m != null) mensajes.add(m);
            } catch (Exception ex) {
                LOGGER.warning("Error al recuperar mensaje desde ID: " + ex.getMessage());
            }
        }
        return mensajes;
    }

    @Override
    public List<ContactoIndividual> recuperarTodosContactos() {
        LOGGER.info("Recuperando todos los contactos individuales...");
        return sp.recuperarEntidades(ENTIDAD_CONTACTO).stream()
                .map(e -> recuperarContacto(e.getId()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public void borrarContacto(ContactoIndividual contacto) {
        LOGGER.info("Borrando contacto con ID: " + contacto.getCodigo());
        Entidad e = sp.recuperarEntidad(contacto.getCodigo());
        sp.borrarEntidad(e);
    }

    @Override
    public void modificarContacto(ContactoIndividual contacto) {
        LOGGER.info("Modificando contacto con ID: " + contacto.getCodigo());
        Entidad e = sp.recuperarEntidad(contacto.getCodigo());

        sp.eliminarPropiedadEntidad(e, "nombre");
        sp.anadirPropiedadEntidad(e, "nombre", contacto.getNombre());

        sp.eliminarPropiedadEntidad(e, "movil");
        sp.anadirPropiedadEntidad(e, "movil", String.valueOf(contacto.getTelefono()));

        String usuarioId = contacto.getUsuario() != null ? String.valueOf(contacto.getUsuario().getId()) : "";
        sp.eliminarPropiedadEntidad(e, "usuario");
        sp.anadirPropiedadEntidad(e, "usuario", usuarioId);

        sp.eliminarPropiedadEntidad(e, "mensajes");
        sp.anadirPropiedadEntidad(e, "mensajes", codigosMensajes(contacto.getMensajesEnviados()));
    }
}
