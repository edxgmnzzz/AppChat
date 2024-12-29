package umu.tds.app.AppChat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

public class Controlador {
    private static final Controlador instancia = new Controlador();
    private Map<String, Usuario> usuariosSimulados;  // Map para almacenar los usuarios simulados
    private List<ContactoIndividual> contactos;      // Lista de contactos individuales
    private Map<String, List<String>> mensajes;      // Historial de mensajes
    private Usuario usuarioActual;                   // Usuario actual

    private Controlador() {
        // Inicializamos usuarios simulados
        usuariosSimulados = new HashMap<>();
        usuariosSimulados.put("admin", new Usuario(new ImageIcon(), "Admin", LocalDate.of(1990, 1, 1), 1234567890, "admin", "admin123", "admin@gmail.com"));
        usuariosSimulados.put("usuario", new Usuario(new ImageIcon(), "Usuario", LocalDate.of(2000, 1, 1), 987654321, "usuario", "password", "user@gmail.com"));

        // Inicializamos contactos (simulados)
        contactos = new ArrayList<>();
        contactos.add(new ContactoIndividual("Oscar", 123456789, new Usuario(new ImageIcon(), 
        		"Oscar", LocalDate.of(1990, 1, 1), 123456789, "oscar", "12345", "oscar@gmail.com")));
        contactos.add(new ContactoIndividual("Javi", 234567890, new Usuario(new ImageIcon(), "Javi", 
        		LocalDate.of(1995, 1, 1), 234567890, "javi", "password","javi@gmail.com")));
        contactos.add(new ContactoIndividual("Lucia", 345678901, new Usuario(new ImageIcon(), 
        		"Lucia", LocalDate.of(2000, 1, 1), 345678901, "lucia", "12345", "lucia@gmail.com")));
        contactos.add(new ContactoIndividual("Gloria", 456789012, new Usuario(new ImageIcon(), 
        		"Gloria", LocalDate.of(1985, 1, 1), 456789012, "gloria", "password", "gloria@gmail.com")));

        // Inicializamos los mensajes (vacíos inicialmente)
        mensajes = new HashMap<>();
    }

    public static Controlador getInstancia() {
        return instancia;
    }

    // Método de inicio de sesión
    public boolean iniciarSesion(String nombreUsuario, String password) {
        if (nombreUsuario == null || password == null) {
            return false;
        }
        if (usuariosSimulados.containsKey(nombreUsuario) && 
            usuariosSimulados.get(nombreUsuario).getPassword().equals(password)) {
            usuarioActual = usuariosSimulados.get(nombreUsuario);
            return true;
        }
        return false;
    }

    // Método de cierre de sesión
    public void cerrarSesion() {
        usuarioActual = null;
    }

    // Obtener usuario actual
    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    // Obtener lista de contactos
    public List<ContactoIndividual> obtenerContactos() {
        return contactos;
    }

    // Enviar mensaje (agregar al historial de mensajes)
    public void enviarMensaje(ContactoIndividual contacto, String mensaje) {
        if (usuarioActual == null || contacto == null || mensaje == null || mensaje.isEmpty()) {
            return;
        }
        
        // Agregar mensaje al historial
        String clave = generarClaveConversacion(contacto);
        if (!mensajes.containsKey(clave)) {
            mensajes.put(clave, new ArrayList<>());
        }
        mensajes.get(clave).add("Tú: " + mensaje);
    }

    // Obtener historial de mensajes con un contacto
    public List<String> obtenerMensajes(ContactoIndividual contacto) {
        String clave = generarClaveConversacion(contacto);
        return mensajes.getOrDefault(clave, new ArrayList<>());
    }

    // Generar clave única para las conversaciones (usuario + contacto)
    private String generarClaveConversacion(ContactoIndividual contacto) {
        return usuarioActual.getNick() + "-" + contacto.getUsuario().getNick(); // Usar getUsuario() en lugar de nick
    }

    // Simular respuesta automática del contacto
    public String obtenerRespuesta(ContactoIndividual contacto) {
        return "Gracias por tu mensaje, " + contacto.getUsuario().getNick() + ". ¡Hablamos pronto!";
    }

    // Método para añadir un nuevo contacto
    public void agregarContacto(ContactoIndividual contacto) {
        contactos.add(contacto);
    }

    // Método para eliminar un contacto
    public void eliminarContacto(ContactoIndividual contacto) {
        contactos.remove(contacto);
    }
    public boolean registrarUsuario(String nombreReal, String nombreUsuario, String password, String confirmarPassword, 
    		String email, int telefono, LocalDate fechaNacimiento, String rutaFoto) {
    	// Validar si la contraseña y la confirmación son iguales
    	if (password == null || confirmarPassword == null || !password.equals(confirmarPassword)) {
    		JOptionPane.showMessageDialog(null, "Las contraseñas no coinciden.", "Error", JOptionPane.ERROR_MESSAGE);
    		return false;
    	}

    	// Verificar si ya existe un usuario con el mismo correo o teléfono
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

    	// Si no se proporcionó una foto, asignamos una por defecto
    	if (rutaFoto == null || rutaFoto.isEmpty()) {
    		rutaFoto = "/umu/tds/app/recursos/grupo.png";  // Foto por defecto
    	}

    	// Crear un nuevo usuario con la información proporcionada
    	Usuario nuevoUsuario = new Usuario(
    		    new ImageIcon(rutaFoto),  // Foto de perfil del usuario
    		    nombreReal,               // Nombre completo
    		    fechaNacimiento,          // Fecha de nacimiento
    		    telefono,                 // Número de teléfono
    		    nombreUsuario,            // Nombre de usuario
    		    password,                 // Contraseña
    		    email                     // Correo electrónico
    		);
    	// Agregar el nuevo usuario al mapa de usuarios simulados
    	usuariosSimulados.put(nombreUsuario, nuevoUsuario);

    	JOptionPane.showMessageDialog(null, "Usuario registrado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
    	return true;
}

}
