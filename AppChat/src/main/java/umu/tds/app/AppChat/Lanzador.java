package umu.tds.app.AppChat;

import umu.tds.app.ventanas.VentanaLogin; // Podríamos usar la ventana de Login
import umu.tds.app.ventanas.VentanaPrincipal;
import javax.swing.SwingUtilities;
//Lanzador.java
//Lanzador.java
public class Lanzador {
 public static void main(String[] args) {
     SwingUtilities.invokeLater(() -> {
         try {
             // BORRA LA BASE DE DATOS ANTES DE EJECUTAR
             Controlador controlador = Controlador.getInstancia(); // La simulación se ejecuta aquí dentro si es necesario

             // Iniciar sesión como Laporta para ver el mensaje recibido
             boolean loginExitoso = controlador.iniciarSesion("600333444", "pass2");
             
             if (loginExitoso) {
                 new VentanaPrincipal().setVisible(true);
             } else {
                 System.err.println("Login de prueba fallido.");
             }
         } catch (Exception e) {
             e.printStackTrace();
         }
     });
 }
}