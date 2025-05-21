package umu.tds.app.ventanas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.logging.Logger;

import tds.BubbleText;
import umu.tds.app.AppChat.Contacto;
import umu.tds.app.AppChat.ContactoIndividual;
import umu.tds.app.AppChat.Controlador;
import umu.tds.app.AppChat.ObserverChats;
import umu.tds.app.AppChat.Theme;
import umu.tds.app.AppChat.Usuario;

public class VentanaChatActual extends JPanel implements ObserverChats {
    private static final long serialVersionUID = 1L;
    private JPanel chatPanel;
    private JScrollPane chatScrollPane;
    private JTextField messageInput;
    private Contacto contactoActual;
    private Controlador controlador;
    private JLabel contactLabel;
    private JButton btnAgregarContacto;
    private static final Logger LOGGER = Logger.getLogger(VentanaChatActual.class.getName());
    private boolean isSending = false;

    public VentanaChatActual() {

        controlador = Controlador.getInstancia();
        controlador.addObserverChats(this);
        setLayout(new BorderLayout());
        setBackground(Theme.COLOR_CHAT_BACKGROUND);

        // Contenedor principal del área de chat
        JPanel panelInterno = new JPanel(new BorderLayout());
        panelInterno.setBackground(Theme.COLOR_CHAT_BACKGROUND);
        add(panelInterno, BorderLayout.CENTER);

        // Panel superior con etiqueta del nombre del contacto
        contactLabel = new JLabel("Seleccione un contacto", SwingConstants.LEFT);
        contactLabel.setFont(Theme.FONT_BOLD_LARGE);
        contactLabel.setForeground(Theme.COLOR_TEXTO);
        contactLabel.setOpaque(true);
        contactLabel.setBackground(Theme.COLOR_HEADER);
        contactLabel.setBorder(BorderFactory.createEmptyBorder(Theme.PADDING_MEDIUM, Theme.PADDING_MEDIUM, Theme.PADDING_MEDIUM, 0));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Theme.COLOR_HEADER);
        topPanel.add(contactLabel, BorderLayout.WEST);
        panelInterno.add(topPanel, BorderLayout.NORTH);

        // Botón "Añadir a contactos"
        JPanel contactoPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        contactoPanel.setBackground(Theme.COLOR_CHAT_BACKGROUND);
        contactoPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        btnAgregarContacto = new JButton("Añadir a contactos");
        btnAgregarContacto.setVisible(false);
        btnAgregarContacto.setBorderPainted(false);
        btnAgregarContacto.setFocusPainted(false);
        btnAgregarContacto.setContentAreaFilled(false);
        btnAgregarContacto.setForeground(Color.BLUE.darker());
        btnAgregarContacto.setFont(Theme.FONT_PLAIN_MEDIUM);
        btnAgregarContacto.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnAgregarContacto.addActionListener(e -> agregarContactoDesconocido());
        contactoPanel.add(btnAgregarContacto);

        // Panel de mensajes reales
        chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
        chatPanel.setBackground(Theme.COLOR_CHAT_BACKGROUND);

        // Envoltorio con el botón + mensajes
        JPanel chatWrapper = new JPanel();
        chatWrapper.setLayout(new BoxLayout(chatWrapper, BoxLayout.Y_AXIS));
        chatWrapper.setBackground(Theme.COLOR_CHAT_BACKGROUND);
        chatWrapper.add(contactoPanel);  // botón arriba
        chatWrapper.add(chatPanel);      // mensajes debajo

        // Scroll con todo dentro
        chatScrollPane = new JScrollPane(chatWrapper);
        chatScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        chatScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        chatScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        chatScrollPane.setBorder(BorderFactory.createEmptyBorder(Theme.PADDING_SMALL, Theme.PADDING_SMALL, Theme.PADDING_SMALL, Theme.PADDING_SMALL));
        panelInterno.add(chatScrollPane, BorderLayout.CENTER);

        // Panel inferior con input + enviar + emoji
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(Theme.PADDING_SMALL, Theme.PADDING_SMALL, Theme.PADDING_SMALL, Theme.PADDING_SMALL));
        bottomPanel.setBackground(Theme.COLOR_CHAT_BACKGROUND);

        // Botón emoji
        JButton emojiButton = new JButton("😀");
        emojiButton.setPreferredSize(new Dimension(40, 30));
        emojiButton.setFocusPainted(false);
        emojiButton.setBackground(Theme.COLOR_SECUNDARIO);
        emojiButton.setFont(Theme.FONT_BOLD_MEDIUM);

        // Menú emergente con emoticonos
        JPopupMenu emojiMenu = new JPopupMenu();
        for (int i = 0; i < 8; i++) {
            ImageIcon icon = BubbleText.getEmoji(i);
            JMenuItem item = new JMenuItem(icon);
            final int emojiId = i;
            item.addActionListener(e -> {
                messageInput.setText("emoji:" + emojiId);
                emojiMenu.setVisible(false);
            });
            emojiMenu.add(item);
        }

        emojiButton.addActionListener(e -> {
            emojiMenu.show(emojiButton, 0, emojiButton.getHeight());
        });

        bottomPanel.add(emojiButton, BorderLayout.WEST);

        // Campo de entrada de texto
        messageInput = new JTextField();
        messageInput.setFont(Theme.FONT_PLAIN_MEDIUM);
        messageInput.setBackground(Theme.COLOR_SECUNDARIO);
        messageInput.setForeground(Theme.COLOR_MESSAGE_TEXT);
        messageInput.setPreferredSize(new Dimension(0, 30));
        messageInput.addActionListener(new SendButtonListener());
        bottomPanel.add(messageInput, BorderLayout.CENTER);

        // Botón Enviar
        JButton sendButton = new JButton("Enviar");
        sendButton.setBackground(Theme.COLOR_HOVER);
        sendButton.setForeground(Theme.COLOR_TEXTO);
        sendButton.setFont(Theme.FONT_BOLD_MEDIUM);
        sendButton.setFocusPainted(false);
        sendButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.COLOR_ACENTO, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        sendButton.addActionListener(new SendButtonListener());
        bottomPanel.add(sendButton, BorderLayout.EAST);

        panelInterno.add(bottomPanel, BorderLayout.SOUTH);
        SwingUtilities.invokeLater(() -> {
            contactoActual = controlador.getContactoActual();
            updateChat();
        });
    }


    @Override
    public void updateChatsRecientes(String[] chatsRecientes) {}

    @Override
    public void updateContactoActual(Contacto contacto) {
        LOGGER.info("updateContactoActual llamado con: " + (contacto != null ? contacto.getNombre() : "null"));
        contactoActual = contacto;
        updateChat();
        boolean esContacto = controlador.getUsuarioActual() != null && controlador.getUsuarioActual().getContactos().stream()
            .filter(c -> c instanceof ContactoIndividual)
            .map(c -> ((ContactoIndividual) c).getTelefono())
            .anyMatch(movil -> contactoActual instanceof ContactoIndividual actual && movil.equals(actual.getTelefono()));
        btnAgregarContacto.setVisible(contactoActual instanceof ContactoIndividual && !esContacto);
    }

    public void updateChat() {
        chatPanel.removeAll();

        if (contactoActual != null) {
            contactLabel.setText("Chat con " + contactoActual.getNombre());
            List<String> mensajes = controlador.obtenerMensajes(contactoActual);
            if (mensajes.isEmpty()) {
                JLabel vacio = new JLabel("No hay mensajes en esta conversación.");
                vacio.setFont(Theme.FONT_PLAIN_MEDIUM);
                vacio.setForeground(Theme.COLOR_TEXTO);
                vacio.setAlignmentX(Component.CENTER_ALIGNMENT);
                chatPanel.add(Box.createVerticalStrut(20));
                chatPanel.add(vacio);
            } else {
                for (String mensaje : mensajes) {
                    if (mensaje.startsWith("Tú: ")) {
                        addMessageBubble(mensaje.substring(4), Theme.COLOR_BUBBLE_SENT, controlador.getNombreUserActual(), BubbleText.SENT);
                    } else {
                        String autor = mensaje.substring(0, mensaje.indexOf(": "));
                        String texto = mensaje.substring(mensaje.indexOf(": ") + 2);
                        addMessageBubble(texto, Theme.COLOR_BUBBLE_RECEIVED, autor, BubbleText.RECEIVED);
                    }
                }
            }
        } else {
            contactLabel.setText("Seleccione un contacto");
            chatPanel.setBackground(Color.LIGHT_GRAY);
            JLabel noContacto = new JLabel("Seleccione un contacto para comenzar", SwingConstants.CENTER);
            noContacto.setFont(Theme.FONT_PLAIN_MEDIUM);
            noContacto.setForeground(Theme.COLOR_TEXTO);
            chatPanel.add(Box.createVerticalGlue());
            chatPanel.add(noContacto);
            chatPanel.add(Box.createVerticalGlue());
        }

        chatPanel.revalidate();
        chatPanel.repaint();

        SwingUtilities.invokeLater(() -> {
            JScrollBar verticalBar = chatScrollPane.getVerticalScrollBar();
            verticalBar.setValue(verticalBar.getMaximum());
        });
    }
    private void addMessageBubble(String message, Color color, String author, int type) {
        chatPanel.add(Box.createVerticalStrut(5));

        BubbleText bubble;
        if (message.startsWith("emoji:")) {
            try {
                int emojiId = Integer.parseInt(message.substring(6));
                bubble = new BubbleText(chatPanel, emojiId, color, author, type, 18); // 18 es el tamaño sugerido
            } catch (NumberFormatException e) {
                bubble = new BubbleText(chatPanel, message, color, author, type);
            }
        } else {
            bubble = new BubbleText(chatPanel, message, color, author, type);
        }

        bubble.setForeground(Theme.COLOR_MESSAGE_TEXT);
        chatPanel.add(bubble);
    }


    private class SendButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (isSending) return;
            if (contactoActual != null && !messageInput.getText().trim().isEmpty()) {
                isSending = true;
                messageInput.setEnabled(false);
                String mensaje = messageInput.getText().trim();
                LOGGER.info("Enviando mensaje a " + contactoActual.getNombre() + ": " + mensaje);
                SwingUtilities.invokeLater(() -> {
                    try {
                        controlador.enviarMensaje(contactoActual, mensaje);
                        messageInput.setText("");
                    } finally {
                        isSending = false;
                        messageInput.setEnabled(true);
                        messageInput.requestFocusInWindow();
                    }
                });
            } else {
                JOptionPane.showMessageDialog(VentanaChatActual.this,
                    "Por favor, seleccione un contacto y escriba un mensaje",
                    "Error", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private void agregarContactoDesconocido() {
        if (!(contactoActual instanceof ContactoIndividual actual)) return;
        Usuario usuarioDesconocido = actual.getUsuario();
        if (usuarioDesconocido == null) return;

        String nombre = JOptionPane.showInputDialog(this, "Nombre para el nuevo contacto:", "Añadir Contacto", JOptionPane.PLAIN_MESSAGE);
        if (nombre != null && !nombre.trim().isEmpty()) {
            ContactoIndividual nuevo = new ContactoIndividual(nombre.trim(), controlador.generarCodigoContacto(), usuarioDesconocido.getTelefono(), usuarioDesconocido);
            if (controlador.nuevoContacto(nuevo)) {
                JOptionPane.showMessageDialog(this, "Contacto añadido correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                btnAgregarContacto.setVisible(false);
            } else {
                JOptionPane.showMessageDialog(this, "Ese contacto ya existe", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
