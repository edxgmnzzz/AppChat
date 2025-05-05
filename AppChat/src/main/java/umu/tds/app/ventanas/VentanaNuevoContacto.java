package umu.tds.app.ventanas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import umu.tds.app.AppChat.ContactoIndividual;
import umu.tds.app.AppChat.Controlador;
import umu.tds.app.AppChat.Theme;

public class VentanaNuevoContacto extends JDialog {
    private static final long serialVersionUID = 1L;
    private JTextField nombreField;
    private JTextField telefonoField;
    private Controlador controlador;

    public VentanaNuevoContacto(JDialog parent) {
        super(parent, "Añadir Contacto", ModalityType.APPLICATION_MODAL);
        controlador = Controlador.getInstancia();
        configurarVentana();
        crearComponentes();
    }

    private void configurarVentana() {
        setSize(300, 200);
        setLocationRelativeTo(getParent());
        setUndecorated(true);
        setShape(new java.awt.geom.RoundRectangle2D.Double(0, 0, 300, 200, Theme.BORDER_RADIUS, Theme.BORDER_RADIUS));
    }

    private void crearComponentes() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Theme.COLOR_FONDO);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(Theme.PADDING_MEDIUM, Theme.PADDING_MEDIUM, Theme.PADDING_MEDIUM, Theme.PADDING_MEDIUM));

        mainPanel.add(crearBarraTitulo(), BorderLayout.NORTH);
        mainPanel.add(crearPanelContenido(), BorderLayout.CENTER);

        add(mainPanel);
    }

    private JPanel crearBarraTitulo() {
        JPanel barraTitulo = new JPanel(new BorderLayout());
        barraTitulo.setBackground(Theme.COLOR_PRINCIPAL);
        barraTitulo.setPreferredSize(new Dimension(300, Theme.TITLE_BAR_HEIGHT));

        JLabel labelTitulo = new JLabel("  Añadir Contacto");
        labelTitulo.setForeground(Theme.COLOR_SECUNDARIO);
        labelTitulo.setFont(Theme.FONT_BOLD_MEDIUM);
        barraTitulo.add(labelTitulo, BorderLayout.WEST);

        JButton cerrarBoton = new JButton("×");
        cerrarBoton.setPreferredSize(new Dimension(45, Theme.TITLE_BAR_HEIGHT));
        cerrarBoton.setFocusPainted(false);
        cerrarBoton.setBorderPainted(false);
        cerrarBoton.setContentAreaFilled(false);
        cerrarBoton.setForeground(Theme.COLOR_SECUNDARIO);
        cerrarBoton.setFont(Theme.FONT_BOLD_MEDIUM);
        cerrarBoton.addActionListener(e -> dispose());
        cerrarBoton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                cerrarBoton.setBackground(Theme.COLOR_ACENTO);
                cerrarBoton.setContentAreaFilled(true);
            }
            public void mouseExited(MouseEvent e) {
                cerrarBoton.setContentAreaFilled(false);
            }
        });

        JPanel botonesControl = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        botonesControl.setOpaque(false);
        botonesControl.add(cerrarBoton);
        barraTitulo.add(botonesControl, BorderLayout.EAST);

        return barraTitulo;
    }

    private JPanel crearPanelContenido() {
        JPanel panelContenido = new JPanel(new GridBagLayout());
        panelContenido.setBackground(Theme.COLOR_FONDO);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel nombreLabel = new JLabel("Nombre:");
        nombreLabel.setFont(Theme.FONT_BOLD_MEDIUM);
        nombreLabel.setForeground(Theme.COLOR_SECUNDARIO);
        gbc.gridx = 0; gbc.gridy = 0;
        panelContenido.add(nombreLabel, gbc);

        nombreField = new JTextField(15);
        nombreField.setFont(Theme.FONT_PLAIN_MEDIUM);
        nombreField.setForeground(Theme.COLOR_PRINCIPAL);
        nombreField.setBackground(Theme.COLOR_SECUNDARIO);
        nombreField.setBorder(BorderFactory.createLineBorder(Theme.COLOR_PRINCIPAL, 2));
        gbc.gridx = 1; gbc.gridy = 0;
        panelContenido.add(nombreField, gbc);

        JLabel telefonoLabel = new JLabel("Teléfono:");
        telefonoLabel.setFont(Theme.FONT_BOLD_MEDIUM);
        telefonoLabel.setForeground(Theme.COLOR_SECUNDARIO);
        gbc.gridx = 0; gbc.gridy = 1;
        panelContenido.add(telefonoLabel, gbc);

        telefonoField = new JTextField(15);
        telefonoField.setFont(Theme.FONT_PLAIN_MEDIUM);
        telefonoField.setForeground(Theme.COLOR_PRINCIPAL);
        telefonoField.setBackground(Theme.COLOR_SECUNDARIO);
        telefonoField.setBorder(BorderFactory.createLineBorder(Theme.COLOR_PRINCIPAL, 2));
        gbc.gridx = 1; gbc.gridy = 1;
        panelContenido.add(telefonoField, gbc);

        JPanel botonesPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botonesPanel.setBackground(Theme.COLOR_FONDO);
        JButton aceptarBoton = createStyledButton("Aceptar", e -> agregarContacto());
        JButton cancelarBoton = createStyledButton("Cancelar", e -> dispose());
        botonesPanel.add(aceptarBoton);
        botonesPanel.add(cancelarBoton);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        panelContenido.add(botonesPanel, gbc);

        return panelContenido;
    }

    private JButton createStyledButton(String text, ActionListener action) {
        JButton button = new JButton(text);
        button.setBackground(Theme.COLOR_PRINCIPAL);
        button.setForeground(Theme.COLOR_TEXTO);
        button.setFont(Theme.FONT_BOLD_MEDIUM);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.COLOR_ACENTO, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        button.addActionListener(action);
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(Theme.COLOR_HOVER);
            }
            public void mouseExited(MouseEvent evt) {
                button.setBackground(Theme.COLOR_PRINCIPAL);
            }
        });
        return button;
    }

    private void agregarContacto() {
        String nombre = nombreField.getText().trim();
        String telefono = telefonoField.getText().trim();
        if (nombre.isEmpty() || !telefono.matches("\\d{9,15}")) {
            JOptionPane.showMessageDialog(this, "Nombre o teléfono inválido", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // CAMBIAR
        ContactoIndividual contacto = new ContactoIndividual(nombre, controlador.generarCodigoContacto(), Integer.parseInt(telefono), null);
        if (controlador.nuevoContacto(contacto)) {
            JOptionPane.showMessageDialog(this, "Contacto agregado", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "El contacto ya existe", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}