package umu.tds.app.ventanas;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import umu.tds.app.AppChat.Contacto;
import umu.tds.app.AppChat.Controlador;
import umu.tds.app.AppChat.ObserverChats;
import umu.tds.app.AppChat.Theme;

public class VentanaChatsRecientes extends JPanel implements ObserverChats {
    private static final long serialVersionUID = 1L;
    private JList<String> chatList;
    private Controlador controlador;

    public VentanaChatsRecientes() {
        controlador = Controlador.getInstancia();
        controlador.addObserverChats(this);

        setLayout(new BorderLayout());
        setBackground(Theme.COLOR_FONDO);
        setPreferredSize(new Dimension(200, 0));
        setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Theme.COLOR_PRINCIPAL), "Chats", 
            TitledBorder.CENTER, TitledBorder.TOP, Theme.FONT_BOLD_MEDIUM, Theme.COLOR_TEXTO));

        chatList = new JList<>(controlador.getChatsRecientes());
        chatList.setCellRenderer(new CustomChatListRenderer());
        chatList.setBackground(Theme.COLOR_SECUNDARIO);

        chatList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String nombreChatSeleccionado = chatList.getSelectedValue();
                if (nombreChatSeleccionado != null && !nombreChatSeleccionado.equals("No hay chats recientes")) {
                    String nombreContacto = nombreChatSeleccionado.replace("Chat con ", "");
                    Contacto contacto = controlador.obtenerContactoPorNombre(nombreContacto);
                    if (contacto != null) {
                        controlador.setContactoActual(contacto);
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(chatList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(Theme.PADDING_SMALL, Theme.PADDING_SMALL, Theme.PADDING_SMALL, Theme.PADDING_SMALL));
        add(scrollPane, BorderLayout.CENTER);

        sincronizarSeleccionConContactoActual();
    }

    public JList<String> getChatList() {
        return chatList;
    }

    public void actualizarLista(String[] chatsRecientes) {
        SwingUtilities.invokeLater(() -> {
            chatList.setListData(chatsRecientes);
            if (chatsRecientes.length == 1 && chatsRecientes[0].equals("No hay chats recientes")) {
                chatList.setForeground(Color.GRAY);
                chatList.setCellRenderer(new DefaultListCellRenderer() {
                    @Override
                    public Component getListCellRendererComponent(JList<?> list, Object value, int index, 
                                                                  boolean isSelected, boolean cellHasFocus) {
                        Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                        c.setForeground(Color.GRAY);
                        ((JLabel) c).setHorizontalAlignment(SwingConstants.CENTER);
                        return c;
                    }
                });
            } else {
                chatList.setForeground(Theme.COLOR_PRINCIPAL);
                chatList.setCellRenderer(new CustomChatListRenderer());
            }
            sincronizarSeleccionConContactoActual();
            chatList.revalidate();
            chatList.repaint();
        });
    }

    private void sincronizarSeleccionConContactoActual() {
        Contacto contactoActual = controlador.getContactoActual();
        if (contactoActual != null) {
            String chatSeleccionado = "Chat con " + contactoActual.getNombre();
            chatList.setSelectedValue(chatSeleccionado, true);
        } else if (chatList.getModel().getSize() > 0 && !chatList.getModel().getElementAt(0).equals("No hay chats recientes")) {
            chatList.setSelectedIndex(0);
        }
    }

    @Override
    public void updateChatsRecientes(String[] chatsRecientes) {
        actualizarLista(chatsRecientes);
    }

    @Override
    public void updateContactoActual(Contacto contacto) {
        sincronizarSeleccionConContactoActual();
    }

    private class CustomChatListRenderer extends DefaultListCellRenderer {
        private static final long serialVersionUID = 1L;

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, 
                                                      boolean isSelected, boolean cellHasFocus) {
            Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (isSelected) {
                c.setBackground(Theme.COLOR_HOVER);
                c.setForeground(Theme.COLOR_TEXTO);
            } else {
                c.setBackground(Theme.COLOR_SECUNDARIO);
                c.setForeground(Theme.COLOR_PRINCIPAL);
            }
            return c;
        }
    }
}