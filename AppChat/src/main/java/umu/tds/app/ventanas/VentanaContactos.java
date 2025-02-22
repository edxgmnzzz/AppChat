package umu.tds.app.ventanas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class VentanaContactos extends JFrame {
    private static final long serialVersionUID = 1L;

	public VentanaContactos(JFrame parent) {
        setTitle("Contactos");
        setSize(500, 500);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new GridLayout(1, 3, 10, 10));

        // Panel izquierdo - Lista de contactos
        String[] chatsRecientes = {"Chat con Piter", "Chat con Maria", "Chat con Pedro"};
        JList<String> contactList = new JList<>(chatsRecientes);
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

        // Panel derecho - Lista de grupos
        JList<String> groupList = new JList<>(chatsRecientes);
        JScrollPane groupScrollPane = new JScrollPane(groupList);
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("Grupo1"));
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
}