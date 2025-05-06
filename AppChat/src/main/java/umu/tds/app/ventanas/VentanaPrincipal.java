package umu.tds.app.ventanas;

import javax.swing.*;
import umu.tds.app.AppChat.Contacto;
import umu.tds.app.AppChat.Controlador;
import java.util.List;
import umu.tds.app.AppChat.Theme;

import java.awt.*;
public class VentanaPrincipal extends JFrame {
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

        // 🧩 Crear los componentes principales
        ventanaSuperior = new VentanaSuperior();
        chatsRecientes = new VentanaChatsRecientes();
        chatActual = new VentanaChatActual();

        // 📐 Panel de división horizontal: izquierda (chats) y derecha (chat actual)
        JSplitPane splitPane = new JSplitPane(
            JSplitPane.HORIZONTAL_SPLIT, chatsRecientes, chatActual
        );
        splitPane.setDividerLocation(Theme.SPLIT_PANE_DIVIDER);
        splitPane.setDividerSize(5);
        splitPane.setBorder(BorderFactory.createMatteBorder(
            0, 1, 0, 1, Theme.COLOR_PRINCIPAL
        ));
        chatActual.setBorder(BorderFactory.createLineBorder(Color.RED));


        // 📌 Añadir al layout
        mainPanel.add(ventanaSuperior, BorderLayout.NORTH);
        mainPanel.add(splitPane, BorderLayout.CENTER);
        add(mainPanel);

        setVisible(true);

        // 🔄 Seleccionar contacto inicial si existe
        SwingUtilities.invokeLater(() -> {
            if (Controlador.getInstancia().getContactoActual() != null) {
                Controlador.getInstancia().setContactoActual(Controlador.getInstancia().getContactoActual());
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VentanaPrincipal());
    }
}
