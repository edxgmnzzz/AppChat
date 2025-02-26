package umu.tds.app.ventanas;

import javax.swing.*;
import java.awt.*;
import umu.tds.app.AppChat.Contacto;
import umu.tds.app.AppChat.Controlador;
import umu.tds.app.AppChat.ObserverChats;

public class VentanaChatsRecientes extends JPanel implements ObserverChats {
    private static final long serialVersionUID = 1L;
    private JList<String> chatList;
    private Controlador controlador;

    public VentanaChatsRecientes() {
        controlador = Controlador.getInstancia();
        controlador.addObserverChats(this);

        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(300, 600));
        setBorder(BorderFactory.createTitledBorder("Chats"));

        chatList = new JList<>(controlador.getChatsRecientes());
        chatList.setCellRenderer(new CustomChatListRenderer()); // Añadir renderer personalizado para color
        chatList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String nombreChatSeleccionado = chatList.getSelectedValue();
                if (nombreChatSeleccionado != null && !nombreChatSeleccionado.equals("No hay chats recientes")) {
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

        // Sincronizar con el contacto actual inicial
        sincronizarSeleccionConContactoActual();
    }

    public JList<String> getChatList() {
        return chatList;
    }

    public void actualizarLista(String[] chatsRecientes) {
        SwingUtilities.invokeLater(() -> {
            chatList.setListData(chatsRecientes);
            sincronizarSeleccionConContactoActual(); // Mantener sincronización tras actualizar
            chatList.revalidate();
            chatList.repaint();
        });
    }

    private void sincronizarSeleccionConContactoActual() {
        Contacto contactoActual = controlador.getContactoActual();
        if (contactoActual != null) {
            String chatSeleccionado = "Chat con " + contactoActual.getNombre();
            chatList.setSelectedValue(chatSeleccionado, true); // Seleccionar y hacer visible
        } else if (chatList.getModel().getSize() > 0 && !chatList.getModel().getElementAt(0).equals("No hay chats recientes")) {
            chatList.setSelectedIndex(0); // Seleccionar el primero si no hay contacto actual
        }
    }

    @Override
    public void updateChatsRecientes(String[] chatsRecientes) {
        actualizarLista(chatsRecientes);
    }

    @Override
    public void updateContactoActual(Contacto contacto) {
        sincronizarSeleccionConContactoActual(); // Sincronizar cuando cambie el contacto actual
    }

    // Custom renderer para añadir color a la selección
    private class CustomChatListRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, 
                                                      boolean isSelected, boolean cellHasFocus) {
            Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (isSelected) {
                c.setBackground(new Color(173, 216, 230)); // Mismo color que VentanaSuperior
                c.setForeground(Color.BLACK); // Texto legible
            } else {
                c.setBackground(list.getBackground());
                c.setForeground(list.getForeground());
            }
            return c;
        }
    }
}