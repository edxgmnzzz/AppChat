package umu.tds.app.ventanas;

import javax.swing.*;
import umu.tds.app.AppChat.Contacto;
import umu.tds.app.AppChat.Controlador;
import umu.tds.app.AppChat.Observer;

import java.awt.*;

public class VentanaChatsRecientes extends JPanel implements Observer {
    private static final long serialVersionUID = 1L;
    private JList<String> chatList;
    private Controlador controlador;

    public VentanaChatsRecientes() { // Eliminamos la dependencia de VentanaPrincipal como parámetro
        controlador = Controlador.getInstancia();
        controlador.addObserver(this);

        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(300, 600));
        setBorder(BorderFactory.createTitledBorder("Chats"));

        chatList = new JList<>(controlador.getChatsRecientes());
        chatList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String nombreChatSeleccionado = chatList.getSelectedValue();
                if (nombreChatSeleccionado != null && !nombreChatSeleccionado.equals("No hay chats recientes")) {
                    // Extraer solo el nombre del contacto (eliminar "Chat con ")
                    String nombreContacto = nombreChatSeleccionado.replace("Chat con ", "");
                    Contacto contacto = controlador.obtenerContactoPorNombre(nombreContacto);
                    if (contacto != null) {
                        controlador.setContactoActual(contacto); // Notificar al Controlador el cambio de contacto
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(chatList);
        scrollPane.setPreferredSize(new Dimension(280, 500));
        add(scrollPane, BorderLayout.CENTER);
    }

    public JList<String> getChatList() {
        return chatList;
    }

    public void actualizarLista(String[] chatsRecientes) {
        SwingUtilities.invokeLater(() -> {
            chatList.setListData(chatsRecientes);
            chatList.revalidate();
            chatList.repaint();
        });
    }

    @Override
    public void updateChatsRecientes(String[] chatsRecientes) {
        actualizarLista(chatsRecientes);
    }

    @Override
    public void updateContactoActual(Contacto contacto) {
        // No necesitamos implementar nada aquí, ya que VentanaChatsRecientes
        // no necesita reaccionar directamente a cambios en el contacto actual
    }

	@Override
	public void updateListaContactos() {
		// TODO Auto-generated method stub
		
	}
}