package umu.tds.app.AppChat;

import umu.tds.app.ventanas.VentanaPrincipal;
import javax.swing.SwingUtilities;

public class Lanzador {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                Controlador controlador = Controlador.getInstancia();
                String telefono = "1234567890";
                String password = "admin";

                // Registrar solo si no existe
                if (!controlador.existeUsuario(telefono)) {
                    System.out.println("Usuario no encontrado. Registrando nuevo usuario...");
                    boolean registrado = controlador.registrarUsuario(
                        "Florentino Pérez",   // nombreReal
                        "florentino",         // nombreUsuario
                        password,
                        password,
                        "admin@gmail.com",
                        telefono,
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/3/36/Florentino_perez.jpg/220px-Florentino_perez.jpg",
                        "Hala Madrid"
                    );

                    if (!registrado) {
                        System.err.println("Error: No se pudo registrar el usuario por defecto.");
                        System.exit(1);
                    }
                }

                // Iniciar sesión
                boolean loginExitoso = controlador.iniciarSesion(telefono, password);
                if (loginExitoso) {
                    VentanaPrincipal ventana = new VentanaPrincipal();
                    ventana.setVisible(true);
                } else {
                    System.err.println("Error: No se pudo iniciar sesión.");
                    System.exit(1);
                }

            } catch (Exception e) {
                System.err.println("Error al iniciar la aplicación: " + e.getMessage());
                e.printStackTrace();
                System.exit(1);
            }
        });
    }
}
