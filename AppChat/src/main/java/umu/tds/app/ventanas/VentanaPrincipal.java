package umu.tds.app.ventanas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import umu.tds.app.AppChat.Contacto;
import umu.tds.app.AppChat.Controlador;
import umu.tds.app.AppChat.Observer;

public class VentanaPrincipal extends JFrame implements Observer {
    private static final long serialVersionUID = 1L;
    private VentanaChatsRecientes chatsRecientes;
    private VentanaChatActual chatActual;
    private VentanaSuperior ventanaSuperior;
    private Controlador controlador;

    public VentanaPrincipal() {
        controlador = Controlador.getInstancia();
        controlador.addObserver(this); // Registrarse como observer
        
        setTitle("AppChat");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        ventanaSuperior = new VentanaSuperior();
        add(ventanaSuperior, BorderLayout.NORTH);

        chatsRecientes = new VentanaChatsRecientes();
        chatActual = new VentanaChatActual();

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, chatsRecientes, chatActual);
        splitPane.setDividerLocation(250);
        add(splitPane, BorderLayout.CENTER);

        agregarEventos();
    }

    private void agregarEventos() {
        chatsRecientes.getChatList().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String seleccionado = chatsRecientes.getChatList().getSelectedValue();
                if (seleccionado != null && seleccionado.startsWith("Chat con ")) {
                    String nombreContacto = seleccionado.substring(9);
                    Contacto contacto = controlador.obtenerContactoPorNombre(nombreContacto);
                    if (contacto != null) {
                        controlador.setContactoActual(contacto); // Notificar al Controlador el cambio de contacto
                    }
                }
            }
        });
    }

    @Override
    public void updateChatsRecientes(String[] chatsRecientes) {
        // Este método se llamará cuando el Controlador notifique a los observadores
        // No necesitamos hacer nada adicional aquí porque VentanaChatsRecientes
        // ya está registrado como Observer y se actualizará automáticamente
    }

    @Override
    public void updateContactoActual(Contacto contacto) {
        // Actualizar el chat actual en VentanaChatActual cuando cambie el contacto
        chatActual.updateChat();
    }

	@Override
	public void updateListaContactos() {
		// TODO Auto-generated method stub
		
	}
}