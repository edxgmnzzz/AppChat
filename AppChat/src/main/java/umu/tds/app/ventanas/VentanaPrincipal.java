package umu.tds.app.ventanas;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import tds.BubbleText;

import java.awt.*;
import java.awt.event.*;
import umu.tds.app.AppChat.Controlador;
import umu.tds.app.AppChat.ContactoIndividual;
import umu.tds.app.AppChat.*;




public class VentanaPrincipal extends JFrame {
    private static final long serialVersionUID = 1L;
    private static final int WINDOW_WIDTH = 900;
    private static final int WINDOW_HEIGHT = 650;
    private static final int BORDER_RADIUS = 15;

    private JPanel panelContactos;
    private JPanel panelChat;
    private JPanel panelContenidos;
    private JTextField campoMensaje;
    private JTextArea areaMensajes;
    private ContactoIndividual contactoActual;  
    private Controlador controlador;
    private int xMouse, yMouse;

    private final Color colorFondo = new Color(41, 128, 185);
    private final Color colorPrincipal = new Color(52, 152, 219);
    private final Color colorSecundario = new Color(236, 240, 241);
    private final Color colorAcento = new Color(231, 76, 60);

    
    
    public VentanaPrincipal() {
        controlador = Controlador.getInstancia();
        //setIconImage(Toolkit.getDefaultToolkit().getImage("poner bien));
        panelContenidos = new JPanel();
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setUndecorated(true);
        setLocationRelativeTo(null);
        setShape(new java.awt.geom.RoundRectangle2D.Double(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT, BORDER_RADIUS, BORDER_RADIUS));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        setContentPane(panelContenidos);
        Toolbar toolbar = new Toolbar(this, "Principal");
        add(toolbar, BorderLayout.NORTH);
        JPanel chat=new JPanel();
        chat.setLayout(new BoxLayout(chat,BoxLayout.Y_AXIS));
        chat.setSize(200,300);
        chat.setMinimumSize(new Dimension(400,700));
        chat.setMaximumSize(new Dimension(400,700));
        chat.setPreferredSize(new Dimension(400,700));
        BubbleText burbuja;
        burbuja=new BubbleText(chat,"Hola grupo!!", Color.GREEN, "J.Ramón", BubbleText.SENT);
        chat.add(burbuja);
        panelContenidos.add(chat);
    }


}
