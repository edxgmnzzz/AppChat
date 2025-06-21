package umu.tds.app.ventanas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;
import umu.tds.app.AppChat.Controlador;
import umu.tds.app.AppChat.Mensaje;
import umu.tds.app.AppChat.Theme;

/**
 * Clase que representa una ventana de búsqueda de mensajes en la aplicación.
 * Permite al usuario buscar mensajes por texto, teléfono o nombre de contacto.
 */
public class VentanaBusqueda extends JFrame {
    private static final long serialVersionUID = 1L;
    private Controlador controlador;
    private JTextField textSearch;
    private JTextField phoneSearch;
    private JTextField contactSearch;
    private JPanel messagesPanel;

    /**
     * Crea una nueva instancia de la ventana de búsqueda.
     */
    public VentanaBusqueda() {
        controlador = Controlador.getInstancia();
        setTitle("Buscar Mensajes");
        setSize(700, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setShape(new java.awt.geom.RoundRectangle2D.Double(0, 0, 700, 600, Theme.BORDER_RADIUS, Theme.BORDER_RADIUS));
        inicializarInterfaz();
    }

    /**
     * Inicializa los componentes gráficos de la ventana.
     */
    private void inicializarInterfaz() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Theme.COLOR_FONDO);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(Theme.PADDING_MEDIUM, Theme.PADDING_MEDIUM, Theme.PADDING_MEDIUM, Theme.PADDING_MEDIUM));
        add(mainPanel);

        JPanel headerPanel = crearBarraSuperior();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel searchPanel = crearPanelBusqueda();
        mainPanel.add(searchPanel, BorderLayout.CENTER);

        JScrollPane resultadosScroll = crearPanelResultados();
        mainPanel.add(resultadosScroll, BorderLayout.SOUTH);
    }

    /**
     * Crea la barra superior con el título y el botón de cierre.
     * @return JPanel con la barra de título.
     */
    private JPanel crearBarraSuperior() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Theme.COLOR_HEADER);
        headerPanel.setPreferredSize(new Dimension(700, Theme.TITLE_BAR_HEIGHT));

        JLabel header = new JLabel("  🔎 Buscador de Mensajes", JLabel.LEFT);
        header.setFont(Theme.FONT_BOLD_MEDIUM);
        header.setForeground(Color.WHITE);
        headerPanel.add(header, BorderLayout.WEST);

        JButton closeButton = new JButton("×");
        closeButton.setPreferredSize(new Dimension(45, Theme.TITLE_BAR_HEIGHT));
        closeButton.setFocusPainted(false);
        closeButton.setBorderPainted(false);
        closeButton.setContentAreaFilled(false);
        closeButton.setForeground(Color.WHITE);
        closeButton.setFont(Theme.FONT_BOLD_MEDIUM);
        closeButton.addActionListener(e -> dispose());
        closeButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                closeButton.setBackground(Theme.COLOR_ACENTO);
                closeButton.setContentAreaFilled(true);
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                closeButton.setContentAreaFilled(false);
            }
        });
        headerPanel.add(closeButton, BorderLayout.EAST);

        return headerPanel;
    }

    /**
     * Crea el panel de búsqueda con los campos de texto y el botón de búsqueda.
     * @return JPanel con los controles de búsqueda.
     */
    private JPanel crearPanelBusqueda() {
        JPanel searchPanel = new JPanel(new GridLayout(2, 4, 10, 10));
        searchPanel.setBackground(Theme.COLOR_FONDO);

        searchPanel.add(new JLabel("Texto:", JLabel.RIGHT)).setFont(Theme.FONT_BOLD_MEDIUM);
        textSearch = crearCampoTexto();
        searchPanel.add(textSearch);

        searchPanel.add(new JLabel("Teléfono:", JLabel.RIGHT)).setFont(Theme.FONT_BOLD_MEDIUM);
        phoneSearch = crearCampoTexto();
        searchPanel.add(phoneSearch);

        searchPanel.add(new JLabel("Contacto:", JLabel.RIGHT)).setFont(Theme.FONT_BOLD_MEDIUM);
        contactSearch = crearCampoTexto();
        searchPanel.add(contactSearch);

        JButton buscarBtn = crearBotonAccion("Buscar", e -> realizarBusqueda());
        searchPanel.add(new JLabel());
        searchPanel.add(buscarBtn);

        return searchPanel;
    }

    /**
     * Crea un campo de texto con estilo.
     * @return JTextField personalizado.
     */
    private JTextField crearCampoTexto() {
        JTextField campo = new JTextField();
        campo.setFont(Theme.FONT_PLAIN_MEDIUM);
        campo.setBackground(Theme.COLOR_SECUNDARIO);
        campo.setForeground(Theme.COLOR_TEXTO);
        campo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.COLOR_PRINCIPAL, 1), 
            BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        return campo;
    }

    /**
     * Crea un botón con estilo y acción asociada.
     * @param texto Texto del botón.
     * @param accion Acción al hacer clic.
     * @return JButton personalizado.
     */
    private JButton crearBotonAccion(String texto, ActionListener accion) {
        JButton boton = new JButton(texto);
        boton.setFont(Theme.FONT_BOLD_MEDIUM);
        boton.setForeground(Color.WHITE);
        boton.setBackground(Theme.COLOR_PRINCIPAL);
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.COLOR_ACENTO, 1), 
            BorderFactory.createEmptyBorder(8, 20, 8, 20)));
        boton.addActionListener(accion);
        boton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                boton.setBackground(Theme.COLOR_HOVER);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                boton.setBackground(Theme.COLOR_PRINCIPAL);
            }
        });
        return boton;
    }

    /**
     * Crea el panel que mostrará los resultados de la búsqueda.
     * @return JScrollPane con los resultados.
     */
    private JScrollPane crearPanelResultados() {
        messagesPanel = new JPanel();
        messagesPanel.setLayout(new BoxLayout(messagesPanel, BoxLayout.Y_AXIS));
        messagesPanel.setBackground(Theme.COLOR_SECUNDARIO);

        JScrollPane scrollPane = new JScrollPane(messagesPanel);
        scrollPane.setBorder(BorderFactory.createLineBorder(Theme.COLOR_PRINCIPAL, 1));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setPreferredSize(new Dimension(600, 350));
        return scrollPane;
    }

    /**
     * Realiza la búsqueda de mensajes según los criterios introducidos.
     */
    private void realizarBusqueda() {
        String texto = textSearch.getText().trim();
        String telefono = phoneSearch.getText().trim();
        String nombre = contactSearch.getText().trim();

        List<Mensaje> resultados = controlador.buscarMensajes(texto, telefono, nombre);
        mostrarResultados(resultados);
    }

    /**
     * Muestra los mensajes encontrados en el panel de resultados.
     * @param resultados Lista de mensajes encontrados.
     */
    private void mostrarResultados(List<Mensaje> resultados) {
        messagesPanel.removeAll();
        if (resultados.isEmpty()) {
            JLabel sinResultados = new JLabel("No se encontraron mensajes");
            sinResultados.setFont(Theme.FONT_BOLD_MEDIUM);
            sinResultados.setAlignmentX(Component.CENTER_ALIGNMENT);
            messagesPanel.add(Box.createVerticalStrut(20));
            messagesPanel.add(sinResultados);
        } else {
            for (Mensaje m : resultados) {
                String linea = String.format("[%s] %s -> %s: %s",
                    m.getHora().toString(),
                    m.getEmisor().getNombre(),
                    m.getReceptor().getNombre(),
                    m.getTexto());
                JLabel mensajeLabel = new JLabel(linea);
                mensajeLabel.setFont(Theme.FONT_PLAIN_MEDIUM);
                mensajeLabel.setForeground(Theme.COLOR_TEXTO);
                mensajeLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                messagesPanel.add(mensajeLabel);
            }
        }
        messagesPanel.revalidate();
        messagesPanel.repaint();
    }
}
