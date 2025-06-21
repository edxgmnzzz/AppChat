package umu.tds.app.ventanas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import umu.tds.app.AppChat.Controlador;
import umu.tds.app.AppChat.Theme;

/**
 * Ventana emergente de ajustes de usuario. Permite editar ciertos campos como
 * el nombre, la contraseña, saludo y la URL de la foto.
 * Muestra otros datos del usuario actual de forma no editable.
 */
public class VentanaAjustes extends JDialog {
    private static final long serialVersionUID = 1L;
    private JTextField nombreField, emailField, telefonoField, saludoField;
    private JPasswordField passwordField;
    private JTextField fotoField;
    private Controlador controlador;

    /**
     * Crea la ventana de ajustes con los datos del usuario actual cargados.
     */
    public VentanaAjustes() {
        controlador = Controlador.getInstancia();
        configurarVentana();
        crearComponentes();
    }

    /**
     * Configura el tamaño, forma y posición de la ventana.
     */
    private void configurarVentana() {
        setSize(400, 500);
        setLocationRelativeTo(getParent());
        setUndecorated(true);
        setShape(new java.awt.geom.RoundRectangle2D.Double(0, 0, 400, 500, Theme.BORDER_RADIUS, Theme.BORDER_RADIUS));
    }

    /**
     * Agrega el contenido principal a la ventana, incluyendo barra de título y formulario.
     */
    private void crearComponentes() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Theme.COLOR_FONDO);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(Theme.PADDING_MEDIUM, Theme.PADDING_MEDIUM, Theme.PADDING_MEDIUM, Theme.PADDING_MEDIUM));

        mainPanel.add(crearBarraTitulo(), BorderLayout.NORTH);
        mainPanel.add(crearPanelContenido(), BorderLayout.CENTER);

        add(mainPanel);
    }

    /**
     * Crea la barra de título con el título "Ajustes" y un botón para cerrar.
     * 
     * @return Panel con la barra de título.
     */
    private JPanel crearBarraTitulo() {
        JPanel barraTitulo = new JPanel(new BorderLayout());
        barraTitulo.setBackground(Theme.COLOR_PRINCIPAL);
        barraTitulo.setPreferredSize(new Dimension(400, Theme.TITLE_BAR_HEIGHT));

        JLabel labelTitulo = new JLabel("  Ajustes");
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

    /**
     * Crea el formulario de ajustes con los campos editables y no editables.
     * 
     * @return Panel con el formulario completo de ajustes.
     */
    private JPanel crearPanelContenido() {
        JPanel panelContenido = new JPanel(new GridBagLayout());
        panelContenido.setBackground(Theme.COLOR_FONDO);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panelContenido.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1;
        nombreField = new JTextField(controlador.getNombreUserActual(), 20);
        panelContenido.add(nombreField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panelContenido.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        emailField = new JTextField(controlador.getUsuarioActual().getEmail(), 20);
        emailField.setEditable(false);
        panelContenido.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panelContenido.add(new JLabel("Teléfono:"), gbc);
        gbc.gridx = 1;
        telefonoField = new JTextField(String.valueOf(controlador.getUsuarioActual().getTelefono()), 20);
        telefonoField.setEditable(false);
        panelContenido.add(telefonoField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panelContenido.add(new JLabel("Contraseña:"), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        panelContenido.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        panelContenido.add(new JLabel("Saludo:"), gbc);
        gbc.gridx = 1;
        saludoField = new JTextField(controlador.getUsuarioActual().getSaludo(), 20);
        panelContenido.add(saludoField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        panelContenido.add(new JLabel("URL Foto:"), gbc);
        gbc.gridx = 1;
        fotoField = new JTextField(controlador.getUsuarioActual().getUrlFoto(), 20);
        panelContenido.add(fotoField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        panelContenido.add(new JLabel("Premium:"), gbc);
        gbc.gridx = 1;
        JTextField premiumField = new JTextField(controlador.isPremiumUserActual() ? "Sí" : "No", 20);
        premiumField.setEditable(false);
        panelContenido.add(premiumField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        JButton guardarButton = createStyledButton("Guardar Cambios", e -> guardarCambios());
        panelContenido.add(guardarButton, gbc);

        return panelContenido;
    }

    /**
     * Crea un botón estilizado con los colores del tema y una acción asociada.
     *
     * @param text Texto del botón.
     * @param action Acción a ejecutar al pulsarlo.
     * @return JButton con estilo personalizado.
     */
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

    /**
     * Intenta guardar los nuevos valores introducidos por el usuario. Si la contraseña
     * está vacía, se conserva la anterior.
     * Muestra un mensaje de confirmación o error.
     */
    private void guardarCambios() {
        String nuevoNombre = nombreField.getText().trim();
        String nuevaPassword = new String(passwordField.getPassword()).trim();
        String nuevoSaludo = saludoField.getText().trim();
        String nuevaFoto = fotoField.getText().trim();

        if (controlador.actualizarUsuario(nuevoNombre,
            nuevaPassword.isEmpty() ? controlador.getUsuarioActual().getPassword() : nuevaPassword,
            nuevoSaludo, nuevaFoto)) {

            JOptionPane.showMessageDialog(this, "Perfil actualizado con éxito", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Error al actualizar el perfil", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
