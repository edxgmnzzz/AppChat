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
    
    	
        controlador = Controlador.getInstancia();
        setTitle("ParabarApp");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        //setUndecorated(true);  // Esto elimina la barra de tÃ­tulo predeterminada
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
        JLabel userLabel = new JLabel(controlador.getNombreUserActual());
        JLabel userImage = new JLabel(controlador.getIconoUserActual());
        
        panelOpciones.add(contactos);
        panelOpciones.add(phoneInput);
        panelOpciones.add(sendButton);
        panelOpciones.add(searchButton);
        panelOpciones.add(contactsButton);
        panelOpciones.add(premiumButton);
        panelOpciones.add(userLabel);
        panelOpciones.add(userImage);
        
        searchButton.addActionListener(ev2 -> {
        	JFrame panelBuscar = new JFrame();
        	panelBuscar.setSize(500,500);
        	panelBuscar.setLocationRelativeTo(null);
        	panelBuscar.setLayout(new BorderLayout());
        	panelBuscar.setVisible(true);
        	
        	// Panel de búsqueda
            JPanel searchPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            JLabel searchIcon = new JLabel(new ImageIcon("search_icon.png")); // Reemplazar con un icono real
            JTextField textSearch = new JTextField(10);
            JTextField phoneSearch = new JTextField(10);
            JTextField contactSearch = new JTextField(10);
            JButton searchButton2 = new JButton("Buscar");

            gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1;
            searchPanel.add(searchIcon, gbc);

            gbc.gridx = 1; gbc.gridwidth = 1;
            searchPanel.add(textSearch, gbc);

            gbc.gridx = 2;
            searchPanel.add(phoneSearch, gbc);

            gbc.gridx = 3;
            searchPanel.add(contactSearch, gbc);

            gbc.gridx = 4;
            searchPanel.add(searchButton2, gbc);

            panelBuscar.add(searchPanel, BorderLayout.NORTH);
        	
            JPanel messagesPanel = new JPanel();
            messagesPanel.setLayout(new BoxLayout(messagesPanel, BoxLayout.Y_AXIS));
            JScrollPane scrollPane = new JScrollPane(messagesPanel);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            panelBuscar.add(scrollPane, BorderLayout.CENTER);
        });
        
        premiumButton.addActionListener(ev -> {
        	JOptionPane.showConfirmDialog(null, "¿Quieres hacerte Premium socio?");
        });
        
        contactsButton.addActionListener(ev -> {
        	JFrame panelContactos = new JFrame();
        	panelContactos.setSize(500,500);
        	panelContactos.setLocationRelativeTo(null);
        	panelContactos.setLayout(new BorderLayout());
        	panelContactos.setVisible(true);
        	
        	JPanel principalContactos = new JPanel(new GridLayout(1, 3, 10, 10));
        	// Panel izquierdo - Lista de contactos
        	
        	String[] chatsRecientes = { "Chat con Piter", "Chat con Maria", "Chat con Pedro", "Chat con Pedro", "Chat con Pedro", "Chat con Pedro", "Chat con Pedro", "Chat con Pedro", "Chat con Pedro", "Chat con Pedro", "Chat con Pedro","Chat con Piter", "Chat con Maria", "Chat con Pedro", "Chat con Pedro", "Chat con Pedro", "Chat con Pedro", "Chat con Pedro", "Chat con Pedro", "Chat con Pedro", "Chat con Pedro", "Chat con Pedro","Chat con Piter", "Chat con Maria", "Chat con Pedro", "Chat con Pedro", "Chat con Pedro", "Chat con Pedro", "Chat con Pedro", "Chat con Pedro", "Chat con Pedro", "Chat con Pedro", "Chat con Pedro" ,"Chat con Piter", "Chat con Maria", "Chat con Pedro", "Chat con Pedro", "Chat con Pedro", "Chat con Pedro", "Chat con Pedro", "Chat con Pedro", "Chat con Pedro", "Chat con Pedro", "Chat con Pedro" }; // Ejemplo
            JList<String> listaChats = new JList<>(chatsRecientes);
            JList<String> listaChats2 = new JList<>(chatsRecientes);
            
            JScrollPane contactScrollPane = new JScrollPane(listaChats);
            JPanel leftPanel = new JPanel(new BorderLayout());
            leftPanel.setBorder(BorderFactory.createTitledBorder("Lista Contactos"));
            leftPanel.add(contactScrollPane, BorderLayout.CENTER);
            JButton addContactButton = new JButton("Añadir Contacto");
            leftPanel.add(addContactButton, BorderLayout.SOUTH);
            
            JPanel centerPanel = new JPanel(new GridLayout(2, 1, 5, 5));
            JButton moveRightButton = new JButton(">>");
            JButton moveLeftButton = new JButton("<<");
            centerPanel.add(moveRightButton);
            centerPanel.add(moveLeftButton);
            
         // Panel derecho - Lista de grupos
            JScrollPane groupScrollPane = new JScrollPane(listaChats2);
            JPanel rightPanel = new JPanel(new BorderLayout());
            rightPanel.setBorder(BorderFactory.createTitledBorder("Grupo1"));
            rightPanel.add(groupScrollPane, BorderLayout.CENTER);
            JButton addGroupButton = new JButton("Añadir Grupo");
            rightPanel.add(addGroupButton, BorderLayout.SOUTH);
            
            principalContactos.add(rightPanel);
            principalContactos.add(leftPanel);
            principalContactos.add(centerPanel);
        	panelContactos.add(principalContactos);
        	
        	addContactButton.addActionListener(ev1 -> {
        		JFrame addContact = new JFrame();
        		addContact.setSize(350,200);
        		addContact.setLocationRelativeTo(null);
        		addContact.setLayout(new BorderLayout());
        		addContact.setVisible(true);
        		
        		JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        		JLabel nameLabel = new JLabel("Nombre:");
        	    JTextField nameField = new JTextField();

        	    JLabel phoneLabel = new JLabel("Teléfono:");
        	    JTextField phoneField = new JTextField();

        	    panel.add(nameLabel);
        	    panel.add(nameField);
        	    panel.add(phoneLabel);
        	    panel.add(phoneField);
        		
        	    addContact.add(panel, BorderLayout.CENTER);
        	    
        	    JPanel buttonPanel = new JPanel();
                JButton acceptButton = new JButton("Aceptar");
                JButton cancelButton = new JButton("Cancelar");
                
                buttonPanel.add(acceptButton);
                buttonPanel.add(cancelButton);
                addContact.add(buttonPanel, BorderLayout.SOUTH);
                
                acceptButton.addActionListener(ev11 -> {
                        if (phoneField.getText().trim().isEmpty()) {
                            JOptionPane.showMessageDialog(addContact, "El teléfono indicado no existe.", "Error", JOptionPane.WARNING_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(null, "Contacto añadido con éxito");
                        }
                });

                cancelButton.addActionListener(e -> addContact.dispose());
                
        	});
        	
        	
        	
        });
        
        // Crear panel de la izquierda
        JPanel panelIzquierda = new JPanel();
        panelContenidos.add(panelIzquierda, BorderLayout.WEST);
        panelIzquierda.setPreferredSize(new Dimension(300, getHeight()));
        panelIzquierda.setBorder(BorderFactory.createTitledBorder("Chats"));
        
        String[] chatsRecientes = { "Chat con Piter", "Chat con Maria", "Chat con Pedro", "Chat con Pedro", "Chat con Pedro", "Chat con Pedro", "Chat con Pedro", "Chat con Pedro", "Chat con Pedro", "Chat con Pedro", "Chat con Pedro","Chat con Piter", "Chat con Maria", "Chat con Pedro", "Chat con Pedro", "Chat con Pedro", "Chat con Pedro", "Chat con Pedro", "Chat con Pedro", "Chat con Pedro", "Chat con Pedro", "Chat con Pedro","Chat con Piter", "Chat con Maria", "Chat con Pedro", "Chat con Pedro", "Chat con Pedro", "Chat con Pedro", "Chat con Pedro", "Chat con Pedro", "Chat con Pedro", "Chat con Pedro", "Chat con Pedro" ,"Chat con Piter", "Chat con Maria", "Chat con Pedro", "Chat con Pedro", "Chat con Pedro", "Chat con Pedro", "Chat con Pedro", "Chat con Pedro", "Chat con Pedro", "Chat con Pedro", "Chat con Pedro" }; // Ejemplo
        JList<String> listaChats = new JList<>(chatsRecientes);
        listaChats.setBackground(new Color(245, 245, 245)); // Color de fondo claro
        //listaChats.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // SÃ³lo una selecciÃ³n
                
        JScrollPane scrollPane = new JScrollPane(listaChats);
        panelIzquierda.add(scrollPane);
        scrollPane.setPreferredSize(new Dimension(280, 500)); // Ajusta el tamaÃ±o de la lista
        
        
        
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
        burbuja = new BubbleText(chat, "Hola su florentineza",Color.GREEN, "J.Ramón", BubbleText.SENT);
        chat.add(burbuja);
        
        BubbleText burbuja2;
        burbuja2=new BubbleText(chat, "Hola, ¿Está seguro de que la burbuja usa varias lineas si es necesario?", Color.LIGHT_GRAY, "Alumno", BubbleText.RECEIVED);
        chat.add(burbuja2);
        
        BubbleText burbuja3;
        burbuja3 = new BubbleText(chat, "Hola florentineza",Color.GREEN, "J.Ramón", BubbleText.SENT);
        chat.add(burbuja3);
        
        BubbleText burbuja4;
        burbuja4=new BubbleText(chat, "Hola, ¿ seguro de que la burbuja usa varias lineas si es necesario?", Color.LIGHT_GRAY, "Alumno", BubbleText.RECEIVED);
        chat.add(burbuja4);
        
        BubbleText burbuja5;
        burbuja5 = new BubbleText(chat, "Hola florentineza",Color.GREEN, "J.Ramón", BubbleText.SENT);
        chat.add(burbuja5);
        
        BubbleText burbuja6;
        burbuja6=new BubbleText(chat, "Hola, ¿ seguro de que la burbuja usa varias lineas si es necesario?", Color.LIGHT_GRAY, "Alumno", BubbleText.RECEIVED);
        chat.add(burbuja6);
        
        BubbleText burbuja7;
        burbuja7 = new BubbleText(chat, "Hola florentineza",Color.GREEN, "J.Ramón", BubbleText.SENT);
        chat.add(burbuja7);
        
        BubbleText burbuja8;
        burbuja8=new BubbleText(chat, "Hola, ¿ seguro de que la burbuja usa varias lineas si es necesario?", Color.LIGHT_GRAY, "Alumno", BubbleText.RECEIVED);
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
