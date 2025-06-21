package umu.tds.app.ventanas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import umu.tds.app.AppChat.Controlador;
import umu.tds.app.AppChat.Theme;

/**
 * Ventana principal de inicio de sesión para la aplicación AppChat.
 * Permite a los usuarios autenticarse o acceder al registro.
 * Implementa un diseño sin bordes con desplazamiento por la barra superior.
 */
public class VentanaLogin extends JFrame {
    private static final long serialVersionUID = 1L;

    /** Campo para introducir el número de teléfono del usuario */
    private JTextField campoTelefono;

    /** Campo para introducir la contraseña del usuario */
    private JPasswordField campoPassword;

    /** Punto inicial usado para permitir el arrastre de la ventana */
    private Point initialClick;

    /** Instancia del controlador que gestiona la lógica de negocio */
    private final Controlador controlador;

    /**
     * Constructor por defecto. Inicializa la ventana de login.
     */
    public VentanaLogin() {
        controlador = Controlador.getInstancia();
        initializeUI();
    }

    /**
     * Inicializa la interfaz gráfica: configuración general, componentes y listeners.
     */
    private void initializeUI() {
        configurarVentana();
        crearComponentes();
        addKeyListeners();
    }

    /**
     * Configura las propiedades básicas de la ventana.
     */
    private void configurarVentana() {
        setSize(Theme.LOGIN_WINDOW_WIDTH, Theme.LOGIN_WINDOW_HEIGHT);
        setUndecorated(true);
        setLocationRelativeTo(null);
        setShape(new java.awt.geom.RoundRectangle2D.Double(
            0, 0, Theme.LOGIN_WINDOW_WIDTH, Theme.LOGIN_WINDOW_HEIGHT, Theme.BORDER_RADIUS, Theme.BORDER_RADIUS));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /**
     * Registra teclas rápidas (ENTER para login, ESC para cerrar).
     */
    private void addKeyListeners() {
        KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
        getRootPane().registerKeyboardAction(e -> iniciarSesion(), enter, JComponent.WHEN_IN_FOCUSED_WINDOW);

        KeyStroke escape = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        getRootPane().registerKeyboardAction(e -> System.exit(0), escape, JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    /**
     * Crea y organiza todos los paneles y componentes visuales.
     */
    private void crearComponentes() {
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(Theme.COLOR_FONDO);
        panelPrincipal.add(crearBarraTitulo(), BorderLayout.NORTH);
        panelPrincipal.add(crearPanelContenido(), BorderLayout.CENTER);
        add(panelPrincipal);
    }

    /**
     * Crea la barra superior con el título y botón de cerrar, además de permitir arrastrar la ventana.
     * @return Panel configurado como barra de título
     */
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

        // Soporte para mover la ventana
        barraTitulo.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                initialClick = e.getPoint();
            }
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
     * Crea un botón de control para la barra superior.
     * @param texto símbolo del botón (ej. ×)
     * @param accion acción a ejecutar al pulsar
     * @return botón configurado
     */
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

    /**
     * Crea el contenido principal de la ventana: campos de login y botones.
     * @return panel con los campos de entrada
     */
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

    /**
     * Crea una etiqueta estilizada para un campo de entrada.
     * @param texto texto descriptivo del campo
     * @return componente JLabel estilizado
     */
    private JLabel crearLabelCampo(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(Theme.FONT_BOLD_MEDIUM);
        label.setForeground(Theme.COLOR_TEXTO);
        return label;
    }

    /**
     * Crea un campo de texto y aplica su estilo.
     * @return JTextField configurado
     */
    private JTextField crearCampoTexto() {
        JTextField campo = new JTextField(15);
        configurarCampo(campo);
        return campo;
    }

    /**
     * Aplica estilo común a campos de texto.
     * @param campo el campo de texto al que se le aplicará el estilo
     */
    private void configurarCampo(JTextField campo) {
        campo.setFont(Theme.FONT_PLAIN_MEDIUM);
        campo.setForeground(Theme.COLOR_TEXTO);
        campo.setBackground(Theme.COLOR_SECUNDARIO);
        campo.setBorder(BorderFactory.createLineBorder(Theme.COLOR_PRINCIPAL, 2));
    }

    /**
     * Crea un botón estilizado para acciones como login o registro.
     * @param texto texto del botón
     * @param accion acción a ejecutar al pulsarlo
     * @return JButton estilizado
     */
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

    /**
     * Intenta iniciar sesión validando el número de teléfono y la contraseña.
     * Muestra mensajes de error o éxito según el resultado.
     */
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

    /**
     * Abre la ventana de registro al pulsar el botón correspondiente.
     */
    private void abrirVentanaRegistro() {
        this.dispose();
        new VentanaRegistro().setVisible(true);
    }

    /**
     * Abre la ventana principal tras un inicio de sesión exitoso.
     */
    private void abrirVentanaPrincipal() {
        this.dispose();
        VentanaPrincipal ventanaPrincipal = new VentanaPrincipal();
        controlador.notifyObservers();
        ventanaPrincipal.setVisible(true);
    }

    /**
     * Método principal para lanzar la aplicación y mostrar la ventana de login.
     * @param args argumentos de línea de comandos (no utilizados)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            VentanaLogin ventana = new VentanaLogin();
            ventana.setVisible(true);
        });
    }
}
