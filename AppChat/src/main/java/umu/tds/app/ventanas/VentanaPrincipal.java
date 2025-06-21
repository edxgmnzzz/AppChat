package umu.tds.app.ventanas;

import javax.swing.*;

import umu.tds.app.AppChat.Theme;
import java.awt.*;

/**
 * Ventana principal de la aplicación AppChat.
 * Se encarga de componer e integrar las subventanas:
 * - {@link VentanaSuperior}: parte superior con barra de usuario y controles.
 * - {@link VentanaChatsRecientes}: lista lateral de chats recientes.
 * - {@link VentanaChatActual}: ventana central de mensajes del chat activo.
 * 
 * Aplica estilos visuales definidos por la clase {@link Theme}.
 * Al iniciarse, se maximiza a pantalla completa.
 * 
 */
public class VentanaPrincipal extends JFrame {
    private static final long serialVersionUID = 1L;

    /** Panel superior con información del usuario y botones principales. */
    private VentanaSuperior ventanaSuperior;

    /** Panel lateral izquierdo con la lista de chats recientes. */
    private VentanaChatsRecientes chatsRecientes;

    /** Panel central con el contenido del chat actualmente activo. */
    private VentanaChatActual chatActual;

    /**
     * Constructor. Inicializa y muestra la ventana principal maximizada.
     * Configura el diseño general y compone los componentes internos.
     */
    public VentanaPrincipal() {
        setTitle("AppChat");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Pantalla completa
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

        // División horizontal entre lista de chats y contenido del chat
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, chatsRecientes, chatActual);
        splitPane.setDividerLocation(Theme.SPLIT_PANE_DIVIDER);
        splitPane.setDividerSize(5);
        splitPane.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 1, Theme.COLOR_PRINCIPAL));

        mainPanel.add(ventanaSuperior, BorderLayout.NORTH);
        mainPanel.add(splitPane, BorderLayout.CENTER);
        add(mainPanel);

        setVisible(true);
    }

    /**
     * Método principal de arranque de la aplicación.
     * Lanza la interfaz gráfica dentro del hilo de eventos Swing.
     *
     * @param args Argumentos de línea de comandos (no utilizados)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VentanaPrincipal());
    }
}
