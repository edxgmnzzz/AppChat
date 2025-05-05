package umu.tds.app.AppChat;

import java.awt.Image;
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
    private static final int PROFILE_IMAGE_SIZE = 100;

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
        usuariosSimulados.put("1234567890", usuarioActual);

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
            }
        } catch (Exception e) {
            LOGGER.severe("Error al cargar imagen desde URL " + urlString + ": " + e.getMessage());
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

    public boolean iniciarSesion(String telefono, String password) {
        if (telefono == null || password == null) {
            LOGGER.warning("Intento de inicio de sesión con credenciales nulas.");
            return false;
        }
        Usuario usuario = usuariosSimulados.get(telefono);
        if (usuario != null && usuario.getPassword().equals(password)) {
            usuarioActual = usuario;
            LOGGER.info("Inicio de sesión exitoso para teléfono: " + telefono);
            notifyObserversChatsRecientes();
            return true;
        }
        LOGGER.warning("Credenciales incorrectas para teléfono: " + telefono);
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
                                    String email, String telefono, LocalDate fechaNacimiento, String rutaFoto, String saludo) {
        String error = validateRegistration(nombreReal, nombreUsuario, password, confirmarPassword, email, telefono);
        if (error != null) {
            JOptionPane.showMessageDialog(null, error, "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        ImageIcon profileIcon = loadImageFromUrl(rutaFoto);
        Usuario nuevoUsuario = new Usuario(profileIcon, nombreReal, fechaNacimiento, Integer.parseInt(telefono), 
                password, email, false, saludo.isEmpty() ? null : saludo, null, null);
        usuariosSimulados.put(telefono, nuevoUsuario);
        LOGGER.info("Usuario registrado: " + nombreUsuario);
        notifyObserversChatsRecientes();
        return true;
    }

    private String validateRegistration(String nombreReal, String nombreUsuario, String password, 
                                        String confirmarPassword, String email, String telefono) {
        if (nombreReal.isEmpty() || nombreUsuario.isEmpty() || password.isEmpty() || email.isEmpty() || telefono.isEmpty()) {
            return "Por favor, complete todos los campos obligatorios";
        }
        if (!password.equals(confirmarPassword)) {
            return "Las contraseñas no coinciden";
        }
        if (usuariosSimulados.containsKey(telefono)) {
            return "El número de teléfono ya está registrado";
        }
        if (usuariosSimulados.values().stream().anyMatch(u -> u.getEmail().equals(email))) {
            return "El correo electrónico ya está registrado";
        }
        return null;
    }

    public boolean actualizarUsuario(String nuevoNombre, String nuevaPassword, String nuevoSaludo, String rutaFoto) {
        if (usuarioActual == null) {
            LOGGER.warning("Intento de actualizar usuario sin estar autenticado.");
            return false;
        }
        if (!nuevoNombre.equals(usuarioActual.getName()) && 
            usuariosSimulados.values().stream().anyMatch(u -> u.getName().equals(nuevoNombre))) {
            LOGGER.warning("Nombre de usuario ya existe: " + nuevoNombre);
            return false;
        }
        if (nuevaPassword == null || nuevaPassword.trim().isEmpty()) {
            LOGGER.warning("La contraseña no puede estar vacía para usuario: " + nuevoNombre);
            return false;
        }
        ImageIcon profileIcon = loadImageFromUrl(rutaFoto);
        String nombreAnterior = usuarioActual.getName();
        usuarioActual.setName(nuevoNombre);
        usuarioActual.setPassword(nuevaPassword);
        usuarioActual.setSaludo(nuevoSaludo);
        usuarioActual.setProfilePhoto(profileIcon);
        if (!nuevoNombre.equals(nombreAnterior)) {
            usuariosSimulados.remove(nombreAnterior);
            usuariosSimulados.put(String.valueOf(usuarioActual.getNumTelefono()), usuarioActual);
        }
        LOGGER.info("Usuario actualizado: " + nuevoNombre);
        notifyObserversChatsRecientes();
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