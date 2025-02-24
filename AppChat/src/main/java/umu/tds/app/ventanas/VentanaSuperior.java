package umu.tds.app.ventanas;

import javax.swing.*;
import umu.tds.app.AppChat.Contacto;
import umu.tds.app.AppChat.ContactoIndividual;
import umu.tds.app.AppChat.Controlador;
import umu.tds.app.AppChat.Observer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class VentanaSuperior extends JPanel implements Observer {
    private static final long serialVersionUID = 1L;
    private JComboBox<String> contactos;
    private JButton searchButton, contactsButton, premiumButton;
    private JLabel userLabel, userImage;
    private Controlador controlador;

    public VentanaSuperior() {
        controlador = Controlador.getInstancia();
        controlador.addObserver(this); // Registrarse como observador
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        JPanel optionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        List<ContactoIndividual> contactosList = controlador.obtenerContactos();
        contactos = new JComboBox<>(contactosList.stream()
                .map(Contacto::getNombre)
                .toArray(String[]::new));
        contactos.insertItemAt("Seleccione un contacto...", 0);
        contactos.setSelectedIndex(0);
        contactos.addActionListener(e -> {
            String selected = (String) contactos.getSelectedItem();
            Contacto contacto = "Seleccione un contacto...".equals(selected) ? null :
                    contactosList.stream().filter(c -> c.getNombre().equals(selected)).findFirst().orElse(null);
            controlador.setContactoActual(contacto); // Notificar al controlador el cambio
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

    @Override
    public void updateChatsRecientes(String[] chatsRecientes) {
        // No necesitamos implementar esto aquí, pero lo dejamos para cumplir con la interfaz
    }

    @Override
    public void updateContactoActual(Contacto contacto) {
        if (contactos.getItemCount() == 0) {
            return; // Evita el error si el JComboBox está vacío
        }

        if (contacto == null) {
            contactos.setSelectedIndex(0); // "Seleccione un contacto..."
        } else {
            contactos.setSelectedItem(contacto.getNombre());
        }
    }


    @Override
    public void updateListaContactos() {
        List<ContactoIndividual> contactosList = controlador.obtenerContactos();
        contactos.removeAllItems(); // Limpiar la lista
        contactos.addItem("Seleccione un contacto..."); // Opción por defecto

        for (ContactoIndividual contacto : contactosList) {
            contactos.addItem(contacto.getNombre()); // Añadir cada contacto
        }

    }

}