package umu.tds.app.AppChat;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.swing.ImageIcon;

public class ContactoIndividual extends Contacto {
    private int movil;
    private Usuario usuario;

    public ContactoIndividual(String nombre, int codigo, int movil, Usuario usuario) {
        super(nombre, codigo);
        this.movil = movil;
        this.usuario = usuario;
    }

    public int getMovil() {
        return movil;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    @Override
    public ImageIcon getFoto() {
        return usuario.getProfilePhotos();
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    @Override
    public List<Mensaje> getMensajesRecibidos(Optional<Usuario> usuario) {
        List<Mensaje> recibidos = new ArrayList<>();
        for (Mensaje msg : mensajes) {
            if (msg.getReceptor() == this && (!usuario.isPresent() || msg.getEmisor() == usuario.get())) {
                recibidos.add(msg);
            }
        }
        return recibidos;
    }

    public ContactoIndividual getContacto(Usuario usuario) {
        return this.usuario.getContactos().stream()
                .filter(c -> c instanceof ContactoIndividual)
                .map(c -> (ContactoIndividual) c)
                .filter(c -> c.getUsuario().equals(usuario))
                .findAny()
                .orElse(null);
    }

    public List<Mensaje> removeMensajesRecibidos(Usuario usuarioActual) {
        List<Mensaje> recibidos = getMensajesRecibidos(Optional.of(usuarioActual));
        List<Mensaje> copia = new ArrayList<>(recibidos);
        mensajes.removeAll(recibidos);
        return copia;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + movil;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ContactoIndividual other = (ContactoIndividual) obj;
        return movil == other.movil;
    }

    public boolean isUsuario(Usuario otherUsuario) {
        return usuario.equals(otherUsuario);
    }
}