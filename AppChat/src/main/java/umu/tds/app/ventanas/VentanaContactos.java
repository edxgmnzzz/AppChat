package umu.tds.app.ventanas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.stream.Collectors;

import umu.tds.app.AppChat.Contacto;
import umu.tds.app.AppChat.ContactoIndividual;
import umu.tds.app.AppChat.Controlador;
import umu.tds.app.AppChat.Grupo;
import umu.tds.app.AppChat.ObserverContactos;
import umu.tds.app.AppChat.Theme;

/**
 * Ventana para visualizar, agregar o eliminar contactos conocidos,
 * así como crear nuevos grupos y añadir contactos a ellos.
 */
public class VentanaContactos extends JDialog implements ObserverContactos {
    private static final long serialVersionUID = 1L;

    private JList<String> contactosList;
    private Controlador controlador;

    public VentanaContactos() {
        controlador = Controlador.getInstancia();
        controlador.addObserverContactos(this);
        configurarVentana();
        crearComponentes();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                controlador.removeObserverContactos(VentanaContactos.this);
            }
        });
    }

    private void configurarVentana() {
        setSize(500, 600);
        setLocationRelativeTo(getParent());
        setUndecorated(true);
        setShape(new java.awt.geom.RoundRectangle2D.Double(0, 0, 500, 600, Theme.BORDER_RADIUS, Theme.BORDER_RADIUS));
    }

    private void crearComponentes() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Theme.COLOR_FONDO);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(
            Theme.PADDING_MEDIUM, Theme.PADDING_MEDIUM, Theme.PADDING_MEDIUM, Theme.PADDING_MEDIUM
        ));

        mainPanel.add(crearBarraTitulo(), BorderLayout.NORTH);
        mainPanel.add(crearPanelContenido(), BorderLayout.CENTER);

        add(mainPanel);
    }

    private JPanel crearBarraTitulo() {
        JPanel barraTitulo = new JPanel(new BorderLayout());
        barraTitulo.setBackground(Theme.COLOR_PRINCIPAL);
        barraTitulo.setPreferredSize(new Dimension(500, Theme.TITLE_BAR_HEIGHT));

        JLabel labelTitulo = new JLabel("  Gestionar Contactos");
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

    private JPanel crearPanelContenido() {
        JPanel panelContenido = new JPanel(new BorderLayout());
        panelContenido.setBackground(Theme.COLOR_FONDO);

        contactosList = new JList<>();
        contactosList.setCellRenderer(new DefaultListCellRenderer() {
            private static final long serialVersionUID = 1L;
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                c.setBackground(isSelected ? Theme.COLOR_HOVER : Theme.COLOR_SECUNDARIO);
                c.setForeground(isSelected ? Theme.COLOR_TEXTO : Theme.COLOR_PRINCIPAL);
                return c;
            }
        });
        contactosList.setBackground(Theme.COLOR_SECUNDARIO);
        actualizarListaContactos();

        JScrollPane scrollPane = new JScrollPane(contactosList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(
            Theme.PADDING_SMALL, Theme.PADDING_SMALL, Theme.PADDING_SMALL, Theme.PADDING_SMALL));
        panelContenido.add(scrollPane, BorderLayout.CENTER);

        JPanel botonesPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        botonesPanel.setBackground(Theme.COLOR_FONDO);
        botonesPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        botonesPanel.add(createStyledButton("Agregar Contacto", e -> agregarContacto()));
        botonesPanel.add(createStyledButton("Eliminar Contacto", e -> eliminarContacto()));
        botonesPanel.add(createStyledButton("Crear Grupo", e -> new VentanaNuevoGrupo().setVisible(true)));
        botonesPanel.add(createStyledButton("Añadir a Grupo", e -> añadirContactoAGrupo()));

        panelContenido.add(botonesPanel, BorderLayout.SOUTH);

        return panelContenido;
    }

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

    private void agregarContacto() {
        VentanaNuevoContacto ventanaNuevoContacto = new VentanaNuevoContacto(this);
        ventanaNuevoContacto.setVisible(true);
        if (!ventanaNuevoContacto.isVisible()) {
            dispose();
        }
    }

    private void eliminarContacto() {
        String seleccionado = contactosList.getSelectedValue();
        if (seleccionado == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un contacto para eliminar.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String nombre = seleccionado.substring(seleccionado.indexOf(":") + 2);
        int confirmacion = JOptionPane.showConfirmDialog(
            this,
            "¿Estás seguro de que quieres eliminar a '" + nombre + "'?",
            "Confirmar Eliminación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (confirmacion == JOptionPane.YES_OPTION) {
            Contacto contacto = controlador.obtenerContactoPorNombre(nombre);
            if (contacto instanceof ContactoIndividual) {
                controlador.eliminarContacto((ContactoIndividual) contacto);
            } else {
                JOptionPane.showMessageDialog(this, "La eliminación de grupos no está implementada.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void añadirContactoAGrupo() {
        String seleccionado = contactosList.getSelectedValue();
        if (seleccionado == null || !seleccionado.startsWith("Individual: ")) {
            JOptionPane.showMessageDialog(this, "Selecciona un contacto individual primero.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String nombreContacto = seleccionado.substring(seleccionado.indexOf(":") + 2);
        Contacto contacto = controlador.obtenerContactoPorNombre(nombreContacto);
        if (!(contacto instanceof ContactoIndividual contactoInd)) return;

        List<Grupo> grupos = controlador.obtenerContactosConocidos().stream()
            .filter(c -> c instanceof Grupo)
            .map(c -> (Grupo) c)
            .collect(Collectors.toList());

        if (grupos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No tienes grupos creados.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String[] opciones = grupos.stream().map(Grupo::getNombre).toArray(String[]::new);
        String grupoSeleccionado = (String) JOptionPane.showInputDialog(this,
            "Selecciona el grupo al que añadir el contacto:",
            "Añadir a Grupo", JOptionPane.PLAIN_MESSAGE, null,
            opciones, opciones[0]);

        if (grupoSeleccionado == null) return;

        Grupo grupo = (Grupo) controlador.obtenerContactoPorNombre(grupoSeleccionado);

        boolean añadido = controlador.añadirContactoAGrupo(grupo, contactoInd);

        if (añadido) {
            JOptionPane.showMessageDialog(this, "Contacto añadido al grupo correctamente.");
        } else {
            JOptionPane.showMessageDialog(this, "El contacto ya pertenece al grupo o hubo un error.", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void actualizarListaContactos() {
        DefaultListModel<String> model = new DefaultListModel<>();
        for (Contacto contacto : controlador.obtenerContactosConocidos()) {
            String prefixedName = (contacto instanceof ContactoIndividual)
                ? "Individual: " + contacto.getNombre()
                : "Grupo: " + contacto.getNombre();
            model.addElement(prefixedName);
        }
        contactosList.setModel(model);
    }

    @Override
    public void updateListaContactos() {
        SwingUtilities.invokeLater(this::actualizarListaContactos);
    }
}
