package umu.tds.app.ventanas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import umu.tds.app.AppChat.Controlador;
import umu.tds.app.AppChat.Theme;

public class VentanaLogin extends JFrame {
    private static final long serialVersionUID = 1L;

    private JTextField campoTelefono;
    private JPasswordField campoPassword;
    private Point initialClick;
    private final Controlador controlador;

    public VentanaLogin() {
        controlador = Controlador.getInstancia();
        initializeUI();
    }

    private void initializeUI() {
        configurarVentana();
        crearComponentes();
        addKeyListeners();
    }

    private void configurarVentana() {
        setSize(Theme.LOGIN_WINDOW_WIDTH, Theme.LOGIN_WINDOW_HEIGHT);
        setUndecorated(true);
        setLocationRelativeTo(null);
        setShape(new java.awt.geom.RoundRectangle2D.Double(0, 0, Theme.LOGIN_WINDOW_WIDTH, Theme.LOGIN_WINDOW_HEIGHT,
                Theme.BORDER_RADIUS, Theme.BORDER_RADIUS));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void addKeyListeners() {
        KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
        getRootPane().registerKeyboardAction(e -> iniciarSesion(), enter, JComponent.WHEN_IN_FOCUSED_WINDOW);
        
        KeyStroke escape = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        getRootPane().registerKeyboardAction(e -> System.exit(0), escape, JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    private void crearComponentes() {
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(Theme.COLOR_FONDO);
        panelPrincipal.add(crearBarraTitulo(), BorderLayout.NORTH);
        panelPrincipal.add(crearPanelContenido(), BorderLayout.CENTER);
        add(panelPrincipal);
    }

    private JPanel crearBarraTitulo() {
        JPanel barraTitulo = new JPanel(new BorderLayout());
        barraTitulo.setBackground(Theme.COLOR_PRINCIPAL);
        barraTitulo.setPreferredSize(new Dimension(Theme.LOGIN_WINDOW_WIDTH, Theme.TITLE_BAR_HEIGHT));

        JLabel labelTitulo = new JLabel("  Iniciar Sesión", JLabel.LEFT);
        labelTitulo.setForeground(Theme.COLOR_SECUNDARIO);
        labelTitulo.setFont(Theme.FONT_BOLD_MEDIUM);
        barraTitulo.add(labelTitulo, BorderLayout.WEST);

        JPanel botonesControl = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        botonesControl.setOpaque(false);
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

    private JButton crearBotonControl(String texto, ActionListener accion) {
        JButton boton = new JButton(texto);
        boton.setPreferredSize(new Dimension(45, Theme.TITLE_BAR_HEIGHT));
        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
        boton.setContentAreaFilled(false);
        boton.setForeground(Theme.COLOR_SECUNDARIO);
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

    private JPanel crearPanelContenido() {
        JPanel panelContenido = new JPanel(new GridBagLayout());
        panelContenido.setBackground(Theme.COLOR_FONDO);
        panelContenido.setBorder(BorderFactory.createEmptyBorder(Theme.PADDING_LARGE, Theme.PADDING_LARGE, Theme.PADDING_LARGE, Theme.PADDING_LARGE));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);

        JLabel labelBienvenida = new JLabel("Bienvenido a AppChat", JLabel.CENTER);
        labelBienvenida.setFont(Theme.FONT_BOLD_LARGE);
        labelBienvenida.setForeground(Theme.COLOR_TEXTO);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panelContenido.add(labelBienvenida, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        panelContenido.add(crearLabelCampo("Teléfono:"), gbc);
        
        campoTelefono = crearCampoTexto();
        gbc.gridx = 1;
        panelContenido.add(campoTelefono, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panelContenido.add(crearLabelCampo("Contraseña:"), gbc);
        
        campoPassword = new JPasswordField(15);
        configurarCampo(campoPassword);
        gbc.gridx = 1;
        panelContenido.add(campoPassword, gbc);

        JButton botonLogin = crearBotonAccion("Iniciar Sesión", e -> iniciarSesion());
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        panelContenido.add(botonLogin, gbc);

        JButton botonRegistrar = crearBotonAccion("Registrarse", e -> abrirVentanaRegistro());
        gbc.gridy = 4;
        panelContenido.add(botonRegistrar, gbc);

        return panelContenido;
    }

    private JLabel crearLabelCampo(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(Theme.FONT_BOLD_MEDIUM);
        label.setForeground(Theme.COLOR_TEXTO);
        return label;
    }

    private JTextField crearCampoTexto() {
        JTextField campo = new JTextField(15);
        configurarCampo(campo);
        return campo;
    }

    private void configurarCampo(JTextField campo) {
        campo.setFont(Theme.FONT_PLAIN_MEDIUM);
        campo.setForeground(Theme.COLOR_TEXTO);
        campo.setBackground(Theme.COLOR_SECUNDARIO);
        campo.setBorder(BorderFactory.createLineBorder(Theme.COLOR_PRINCIPAL, 2));
    }

    private JButton crearBotonAccion(String texto, ActionListener accion) {
        JButton boton = new JButton(texto);
        boton.setFont(Theme.FONT_BOLD_MEDIUM);
        boton.setForeground(Color.WHITE);
        boton.setBackground(Theme.COLOR_PRINCIPAL);
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.COLOR_HOVER, 1),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)));
        boton.addActionListener(accion);
        boton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                boton.setBackground(Theme.COLOR_HOVER);
            }
            public void mouseExited(MouseEvent evt) {
                boton.setBackground(Theme.COLOR_PRINCIPAL);
            }
        });
        return boton;
    }

    private void iniciarSesion() {
        String telefono = campoTelefono.getText().trim();
        String password = new String(campoPassword.getPassword());

        if (telefono.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, complete todos los campos", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!telefono.matches("\\d{9,15}")) {
            JOptionPane.showMessageDialog(this, "El teléfono debe ser un número válido", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (controlador.iniciarSesion(telefono, password)) {
            JOptionPane.showMessageDialog(this, "Inicio de sesión exitoso", "Bienvenido", JOptionPane.INFORMATION_MESSAGE);
            abrirVentanaPrincipal();
        } else {
            JOptionPane.showMessageDialog(this, "Teléfono o contraseña incorrectos", "Error", JOptionPane.ERROR_MESSAGE);
            campoPassword.setText("");
        }
    }

    private void abrirVentanaRegistro() {
        this.dispose();
        VentanaRegistro ventanaRegistro = new VentanaRegistro();
        ventanaRegistro.setVisible(true);
    }

    private void abrirVentanaPrincipal() {
        this.dispose();
        VentanaPrincipal ventanaPrincipal = new VentanaPrincipal();
        ventanaPrincipal.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            VentanaLogin ventana = new VentanaLogin();
            ventana.setVisible(true);
        });
    }
}