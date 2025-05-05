package umu.tds.app.ventanas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import tds.BubbleText;
import umu.tds.app.AppChat.Contacto;
import umu.tds.app.AppChat.Controlador;
import umu.tds.app.AppChat.ObserverChats;
import umu.tds.app.AppChat.Theme;

public class VentanaChatActual extends JPanel implements ObserverChats {
    private static final long serialVersionUID = 1L;
    private JPanel chatPanel;
    private JScrollPane chatScrollPane;
    private JTextField messageInput;
    private Contacto contactoActual;
    private Controlador controlador;
    private JLabel contactLabel;

    public VentanaChatActual() {
        controlador = Controlador.getInstancia();
        controlador.addObserverChats(this);
        setLayout(new BorderLayout());
        setBackground(Theme.COLOR_CHAT_BACKGROUND);

        contactLabel = new JLabel("Seleccione un contacto", SwingConstants.CENTER);
        contactLabel.setFont(Theme.FONT_BOLD_LARGE);
        contactLabel.setForeground(Theme.COLOR_TEXTO);
        contactLabel.setOpaque(true);
        contactLabel.setBackground(Theme.COLOR_HEADER);
        contactLabel.setBorder(BorderFactory.createEmptyBorder(Theme.PADDING_MEDIUM, 0, Theme.PADDING_MEDIUM, 0));
        add(contactLabel, BorderLayout.NORTH);

        chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
        chatPanel.setBackground(Theme.COLOR_CHAT_BACKGROUND);

        chatScrollPane = new JScrollPane(chatPanel);
        chatScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        chatScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        chatScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        chatScrollPane.setBorder(BorderFactory.createEmptyBorder(Theme.PADDING_SMALL, Theme.PADDING_SMALL, Theme.PADDING_SMALL, Theme.PADDING_SMALL));
        add(chatScrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(Theme.PADDING_SMALL, Theme.PADDING_SMALL, Theme.PADDING_SMALL, Theme.PADDING_SMALL));
        bottomPanel.setBackground(Theme.COLOR_CHAT_BACKGROUND);

        messageInput = new JTextField();
        messageInput.setFont(Theme.FONT_PLAIN_MEDIUM);
        messageInput.setBackground(Theme.COLOR_SECUNDARIO);
        messageInput.setForeground(Theme.COLOR_MESSAGE_TEXT);
        messageInput.setPreferredSize(new Dimension(0, 30));
        bottomPanel.add(messageInput, BorderLayout.CENTER);

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

        add(bottomPanel, BorderLayout.SOUTH);
    }

    @Override
    public void updateChatsRecientes(String[] chatsRecientes) {}

    @Override
    public void updateContactoActual(Contacto contacto) {
        contactoActual = contacto;
        updateChat();
    }

    public void updateChat() {
        chatPanel.removeAll();
        if (contactoActual != null) {
            contactLabel.setText("Chat con " + contactoActual.getNombre());
            List<String> mensajes = controlador.obtenerMensajes(contactoActual);
            for (String mensaje : mensajes) {
                if (mensaje.startsWith("Tú: ")) {
                    addMessageBubble(mensaje.substring(4), Theme.COLOR_BUBBLE_SENT, controlador.getNombreUserActual(), BubbleText.SENT);
                } else {
                    addMessageBubble(mensaje, Theme.COLOR_BUBBLE_RECEIVED, contactoActual.getNombre(), BubbleText.RECEIVED);
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
        BubbleText bubble = new BubbleText(chatPanel, message, color, author, type);
        bubble.setForeground(Theme.COLOR_MESSAGE_TEXT);
        chatPanel.add(bubble);
        chatPanel.revalidate();
        chatPanel.repaint();
        chatScrollPane.revalidate();
        chatScrollPane.repaint();

        SwingUtilities.invokeLater(() -> {
            JScrollBar verticalBar = chatScrollPane.getVerticalScrollBar();
            verticalBar.setValue(verticalBar.getMaximum());
        });
    }

    private class SendButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (contactoActual != null && !messageInput.getText().trim().isEmpty()) {
                String mensaje = messageInput.getText().trim();
                controlador.enviarMensaje(contactoActual, mensaje);
                addMessageBubble(mensaje, Theme.COLOR_BUBBLE_SENT, controlador.getNombreUserActual(), BubbleText.SENT);
                messageInput.setText("");
            } else {
                JOptionPane.showMessageDialog(VentanaChatActual.this,
                    "Por favor, seleccione un contacto y escriba un mensaje",
                    "Error", JOptionPane.WARNING_MESSAGE);
            }
        }
    }
}