package umu.tds.app.ventanas;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import umu.tds.app.AppChat.Contacto;
import umu.tds.app.AppChat.ContactoIndividual;
import umu.tds.app.AppChat.Controlador;
import umu.tds.app.AppChat.Theme;

/**
 * Ventana para crear un nuevo grupo en la aplicación de mensajería.
 * Permite introducir un nombre, seleccionar contactos y añadir una URL de imagen.
 * Aplica estilo visual definido por la clase {@link Theme}.
 * 
 * La ventana se comporta como un JDialog modal y sin bordes decorativos.
 * 
 */
public class VentanaNuevoGrupo extends JDialog {
    private static final long serialVersionUID = 1L;

    /** Campo para introducir el nombre del grupo. */
    private JTextField groupNameField;

    /** Campo para introducir la URL de la imagen del grupo. */
    private JTextField imageUrlField;

    /** Lista de contactos disponibles que se pueden añadir al grupo. */
    private JList<String> availableContactsList;

    /** Lista de contactos seleccionados para formar parte del grupo. */
    private JList<String> selectedMembersList;

    /** Controlador de la lógica de la aplicación. */
    private Controlador controlador;

    /** Modelo para los contactos disponibles. */
    private DefaultListModel<String> availableModel;

    /** Modelo para los miembros seleccionados. */
    private DefaultListModel<String> selectedModel;

    /**
     * Constructor. Inicializa la ventana, controlador y los componentes visuales.
     */
    public VentanaNuevoGrupo() {
        controlador = Controlador.getInstancia();
        configurarVentana();
        crearComponentes();
    }

    /**
     * Configura propiedades básicas de la ventana como tamaño, posición y estilo sin decoración.
     */
    private void configurarVentana() {
        setSize(600, 400);
        setLocationRelativeTo(getParent());
        setUndecorated(true);
        setShape(new java.awt.geom.RoundRectangle2D.Double(0, 0, 600, 400, Theme.BORDER_RADIUS, Theme.BORDER_RADIUS));
    }

    /**
     * Crea todos los componentes internos de la ventana y los añade al panel principal.
     */
    private void crearComponentes() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(Theme.COLOR_FONDO);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(Theme.PADDING_MEDIUM, Theme.PADDING_MEDIUM, Theme.PADDING_MEDIUM, Theme.PADDING_MEDIUM));

        mainPanel.add(crearBarraTitulo(), BorderLayout.NORTH);
        mainPanel.add(crearPanelContenido(), BorderLayout.CENTER);

        add(mainPanel);
    }

    /**
     * Crea la barra superior con título y botón para cerrar la ventana.
     * @return panel con la barra de título
     */
    private JPanel crearBarraTitulo() {
        JPanel barraTitulo = new JPanel(new BorderLayout());
        barraTitulo.setBackground(Theme.COLOR_PRINCIPAL);
        barraTitulo.setPreferredSize(new Dimension(600, Theme.TITLE_BAR_HEIGHT));

        JLabel labelTitulo = new JLabel("  Crear Nuevo Grupo");
        labelTitulo.setForeground(Theme.COLOR_SECUNDARIO);
        labelTitulo.setFont(Theme.FONT_BOLD_MEDIUM);
        barraTitulo.add(labelTitulo, BorderLayout.WEST);

        JButton cerrarBoton = new JButton("×");
        cerrarBoton.setPreferredSize(new Dimension(45, Theme.TITLE_BAR_HEIGHT));
        cerrarBoton.setFocusPainted(false);
        cerrarBoton.setBorderPainted(false);
        cerrarBoton.setContentAreaFilled(false);
        cerrarBoton.setForeground(Theme.COLOR_SECUNDARIO);
        cerrarBoton.setFont(Theme.FONT_BOLD_MEDIUM);
        cerrarBoton.addActionListener(e -> dispose());
        cerrarBoton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                cerrarBoton.setBackground(Theme.COLOR_ACENTO);
                cerrarBoton.setContentAreaFilled(true);
            }
            public void mouseExited(MouseEvent e) {
                cerrarBoton.setContentAreaFilled(false);
            }
        });

        JPanel botonesControl = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        botonesControl.setOpaque(false);
        botonesControl.add(cerrarBoton);
        barraTitulo.add(botonesControl, BorderLayout.EAST);

        return barraTitulo;
    }

    /**
     * Crea el contenido principal con campos, listas de contactos y botones.
     * @return panel con los componentes principales
     */
    private JPanel crearPanelContenido() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(Theme.COLOR_FONDO);

        JPanel namePanel = new JPanel(new FlowLayout());
        namePanel.setBackground(Theme.COLOR_FONDO);
        JLabel nameLabel = new JLabel("Nombre del Grupo:");
        nameLabel.setFont(Theme.FONT_BOLD_MEDIUM);
        nameLabel.setForeground(Theme.COLOR_SECUNDARIO);
        groupNameField = new JTextField(20);
        groupNameField.setFont(Theme.FONT_PLAIN_MEDIUM);
        groupNameField.setForeground(Theme.COLOR_PRINCIPAL);
        groupNameField.setBackground(Theme.COLOR_SECUNDARIO);
        groupNameField.setBorder(BorderFactory.createLineBorder(Theme.COLOR_PRINCIPAL, 2));
        namePanel.add(nameLabel);
        namePanel.add(groupNameField);

        JPanel selectionPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        selectionPanel.setBackground(Theme.COLOR_FONDO);

        availableModel = new DefaultListModel<>();
        cargarContactosDisponibles();
        availableContactsList = new JList<>(availableModel);
        availableContactsList.setBackground(Theme.COLOR_SECUNDARIO);
        availableContactsList.setForeground(Theme.COLOR_PRINCIPAL);
        JScrollPane availableScrollPane = new JScrollPane(availableContactsList);
        JPanel availablePanel = new JPanel(new BorderLayout());
        availablePanel.setBackground(Theme.COLOR_FONDO);
        availablePanel.setBorder(BorderFactory.createTitledBorder("Contactos Disponibles"));
        availablePanel.add(availableScrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        buttonPanel.setBackground(Theme.COLOR_FONDO);
        JButton addButton = createStyledButton(">>", e -> agregarMiembro());
        JButton removeButton = createStyledButton("<<", e -> quitarMiembro());
        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);

        selectedModel = new DefaultListModel<>();
        selectedMembersList = new JList<>(selectedModel);
        selectedMembersList.setBackground(Theme.COLOR_SECUNDARIO);
        selectedMembersList.setForeground(Theme.COLOR_PRINCIPAL);
        JScrollPane selectedScrollPane = new JScrollPane(selectedMembersList);
        JPanel selectedPanel = new JPanel(new BorderLayout());
        selectedPanel.setBackground(Theme.COLOR_FONDO);
        selectedPanel.setBorder(BorderFactory.createTitledBorder("Miembros Seleccionados"));
        selectedPanel.add(selectedScrollPane, BorderLayout.CENTER);

        selectionPanel.add(availablePanel);
        selectionPanel.add(buttonPanel);
        selectionPanel.add(selectedPanel);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(Theme.COLOR_FONDO);
        JButton createButton = createStyledButton("Crear Grupo", e -> crearGrupo());
        JButton cancelButton = createStyledButton("Cancelar", e -> dispose());

        JLabel imageLabel = new JLabel("URL de la Imagen:");
        imageLabel.setFont(Theme.FONT_BOLD_MEDIUM);
        imageLabel.setForeground(Theme.COLOR_SECUNDARIO);
        imageUrlField = new JTextField(15);
        imageUrlField.setFont(Theme.FONT_PLAIN_MEDIUM);
        imageUrlField.setForeground(Theme.COLOR_PRINCIPAL);
        imageUrlField.setBackground(Theme.COLOR_SECUNDARIO);
        imageUrlField.setBorder(BorderFactory.createLineBorder(Theme.COLOR_PRINCIPAL, 2));

        bottomPanel.add(imageLabel);
        bottomPanel.add(imageUrlField);
        bottomPanel.add(createButton);
        bottomPanel.add(cancelButton);

        mainPanel.add(namePanel, BorderLayout.NORTH);
        mainPanel.add(selectionPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        return mainPanel;
    }

    /**
     * Crea un botón estilizado con los colores definidos en el tema.
     * 
     * @param text Texto del botón.
     * @param action Acción que se ejecutará al pulsarlo.
     * @return botón personalizado
     */
    private JButton createStyledButton(String text, ActionListener action) {
        JButton button = new JButton(text);
        button.setBackground(Theme.COLOR_PRINCIPAL);
        button.setForeground(Theme.COLOR_TEXTO);
        button.setFont(Theme.FONT_BOLD_MEDIUM);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.COLOR_ACENTO, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
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

    /**
     * Carga los contactos individuales disponibles del controlador al modelo de lista.
     */
    private void cargarContactosDisponibles() {
        List<Contacto> contactos = controlador.obtenerContactos();
        for (Contacto c : contactos) {
            if (c instanceof ContactoIndividual) {
                availableModel.addElement(c.getNombre());
            }
        }
    }

    /**
     * Mueve el contacto seleccionado a la lista de miembros del grupo.
     */
    private void agregarMiembro() {
        String selectedContact = availableContactsList.getSelectedValue();
        if (selectedContact != null) {
            availableModel.removeElement(selectedContact);
            selectedModel.addElement(selectedContact);
        }
    }

    /**
     * Quita el contacto de la lista de miembros seleccionados y lo devuelve a disponibles.
     */
    private void quitarMiembro() {
        String selectedMember = selectedMembersList.getSelectedValue();
        if (selectedMember != null) {
            selectedModel.removeElement(selectedMember);
            availableModel.addElement(selectedMember);
        }
    }

    /**
     * Valida el nombre del grupo, selecciona los miembros y crea el grupo.
     * Si se proporciona una URL válida, se usará como imagen del grupo.
     */
    private void crearGrupo() {
        String groupName = groupNameField.getText().trim();
        String urlFoto = imageUrlField.getText().trim();

        if (groupName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, introduce un nombre para el grupo.",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (controlador.existeContacto(groupName)) {
            JOptionPane.showMessageDialog(this, "El nombre del grupo ya existe.",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (selectedModel.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione al menos un miembro.",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<ContactoIndividual> miembros = new ArrayList<>();
        List<Contacto> contactos = controlador.obtenerContactos();
        for (int i = 0; i < selectedModel.size(); i++) {
            String miembroNombre = selectedModel.getElementAt(i);
            for (Contacto c : contactos) {
                if (c instanceof ContactoIndividual && c.getNombre().equals(miembroNombre)) {
                    miembros.add((ContactoIndividual) c);
                    break;
                }
            }
        }

        ImageIcon icono = null;

        if (!urlFoto.isBlank()) {
            try {
                BufferedImage image = ImageIO.read(new URI(urlFoto).toURL());
                icono = new ImageIcon(image);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "No se pudo cargar la imagen desde la URL proporcionada.\nSe creará el grupo sin imagen.",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            }
        }

        controlador.crearGrupo(groupName, miembros, icono, urlFoto);
        JOptionPane.showMessageDialog(this, "Grupo '" + groupName + "' creado con éxito.",
            "Éxito", JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }
}
