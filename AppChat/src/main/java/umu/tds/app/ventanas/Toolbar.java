package umu.tds.app.ventanas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
;

public class Toolbar extends JPanel {
    private static final long serialVersionUID = 1L;
	private JFrame ventanaPadre;
    private Point initialClick;

    public Toolbar(JFrame ventana, String titulo) {
        this.ventanaPadre = ventana;
        configurarBarraTitulo(titulo);
    }

    private void configurarBarraTitulo(String titulo) {
        setLayout(new BorderLayout());
        setBackground(new Color(52, 152, 219));
        setPreferredSize(new Dimension(800, 30));

        // Título
        JLabel labelTitulo = new JLabel(titulo, JLabel.LEFT);
        labelTitulo.setForeground(Color.WHITE);
        add(labelTitulo, BorderLayout.WEST);

        // Botones de control
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBotones.setOpaque(false);

        // Botón minimizar
        JButton btnMinimizar = crearBotonControl("−", e -> ventanaPadre.setState(Frame.ICONIFIED));
        
        // Botón maximizar/restaurar
        JButton btnMaximizar = crearBotonControl("□", e -> {
            if (ventanaPadre.getExtendedState() == JFrame.MAXIMIZED_BOTH) {
                ventanaPadre.setExtendedState(JFrame.NORMAL);
            } else {
                ventanaPadre.setExtendedState(JFrame.MAXIMIZED_BOTH);
            }
        });

        // Botón cerrar
        JButton btnCerrar = crearBotonControl("×", e -> ventanaPadre.dispose());

        panelBotones.add(btnMinimizar);
        panelBotones.add(btnMaximizar);
        panelBotones.add(btnCerrar);

        add(panelBotones, BorderLayout.EAST);

        // Permitir arrastrar la ventana
        configurarMovimientoVentana();
    }

    private JButton crearBotonControl(String texto, ActionListener accion) {
        JButton boton = new JButton(texto);
        boton.setPreferredSize(new Dimension(45, 30));
        boton.setForeground(Color.WHITE);
        boton.setBackground(new Color(52, 152, 219));
        boton.setBorderPainted(false);
        boton.setFocusPainted(false);
        boton.addActionListener(accion);
        
        boton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                boton.setBackground(new Color(231, 76, 60));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                boton.setBackground(new Color(52, 152, 219));
            }
        });
        
        return boton;
    }

    private void configurarMovimientoVentana() {
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                initialClick = e.getPoint();
                ventanaPadre.getComponentAt(initialClick);
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int thisX = ventanaPadre.getLocation().x;
                int thisY = ventanaPadre.getLocation().y;

                int xMoved = e.getX() - initialClick.x;
                int yMoved = e.getY() - initialClick.y;

                ventanaPadre.setLocation(thisX + xMoved, thisY + yMoved);
            }
        });
    }
}