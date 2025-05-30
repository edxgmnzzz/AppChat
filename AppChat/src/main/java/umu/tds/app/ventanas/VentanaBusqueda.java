package umu.tds.app.ventanas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import umu.tds.app.AppChat.Controlador;
import umu.tds.app.AppChat.Mensaje;

public class VentanaBusqueda extends JFrame {
    private static final long serialVersionUID = 1L;
    private Controlador controlador;
    private JTextField textSearch;
    private JTextField phoneSearch;
    private JTextField contactSearch;
    private JPanel messagesPanel;

    public VentanaBusqueda() {
        controlador = Controlador.getInstancia();
        setTitle("Buscar");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        inicializarInterfaz();

        setVisible(true);
    }

    private void inicializarInterfaz() {
        JPanel searchPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel searchIcon = new JLabel("🔍"); // Icono básico por ahora

        textSearch = new JTextField(10);
        phoneSearch = new JTextField(10);
        contactSearch = new JTextField(10);
        JButton searchButton = new JButton("Buscar");

        gbc.gridx = 0; gbc.gridy = 0;
        searchPanel.add(searchIcon, gbc);
        gbc.gridx = 1;
        searchPanel.add(new JLabel("Texto:"), gbc);
        gbc.gridx = 2;
        searchPanel.add(textSearch, gbc);
        gbc.gridx = 3;
        searchPanel.add(new JLabel("Teléfono:"), gbc);
        gbc.gridx = 4;
        searchPanel.add(phoneSearch, gbc);
        gbc.gridx = 5;
        searchPanel.add(new JLabel("Contacto:"), gbc);
        gbc.gridx = 6;
        searchPanel.add(contactSearch, gbc);
        gbc.gridx = 7;
        searchPanel.add(searchButton, gbc);

        messagesPanel = new JPanel();
        messagesPanel.setLayout(new BoxLayout(messagesPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(messagesPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        setLayout(new BorderLayout());
        add(searchPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Acción del botón
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                realizarBusqueda();
            }
        });
    }

    private void realizarBusqueda() {
        String texto = textSearch.getText().trim();
        String telefono = phoneSearch.getText().trim();
        String nombre = contactSearch.getText().trim();

        List<Mensaje> resultados = controlador.buscarMensajes(texto, telefono, nombre);

        mostrarResultados(resultados);
    }

    private void mostrarResultados(List<Mensaje> resultados) {
        messagesPanel.removeAll();

        if (resultados.isEmpty()) {
            JLabel sinResultados = new JLabel("No se encontraron mensajes");
            sinResultados.setAlignmentX(Component.CENTER_ALIGNMENT);
            messagesPanel.add(sinResultados);
        } else {
            for (Mensaje m : resultados) {
                String linea = "[" + m.getHora().toString() + "] " 
                                + m.getEmisor().getName() + " -> " 
                                + m.getReceptor().getNombre() + ": " 
                                + m.getTexto();
                JLabel mensajeLabel = new JLabel(linea);
                mensajeLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                messagesPanel.add(mensajeLabel);
            }
        }

        messagesPanel.revalidate();
        messagesPanel.repaint();
    }
}
