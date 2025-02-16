package umu.tds.app.ventanas;

import javax.swing.*;
import umu.tds.app.AppChat.Controlador;

import java.awt.*;
import java.awt.event.*;

public class VentanaPrincipal extends JFrame {
    private static final long serialVersionUID = 1L;
    private static final int WINDOW_WIDTH = 900;
    private static final int WINDOW_HEIGHT = 650;
    private JPanel panelContenidos;
    private Controlador controlador;
    private int xMouse, yMouse;

    private final Color colorFondo = new Color(41, 128, 185);

    public VentanaPrincipal() {
        controlador = Controlador.getInstancia();
        setTitle("ParabarApp");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setUndecorated(true);  // Esto elimina la barra de título predeterminada
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);

        // Creamos el panel de contenidos y lo asignamos a la ventana
        panelContenidos = new JPanel();
        setContentPane(panelContenidos);
        panelContenidos.setLayout(new BorderLayout());

        // Añadir la barra de herramientas personalizada en la parte superior
        Toolbar toolbar = new Toolbar(this, "Principal");
        panelContenidos.add(toolbar, BorderLayout.NORTH);

        // Crear panel para el contenido principal (cuerpo de la ventana)
        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(colorFondo);
        contentPanel.setLayout(new BorderLayout());
        
        // Crear el JScrollPane que contendrá la lista de chats recientes
        String[] chatsRecientes = { "Chat con Pedro", "Chat con María", "Chat con Pedro" }; // Ejemplo
        JList<String> listaChats = new JList<>(chatsRecientes);
        listaChats.setBackground(new Color(245, 245, 245)); // Color de fondo claro
        listaChats.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Sólo una selección

        JScrollPane scrollPane = new JScrollPane(listaChats);
        scrollPane.setPreferredSize(new Dimension(250, 500)); // Ajusta el tamaño de la lista
        
        contentPanel.add(scrollPane, BorderLayout.WEST);  // Coloca el JScrollPane a la izquierda

        // Crear un área de chat o contenido adicional
        JTextArea areaMensajes = new JTextArea();
        areaMensajes.setBackground(Color.WHITE);
        areaMensajes.setEditable(false);
        areaMensajes.setText("�Bienvenido a la aplicaci�n!");
        areaMensajes.setWrapStyleWord(true);
        areaMensajes.setLineWrap(true);

        // Añadir el área de mensajes al centro de la ventana
        contentPanel.add(areaMensajes, BorderLayout.CENTER);

        panelContenidos.add(contentPanel, BorderLayout.CENTER);  // Añadimos el contenido al centro de la ventana
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            VentanaConBarra ventana = new VentanaConBarra();
            ventana.setVisible(true);
        });
    }
}
