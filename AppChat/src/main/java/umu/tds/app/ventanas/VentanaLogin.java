package umu.tds.app.ventanas;

import javax.swing.*;
import javax.swing.border.LineBorder;
import umu.tds.app.AppChat.Controlador;

import java.awt.*;
import java.awt.event.*;

public class VentanaLogin extends JFrame {
    private static final long serialVersionUID = 1L;
    private JTextField campoUsuario;
    private JPasswordField campoPassword;
    private int xMouse, yMouse;
    private Controlador controlador;

    private final Color colorFondo = new Color(41, 128, 185);
    private final Color colorPrincipal = new Color(52, 152, 219);
    private final Color colorSecundario = new Color(236, 240, 241);
    private final Color colorAcento = new Color(231, 76, 60);

    public VentanaLogin() {
        controlador = Controlador.getInstancia();
        configurarVentana();
        crearComponentes();
    }

    private void configurarVentana() {
        setSize(400, 350);
        setUndecorated(true);
        setLocationRelativeTo(null);
        setShape(new java.awt.geom.RoundRectangle2D.Double(0, 0, 400, 350, 15, 15));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void crearComponentes() {
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(colorFondo);

        JPanel barraTitulo = crearBarraTitulo();
        panelPrincipal.add(barraTitulo, BorderLayout.NORTH);

        JPanel panelContenido = new JPanel();
        panelContenido.setLayout(new BoxLayout(panelContenido, BoxLayout.Y_AXIS));
        panelContenido.setOpaque(false);
        panelContenido.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel labelBienvenida = crearLabel("Bienvenido a ParabarApp", 18, colorSecundario);
        labelBienvenida.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelContenido.add(labelBienvenida);
        panelContenido.add(Box.createRigidArea(new Dimension(0, 30)));

        JPanel panelCampos = new JPanel(new GridBagLayout());
        panelCampos.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        campoUsuario = crearCampoTexto(colorPrincipal, colorSecundario, colorPrincipal);
        campoPassword = crearCampoPassword(colorPrincipal, colorSecundario, colorPrincipal);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panelCampos.add(crearLabel("Usuario:", 14, colorSecundario), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        panelCampos.add(campoUsuario, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panelCampos.add(crearLabel("Password:", 14, colorSecundario), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        panelCampos.add(campoPassword, gbc);

        panelContenido.add(panelCampos);

        panelContenido.add(Box.createRigidArea(new Dimension(0, 20)));

        JButton botonLogin = crearBoton("Iniciar sesión", e -> iniciarSesion(),
            Color.RED, Color.WHITE, new Color(192, 57, 43));
        botonLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelContenido.add(botonLogin);

        // Botón de registrar
        JButton botonRegistrar = crearBoton("Registrar", e -> abrirVentanaRegistro(),
            colorAcento, Color.WHITE, new Color(231, 76, 60));
        botonRegistrar.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelContenido.add(botonRegistrar);

        panelPrincipal.add(panelContenido, BorderLayout.CENTER);
        add(panelPrincipal);
    }

    
    
    private void abrirVentanaRegistro() {
        SwingUtilities.invokeLater(() -> {
            this.dispose(); // Cerramos la ventana de login
            VentanaRegistro ventanaRegistro = new VentanaRegistro();
            ventanaRegistro.setVisible(true);
        });
    }

	private JPanel crearBarraTitulo() {
        JPanel barraTitulo = new JPanel(new BorderLayout());
        barraTitulo.setBackground(colorPrincipal);
        barraTitulo.setPreferredSize(new Dimension(400, 30));
        barraTitulo.add(new JLabel("  Login", JLabel.LEFT), BorderLayout.WEST);

        JPanel botonesControl = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        botonesControl.setOpaque(false);
        botonesControl.add(crearBotonControl("−", e -> setState(Frame.ICONIFIED)));
        botonesControl.add(crearBotonControl("□", e -> setExtendedState(getExtendedState() == JFrame.MAXIMIZED_BOTH ? JFrame.NORMAL : JFrame.MAXIMIZED_BOTH)));
        botonesControl.add(crearBotonControl("×", e -> System.exit(0)));
        barraTitulo.add(botonesControl, BorderLayout.EAST);

        barraTitulo.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) { xMouse = e.getX(); yMouse = e.getY(); }
        });
        barraTitulo.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                setLocation(e.getXOnScreen() - xMouse, e.getYOnScreen() - yMouse);
            }
        });

        return barraTitulo;
    }

    private JTextField crearCampoTexto(Color colorTexto, Color colorFondo, Color colorBorde) {
        JTextField campo = new JTextField(15);
        campo.setFont(new Font("Arial", Font.PLAIN, 14));
        campo.setForeground(colorTexto);
        campo.setBackground(colorFondo);
        campo.setBorder(new LineBorder(colorBorde, 2, true));
        return campo;
    }

    private JPasswordField crearCampoPassword(Color colorTexto, Color colorFondo, Color colorBorde) {
        JPasswordField campo = new JPasswordField(15);
        campo.setFont(new Font("Arial", Font.PLAIN, 14));
        campo.setForeground(colorTexto);
        campo.setBackground(colorFondo);
        campo.setBorder(new LineBorder(colorBorde, 2, true));
        return campo;
    }

    private JButton crearBotonControl(String texto, ActionListener accion) {
        JButton boton = new JButton(texto);
        boton.setPreferredSize(new Dimension(45, 30));
        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
        boton.setContentAreaFilled(false);
        boton.setForeground(colorSecundario);
        boton.setFont(new Font("Arial", Font.BOLD, 14));
        boton.addActionListener(accion);
        boton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { boton.setBackground(colorAcento); boton.setContentAreaFilled(true); }
            public void mouseExited(MouseEvent e) { boton.setContentAreaFilled(false); }
        });
        return boton;
    }

    private JButton crearBoton(String texto, ActionListener accion, Color colorFondo, Color colorTexto, Color colorHover) {
        JButton boton = new JButton(texto) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getModel().isArmed() ? colorHover.darker() : colorHover);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                super.paintComponent(g2d);
                g2d.dispose();
            }
        };
        boton.setForeground(colorTexto);
        boton.setFont(new Font("Arial", Font.BOLD, 14));
        boton.setContentAreaFilled(false);
        boton.setBorderPainted(false);
        boton.setFocusPainted(false);
        boton.addActionListener(accion);
        
        Dimension buttonSize = new Dimension(200, 40);
        boton.setPreferredSize(buttonSize);
        boton.setMaximumSize(buttonSize);
        boton.setMinimumSize(buttonSize);
        
        return boton;
    }

    private JLabel crearLabel(String texto, int size, Color color) {
        JLabel label = new JLabel(texto, JLabel.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, size));
        label.setForeground(color);
        return label;
    }

    private void iniciarSesion() {
        String nombreUsuario = campoUsuario.getText();
        String Password = new String(campoPassword.getPassword());
        
        if (controlador.iniciarSesion(nombreUsuario, Password)) {
            JOptionPane.showMessageDialog(this, "Inicio de sesión exitoso", "Bienvenido", JOptionPane.INFORMATION_MESSAGE);
            abrirVentanaPrincipal();
        } else {
            JOptionPane.showMessageDialog(this, "Usuario o Password incorrectos", "Error de login", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirVentanaPrincipal() {
        SwingUtilities.invokeLater(() -> {
            this.dispose(); // Cerramos la ventana de login
            VentanaPrincipal ventanaPrincipal = new VentanaPrincipal();
            ventanaPrincipal.setVisible(true);
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            VentanaLogin ventana = new VentanaLogin();
            ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            ventana.setVisible(true);
        });
    }
}
