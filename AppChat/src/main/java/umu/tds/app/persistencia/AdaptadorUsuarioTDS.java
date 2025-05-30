package umu.tds.app.persistencia;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon; // Importación correcta de ImageIcon
import beans.Entidad;
import beans.Propiedad;
import tds.driver.FactoriaServicioPersistencia;
import tds.driver.ServicioPersistencia;
import umu.tds.app.AppChat.Usuario; // Asegúrate que la ruta a Usuario es correcta

public class AdaptadorUsuarioTDS implements UsuarioDAO {

    private static ServicioPersistencia sp;
    private static AdaptadorUsuarioTDS unicaInstancia = null;
    private static final String ENTIDAD_USUARIO = "usuario";
    private static final Logger LOGGER = Logger.getLogger(AdaptadorUsuarioTDS.class.getName());

    private AdaptadorUsuarioTDS() {
        sp = FactoriaServicioPersistencia.getInstance().getServicioPersistencia();
    }

    public static AdaptadorUsuarioTDS getInstancia() {
        if (unicaInstancia == null) {
            unicaInstancia = new AdaptadorUsuarioTDS();
        }
        return unicaInstancia;
    }

    /**
     * Modifica una propiedad existente en la lista de propiedades de la entidad.
     * Si la propiedad no existe, intenta añadirla.
     * ASUNCIÓN: beans.Propiedad tiene setValor() y e.getPropiedades() devuelve una lista
     * que JPA/EclipseLink puede rastrear para detectar cambios.
     *
     * @param entidad La entidad a modificar.
     * @param propertyName El nombre de la propiedad.
     * @param newValue El nuevo valor para la propiedad.
     * @return true si la propiedad fue actualizada o añadida exitosamente, false en caso contrario.
     */
    private boolean setEntityProperty(Entidad entidad, String propertyName, String newValue) {
        if (entidad == null || entidad.getPropiedades() == null) {
            LOGGER.warning("[setEntityProperty] Entidad o su lista de propiedades es null. No se puede actualizar '" + propertyName + "'.");
            return false;
        }

        // Intentar actualizar una propiedad existente
        for (Propiedad p : entidad.getPropiedades()) {
            if (p != null && p.getNombre().equals(propertyName)) {
                String oldValue = p.getValor();
                try {
                    p.setValor(newValue); // Asume que Propiedad tiene setValor y es mutable
                    if (Objects.equals(p.getValor(), newValue)) {
                        LOGGER.fine("[setEntityProperty] Propiedad '" + propertyName + "' (ID Entidad: " + entidad.getId() + ") actualizada. Viejo: '" + oldValue + "', Nuevo: '" + p.getValor() + "'.");
                        return true;
                    } else {
                        // Si setValor no funcionó como se esperaba, registrar y considerar fallido
                        LOGGER.warning("[setEntityProperty] p.setValor() para '" + propertyName + "' (ID Entidad: " + entidad.getId() + ") no cambió el valor como se esperaba. Actual: '" + p.getValor() + "', Esperado: '" + newValue + "'.");
                        return false;
                    }
                } catch (Exception ex) {
                    LOGGER.log(Level.SEVERE, "[setEntityProperty] Error al llamar a p.setValor() para '" + propertyName + "' (ID Entidad: " + entidad.getId() + ")", ex);
                    return false;
                }
            }
        }

        // Si la propiedad no se encontró para actualizar, intentar añadirla
        // Esto es importante si la entidad es nueva o si la propiedad se eliminó previamente.
        LOGGER.info("[setEntityProperty] Propiedad '" + propertyName + "' no encontrada en Entidad ID " + entidad.getId() + ". Intentando añadirla.");
        try {
            // Usar el método del driver para añadir, ya que la modificación directa de la lista
            // (entidad.getPropiedades().add(...)) podría no ser rastreada por el driver/JPA de la misma manera.
            // Si el problema original era con anadirPropiedadEntidad, esto podría fallar.
            // Sin embargo, el problema principal parecía ser la *combinación* de eliminar/anadir.
            // Una adición simple podría funcionar si la propiedad realmente no existe.
            sp.anadirPropiedadEntidad(entidad, propertyName, newValue);
            // Verificar si se añadió correctamente
            for (Propiedad p : entidad.getPropiedades()) {
                if (p != null && p.getNombre().equals(propertyName) && Objects.equals(p.getValor(), newValue)) {
                    LOGGER.info("[setEntityProperty] Propiedad '" + propertyName + "' añadida exitosamente a Entidad ID " + entidad.getId() + " con valor '" + newValue + "'.");
                    return true;
                }
            }
            LOGGER.warning("[setEntityProperty] sp.anadirPropiedadEntidad no resultó en la propiedad '" + propertyName + "' presente con el valor correcto en Entidad ID " + entidad.getId());
            return false;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "[setEntityProperty] Error durante sp.anadirPropiedadEntidad para '" + propertyName + "' con valor '" + newValue + "'", e);
            return false;
        }
    }
    
    @Override
    public void modificarUsuario(Usuario usuario) {
        if (usuario == null || usuario.getId() <= 0) {
            LOGGER.warning("[modificarUsuario] Intento de modificar usuario nulo o sin ID válido.");
            return;
        }
        LOGGER.info("[modificarUsuario] INICIO para Usuario ID: " + usuario.getId() + ", Tel: " + usuario.getTelefono() + ", Premium deseado: " + usuario.isPremium());

        Entidad e = null;
        try {
            e = sp.recuperarEntidad(usuario.getId());
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "[modificarUsuario] Error al recuperar entidad ID: " + usuario.getId(), ex);
            return;
        }
        
        if (e == null) {
            LOGGER.severe("[modificarUsuario] No se pudo recuperar la entidad ID: " + usuario.getId() + " para modificar.");
            return;
        }

        // Actualizar propiedades
        setEntityProperty(e, "nombre", usuario.getName());
        setEntityProperty(e, "telefono", usuario.getTelefono());
        setEntityProperty(e, "password", usuario.getPassword());
        setEntityProperty(e, "email", usuario.getEmail() != null ? usuario.getEmail() : "");
        setEntityProperty(e, "saludo", usuario.getSaludo() != null ? usuario.getSaludo() : "");
        setEntityProperty(e, "premium", String.valueOf(usuario.isPremium())); // Punto crítico
        setEntityProperty(e, "foto", usuario.getFoto() != null && usuario.getFoto().getDescription() != null ? usuario.getFoto().getDescription() : "");
        setEntityProperty(e, "urlFoto", usuario.getUrlFoto() != null ? usuario.getUrlFoto() : "");
        
        LOGGER.fine("[modificarUsuario] Estado de la Entidad ID " + e.getId() + " en memoria ANTES de sp.modificarEntidad:");
        // logEntidadProperties(e, "[modificarUsuario] Detalle props antes de modificarEntidad"); // Descomentar para depuración profunda

        try {
            sp.modificarEntidad(e);
            LOGGER.info("[modificarUsuario] sp.modificarEntidad llamado para Usuario ID: " + usuario.getId());
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "[modificarUsuario] Error durante sp.modificarEntidad para ID: " + e.getId(), ex);
        }

        // Verificación Inmediata simplificada
        Entidad eVerificacion = null;
        try {
            eVerificacion = sp.recuperarEntidad(usuario.getId());
        } catch (Exception ex) {
            LOGGER.log(Level.WARNING, "[modificarUsuario-Verif] Error al re-recuperar entidad ID: " + usuario.getId(), ex);
        }
        if (eVerificacion != null) {
            String premiumVerificadoStr = sp.recuperarPropiedadEntidad(eVerificacion, "premium");
            LOGGER.info("[modificarUsuario-Verif] Usuario ID: " + usuario.getId() + ". 'premium' en BD/Pool después de modificar: '" + premiumVerificadoStr + "'. Esperado: '" + String.valueOf(usuario.isPremium()) + "'");
            if (!Objects.equals(premiumVerificadoStr, String.valueOf(usuario.isPremium()))) {
                LOGGER.severe("[modificarUsuario-Verif] ¡DISCREPANCIA en 'premium' para Usuario ID: " + usuario.getId() + "!");
            }
        } else {
             LOGGER.warning("[modificarUsuario-Verif] No se pudo re-recuperar la entidad ID: " + usuario.getId() + " para verificación.");
        }
        LOGGER.info("[modificarUsuario] FIN para Usuario ID: " + usuario.getId());
    }
    
    @Override
    public void registrarUsuario(Usuario usuario) {
        if (usuario == null) {
            LOGGER.warning("[registrarUsuario] Intento de registrar usuario nulo.");
            return;
        }
        // Asumiendo que PoolDAO no se usa aquí para simplificar, o que el ID 0 indica nuevo
        if (usuario.getId() > 0) {
             LOGGER.info("[registrarUsuario] Usuario ya tiene ID (" + usuario.getId() + "). Asumiendo actualización y llamando a modificarUsuario.");
             modificarUsuario(usuario); // Si ya tiene ID, es una modificación
             return;
        }
      
        if (usuario.getTelefono() == null || usuario.getTelefono().isBlank()) {
            LOGGER.severe("[registrarUsuario] Usuario sin teléfono. Registro cancelado.");
            return;
        }

        LOGGER.info("[registrarUsuario] Registrando nuevo usuario: " + usuario.getName() + ", Tel: " + usuario.getTelefono() + ", Premium: " + usuario.isPremium());

        Entidad eUsuario = new Entidad();
        eUsuario.setNombre(ENTIDAD_USUARIO);
        // Crear lista de propiedades
        List<Propiedad> propiedades = new LinkedList<>();
        propiedades.add(new Propiedad("nombre", usuario.getName()));
        propiedades.add(new Propiedad("telefono", usuario.getTelefono()));
        propiedades.add(new Propiedad("password", usuario.getPassword()));
        propiedades.add(new Propiedad("email", usuario.getEmail() != null ? usuario.getEmail() : ""));
        propiedades.add(new Propiedad("saludo", usuario.getSaludo() != null ? usuario.getSaludo() : ""));
        propiedades.add(new Propiedad("premium", String.valueOf(usuario.isPremium())));
        propiedades.add(new Propiedad("foto", usuario.getFoto() != null && usuario.getFoto().getDescription() != null ? usuario.getFoto().getDescription() : ""));
        propiedades.add(new Propiedad("urlFoto", usuario.getUrlFoto() != null ? usuario.getUrlFoto() : ""));
        
        // ASUNCIÓN: Entidad tiene un método setPropiedades que el driver TDS puede usar,
        // o el driver construye la entidad con estas propiedades al registrar.
        eUsuario.setPropiedades(propiedades);

        try {
            eUsuario = sp.registrarEntidad(eUsuario);
            if (eUsuario != null) {
                usuario.setId(eUsuario.getId());
                LOGGER.info("[registrarUsuario] Usuario registrado con ID: " + usuario.getId());
            } else {
                LOGGER.severe("[registrarUsuario] sp.registrarEntidad devolvió null para: " + usuario.getName());
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "[registrarUsuario] Error durante sp.registrarEntidad para: " + usuario.getName(), e);
        }
    }

    @Override
    public void borrarUsuario(Usuario usuario) {
        if (usuario == null || usuario.getId() <= 0) {
            LOGGER.warning("[borrarUsuario] Intento de borrar usuario nulo o sin ID válido.");
            return;
        }
        LOGGER.info("[borrarUsuario] Solicitando borrar Usuario ID: " + usuario.getId());
        Entidad e = null;
        try {
            e = sp.recuperarEntidad(usuario.getId());
            if (e != null) {
                sp.borrarEntidad(e);
                LOGGER.info("[borrarUsuario] Usuario ID: " + usuario.getId() + " borrado (o intento realizado).");
            } else {
                LOGGER.warning("[borrarUsuario] No se encontró la Entidad ID: " + usuario.getId() + " para borrar.");
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "[borrarUsuario] Error al borrar Usuario ID: " + usuario.getId(), ex);
        }
    }
    
    @Override
    public Usuario recuperarUsuario(int codigo) {
        LOGGER.fine("[recuperarUsuario] Solicitando Usuario ID: " + codigo);
        Entidad e = null;
        try {
            e = sp.recuperarEntidad(codigo);
        } catch (Exception ex) {
            LOGGER.log(Level.WARNING, "[recuperarUsuario] Error al recuperar entidad ID: " + codigo, ex);
            return null;
        }

        if (e == null) {
            LOGGER.warning("[recuperarUsuario] No se encontró Entidad para ID: " + codigo);
            return null;
        }

        String nombre = sp.recuperarPropiedadEntidad(e, "nombre");
        String telefono = sp.recuperarPropiedadEntidad(e, "telefono");
        String password = sp.recuperarPropiedadEntidad(e, "password");
        String email = sp.recuperarPropiedadEntidad(e, "email");
        String saludo = sp.recuperarPropiedadEntidad(e, "saludo");
        String premiumStr = sp.recuperarPropiedadEntidad(e, "premium");
        String urlFoto = sp.recuperarPropiedadEntidad(e, "urlFoto");
        
        boolean premium = false; 
        if (premiumStr != null) {
            premium = premiumStr.equalsIgnoreCase("true");
        } else {
            // No es necesariamente un warning si la propiedad puede no existir y el default es false.
            LOGGER.fine("[recuperarUsuario] Propiedad 'premium' no encontrada o null para ID: " + codigo + ". Asumiendo false.");
        }
        
        if (telefono == null || telefono.isBlank()) {
            LOGGER.severe("[recuperarUsuario] Teléfono es null o vacío para Usuario ID: " + codigo + ". No se puede crear el objeto.");
            return null; 
        }

        ImageIcon icono = new ImageIcon(); // El controlador se encarga de cargar la imagen real
        Usuario u = new Usuario(telefono, nombre, password, email, saludo, icono, premium);
        u.setId(codigo); 
        u.setUrlFoto(urlFoto);

        LOGGER.info("[recuperarUsuario] Usuario ID: " + codigo + " recuperado. Tel: " + u.getTelefono() + ", Premium: " + u.isPremium());
        return u;
    }

    @Override
    public List<Usuario> recuperarTodosUsuarios() {
        LOGGER.info("[recuperarTodosUsuarios] Solicitando todos los usuarios...");
        List<Entidad> entidades = null;
        try {
            entidades = sp.recuperarEntidades(ENTIDAD_USUARIO);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "[recuperarTodosUsuarios] Error al recuperar entidades '" + ENTIDAD_USUARIO + "'", e);
            return new ArrayList<>(); 
        }

        List<Usuario> usuarios = new ArrayList<>();
        if (entidades != null) {
            for (Entidad eIt : entidades) {
                if (eIt == null) continue; // Saltar entidades null si el driver las devuelve
                Usuario u = recuperarUsuario(eIt.getId()); 
                if (u != null) {
                    usuarios.add(u);
                }
            }
            LOGGER.info("[recuperarTodosUsuarios] Recuperados " + usuarios.size() + " usuarios.");
        } else {
            LOGGER.warning("[recuperarTodosUsuarios] sp.recuperarEntidades devolvió null.");
        }
        return usuarios;
    }

    // Eliminar el método updateProperty original ya que ahora usamos setEntityProperty
    // private void updateProperty(Entidad entidad, String nombrePropiedad, String nuevoValor) { ... }

    // El método logEntidadProperties se puede mantener para depuración si se descomenta su llamada
    // o se puede eliminar si no se usa. Por ahora lo dejo por si lo necesitas.
    private void logEntidadProperties(Entidad entidad, String contextMessage) {
        // ... (implementación de logEntidadProperties, se puede comentar/eliminar si no se usa activamente) ...
        // Para simplificar la salida, podemos comentar su contenido o las llamadas a él.
        // Por ejemplo:
        // LOGGER.fine(contextMessage + ": Propiedades de Entidad ID " + (entidad != null ? entidad.getId() : "null"));
    }
}