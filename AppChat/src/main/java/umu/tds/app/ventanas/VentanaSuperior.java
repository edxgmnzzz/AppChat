package umu.tds.app.ventanas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

import umu.tds.app.AppChat.Contacto;
import umu.tds.app.AppChat.ContactoIndividual;
import umu.tds.app.AppChat.Controlador;
import umu.tds.app.AppChat.Grupo;
import umu.tds.app.AppChat.ObserverChats;
import umu.tds.app.AppChat.ObserverContactos;
import umu.tds.app.AppChat.Theme;

import java.util.List;

public class VentanaSuperior extends JPanel implements ObserverChats, ObserverContactos {
    private static final long serialVersionUID = 1L;
    private JComboBox<String> contactos;
    private JButton searchButton, contactsButton, premiumButton;
    private JLabel userLabel, userImage;
    private Controlador controlador;

    public VentanaSuperior() {
        controlador = Controlador.getInstancia();
        controlador.addObserverChats(this);
        controlador.addObserverContactos(this);
        setLayout(new BorderLayout());
        setBackground(Theme.COLOR_HEADER);
        setBorder(BorderFactory.createEmptyBorder(Theme.PADDING_SMALL, Theme.PADDING_MEDIUM, Theme.PADDING_SMALL, Theme.PADDING_MEDIUM));

        // Panel de opciones (izquierda)
        JPanel optionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        optionsPanel.setOpaque(false);

        contactos = new JComboBox<>();
        contactos.setRenderer(new CustomComboBoxRenderer());
        contactos.setBackground(Theme.COLOR_SECUNDARIO);
        contactos.setForeground(Theme.COLOR_PRINCIPAL);
        actualizarContactosDropdown();

        contactos.addActionListener(e -> {
            String selected = (String) contactos.getSelectedItem();
            if (selected == null || "Seleccione un contacto...".equals(selected)) {
                controlador.setContactoActual(null);
            } else {
                String nombre = selected.startsWith("Individual: ") ? selected.substring(12) : selected.substring(7);
                Contacto contacto = controlador.obtenerContactoPorNombre(nombre);
                controlador.setContactoActual(contacto);
            }
        });

        searchButton = createStyledButton("Buscar", e -> new VentanaBusqueda(null));
        contactsButton = createStyledButton("Contactos", e -> new VentanaContactos(null));
        premiumButton = createStyledButton("Premium", e -> JOptionPane.showConfirmDialog(this, "¿Quieres hacerte Premium socio?"));

        optionsPanel.add(contactos);
        optionsPanel.add(searchButton);
        optionsPanel.add(contactsButton);
        optionsPanel.add(premiumButton);

        add(optionsPanel, BorderLayout.WEST);

        // Panel de usuario (derecha)
        JPanel userPanel = new JPanel(new BorderLayout());
        userPanel.setOpaque(false);

        userImage = new JLabel(controlador.getIconoUserActual());
        userImage.setBorder(BorderFactory.createLineBorder(Theme.COLOR_PRINCIPAL, 2));
        userPanel.add(userImage, BorderLayout.CENTER);

        userLabel = new JLabel(controlador.getNombreUserActual());
        userLabel.setForeground(Theme.COLOR_TEXTO);
        userLabel.setFont(Theme.FONT_BOLD_MEDIUM);
        userLabel.setHorizontalAlignment(SwingConstants.CENTER);
        userPanel.add(userLabel, BorderLayout.SOUTH);

        add(userPanel, BorderLayout.EAST);
    }

    private JButton createStyledButton(String text, ActionListener action) {
        JButton button = new JButton(text);
        button.setBackground(Theme.COLOR_PRINCIPAL);
        button.setForeground(Theme.COLOR_TEXTO);
        button.setFont(Theme.FONT_BOLD_MEDIUM);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.COLOR_ACENTO, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        button.addActionListener(action);
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(Theme.COLOR_HOVER);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(Theme.COLOR_PRINCIPAL);
            }
        });
        return button;
    }

    public void inicializarContactoActual() {
        String[] chatsRecientes = controlador.getChatsRecientes();
        if (chatsRecientes.length > 0 && !chatsRecientes[0].equals("No hay chats recientes")) {
            String primerChat = chatsRecientes[0].substring(9);
            Contacto contactoInicial = controlador.obtenerContactoPorNombre(primerChat);
            controlador.setContactoActual(contactoInicial);
            String prefixedName = (contactoInicial instanceof ContactoIndividual) 
                ? "Individual: " + contactoInicial.getNombre() 
                : "Grupo: " + contactoInicial.getNombre();
            contactos.setSelectedItem(prefixedName);
        } else {
            contactos.setSelectedIndex(0);
        }
    }

    private void actualizarContactosDropdown() {
        contactos.removeAllItems();
        contactos.addItem("Seleccione un contacto...");

        List<Contacto> contactosList = controlador.obtenerContactos();
        for (Contacto contacto : contactosList) {
            if (contacto instanceof ContactoIndividual) {
                contactos.addItem("Individual: " + contacto.getNombre());
            }
        }
        for (Contacto contacto : contactosList) {
            if (contacto instanceof Grupo) {
                contactos.addItem("Grupo: " + contacto.getNombre());
            }
        }
    }

    @Override
    public void updateChatsRecientes(String[] chatsRecientes) {}

    @Override
    public void updateContactoActual(Contacto contacto) {
        if (contactos.getItemCount() == 0) return;
        if (contacto == null) {
            contactos.setSelectedIndex(0);
        } else {
            String prefixedName = (contacto instanceof ContactoIndividual) 
                ? "Individual: " + contacto.getNombre() 
                : "Grupo: " + contacto.getNombre();
            contactos.setSelectedItem(prefixedName);
        }
    }

    @Override
    public void updateListaContactos() {
        actualizarContactosDropdown();
    }

    private class CustomComboBoxRenderer extends DefaultListCellRenderer {
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