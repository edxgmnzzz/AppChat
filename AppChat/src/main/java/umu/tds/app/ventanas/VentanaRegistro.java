package umu.tds.app.ventanas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import com.toedter.calendar.JDateChooser;
import umu.tds.app.AppChat.Controlador;
import umu.tds.app.AppChat.Theme;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class VentanaRegistro extends JFrame {
    private static final long serialVersionUID = 1L;
    
    private JTextField campoNombreReal;
    private JTextField campoNombreUsuario;
    private JPasswordField campoPassword;
    private JPasswordField campoConfirmarPassword;
    private JTextField campoEmail;
    private JTextField campoTelefono;
    private JDateChooser campoFechaNacimiento;
    private JTextField campoRutaFoto;
    private JTextField campoSaludo;
    private Point initialClick;
    private final Controlador controlador;

    public VentanaRegistro() {
        controlador = Controlador.getInstancia();
        configurarVentana();
        crearComponentes();
    }

    private void configurarVentana() {
        setSize(400, 600);
        setUndecorated(true);
        setLocationRelativeTo(null);
        setShape(new java.awt.geom.RoundRectangle2D.Double(0, 0, 400, 600, 15, 15));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
        barraTitulo.setPreferredSize(new Dimension(400, 30));
        
        JLabel labelTitulo = new JLabel("  Registro");
        labelTitulo.setForeground(Theme.COLOR_SECUNDARIO);
        labelTitulo.setFont(Theme.FONT_BOLD_MEDIUM);
        barraTitulo.add(labelTitulo, BorderLayout.WEST);

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
        panelContenido.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel labelBienvenida = new JLabel("Registro de nuevo usuario", JLabel.CENTER);
        labelBienvenida.setFont(Theme.FONT_BOLD_LARGE);
        labelBienvenida.setForeground(Theme.COLOR_SECUNDARIO);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panelContenido.add(labelBienvenida, gbc);

        JLabel labelNombreReal = new JLabel("Nombre Completo:");
        labelNombreReal.setFont(Theme.FONT_BOLD_MEDIUM);
        labelNombreReal.setForeground(Theme.COLOR_SECUNDARIO);
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        panelContenido.add(labelNombreReal, gbc);

        campoNombreReal = new JTextField(15);
        campoNombreReal.setFont(Theme.FONT_PLAIN_MEDIUM);
        campoNombreReal.setForeground(Theme.COLOR_PRINCIPAL);
        campoNombreReal.setBackground(Theme.COLOR_SECUNDARIO);
        campoNombreReal.setBorder(BorderFactory.createLineBorder(Theme.COLOR_PRINCIPAL, 2));
        gbc.gridx = 1; gbc.gridy = 1;
        panelContenido.add(campoNombreReal, gbc);

        JLabel labelNombreUsuario = new JLabel("Usuario:");
        labelNombreUsuario.setFont(Theme.FONT_BOLD_MEDIUM);
        labelNombreUsuario.setForeground(Theme.COLOR_SECUNDARIO);
        gbc.gridx = 0; gbc.gridy = 2;
        panelContenido.add(labelNombreUsuario, gbc);

        campoNombreUsuario = new JTextField(15);
        campoNombreUsuario.setFont(Theme.FONT_PLAIN_MEDIUM);
        campoNombreUsuario.setForeground(Theme.COLOR_PRINCIPAL);
        campoNombreUsuario.setBackground(Theme.COLOR_SECUNDARIO);
        campoNombreUsuario.setBorder(BorderFactory.createLineBorder(Theme.COLOR_PRINCIPAL, 2));
        gbc.gridx = 1; gbc.gridy = 2;
        panelContenido.add(campoNombreUsuario, gbc);

        JLabel labelPassword = new JLabel("Contraseña:");
        labelPassword.setFont(Theme.FONT_BOLD_MEDIUM);
        labelPassword.setForeground(Theme.COLOR_SECUNDARIO);
        gbc.gridx = 0; gbc.gridy = 3;
        panelContenido.add(labelPassword, gbc);

        campoPassword = new JPasswordField(15);
        campoPassword.setFont(Theme.FONT_PLAIN_MEDIUM);
        campoPassword.setForeground(Theme.COLOR_PRINCIPAL);
        campoPassword.setBackground(Theme.COLOR_SECUNDARIO);
        campoPassword.setBorder(BorderFactory.createLineBorder(Theme.COLOR_PRINCIPAL, 2));
        gbc.gridx = 1; gbc.gridy = 3;
        panelContenido.add(campoPassword, gbc);

        JLabel labelConfirmarPassword = new JLabel("Confirmar Contraseña:");
        labelConfirmarPassword.setFont(Theme.FONT_BOLD_MEDIUM);
        labelConfirmarPassword.setForeground(Theme.COLOR_SECUNDARIO);
        gbc.gridx = 0; gbc.gridy = 4;
        panelContenido.add(labelConfirmarPassword, gbc);

        campoConfirmarPassword = new JPasswordField(15);
        campoConfirmarPassword.setFont(Theme.FONT_PLAIN_MEDIUM);
        campoConfirmarPassword.setForeground(Theme.COLOR_PRINCIPAL);
        campoConfirmarPassword.setBackground(Theme.COLOR_SECUNDARIO);
        campoConfirmarPassword.setBorder(BorderFactory.createLineBorder(Theme.COLOR_PRINCIPAL, 2));
        gbc.gridx = 1; gbc.gridy = 4;
        panelContenido.add(campoConfirmarPassword, gbc);

        JLabel labelEmail = new JLabel("Email:");
        labelEmail.setFont(Theme.FONT_BOLD_MEDIUM);
        labelEmail.setForeground(Theme.COLOR_SECUNDARIO);
        gbc.gridx = 0; gbc.gridy = 5;
        panelContenido.add(labelEmail, gbc);

        campoEmail = new JTextField(15);
        campoEmail.setFont(Theme.FONT_PLAIN_MEDIUM);
        campoEmail.setForeground(Theme.COLOR_PRINCIPAL);
        campoEmail.setBackground(Theme.COLOR_SECUNDARIO);
        campoEmail.setBorder(BorderFactory.createLineBorder(Theme.COLOR_PRINCIPAL, 2));
        gbc.gridx = 1; gbc.gridy = 5;
        panelContenido.add(campoEmail, gbc);

        JLabel labelTelefono = new JLabel("Teléfono:");
        labelTelefono.setFont(Theme.FONT_BOLD_MEDIUM);
        labelTelefono.setForeground(Theme.COLOR_SECUNDARIO);
        gbc.gridx = 0; gbc.gridy = 6;
        panelContenido.add(labelTelefono, gbc);

        campoTelefono = new JTextField(15);
        campoTelefono.setFont(Theme.FONT_PLAIN_MEDIUM);
        campoTelefono.setForeground(Theme.COLOR_PRINCIPAL);
        campoTelefono.setBackground(Theme.COLOR_SECUNDARIO);
        campoTelefono.setBorder(BorderFactory.createLineBorder(Theme.COLOR_PRINCIPAL, 2));
        gbc.gridx = 1; gbc.gridy = 6;
        panelContenido.add(campoTelefono, gbc);

        JLabel labelFechaNacimiento = new JLabel("Fecha de Nacimiento:");
        labelFechaNacimiento.setFont(Theme.FONT_BOLD_MEDIUM);
        labelFechaNacimiento.setForeground(Theme.COLOR_SECUNDARIO);
        gbc.gridx = 0; gbc.gridy = 7;
        panelContenido.add(labelFechaNacimiento, gbc);

        campoFechaNacimiento = new JDateChooser();
        campoFechaNacimiento.setFont(Theme.FONT_PLAIN_MEDIUM);
        campoFechaNacimiento.setBackground(Theme.COLOR_SECUNDARIO);
        gbc.gridx = 1; gbc.gridy = 7;
        panelContenido.add(campoFechaNacimiento, gbc);

        JLabel labelRutaFoto = new JLabel("URL de Foto:");
        labelRutaFoto.setFont(Theme.FONT_BOLD_MEDIUM);
        labelRutaFoto.setForeground(Theme.COLOR_SECUNDARIO);
        gbc.gridx = 0; gbc.gridy = 8;
        panelContenido.add(labelRutaFoto, gbc);

        campoRutaFoto = new JTextField(15);
        campoRutaFoto.setFont(Theme.FONT_PLAIN_MEDIUM);
        campoRutaFoto.setForeground(Theme.COLOR_PRINCIPAL);
        campoRutaFoto.setBackground(Theme.COLOR_SECUNDARIO);
        campoRutaFoto.setBorder(BorderFactory.createLineBorder(Theme.COLOR_PRINCIPAL, 2));
        gbc.gridx = 1; gbc.gridy = 8;
        panelContenido.add(campoRutaFoto, gbc);

        JLabel labelSaludo = new JLabel("Mensaje de Saludo:");
        labelSaludo.setFont(Theme.FONT_BOLD_MEDIUM);
        labelSaludo.setForeground(Theme.COLOR_SECUNDARIO);
        gbc.gridx = 0; gbc.gridy = 9;
        panelContenido.add(labelSaludo, gbc);

        campoSaludo = new JTextField(15);
        campoSaludo.setFont(Theme.FONT_PLAIN_MEDIUM);
        campoSaludo.setForeground(Theme.COLOR_PRINCIPAL);
        campoSaludo.setBackground(Theme.COLOR_SECUNDARIO);
        campoSaludo.setBorder(BorderFactory.createLineBorder(Theme.COLOR_PRINCIPAL, 2));
        gbc.gridx = 1; gbc.gridy = 9;
        panelContenido.add(campoSaludo, gbc);

        JButton botonRegistrar = new JButton("Registrar");
        botonRegistrar.setFont(Theme.FONT_BOLD_MEDIUM);
        botonRegistrar.setForeground(Theme.COLOR_TEXTO);
        botonRegistrar.setBackground(Theme.COLOR_ACENTO);
        botonRegistrar.setFocusPainted(false);
        botonRegistrar.addActionListener(e -> registrarUsuario());
        gbc.gridx = 0; gbc.gridy = 10; gbc.gridwidth = 2;
        panelContenido.add(botonRegistrar, gbc);

        return panelContenido;
    }

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
        if (!rutaFoto.isEmpty() && !rutaFoto.matches("https?://.*\\.(jpg|jpeg|png|gif)")) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese una URL válida de una imagen", "Error", JOptionPane.ERROR_MESSAGE);
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