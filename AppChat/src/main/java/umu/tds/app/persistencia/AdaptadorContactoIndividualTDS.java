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

public class AdaptadorContactoIndividualTDS implements ContactoIndividualDAO {
    private static ServicioPersistencia sp;
    private static AdaptadorContactoIndividualTDS unicaInstancia = null;
    private static final String ENTIDAD_CONTACTO = "contactoIndividual";
    private static final Logger LOGGER = Logger.getLogger(AdaptadorContactoIndividualTDS.class.getName());

    private AdaptadorContactoIndividualTDS() {
        sp = FactoriaServicioPersistencia.getInstance().getServicioPersistencia();
    }

    public static AdaptadorContactoIndividualTDS getInstancia() {
        if (unicaInstancia == null) {
            unicaInstancia = new AdaptadorContactoIndividualTDS();
        }
        return unicaInstancia;
    }

    @Override
    public ContactoIndividual registrarContacto(ContactoIndividual contacto) {
        if (contacto == null) {
            LOGGER.warning("Intento de registrar un contacto nulo.");
            return null;
        }

        if (contacto.getCodigo() > 0 && sp.recuperarEntidad(contacto.getCodigo()) != null) {
            // Ya existe, no hacer nada.
            return contacto;
        }

        Entidad eContacto = new Entidad();
        eContacto.setNombre(ENTIDAD_CONTACTO);
        
        // Se crea una lista mutable para poder añadir propiedades.
        List<Propiedad> propiedades = new ArrayList<>();
        propiedades.add(new Propiedad("nombre", contacto.getNombre()));
        propiedades.add(new Propiedad("telefono", contacto.getTelefono()));
        // <-- AÑADIDO: Guardar el estado 'esDesconocido'
        propiedades.add(new Propiedad("esDesconocido", String.valueOf(contacto.isDesconocido())));
        propiedades.add(new Propiedad("mensajes", codigosDeMensajes(contacto.getMensajes())));
        
        eContacto.setPropiedades(propiedades);

        eContacto = sp.registrarEntidad(eContacto);
        contacto.setCodigo(eContacto.getId());
        
        PoolDAO.getInstancia().addObjeto(contacto.getCodigo(), contacto);
        LOGGER.info("Contacto '" + contacto.getNombre() + "' registrado con ID: " + contacto.getCodigo());
        return contacto;
    }

    @Override
    public ContactoIndividual recuperarContacto(int codigo) {
        if (PoolDAO.getInstancia().contiene(codigo)) {
            ContactoIndividual cached = (ContactoIndividual) PoolDAO.getInstancia().getObjeto(codigo);
            LOGGER.info("🔁 Contacto recuperado del PoolDAO: " + cached.getNombre() + " [ID=" + codigo + "], esDesconocido=" + cached.isDesconocido());
            return cached;
        }

        Entidad e = sp.recuperarEntidad(codigo);
        if (e == null) {
            LOGGER.warning("⚠️ No se encontró la entidad con ID=" + codigo);
            return null;
        }

        String nombre = sp.recuperarPropiedadEntidad(e, "nombre");
        String telefono = sp.recuperarPropiedadEntidad(e, "telefono");
        String esDesconocidoStr = sp.recuperarPropiedadEntidad(e, "esDesconocido");
        boolean esDesconocido = Boolean.parseBoolean(esDesconocidoStr);

        LOGGER.info("📥 Propiedades recuperadas: nombre=" + nombre + ", telefono=" + telefono + ", esDesconocido=" + esDesconocidoStr);

        ContactoIndividual contacto;
        if (esDesconocido) {
            contacto = new ContactoIndividual(telefono);
            contacto.setNombre(nombre); // puede haber sido modificado
        } else {
            contacto = new ContactoIndividual(nombre, telefono);
        }

        contacto.setCodigo(codigo);
        PoolDAO.getInstancia().addObjeto(codigo, contacto);

        LOGGER.info("✅ ContactoIndividual reconstruido: " + contacto.getNombre() + " [ID=" + codigo + "], esDesconocido=" + contacto.isDesconocido());

        String codigosMensajes = sp.recuperarPropiedadEntidad(e, "mensajes");
        if (codigosMensajes != null && !codigosMensajes.isBlank()) {
            List<Mensaje> mensajes = obtenerMensajesDesdeCodigos(codigosMensajes);
            mensajes.forEach(contacto::sendMensaje);
            LOGGER.info("✉️ Se han vinculado " + mensajes.size() + " mensajes al contacto " + contacto.getNombre());
        }

        return contacto;
    }


   /* @Override
    public void modificarContacto(ContactoIndividual contacto) {
        Entidad e = sp.recuperarEntidad(contacto.getCodigo());
        if (e == null) {
            LOGGER.warning("Intento de modificar un contacto no persistido con ID: " + contacto.getCodigo());
            return;
        }

        sp.eliminarPropiedadEntidad(e, "nombre");
        sp.anadirPropiedadEntidad(e, "nombre", contacto.getNombre());

        sp.eliminarPropiedadEntidad(e, "telefono");
        sp.anadirPropiedadEntidad(e, "telefono", contacto.getTelefono());

        // <-- AÑADIDO: Modificar el estado 'esDesconocido'
        sp.eliminarPropiedadEntidad(e, "esDesconocido");
        System.out.println("Aquí, contacto.isDesconocido es" + String.valueOf(contacto.isDesconocido()));
        sp.anadirPropiedadEntidad(e, "esDesconocido", String.valueOf(contacto.isDesconocido()));

        sp.eliminarPropiedadEntidad(e, "mensajes");
        sp.anadirPropiedadEntidad(e, "mensajes", codigosDeMensajes(contacto.getMensajes()));
    }*/
    
 // EN: umu.tds.app.persistencia.AdaptadorContactoIndividualTDS.java

    @Override
    public void modificarContacto(ContactoIndividual contacto) {
        // 1. Recuperar la entidad antigua para poder borrarla.
        Entidad eAntigua = sp.recuperarEntidad(contacto.getCodigo());
        
        if (eAntigua == null) {
            LOGGER.warning("Intento de modificar un contacto no persistido con ID: " + contacto.getCodigo() + ". Se registrará como nuevo.");
            // Si no existe, simplemente lo registramos.
            registrarContacto(contacto);
            return;
        }

        // -- LA LÓGICA CLAVE: BORRAR Y CREAR DE NUEVO --
        // Este enfoque es más robusto que modificar propiedades una a una.
        
        // 2. Borrar la entidad antigua de la persistencia.
        sp.borrarEntidad(eAntigua);
        
        // 3. Crear una nueva entidad con los datos actualizados del objeto 'contacto'.
        Entidad eNueva = new Entidad();
        eNueva.setNombre(ENTIDAD_CONTACTO);
        
        // Se crea una lista mutable para poder añadir propiedades.
        List<Propiedad> propiedades = new ArrayList<>();
        propiedades.add(new Propiedad("nombre", contacto.getNombre()));
        propiedades.add(new Propiedad("telefono", contacto.getTelefono()));
        propiedades.add(new Propiedad("esDesconocido", String.valueOf(contacto.isDesconocido())));
        propiedades.add(new Propiedad("mensajes", codigosDeMensajes(contacto.getMensajes())));
        
        eNueva.setPropiedades(propiedades);

        // 4. Registrar la nueva entidad. Esto nos dará un NUEVO ID.
        eNueva = sp.registrarEntidad(eNueva);
        
        // 5. Opcional pero MUY RECOMENDADO: Actualizar el código del objeto en memoria.
        // Aunque el ID pueda cambiar, es bueno mantener la consistencia.
        // Primero, removemos la entrada antigua del PoolDAO si existe.
        if (PoolDAO.getInstancia().contiene(contacto.getCodigo())) {
            PoolDAO.getInstancia().removeObjeto(contacto.getCodigo());
        }

        // Actualizamos el código en el objeto Java
        contacto.setCodigo(eNueva.getId());

        // Añadimos el objeto actualizado al pool con su nuevo ID.
        PoolDAO.getInstancia().addObjeto(contacto.getCodigo(), contacto);

        LOGGER.info("Contacto '" + contacto.getNombre() + "' modificado (re-registrado) con nuevo ID: " + contacto.getCodigo());
    }
    
    // --- MÉTODOS SIN CAMBIOS ---
    
    @Override
    public List<ContactoIndividual> recuperarTodosContactos() {
        List<Entidad> entidades = sp.recuperarEntidades(ENTIDAD_CONTACTO);
        List<ContactoIndividual> contactos = new ArrayList<>();

        for (Entidad e : entidades) {
            ContactoIndividual c = recuperarContacto(e.getId());
            if (c != null) {
                contactos.add(c);
            } else {
                LOGGER.warning("Error: No se pudo recuperar el contacto con ID: " + e.getId());
            }
        }
        return contactos;
    }
    
    @Override
    public void borrarContacto(ContactoIndividual contacto) {
        Entidad e = sp.recuperarEntidad(contacto.getCodigo());
        if (e != null) {
            sp.borrarEntidad(e);
        }
        if (PoolDAO.getInstancia().contiene(contacto.getCodigo())) {
            PoolDAO.getInstancia().removeObjeto(contacto.getCodigo());
        }
    }

    private String codigosDeMensajes(List<Mensaje> mensajes) {
        if (mensajes == null || mensajes.isEmpty()) return "";
        return mensajes.stream()
                .map(m -> String.valueOf(m.getCodigo()))
                .collect(Collectors.joining(" "));
    }

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