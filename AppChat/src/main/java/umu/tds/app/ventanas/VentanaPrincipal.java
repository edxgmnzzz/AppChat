package main.java.umu.tds.app.ventanas;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import main.java.umu.tds.app.AppChat.Controlador;
import tds.BubbleText;

public class VentanaPrincipal extends JFrame {
    private static final long serialVersionUID = 1L;
    private JPanel panelContactos;
    private JPanel panelChat;
    private JTextField campoMensaje;
    private JTextArea areaMensajes;
    private String contactoActual;
    private Controlador controlador;

    public VentanaPrincipal() {
        controlador = Controlador.getInstancia();
        configurarVentana();
        inicializarComponentes();
    }

    private void configurarVentana() {
        setTitle("ParabarApp");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void inicializarComponentes() {
        JPanel panelPrincipal = new JPanel(new BorderLayout());

        // Panel de contactos (izquierda)
        panelContactos = new JPanel(new GridLayout(0, 1, 0, 0)); // Manteniendo GridLayout
        panelContactos.setBorder(new EmptyBorder(0, 0, 0, 0)); // Sin padding
        JScrollPane scrollContactos = new JScrollPane(panelContactos);
        scrollContactos.setPreferredSize(new Dimension(200, 0));

        // Simulamos una lista de contactos
        for (String contacto : controlador.obtenerContactos()) {
            JButton btnContacto = new JButton(contacto);
            btnContacto.setHorizontalAlignment(SwingConstants.LEFT);
            btnContacto.setBorderPainted(false);
            btnContacto.setFocusPainted(false);
            btnContacto.setContentAreaFilled(false);
            btnContacto.setOpaque(true);
            btnContacto.setBackground(Color.WHITE);
            btnContacto.addActionListener(e -> cambiarChat(contacto));
            btnContacto.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent evt) {
                    btnContacto.setBackground(new Color(230, 230, 230)); // Cambio de color al pasar el mouse
                }
                public void mouseExited(MouseEvent evt) {
                    btnContacto.setBackground(Color.WHITE); // Restaurar color
                }
            });
            panelContactos.add(btnContacto);
        }

        // Panel de chat (derecha)
        panelChat = new JPanel(new BorderLayout());
        panelChat.setBorder(new EmptyBorder(10, 10, 10, 10));

        areaMensajes = new JTextArea();
        areaMensajes.setEditable(false);
        JScrollPane scrollMensajes = new JScrollPane(areaMensajes);

        campoMensaje = new JTextField();
        JButton btnEnviar = new JButton("Enviar");
        btnEnviar.addActionListener(e -> enviarMensaje());

        JPanel panelEnvio = new JPanel(new BorderLayout());
        panelEnvio.add(campoMensaje, BorderLayout.CENTER);
        panelEnvio.add(btnEnviar, BorderLayout.EAST);

        panelChat.add(scrollMensajes, BorderLayout.CENTER);
        panelChat.add(panelEnvio, BorderLayout.SOUTH);

        panelPrincipal.add(scrollContactos, BorderLayout.WEST);
        panelPrincipal.add(panelChat, BorderLayout.CENTER);

        setContentPane(panelPrincipal);
    }

    private void cambiarChat(String contacto) {
        contactoActual = contacto;
        areaMensajes.setText(""); // Limpiar mensajes anteriores
        areaMensajes.append("Chat con " + contacto + "\n\n");
        
        // Cargar los mensajes previos con este contacto
        for (String mensaje : controlador.obtenerMensajes(contacto)) {
            areaMensajes.append(mensaje + "\n");
        }
    }

    private void enviarMensaje() {
        String mensaje = campoMensaje.getText();
        if (!mensaje.isEmpty() && contactoActual != null) {
            areaMensajes.append("Tú: " + mensaje + "\n");
            campoMensaje.setText(""); // Limpiar campo de mensaje

            // Enviar el mensaje al controlador
            controlador.enviarMensaje(contactoActual, mensaje);

            // Simular respuesta automática después de un breve retraso
            Timer timer = new Timer(1000, e -> {
                String respuesta = controlador.obtenerRespuesta(contactoActual);
                areaMensajes.append(contactoActual + ": " + respuesta + "\n");
            });
            timer.setRepeats(false);
            timer.start();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            VentanaPrincipal ventana = new VentanaPrincipal();
            ventana.setVisible(true);
        });
    }
}
