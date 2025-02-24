package umu.tds.app.ventanas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import umu.tds.app.AppChat.Contacto;
import umu.tds.app.AppChat.ContactoIndividual;
import umu.tds.app.AppChat.Controlador;
import umu.tds.app.AppChat.Observer;
import java.util.List;

public class VentanaContactos extends JFrame implements Observer {
    private static final long serialVersionUID = 1L;
    private JList<String> contactList;
    private JList<String> groupList;
    private Controlador controlador;

    public VentanaContactos(JFrame parent) {
        controlador = Controlador.getInstancia();
        controlador.addObserver(this); // Registrarse como observador

        setTitle("Contactos");
        setSize(500, 500);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new GridLayout(1, 3, 10, 10));

        // Panel izquierdo - Lista de contactos
        contactList = new JList<>(convertirContactosAArray(controlador.obtenerContactos()));
        JScrollPane contactScrollPane = new JScrollPane(contactList);
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("Lista Contactos"));
        leftPanel.add(contactScrollPane, BorderLayout.CENTER);
        JButton addContactButton = new JButton("Añadir Contacto");
        addContactButton.addActionListener(e -> new VentanaNuevoContacto(this));
        leftPanel.add(addContactButton, BorderLayout.SOUTH);

        // Panel central - Botones de mover
        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        JButton moveRightButton = new JButton(">>");
        JButton moveLeftButton = new JButton("<<");
        centerPanel.add(moveRightButton);
        centerPanel.add(moveLeftButton);

        // Panel derecho - Lista de grupos (por ahora usamos un placeholder)
        groupList = new JList<>(new String[]{"Grupo 1", "Grupo 2"});
        JScrollPane groupScrollPane = new JScrollPane(groupList);
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("Grupos"));
        rightPanel.add(groupScrollPane, BorderLayout.CENTER);
        JButton addGroupButton = new JButton("Añadir Grupo");
        rightPanel.add(addGroupButton, BorderLayout.SOUTH);

        mainPanel.add(leftPanel);
        mainPanel.add(centerPanel);
        mainPanel.add(rightPanel);

        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    private String[] convertirContactosAArray(List<ContactoIndividual> contactos) {
        String[] array = new String[contactos.size()];
        for (int i = 0; i < contactos.size(); i++) {
            array[i] = "Chat con " + contactos.get(i).getNombre();
        }
        return array;
    }

    @Override
    public void updateChatsRecientes(String[] chatsRecientes) {
        // Actualizar la lista de contactos con los chats recientes
        contactList.setListData(chatsRecientes);
        contactList.revalidate();
        contactList.repaint();
    }

    @Override
    public void updateContactoActual(Contacto contacto) {
        // No necesitamos implementar nada aquí, ya que VentanaContactos
        // solo muestra la lista de contactos/chats, no el contacto actual
    }

    @Override
    public void updateListaContactos() {
        SwingUtilities.invokeLater(() -> {
            System.out.println("Actualizando lista de contactos en VentanaContactos");
            List<ContactoIndividual> contactos = controlador.obtenerContactos();
            System.out.println("Contactos obtenidos: " + contactos);

            contactList.setListData(convertirContactosAArray(contactos));
            contactList.revalidate();
            contactList.repaint();
        });
    }




}