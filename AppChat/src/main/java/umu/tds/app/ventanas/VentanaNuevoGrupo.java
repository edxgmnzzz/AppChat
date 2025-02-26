package umu.tds.app.ventanas;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import umu.tds.app.AppChat.Contacto;
import umu.tds.app.AppChat.ContactoIndividual;
import umu.tds.app.AppChat.Controlador;

public class VentanaNuevoGrupo extends JFrame {
    private static final long serialVersionUID = 1L;
    private JTextField groupNameField;
    private JList<String> availableContactsList;
    private JList<String> selectedMembersList;
    private Controlador controlador;
    private DefaultListModel<String> availableModel;
    private DefaultListModel<String> selectedModel;

    public VentanaNuevoGrupo(JFrame parent) {
        controlador = Controlador.getInstancia();

        setTitle("Crear Nuevo Grupo");
        setSize(600, 400);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Group Name Input
        JPanel namePanel = new JPanel(new FlowLayout());
        JLabel nameLabel = new JLabel("Nombre del Grupo:");
        groupNameField = new JTextField(20);
        namePanel.add(nameLabel);
        namePanel.add(groupNameField);

        // Contact Selection Panels
        JPanel selectionPanel = new JPanel(new GridLayout(1, 3, 10, 10));

        // Available Contacts (Left)
        availableModel = new DefaultListModel<>();
        cargarContactosDisponibles();
        availableContactsList = new JList<>(availableModel);
        JScrollPane availableScrollPane = new JScrollPane(availableContactsList);
        JPanel availablePanel = new JPanel(new BorderLayout());
        availablePanel.setBorder(BorderFactory.createTitledBorder("Contactos Disponibles"));
        availablePanel.add(availableScrollPane, BorderLayout.CENTER);

        // Move Buttons (Center)
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        JButton addButton = new JButton(">>");
        JButton removeButton = new JButton("<<");
        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);

        // Selected Members (Right)
        selectedModel = new DefaultListModel<>();
        selectedMembersList = new JList<>(selectedModel);
        JScrollPane selectedScrollPane = new JScrollPane(selectedMembersList);
        JPanel selectedPanel = new JPanel(new BorderLayout());
        selectedPanel.setBorder(BorderFactory.createTitledBorder("Miembros Seleccionados"));
        selectedPanel.add(selectedScrollPane, BorderLayout.CENTER);

        selectionPanel.add(availablePanel);
        selectionPanel.add(buttonPanel);
        selectionPanel.add(selectedPanel);

        // Bottom Buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton createButton = new JButton("Crear Grupo");
        JButton cancelButton = new JButton("Cancelar");
        bottomPanel.add(createButton);
        bottomPanel.add(cancelButton);

        mainPanel.add(namePanel, BorderLayout.NORTH);
        mainPanel.add(selectionPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Button Actions
        addButton.addActionListener(e -> agregarMiembro());
        removeButton.addActionListener(e -> quitarMiembro());
        createButton.addActionListener(e -> crearGrupo());
        cancelButton.addActionListener(e -> dispose());

        setContentPane(mainPanel);
        setVisible(true);
    }

    private void cargarContactosDisponibles() {
        List<Contacto> contactos = controlador.obtenerContactos();
        for (Contacto c : contactos) {
            if (c instanceof ContactoIndividual) {
                availableModel.addElement(c.getNombre());
            }
        }
    }

    private void agregarMiembro() {
        String selectedContact = availableContactsList.getSelectedValue();
        if (selectedContact != null) {
            availableModel.removeElement(selectedContact);
            selectedModel.addElement(selectedContact);
        }
    }

    private void quitarMiembro() {
        String selectedMember = selectedMembersList.getSelectedValue();
        if (selectedMember != null) {
            selectedModel.removeElement(selectedMember);
            availableModel.addElement(selectedMember);
        }
    }

    private void crearGrupo() {
        String groupName = groupNameField.getText().trim();
        if (groupName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, introduce un nombre para el grupo.", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<ContactoIndividual> miembros = new ArrayList<>();
        List<Contacto> contactos = controlador.obtenerContactos();
        for (int i = 0; i < selectedModel.size(); i++) {
            String miembroNombre = selectedModel.getElementAt(i);
            for (Contacto c : contactos) {
                if (c instanceof ContactoIndividual && c.getNombre().equals(miembroNombre)) {
                    miembros.add((ContactoIndividual) c);
                    break;
                }
            }
        }

        // Create the group via Controlador
        controlador.crearGrupo(groupName, miembros); // Assumes this method exists
        JOptionPane.showMessageDialog(this, "Grupo '" + groupName + "' creado con éxito.", 
            "Éxito", JOptionPane.INFORMATION_MESSAGE);
        dispose(); // Close the window after creation
    }
}