package main.java.umu.tds.app.AppChat;

import java.util.HashMap;
import java.util.Map;

public class Controlador {
    private static Controlador instancia;
    private Map<String, String> usuariosSimulados;
    private String usuarioActual;

    private Controlador() {
        usuariosSimulados = new HashMap<>();
        usuariosSimulados.put("admin", "admin123");
        usuariosSimulados.put("usuario", "password");
    }

    public static Controlador getInstancia() {
        if (instancia == null) {
            instancia = new Controlador();
        }
        return instancia;
    }

    public boolean iniciarSesion(String nombreUsuario, String password) {
        if (usuariosSimulados.containsKey(nombreUsuario) && 
            usuariosSimulados.get(nombreUsuario).equals(password)) {
            usuarioActual = nombreUsuario;
            return true;
        }
        return false;
    }

    public void cerrarSesion() {
        usuarioActual = null;
    }

    public String getUsuarioActual() {
        return usuarioActual;
    }
}