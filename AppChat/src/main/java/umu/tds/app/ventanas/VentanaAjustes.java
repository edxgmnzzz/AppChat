package umu.tds.app.ventanas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.*;

import umu.tds.app.AppChat.Controlador;
import umu.tds.app.AppChat.Theme;

public class VentanaAjustes extends JFrame {
    private static final long serialVersionUID = 1L;
    private JTextField campoNombre;
    private JPasswordField campoPassword;
    private JPasswordField campoConfirmarPassword;
    private JTextField campoSaludo;
    private JTextField campoRutaFoto;
    private Controlador controlador;
    private VentanaSuperior ventanaSuperior;
    private static final int WINDOW_WIDTH = 400;
    private static final int WINDOW_HEIGHT = 450;
    private Point initialClick;

    // Colores de la aplicación (reusando los de VentanaRegistro)
    private static final Color COLOR_FONDO = new Color(41, 128, 185);
    private static final Color COLOR_PRINCIPAL = new Color(52, 152, 219);
    private static final Color COLOR_SECUNDARIO = new Color(236, 240, 241);
    private static final Color COLOR_ACENTO = new Color(231, 76, 60);
    private static final Color COLOR_TEXTO_CLARO = Color.BLACK; // Darker color for better readability

    public VentanaAjustes(VentanaSuperior ventanaSuperior) {
        this.ventanaSuperior = ventanaSuperior;
        controlador = Controlador.getInstancia();
        configurarVentana();
        crearComponentes();
        setVisible(true);
    }

    private void configurarVentana() {
        setTitle("Ajustes de Perfil");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setUndecorated(true); // Remove default title bar
    }

    private void crearComponentes() {
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(COLOR_FONDO);

        // Barra de título
        JPanel barraTitulo = crearBarraTitulo();
        panelPrincipal.add(barraTitulo, BorderLayout.NORTH);

        // Panel de contenido
        JPanel panelContenido = new JPanel();
        panelContenido.setLayout(new BoxLayout(panelContenido, BoxLayout.Y_AXIS));
        panelContenido.setOpaque(false);
        panelContenido.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel labelBienvenida = crearLabel("Ajustes de Perfil", 18, COLOR_SECUNDARIO);
        labelBienvenida.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelContenido.add(labelBienvenida);
        panelContenido.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel panelCampos = new JPanel(new GridBagLayout());
        panelCampos.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Campos del formulario
        campoNombre = crearCampoTextoPersonalizado(COLOR_TEXTO_CLARO, COLOR_SECUNDARIO, COLOR_PRINCIPAL);
        campoNombre.setText(controlador.getNombreUserActual());
        campoPassword = crearCampoPasswordPersonalizado(COLOR_TEXTO_CLARO, COLOR_SECUNDARIO, COLOR_PRINCIPAL);
        campoConfirmarPassword = crearCampoPasswordPersonalizado(COLOR_TEXTO_CLARO, COLOR_SECUNDARIO, COLOR_PRINCIPAL);
        campoSaludo = crearCampoTextoPersonalizado(COLOR_TEXTO_CLARO, COLOR_SECUNDARIO, COLOR_PRINCIPAL);
        campoSaludo.setText(controlador.getUsuarioActual().getSaludo().orElse(""));
        campoRutaFoto = crearCampoTextoPersonalizado(COLOR_TEXTO_CLARO, COLOR_SECUNDARIO, COLOR_PRINCIPAL);

        // Panel para la ruta de foto
        JPanel panelFoto = new JPanel(new BorderLayout());
        panelFoto.setOpaque(false);
        configurarDragAndDrop(campoRutaFoto);
        JButton botonSeleccionarFoto = new JButton("...");
        botonSeleccionarFoto.addActionListener(e -> seleccionarFoto());
        panelFoto.add(campoRutaFoto, BorderLayout.CENTER);
        panelFoto.add(botonSeleccionarFoto, BorderLayout.EAST);

        // Agregar campos
        gbc.gridx = 0;
        gbc.gridy = 0;
        panelCampos.add(crearLabel("Nombre:", 14, COLOR_SECUNDARIO), gbc);
        gbc.gridx = 1;
        panelCampos.add(campoNombre, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panelCampos.add(crearLabel("Nueva Contraseña:", 14, COLOR_SECUNDARIO), gbc);
        gbc.gridx = 1;
        panelCampos.add(campoPassword, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panelCampos.add(crearLabel("Confirmar Contraseña:", 14, COLOR_SECUNDARIO), gbc);
        gbc.gridx = 1;
        panelCampos.add(campoConfirmarPassword, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panelCampos.add(crearLabel("Saludo:", 14, COLOR_SECUNDARIO), gbc);
        gbc.gridx = 1;
        panelCampos.add(campoSaludo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        panelCampos.add(crearLabel("Ruta Foto:", 14, COLOR_SECUNDARIO), gbc);
        gbc.gridx = 1;
        panelCampos.add(panelFoto, gbc);

        panelContenido.add(panelCampos);
        panelContenido.add(Box.createRigidArea(new Dimension(0, 20)));

        // Botón de guardar
        JButton botonGuardar = crearBotonPersonalizado("Guardar Cambios", e -> guardarCambios(),
                Color.RED, Color.WHITE, new Color(192, 57, 43));
        botonGuardar.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelContenido.add(botonGuardar);

        panelPrincipal.add(panelContenido, BorderLayout.CENTER);
        add(panelPrincipal);
    }

    private void seleccionarFoto() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos de imagen", "jpg", "jpeg", "png", "gif"));
        int resultado = fileChooser.showOpenDialog(this);
        if (resultado == JFileChooser.APPROVE_OPTION) {
            File archivoSeleccionado = fileChooser.getSelectedFile();
            campoRutaFoto.setText(archivoSeleccionado.getAbsolutePath());
        }
    }

    private void configurarDragAndDrop(JTextField campo) {
        new DropTarget(campo, new DropTargetAdapter() {
            @Override
            public void drop(DropTargetDropEvent dtde) {
                try {
                    dtde.acceptDrop(DnDConstants.ACTION_COPY);
                    @SuppressWarnings("unchecked")
                    java.util.List<File> files = (java.util.List<File>) dtde.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                    if (!files.isEmpty()) {
                        File file = files.get(0);
                        String extension = getFileExtension(file);
                        if (extension != null && (extension.equals("jpg") || extension.equals("jpeg") || 
                                                  extension.equals("png") || extension.equals("gif"))) {
                            campo.setText(file.getAbsolutePath());
                        } else {
                            JOptionPane.showMessageDialog(VentanaAjustes.this, 
                                                          "Solo se permiten archivos de imagen (jpg, jpeg, png, gif)", 
                                                          "Formato no válido", JOptionPane.WARNING_MESSAGE);
                        }
                    }
                    dtde.dropComplete(true);
                } catch (Exception e) {
                    dtde.dropComplete(false);
                    e.printStackTrace();
                }
            }
        });
    }

    private String getFileExtension(File file) {
        String name = file.getName();
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return null;
        }
        return name.substring(lastIndexOf + 1).toLowerCase();
    }

    private void guardarCambios() {
        String nuevoNombre = campoNombre.getText().trim();
        String nuevaPassword = new String(campoPassword.getPassword());
        String confirmarPassword = new String(campoConfirmarPassword.getPassword());
        String nuevoSaludo = campoSaludo.getText().trim();
        String rutaFoto = campoRutaFoto.getText().trim();

        // Validar contraseña
        if (!nuevaPassword.isEmpty() && !nuevaPassword.equals(confirmarPassword)) {
            JOptionPane.showMessageDialog(this, "Las contraseñas no coinciden", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validar nombre no vacío
        if (nuevoNombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre no puede estar vacío", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Usar contraseña actual si no se proporciona una nueva
        String passwordToUse = nuevaPassword.isEmpty() ? null : nuevaPassword;

        if (controlador.actualizarUsuario(nuevoNombre, passwordToUse, nuevoSaludo, rutaFoto)) {
            JOptionPane.showMessageDialog(this, "Cambios guardados exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            if (ventanaSuperior != null) {
                ventanaSuperior.updateUI();
            }
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Error al guardar los cambios", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel crearBarraTitulo() {
        JPanel barraTitulo = new JPanel(new BorderLayout());
        barraTitulo.setBackground(COLOR_PRINCIPAL);
        barraTitulo.setPreferredSize(new Dimension(WINDOW_WIDTH, 30));
        barraTitulo.add(new JLabel("  Ajustes", JLabel.LEFT), BorderLayout.WEST);

        JPanel botonesControl = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        botonesControl.setOpaque(false);
        botonesControl.add(crearBotonControl("−", e -> setState(Frame.ICONIFIED)));
        botonesControl.add(crearBotonControl("□", e -> setExtendedState(getExtendedState() == JFrame.MAXIMIZED_BOTH ? JFrame.NORMAL : JFrame.MAXIMIZED_BOTH)));
        botonesControl.add(crearBotonControl("×", e -> dispose()));
        barraTitulo.add(botonesControl, BorderLayout.EAST);

        barraTitulo.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                initialClick = e.getPoint();
            }
        });

        barraTitulo.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int thisX = getLocation().x;
                int thisY = getLocation().y;

                int xMoved = (thisX + e.getX()) - (thisX + initialClick.x);
                int yMoved = (thisY + e.getY()) - (thisY + initialClick.y);

                setLocation(thisX + xMoved, thisY + yMoved);
            }
        });

        return barraTitulo;
    }

    private JButton crearBotonControl(String texto, ActionListener accion) {
        JButton boton = new JButton(texto);
        boton.setPreferredSize(new Dimension(45, 30));
        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
        boton.setContentAreaFilled(false);
        boton.setForeground(COLOR_SECUNDARIO);
        boton.setFont(new Font("Arial", Font.BOLD, 14));
        boton.addActionListener(accion);
        boton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                boton.setBackground(COLOR_ACENTO);
                boton.setContentAreaFilled(true);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                boton.setContentAreaFilled(false);
            }
        });
        return boton;
    }

    private JTextField crearCampoTextoPersonalizado(Color colorTexto, Color fondo, Color colorBorde) {
        JTextField campo = new JTextField(15);
        campo.setFont(new Font("Arial", Font.PLAIN, 14));
        campo.setForeground(colorTexto);
        campo.setBackground(fondo);
        campo.setBorder(BorderFactory.createLineBorder(colorBorde, 2));
        return campo;
    }

    private JPasswordField crearCampoPasswordPersonalizado(Color colorTexto, Color fondo, Color colorBorde) {
        JPasswordField campo = new JPasswordField(15);
        campo.setFont(new Font("Arial", Font.PLAIN, 14));
        campo.setForeground(colorTexto);
        campo.setBackground(fondo);
        campo.setBorder(BorderFactory.createLineBorder(colorBorde, 2));
        return campo;
    }

    private JButton crearBotonPersonalizado(String texto, ActionListener accion, Color colorFondo, Color colorTexto, Color colorHover) {
        JButton boton = new JButton(texto) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getModel().isArmed() ? colorHover.darker() : colorHover);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                super.paintComponent(g2d);
                g2d.dispose();
            }
        };
        boton.setForeground(colorTexto);
        boton.setFont(new Font("Arial", Font.BOLD, 14));
        boton.setContentAreaFilled(false);
        boton.setBorderPainted(false);
        boton.setFocusPainted(false);
        boton.addActionListener(accion);
        return boton;
    }

    private JLabel crearLabel(String texto, int tamanoFuente, Color colorTexto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Arial", Font.BOLD, tamanoFuente));
        label.setForeground(colorTexto);
        return label;
    }
}