package umu.tds.app.ventanas;

import javax.swing.*;
import java.awt.*;
import umu.tds.app.AppChat.Contacto;
import umu.tds.app.AppChat.ContactoIndividual;
import umu.tds.app.AppChat.Controlador;
import umu.tds.app.AppChat.Grupo;
import umu.tds.app.AppChat.ObserverChats;
import umu.tds.app.AppChat.ObserverContactos;

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
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        JPanel optionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        contactos = new JComboBox<>();
        contactos.setRenderer(new CustomComboBoxRenderer());
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
        
        searchButton = new JButton("Buscar");
        searchButton.addActionListener(e -> new VentanaBusqueda(null));

        contactsButton = new JButton("Contactos");
        contactsButton.addActionListener(e -> new VentanaContactos(null));

        premiumButton = new JButton("Premium");
        premiumButton.addActionListener(e -> JOptionPane.showConfirmDialog(this, "¿Quieres hacerte Premium socio?"));

        userLabel = new JLabel(controlador.getNombreUserActual());
        userImage = new JLabel(controlador.getIconoUserActual());

        optionsPanel.add(contactos);
        optionsPanel.add(searchButton);
        optionsPanel.add(contactsButton);
        optionsPanel.add(premiumButton);
        optionsPanel.add(userLabel);
        optionsPanel.add(userImage);

        add(optionsPanel);
    }

    public void inicializarContactoActual() {
        String[] chatsRecientes = controlador.getChatsRecientes();
        if (chatsRecientes.length > 0 && !chatsRecientes[0].equals("No hay chats recientes")) {
            String primerChat = chatsRecientes[0].substring(9); // Remove "Chat con " prefix
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
    public void updateChatsRecientes(String[] chatsRecientes) {
        // No implementado aquí
    }

    @Override
    public void updateContactoActual(Contacto contacto) {
        if (contactos.getItemCount() == 0) {
            return;
        }

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
                c.setBackground(new Color(173, 216, 230));
                c.setForeground(Color.BLACK);
            } else {
                c.setBackground(list.getBackground());
                c.setForeground(list.getForeground());
            }
            return c;
        }
    }
}