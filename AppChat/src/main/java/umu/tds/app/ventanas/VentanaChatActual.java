package umu.tds.app.ventanas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import tds.BubbleText;
import umu.tds.app.AppChat.Contacto;
import umu.tds.app.AppChat.Controlador;
import umu.tds.app.AppChat.Observer;

public class VentanaChatActual extends JPanel implements Observer {
    private static final long serialVersionUID = 1L;
    private JPanel chatPanel;
    private JScrollPane chatScrollPane;
    private JTextField messageInput;
    private Contacto contactoActual;
    private Controlador controlador;
    private JLabel contactLabel;

    public VentanaChatActual() {
        controlador = Controlador.getInstancia();
        controlador.addObserver(this); // Registrarse como observador
        setLayout(new BorderLayout());

        // Encabezado con el nombre del contacto (fijo en la parte superior)
        contactLabel = new JLabel("Seleccione un contacto", SwingConstants.CENTER);
        contactLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(contactLabel, BorderLayout.NORTH);

        // Configurar el panel con tamaño preferido fijo para evitar expansión
        chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
        chatPanel.setBorder(BorderFactory.createEmptyBorder());

        chatScrollPane = new JScrollPane(chatPanel);
        chatScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        chatScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); // Evitar scroll horizontal
        chatScrollPane.getVerticalScrollBar().setUnitIncrement(16);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        messageInput = new JTextField();
        JButton sendButton = new JButton("Enviar");
        sendButton.addActionListener(new SendButtonListener());

        bottomPanel.add(messageInput, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);

        add(chatScrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    @Override
    public void updateChatsRecientes(String[] chatsRecientes) {
        // No necesitamos implementar esto aquí, pero lo dejamos para cumplir con la interfaz
    }

    @Override
    public void updateContactoActual(Contacto contacto) {
        contactoActual = contacto;
        updateChat(); // Actualizar el chat con el nuevo contacto
    }

    public void updateChat() {
        chatPanel.removeAll();
        if (contactoActual != null) {
            contactLabel.setText("Chat con " + contactoActual.getNombre());
            List<String> mensajes = controlador.obtenerMensajes(contactoActual);
            for (String mensaje : mensajes) {
                if (mensaje.startsWith("Tú: ")) {
                    addMessageBubble(mensaje.substring(4), Color.GREEN, controlador.getNombreUserActual(), BubbleText.SENT);
                } else {
                    addMessageBubble(mensaje, Color.LIGHT_GRAY, contactoActual.getNombre(), BubbleText.RECEIVED);
                }
            }
        } else {
            contactLabel.setText("Seleccione un contacto");
        }
        chatPanel.revalidate();
        chatPanel.repaint();
        
        SwingUtilities.invokeLater(() -> {
            JScrollBar verticalBar = chatScrollPane.getVerticalScrollBar();
            verticalBar.setValue(verticalBar.getMaximum());
        });
    }

    private void addMessageBubble(String message, Color color, String author, int type) {
        BubbleText bubble = new BubbleText(chatPanel, message, color, author, type);
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
                addMessageBubble(mensaje, Color.GREEN, controlador.getNombreUserActual(), BubbleText.SENT);
                messageInput.setText("");
            } else {
                JOptionPane.showMessageDialog(VentanaChatActual.this,
                    "Por favor, seleccione un contacto y escriba un mensaje",
                    "Error", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

	@Override
	public void updateListaContactos() {
		// TODO Auto-generated method stub
		
	}
}