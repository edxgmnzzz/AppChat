package umu.tds.app.AppChat;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

/**
 * Clase Controlador que implementa el patrón Singleton y Subject para gestionar usuarios,
 * contactos y mensajes en la aplicación AppChat.
 */
public class Controlador {
    private static final Controlador instancia;
    private static final Logger LOGGER;

    static {
        LOGGER = Logger.getLogger(Controlador.class.getName());
        try {
            instancia = new Controlador();
        } catch (Exception e) {
            throw new ExceptionInInitializerError("Error al inicializar Controlador: " + e.getMessage());
        }
    }

    private Map<String, Usuario> usuariosSimulados;
    private List<Contacto> contactos; // Already updated to List<Contacto>
    private Map<String, List<String>> mensajes;
    private Usuario usuarioActual;
    private Contacto contactoActual;
    private List<ObserverChats> observersChats;
    private List<ObserverContactos> observersContactos;

    private Controlador() {
        initialize();
    }

    private void initialize() {
        usuariosSimulados = new HashMap<>();
        contactos = new ArrayList<>();
        mensajes = new HashMap<>();
        observersChats = new ArrayList<>();
        observersContactos = new ArrayList<>();

        String defaultImageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/3/36/Florentino_perez.jpg/220px-Florentino_perez.jpg";
        ImageIcon profileIcon = loadImageFromUrl(defaultImageUrl);

        usuarioActual = new Usuario(profileIcon, "Florentino Pérez", LocalDate.of(1990, 1, 1), 
                1234567890, "admin", "admin@gmail.com", false, "Soy su florentineza", null, null);
        usuariosSimulados.put("florentino", usuarioActual);

        contactos.add(new ContactoIndividual("Oscar", 123456789, 
                new Usuario(new ImageIcon(), "Oscar", LocalDate.of(1990, 1, 1), 123456789, "12345", 
                        "oscar@gmail.com", true, "Hola", null, null)));
        contactos.add(new ContactoIndividual("Javi", 234567890, 
                new Usuario(new ImageIcon(), "Javi", LocalDate.of(1995, 1, 1), 234567890, "password", 
                        "javi@gmail.com", false, "Hey!", null, null)));
        contactos.add(new ContactoIndividual("Lucia", 345678901, 
                new Usuario(new ImageIcon(), "Lucia", LocalDate.of(2000, 1, 1), 345678901, "12345", 
                        "lucia@gmail.com", true, "Saludos", null, null)));

        mensajes.put("Florentino Pérez-Oscar", new ArrayList<>(List.of("Tú: Hola Oscar")));
        mensajes.put("Florentino Pérez-Javi", new ArrayList<>(List.of("Tú: Hola Javi")));
    }

    private ImageIcon loadImageFromUrl(String urlString) {
        try {
            URI uri = new URI(urlString);
            URL url = uri.toURL();
            BufferedImage image = ImageIO.read(url);
            if (image != null) {
                if (LOGGER != null) LOGGER.info("Imagen cargada exitosamente desde: " + urlString);
                return new ImageIcon(image);
            } else {
                if (LOGGER != null) LOGGER.warning("No se pudo cargar la imagen desde: " + urlString);
            }
        } catch (Exception e) {
            if (LOGGER != null) LOGGER.severe("Error al cargar imagen desde " + urlString + ": " + e.getMessage());
        }
        return new ImageIcon();
    }

    public static Controlador getInstancia() {
        return instancia;
    }

    public void addObserverChats(ObserverChats observer) {
        observersChats.add(observer);
    }

    public void removeObserverChats(ObserverChats observer) {
        observersChats.remove(observer);
    }

    private void notifyObserversChatsRecientes() {
        String[] chatsRecientes = getChatsRecientes();
        for (ObserverChats observer : observersChats) {
            observer.updateChatsRecientes(chatsRecientes);
        }
    }

    private void notifyObserversContactoActual(Contacto contacto) {
        for (ObserverChats observer : observersChats) {
            observer.updateContactoActual(contacto);
        }
    }

    public void addObserverContactos(ObserverContactos observer) {
        observersContactos.add(observer);
    }

    public void removeObserverContactos(ObserverContactos observer) {
        observersContactos.remove(observer);
    }

    private void notifyObserversListaContactos() {
        for (ObserverContactos observer : observersContactos) {
            observer.updateListaContactos();
        }
    }

    public boolean iniciarSesion(String nombreUsuario, String password) {
        if (nombreUsuario == null || password == null) {
            if (LOGGER != null) LOGGER.warning("Intento de inicio de sesión con credenciales nulas.");
            return false;
        }
        Usuario usuario = usuariosSimulados.get(nombreUsuario);
        if (usuario != null && usuario.getPassword().equals(password)) {
            usuarioActual = usuario;
            if (LOGGER != null) LOGGER.info("Usuario " + nombreUsuario + " ha iniciado sesión.");
            notifyObserversChatsRecientes();
            return true;
        }
        if (LOGGER != null) LOGGER.warning("Credenciales incorrectas para " + nombreUsuario);
        return false;
    }

    public Contacto obtenerContactoPorNombre(String nombre) {
        for (Contacto contacto : contactos) {
            if (contacto.getNombre().equalsIgnoreCase(nombre)) {
                return contacto;
            }
        }
        return null;
    }

    public void cerrarSesion() {
        if (LOGGER != null) LOGGER.info("Cerrando sesión para " + (usuarioActual != null ? usuarioActual.getName() : "usuario nulo"));
        usuarioActual = null;
        contactoActual = null;
        notifyObserversChatsRecientes();
    }

    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public Contacto getContactoActual() {
        return contactoActual;
    }

    public void setContactoActual(Contacto contacto) {
        this.contactoActual = contacto;
        notifyObserversContactoActual(contacto);
    }

    public List<Contacto> obtenerContactos() {
        return Collections.unmodifiableList(contactos);
    }

    public void enviarMensaje(Contacto contacto, String mensaje) {
        if (usuarioActual == null || contacto == null || mensaje == null || mensaje.trim().isEmpty()) {
            if (LOGGER != null) LOGGER.warning("Intento de envío de mensaje inválido.");
            return;
        }
        String clave = generarClaveConversacion(contacto);
        mensajes.computeIfAbsent(clave, k -> new ArrayList<>()).add("Tú: " + mensaje);
        if (LOGGER != null) LOGGER.info("Mensaje enviado a " + contacto.getNombre() + ": " + mensaje);
        notifyObserversChatsRecientes();
    }

    public List<String> obtenerMensajes(Contacto contacto) {
        if (contacto == null) {
            return Collections.emptyList();
        }
        String clave = generarClaveConversacion(contacto);
        return Collections.unmodifiableList(mensajes.getOrDefault(clave, new ArrayList<>()));
    }

    private String generarClaveConversacion(Contacto contacto) {
        return usuarioActual.getName() + "-" + contacto.getNombre();
    }

    public String[] getChatsRecientes() {
        List<String> chats = new ArrayList<>();
        for (Contacto contacto : contactos) {
            String clave = generarClaveConversacion(contacto);
            if (mensajes.containsKey(clave) && !mensajes.get(clave).isEmpty()) {
                chats.add("Chat con " + contacto.getNombre());
            }
        }
        Collections.reverse(chats);
        return chats.isEmpty() ? new String[]{"No hay chats recientes"} : chats.toArray(new String[0]);
    }

    public boolean agregarContacto(ContactoIndividual contacto) {
        if (contacto == null) {
            if (LOGGER != null) LOGGER.warning("Intento de agregar contacto nulo.");
            return false;
        }

        // Check for duplicates: for ContactoIndividual, use movil; for others, use nombre
        boolean isDuplicate = contactos.stream().anyMatch(c -> {
            if (c instanceof ContactoIndividual) {
                return ((ContactoIndividual) c).getMovil() == contacto.getMovil();
            } else {
                return c.getNombre().equalsIgnoreCase(contacto.getNombre());
            }
        });

        if (isDuplicate) {
            if (LOGGER != null) LOGGER.warning("Intento de agregar contacto duplicado: " + contacto.getNombre());
            return false;
        }

        contactos.add(contacto);
        if (LOGGER != null) LOGGER.info("Contacto " + contacto.getNombre() + " agregado exitosamente.");
        
        notifyObserversChatsRecientes();
        notifyObserversListaContactos();
        return true;
    }

    public void eliminarContacto(ContactoIndividual contacto) {
        if (contacto != null && contactos.remove(contacto)) {
            String clave = generarClaveConversacion(contacto);
            mensajes.remove(clave);
            if (LOGGER != null) LOGGER.info("Contacto " + contacto.getNombre() + " eliminado.");
            notifyObserversChatsRecientes();
        }
    }

    public boolean nuevoContacto(ContactoIndividual contacto) {
        boolean added = agregarContacto(contacto);
        if (added && usuarioActual != null) {
            List<Contacto> userContactos = usuarioActual.getContactos();
            if (userContactos == null) {
                userContactos = new ArrayList<>();
            }
            userContactos.add(contacto);
            notifyObserversChatsRecientes();
            notifyObserversListaContactos();
        }
        return added;
    }

    public void crearGrupo(String nombre, List<ContactoIndividual> miembros) {
        if (usuarioActual == null) {
            if (LOGGER != null) LOGGER.warning("No hay usuario autenticado para crear un grupo.");
            JOptionPane.showMessageDialog(null, "Debes estar autenticado para crear un grupo.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (nombre == null || nombre.trim().isEmpty() || miembros == null || miembros.isEmpty()) {
            if (LOGGER != null) LOGGER.warning("Intento de crear grupo inválido: nombre o miembros nulos o vacíos.");
            JOptionPane.showMessageDialog(null, "El nombre y los miembros son obligatorios.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (contactos.stream().anyMatch(c -> c.getNombre().equalsIgnoreCase(nombre))) {
            if (LOGGER != null) LOGGER.warning("Ya existe un contacto o grupo con el nombre: " + nombre);
            JOptionPane.showMessageDialog(null, "Ya existe un contacto o grupo con ese nombre.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create a defensive copy of miembros to avoid external modifications
        List<ContactoIndividual> miembrosCopia = new ArrayList<>(miembros);
        Grupo nuevoGrupo = new Grupo(nombre, miembrosCopia, usuarioActual); // Current user as admin
        contactos.add(nuevoGrupo);
        
        // Add the group to the current user's contact list
        List<Contacto> userContactos = usuarioActual.getContactos();
        if (userContactos == null) {
            userContactos = new ArrayList<>();
        }
        userContactos.add(nuevoGrupo);

        if (LOGGER != null) LOGGER.info("Grupo " + nombre + " creado con " + miembros.size() + " miembros por " + usuarioActual.getName());
        
        notifyObserversChatsRecientes();
        notifyObserversListaContactos();
    }

    public boolean registrarUsuario(String nombreReal, String nombreUsuario, String password, String confirmarPassword,
                                    String email, int telefono, LocalDate fechaNacimiento, String rutaFoto) {
        if (!validateRegistration(nombreReal, nombreUsuario, password, confirmarPassword, email, telefono)) {
            return false;
        }
        ImageIcon profileIcon = rutaFoto != null && !rutaFoto.isEmpty() ? loadImageFromUrl(rutaFoto) : new ImageIcon();
        Usuario nuevoUsuario = new Usuario(profileIcon, nombreReal, fechaNacimiento, telefono, password, 
                email, false, "¡Hola, soy nuevo!", null, null);
        usuariosSimulados.put(nombreUsuario, nuevoUsuario);
        if (LOGGER != null) LOGGER.info("Usuario " + nombreUsuario + " registrado exitosamente.");
        JOptionPane.showMessageDialog(null, "Usuario registrado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        notifyObserversChatsRecientes();
        return true;
    }

    private boolean validateRegistration(String nombreReal, String nombreUsuario, String password, 
                                         String confirmarPassword, String email, int telefono) {
        if (nombreReal == null || nombreUsuario == null || password == null || confirmarPassword == null || email == null) {
            JOptionPane.showMessageDialog(null, "Todos los campos son obligatorios.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (!password.equals(confirmarPassword)) {
            JOptionPane.showMessageDialog(null, "Las contraseñas no coinciden.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (usuariosSimulados.containsKey(nombreUsuario)) {
            JOptionPane.showMessageDialog(null, "El nombre de usuario ya existe.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (usuariosSimulados.values().stream().anyMatch(u -> u.getEmail().equals(email))) {
            JOptionPane.showMessageDialog(null, "El correo electrónico ya está registrado.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (usuariosSimulados.values().stream().anyMatch(u -> u.getNumTelefono() == telefono)) {
            JOptionPane.showMessageDialog(null, "El número de teléfono ya está registrado.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    public String getNombreUserActual() {
        return usuarioActual != null ? usuarioActual.getName() : "Usuario no autenticado";
    }

    public ImageIcon getIconoUserActual() {
        return usuarioActual != null ? usuarioActual.getProfilePhotos() : new ImageIcon();
    }

    public int getNumTelefonoUserActual() {
        return usuarioActual != null ? usuarioActual.getNumTelefono() : -1;
    }

    public String getEmailUserActual() {
        return usuarioActual != null ? usuarioActual.getEmail() : "";
    }

    public boolean isPremiumUserActual() {
        return usuarioActual != null && usuarioActual.isPremium();
    }
}