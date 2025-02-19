package umu.tds.app.ventanas;

import javax.swing.*;

import tds.BubbleText;
import umu.tds.app.AppChat.ContactoIndividual;
import umu.tds.app.AppChat.Controlador;
import umu.tds.app.AppChat.Usuario;

import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class VentanaPrincipal extends JFrame {
    private static final long serialVersionUID = 1L;
    private static final int WINDOW_WIDTH = 900;
    private static final int WINDOW_HEIGHT = 650;
    private JPanel panelContenidos;
    private Controlador controlador;
    private int xMouse, yMouse;

    private final Color colorFondo = new Color(41, 128, 185);

    public VentanaPrincipal() {
    	
    	List<ContactoIndividual> pruebacs = new ArrayList<>();
        pruebacs.add(new ContactoIndividual("Oscar", 123456789, new Usuario(new ImageIcon(), "Oscar", LocalDate.of(1990, 1, 1), 123456789, "oscar", "12345", "oscar@gmail.com")));
        pruebacs.add(new ContactoIndividual("Javi", 234567890, new Usuario(new ImageIcon(), "Javi", LocalDate.of(1995, 1, 1), 234567890, "javi", "password", "javi@gmail.com")));
        pruebacs.add(new ContactoIndividual("Lucia", 345678901, new Usuario(new ImageIcon(), "Lucia", LocalDate.of(2000, 1, 1), 345678901, "lucia", "12345", "lucia@gmail.com")));
        pruebacs.add(new ContactoIndividual("Gloria", 456789012, new Usuario(new ImageIcon(), "Gloria", LocalDate.of(1985, 1, 1), 456789012, "gloria", "password", "gloria@gmail.com")));
    	
        //controlador = Controlador.getInstancia();
        setTitle("ParabarApp");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        //setUndecorated(true);  // Esto elimina la barra de t√≠tulo predeterminada
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        
        // Creamos el panel de contenidos y lo asignamos a la ventana
        panelContenidos = new JPanel();
        setContentPane(panelContenidos);
        panelContenidos.setLayout(new BorderLayout());
        
        // Crear panel de arriba con todas las opciones
        JPanel panelNorte = new JPanel();
        panelContenidos.add(panelNorte, BorderLayout.NORTH);
        panelNorte.setLayout((new BoxLayout(panelNorte, BoxLayout.Y_AXIS)));
        
        JPanel panelOpciones = new JPanel();
        panelNorte.add(panelOpciones);
        
        JComboBox<String> contactos = new JComboBox<>(new String[]{"Seleccione un contacto...", "Florentino", "Laporta"});
        JTextField phoneInput = new JTextField(10);
        JButton sendButton = new JButton("Enviar");
        JButton searchButton = new JButton("Buscar");
        JButton contactsButton = new JButton("Contactos");
        JButton premiumButton = new JButton("Premium");
        JLabel userLabel = new JLabel("Florentino PÈrez");
        JLabel userImage = new JLabel(new ImageIcon("user_icon.png"));
        
        panelOpciones.add(contactos);
        panelOpciones.add(phoneInput);
        panelOpciones.add(sendButton);
        panelOpciones.add(searchButton);
        panelOpciones.add(contactsButton);
        panelOpciones.add(premiumButton);
        panelOpciones.add(userLabel);
        panelOpciones.add(userImage);
        
        premiumButton.addActionListener(ev -> {
        	JOptionPane.showConfirmDialog(null, "øQuieres hacerte Premium socio?");
        });
        
        contactsButton.addActionListener(ev -> {
        	JFrame panelContactos = new JFrame();
        	panelContactos.setSize(500,500);
        	panelContactos.setLocationRelativeTo(null);
        	panelContactos.setLayout(new BorderLayout());
        	panelContactos.setVisible(true);
        	
        	JPanel principalContactos = new JPanel(new GridLayout(1, 3, 10, 10));
        	
        });
        
        // Crear panel de la izquierda
        JPanel panelIzquierda = new JPanel();
        panelContenidos.add(panelIzquierda, BorderLayout.WEST);
        panelIzquierda.setPreferredSize(new Dimension(300, getHeight()));
        panelIzquierda.setBorder(BorderFactory.createTitledBorder("Chats"));
        
        String[] chatsRecientes = { "Chat con Piter", "Chat con Maria", "Chat con Pedro", "Chat con Pedro", "Chat con Pedro", "Chat con Pedro", "Chat con Pedro", "Chat con Pedro", "Chat con Pedro", "Chat con Pedro", "Chat con Pedro","Chat con Piter", "Chat con Maria", "Chat con Pedro", "Chat con Pedro", "Chat con Pedro", "Chat con Pedro", "Chat con Pedro", "Chat con Pedro", "Chat con Pedro", "Chat con Pedro", "Chat con Pedro","Chat con Piter", "Chat con Maria", "Chat con Pedro", "Chat con Pedro", "Chat con Pedro", "Chat con Pedro", "Chat con Pedro", "Chat con Pedro", "Chat con Pedro", "Chat con Pedro", "Chat con Pedro" ,"Chat con Piter", "Chat con Maria", "Chat con Pedro", "Chat con Pedro", "Chat con Pedro", "Chat con Pedro", "Chat con Pedro", "Chat con Pedro", "Chat con Pedro", "Chat con Pedro", "Chat con Pedro" }; // Ejemplo
        JList<String> listaChats = new JList<>(chatsRecientes);
        listaChats.setBackground(new Color(245, 245, 245)); // Color de fondo claro
        //listaChats.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // S√≥lo una selecci√≥n
        
        listaChats.setCellRenderer(null);
        
        JScrollPane scrollPane = new JScrollPane(listaChats);
        panelIzquierda.add(scrollPane);
        scrollPane.setPreferredSize(new Dimension(280, 500)); // Ajusta el tama√±o de la lista
        
        
        
        // Crear panel de la derecha
        JPanel panelDerecha = new JPanel(new BorderLayout());
        panelDerecha.setBorder(BorderFactory.createTitledBorder("mensajes con Laporta"));

        JPanel chat = new JPanel();
        chat.setLayout(new BoxLayout(chat, BoxLayout.Y_AXIS)); // Apilar burbujas verticalmente
        chat.setSize(400,700);
        JScrollPane scrollchat = new JScrollPane();
        scrollchat.setViewportView(chat);
        scrollchat.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS); // Siempre mostrar scrollbar
        scrollchat.getVerticalScrollBar().setUnitIncrement(16);
        
        BubbleText burbuja;
        burbuja = new BubbleText(chat, "Hola su florentineza",Color.GREEN, "J.RamÛn", BubbleText.SENT);
        chat.add(burbuja);
        
        BubbleText burbuja2;
        burbuja2=new BubbleText(chat, "Hola,†øEst·†seguro†de†que†la†burbuja†usa†varias†lineas†si†es†necesario?", Color.LIGHT_GRAY, "Alumno", BubbleText.RECEIVED);
        chat.add(burbuja2);
        
        BubbleText burbuja3;
        burbuja3 = new BubbleText(chat, "Hola florentineza",Color.GREEN, "J.RamÛn", BubbleText.SENT);
        chat.add(burbuja3);
        
        BubbleText burbuja4;
        burbuja4=new BubbleText(chat, "Hola,†ø†seguro†de†que†la†burbuja†usa†varias†lineas†si†es†necesario?", Color.LIGHT_GRAY, "Alumno", BubbleText.RECEIVED);
        chat.add(burbuja4);
        
        BubbleText burbuja5;
        burbuja5 = new BubbleText(chat, "Hola florentineza",Color.GREEN, "J.RamÛn", BubbleText.SENT);
        chat.add(burbuja5);
        
        BubbleText burbuja6;
        burbuja6=new BubbleText(chat, "Hola,†ø†seguro†de†que†la†burbuja†usa†varias†lineas†si†es†necesario?", Color.LIGHT_GRAY, "Alumno", BubbleText.RECEIVED);
        chat.add(burbuja6);
        
        BubbleText burbuja7;
        burbuja7 = new BubbleText(chat, "Hola florentineza",Color.GREEN, "J.RamÛn", BubbleText.SENT);
        chat.add(burbuja7);
        
        BubbleText burbuja8;
        burbuja8=new BubbleText(chat, "Hola,†ø†seguro†de†que†la†burbuja†usa†varias†lineas†si†es†necesario?", Color.LIGHT_GRAY, "Alumno", BubbleText.RECEIVED);
        chat.add(burbuja8);
        
        panelDerecha.add(scrollchat);
        panelContenidos.add(panelDerecha,BorderLayout.CENTER);
        
    }

    /*public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            VentanaConBarra ventana = new VentanaConBarra();
            ventana.setVisible(true);
        });
    }*/
}
