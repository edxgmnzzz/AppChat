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

    private final Color colorFondo = new Color(41, 128, 185);
    private final Color colorSecundario = new Color(236, 240, 241);
    private JTextComponent campoNombreReal;
    private JPasswordField campoConfirmarPassword;
    private JTextComponent campoTelefono;
    private JDateChooser campoFechaNacimiento;
    private JTextComponent campoRutaFoto;

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
        panelPrincipal.setBackground(colorFondo);

        // Barra de título con los botones de minimizar, maximizar y cerrar
        JPanel barraTitulo = crearBarraTitulo();
        panelPrincipal.add(barraTitulo, BorderLayout.NORTH);

        // Panel de contenido principal
        JPanel panelContenido = new JPanel();
        panelContenido.setLayout(new BoxLayout(panelContenido, BoxLayout.Y_AXIS));
        panelContenido.setOpaque(false);
        panelContenido.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel labelBienvenida = crearLabel("Registro de nuevo usuario", 18, colorSecundario);
        labelBienvenida.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelContenido.add(labelBienvenida);
        panelContenido.add(Box.createRigidArea(new Dimension(0, 30)));

        JPanel panelCampos = new JPanel(new GridBagLayout());
        panelCampos.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Campos del formulario
        campoUsuario = crearCampoTextoPersonalizado(colorSecundario, colorFondo, colorSecundario);
        campoPassword = crearCampoPasswordPersonalizado(colorSecundario, colorFondo, colorSecundario);
        campoEmail = crearCampoTextoPersonalizado(colorSecundario, colorFondo, colorSecundario);
        campoNombreReal = crearCampoTextoPersonalizado(colorSecundario, colorFondo, colorSecundario);
        campoConfirmarPassword = crearCampoPasswordPersonalizado(colorSecundario, colorFondo, colorSecundario);
        campoTelefono = crearCampoTextoPersonalizado(colorSecundario, colorFondo, colorSecundario);
        campoFechaNacimiento = new JDateChooser(); // Para la fecha de nacimiento
        campoFechaNacimiento.setBackground(colorFondo);
        campoRutaFoto = crearCampoTextoPersonalizado(colorSecundario, colorFondo, colorSecundario);

        // Agregar los campos al formulario
        gbc.gridx = 0;
        gbc.gridy = 0;
        panelCampos.add(crearLabel("Nombre Real:", 14, colorSecundario), gbc);
        gbc.gridx = 1;
        panelCampos.add(campoNombreReal, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panelCampos.add(crearLabel("Usuario:", 14, colorSecundario), gbc);
        gbc.gridx = 1;
        panelCampos.add(campoUsuario, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panelCampos.add(crearLabel("Password:", 14, colorSecundario), gbc);
        gbc.gridx = 1;
        panelCampos.add(campoPassword, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panelCampos.add(crearLabel("Confirmar Password:", 14, colorSecundario), gbc);
        gbc.gridx = 1;
        panelCampos.add(campoConfirmarPassword, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        panelCampos.add(crearLabel("Email:", 14, colorSecundario), gbc);
        gbc.gridx = 1;
        panelCampos.add(campoEmail, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        panelCampos.add(crearLabel("Teléfono:", 14, colorSecundario), gbc);
        gbc.gridx = 1;
        panelCampos.add(campoTelefono, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        panelCampos.add(crearLabel("Fecha Nacimiento:", 14, colorSecundario), gbc);
        gbc.gridx = 1;
        panelCampos.add(campoFechaNacimiento, gbc);

        gbc.gridx = 0;
        gbc.gridy = 7;
        panelCampos.add(crearLabel("Ruta Foto:", 14, colorSecundario), gbc);
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
        barraTitulo.setBackground(new Color(52, 152, 219));
        barraTitulo.setPreferredSize(new Dimension(getWidth(), 30));

        // Agregar los botones
        JButton botonMinimizar = new JButton("_");
        botonMinimizar.setFont(new Font("Arial", Font.PLAIN, 20));
        botonMinimizar.setBackground(new Color(52, 152, 219));
        botonMinimizar.setForeground(Color.WHITE);
        botonMinimizar.setBorder(null);
        botonMinimizar.setFocusPainted(false);
        botonMinimizar.addActionListener(e -> setState(JFrame.ICONIFIED));

        JButton botonMaximizar = new JButton("[]");
        botonMaximizar.setFont(new Font("Arial", Font.PLAIN, 20));
        botonMaximizar.setBackground(new Color(52, 152, 219));
        botonMaximizar.setForeground(Color.WHITE);
        botonMaximizar.setBorder(null);
        botonMaximizar.setFocusPainted(false);
        botonMaximizar.addActionListener(e -> {
            if (getExtendedState() == JFrame.MAXIMIZED_BOTH) {
                setExtendedState(JFrame.NORMAL);
            } else {
                setExtendedState(JFrame.MAXIMIZED_BOTH);
            }
        });

        JButton botonCerrar = new JButton("X");
        botonCerrar.setFont(new Font("Arial", Font.PLAIN, 20));
        botonCerrar.setBackground(new Color(52, 152, 219));
        botonCerrar.setForeground(Color.WHITE);
        botonCerrar.setBorder(null);
        botonCerrar.setFocusPainted(false);
        botonCerrar.addActionListener(e -> System.exit(0));

        // Agregar los botones a la barra
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        panelBotones.setOpaque(false);
        panelBotones.add(botonMinimizar);
        panelBotones.add(botonMaximizar);
        panelBotones.add(botonCerrar);
        barraTitulo.add(panelBotones, BorderLayout.EAST);

        return barraTitulo;
    }

    private JTextField crearCampoTextoPersonalizado(Color colorTexto, Color colorFondo, Color colorBorde) {
        JTextField campo = new JTextField(15);
        campo.setFont(new Font("Arial", Font.PLAIN, 14));
        campo.setForeground(colorTexto);
        campo.setBackground(colorFondo);
        campo.setBorder(BorderFactory.createLineBorder(colorBorde, 2));
        return campo;
    }

    private JPasswordField crearCampoPasswordPersonalizado(Color colorTexto, Color colorFondo, Color colorBorde) {
        JPasswordField campo = new JPasswordField(15);
        campo.setFont(new Font("Arial", Font.PLAIN, 14));
        campo.setForeground(colorTexto);
        campo.setBackground(colorFondo);
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
        boton.setBackground(colorFondo);
        boton.setFocusPainted(false);
        boton.setBorder(null);
        boton.addActionListener(accion);
        return boton;
    }

    private JLabel crearLabel(String texto, int tamañoFuente, Color colorTexto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Arial", Font.BOLD, tamañoFuente));
        label.setForeground(colorTexto);
        return label;
    }
}
