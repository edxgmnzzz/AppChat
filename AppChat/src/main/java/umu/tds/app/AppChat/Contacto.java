package umu.tds.app.AppChat;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import javax.swing.ImageIcon;

public abstract class Contacto {
    protected String nombre;
    protected int codigo;
    protected List<Mensaje> mensajes;

    public Contacto(String nombre) {
        this.nombre = nombre;
        this.codigo = generateUniqueCode();
        this.mensajes = new ArrayList<>();
    }

    public Contacto(String nombre, List<Mensaje> mensajes) {
        this.nombre = nombre;
        this.codigo = generateUniqueCode();
        this.mensajes = new ArrayList<>(mensajes);
    }

    protected Contacto(String nombre, int codigo) {
        this.nombre = nombre;
        this.codigo = codigo;
        this.mensajes = new ArrayList<>();
    }

    private static int generateUniqueCode() {
        return (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
    }

    public String getNombre() {
        return nombre;
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
    	this.codigo = codigo;
    }
    public void sendMensaje(Mensaje mensaje) {
        mensajes.add(mensaje);
    }

    public List<Mensaje> getMensajesEnviados() {
        List<Mensaje> enviados = new ArrayList<>();
        for (Mensaje msg : mensajes) {
            if (msg.getEmisor() != null && msg.getReceptor() == this) {
                enviados.add(msg);
            }
        }
        return enviados;
    }

    public List<Mensaje> getMensajes() {
        return new ArrayList<>(mensajes);
    }

    public abstract List<Mensaje> getMensajesRecibidos(Optional<Usuario> usuario);

    public ImageIcon getFoto() {
        return new ImageIcon();
    }
    
}