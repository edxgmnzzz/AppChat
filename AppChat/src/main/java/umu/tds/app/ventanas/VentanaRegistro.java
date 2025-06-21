package umu.tds.app.ventanas;

import com.toedter.calendar.JDateChooser;
import umu.tds.app.AppChat.Controlador;
import umu.tds.app.AppChat.Theme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * Ventana de registro para nuevos usuarios en la aplicación AppChat.
 * Permite introducir datos como nombre, usuario, contraseña, email, teléfono,
 * fecha de nacimiento, foto y mensaje de saludo.
 * Utiliza {@link Theme} para estilos y {@link Controlador} para lógica.
 */
public class VentanaRegistro extends JFrame {
    private static final long serialVersionUID = 1L;

    private JTextField campoNombreReal, campoNombreUsuario, campoEmail, campoTelefono, campoRutaFoto, campoSaludo;
    private JPasswordField campoPassword, campoConfirmarPassword;
    private JDateChooser campoFechaNacimiento;
    private Point initialClick;
    private final Controlador controlador;

    /**
     * Constructor. Inicializa la ventana y sus componentes.
     */
    public VentanaRegistro() {
        controlador = Controlador.getInstancia();
        initializeUI();
    }

    /**
     * Inicializa la interfaz de usuario.
     */
    private void initializeUI() {
        configurarVentana();
        crearComponentes();
    }

    /**
     * Configura la ventana (tamaño, posición, estilo).
     */
    private void configurarVentana() {
        setSize(500, 700);
        setUndecorated(true);
        setLocationRelativeTo(null);
        setShape(new java.awt.geom.RoundRectangle2D.Double(0, 0, 500, 700, Theme.BORDER_RADIUS, Theme.BORDER_RADIUS));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /**
     * Crea y organiza los componentes visuales de la ventana.
     */
    private void crearComponentes() {
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(Theme.COLOR_FONDO);
        panelPrincipal.add(crearBarraTitulo(), BorderLayout.NORTH);
        panelPrincipal.add(crearPanelFormulario(), BorderLayout.CENTER);
        add(panelPrincipal);
    }

    /**
     * Crea la barra superior de la ventana con controles (minimizar, cerrar).
     */
    private JPanel crearBarraTitulo() {
        JPanel barraTitulo = new JPanel(new BorderLayout());
        barraTitulo.setBackground(Theme.COLOR_HEADER);
        barraTitulo.setPreferredSize(new Dimension(500, Theme.TITLE_BAR_HEIGHT));

        JLabel labelTitulo = new JLabel("  Registro de usuario", JLabel.LEFT);
        labelTitulo.setForeground(Color.WHITE);
        labelTitulo.setFont(Theme.FONT_BOLD_MEDIUM);
        barraTitulo.add(labelTitulo, BorderLayout.WEST);

        JPanel botonesControl = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        botonesControl.setOpaque(false);
        botonesControl.add(crearBotonControl("−", e -> setState(Frame.ICONIFIED)));
        botonesControl.add(crearBotonControl("□", e -> setExtendedState(getExtendedState() == JFrame.MAXIMIZED_BOTH ? JFrame.NORMAL : JFrame.MAXIMIZED_BOTH)));
        botonesControl.add(crearBotonControl("×", e -> System.exit(0)));
        barraTitulo.add(botonesControl, BorderLayout.EAST);

        barraTitulo.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) { initialClick = e.getPoint(); }
        });
        barraTitulo.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                int thisX = getLocation().x;
                int thisY = getLocation().y;
                int xMoved = (thisX + e.getX()) - (thisX + initialClick.x);
                int yMoved = (thisY + e.getY()) - (thisY + initialClick.y);
                setLocation(thisX + xMoved, thisY + yMoved);
            }
        });
        return barraTitulo;
    }

    /**
     * Crea un botón de la barra de título.
     */
    private JButton crearBotonControl(String texto, ActionListener accion) {
        JButton boton = new JButton(texto);
        boton.setPreferredSize(new Dimension(45, Theme.TITLE_BAR_HEIGHT));
        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
        boton.setContentAreaFilled(false);
        boton.setForeground(Color.WHITE);
        boton.setFont(Theme.FONT_BOLD_MEDIUM);
        boton.addActionListener(accion);
        boton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                boton.setBackground(Theme.COLOR_ACENTO);
                boton.setContentAreaFilled(true);
            }
            public void mouseExited(MouseEvent e) {
                boton.setContentAreaFilled(false);
            }
        });
        return boton;
    }

    /**
     * Crea el panel central con el formulario de registro.
     */
    private JPanel crearPanelFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Theme.COLOR_FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        int y = 0;
        gbc.gridx = 0; gbc.gridy = y++; gbc.gridwidth = 2;
        panel.add(crearLabel("Registro de nuevo usuario", Theme.FONT_BOLD_LARGE), gbc);

        gbc.gridwidth = 1;
        agregarCampo(panel, "Nombre Completo:", campoNombreReal = new JTextField(20), gbc, y++);
        agregarCampo(panel, "Usuario:", campoNombreUsuario = new JTextField(20), gbc, y++);
        agregarCampo(panel, "Contraseña:", campoPassword = new JPasswordField(20), gbc, y++);
        agregarCampo(panel, "Confirmar Contraseña:", campoConfirmarPassword = new JPasswordField(20), gbc, y++);
        agregarCampo(panel, "Email:", campoEmail = new JTextField(20), gbc, y++);
        agregarCampo(panel, "Teléfono:", campoTelefono = new JTextField(20), gbc, y++);

        panel.add(crearLabel("Fecha de Nacimiento:"), gbc(0, y));
        campoFechaNacimiento = new JDateChooser();
        campoFechaNacimiento.setFont(Theme.FONT_PLAIN_MEDIUM);
        campoFechaNacimiento.setPreferredSize(new Dimension(200, 25));
        panel.add(campoFechaNacimiento, gbc(1, y++));

        agregarCampo(panel, "URL de Foto:", campoRutaFoto = new JTextField(20), gbc, y++);
        agregarCampo(panel, "Mensaje de Saludo:", campoSaludo = new JTextField(20), gbc, y++);

        gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        JButton botonRegistrar = crearBotonAccion("Registrar", e -> registrarUsuario());
        panel.add(botonRegistrar, gbc);

        return panel;
    }

    private void agregarCampo(JPanel panel, String etiqueta, JTextField campo, GridBagConstraints gbc, int fila) {
        gbc.gridx = 0; gbc.gridy = fila; gbc.gridwidth = 1; gbc.anchor = GridBagConstraints.WEST;
        panel.add(crearLabel(etiqueta), gbc);
        gbc.gridx = 1;
        configurarCampo(campo);
        panel.add(campo, gbc);
    }

    private JLabel crearLabel(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(Theme.FONT_BOLD_MEDIUM);
        label.setForeground(Theme.COLOR_TEXTO);
        return label;
    }

    private JLabel crearLabel(String texto, Font font) {
        JLabel label = new JLabel(texto);
        label.setFont(font);
        label.setForeground(Theme.COLOR_TEXTO);
        return label;
    }

    private void configurarCampo(JTextField campo) {
        campo.setFont(Theme.FONT_PLAIN_MEDIUM);
        campo.setForeground(Theme.COLOR_TEXTO);
        campo.setBackground(Theme.COLOR_SECUNDARIO);
        campo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.COLOR_PRINCIPAL, 1),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
    }

    private JButton crearBotonAccion(String texto, ActionListener accion) {
        JButton boton = new JButton(texto);
        boton.setFont(Theme.FONT_BOLD_MEDIUM);
        boton.setForeground(Color.WHITE);
        boton.setBackground(Theme.COLOR_PRINCIPAL);
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.COLOR_ACENTO, 1),
                BorderFactory.createEmptyBorder(10, 25, 10, 25)));
        boton.addActionListener(accion);
        boton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) { boton.setBackground(Theme.COLOR_HOVER); }
            public void mouseExited(MouseEvent evt) { boton.setBackground(Theme.COLOR_PRINCIPAL); }
        });
        return boton;
    }

    private GridBagConstraints gbc(int x, int y) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        return gbc;
    }

    /**
     * Ejecuta las validaciones y registra al usuario si los datos son válidos.
     */
    private void registrarUsuario() {
        String nombreReal = campoNombreReal.getText().trim();
        String nombreUsuario = campoNombreUsuario.getText().trim();
        String password = new String(campoPassword.getPassword());
        String confirmarPassword = new String(campoConfirmarPassword.getPassword());
        String email = campoEmail.getText().trim();
        String telefono = campoTelefono.getText().trim();
        Date fechaNacimiento = campoFechaNacimiento.getDate();
        String rutaFoto = campoRutaFoto.getText().trim();
        String saludo = campoSaludo.getText().trim();

        if (nombreReal.isEmpty() || nombreUsuario.isEmpty() || password.isEmpty() || email.isEmpty() || telefono.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, complete todos los campos obligatorios", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!telefono.matches("\\d{9,15}")) {
            JOptionPane.showMessageDialog(this, "El teléfono debe ser un número válido", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese un email válido", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        LocalDate localFechaNacimiento = null;
        if (fechaNacimiento != null) {
            localFechaNacimiento = fechaNacimiento.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            if (localFechaNacimiento.isAfter(LocalDate.now())) {
                JOptionPane.showMessageDialog(this, "La fecha de nacimiento no puede ser futura", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        if (controlador.registrarUsuario(nombreReal, nombreUsuario, password, confirmarPassword, email, telefono, rutaFoto, saludo)) {
            JOptionPane.showMessageDialog(this, "Registro exitoso", "Bienvenido", JOptionPane.INFORMATION_MESSAGE);
            this.dispose();
            VentanaLogin ventanaLogin = new VentanaLogin();
            ventanaLogin.setVisible(true);
        }
    }
}
