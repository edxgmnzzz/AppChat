package umu.tds.app.AppChat;

import umu.tds.app.ventanas.VentanaPrincipal;
import umu.tds.app.ventanas.VentanaLogin;

import javax.swing.SwingUtilities;

/**
 * Clase Lanzador que inicia la aplicación AppChat.
 * Actualmente, inicia directamente VentanaPrincipal con un usuario predeterminado para desarrollo.
 * Contiene código comentado para iniciar con VentanaLogin cuando se desee usar autenticación.
 */
public class Lanzador {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Simular inicio de sesión con usuario predeterminado para desarrollo
                Controlador controlador = Controlador.getInstancia();
                boolean loginExitoso = controlador.iniciarSesion("1234567890", "admin");

                if (loginExitoso) {
                    // Iniciar directamente VentanaPrincipal
                    VentanaPrincipal ventana = new VentanaPrincipal();
                    ventana.setVisible(true);
                } else {
                    System.err.println("Error: No se pudo simular el inicio de sesión.");
                    System.exit(1);
                }

                /*
                 * Para iniciar la aplicación con autenticación (login o registro), 
                 * descomentar el siguiente código y comentar el bloque anterior:
                 *
                 * VentanaLogin ventanaLogin = new VentanaLogin();
                 * ventanaLogin.setVisible(true);
                 *
                 * Esto mostrará la ventana de login, permitiendo al usuario iniciar sesión
                 * o registrarse antes de acceder a VentanaPrincipal.
                 */
            } catch (Exception e) {
                System.err.println("Error al iniciar la aplicación: " + e.getMessage());
                e.printStackTrace();
                System.exit(1);
            }
        });
    }
}