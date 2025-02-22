package umu.tds.app.ventanas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import tds.BubbleText;
import umu.tds.app.AppChat.Contacto;
import umu.tds.app.AppChat.ContactoIndividual;
import umu.tds.app.AppChat.Controlador;
import umu.tds.app.AppChat.Observer;

public class VentanaPrincipal extends JFrame implements Observer {
    private static final long serialVersionUID = 1L;
    private static final int WINDOW_WIDTH = 900;
    private static final int WINDOW_HEIGHT = 650;
    private JPanel contentPanel;
    private Controlador controlador;
    private Contacto contactoActual;
    private JTextField phoneInput;
    private JPanel chatPanel;
    private JScrollPane chatScrollPane;
    private JList<String> chatList; // Referencia al JList de chats recientes

    private final Color BACKGROUND_COLOR = new Color(41, 128, 185);

    public VentanaPrincipal() {
        controlador = Controlador.getInstancia();
        controlador.addObserver(this); // Registrarse como observador
        initializeUI();
    }

    private void initializeUI() {
        setTitle("ParabarApp");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);

        contentPanel = new JPanel(new BorderLayout());
        setContentPane(contentPanel);

        contentPanel.add(configureNorthPanel(), BorderLayout.NORTH);
        contentPanel.add(configureLeftPanel(), BorderLayout.WEST);
        contentPanel.add(configureRightPanel(), BorderLayout.CENTER);

        pack();
        setVisible(true);
    }

    private JPanel configureNorthPanel() {
        JPanel northPanel = new JPanel();
        northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));

        JPanel optionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        List<ContactoIndividual> contactosList = controlador.obtenerContactos();
        JComboBox<String> contactos = new JComboBox<>(contactosList.stream()
                .map(Contacto::getNombre)
                .toArray(String[]::new));
        contactos.insertItemAt("Seleccione un contacto...", 0);
        contactos.setSelectedIndex(0);
        contactos.addActionListener(e -> {
            String selected = (String) contactos.getSelectedItem();
            contactoActual = "Seleccione un contacto...".equals(selected) ? null :
                    contactosList.stream().filter(c -> c.getNombre().equals(selected)).findFirst().orElse(null);
            updateChatPanel(); // Actualizar el chat al cambiar contacto
        });

        phoneInput = new JTextField(10);
        phoneInput.setToolTipText("Escribe un mensaje...");
        JButton sendButton = new JButton("Enviar");
        sendButton.addActionListener(new SendButtonListener());
        JButton searchButton = new JButton("Buscar");
        searchButton.addActionListener(e -> new VentanaBusqueda(this));
        JButton contactsButton = new JButton("Contactos");
        contactsButton.addActionListener(e -> new VentanaContactos(this));
        JButton premiumButton = new JButton("Premium");
        premiumButton.addActionListener(e -> JOptionPane.showConfirmDialog(this, "¿Quieres hacerte Premium socio?"));

        JLabel userLabel = new JLabel(controlador.getNombreUserActual());
        JLabel userImage = new JLabel(controlador.getIconoUserActual());

        optionsPanel.add(contactos);
        optionsPanel.add(phoneInput);
        optionsPanel.add(sendButton);
        optionsPanel.add(searchButton);
        optionsPanel.add(contactsButton);
        optionsPanel.add(premiumButton);
        optionsPanel.add(userLabel);
        optionsPanel.add(userImage);

        northPanel.add(optionsPanel);
        return northPanel;
    }

    private JPanel configureLeftPanel() {
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(300, WINDOW_HEIGHT));
        leftPanel.setBorder(BorderFactory.createTitledBorder("Chats"));

        String[] recentChats = controlador.getChatsRecientes();
        chatList = new JList<>(recentChats);
        chatList.setBackground(new Color(245, 245, 245));
        JScrollPane scrollPane = new JScrollPane(chatList);
        scrollPane.setPreferredSize(new Dimension(280, 500));

        leftPanel.add(scrollPane, BorderLayout.CENTER);
        return leftPanel;
    }

    private JPanel configureRightPanel() {
        chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
        chatPanel.setBorder(BorderFactory.createTitledBorder("Mensajes"));
        chatPanel.setPreferredSize(new Dimension(400, 500));

        chatScrollPane = new JScrollPane(chatPanel);
        chatScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        chatScrollPane.getVerticalScrollBar().setUnitIncrement(16);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(chatScrollPane, BorderLayout.CENTER);
        return rightPanel;
    }

    private void addMessageBubble(String message, Color color, String author, int type) {
        BubbleText bubble = new BubbleText(chatPanel, message, color, author, type);
        chatPanel.add(bubble);
        chatPanel.revalidate();
        chatPanel.repaint();

        SwingUtilities.invokeLater(() -> {
            JScrollBar verticalBar = chatScrollPane.getVerticalScrollBar();
            verticalBar.setValue(verticalBar.getMaximum());
        });
    }

    private void updateChatPanel() {
        chatPanel.removeAll(); // Limpiar el panel de mensajes
        if (contactoActual != null) {
            chatPanel.setBorder(BorderFactory.createTitledBorder("Mensajes con " + contactoActual.getNombre()));
            List<String> mensajes = controlador.obtenerMensajes(contactoActual);
            for (String mensaje : mensajes) {
                if (mensaje.startsWith("Tú: ")) {
                    addMessageBubble(mensaje.substring(4), Color.GREEN, "J.Ramón", BubbleText.SENT);
                } else {
                    addMessageBubble(mensaje, Color.LIGHT_GRAY, contactoActual.getNombre(), BubbleText.RECEIVED);
                }
            }
        } else {
            chatPanel.setBorder(BorderFactory.createTitledBorder("Mensajes"));
        }
        chatPanel.revalidate();
        chatPanel.repaint();
    }

    @Override
    public void updateChatsRecientes(String[] chatsRecientes) {
        chatList.setListData(chatsRecientes); // Actualizar la lista de chats recientes
        chatList.revalidate();
        chatList.repaint();
    }

    private class SendButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (contactoActual != null && !phoneInput.getText().trim().isEmpty()) {
                controlador.enviarMensaje(contactoActual, phoneInput.getText());
                addMessageBubble(phoneInput.getText(), Color.GREEN, "J.Ramón", BubbleText.SENT);
                phoneInput.setText("");
            } else {
                JOptionPane.showMessageDialog(VentanaPrincipal.this,
                        "Por favor, seleccione un contacto y escriba un mensaje",
                        "Error", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VentanaPrincipal());
    }
}