package main.java.umu.tds.app.ventanas;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class VentanaPrincipal extends JFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel panelContactos;
    private JPanel panelChat;
    private JTextField campoMensaje;
    private JTextArea areaMensajes;
    private List<String> contactos;
    private String contactoActual;

    public VentanaPrincipal() {
        configurarVentana();
        inicializarComponentes();
        configurarEventos();
    }

    private void configurarVentana() {
        setTitle("ParabarApp");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void inicializarComponentes() {
        // Panel principal con BorderLayout
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        
        // Panel de contactos (izquierda)
        panelContactos = new JPanel(new GridLayout(0, 1, 0, 0)); // Cambiado a GridLayout
        panelContactos.setBorder(new EmptyBorder(0, 0, 0, 0)); // Eliminado el padding
        JScrollPane scrollContactos = new JScrollPane(panelContactos);
        scrollContactos.setPreferredSize(new Dimension(200, 0));
        
        // Simulamos una lista de contactos
        contactos = new ArrayList<>(List.of("Oscar", "Javi", "Lucia", "Gloria"));
        for (String contacto : contactos) {
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
                    btnContacto.setBackground(new Color(230, 230, 230));
                }
                public void mouseExited(MouseEvent evt) {
                    btnContacto.setBackground(Color.WHITE);
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

        // Agregar componentes al panel principal
        panelPrincipal.add(scrollContactos, BorderLayout.WEST);
        panelPrincipal.add(panelChat, BorderLayout.CENTER);

        setContentPane(panelPrincipal);
    }
    private void configurarEventos() {
        campoMensaje.addActionListener(e -> enviarMensaje());
    }

    private void cambiarChat(String contacto) {
        contactoActual = contacto;
        areaMensajes.setText(""); // Limpiar mensajes anteriores
        areaMensajes.append("Chat con " + contacto + "\n\n");
    }

    private void enviarMensaje() {
        String mensaje = campoMensaje.getText();
        if (!mensaje.isEmpty() && contactoActual != null) {
            areaMensajes.append("Tú: " + mensaje + "\n");
            campoMensaje.setText(""); // Limpiar campo de mensaje
            
            // Simular respuesta después de un breve retraso
            Timer timer = new Timer(1000, e -> {
                areaMensajes.append(contactoActual + ": Gracias.\n");
            });
            timer.setRepeats(false);
            timer.start();
        }
    }
    /**
     * Método principal que lanza la aplicación.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            VentanaPrincipal ventana = new VentanaPrincipal();
            ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            ventana.setVisible(true);
        });
    }
}