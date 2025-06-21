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

/**
 * Adaptador DAO para la entidad {@link ContactoIndividual} usando el servicio de
 * persistencia TDS. Se encarga de registrar, recuperar, modificar y borrar
 * contactos individuales en la base de datos y de mantener un pequeño
 * cache en memoria mediante PoolDAO.

 */
public class AdaptadorContactoIndividualTDS implements ContactoIndividualDAO {

    private static ServicioPersistencia sp;
    private static AdaptadorContactoIndividualTDS unicaInstancia = null;
    private static final String ENTIDAD_CONTACTO = "contactoIndividual";
    private static final Logger LOGGER = Logger.getLogger(AdaptadorContactoIndividualTDS.class.getName());

    /**
     * Constructor privado para cumplir el patrón Singleton.
     */
    private AdaptadorContactoIndividualTDS() {
        sp = FactoriaServicioPersistencia.getInstance().getServicioPersistencia();
    }

    /**
     * Devuelve la única instancia del adaptador (patrón Singleton).
     *
     * @return Instancia única de {@code AdaptadorContactoIndividualTDS}.
     */
    public static AdaptadorContactoIndividualTDS getInstancia() {
        if (unicaInstancia == null) {
            unicaInstancia = new AdaptadorContactoIndividualTDS();
        }
        return unicaInstancia;
    }

    // ─────────────────────────── Registro ───────────────────────────

    /**
     * Registra un {@link ContactoIndividual} en la base de datos. Si el contacto
     * ya está persistido (tiene código &gt; 0 y existe en BD), se devuelve tal
     * cual.
     *
     * @param contacto Contacto a registrar.
     * @return El mismo contacto, ya registrado y con ID asignado; {@code null}
     *         si el contacto era nulo.
     */
    @Override
    public ContactoIndividual registrarContacto(ContactoIndividual contacto) {
        if (contacto == null) {
            LOGGER.warning("Intento de registrar un contacto nulo.");
            return null;
        }

        if (contacto.getCodigo() > 0 && sp.recuperarEntidad(contacto.getCodigo()) != null) {
            // Ya existía en la BD; no se hace nada.
            return contacto;
        }

        Entidad eContacto = new Entidad();
        eContacto.setNombre(ENTIDAD_CONTACTO);

        List<Propiedad> props = new ArrayList<>();
        props.add(new Propiedad("nombre", contacto.getNombre()));
        props.add(new Propiedad("telefono", contacto.getTelefono()));
        props.add(new Propiedad("esDesconocido", String.valueOf(contacto.isDesconocido())));
        props.add(new Propiedad("mensajes", codigosDeMensajes(contacto.getMensajes())));
        eContacto.setPropiedades(props);

        eContacto = sp.registrarEntidad(eContacto);
        contacto.setCodigo(eContacto.getId());
        PoolDAO.getInstancia().addObjeto(contacto.getCodigo(), contacto);
        return contacto;
    }

    // ─────────────────────────── Recuperación ───────────────────────────

    /**
     * Recupera un {@link ContactoIndividual} desde su ID. Utiliza primero el
     * {@link PoolDAO} para evitar accesos redundantes a la base de datos.
     *
     * @param codigo ID del contacto.
     * @return Contacto reconstruido o {@code null} si no existe.
     */
    @Override
    public ContactoIndividual recuperarContacto(int codigo) {
        // 1. Intentar cache
        if (PoolDAO.getInstancia().contiene(codigo)) {
            return (ContactoIndividual) PoolDAO.getInstancia().getObjeto(codigo);
        }

        // 2. Ir a la BD
        Entidad e = sp.recuperarEntidad(codigo);
        if (e == null) {
            LOGGER.warning("No se encontró la entidad contactoIndividual con ID=" + codigo);
            return null;
        }

        String nombre = sp.recuperarPropiedadEntidad(e, "nombre");
        String telefono = sp.recuperarPropiedadEntidad(e, "telefono");
        boolean esDesconocido = Boolean.parseBoolean(sp.recuperarPropiedadEntidad(e, "esDesconocido"));

        ContactoIndividual contacto = esDesconocido ? new ContactoIndividual(telefono) : new ContactoIndividual(nombre, telefono);
        if (esDesconocido) contacto.setNombre(nombre); // pudo ser renombrado
        contacto.setCodigo(codigo);
        PoolDAO.getInstancia().addObjeto(codigo, contacto);

        // Vincular mensajes
        String codigosMsg = sp.recuperarPropiedadEntidad(e, "mensajes");
        if (codigosMsg != null && !codigosMsg.isBlank()) {
            obtenerMensajesDesdeCodigos(codigosMsg).forEach(contacto::sendMensaje);
        }
        return contacto;
    }

    // ─────────────────────────── Modificación ───────────────────────────

    /**
     * Modifica un contacto sobrescribiendo la entidad en BD. Para simplificar y
     * evitar inconsistencias se borra la entidad antigua y se vuelve a crear.
     * Se actualiza también el ID en memoria.
     *
     * @param contacto Contacto a modificar.
     */
    @Override
    public void modificarContacto(ContactoIndividual contacto) {
        Entidad eAntigua = sp.recuperarEntidad(contacto.getCodigo());
        if (eAntigua == null) {
            LOGGER.warning("Intento de modificar contacto no persistido con ID=" + contacto.getCodigo() + ". Se registra como nuevo.");
            registrarContacto(contacto);
            return;
        }

        // 1. Borrar la entidad vieja
        sp.borrarEntidad(eAntigua);

        // 2. Crear nueva entidad con propiedades actualizadas
        Entidad eNueva = new Entidad();
        eNueva.setNombre(ENTIDAD_CONTACTO);
        List<Propiedad> props = new ArrayList<>();
        props.add(new Propiedad("nombre", contacto.getNombre()));
        props.add(new Propiedad("telefono", contacto.getTelefono()));
        props.add(new Propiedad("esDesconocido", String.valueOf(contacto.isDesconocido())));
        props.add(new Propiedad("mensajes", codigosDeMensajes(contacto.getMensajes())));
        eNueva.setPropiedades(props);
        eNueva = sp.registrarEntidad(eNueva);

        // 3. Sincronizar ID en memoria y en cache
        PoolDAO pool = PoolDAO.getInstancia();
        pool.removeObjeto(contacto.getCodigo());
        contacto.setCodigo(eNueva.getId());
        pool.addObjeto(contacto.getCodigo(), contacto);
    }

    // ─────────────────────────── Listado y borrado ───────────────────────────

    /**
     * Recupera todos los contactos individuales persistidos.
     *
     * @return Lista de contactos.
     */
    @Override
    public List<ContactoIndividual> recuperarTodosContactos() {
        List<Entidad> entidades = sp.recuperarEntidades(ENTIDAD_CONTACTO);
        List<ContactoIndividual> lista = new ArrayList<>();
        for (Entidad e : entidades) {
            ContactoIndividual c = recuperarContacto(e.getId());
            if (c != null) lista.add(c);
        }
        return lista;
    }

    /**
     * Elimina un contacto de la persistencia y del {@link PoolDAO}.
     *
     * @param contacto Contacto a borrar.
     */
    @Override
    public void borrarContacto(ContactoIndividual contacto) {
        Entidad e = sp.recuperarEntidad(contacto.getCodigo());
        if (e != null) sp.borrarEntidad(e);
        PoolDAO.getInstancia().removeObjeto(contacto.getCodigo());
    }

    // ─────────────────────────── Utilidades ───────────────────────────

    /**
     * Convierte una lista de mensajes en una cadena de IDs separados por espacio.
     */
    private String codigosDeMensajes(List<Mensaje> mensajes) {
        if (mensajes == null || mensajes.isEmpty()) return "";
        return mensajes.stream().map(m -> String.valueOf(m.getCodigo())).collect(Collectors.joining(" "));
    }

    /**
     * Reconstruye una lista de mensajes a partir de una cadena de IDs.
     */
    private List<Mensaje> obtenerMensajesDesdeCodigos(String codigos) {
        if (codigos == null || codigos.isBlank()) return new ArrayList<>();
        List<Mensaje> mensajes = new ArrayList<>();
        StringTokenizer st = new StringTokenizer(codigos);
        AdaptadorMensajeTDS adaptadorMsg = AdaptadorMensajeTDS.getInstancia();
        while (st.hasMoreTokens()) {
            try {
                int id = Integer.parseInt(st.nextToken());
                Mensaje m = adaptadorMsg.recuperarMensaje(id);
                if (m != null) mensajes.add(m);
            } catch (NumberFormatException ex) {
                LOGGER.warning("Error al parsear ID de mensaje: " + ex.getMessage());
            }
        }
        return mensajes;
    }
}
