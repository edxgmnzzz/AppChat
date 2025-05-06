package umu.tds.app.AppChat;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

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

    private Map<String, Usuario> usuariosRegistrados;
    private List<Contacto> contactos;
    private Usuario usuarioActual;
    private Contacto contactoActual;
    private List<ObserverChats> observersChats;
    private List<ObserverContactos> observersContactos;

    private Controlador() {
        initialize();
    }

    private void initialize() {
        usuariosRegistrados = new HashMap<>();
        contactos = new ArrayList<>();
        observersChats = new ArrayList<>();
        observersContactos = new ArrayList<>();

        String defaultImageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/3/36/Florentino_perez.jpg/220px-Florentino_perez.jpg";
        ImageIcon profileIcon = loadImageFromUrl(defaultImageUrl);

        // Crear usuarios
        Usuario florentino = new Usuario("1234567890", "Florentino Pérez", "admin", "admin@gmail.com", "Soy su florentineza", profileIcon, false);
        Usuario oscar = new Usuario("123456789", "Oscar", "12345", "oscar@gmail.com", "Hola", null, true);
        Usuario javi = new Usuario("234567890", "Javi", "password", "javi@gmail.com", "Hey!", null, false);
        Usuario lucia = new Usuario("345678901", "Lucia", "12345", "lucia@gmail.com", "Saludos", null, true);

        usuariosRegistrados.put(florentino.getTelefono(), florentino);
        usuariosRegistrados.put(oscar.getTelefono(), oscar);
        usuariosRegistrados.put(javi.getTelefono(), javi);
        usuariosRegistrados.put(lucia.getTelefono(), lucia);

        // Crear contactos
        ContactoIndividual contactoOscar = new ContactoIndividual("Oscar", 1, "123456789", oscar);
        ContactoIndividual contactoJavi = new ContactoIndividual("Javi", 2, "234567890", javi);
        ContactoIndividual contactoLucia = new ContactoIndividual("Lucia", 3, "345678901", lucia);
        List<ContactoIndividual> miembrosGrupo = new ArrayList<>(List.of(contactoOscar, contactoJavi, contactoLucia));
        Grupo grupoAmigos = new Grupo("Amigos", 4, miembrosGrupo, florentino);

        contactos.add(contactoOscar);
        contactos.add(contactoJavi);
        contactos.add(contactoLucia);
        contactos.add(grupoAmigos);

        // Mensajes de prueba
        Mensaje mensaje1 = new Mensaje("Hola Oscar", LocalDateTime.now().minusMinutes(5), florentino, contactoOscar);
        Mensaje mensaje2 = new Mensaje("¡Hola Florentino!", LocalDateTime.now().minusMinutes(4), oscar, contactoOscar);
        contactoOscar.sendMensaje(mensaje1);
        contactoOscar.sendMensaje(mensaje2);
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
        if (!observersChats.contains(observer)) {
            observersChats.add(observer);
        }
    }

    public void removeObserverChats(ObserverChats observer) {
        observersChats.remove(observer);
    }

    private void notifyObserversChatsRecientes() {
        String[] chatsRecientes = getChatsRecientes();
        observersChats.forEach(observer -> observer.updateChatsRecientes(chatsRecientes));
    }

    private void notifyObserversContactoActual(Contacto contacto) {
        observersChats.forEach(observer -> observer.updateContactoActual(contacto));
    }

    public void addObserverContactos(ObserverContactos observer) {
        if (!observersContactos.contains(observer)) {
            observersContactos.add(observer);
        }
    }

    public void removeObserverContactos(ObserverContactos observer) {
        observersContactos.remove(observer);
    }

    private void notifyObserversListaContactos() {
        observersContactos.forEach(ObserverContactos::updateListaContactos);
    }

    public boolean iniciarSesion(String telefono, String password) {
        if (telefono == null || password == null) {
            LOGGER.warning("Intento de inicio de sesión con credenciales nulas.");
            return false;
        }
        Usuario usuario = usuariosRegistrados.get(telefono);
        if (usuario != null && usuario.getPassword().equals(password)) {
            usuarioActual = usuario;
            LOGGER.info("Inicio de sesión exitoso para teléfono: " + telefono);
            notifyObserversChatsRecientes();
            notifyObserversListaContactos();
            return true;
        }
        LOGGER.warning("Credenciales incorrectas para teléfono: " + telefono);
        return false;
    }

    public Contacto obtenerContactoPorNombre(String nombre) {
        return contactos.stream()
            .filter(c -> c.getNombre().equalsIgnoreCase(nombre))
            .findFirst()
            .orElse(null);
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
        notifyObserversContactoActual(null);
    }

    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public Contacto getContactoActual() {
        return contactoActual;
    }

    public void setContactoActual(Contacto contacto) {
        this.contactoActual = contacto;
        for (ObserverChats obs : observersChats) {
            obs.updateContactoActual(contacto);
        }
    }


    public List<Contacto> obtenerContactos() {
        return Collections.unmodifiableList(contactos);
    }

    public void enviarMensaje(Contacto contacto, String mensaje) {
        if (usuarioActual == null || contacto == null || mensaje == null || mensaje.trim().isEmpty()) {
            LOGGER.warning("Intento de envío de mensaje inválido.");
            return;
        }

        // 💬 Si es grupo, reenviar individualmente
        if (contacto instanceof Grupo grupo) {
            for (ContactoIndividual c : grupo.getParticipantes()) {
                enviarMensaje(c, mensaje);  // Envío individual a cada contacto
            }
            LOGGER.info("Mensaje enviado al grupo " + grupo.getNombre() + ": " + mensaje);
            return;
        }

        // Envío estándar
        Mensaje msg = new Mensaje(mensaje, LocalDateTime.now(), usuarioActual, contacto);
        contacto.sendMensaje(msg);
        LOGGER.info("Mensaje enviado a " + contacto.getNombre() + ": " + mensaje);

        // (Opcional) Respuesta automática
        if (contacto instanceof ContactoIndividual contactoInd) {
            Usuario receptor = contactoInd.getUsuario();
            if (receptor != null && !receptor.equals(usuarioActual)) {
                Mensaje respuesta = new Mensaje("Respuesta automática", LocalDateTime.now().plusSeconds(1), receptor, contacto);
                contacto.sendMensaje(respuesta);
                LOGGER.info("Respuesta automática enviada desde " + receptor.getName() + " a " + usuarioActual.getName());
            }
        }

        notifyObserversChatsRecientes();
        notifyObserversContactoActual(contacto);
    }

    public Usuario buscarUsuarioPorTelefono(String telefono) {
        return usuariosRegistrados.get(telefono);
    }



    public List<String> obtenerMensajes(Contacto contacto) {
        if (contacto == null || usuarioActual == null) {
            return Collections.emptyList();
        }

        List<Mensaje> mensajes = contacto.getMensajes().stream()
            .filter(msg ->
                msg.getEmisor().equals(usuarioActual) ||
                msg.getReceptor().equals(contacto))
            .sorted(Comparator.comparing(Mensaje::getHora))
            .collect(Collectors.toList());

        return mensajes.stream()
            .map(msg -> {
                String autor;

                if (msg.getEmisor().equals(usuarioActual)) {
                    autor = "Tú";
                } else {
                    String telefonoEmisor = msg.getEmisor().getTelefono();
                    ContactoIndividual contactoEncontrado = usuarioActual.getContactos().stream()
                        .filter(c -> c instanceof ContactoIndividual)
                        .map(c -> (ContactoIndividual) c)
                        .filter(ci -> ci.getTelefono().equals(telefonoEmisor))
                        .findFirst()
                        .orElse(null);

                    if (contactoEncontrado != null) {
                        autor = contactoEncontrado.getNombre();
                    } else {
                        autor = telefonoEmisor;
                    }
                }

                return autor + ": " + msg.getTexto();
            })
            .collect(Collectors.toList());
    }

      

    public String[] getChatsRecientes() {
        List<String> chats = new ArrayList<>();
        for (Contacto contacto : contactos) {
            List<Mensaje> mensajes = new ArrayList<>();
            mensajes.addAll(contacto.getMensajesEnviados());
            mensajes.addAll(contacto.getMensajesRecibidos(Optional.of(usuarioActual)));
            if (!mensajes.isEmpty()) {
                chats.add("Chat con " + contacto.getNombre());
            }
        }
        Collections.sort(chats, (a, b) -> {
            String nombreA = a.replace("Chat con ", "");
            String nombreB = b.replace("Chat con ", "");
            Contacto contactoA = obtenerContactoPorNombre(nombreA);
            Contacto contactoB = obtenerContactoPorNombre(nombreB);
            LocalDateTime ultimaA = getUltimoMensajeTiempo(contactoA);
            LocalDateTime ultimaB = getUltimoMensajeTiempo(contactoB);
            return ultimaB.compareTo(ultimaA);
        });
        return chats.isEmpty() ? new String[]{"No hay chats recientes"} : chats.toArray(new String[0]);
    }

    private LocalDateTime getUltimoMensajeTiempo(Contacto contacto) {
        if (contacto == null) return LocalDateTime.MIN;
        List<Mensaje> mensajes = new ArrayList<>();
        mensajes.addAll(contacto.getMensajesEnviados());
        mensajes.addAll(contacto.getMensajesRecibidos(Optional.of(usuarioActual)));
        return mensajes.stream()
            .map(Mensaje::getHora)
            .max(LocalDateTime::compareTo)
            .orElse(LocalDateTime.MIN);
    }

    public void activarPremium() {
        if (usuarioActual != null) {
            usuarioActual.setPremium(true);
            LOGGER.info("Usuario " + usuarioActual.getName() + " activado como Premium");
        }
    }

    public String generarClaveConversacion(Contacto contacto) {
        if (usuarioActual == null || contacto == null) {
            LOGGER.warning("No se puede generar clave de conversación: usuario o contacto nulo");
            return "";
        }
        return usuarioActual.getTelefono() + "_" + contacto.getCodigo();
    }

    public boolean agregarContacto(ContactoIndividual contacto) {
        if (contacto == null) {
            LOGGER.warning("Intento de agregar contacto nulo.");
            return false;
        }
        boolean isDuplicate = contactos.stream().anyMatch(c ->
            c instanceof ContactoIndividual && ((ContactoIndividual) c).getTelefono() == contacto.getTelefono() ||
            c.getNombre().equalsIgnoreCase(contacto.getNombre()));
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
    
    public ContactoIndividual obtenerContactoPorUsuario(Usuario usuario) {
        return usuarioActual.getContactos().stream()
            .filter(c -> c instanceof ContactoIndividual)
            .map(c -> (ContactoIndividual) c)
            .filter(ci -> ci.getUsuario() != null && ci.getUsuario().equals(usuario))
            .findFirst()
            .orElse(null);
    }


    public void eliminarContacto(ContactoIndividual contacto) {
        if (contacto == null) {
            LOGGER.warning("Intento de eliminar contacto nulo.");
            return;
        }
        if (contactos.remove(contacto)) {
            LOGGER.info("Contacto eliminado: " + contacto.getNombre());
            notifyObserversChatsRecientes();
            notifyObserversListaContactos();
        } else {
            LOGGER.warning("Contacto no encontrado para eliminar: " + contacto.getNombre());
        }
    }

    public boolean nuevoContacto(ContactoIndividual contacto) {
        return agregarContacto(contacto);
    }

    public void crearGrupo(String nombre, List<ContactoIndividual> miembros) {
        if (usuarioActual == null || nombre == null || nombre.trim().isEmpty() || miembros == null || miembros.isEmpty()) {
            LOGGER.warning("Intento de crear grupo inválido.");
            return;
        }
        if (contactos.stream().anyMatch(c -> c.getNombre().equalsIgnoreCase(nombre))) {
            LOGGER.warning("Nombre de grupo ya existe: " + nombre);
            return;
        }
        int codigo = (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
        Grupo nuevoGrupo = new Grupo(nombre, codigo, miembros, usuarioActual);
        contactos.add(nuevoGrupo);
        LOGGER.info("Grupo creado: " + nombre + " con " + miembros.size() + " miembros");
        notifyObserversChatsRecientes();
        notifyObserversListaContactos();
    }

    public boolean registrarUsuario(String nombreReal, String nombreUsuario, String password, String confirmarPassword,
                                    String email, String telefono, String rutaFoto, String saludo) {
        String error = validateRegistration(nombreReal, nombreUsuario, password, confirmarPassword, email, telefono);
        if (error != null) {
            JOptionPane.showMessageDialog(null, error, "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        ImageIcon profileIcon = loadImageFromUrl(rutaFoto);
        Usuario nuevoUsuario = new Usuario(telefono, nombreReal, password, email, saludo, profileIcon, false);
        usuariosRegistrados.put(telefono, nuevoUsuario);
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
        if (usuariosRegistrados.containsKey(telefono)) {
            return "El número de teléfono ya está registrado";
        }
        if (usuariosRegistrados.values().stream().anyMatch(u -> u.getEmail().equals(email))) {
            return "El correo electrónico ya está registrado";
        }
        return null;
    }

    public boolean actualizarUsuario(String nuevoNombre, String nuevaPassword, String nuevoSaludo, String rutaFoto) {
        if (usuarioActual == null) {
            LOGGER.warning("Intento de actualizar usuario sin estar autenticado.");
            return false;
        }
        if (nuevaPassword == null || nuevaPassword.trim().isEmpty()) {
            LOGGER.warning("La contraseña no puede estar vacía para usuario: " + nuevoNombre);
            return false;
        }
        ImageIcon profileIcon = loadImageFromUrl(rutaFoto);
        usuarioActual.setName(nuevoNombre);
        usuarioActual.setPassword(nuevaPassword);
        usuarioActual.setSaludo(nuevoSaludo);
        usuarioActual.setFoto(profileIcon);
        LOGGER.info("Usuario actualizado: " + nuevoNombre);
        notifyObserversChatsRecientes();
        return true;
    }

    public boolean existeUsuario(String telefono) {
        return usuariosRegistrados.containsKey(telefono);
    }

    public String getNombreUserActual() {
        return usuarioActual != null ? usuarioActual.getName() : "Usuario no autenticado";
    }

    public ImageIcon getIconoUserActual() {
        return usuarioActual != null ? usuarioActual.getFoto() : new ImageIcon();
    }

    public int getNumTelefonoUserActual() {
        return usuarioActual != null ? Integer.parseInt(usuarioActual.getTelefono()) : -1;
    }

    public String getEmailUserActual() {
        return usuarioActual != null ? usuarioActual.getEmail() : "";
    }

    public boolean isPremiumUserActual() {
        return usuarioActual != null && usuarioActual.isPremium();
    }

    public boolean existeContacto(String nombre) {
        return contactos.stream().anyMatch(c -> c.getNombre().equalsIgnoreCase(nombre));
    }

    public Map<String, Usuario> getusuariosRegistrados() {
        return usuariosRegistrados;
    }

    public int generarCodigoContacto() {
        int codigo;
        do {
            codigo = (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
        } while (isCodigoUsado(codigo));
        LOGGER.info("Código de contacto generado: " + codigo);
        return codigo;
    }

    private boolean isCodigoUsado(int codigo) {
        return contactos.stream().anyMatch(c -> c.getCodigo() == codigo);
    }
}