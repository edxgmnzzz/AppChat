package umu.tds.app.ventanas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import umu.tds.app.AppChat.Controlador;
import umu.tds.app.AppChat.ContactoIndividual;
import umu.tds.app.AppChat.Usuario;

public class VentanaNuevoContacto extends JFrame  {
    private static final long serialVersionUID = 1L;
    private final Controlador controlador; // Referencia al Controlador

    public VentanaNuevoContacto(JFrame parent) {
        controlador = Controlador.getInstancia(); // Obtener la instancia del Controlador
        setTitle("Añadir Contacto");
        setSize(350, 200);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        JLabel nameLabel = new JLabel("Nombre:");
        JTextField nameField = new JTextField();
        JLabel phoneLabel = new JLabel("Teléfono:");
        JTextField phoneField = new JTextField();

        panel.add(nameLabel);
        panel.add(nameField);
        panel.add(phoneLabel);
        panel.add(phoneField);

        JButton acceptButton = new JButton("Aceptar");
        JButton cancelButton = new JButton("Cancelar");

        acceptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nombre = nameField.getText().trim();
                String telefonoStr = phoneField.getText().trim();

                if (nombre.isEmpty() || telefonoStr.isEmpty()) {
                    JOptionPane.showMessageDialog(VentanaNuevoContacto.this, 
                            "El nombre y el teléfono son obligatorios.", "Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                try {
                    int telefono = Integer.parseInt(telefonoStr);
                    // Crear un usuario básico para el contacto (sin datos adicionales por ahora)
                    Usuario usuarioContacto = new Usuario(new ImageIcon(), nombre, null, telefono, 
                            "default", nombre + "@email.com", false, null, null, null);
                    ContactoIndividual nuevoContacto = new ContactoIndividual(nombre, telefono, usuarioContacto);

                    if (controlador.nuevoContacto(nuevoContacto)) {
                        JOptionPane.showMessageDialog(null, "Contacto añadido con éxito", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(VentanaNuevoContacto.this, 
                                "El contacto ya existe o los datos son inválidos.", "Error", JOptionPane.WARNING_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(VentanaNuevoContacto.this, 
                            "El teléfono debe ser un número válido.", "Error", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        cancelButton.addActionListener(e -> dispose());

        setLayout(new BorderLayout());
        add(panel, BorderLayout.CENTER);
        add(acceptButton, BorderLayout.SOUTH);

        setVisible(true);
    }
}