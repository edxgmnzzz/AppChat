package umu.tds.app.ventanas;

import javax.swing.*;
import javax.swing.border.LineBorder;
import umu.tds.app.AppChat.Controlador;

import java.awt.*;
import java.awt.event.*;

public class VentanaLogin extends JFrame {
    private static final long serialVersionUID = 1L;
    
    // Constantes de la interfaz
    private static final int WINDOW_WIDTH = 400;
    private static final int WINDOW_HEIGHT = 350;
    private static final int BORDER_RADIUS = 15;
    private static final int BUTTON_WIDTH = 200;
    private static final int BUTTON_HEIGHT = 40;
    
    // Colores de la aplicación
    private static final Color COLOR_FONDO = new Color(41, 128, 185);
    private static final Color COLOR_PRINCIPAL = new Color(52, 152, 219);
    private static final Color COLOR_SECUNDARIO = new Color(236, 240, 241);
    private static final Color COLOR_ACENTO = new Color(231, 76, 60);
    
    // Componentes de la interfaz
    private JTextField campoUsuario;
    private JPasswordField campoPassword;
    private Point initialClick;
    private final Controlador controlador;

    // Constructor 
    public VentanaLogin() {
        controlador = Controlador.getInstancia();
        initializeUI();
    }

    // Inicializador de los componentes
    private void initializeUI() {
    	abrirVentanaPrincipal();
        //configurarVentana();
        //crearComponentes();
        //addKeyListeners();
    }

    // Diseño de la ventana
    private void configurarVentana() {
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setUndecorated(true);
        setLocationRelativeTo(null);
        setShape(new java.awt.geom.RoundRectangle2D.Double(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT, BORDER_RADIUS, BORDER_RADIUS));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
    }

    //Para iniciar sesión con enter o salir con ESC
    private void addKeyListeners() {
        KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
        getRootPane().registerKeyboardAction(
            e -> iniciarSesion(),
            enter,
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
        
        KeyStroke escape = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        getRootPane().registerKeyboardAction(
            e -> System.exit(0),
            escape,
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
    }

    private void crearComponentes() {
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(COLOR_FONDO);

        panelPrincipal.add(crearBarraTitulo(), BorderLayout.NORTH);
        panelPrincipal.add(crearPanelContenido(), BorderLayout.CENTER);

        add(panelPrincipal);
    }

    private JPanel crearBarraTitulo() {
        JPanel barraTitulo = new JPanel(new BorderLayout());
        barraTitulo.setBackground(COLOR_PRINCIPAL);
        barraTitulo.setPreferredSize(new Dimension(WINDOW_WIDTH, 30));
        barraTitulo.add(new JLabel("  Login", JLabel.LEFT), BorderLayout.WEST);

        JPanel botonesControl = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        botonesControl.setOpaque(false);
        botonesControl.add(crearBotonControl("−", e -> setState(Frame.ICONIFIED)));
        botonesControl.add(crearBotonControl("□", e -> setExtendedState(getExtendedState() == JFrame.MAXIMIZED_BOTH ? JFrame.NORMAL : JFrame.MAXIMIZED_BOTH)));
        botonesControl.add(crearBotonControl("×", e -> System.exit(0)));
        barraTitulo.add(botonesControl, BorderLayout.EAST);

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

    private JButton crearBotonControl(String texto, ActionListener accion) {
        JButton boton = new JButton(texto);
        boton.setPreferredSize(new Dimension(45, 30));
        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
        boton.setContentAreaFilled(false);
        boton.setForeground(COLOR_SECUNDARIO);
        boton.setFont(new Font("Arial", Font.BOLD, 14));
        boton.addActionListener(accion);
        boton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                boton.setBackground(COLOR_ACENTO);
                boton.setContentAreaFilled(true);
            }
            public void mouseExited(MouseEvent e) {
                boton.setContentAreaFilled(false);
            }
        });
        return boton;
    }

    private JPanel crearPanelContenido() {
        JPanel panelContenido = new JPanel();
        panelContenido.setLayout(new BoxLayout(panelContenido, BoxLayout.Y_AXIS));
        panelContenido.setOpaque(false);
        panelContenido.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel labelBienvenida = crearLabel("Bienvenido a ParabarApp", 18, COLOR_SECUNDARIO);
        labelBienvenida.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelContenido.add(labelBienvenida);
        panelContenido.add(Box.createRigidArea(new Dimension(0, 30)));

        panelContenido.add(crearPanelCampos());
        panelContenido.add(Box.createRigidArea(new Dimension(0, 20)));
        panelContenido.add(crearPanelBotones());

        return panelContenido;
    }

    private JPanel crearPanelCampos() {
        JPanel panelCampos = new JPanel(new GridBagLayout());
        panelCampos.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        campoUsuario = crearCampoTexto("Ingrese su usuario");
        campoPassword = crearCampoPassword("Contraseña");

        gbc.gridx = 0; gbc.gridy = 0;
        panelCampos.add(crearLabel("Usuario:", 14, COLOR_SECUNDARIO), gbc);
        gbc.gridx = 1;
        panelCampos.add(campoUsuario, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panelCampos.add(crearLabel("Password:", 14, COLOR_SECUNDARIO), gbc);
        gbc.gridx = 1;
        panelCampos.add(campoPassword, gbc);

        return panelCampos;
    }

    private JPanel crearPanelBotones() {
        JPanel panelBotones = new JPanel();
        panelBotones.setLayout(new BoxLayout(panelBotones, BoxLayout.Y_AXIS));
        panelBotones.setOpaque(false);

        JButton botonLogin = crearBoton("Iniciar sesión", e -> iniciarSesion(),
            Color.RED, Color.WHITE, new Color(192, 57, 43));
        JButton botonRegistrar = crearBoton("Registrar", e -> abrirVentanaRegistro(),
            COLOR_ACENTO, Color.WHITE, new Color(231, 76, 60));

        botonLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        botonRegistrar.setAlignmentX(Component.CENTER_ALIGNMENT);

        panelBotones.add(botonLogin);
        panelBotones.add(Box.createRigidArea(new Dimension(0, 10)));
        panelBotones.add(botonRegistrar);

        return panelBotones;
    }

    private JTextField crearCampoTexto(String placeholder) {
        JTextField campo = new JTextField(15) {
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !hasFocus()) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setColor(Color.GRAY);
                    g2d.drawString(placeholder, getInsets().left, g.getFontMetrics().getMaxAscent() + getInsets().top);
                }
            }
        };
        configurarCampoTexto(campo);
        return campo;
    }

  
    private JPasswordField crearCampoPassword(String placeholder) {
        JPasswordField campo = new JPasswordField(15) {
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getPassword().length == 0 && !hasFocus()) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setColor(Color.GRAY);
                    g2d.drawString(placeholder, getInsets().left, g.getFontMetrics().getMaxAscent() + getInsets().top);
                }
            }
        };
        configurarCampoTexto(campo);
        return campo;
    }

    // Para modificar fácilmente el texto
    private void configurarCampoTexto(JTextField campo) {
        campo.setFont(new Font("Arial", Font.PLAIN, 14));
        campo.setForeground(COLOR_PRINCIPAL);
        campo.setBackground(COLOR_SECUNDARIO);
        campo.setBorder(new LineBorder(COLOR_PRINCIPAL, 2, true));
        
        campo.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                campo.setBorder(new LineBorder(COLOR_ACENTO, 2, true));
            }
            @Override
            public void focusLost(FocusEvent e) {
                campo.setBorder(new LineBorder(COLOR_PRINCIPAL, 2, true));
            }
        });
    }

    private JLabel crearLabel(String texto, int size, Color color) {
        JLabel label = new JLabel(texto, JLabel.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, size));
        label.setForeground(color);
        return label;
    }

    private JButton crearBoton(String texto, ActionListener accion, Color colorFondo, Color colorTexto, Color colorHover) {
        JButton boton = new JButton(texto) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void paintComponent(Graphics g) {
                if (!isEnabled()) {
                    return;
                }
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                Color baseColor = getModel().isPressed() ? colorHover.darker() : 
                                getModel().isRollover() ? colorHover : 
                                colorFondo;
                
                g2d.setColor(baseColor);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                if (getModel().isRollover() && !getModel().isPressed()) {
                    g2d.setColor(new Color(255, 255, 255, 50));
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight()/2, 10, 10);
                }
                
                FontMetrics fm = g2d.getFontMetrics();
                Rectangle textRect = fm.getStringBounds(texto, g2d).getBounds();
                int x = (getWidth() - textRect.width) / 2;
                int y = (getHeight() - textRect.height) / 2 + fm.getAscent();
                
                g2d.setColor(colorTexto);
                g2d.setFont(getFont());
                g2d.drawString(texto, x, y);
                
                g2d.dispose();
            }
        };
        
        boton.setForeground(colorTexto);
        boton.setFont(new Font("Arial", Font.BOLD, 14));
        boton.setContentAreaFilled(false);
        boton.setBorderPainted(false);
        boton.setFocusPainted(false);
        boton.addActionListener(accion);
        
        Dimension buttonSize = new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT);
        boton.setPreferredSize(buttonSize);
        boton.setMaximumSize(buttonSize);
        boton.setMinimumSize(buttonSize);
        
        boton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                boton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
        
        return boton;
    }

 
    private void iniciarSesion() {
        String nombreUsuario = campoUsuario.getText().trim();
        String password = new String(campoPassword.getPassword());
        
        if (nombreUsuario.isEmpty() || password.isEmpty()) {
            mostrarError("Por favor, complete todos los campos");
            return;
        }
        
        if (controlador.iniciarSesion(nombreUsuario, password)) {
            mostrarMensaje("Inicio de sesión exitoso", "Bienvenido");
            abrirVentanaPrincipal();
        } else {
            mostrarError("Usuario o contraseña incorrectos");
            campoPassword.setText("");
        }
    }
    
    private void mostrarError(String mensaje) {
        // Crear un panel personalizado para el mensaje de error
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        
        // Crear icono de error personalizado
        JLabel iconoLabel = new JLabel(UIManager.getIcon("OptionPane.errorIcon"));
        panel.add(iconoLabel, BorderLayout.WEST);
        
        // Crear label para el mensaje
        JLabel mensajeLabel = new JLabel("<html><body style='width: 200px; padding: 5px;'>" + mensaje + "</body></html>");
        mensajeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(mensajeLabel, BorderLayout.CENTER);
        
        // Configurar el JOptionPane
        JOptionPane optionPane = new JOptionPane(
            panel,
            JOptionPane.PLAIN_MESSAGE,
            JOptionPane.DEFAULT_OPTION,
            null,
            new Object[]{},
            null);
        
        // Crear y configurar el diálogo
        JDialog dialog = optionPane.createDialog(this, "Error");
        dialog.setBackground(Color.WHITE);
        
        // Temporizador para cerrar automáticamente después de 3 segundos
        Timer timer = new Timer(3000, e -> dialog.dispose());
        timer.setRepeats(false);
        timer.start();
        
        dialog.setVisible(true);
    }

    private void mostrarMensaje(String mensaje, String titulo) {
        // Crear un panel personalizado para el mensaje
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        
        // Crear icono de información personalizado
        JLabel iconoLabel = new JLabel(UIManager.getIcon("OptionPane.informationIcon"));
        panel.add(iconoLabel, BorderLayout.WEST);
        
        // Crear label para el mensaje
        JLabel mensajeLabel = new JLabel("<html><body style='width: 200px; padding: 5px;'>" + mensaje + "</body></html>");
        mensajeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(mensajeLabel, BorderLayout.CENTER);
        
        // Configurar el JOptionPane
        JOptionPane optionPane = new JOptionPane(
            panel,
            JOptionPane.PLAIN_MESSAGE,
            JOptionPane.DEFAULT_OPTION,
            null,
            new Object[]{},
            null);
        
        // Crear y configurar el diálogo
        JDialog dialog = optionPane.createDialog(this, titulo);
        dialog.setBackground(Color.WHITE);
        
        // Temporizador para cerrar automáticamente después de 2 segundos
        Timer timer = new Timer(2000, e -> dialog.dispose());
        timer.setRepeats(false);
        timer.start();
        
        dialog.setVisible(true);
    }

				
		private void abrirVentanaRegistro() {
		SwingUtilities.invokeLater(() -> {
		this.dispose(); // Cerramos la ventana de login
		VentanaRegistro ventanaRegistro = new VentanaRegistro();
		ventanaRegistro.setVisible(true);
		});
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
