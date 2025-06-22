package umu.tds.app.ventanas;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import tds.BubbleText;
import umu.tds.app.AppChat.*;
/**
 * Panel de interfaz gráfica que representa el chat activo entre el usuario actual y un contacto (individual o grupo).
 * Permite visualizar mensajes, enviar nuevos mensajes y gestionar la imagen de grupo o agregar contactos desconocidos.
 */
public class VentanaChatActual extends JPanel implements ObserverChats {
    private static final long serialVersionUID = 1L;
    private JPanel chatPanel;
    private JScrollPane chatScrollPane;
    private JTextField messageInput;
    private Contacto contactoActual;
    private Controlador controlador;
    private JLabel contactLabel;
    private JLabel contactImage;
    private JButton btnAgregarContacto;
    private static final int DISPLAY_IMAGE_SIZE = 50;
    private boolean isSending = false;

    /**
     * Constructor que inicializa la interfaz del panel de chat.
     */
    public VentanaChatActual() {
        controlador = Controlador.getInstancia();
        controlador.addObserverChats(this);
        setLayout(new BorderLayout());
        setBackground(Theme.COLOR_CHAT_BACKGROUND);

        JPanel panelInterno = new JPanel(new BorderLayout());
        panelInterno.setBackground(Theme.COLOR_CHAT_BACKGROUND);
        add(panelInterno, BorderLayout.CENTER);

        crearPanelSuperior(panelInterno);
        crearPanelMensajes(panelInterno);
        crearPanelInferior(panelInterno);

        //updateContactoActual(controlador.getContactoActual());
    }

    /**
     * Crea y configura la parte superior del panel de chat.
     */
    private void crearPanelSuperior(JPanel panelInterno) {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Theme.COLOR_HEADER);

        contactLabel = new JLabel("Seleccione un contacto", SwingConstants.LEFT);
        contactLabel.setFont(Theme.FONT_BOLD_LARGE);
        contactLabel.setForeground(Theme.COLOR_TEXTO);
        contactLabel.setOpaque(true);
        contactLabel.setBackground(Theme.COLOR_HEADER);
        contactLabel.setBorder(BorderFactory.createEmptyBorder(Theme.PADDING_MEDIUM, Theme.PADDING_MEDIUM, Theme.PADDING_MEDIUM, 0));
        topPanel.add(contactLabel, BorderLayout.CENTER);

        contactImage = new JLabel();
        contactImage.setPreferredSize(new Dimension(40, 40));
        contactImage.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        setupContactImageClickListener();
        topPanel.add(contactImage, BorderLayout.WEST);

        btnAgregarContacto = new JButton("Agregar a Contactos");
        btnAgregarContacto.setVisible(false);
        btnAgregarContacto.setBorderPainted(false);
        btnAgregarContacto.setFocusPainted(false);
        btnAgregarContacto.setContentAreaFilled(false);
        btnAgregarContacto.setForeground(Color.BLUE.darker());
        btnAgregarContacto.setFont(Theme.FONT_PLAIN_MEDIUM);
        btnAgregarContacto.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnAgregarContacto.addActionListener(e -> agregarContactoActual());
        topPanel.add(btnAgregarContacto, BorderLayout.EAST);

        panelInterno.add(topPanel, BorderLayout.NORTH);
    }

    /**
     * Crea el panel donde se visualizarán los mensajes del chat.
     */
    private void crearPanelMensajes(JPanel panelInterno) {
        chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
        chatPanel.setBackground(Theme.COLOR_CHAT_BACKGROUND);

        chatScrollPane = new JScrollPane(chatPanel);
        chatScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        chatScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        chatScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        chatScrollPane.setBorder(null);
        panelInterno.add(chatScrollPane, BorderLayout.CENTER);
    }

    /**
     * Crea el panel inferior con el campo de entrada de mensajes y el botón de envío.
     */
    private void crearPanelInferior(JPanel panelInterno) {
        JPanel bottomPanel = new JPanel(new BorderLayout(5, 0));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(Theme.PADDING_SMALL, Theme.PADDING_SMALL, Theme.PADDING_SMALL, Theme.PADDING_SMALL));
        bottomPanel.setBackground(Theme.COLOR_CHAT_BACKGROUND);

        JButton emojiButton = new JButton("😀");
        emojiButton.setPreferredSize(new Dimension(40, 30));
        JPopupMenu emojiMenu = createEmojiMenu();
        emojiButton.addActionListener(e -> emojiMenu.show(emojiButton, 0, -emojiMenu.getPreferredSize().height));
        bottomPanel.add(emojiButton, BorderLayout.WEST);

        messageInput = new JTextField();
        messageInput.setFont(Theme.FONT_PLAIN_MEDIUM);
        messageInput.addActionListener(new SendButtonListener());
        bottomPanel.add(messageInput, BorderLayout.CENTER);

        JButton sendButton = new JButton("Enviar");
        sendButton.addActionListener(new SendButtonListener());
        bottomPanel.add(sendButton, BorderLayout.EAST);

        panelInterno.add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * Recibe actualizaciones del contacto activo y refresca la vista del chat.
     */
    @Override
    public void updateContactoActual(Contacto contacto) {
        this.contactoActual = contacto;
        updateChatAppearance();
        updateChatMessages();
    }

    @Override
    public void updateChatsRecientes(String[] chatsRecientes) {
        // No usado en esta clase
    }

    /**
     * Actualiza el título y la imagen del contacto actual.
     */
    private void updateChatAppearance() {
        contactImage.setIcon(null);
        contactImage.setBorder(null);
        if (contactoActual == null) {
            contactLabel.setText("Seleccione un chat");
            btnAgregarContacto.setVisible(false);
        } else {
            if (contactoActual instanceof ContactoIndividual ci) {
                String displayName = ci.isDesconocido() ? ci.getTelefono() : ci.getNombre();
                contactLabel.setText(displayName);
                btnAgregarContacto.setVisible(ci.isDesconocido());

                Usuario usuarioAsociado = controlador.buscarUsuarioPorTelefono(ci.getTelefono());
                if (usuarioAsociado != null && usuarioAsociado.getFoto() != null) {
                    contactImage.setIcon(getScaledIcon(usuarioAsociado.getFoto(), DISPLAY_IMAGE_SIZE));
                }
            } else if (contactoActual instanceof Grupo grupo) {
                contactLabel.setText(grupo.getNombre());
                btnAgregarContacto.setVisible(false);
                String urlFoto = grupo.getUrlFoto();
                if (urlFoto != null && !urlFoto.isBlank()) {
                    try {
                        BufferedImage image = ImageIO.read(new URI(urlFoto.trim()).toURL());
                        ImageIcon icon = new ImageIcon(image);
                        contactImage.setIcon(getScaledIcon(icon, DISPLAY_IMAGE_SIZE));
                    } catch (IOException | URISyntaxException e) {
                        contactImage.setIcon(null);
                        contactImage.setBorder(null);
                    }
                }
            }
        }
    }

    /**
     * Actualiza la lista de mensajes mostrada en pantalla.
     */
    private void updateChatMessages() {
        chatPanel.removeAll();

        if (contactoActual != null) {
            // Obtenemos la lista de mensajes del contacto actual, ya ordenada.
            List<Mensaje> mensajes = controlador.getMensajes(contactoActual);

            if (mensajes.isEmpty()) {
                JLabel vacio = new JLabel("No hay mensajes en esta conversación.");
                vacio.setFont(Theme.FONT_PLAIN_MEDIUM);
                vacio.setForeground(Theme.COLOR_TEXTO);
                vacio.setAlignmentX(Component.CENTER_ALIGNMENT);
                chatPanel.add(Box.createVerticalStrut(20));
                chatPanel.add(vacio);
            } else {

                for (Mensaje msg : mensajes) {
                    boolean enviadoPorMi = msg.getEmisor().equals(controlador.getUsuarioActual());
                    
                    // --- Lógica para obtener el nombre del emisor ---
                    String nombreEmisor;
                    if (enviadoPorMi) {
                        nombreEmisor = "Tú";
                    } else {
                        // Si el mensaje es recibido, buscamos cómo el usuario actual ve a este contacto.
                        // El `contactoActual` ya es la representación local de ese contacto.
                        
                        if (contactoActual instanceof ContactoIndividual) {
                            ContactoIndividual contactoIndividual = (ContactoIndividual) contactoActual;
                            // Si es desconocido, usamos su teléfono. Si no, su nombre (el alias local).
                            nombreEmisor = contactoIndividual.isDesconocido() 
                                         ? contactoIndividual.getTelefono() 
                                         : contactoIndividual.getNombre();
                        } else {
                            // Para grupos u otros tipos, usamos el nombre del contacto actual (el nombre del grupo).
                            // Y dentro del grupo, usamos el nombre real del emisor.
                            nombreEmisor = msg.getEmisor().getNombre() + " (" + contactoActual.getNombre() + ")";
                        }
                    }
                   
                    String horaFormateada = msg.getHora().format(DateTimeFormatter.ofPattern("HH:mm"));
                    String textoConHora = msg.getTexto() + "\n\n" + horaFormateada;
                    
                    // Añadimos la burbuja de mensaje con la información correcta.
                    addMessageBubble(
                    	msg.getTexto(), // Usamos el texto con HTML para la hora
                        enviadoPorMi ? Theme.COLOR_BUBBLE_SENT : Theme.COLOR_BUBBLE_RECEIVED, 
                        nombreEmisor, // Usamos el nombre que acabamos de calcular
                        enviadoPorMi ? BubbleText.SENT : BubbleText.RECEIVED
                    );
                }
                chatPanel.add(Box.createVerticalStrut(10)); // pequeño espacio final
            }
        }
        
        chatPanel.revalidate();
        chatPanel.repaint();
        scrollToBottom();
    }
    /**
     * Agrega el contacto desconocido a la lista de contactos conocidos.
     */
    private void agregarContactoActual() {
        if (!(contactoActual instanceof ContactoIndividual ci) || !ci.isDesconocido()) return;

        String nuevoNombre = JOptionPane.showInputDialog(
            this,
            "Introduce un nombre para el contacto con teléfono:\n" + ci.getTelefono(),
            "Agregar Nuevo Contacto",
            JOptionPane.PLAIN_MESSAGE
        );

        if (nuevoNombre != null && !nuevoNombre.trim().isEmpty()) {
            controlador.registrarContactoDesconocido(ci, nuevoNombre.trim());
        }
    }

    private class SendButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (isSending || contactoActual == null || messageInput.getText().trim().isEmpty()) return;

            isSending = true;
            try {
                String mensaje = messageInput.getText().trim();
                if (contactoActual instanceof Grupo grupo) {
                    controlador.enviarMensajeAGrupo(grupo, mensaje);
                } else if (contactoActual instanceof ContactoIndividual contactoIndividual) {
                    controlador.enviarMensaje(contactoIndividual, mensaje);
                }
                messageInput.setText("");
            } finally {
                isSending = false;
                messageInput.requestFocusInWindow();
            }
        }
    }

    private void setupContactImageClickListener() {
        contactImage.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        contactImage.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (contactoActual instanceof Grupo grupo) {
                    String nuevaUrl = JOptionPane.showInputDialog(
                        VentanaChatActual.this,
                        "Introduce la nueva URL de imagen para el grupo:",
                        grupo.getUrlFoto() != null ? grupo.getUrlFoto() : "");

                    if (nuevaUrl != null && !nuevaUrl.isBlank()) {
                        try {
                            BufferedImage image = ImageIO.read(new URI(nuevaUrl.trim()).toURL());
                            ImageIcon nuevaIcon = new ImageIcon(image);
                            contactImage.setIcon(getScaledIcon(nuevaIcon, DISPLAY_IMAGE_SIZE));
                            grupo.setUrlFoto(nuevaUrl);
                            grupo.setFoto(nuevaIcon);
                            controlador.modificarGrupo(grupo);
                            JOptionPane.showMessageDialog(VentanaChatActual.this, "Imagen actualizada", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(VentanaChatActual.this, "No se pudo cargar la imagen", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        });
    }

    private JPopupMenu createEmojiMenu() {
        JPopupMenu emojiMenu = new JPopupMenu();
        for (int i = 0; i < 8; i++) {
            final int emojiId = i;
            JMenuItem item = new JMenuItem(BubbleText.getEmoji(i));
            item.addActionListener(e -> messageInput.setText("emoji:" + emojiId));
            emojiMenu.add(item);
        }
        return emojiMenu;
    }

    private void addMessageBubble(String message, Color color, String author, int type) {
        chatPanel.add(Box.createVerticalStrut(5));
        BubbleText bubble = message.startsWith("emoji:")
            ? new BubbleText(chatPanel, Integer.parseInt(message.substring(6)), color, author, type, 18)
            : new BubbleText(chatPanel, message, color, author, type);
        chatPanel.add(bubble);
    }

    private void scrollToBottom() {
        SwingUtilities.invokeLater(() -> {
            JScrollBar verticalBar = chatScrollPane.getVerticalScrollBar();
            verticalBar.setValue(verticalBar.getMaximum());
        });
    }

    private ImageIcon getScaledIcon(ImageIcon srcIcon, int size) {
        if (srcIcon == null || srcIcon.getImage() == null) return new ImageIcon();
        Image scaledImage = srcIcon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }
}
