package umu.tds.app.AppChat;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
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

/**
 * Clase Controlador que implementa el patrón Singleton y Subject para gestionar usuarios,
 * contactos y mensajes en la aplicación AppChat.
 */
public class Controlador {
    private static final Controlador instancia;
    private static final Logger LOGGER;
    private static final int PROFILE_IMAGE_SIZE = 100; // Fixed size for profile images (100x100 pixels)

    static {
        LOGGER = Logger.getLogger(Controlador.class.getName());
        try {
            instancia = new Controlador();
        } catch (Exception e) {
            throw new ExceptionInInitializerError("Error al inicializar Controlador: " + e.getMessage());
        }
    }

    private Map<String, Usuario> usuariosSimulados;
    private List<Contacto> contactos;
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
                ImageIcon icon = new ImageIcon(image);
                Image scaledImage = icon.getImage().getScaledInstance(PROFILE_IMAGE_SIZE, PROFILE_IMAGE_SIZE, Image.SCALE_SMOOTH);
                LOGGER.info("Imagen cargada y redimensionada desde URL: " + urlString);
                return new ImageIcon(scaledImage);
            } else {
                LOGGER.warning("No se pudo cargar la imagen desde URL: " + urlString);
            }
        } catch (Exception e) {
            LOGGER.severe("Error al cargar imagen desde URL " + urlString + ": " + e.getMessage());
        }
        return new ImageIcon();
    }

    private ImageIcon loadImageFromFile(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                LOGGER.warning("Archivo no encontrado: " + filePath);
                return new ImageIcon();
            }
            BufferedImage image = ImageIO.read(file);
            if (image != null) {
                ImageIcon icon = new ImageIcon(image);
                Image scaledImage = icon.getImage().getScaledInstance(PROFILE_IMAGE_SIZE, PROFILE_IMAGE_SIZE, Image.SCALE_SMOOTH);
                LOGGER.info("Imagen cargada y redimensionada desde archivo: " + filePath);
                return new ImageIcon(scaledImage);
            } else {
                LOGGER.warning("No se pudo cargar la imagen desde archivo: " + filePath);
            }
        } catch (IOException e) {
            LOGGER.severe("Error al cargar imagen desde archivo " + filePath + ": " + e.getMessage());
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
            LOGGER.warning("Intento de inicio de sesión con credenciales nulas.");
            return false;
        }
        Usuario usuario = usuariosSimulados.get(nombreUsuario);
        if (usuario != null && usuario.getPassword().equals(password)) {
            usuarioActual = usuario;
            LOGGER.info("Inicio de sesión exitoso para usuario: " + nombreUsuario);
            notifyObserversChatsRecientes();
            return true;
        }
        LOGGER.warning("Credenciales incorrectas para usuario: " + nombreUsuario);
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
        if (usuarioActual == null) {
            LOGGER.warning("Intento de cerrar sesión sin usuario autenticado.");
            return;
        }
        String nombreUsuario = usuarioActual.getName();
        usuarioActual = null;
        contactoActual = null;
        LOGGER.info("Sesión cerrada para usuario: " + nombreUsuario);
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
            LOGGER.warning("Intento de envío de mensaje inválido.");
            return;
        }
        String clave = generarClaveConversacion(contacto);
        mensajes.computeIfAbsent(clave, k -> new ArrayList<>()).add("Tú: " + mensaje);
        LOGGER.info("Mensaje enviado a " + contacto.getNombre() + ": " + mensaje);
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
            LOGGER.warning("Intento de agregar contacto nulo.");
            return false;
        }

        boolean isDuplicate = contactos.stream().anyMatch(c -> {
            if (c instanceof ContactoIndividual) {
                return ((ContactoIndividual) c).getMovil() == contacto.getMovil();
            } else {
                return c.getNombre().equalsIgnoreCase(contacto.getNombre());
            }
        });

        if (isDuplicate) {
            LOGGER.warning("Contacto duplicado detectado: " + contacto.getNombre());
            return false;
        }

        contactos.add(contacto);
        LOGGER.info("Contacto agregado: " + contacto.getNombre());
        notifyObserversChatsRecientes();
        notifyObserversListaContactos();
        return true;
    }

    public void eliminarContacto(ContactoIndividual contacto) {
        if (contacto == null) {
            LOGGER.warning("Intento de eliminar contacto nulo.");
            return;
        }
        if (contactos.remove(contacto)) {
            String clave = generarClaveConversacion(contacto);
            mensajes.remove(clave);
            LOGGER.info("Contacto eliminado: " + contacto.getNombre());
            notifyObserversChatsRecientes();
        } else {
            LOGGER.warning("Contacto no encontrado para eliminar: " + contacto.getNombre());
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
            LOGGER.warning("Intento de crear grupo sin usuario autenticado.");
            return;
        }
        if (nombre == null || nombre.trim().isEmpty() || miembros == null || miembros.isEmpty()) {
            LOGGER.warning("Intento de crear grupo con nombre o miembros inválidos.");
            return;
        }
        if (contactos.stream().anyMatch(c -> c.getNombre().equalsIgnoreCase(nombre))) {
            LOGGER.warning("Nombre de grupo ya existe: " + nombre);
            return;
        }

        List<ContactoIndividual> miembrosCopia = new ArrayList<>(miembros);
        Grupo nuevoGrupo = new Grupo(nombre, miembrosCopia, usuarioActual);
        contactos.add(nuevoGrupo);
        
        List<Contacto> userContactos = usuarioActual.getContactos();
        if (userContactos == null) {
            userContactos = new ArrayList<>();
        }
        userContactos.add(nuevoGrupo);

        LOGGER.info("Grupo creado: " + nombre + " con " + miembros.size() + " miembros");
        notifyObserversChatsRecientes();
        notifyObserversListaContactos();
    }

    public boolean registrarUsuario(String nombreReal, String nombreUsuario, String password, String confirmarPassword,
                                    String email, int telefono, LocalDate fechaNacimiento, String rutaFoto) {
        if (!validateRegistration(nombreReal, nombreUsuario, password, confirmarPassword, email, telefono)) {
            return false;
        }
        ImageIcon profileIcon;
        if (rutaFoto != null && !rutaFoto.isEmpty()) {
            if (rutaFoto.startsWith("http://") || rutaFoto.startsWith("https://")) {
                profileIcon = loadImageFromUrl(rutaFoto);
            } else {
                profileIcon = loadImageFromFile(rutaFoto);
            }
        } else {
            profileIcon = new ImageIcon();
        }
        Usuario nuevoUsuario = new Usuario(profileIcon, nombreReal, fechaNacimiento, telefono, password, 
                email, false, "¡Hola, soy nuevo!", null, null);
        usuariosSimulados.put(nombreUsuario, nuevoUsuario);
        LOGGER.info("Usuario registrado: " + nombreUsuario);
        notifyObserversChatsRecientes();
        return true;
    }

    private boolean validateRegistration(String nombreReal, String nombreUsuario, String password, 
                                         String confirmarPassword, String email, int telefono) {
        if (nombreReal == null || nombreUsuario == null || password == null || confirmarPassword == null || email == null) {
            LOGGER.warning("Intento de registro con campos nulos.");
            return false;
        }
        if (!password.equals(confirmarPassword)) {
            LOGGER.warning("Las contraseñas no coinciden para usuario: " + nombreUsuario);
            return false;
        }
        if (usuariosSimulados.containsKey(nombreUsuario)) {
            LOGGER.warning("Nombre de usuario ya existe: " + nombreUsuario);
            return false;
        }
        if (usuariosSimulados.values().stream().anyMatch(u -> u.getEmail().equals(email))) {
            LOGGER.warning("Correo electrónico ya registrado: " + email);
            return false;
        }
        if (usuariosSimulados.values().stream().anyMatch(u -> u.getNumTelefono() == telefono)) {
            LOGGER.warning("Número de teléfono ya registrado: " + telefono);
            return false;
        }
        return true;
    }

    public boolean actualizarUsuario(String nuevoNombre, String nuevaPassword, String nuevoSaludo, String rutaFoto) {
        if (usuarioActual == null) {
            LOGGER.warning("Intento de actualizar usuario sin estar autenticado.");
            return false;
        }

        // Validar nombre único (si cambió)
        if (!nuevoNombre.equals(usuarioActual.getName()) && usuariosSimulados.containsKey(nuevoNombre)) {
            LOGGER.warning("Nombre de usuario ya existe: " + nuevoNombre);
            return false;
        }

        // Validar contraseña no vacía
        if (nuevaPassword == null || nuevaPassword.trim().isEmpty()) {
            LOGGER.warning("La contraseña no puede estar vacía para usuario: " + nuevoNombre);
            return false;
        }

        // Actualizar foto de perfil
        ImageIcon profileIcon;
        if (rutaFoto != null && !rutaFoto.isEmpty()) {
            if (rutaFoto.startsWith("http://") || rutaFoto.startsWith("https://")) {
                profileIcon = loadImageFromUrl(rutaFoto);
            } else {
                profileIcon = loadImageFromFile(rutaFoto);
            }
        } else {
            profileIcon = usuarioActual.getProfilePhotos(); // Mantener la foto actual si no se proporciona una nueva
        }

        // Actualizar datos del usuario
        String nombreAnterior = usuarioActual.getName();
        usuarioActual.setName(nuevoNombre);
        usuarioActual.setPassword(nuevaPassword);
        usuarioActual.setSaludo(nuevoSaludo);
        usuarioActual.setProfilePhoto(profileIcon);

        // Actualizar la clave en usuariosSimulados
        if (!nuevoNombre.equals(nombreAnterior)) {
            usuariosSimulados.remove(nombreAnterior);
            usuariosSimulados.put(nuevoNombre, usuarioActual);
        }

        LOGGER.info("Usuario actualizado: " + nuevoNombre);
        notifyObserversChatsRecientes(); // Notificar cambios (puede afectar UI)
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