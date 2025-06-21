package umu.tds.app.ventanas;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

import umu.tds.app.AppChat.Contacto;
import umu.tds.app.AppChat.Controlador;
import umu.tds.app.AppChat.ObserverChats;
import umu.tds.app.AppChat.Theme;

/**
 * Panel lateral que muestra la lista de chats recientes del usuario.
 * Permite seleccionar un contacto para abrir la conversación correspondiente.
 */
public class VentanaChatsRecientes extends JPanel implements ObserverChats {
    private static final long serialVersionUID = 1L;

    /** Lista visual de los chats recientes. */
    private JList<String> chatList;

    /** Controlador principal de la aplicación. */
    private Controlador controlador;

    /**
     * Constructor de la ventana de chats recientes.
     * Inicializa componentes visuales y registra el observador en el controlador.
     */
    public VentanaChatsRecientes() {
        controlador = Controlador.getInstancia();
        controlador.addObserverChats(this);

        setLayout(new BorderLayout());
        setBackground(Theme.COLOR_FONDO);
        setPreferredSize(new Dimension(200, 0));
        setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Theme.COLOR_PRINCIPAL),
                "Chats",
                TitledBorder.CENTER,
                TitledBorder.TOP,
                Theme.FONT_BOLD_MEDIUM,
                Theme.COLOR_TEXTO
        ));

        chatList = new JList<>(new String[0]);
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
        scrollPane.setBorder(BorderFactory.createEmptyBorder(
                Theme.PADDING_SMALL, Theme.PADDING_SMALL, Theme.PADDING_SMALL, Theme.PADDING_SMALL));
        add(scrollPane, BorderLayout.CENTER);

        SwingUtilities.invokeLater(() -> {
            updateChatsRecientes(controlador.getChatsRecientes());
            sincronizarSeleccionConContactoActual();
        });
    }

    /**
     * Devuelve la lista de chats visual.
     * @return componente JList con los chats.
     */
    public JList<String> getChatList() {
        return chatList;
    }

    /**
     * Actualiza la lista de chats recientes con nuevos datos.
     * @param chatsRecientes lista de chats en formato de texto.
     */
    public void actualizarLista(String[] chatsRecientes) {
        SwingUtilities.invokeLater(() -> {
            chatList.setListData(chatsRecientes);
            if (chatsRecientes.length == 1 && chatsRecientes[0].equals("No hay chats recientes")) {
                chatList.setForeground(Color.GRAY);
                chatList.setCellRenderer(new DefaultListCellRenderer() {
                    private static final long serialVersionUID = 1L;

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

    /**
     * Sincroniza la selección actual de la lista con el contacto seleccionado en el controlador.
     */
    private void sincronizarSeleccionConContactoActual() {
        Contacto contactoActual = controlador.getContactoActual();
        String currentSelection = chatList.getSelectedValue();
        String expectedSelection = contactoActual != null ? "Chat con " + contactoActual.getNombre() : null;

        if (contactoActual != null && !expectedSelection.equals(currentSelection)) {
            chatList.setSelectedValue(expectedSelection, true);
        } else if (contactoActual == null && chatList.getModel().getSize() > 0
                && !chatList.getModel().getElementAt(0).equals("No hay chats recientes")) {
            chatList.setSelectedIndex(0);
        }
    }

    /**
     * Recibe la actualización de la lista de chats recientes desde el controlador.
     * @param chatsRecientes array con los nombres de los chats recientes.
     */
    @Override
    public void updateChatsRecientes(String[] chatsRecientes) {
        actualizarLista(chatsRecientes);
    }

    /**
     * Se ejecuta cuando cambia el contacto actual.
     * Sincroniza la selección visual.
     * @param contacto nuevo contacto actual.
     */
    @Override
    public void updateContactoActual(Contacto contacto) {
        sincronizarSeleccionConContactoActual();
    }

    /**
     * Renderizador personalizado para cada celda de la lista de chats.
     * Cambia los colores según el tema y si está seleccionado o no.
     */
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
