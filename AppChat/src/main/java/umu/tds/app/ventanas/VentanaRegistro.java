package umu.tds.app.ventanas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.text.JTextComponent;
import com.toedter.calendar.JDateChooser;
import umu.tds.app.AppChat.Controlador;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class VentanaRegistro extends JFrame {
    private static final long serialVersionUID = 1L;
    private JTextField campoUsuario;
    private JPasswordField campoPassword;
    private JTextField campoEmail;
    private Controlador controlador;
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
    private JTextComponent campoNombreReal;
    private JPasswordField campoConfirmarPassword;
    private JTextComponent campoTelefono;
    private JDateChooser campoFechaNacimiento;
    private JTextComponent campoRutaFoto;
    private Point initialClick;

    public VentanaRegistro() {
        controlador = Controlador.getInstancia();
        configurarVentana();
        crearComponentes();
    }

    private void configurarVentana() {
        // Configurar ventana para que se abra maximizada a pantalla completa
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(false); // Para mantener los controles de la ventana
        setLocationRelativeTo(null); // Centrar la ventana en la pantalla
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void crearComponentes() {
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(COLOR_FONDO);

        // Barra de título con los botones de minimizar, maximizar y cerrar
        JPanel barraTitulo = crearBarraTitulo();
        panelPrincipal.add(barraTitulo, BorderLayout.NORTH);

        // Panel de contenido principal
        JPanel panelContenido = new JPanel();
        panelContenido.setLayout(new BoxLayout(panelContenido, BoxLayout.Y_AXIS));
        panelContenido.setOpaque(false);
        panelContenido.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel labelBienvenida = crearLabel("Registro de nuevo usuario", 18, COLOR_SECUNDARIO);
        labelBienvenida.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelContenido.add(labelBienvenida);
        panelContenido.add(Box.createRigidArea(new Dimension(0, 30)));

        JPanel panelCampos = new JPanel(new GridBagLayout());
        panelCampos.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Campos del formulario
        campoUsuario = crearCampoTextoPersonalizado(COLOR_SECUNDARIO, COLOR_FONDO, COLOR_SECUNDARIO);
        campoPassword = crearCampoPasswordPersonalizado(COLOR_SECUNDARIO, COLOR_FONDO, COLOR_SECUNDARIO);
        campoEmail = crearCampoTextoPersonalizado(COLOR_SECUNDARIO, COLOR_FONDO, COLOR_SECUNDARIO);
        campoNombreReal = crearCampoTextoPersonalizado(COLOR_SECUNDARIO, COLOR_FONDO, COLOR_SECUNDARIO);
        campoConfirmarPassword = crearCampoPasswordPersonalizado(COLOR_SECUNDARIO, COLOR_FONDO, COLOR_SECUNDARIO);
        campoTelefono = crearCampoTextoPersonalizado(COLOR_SECUNDARIO, COLOR_FONDO, COLOR_SECUNDARIO);
        campoFechaNacimiento = new JDateChooser(); // Para la fecha de nacimiento
        campoFechaNacimiento.setBackground(COLOR_FONDO);
        campoRutaFoto = crearCampoTextoPersonalizado(COLOR_SECUNDARIO, COLOR_FONDO, COLOR_SECUNDARIO);

        // Agregar los campos al formulario
        gbc.gridx = 0;
        gbc.gridy = 0;
        panelCampos.add(crearLabel("Nombre Real:", 14, COLOR_SECUNDARIO), gbc);
        gbc.gridx = 1;
        panelCampos.add(campoNombreReal, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panelCampos.add(crearLabel("Usuario:", 14, COLOR_SECUNDARIO), gbc);
        gbc.gridx = 1;
        panelCampos.add(campoUsuario, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        panelCampos.add(crearLabel("Password:", 14, COLOR_SECUNDARIO), gbc);
        gbc.gridx = 1;
        panelCampos.add(campoPassword, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panelCampos.add(crearLabel("Confirmar Password:", 14, COLOR_SECUNDARIO), gbc);
        gbc.gridx = 1;
        panelCampos.add(campoConfirmarPassword, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        panelCampos.add(crearLabel("Email:", 14, COLOR_SECUNDARIO), gbc);
        gbc.gridx = 1;
        panelCampos.add(campoEmail, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        panelCampos.add(crearLabel("Teléfono:", 14, COLOR_SECUNDARIO), gbc);
        gbc.gridx = 1;
        panelCampos.add(campoTelefono, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        panelCampos.add(crearLabel("Fecha Nacimiento:", 14, COLOR_SECUNDARIO), gbc);
        gbc.gridx = 1;
        panelCampos.add(campoFechaNacimiento, gbc);

        gbc.gridx = 0;
        gbc.gridy = 7;
        panelCampos.add(crearLabel("Ruta Foto:", 14, COLOR_SECUNDARIO), gbc);
        gbc.gridx = 1;
        panelCampos.add(campoRutaFoto, gbc);

        panelContenido.add(panelCampos);

        panelContenido.add(Box.createRigidArea(new Dimension(0, 20)));

        // Botón de registro
        JButton botonRegistrar = crearBotonPersonalizado("Registrar", e -> registrarUsuario(),
                Color.RED, Color.WHITE, new Color(192, 57, 43));
        botonRegistrar.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelContenido.add(botonRegistrar);

        panelPrincipal.add(panelContenido, BorderLayout.CENTER);
        add(panelPrincipal);
    }

    private void registrarUsuario() {
        String nombreReal = campoNombreReal.getText();  
        String nombreUsuario = campoUsuario.getText();
        String password = new String(campoPassword.getPassword());
        String confirmarPassword = new String(campoConfirmarPassword.getPassword());  
        String email = campoEmail.getText();
        int telefono = Integer.parseInt(campoTelefono.getText());  
        Date fechaNacimiento = campoFechaNacimiento.getDate(); 
        
        // Convertir de Date a LocalDate
        LocalDate localFechaNacimiento = null;
        if (fechaNacimiento != null) {
            localFechaNacimiento = fechaNacimiento.toInstant()
                                                  .atZone(ZoneId.systemDefault()) 
                                                  .toLocalDate();
        }
        
        String rutaFoto = campoRutaFoto.getText();  

        // Validación de contraseñas
        if (!password.equals(confirmarPassword)) {
            JOptionPane.showMessageDialog(this, "Las contraseñas no coinciden", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Registro del usuario usando el controlador
        if (controlador.registrarUsuario(nombreReal, nombreUsuario, password, confirmarPassword, email, telefono, localFechaNacimiento, rutaFoto)) {
            JOptionPane.showMessageDialog(this, "Registro exitoso", "Bienvenido", JOptionPane.INFORMATION_MESSAGE);
            this.dispose(); 
            VentanaLogin ventanaLogin = new VentanaLogin();
            ventanaLogin.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Error en el registro", "Error", JOptionPane.ERROR_MESSAGE);
        }
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
    
    private JTextField crearCampoTextoPersonalizado(Color colorTexto, Color COLOR_FONDO, Color colorBorde) {
        JTextField campo = new JTextField(15);
        campo.setFont(new Font("Arial", Font.PLAIN, 14));
        campo.setForeground(colorTexto);
        campo.setBackground(COLOR_FONDO);
        campo.setBorder(BorderFactory.createLineBorder(colorBorde, 2));
        return campo;
    }

    private JPasswordField crearCampoPasswordPersonalizado(Color colorTexto, Color COLOR_FONDO, Color colorBorde) {
        JPasswordField campo = new JPasswordField(15);
        campo.setFont(new Font("Arial", Font.PLAIN, 14));
        campo.setForeground(colorTexto);
        campo.setBackground(COLOR_FONDO);
        campo.setBorder(BorderFactory.createLineBorder(colorBorde, 2));
        return campo;
    }

    private JButton crearBotonPersonalizado(String texto, ActionListener accion, Color colorFondo, Color colorTexto, Color colorHover) {
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
        return boton;
    }


    private JLabel crearLabel(String texto, int tama�oFuente, Color colorTexto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Arial", Font.BOLD, tama�oFuente));
        label.setForeground(colorTexto);
        return label;
    }
}
