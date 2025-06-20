package umu.tds.app.ventanas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import umu.tds.app.AppChat.Contacto;
import umu.tds.app.AppChat.ContactoIndividual;
import umu.tds.app.AppChat.Controlador;
import umu.tds.app.AppChat.ObserverContactos;
import umu.tds.app.AppChat.Theme;

public class VentanaContactos extends JDialog implements ObserverContactos {
    private static final long serialVersionUID = 1L;
    private JList<String> contactosList;
    private Controlador controlador;

    public VentanaContactos() {
        controlador = Controlador.getInstancia();
        controlador.addObserverContactos(this);
        configurarVentana();
        crearComponentes();
        
        // Buena práctica: desregistrar el observer cuando la ventana se cierra
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                controlador.removeObserverContactos(VentanaContactos.this);
            }
        });
    }


    private void configurarVentana() {
        setSize(400, 500);
        setLocationRelativeTo(getParent());
        setUndecorated(true);
        setShape(new java.awt.geom.RoundRectangle2D.Double(0, 0, 400, 500, Theme.BORDER_RADIUS, Theme.BORDER_RADIUS));
    }

    private void crearComponentes() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Theme.COLOR_FONDO);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(Theme.PADDING_MEDIUM, Theme.PADDING_MEDIUM, Theme.PADDING_MEDIUM, Theme.PADDING_MEDIUM));

        mainPanel.add(crearBarraTitulo(), BorderLayout.NORTH);
        mainPanel.add(crearPanelContenido(), BorderLayout.CENTER);

        add(mainPanel);
    }

    private JPanel crearBarraTitulo() {
        JPanel barraTitulo = new JPanel(new BorderLayout());
        barraTitulo.setBackground(Theme.COLOR_PRINCIPAL);
        barraTitulo.setPreferredSize(new Dimension(400, Theme.TITLE_BAR_HEIGHT));

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
                if (isSelected) {
                    c.setBackground(Theme.COLOR_HOVER);
                    c.setForeground(Theme.COLOR_TEXTO);
                } else {
                    c.setBackground(Theme.COLOR_SECUNDARIO);
                    c.setForeground(Theme.COLOR_PRINCIPAL);
                }
                return c;
            }
        });
        contactosList.setBackground(Theme.COLOR_SECUNDARIO);
        actualizarListaContactos();

        JScrollPane scrollPane = new JScrollPane(contactosList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(Theme.PADDING_SMALL, Theme.PADDING_SMALL, Theme.PADDING_SMALL, Theme.PADDING_SMALL));
        panelContenido.add(scrollPane, BorderLayout.CENTER);

        JPanel botonesPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        botonesPanel.setBackground(Theme.COLOR_FONDO);

        JButton agregarContacto = createStyledButton("Agregar Contacto", e -> agregarContacto());
        JButton eliminarContacto = createStyledButton("Eliminar Contacto", e -> eliminarContacto());
        JButton crearGrupo = createStyledButton("Crear Grupo", e -> new VentanaNuevoGrupo().setVisible(true));

        botonesPanel.add(agregarContacto);
        botonesPanel.add(eliminarContacto);
        botonesPanel.add(crearGrupo);

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
    }
    
    private void eliminarContacto() {
        String seleccionado = contactosList.getSelectedValue();
        if (seleccionado == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un contacto para eliminar.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Extraer el nombre real sin los prefijos "Individual: " o "Grupo: "
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
                // Aquí iría la lógica para eliminar grupos
                JOptionPane.showMessageDialog(this, "La eliminación de grupos no está implementada.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void actualizarListaContactos() {
        DefaultListModel<String> model = new DefaultListModel<>();
        // --- CAMBIO CLAVE ---
        // Se llama al método del controlador que ya filtra los contactos desconocidos.
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
        // Asegurarse de que la actualización de la UI se ejecute en el Event Dispatch Thread
        SwingUtilities.invokeLater(this::actualizarListaContactos);
    }
}