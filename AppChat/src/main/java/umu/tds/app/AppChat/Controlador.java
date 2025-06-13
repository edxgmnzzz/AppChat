package umu.tds.app.AppChat;

import java.awt.Image;
import java.io.FileOutputStream;
import java.awt.image.BufferedImage;
import java.net.URI;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import umu.tds.app.persistencia.*;

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
    
    private Usuario usuarioActual;
    private Contacto contactoActual;
    private Map<String, Usuario> usuariosRegistrados;
    private AdaptadorUsuarioTDS usuarioDAO;
    private AdaptadorContactoIndividualTDS contactoDAO;
    private AdaptadorGrupoTDS grupoDAO;
    private AdaptadorMensajeTDS mensajeDAO;
    private AdaptadorStatusTDS statusDAO;
    private List<ObserverChats> observersChats;
    private List<ObserverContactos> observersContactos;

    private Controlador() {
        initialize();
    }
    
    public static Controlador getInstancia() {
        return instancia;
    }

    // ########################################################################
    // SECCIÓN 1: INICIALIZACIÓN
    // ########################################################################

    private void initialize() {
        LOGGER.info("Inicializando Controlador...");
        initializeDaosAndCollections();
        realizarSimulacionInicialSiEsNecesario();
        cargarDatosDesdePersistencia();
        LOGGER.info("Inicialización completada.");
    }
    
    private void initializeDaosAndCollections() {
        usuarioDAO = AdaptadorUsuarioTDS.getInstancia();
        contactoDAO = AdaptadorContactoIndividualTDS.getInstancia();
        grupoDAO = AdaptadorGrupoTDS.getInstancia();
        mensajeDAO = AdaptadorMensajeTDS.getInstancia();
        statusDAO = AdaptadorStatusTDS.getInstancia();
        usuariosRegistrados = new HashMap<>();
        observersChats = new ArrayList<>();
        observersContactos = new ArrayList<>();
    }

    private void realizarSimulacionInicialSiEsNecesario() {
        if (!usuarioDAO.recuperarTodosUsuarios().isEmpty()) {
            return;
        }
        LOGGER.info("--- BASE DE DATOS VACÍA: REALIZANDO SIMULACIÓN INICIAL ---");

        Usuario florentino = new Usuario("600111222", "Florentino Pérez", "pass1", "f@p.com", "Hala Madrid", null, false);
        usuarioDAO.registrarUsuario(florentino);
        Usuario laporta = new Usuario("600333444", "Joan Laporta", "pass2", "j@l.com", "Visca Barça", null, false);
        usuarioDAO.registrarUsuario(laporta);

        ContactoIndividual convDeFlorentino = new ContactoIndividual("Joan Laporta", 0, laporta.getTelefono(), laporta);
        contactoDAO.registrarContacto(convDeFlorentino);
        
        ContactoIndividual convDeLaporta = new ContactoIndividual("Florentino Pérez", 0, florentino.getTelefono(), florentino);
        contactoDAO.registrarContacto(convDeLaporta);
        
        florentino.addContacto(convDeFlorentino);
        usuarioDAO.modificarUsuario(florentino);

        laporta.addContacto(convDeLaporta);
        usuarioDAO.modificarUsuario(laporta);

        Mensaje mensaje = new Mensaje("¡Hola Joan! ¿Qué tal por Barcelona?", LocalDateTime.now(), florentino, convDeFlorentino);
        mensajeDAO.registrarMensaje(mensaje);

        LOGGER.info("--- SIMULACIÓN INICIAL COMPLETADA Y PERSISTIDA ---");
    }

    private void cargarDatosDesdePersistencia() {
        cargarUsuarios();
        Map<Integer, Contacto> todosLosContactosDelSistema = cargarContactosYGrupos();
        vincularContactosAUsuarios(todosLosContactosDelSistema);
        vincularMensajesAContactos(todosLosContactosDelSistema);
        
        contactoActual = todosLosContactosDelSistema.values().stream()
            .filter(c -> c != null && !c.getMensajes().isEmpty())
            .max(Comparator.comparing(this::getUltimoMensajeTiempo))
            .orElse(null);
    }

    private void cargarUsuarios() {
        for (Usuario u : usuarioDAO.recuperarTodosUsuarios()) {
            try {
                String urlFoto = u.getUrlFoto();
                if (urlFoto != null && !urlFoto.isBlank() && !"null".equalsIgnoreCase(urlFoto)) {
                    u.setFoto(loadImageFromUrl(urlFoto));
                }
            } catch (Exception e) {
                LOGGER.warning("Fallo al cargar imagen para usuario " + u.getTelefono() + ": " + e.getMessage());
            }
            usuariosRegistrados.put(u.getTelefono(), u);
        }
    }
    
    private Map<Integer, Contacto> cargarContactosYGrupos() {
        Map<Integer, Contacto> mapaContactos = new HashMap<>();
        List<ContactoIndividual> individuales = contactoDAO.recuperarTodosContactos();
        if (individuales != null) {
            individuales.forEach(c -> mapaContactos.put(c.getCodigo(), c));
        }
        List<Grupo> grupos = grupoDAO.recuperarTodosGrupos();
        if (grupos != null) {
            grupos.forEach(g -> mapaContactos.put(g.getCodigo(), g));
        }
        return mapaContactos;
    }

    private void vincularContactosAUsuarios(Map<Integer, Contacto> todosLosContactosDelSistema) {
        for (Usuario usuario : usuariosRegistrados.values()) {
            List<Contacto> listaPersonalDeContactos = new ArrayList<>();
            for (int idContacto : usuario.getContactosID()) {
                Contacto contacto = todosLosContactosDelSistema.get(idContacto);
                if (contacto != null) {
                    listaPersonalDeContactos.add(contacto);
                }
            }
            usuario.setContactos(listaPersonalDeContactos);
        }
    }

    private void vincularMensajesAContactos(Map<Integer, Contacto> todosLosContactosDelSistema) {
        List<Mensaje> mensajes = mensajeDAO.recuperarTodosMensajes();
        for (Mensaje mensaje : mensajes) {
            Usuario emisor = usuariosRegistrados.get(mensaje.getEmisor().getTelefono());
            if (emisor == null) continue;
            mensaje.setEmisor(emisor);
            
            Contacto conversacionDelEmisor = todosLosContactosDelSistema.get(mensaje.getReceptor().getCodigo());
            if (conversacionDelEmisor == null || !(conversacionDelEmisor instanceof ContactoIndividual)) continue;
            conversacionDelEmisor.sendMensaje(mensaje);

            Usuario usuarioReceptor = ((ContactoIndividual) conversacionDelEmisor).getUsuario();
            if (usuarioReceptor == null) continue;
            
            Contacto conversacionDelReceptor = usuarioReceptor.getContactos().stream()
                .filter(c -> c instanceof ContactoIndividual ci && ci.getUsuario() != null && ci.getUsuario().equals(emisor))
                .findFirst()
                .orElse(null);
            
            if (conversacionDelReceptor != null) {
                conversacionDelReceptor.sendMensaje(mensaje);
            }
        }
    }
    
    // ########################################################################
    // SECCIÓN 2: LÓGICA DE NEGOCIO Y API PÚBLICA
    // ########################################################################

    public boolean iniciarSesion(String telefono, String password) {
        if (telefono == null || password == null) return false;
        Usuario usuario = usuariosRegistrados.get(telefono);
        if (usuario != null && usuario.getPassword().equals(password)) {
            usuarioActual = usuario;
            contactoActual = obtenerContactos().stream()
                .filter(c -> !c.getMensajes().isEmpty())
                .max(Comparator.comparing(this::getUltimoMensajeTiempo))
                .orElse(null);
            notifyObserversChatsRecientes();
            notifyObserversListaContactos();
            notifyObserversContactoActual(contactoActual);
            return true;
        }
        return false;
    }

    public void cerrarSesion() {
        usuarioActual = null;
        contactoActual = null;
        notifyObserversChatsRecientes();
        notifyObserversContactoActual(null);
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
        nuevoUsuario.setUrlFoto(rutaFoto);
        usuarioDAO.registrarUsuario(nuevoUsuario);
        usuariosRegistrados.put(telefono, nuevoUsuario);
        return true;
    }

    public boolean agregarContacto(ContactoIndividual contacto) {
        if (usuarioActual == null || contacto == null || existeContacto(contacto.getNombre())) {
            return false;
        }
        contactoDAO.registrarContacto(contacto); 
        usuarioActual.addContacto(contacto);
        usuarioDAO.modificarUsuario(usuarioActual);
        notifyObserversListaContactos();
        return true;
    }

    public void crearGrupo(String nombre, List<ContactoIndividual> miembros) {
        if (usuarioActual == null || nombre == null || nombre.trim().isEmpty() || miembros == null || miembros.isEmpty() || existeContacto(nombre)) {
            return;
        }
        Grupo grupo = new Grupo(nombre, 0, miembros, usuarioActual);
        grupoDAO.registrarGrupo(grupo);
        usuarioActual.addContacto(grupo);
        usuarioDAO.modificarUsuario(usuarioActual);
        notifyObserversListaContactos();
    }
    
    public void enviarMensaje(Contacto contactoReceptor, String texto) {
        if (usuarioActual == null || contactoReceptor == null || texto == null || texto.trim().isEmpty()) {
            return;
        }

        if (contactoReceptor instanceof Grupo grupo) {
            for (ContactoIndividual miembro : grupo.getParticipantes()) {
                Contacto contactoMiembro = obtenerContactos().stream()
                                            .filter(c -> c instanceof ContactoIndividual ci && ci.getUsuario() != null && ci.getUsuario().equals(miembro.getUsuario()))
                                            .findFirst().orElse(null);
                if (contactoMiembro != null) {
                    enviarMensaje(contactoMiembro, texto);
                }
            }
            return;
        }

        Mensaje mensaje = new Mensaje(texto, LocalDateTime.now(), usuarioActual, contactoReceptor);
        mensajeDAO.registrarMensaje(mensaje);
        contactoReceptor.sendMensaje(mensaje);
        
        Usuario usuarioReceptor = ((ContactoIndividual) contactoReceptor).getUsuario();
        if (usuarioReceptor != null) {
            Usuario receptorCompleto = usuariosRegistrados.get(usuarioReceptor.getTelefono());
            if (receptorCompleto != null) {
                receptorCompleto.getContactos().stream()
                    .filter(c -> c instanceof ContactoIndividual ci && ci.getUsuario() != null && ci.getUsuario().equals(usuarioActual))
                    .findFirst()
                    .ifPresent(conv -> conv.sendMensaje(mensaje));
            }
        }
        
        notifyObserversChatsRecientes();
        notifyObserversContactoActual(contactoReceptor);
    }
    
    public void eliminarContacto(ContactoIndividual contacto) {
        if (usuarioActual == null || contacto == null) return;
        if (usuarioActual.removeContacto(contacto)) {
            usuarioDAO.modificarUsuario(usuarioActual);
            notifyObserversListaContactos();
            // Opcional: Notificar para actualizar chats si se elimina el chat actual
            if(contacto.equals(contactoActual)) {
                setContactoActual(null);
            }
            notifyObserversChatsRecientes();
        }
    }

    // ########################################################################
    // SECCIÓN 3: MÉTODOS GETTER Y AUXILIARES
    // ########################################################################

    public Usuario getUsuarioActual() { return usuarioActual; }
    public Contacto getContactoActual() { return contactoActual; }
    public void setContactoActual(Contacto contacto) { this.contactoActual = contacto; notifyObserversContactoActual(contacto); }
    public Map<String, Usuario> getusuariosRegistrados() { return usuariosRegistrados; }
    public Usuario buscarUsuarioPorTelefono(String telefono) { return usuariosRegistrados.get(telefono); }
    public List<Contacto> obtenerContactos() { return usuarioActual != null ? usuarioActual.getContactos() : Collections.emptyList(); }
    public Contacto obtenerContactoPorNombre(String nombre) { return obtenerContactos().stream().filter(c -> c.getNombre().equalsIgnoreCase(nombre)).findFirst().orElse(null); }
    public List<Mensaje> getMensajes(Contacto contacto) { return (contacto != null) ? contacto.getMensajes().stream().sorted().collect(Collectors.toList()) : Collections.emptyList(); }
    public String[] getChatsRecientes() { if (usuarioActual == null) { return new String[0]; } return obtenerContactos().stream().filter(c -> !c.getMensajes().isEmpty()).sorted(Comparator.comparing(this::getUltimoMensajeTiempo).reversed()).map(c -> "Chat con " + c.getNombre()).toArray(String[]::new); }
    private LocalDateTime getUltimoMensajeTiempo(Contacto contacto) { return contacto.getMensajes().stream().map(Mensaje::getHora).max(LocalDateTime::compareTo).orElse(LocalDateTime.MIN); }
    public ImageIcon loadImageFromUrl(String urlString) { if (urlString == null || urlString.isBlank()) return new ImageIcon(); try { URL url = new URI(urlString).toURL(); BufferedImage image = ImageIO.read(url); if (image != null) { Image scaledImage = image.getScaledInstance(PROFILE_IMAGE_SIZE, PROFILE_IMAGE_SIZE, Image.SCALE_SMOOTH); return new ImageIcon(scaledImage); } } catch (Exception e) { LOGGER.severe("Error al cargar imagen: " + e.getMessage()); } return new ImageIcon(); }
    public int generarCodigoContacto() { Set<Integer> codigosExistentes = new HashSet<>(); contactoDAO.recuperarTodosContactos().forEach(c -> codigosExistentes.add(c.getCodigo())); grupoDAO.recuperarTodosGrupos().forEach(g -> codigosExistentes.add(g.getCodigo())); int codigoGenerado; do { codigoGenerado = (int) (System.nanoTime() % Integer.MAX_VALUE); } while (codigosExistentes.contains(codigoGenerado)); return codigoGenerado; }
    public boolean existeUsuario(String telefono) { return usuariosRegistrados.containsKey(telefono); }
    public boolean existeContacto(String nombre) { if (usuarioActual == null || nombre == null || nombre.trim().isEmpty()) { return false; } return obtenerContactos().stream().anyMatch(c -> c.getNombre().equalsIgnoreCase(nombre.trim())); }
    private String validateRegistration(String nombreReal, String nombreUsuario, String password, String confirmarPassword, String email, String telefono) { if (nombreReal.isEmpty() || nombreUsuario.isEmpty() || password.isEmpty() || email.isEmpty() || telefono.isEmpty()) return "Por favor, complete todos los campos obligatorios"; if (!password.equals(confirmarPassword)) return "Las contraseñas no coinciden"; if (usuariosRegistrados.containsKey(telefono)) return "El número de teléfono ya está registrado"; if (usuariosRegistrados.values().stream().anyMatch(u -> u.getEmail().equals(email))) return "El correo electrónico ya está registrado"; return null; }

    // ########################################################################
    // SECCIÓN 4: OBSERVERS
    // ########################################################################

    public void addObserverChats(ObserverChats o) { if (!observersChats.contains(o)) observersChats.add(o); }
    public void removeObserverChats(ObserverChats o) { observersChats.remove(o); }
    public void addObserverContactos(ObserverContactos o) { if (!observersContactos.contains(o)) observersContactos.add(o); }
    public void removeObserverContactos(ObserverContactos o) { observersContactos.remove(o); }
    private void notifyObserversContactoActual(Contacto c) { observersChats.forEach(o -> o.updateContactoActual(c)); }
    private void notifyObserversListaContactos() { observersContactos.forEach(ObserverContactos::updateListaContactos); }
    private void notifyObserversChatsRecientes() { String[] chatsRecientes = getChatsRecientes(); observersChats.forEach(observer -> observer.updateChatsRecientes(chatsRecientes));}

    // ########################################################################
    // SECCIÓN 5: FUNCIONALIDADES ADICIONALES
    // ########################################################################

    public boolean actualizarUsuario(String nuevoNombre, String nuevaPassword, String nuevoSaludo, String rutaFoto) { if (usuarioActual == null || nuevaPassword == null || nuevaPassword.trim().isEmpty()) return false; usuarioActual.setName(nuevoNombre); usuarioActual.setPassword(nuevaPassword); usuarioActual.setSaludo(nuevoSaludo); usuarioActual.setFoto(loadImageFromUrl(rutaFoto)); usuarioDAO.modificarUsuario(usuarioActual); notifyObserversChatsRecientes(); return true; }
    public boolean establecerStatus(String mensaje, String rutaImagen) { if (usuarioActual == null) return false; Status status = new Status(loadImageFromUrl(rutaImagen), mensaje); statusDAO.registrarEstado(status); return true; }
    public void activarPremiumConDescuento() { if (usuarioActual == null) return; double precioBase = 100.0; CalculadoraDescuentos calculadora = new CalculadoraDescuentos(usuarioActual, this); String resultado = calculadora.calcularDescuentos(precioBase); if (JOptionPane.showConfirmDialog(null, resultado + "\n¿Desea activar Premium?", "Resumen de descuentos", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) { usuarioActual.setPremium(true); usuarioDAO.modificarUsuario(usuarioActual); JOptionPane.showMessageDialog(null, "¡Felicidades! Ya eres usuario Premium."); } }
    public boolean exportarPdfConDatos(String rutaDestino) { if (usuarioActual == null || !usuarioActual.isPremium()) { JOptionPane.showMessageDialog(null, "Solo los usuarios Premium pueden exportar a PDF.", "Acceso denegado", JOptionPane.ERROR_MESSAGE); return false; } Document document = new Document(); try { PdfWriter.getInstance(document, new FileOutputStream(rutaDestino)); document.open(); /* ... lógica de escritura ... */ document.close(); return true; } catch (Exception e) { e.printStackTrace(); return false; } }
    public List<Mensaje> buscarMensajes(String texto, String telefono, String nombre) { return obtenerContactos().stream().flatMap(contacto -> contacto.getMensajes().stream()).filter(mensaje -> (texto == null || texto.isBlank() || mensaje.getTexto().toLowerCase().contains(texto.toLowerCase())) && (telefono == null || telefono.isBlank() || mensaje.getEmisor().getTelefono().equals(telefono) || (mensaje.getReceptor() instanceof ContactoIndividual ci && ci.getTelefono().equals(telefono))) && (nombre == null || nombre.isBlank() || mensaje.getEmisor().getNombre().equalsIgnoreCase(nombre) || mensaje.getReceptor().getNombre().equalsIgnoreCase(nombre))).sorted().collect(Collectors.toList()); }
    public int contarMensajesDelUsuario(Usuario usuario) { return (int) obtenerContactos().stream().flatMap(c -> c.getMensajes().stream()).filter(m -> m.getEmisor().equals(usuario)).count(); }
    public ContactoIndividual obtenerContactoPorUsuario(Usuario usuario) {
        if (usuarioActual == null || usuario == null) {
            return null;
        }
        
        return obtenerContactos().stream()
            .filter(c -> c instanceof ContactoIndividual) // Nos aseguramos de que es un contacto individual
            .map(c -> (ContactoIndividual) c) // Lo convertimos al tipo correcto
            .filter(ci -> ci.getUsuario() != null && ci.getUsuario().equals(usuario)) // Comparamos el usuario interno
            .findFirst()
            .orElse(null);
    }
 // En Controlador.java

    public String getNombreUserActual() {
        return (usuarioActual != null) ? usuarioActual.getNombre() : "Desconectado";
    }

    public ImageIcon getIconoUserActual() {
        return (usuarioActual != null && usuarioActual.getFoto() != null) 
               ? usuarioActual.getFoto() 
               : new ImageIcon();
    }

    public boolean isPremiumUserActual() {
        return (usuarioActual != null) && usuarioActual.isPremium();
    }

    public String getEmailUserActual() {
        return (usuarioActual != null) ? usuarioActual.getEmail() : "";
    }
    
    public int getNumTelefonoUserActual() {
        return (usuarioActual != null && usuarioActual.getTelefono() != null) 
               ? Integer.parseInt(usuarioActual.getTelefono()) 
               : -1;
    }
}