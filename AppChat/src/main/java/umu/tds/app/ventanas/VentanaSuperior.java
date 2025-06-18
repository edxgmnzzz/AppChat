package umu.tds.app.ventanas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

import umu.tds.app.AppChat.Contacto;
import umu.tds.app.AppChat.ContactoIndividual;
import umu.tds.app.AppChat.Controlador;
import umu.tds.app.AppChat.ObserverChats;
import umu.tds.app.AppChat.ObserverContactos;
import umu.tds.app.AppChat.Theme;
import umu.tds.app.AppChat.Usuario;

public class VentanaSuperior extends JPanel implements ObserverChats, ObserverContactos {
    private static final long serialVersionUID = 1L;
    private JComboBox<String> contactos;
    private JButton searchButton, contactsButton, premiumButton, settingsButton, logoutButton;
    private JLabel userLabel, userImage;
    private Controlador controlador;
    private static final int DISPLAY_IMAGE_SIZE = 50;

    public VentanaSuperior() {
        controlador = Controlador.getInstancia();
        controlador.addObserverChats(this);
        controlador.addObserverContactos(this);
        setLayout(new BorderLayout());
        setBackground(Theme.COLOR_HEADER);
        setBorder(BorderFactory.createEmptyBorder(Theme.PADDING_SMALL, Theme.PADDING_MEDIUM, Theme.PADDING_SMALL, Theme.PADDING_MEDIUM));

        JPanel optionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        optionsPanel.setOpaque(false);
        JTextField telefonoInput = new JTextField();
        telefonoInput.setPreferredSize(new Dimension(130, 30));
        telefonoInput.setToolTipText("Escribe número de teléfono...");
        JButton chatPorNumeroButton = createStyledButton("Chat", e -> {
            String telefono = telefonoInput.getText().trim();
            if (!telefono.matches("\\d{9}")) {
                JOptionPane.showMessageDialog(this, "Número inválido. Debe tener 9 dígitos.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Usuario destino = controlador.buscarUsuarioPorTelefono(telefono);
            if (destino == null) {
                JOptionPane.showMessageDialog(this, "No existe un usuario con ese número.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Crear contacto temporal si no está en la lista
            boolean yaExiste = controlador.getUsuarioActual().getContactos().stream()
                .filter(c -> c instanceof ContactoIndividual)
                .map(c -> ((ContactoIndividual) c).getUsuario())
                .anyMatch(u -> u != null && u.equals(destino));

            Contacto contacto;
            if (yaExiste) {
                contacto = controlador.obtenerContactoPorUsuario(destino); // implementa esto si no lo tienes
            } else {
                contacto = new ContactoIndividual(destino.getNombre(), destino.getTelefono(), destino);
            }

            controlador.setContactoActual(contacto);
        }, "Iniciar chat por teléfono");

        contactos = new JComboBox<>();
        contactos.setRenderer(new CustomComboBoxRenderer());
        contactos.setBackground(Theme.COLOR_SECUNDARIO);
        contactos.setForeground(Theme.COLOR_PRINCIPAL);
        contactos.setPreferredSize(new Dimension(200, 30));
        actualizarContactosDropdown();

        contactos.addActionListener(e -> {
            String selected = (String) contactos.getSelectedItem();
            if (selected == null || "Seleccione un contacto...".equals(selected)) {
                controlador.setContactoActual(null);
            } else {
                String nombre = selected.startsWith("Individual: ") ? selected.substring(12) : selected.substring(7);
                Contacto contacto = controlador.obtenerContactoPorNombre(nombre);
                controlador.setContactoActual(contacto);
            }
        });
        optionsPanel.add(telefonoInput);
        optionsPanel.add(chatPorNumeroButton);


        searchButton = createStyledButton("Buscar", e -> {
            VentanaBusqueda ventana = new VentanaBusqueda();
            ventana.setVisible(true);
        }, "Buscar mensajes o contactos");

        contactsButton = createStyledButton("Contactos", e -> {
            VentanaContactos ventana = new VentanaContactos();
            ventana.setVisible(true);
        }, "Gestionar contactos");

        settingsButton = createStyledButton("Ajustes", e -> {
            VentanaAjustes ventana = new VentanaAjustes();
            ventana.setVisible(true);
        }, "Configurar ajustes");
        premiumButton = createStyledButton("Premium", e -> mostrarDialogoPremium(), "Hazte Premium");


        logoutButton = createStyledButton("Cerrar Sesión", e -> cerrarSesion(), "Cerrar sesión");
        JButton exportPdfButton = createStyledButton("Exportar PDF", e -> exportarPDF(), "Exportar datos y mensajes a PDF");


        optionsPanel.add(contactos);
        optionsPanel.add(searchButton);
        optionsPanel.add(contactsButton);
        optionsPanel.add(premiumButton);
        optionsPanel.add(settingsButton);
        optionsPanel.add(logoutButton);
        optionsPanel.add(exportPdfButton);

        add(optionsPanel, BorderLayout.WEST);

        JPanel userPanel = new JPanel(new BorderLayout());
        userPanel.setOpaque(false);

        ImageIcon originalIcon = controlador.getIconoUserActual();
        ImageIcon scaledIcon = originalIcon.getImage() != null 
            ? new ImageIcon(originalIcon.getImage().getScaledInstance(DISPLAY_IMAGE_SIZE, DISPLAY_IMAGE_SIZE, Image.SCALE_SMOOTH))
            : new ImageIcon();
        userImage = new JLabel(scaledIcon);
        userImage.setBorder(BorderFactory.createLineBorder(Theme.COLOR_PRINCIPAL, 2));
        userImage.setPreferredSize(new Dimension(DISPLAY_IMAGE_SIZE, DISPLAY_IMAGE_SIZE));
        userPanel.add(userImage, BorderLayout.CENTER);

        userLabel = new JLabel(controlador.getNombreUserActual());
        userLabel.setForeground(Theme.COLOR_TEXTO);
        userLabel.setFont(Theme.FONT_BOLD_MEDIUM);
        userLabel.setHorizontalAlignment(SwingConstants.CENTER);
        userPanel.add(userLabel, BorderLayout.SOUTH);

        add(userPanel, BorderLayout.EAST);

        SwingUtilities.invokeLater(this::inicializarContactoActual); // Asegurar que se ejecute tras la inicialización
        
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
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(Theme.COLOR_HOVER);
            }
            public void mouseExited(MouseEvent evt) {
                button.setBackground(Theme.COLOR_PRINCIPAL);
            }
        });
        return button;
    }
    

    private void mostrarDialogoPremium() {
        if (controlador.isPremiumUserActual()) {
            JOptionPane.showMessageDialog(this, "Ya eres Premium!", "Información", JOptionPane.INFORMATION_MESSAGE);
        } else {
            controlador.activarPremiumConDescuento();
        }
    }

    private void cerrarSesion() {
        controlador.cerrarSesion();
        SwingUtilities.getWindowAncestor(this).dispose();
        new VentanaLogin().setVisible(true);
    }
    private void exportarPDF() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar PDF");
        fileChooser.setSelectedFile(new java.io.File("ExportacionAppChat.pdf"));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            String path = fileChooser.getSelectedFile().getAbsolutePath();
            controlador.exportarPdfConDatos(path);
        }
    }

    public void inicializarContactoActual() {
        Contacto contactoInicial = controlador.getContactoActual();
        if (contactoInicial != null) {
            String prefixedName = (contactoInicial instanceof ContactoIndividual)
                ? "Individual: " + contactoInicial.getNombre()
                : "Grupo: " + contactoInicial.getNombre();
            contactos.setSelectedItem(prefixedName);
        } else {
            contactos.setSelectedIndex(0);
        }
    }
    private void actualizarContactosDropdown() {
        contactos.removeAllItems();
        contactos.addItem("Seleccione un contacto...");
        List<Contacto> contactosList = controlador.obtenerContactos();
        for (Contacto contacto : contactosList) {
            String prefixedName = (contacto instanceof ContactoIndividual) 
                ? "Individual: " + contacto.getNombre() 
                : "Grupo: " + contacto.getNombre();
            contactos.addItem(prefixedName);
        }
    }

    @Override
    public void updateChatsRecientes(String[] chatsRecientes) {
        userLabel.setText(controlador.getNombreUserActual());
        ImageIcon originalIcon = controlador.getIconoUserActual();
        ImageIcon scaledIcon = originalIcon.getImage() != null 
            ? new ImageIcon(originalIcon.getImage().getScaledInstance(DISPLAY_IMAGE_SIZE, DISPLAY_IMAGE_SIZE, Image.SCALE_SMOOTH))
            : new ImageIcon();
        userImage.setIcon(scaledIcon);
        inicializarContactoActual();
    }

    @Override
    public void updateContactoActual(Contacto contacto) {
        if (contactos.getItemCount() == 0) return;
        if (contacto == null) {
            contactos.setSelectedIndex(0);
        } else {
            String prefixedName = (contacto instanceof ContactoIndividual) 
                ? "Individual: " + contacto.getNombre() 
                : "Grupo: " + contacto.getNombre();
            if (!prefixedName.equals(contactos.getSelectedItem())) {
                contactos.setSelectedItem(prefixedName);
            }
        }
    }

    @Override
    public void updateListaContactos() {
        actualizarContactosDropdown();
    }

    private class CustomComboBoxRenderer extends DefaultListCellRenderer {
        private static final long serialVersionUID = 1L;

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, 
                                                      boolean isSelected, boolean cellHasFocus) {
            Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (isSelected) {
                c.setBackground(Theme.COLOR_HOVER);
                c.setForeground(Theme.COLOR_TEXTO);
            } else {
                c.setBackground(Theme.COLOR_SECUNDARIO);
                c.setForeground(Theme.COLOR_PRINCIPAL);
            }
            return c;
        }
    }
}