package umu.tds.app.ventanas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.stream.Collectors;

import umu.tds.app.AppChat.Contacto;
import umu.tds.app.AppChat.ContactoIndividual;
import umu.tds.app.AppChat.Controlador;
import umu.tds.app.AppChat.ObserverChats;
import umu.tds.app.AppChat.ObserverContactos;
import umu.tds.app.AppChat.Theme;

/**
 * Panel superior de la interfaz principal que muestra las opciones de usuario,
 * contactos y funcionalidades adicionales como búsqueda, ajustes y exportación.
 * También actúa como observador de cambios en los contactos y chats recientes.
 */
public class VentanaSuperior extends JPanel implements ObserverChats, ObserverContactos {
    private static final long serialVersionUID = 1L;
    private JComboBox<String> contactosDropdown;
    private JButton searchButton, contactsButton, premiumButton, settingsButton, logoutButton;
    private JLabel userLabel, userImage;
    private Controlador controlador;
    private static final int DISPLAY_IMAGE_SIZE = 50;
    private boolean isDropdownListenerActive = true;

    /**
     * Constructor principal. Inicializa la interfaz y se suscribe a eventos de observador.
     */
    public VentanaSuperior() {
        controlador = Controlador.getInstancia();
        controlador.addObserverChats(this);
        controlador.addObserverContactos(this);

        setLayout(new BorderLayout());
        setBackground(Theme.COLOR_HEADER);
        setBorder(BorderFactory.createEmptyBorder(Theme.PADDING_SMALL, Theme.PADDING_MEDIUM, Theme.PADDING_SMALL, Theme.PADDING_MEDIUM));

        JPanel optionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        optionsPanel.setOpaque(false);

        contactosDropdown = new JComboBox<>();
        contactosDropdown.setRenderer(new CustomComboBoxRenderer());
        contactosDropdown.setBackground(Theme.COLOR_SECUNDARIO);
        contactosDropdown.setForeground(Theme.COLOR_PRINCIPAL);
        contactosDropdown.setPreferredSize(new Dimension(200, 30));
        actualizarContactosDropdown();
        contactosDropdown.addActionListener(e -> seleccionarContactoDesdeDropdown());

        optionsPanel.add(new JLabel("Mis Contactos:"));
        optionsPanel.add(contactosDropdown);

        searchButton = createStyledButton("Buscar", e -> new VentanaBusqueda().setVisible(true), "Buscar mensajes o contactos");
        contactsButton = createStyledButton("Contactos", e -> new VentanaContactos().setVisible(true), "Gestionar contactos");
        settingsButton = createStyledButton("Ajustes", e -> new VentanaAjustes().setVisible(true), "Configurar ajustes");
        premiumButton = createStyledButton("Premium", e -> mostrarDialogoPremium(), "Hazte Premium");
        logoutButton = createStyledButton("Cerrar Sesion", e -> cerrarSesion(), "Cerrar sesión");
        JButton exportPdfButton = createStyledButton("Exportar PDF", e -> exportarPDF(), "Exportar agenda y chats a PDF");

        optionsPanel.add(searchButton);
        optionsPanel.add(contactsButton);
        optionsPanel.add(premiumButton);
        optionsPanel.add(settingsButton);
        optionsPanel.add(exportPdfButton);
        add(optionsPanel, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);

        JPanel userPanel = new JPanel(new BorderLayout(5, 0));
        userPanel.setOpaque(false);

        userLabel = new JLabel(controlador.getNombreUserActual());
        userLabel.setForeground(Theme.COLOR_TEXTO);
        userLabel.setFont(Theme.FONT_BOLD_MEDIUM);

        ImageIcon iconoRecibido = controlador.getIconoUserActual();
        userImage = new JLabel();
        userImage.setIcon(getScaledIcon(iconoRecibido, DISPLAY_IMAGE_SIZE));

        userPanel.add(userLabel, BorderLayout.CENTER);
        userPanel.add(userImage, BorderLayout.WEST);

        rightPanel.add(userPanel);
        rightPanel.add(logoutButton);

        add(rightPanel, BorderLayout.EAST);
        SwingUtilities.invokeLater(this::inicializarContactoActual);
    }

    private void seleccionarContactoDesdeDropdown() {
        if (!isDropdownListenerActive) return;
        String selectedItem = (String) contactosDropdown.getSelectedItem();
        if (selectedItem == null || "Seleccione un contacto...".equals(selectedItem)) {
            controlador.setContactoActual(null);
        } else {
            String nombre = selectedItem.substring(selectedItem.indexOf(":") + 2);
            Contacto contacto = controlador.obtenerContactoPorNombre(nombre);
            controlador.setContactoActual(contacto);
        }
    }

    private JButton createStyledButton(String text, ActionListener action, String tooltip) {
        JButton button = new JButton(text);
        button.setBackground(Theme.COLOR_PRINCIPAL);
        button.setForeground(Theme.COLOR_TEXTO);
        button.setFont(Theme.FONT_BOLD_MEDIUM);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.COLOR_ACENTO, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        button.setToolTipText(tooltip);
        button.addActionListener(action);
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) { button.setBackground(Theme.COLOR_HOVER); }
            public void mouseExited(MouseEvent evt) { button.setBackground(Theme.COLOR_PRINCIPAL); }
        });
        return button;
    }

    private ImageIcon getScaledIcon(ImageIcon srcIcon, int size) {
        if (srcIcon == null || srcIcon.getImage() == null) return new ImageIcon();
        Image scaledImage = srcIcon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }


private void mostrarDialogoPremium() {
    if (controlador.isPremiumUserActual()) {
        JOptionPane.showMessageDialog(this, 
            "¡Ya eres un usuario Premium!", 
            "Información", 
            JOptionPane.INFORMATION_MESSAGE);
        return;
    }
    
    String resumen = controlador.getResumenDescuentoPremium();
    if (resumen == null) return;

    int decision = JOptionPane.showConfirmDialog(
        this, 
        resumen + "\n\n¿Desea activar Premium?", 
        "Resumen de descuentos",
        JOptionPane.YES_NO_OPTION,
        JOptionPane.QUESTION_MESSAGE
    );

    if (decision == JOptionPane.YES_OPTION) {
        controlador.confirmarActivacionPremium();
        
        JOptionPane.showMessageDialog(this, 
            "¡Felicidades! Ya eres usuario Premium.", 
            "Activación Completada", 
            JOptionPane.INFORMATION_MESSAGE);
    }
}

    private void cerrarSesion() {
        controlador.cerrarSesion();
        Window ancestor = SwingUtilities.getWindowAncestor(this);
        if (ancestor != null) ancestor.dispose();
        new VentanaLogin().setVisible(true);
    }

    private void exportarPDF() {
        if (!controlador.isPremiumUserActual()) {
            JOptionPane.showMessageDialog(this, "Solo los usuarios Premium pueden exportar sus datos.", "Función Premium", JOptionPane.WARNING_MESSAGE);
            return;
        }
        List<ContactoIndividual> contactosIndividuales = controlador.obtenerContactosConocidos().stream()
            .filter(c -> c instanceof ContactoIndividual)
            .map(c -> (ContactoIndividual) c)
            .collect(Collectors.toList());
        if (contactosIndividuales.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No tienes contactos individuales para exportar un chat.", "Sin contactos", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String[] opciones = contactosIndividuales.stream().map(Contacto::getNombre).toArray(String[]::new);
        String nombreSeleccionado = (String) JOptionPane.showInputDialog(this, "Selecciona el chat que deseas incluir en el informe:",
            "Seleccionar Chat para Exportar", JOptionPane.PLAIN_MESSAGE, null, opciones, opciones[0]);
        if (nombreSeleccionado == null) return;
        ContactoIndividual contactoSeleccionado = (ContactoIndividual) controlador.obtenerContactoPorNombre(nombreSeleccionado);
        if (contactoSeleccionado == null) return;

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar Informe PDF");
        fileChooser.setSelectedFile(new java.io.File("Informe_AppChat_" + controlador.getUsuarioActual().getNombre() + ".pdf"));
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String rutaDestino = fileChooser.getSelectedFile().getAbsolutePath();
            controlador.exportarPdfConDatos(rutaDestino, contactoSeleccionado);
        }
    }

    private void actualizarContactosDropdown() {
        isDropdownListenerActive = false;
        Object selected = contactosDropdown.getSelectedItem();
        contactosDropdown.removeAllItems();
        contactosDropdown.addItem("Seleccione un contacto...");
        List<Contacto> contactosConocidos = controlador.obtenerContactosConocidos();
        for (Contacto contacto : contactosConocidos) {
            String prefixedName = (contacto instanceof ContactoIndividual)
                ? "Individual: " + contacto.getNombre()
                : "Grupo: " + contacto.getNombre();
            contactosDropdown.addItem(prefixedName);
        }
        contactosDropdown.setSelectedItem(selected);
        if (contactosDropdown.getSelectedIndex() == -1) {
            contactosDropdown.setSelectedIndex(0);
        }
        isDropdownListenerActive = true;
    }

    public void inicializarContactoActual() {
        Contacto contactoInicial = controlador.getContactoActual();
        updateContactoActual(contactoInicial);
    }

    @Override
    public void updateChatsRecientes(String[] chatsRecientes) {
        userLabel.setText(controlador.getNombreUserActual());
        userImage.setIcon(getScaledIcon(controlador.getIconoUserActual(), DISPLAY_IMAGE_SIZE));
    }

    @Override
    public void updateContactoActual(Contacto contacto) {
        if (contactosDropdown.getItemCount() == 0) return;
        isDropdownListenerActive = false;
        if (contacto == null || (contacto instanceof ContactoIndividual && ((ContactoIndividual) contacto).isDesconocido())) {
            contactosDropdown.setSelectedIndex(0);
        } else {
            String prefixedName = (contacto instanceof ContactoIndividual)
                ? "Individual: " + contacto.getNombre()
                : "Grupo: " + contacto.getNombre();
            contactosDropdown.setSelectedItem(prefixedName);
        }
        isDropdownListenerActive = true;
    }

    @Override
    public void updateListaContactos() {
        actualizarContactosDropdown();
    }

    private class CustomComboBoxRenderer extends DefaultListCellRenderer {
        private static final long serialVersionUID = 1L;
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (c instanceof JLabel) {
                JLabel label = (JLabel) c;
                label.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
                if (isSelected) {
                    label.setBackground(Theme.COLOR_HOVER);
                    label.setForeground(Theme.COLOR_TEXTO);
                } else {
                    label.setBackground(Theme.COLOR_SECUNDARIO);
                    label.setForeground(Theme.COLOR_PRINCIPAL);
                }
            }
            return c;
        }
    }
}
