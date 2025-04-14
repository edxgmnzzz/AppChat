package umu.tds.app.ventanas;

import javax.swing.*;
import java.awt.*;

public class VentanaBusqueda extends JFrame {
    private static final long serialVersionUID = 1L;

	public VentanaBusqueda(VentanaSuperior ventana) {
        setTitle("Buscar");
        setSize(500, 500);
        setLocationRelativeTo(ventana);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel searchPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel searchIcon = new JLabel(new ImageIcon("search_icon.png")); // Reemplazar con ícono real
        JTextField textSearch = new JTextField(10);
        JTextField phoneSearch = new JTextField(10);
        JTextField contactSearch = new JTextField(10);
        JButton searchButton = new JButton("Buscar");

        gbc.gridx = 0; gbc.gridy = 0;
        searchPanel.add(searchIcon, gbc);
        gbc.gridx = 1;
        searchPanel.add(textSearch, gbc);
        gbc.gridx = 2;
        searchPanel.add(phoneSearch, gbc);
        gbc.gridx = 3;
        searchPanel.add(contactSearch, gbc);
        gbc.gridx = 4;
        searchPanel.add(searchButton, gbc);

        JPanel messagesPanel = new JPanel();
        messagesPanel.setLayout(new BoxLayout(messagesPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(messagesPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        setLayout(new BorderLayout());
        add(searchPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        setVisible(true);
    }
}