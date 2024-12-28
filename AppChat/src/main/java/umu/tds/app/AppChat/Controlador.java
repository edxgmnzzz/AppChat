package main.java.umu.tds.app.AppChat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Controlador {
    private static final Controlador instancia = new Controlador();
    private Map<String, String> usuariosSimulados;
    private List<String> contactos;
    private Map<String, List<String>> mensajes; // Map para almacenar los mensajes por contacto
    private String usuarioActual;

    private Controlador() {
        // Inicializamos usuarios simulados
        usuariosSimulados = new HashMap<>();
        usuariosSimulados.put("admin", "admin123");
        usuariosSimulados.put("usuario", "password");

        // Inicializamos contactos
        contactos = new ArrayList<>();
        contactos.add("Oscar");
        contactos.add("Javi");
        contactos.add("Lucia");
        contactos.add("Gloria");

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
            usuariosSimulados.get(nombreUsuario).equals(password)) {
            usuarioActual = nombreUsuario;
            return true;
        }
        return false;
    }

    // Método de cierre de sesión
    public void cerrarSesion() {
        usuarioActual = null;
    }

    // Obtener usuario actual
    public String getUsuarioActual() {
        return usuarioActual;
    }

    // Obtener lista de contactos
    public List<String> obtenerContactos() {
        return contactos;
    }

    // Enviar mensaje (agregar al historial de mensajes)
    public void enviarMensaje(String contacto, String mensaje) {
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
    public List<String> obtenerMensajes(String contacto) {
        String clave = generarClaveConversacion(contacto);
        return mensajes.getOrDefault(clave, new ArrayList<>());
    }

    // Generar clave única para las conversaciones (usuario + contacto)
    private String generarClaveConversacion(String contacto) {
        return usuarioActual + "-" + contacto;
    }

    // Simular respuesta automática del contacto
    public String obtenerRespuesta(String contacto) {
        return "Gracias por tu mensaje, " + contacto + ". ¡Hablamos pronto!";
    }
}
