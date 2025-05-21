package umu.tds.app.ventanas;

import javax.swing.*;
import umu.tds.app.AppChat.Contacto;
import umu.tds.app.AppChat.Controlador;
import java.util.List;
import umu.tds.app.AppChat.Theme;

import java.awt.*;
public class VentanaPrincipal extends JFrame {
    private static final long serialVersionUID = 1L;
    private VentanaSuperior ventanaSuperior;
    private VentanaChatsRecientes chatsRecientes;
    private VentanaChatActual chatActual;

    public VentanaPrincipal() {
        setTitle("AppChat");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Theme.COLOR_SECUNDARIO);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(
            Theme.PADDING_LARGE, Theme.PADDING_LARGE,
            Theme.PADDING_LARGE, Theme.PADDING_LARGE
        ));

        ventanaSuperior = new VentanaSuperior();
        chatsRecientes = new VentanaChatsRecientes();
        chatActual = new VentanaChatActual();

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, chatsRecientes, chatActual);
        splitPane.setDividerLocation(Theme.SPLIT_PANE_DIVIDER);
        splitPane.setDividerSize(5);
        splitPane.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 1, Theme.COLOR_PRINCIPAL));

        mainPanel.add(ventanaSuperior, BorderLayout.NORTH);
        mainPanel.add(splitPane, BorderLayout.CENTER);
        add(mainPanel);

        setVisible(true);
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VentanaPrincipal());
    }
}

