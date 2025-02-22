package umu.tds.app.AppChat;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * Clase Controlador que implementa el patrón Singleton para gestionar usuarios,
 * contactos y mensajes en la aplicación AppChat.
 */
public class Controlador {
    private static final Controlador instancia = new Controlador();
    private Map<String, Usuario> usuariosSimulados;  // Almacena los usuarios simulados
    private List<ContactoIndividual> contactos;      // Lista de contactos individuales
    private Map<String, List<String>> mensajes;      // Historial de mensajes
    private Usuario usuarioActual;                   // Usuario actualmente autenticado

    private Controlador(){
        // Inicialización de usuarios simulados
        usuariosSimulados = new HashMap<>();
        
        String path = "https://upload.wikimedia.org/wikipedia/commons/thumb/3/36/Florentino_perez.jpg/220px-Florentino_perez.jpg";
        BufferedImage image = null;
        try {
            URI uri = new URI(path);  // Convert String to URI
            URL url = uri.toURL();    // Convert URI to URL
            image = ImageIO.read(url); // Load the image

            if (image != null) {
                System.out.println("Image loaded successfully!");
            } else {
                System.out.println("Failed to load image.");
            }

        } catch (URISyntaxException e) {
            System.err.println("Invalid URI: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error loading image: " + e.getMessage());
        }
        
        usuarioActual = new Usuario(new ImageIcon(image), "Florentino P�rez", LocalDate.of(1990, 1, 1), 1234567890, "1", "admin@gmail.com", false, "Soy su florentineza", null, null);
        
        //usuariosSimulados.put("a", new Usuario(new ImageIcon(image), "Florentino P�rez", LocalDate.of(1990, 1, 1), 1234567890, "1", "admin@gmail.com", false, "Soy su florentineza", null, null));
        //usuariosSimulados.put("usuario", new Usuario(new ImageIcon(), "Usuario", LocalDate.of(2000, 1, 1), 987654321, "usuario", "password", "user@gmail.com"));

        // Inicialización de contactos simulados
        contactos = new ArrayList<>();
        contactos.add(new ContactoIndividual("Oscar", 123456789, new Usuario(new ImageIcon(), "Oscar", LocalDate.of(1990, 1, 1), 123456789, "12345", "oscar@gmail.com", true, "Hola", null, null)));
        //contactos.add(new ContactoIndividual("Javi", 234567890, new Usuario(new ImageIcon(), "Javi", LocalDate.of(1995, 1, 1), 234567890, "javi", "password", "javi@gmail.com")));
        //contactos.add(new ContactoIndividual("Lucia", 345678901, new Usuario(new ImageIcon(), "Lucia", LocalDate.of(2000, 1, 1), 345678901, "lucia", "12345", "lucia@gmail.com")));
        //contactos.add(new ContactoIndividual("Gloria", 456789012, new Usuario(new ImageIcon(), "Gloria", LocalDate.of(1985, 1, 1), 456789012, "gloria", "password", "gloria@gmail.com")));

        // Inicialización del historial de mensajes (vacío inicialmente)
        mensajes = new HashMap<>();
    }

    /**
     * Obtiene la instancia única del controlador.
     * @return La instancia del controlador.
     */
    public static Controlador getInstancia() {
        return instancia;
    }

    /**
     * Inicia sesión con el nombre de usuario y contraseña proporcionados.
     * @param nombreUsuario Nombre de usuario.
     * @param password Contraseña.
     * @return true si las credenciales son correctas; false en caso contrario.
     */
    public boolean iniciarSesion(String nombreUsuario, String password) {
        if (nombreUsuario == null || password == null) {
            return false;
        }
        Usuario usuario = usuariosSimulados.get(nombreUsuario);
        if (usuario != null && usuario.getPassword().equals(password)) {
            usuarioActual = usuario;
            return true;
        }
        return false;
    }

    /**
     * Cierra la sesión actual.
     */
    public void cerrarSesion() {
        usuarioActual = null;
    }

    /**
     * Obtiene el usuario actualmente autenticado.
     * @return El usuario actual.
     */
    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    /**
     * Obtiene la lista de contactos individuales.
     * @return Lista de contactos.
     */
    public List<ContactoIndividual> obtenerContactos() {
        return new ArrayList<>(contactos);
    }

    /**
     * Envía un mensaje a un contacto y lo agrega al historial de mensajes.
     * @param contacto Contacto destinatario.
     * @param mensaje Contenido del mensaje.
     */
    public void enviarMensaje(Contacto contacto, String mensaje) {
        if (usuarioActual == null || contacto == null || mensaje == null || mensaje.isEmpty()) {
            return;
        }
        String clave = generarClaveConversacion(contacto);
        mensajes.computeIfAbsent(clave, k -> new ArrayList<>()).add("Tú: " + mensaje);
    }

    /**
     * Obtiene el historial de mensajes con un contacto.
     * @param contacto Contacto.
     * @return Lista de mensajes.
     */
    /*public List<String> obtenerMensajes(ContactoIndividual contacto) {
        String clave = generarClaveConversacion(contacto);
        return mensajes.getOrDefault(clave, new ArrayList<>());
    }*/

    /**
     * Genera una clave única para identificar la conversación entre el usuario actual y un contacto.
     * @param contacto Contacto.
     * @return Clave única de la conversación.
     */
    private String generarClaveConversacion(Contacto contacto) {
        return usuarioActual.getName() + "-" + contacto.getNombre();
    }
    

    /**
     * Obtiene una respuesta simulada del contacto.
     * @param contacto Contacto.
     * @return Respuesta simulada.
     */
    /*public String obtenerRespuesta(ContactoIndividual contacto) {
        return "Gracias por tu mensaje, " + contacto.getUsuario().getNick() + ". ¡Hablamos pronto!";
    }*/

    /**
     * Agrega un nuevo contacto a la lista de contactos.
     * @param contacto Contacto a agregar.
     */
    public void agregarContacto(ContactoIndividual contacto) {
        contactos.add(contacto);
    }

    /**
     * Elimina un contacto de la lista de contactos.
     * @param contacto Contacto a eliminar.
     */
    public void eliminarContacto(ContactoIndividual contacto) {
        contactos.remove(contacto);
    }

    /**
     * Registra un nuevo usuario en el sistema.
     * @param nombreReal Nombre real del usuario.
     * @param nombreUsuario Nombre de usuario.
     * @param password Contraseña.
     * @param confirmarPassword Confirmación de contraseña.
     * @param email Correo electrónico.
     * @param telefono Número de teléfono.
     * @param fechaNacimiento Fecha de nacimiento.
     * @param rutaFoto Ruta de la foto de perfil.
     * @return true si el registro fue exitoso; false en caso contrario.
     */
    public boolean registrarUsuario(String nombreReal, String nombreUsuario, String password, String confirmarPassword, 
                                    String email, int telefono, LocalDate fechaNacimiento, String rutaFoto) {
        if (password == null || confirmarPassword == null || !password.equals(confirmarPassword)) {
            JOptionPane.showMessageDialog(null, "Las contraseñas no coinciden.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        for (Usuario usuario : usuariosSimulados.values()) {
            if (usuario.getEmail().equals(email)) {
                JOptionPane.showMessageDialog(null, "El correo electrónico ya está registrado.", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            if (usuario.getNumTelefono() == telefono) {
                JOptionPane.showMessageDialog(null, "El número de teléfono ya está registrado.", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
        if (rutaFoto == null || rutaFoto.isEmpty()) {
            rutaFoto = "/umu/tds/app/recursos/grupo.png";
        }
        //Usuario nuevoUsuario = new Usuario(new ImageIcon(rutaFoto), nombreReal, fechaNacimiento, telefono, nombreUsuario, password, email);
        //usuariosSimulados.put(nombreUsuario, nuevoUsuario);
        JOptionPane.showMessageDialog(null, "Usuario registrado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        return true;
    }
    
    public String getNombreUserActual() {
    	return usuarioActual.getName();
    }
    
    public ImageIcon getIconoUserActual() {
    	return usuarioActual.getProfilePhotos();
    }
}
