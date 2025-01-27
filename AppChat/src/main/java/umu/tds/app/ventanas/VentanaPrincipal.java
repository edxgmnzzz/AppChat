package umu.tds.app.ventanas;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import umu.tds.app.AppChat.Controlador;
import umu.tds.app.AppChat.ContactoIndividual;
import umu.tds.app.AppChat.*;


//Esto lo usa el del github para que quede más bonito lo modificaremos
/**
 * Clase que representa al chat con las burbujas. Se creó con el objetivo de que
 * desaparezca la scrollbar horizontal del chat.
 */
class ChatBurbujas extends JPanel implements Scrollable {
	private static final long serialVersionUID = 1L;

	@Override
	public boolean getScrollableTracksViewportWidth() {
		return true;
	}

	@Override
	public boolean getScrollableTracksViewportHeight() {
		return false;
	}

	@Override
	public Dimension getPreferredScrollableViewportSize() {
		return null;
	}

	@Override
	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
		return 0;
	}

	@Override
	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
		return 0;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}
}


public class VentanaPrincipal extends JFrame {
    private static final long serialVersionUID = 1L;
    private JPanel panelContactos;
    private JPanel panelChat;
    private JTextField campoMensaje;
    private JTextArea areaMensajes;
    private ContactoIndividual contactoActual;  
    private Controlador controlador;
    private int xMouse, yMouse;

    private final Color colorFondo = new Color(41, 128, 185);
    private final Color colorPrincipal = new Color(52, 152, 219);
    private final Color colorSecundario = new Color(236, 240, 241);
    private final Color colorAcento = new Color(231, 76, 60);

    
    
    public VentanaPrincipal() {
        controlador = Controlador.getInstancia();
        configurarVentana();
        inicializarComponentes();
    }

    private void configurarVentana() {
        setSize(800, 600);
        setUndecorated(true);
        setLocationRelativeTo(null);
        setShape(new java.awt.geom.RoundRectangle2D.Double(0, 0, 800, 600, 15, 15));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void inicializarComponentes() {
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(colorFondo);

        JPanel barraTitulo = crearBarraTitulo();
        panelPrincipal.add(barraTitulo, BorderLayout.NORTH);

        JPanel panelContenido = new JPanel(new BorderLayout());
        panelContenido.setOpaque(false);

        // Panel de contactos (izquierda)
        panelContactos = new JPanel(new GridLayout(0, 1));
        panelContactos.setBackground(colorPrincipal);
        panelContactos.setBorder(new EmptyBorder(10, 10, 10, 10));
        JScrollPane scrollContactos = new JScrollPane(panelContactos);
        scrollContactos.setPreferredSize(new Dimension(200, 0));
        scrollContactos.setBorder(BorderFactory.createEmptyBorder());

        for (ContactoIndividual contacto : controlador.obtenerContactos()) {
            JButton btnContacto = new JButton(contacto.getNombre());
            btnContacto.setHorizontalAlignment(SwingConstants.LEFT);
            btnContacto.setForeground(colorSecundario);
            btnContacto.setBackground(colorFondo);
            btnContacto.setBorder(new LineBorder(colorSecundario, 1, true));
            btnContacto.setFocusPainted(false);
            btnContacto.addActionListener(e -> cambiarChat(contacto));
            btnContacto.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent evt) {
                    btnContacto.setBackground(colorSecundario);
                    btnContacto.setForeground(colorFondo);
                }

                public void mouseExited(MouseEvent evt) {
                    btnContacto.setBackground(colorFondo);
                    btnContacto.setForeground(colorSecundario);
                }
            });
            panelContactos.add(btnContacto);
        }

        // Panel de chat (derecha)
        panelChat = new JPanel(new BorderLayout());
        panelChat.setBackground(colorFondo);
        panelChat.setBorder(new EmptyBorder(10, 10, 10, 10));

        areaMensajes = new JTextArea();
        areaMensajes.setEditable(false);
        areaMensajes.setFont(new Font("Arial", Font.PLAIN, 14));
        areaMensajes.setForeground(colorSecundario);
        areaMensajes.setBackground(colorPrincipal);
        JScrollPane scrollMensajes = new JScrollPane(areaMensajes);
        scrollMensajes.setBorder(BorderFactory.createLineBorder(colorSecundario));

        campoMensaje = new JTextField();
        campoMensaje.setFont(new Font("Arial", Font.PLAIN, 14));
        campoMensaje.setForeground(colorSecundario);
        campoMensaje.setBackground(colorPrincipal);
        campoMensaje.setBorder(new LineBorder(colorSecundario, 2, true));

        JButton btnEnviar = crearBotonPersonalizado("Enviar", e -> enviarMensaje(), colorPrincipal, colorSecundario, colorAcento);

        JPanel panelEnvio = new JPanel(new BorderLayout());
        panelEnvio.setOpaque(false);
        panelEnvio.add(campoMensaje, BorderLayout.CENTER);
        panelEnvio.add(btnEnviar, BorderLayout.EAST);

        panelChat.add(scrollMensajes, BorderLayout.CENTER);
        panelChat.add(panelEnvio, BorderLayout.SOUTH);

        panelContenido.add(scrollContactos, BorderLayout.WEST);
        panelContenido.add(panelChat, BorderLayout.CENTER);

        panelPrincipal.add(panelContenido, BorderLayout.CENTER);

        setContentPane(panelPrincipal);
    }

    private JPanel crearBarraTitulo() {
        JPanel barraTitulo = new JPanel(new BorderLayout());
        barraTitulo.setBackground(colorPrincipal);
        barraTitulo.setPreferredSize(new Dimension(800, 30));
        barraTitulo.add(new JLabel("  ParabarApp", JLabel.LEFT), BorderLayout.WEST);

        JPanel botonesControl = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        botonesControl.setOpaque(false);
        botonesControl.add(crearBotonControl("−", e -> setState(Frame.ICONIFIED)));
        botonesControl.add(crearBotonControl("×", e -> System.exit(0)));
        barraTitulo.add(botonesControl, BorderLayout.EAST);

        barraTitulo.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                xMouse = e.getX();
                yMouse = e.getY();
            }
        });
        barraTitulo.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                setLocation(e.getXOnScreen() - xMouse, e.getYOnScreen() - yMouse);
            }
        });

        return barraTitulo;
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

    private JButton crearBotonControl(String texto, ActionListener accion) {
        JButton boton = new JButton(texto);
        boton.setPreferredSize(new Dimension(45, 30));
        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
        boton.setContentAreaFilled(false);
        boton.setForeground(colorSecundario);
        boton.setFont(new Font("Arial", Font.BOLD, 14));
        boton.addActionListener(accion);
        boton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                boton.setBackground(colorAcento);
                boton.setContentAreaFilled(true);
            }

            public void mouseExited(MouseEvent e) {
                boton.setContentAreaFilled(false);
            }
        });
        return boton;
    }

    private void cambiarChat(ContactoIndividual contacto) {
        contactoActual = contacto;
        areaMensajes.setText("");
        areaMensajes.append("Chat con " + contacto.getNombre() + "\n\n");

        for (String mensaje : controlador.obtenerMensajes(contacto)) {
            areaMensajes.append(mensaje + "\n");
        }
    }

    private void enviarMensaje(ChatBurbujas panel, JTextField textField, Contacto contacto) {
		// No permite enviar un mensaje si no hay seleccionado ningún contacto
		if (contacto == null)
			return;

		controlador.enviarMensaje(contacto, textField.getText());

		BubbleText burbuja = new BubbleText(panel, textField.getText(), SENT_MESSAGE_COLOR, "You", BubbleText.SENT,
				MESSAGE_SIZE);
		panel.add(burbuja);
		textField.setText(null);
		listaContactos.updateUI();
	}
