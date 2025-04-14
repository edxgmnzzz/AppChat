package umu.tds.app.ventanas;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import umu.tds.app.AppChat.Contacto;
import umu.tds.app.AppChat.ContactoIndividual;
import umu.tds.app.AppChat.Grupo;  // Assuming this is the import for Grupo
import umu.tds.app.AppChat.Controlador;
import umu.tds.app.AppChat.ObserverContactos;

public class VentanaContactos extends JFrame implements ObserverContactos {
    private static final long serialVersionUID = 1L;
    private JList<String> contactList;
    private JList<String> groupList;
    private Controlador controlador;

    public VentanaContactos(VentanaSuperior ventana) {
        controlador = Controlador.getInstancia();
        controlador.addObserverContactos(this); // Register as observer

        setTitle("Contactos");
        setSize(500, 500);
        setLocationRelativeTo(ventana);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new GridLayout(1, 3, 10, 10));

        // Left Panel - Individual Contacts List
        contactList = new JList<>(convertirContactosIndividualesAArray(controlador.obtenerContactos()));
        JScrollPane contactScrollPane = new JScrollPane(contactList);
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("Contactos Individuales"));
        leftPanel.add(contactScrollPane, BorderLayout.CENTER);
        JButton addContactButton = new JButton("Añadir Contacto");
        addContactButton.addActionListener(e -> new VentanaNuevoContacto(this));
        leftPanel.add(addContactButton, BorderLayout.SOUTH);

        // Center Panel - Move Buttons
        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        JButton moveRightButton = new JButton(">>");
        JButton moveLeftButton = new JButton("<<");
        centerPanel.add(moveRightButton);
        centerPanel.add(moveLeftButton);

        // Right Panel - Groups List
        groupList = new JList<>(convertirGruposAArray(controlador.obtenerContactos()));
        JScrollPane groupScrollPane = new JScrollPane(groupList);
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("Grupos"));
        rightPanel.add(groupScrollPane, BorderLayout.CENTER);
        JButton addGroupButton = new JButton("Añadir Grupo");
        addGroupButton.addActionListener(e -> new VentanaNuevoGrupo(this)); // Assuming a window for creating groups
        rightPanel.add(addGroupButton, BorderLayout.SOUTH);

        // Button actions for moving contacts to groups or vice versa
        moveRightButton.addActionListener(e -> moverContactoAGrupo());
        moveLeftButton.addActionListener(e -> moverGrupoAContacto());

        mainPanel.add(leftPanel);
        mainPanel.add(centerPanel);
        mainPanel.add(rightPanel);

        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    private String[] convertirContactosIndividualesAArray(List<Contacto> contactos) {
        List<String> individuales = new ArrayList<>();
        for (Contacto c : contactos) {
            if (c instanceof ContactoIndividual) {
                individuales.add("Chat con " + c.getNombre());
            }
        }
        return individuales.toArray(new String[0]);
    }

    private String[] convertirGruposAArray(List<Contacto> contactos) {
        List<String> grupos = new ArrayList<>();
        for (Contacto c : contactos) {
            if (c instanceof Grupo) {
                grupos.add("Grupo " + c.getNombre());
            }
        }
        return grupos.toArray(new String[0]);
    }

    private void moverContactoAGrupo() {
        int selectedContactIndex = contactList.getSelectedIndex();
        int selectedGroupIndex = groupList.getSelectedIndex();
        if (selectedContactIndex != -1 && selectedGroupIndex != -1) {
            List<Contacto> contactos = controlador.obtenerContactos();
            List<Contacto> individuales = new ArrayList<>();
            List<Grupo> grupos = new ArrayList<>();
            for (Contacto c : contactos) {
                if (c instanceof ContactoIndividual) individuales.add(c);
                if (c instanceof Grupo) grupos.add((Grupo) c);
            }
            ContactoIndividual contacto = (ContactoIndividual) individuales.get(selectedContactIndex);
            Grupo grupo = grupos.get(selectedGroupIndex);
            // Assuming Grupo has a method to add members
            grupo.addIntegrante(contacto); // You need to implement this in Grupo class
            updateListaContactos(); // Refresh UI
        }
    }

    private void moverGrupoAContacto() {
        // This might not make sense in all contexts, but included for symmetry
        // Perhaps remove a contact from a group?
        int selectedGroupIndex = groupList.getSelectedIndex();
        if (selectedGroupIndex != -1) {
            List<Contacto> contactos = controlador.obtenerContactos();
            List<Grupo> grupos = new ArrayList<>();
            for (Contacto c : contactos) {
                if (c instanceof Grupo) grupos.add((Grupo) c);
            }
            Grupo grupo = grupos.get(selectedGroupIndex);
            // Logic to remove a member or handle this differently could go here
            updateListaContactos(); // Refresh UI
        }
    }

    @Override
    public void updateListaContactos() {
        SwingUtilities.invokeLater(() -> {
            System.out.println("Actualizando listas en VentanaContactos");
            List<Contacto> contactos = controlador.obtenerContactos();
            System.out.println("Contactos obtenidos: " + contactos);

            contactList.setListData(convertirContactosIndividualesAArray(contactos));
            groupList.setListData(convertirGruposAArray(contactos));
            contactList.revalidate();
            contactList.repaint();
            groupList.revalidate();
            groupList.repaint();
        });
    }
}