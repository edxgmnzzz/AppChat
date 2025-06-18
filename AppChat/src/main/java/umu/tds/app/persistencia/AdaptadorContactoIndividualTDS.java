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
    public ContactoIndividual registrarContacto(ContactoIndividual contacto) {
        if (contacto == null) {
            LOGGER.warning("Intento de registrar un contacto nulo.");
            return null;
        }

        if (contacto.getCodigo() > 0 && sp.recuperarEntidad(contacto.getCodigo()) != null) {
            LOGGER.info("Contacto ya persistido (ID=" + contacto.getCodigo() + "), no se registra: " + contacto.getNombre());
            return contacto;
        }

        LOGGER.info("Registrando contacto: " + contacto.getNombre() + ", teléfono: " + contacto.getTelefono());

        String usuarioId = contacto.getUsuario() != null ? String.valueOf(contacto.getUsuario().getId()) : "";

        Entidad eContacto = new Entidad();
        eContacto.setNombre(ENTIDAD_CONTACTO);
        eContacto.setPropiedades(List.of(
            new Propiedad("nombre", contacto.getNombre()),
            new Propiedad("movil", String.valueOf(contacto.getTelefono())),
            new Propiedad("usuario", usuarioId),
            new Propiedad("mensajes", codigosMensajes(contacto.getMensajes()))
        ));

        eContacto = sp.registrarEntidad(eContacto);
        contacto.setCodigo(eContacto.getId());
        
        PoolDAO.getInstancia().addObjeto(contacto.getCodigo(), contacto);
        LOGGER.info("Contacto registrado con ID persistente: " + contacto.getCodigo());
        return contacto;
    }

    @Override
    public ContactoIndividual recuperarContacto(int codigo) {
        if (PoolDAO.getInstancia().contiene(codigo)) {
            return (ContactoIndividual) PoolDAO.getInstancia().getObjeto(codigo);
        }

        Entidad e = sp.recuperarEntidad(codigo);
        if (e == null) return null;

        String nombre = sp.recuperarPropiedadEntidad(e, "nombre");
        String movil = sp.recuperarPropiedadEntidad(e, "movil");

        Usuario usuario = null;
        String usuarioProp = sp.recuperarPropiedadEntidad(e, "usuario");
        if (usuarioProp != null && !usuarioProp.isBlank()) {
            try {
                int usuarioId = Integer.parseInt(usuarioProp);
                usuario = AdaptadorUsuarioTDS.getInstancia().recuperarUsuario(usuarioId);
            } catch (NumberFormatException ex) {
                LOGGER.warning("Error al recuperar ID de usuario del contacto: " + ex.getMessage());
            }
        }

        ContactoIndividual contacto = new ContactoIndividual(nombre, movil, usuario);
        contacto.setCodigo(codigo);
        PoolDAO.getInstancia().addObjeto(codigo, contacto);

        String codigosMensajes = sp.recuperarPropiedadEntidad(e, "mensajes");
        if (codigosMensajes != null && !codigosMensajes.isBlank()) {
            List<Mensaje> mensajes = obtenerMensajes(codigosMensajes);
            mensajes.forEach(contacto::sendMensaje);
        }

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
        LOGGER.info("📂 Recuperando todas las entidades de tipo ContactoIndividual...");
        
        List<Entidad> entidades = sp.recuperarEntidades(ENTIDAD_CONTACTO);
        LOGGER.info("🔍 Total entidades encontradas: " + entidades.size());

        List<ContactoIndividual> contactos = new ArrayList<>();

        for (Entidad e : entidades) {
            ContactoIndividual c = recuperarContacto(e.getId());
            if (c != null) {
                contactos.add(c);
                LOGGER.info("✅ Contacto recuperado correctamente: " + c.getNombre() + " [ID=" + c.getCodigo() + "]");
            } else {
                LOGGER.warning("❌ Contacto nulo al intentar recuperar ID: " + e.getId());
            }
        }

        LOGGER.info("📦 Total contactos individuales recuperados: " + contactos.size());
        return contactos;
    }

    @Override
    public void borrarContacto(ContactoIndividual contacto) {
        Entidad e = sp.recuperarEntidad(contacto.getCodigo());
        sp.borrarEntidad(e);
        if (PoolDAO.getInstancia().contiene(contacto.getCodigo()))
            PoolDAO.getInstancia().removeObjeto(contacto.getCodigo());
    }

    @Override
    public void modificarContacto(ContactoIndividual contacto) {
        Entidad e = sp.recuperarEntidad(contacto.getCodigo());

        sp.eliminarPropiedadEntidad(e, "nombre");
        sp.anadirPropiedadEntidad(e, "nombre", contacto.getNombre());

        sp.eliminarPropiedadEntidad(e, "movil");
        sp.anadirPropiedadEntidad(e, "movil", String.valueOf(contacto.getTelefono()));

        String usuarioId = contacto.getUsuario() != null ? String.valueOf(contacto.getUsuario().getId()) : "";
        sp.eliminarPropiedadEntidad(e, "usuario");
        sp.anadirPropiedadEntidad(e, "usuario", usuarioId);

        sp.eliminarPropiedadEntidad(e, "mensajes");
        sp.anadirPropiedadEntidad(e, "mensajes", codigosMensajes(contacto.getMensajes()));
    }
}
