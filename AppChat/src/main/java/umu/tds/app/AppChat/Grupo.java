package umu.tds.app.AppChat;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.swing.ImageIcon;

public class Grupo extends Contacto {
    public static final String GROUP_ICON_PATH = "/umu/tds/app/recursos/grupo.png";
    private List<ContactoIndividual> integrantes;
    private Usuario admin;
    private ImageIcon foto;
    private String urlFoto;

    public Grupo(String nombre, List<ContactoIndividual> contactos, Usuario admin, ImageIcon foto) {
        super(nombre);
        this.integrantes = new ArrayList<>(contactos);
        this.admin = admin;
        this.foto = foto != null ? foto : new ImageIcon();
    }

    public List<ContactoIndividual> getParticipantes() {
        return new ArrayList<>(integrantes);
    }

    public Usuario getAdmin() {
        return admin;
    }

    @Override
    public ImageIcon getFoto() {
        ImageIcon imagen = new ImageIcon(Grupo.class.getResource(GROUP_ICON_PATH));
        imagen.setDescription(GROUP_ICON_PATH);
        return imagen;
    }

    public void addIntegrante(ContactoIndividual contacto) {
        if (!integrantes.contains(contacto)) {
            integrantes.add(contacto);
        }
    }

    public void cambiarAdmin(Usuario usuario) {
        admin = usuario;
    }

    public void setIntegrantes(List<ContactoIndividual> contactos) {
        this.integrantes = new ArrayList<>(contactos);
    }

    public List<Mensaje> getMensajesRecibidos(Optional<Usuario> usuario) {
        List<Mensaje> recibidos = new ArrayList<>();
        for (Mensaje msg : mensajes) {
            if (msg.getReceptor() == this && (!usuario.isPresent() || msg.getEmisor() == usuario.get())) {
                recibidos.add(msg);
            }
        }
        return recibidos;
    }

    public List<Mensaje> removeMensajesRecibidos() {
        List<Mensaje> recibidos = getMensajesRecibidos(Optional.empty());
        List<Mensaje> copia = new ArrayList<>(recibidos);
        mensajes.removeAll(recibidos);
        return copia;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((nombre == null) ? 0 : nombre.hashCode());
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
        Grupo other = (Grupo) obj;
        if (nombre == null) {
            if (other.nombre != null)
                return false;
        } else if (!nombre.equals(other.nombre))
            return false;
        return true;
    }

	public void setCodigo(int id) {
		this.codigo = id;
		
	}

	public String getUrlFoto() {
		return urlFoto;
	}

	public void setUrlFoto(String urlFoto) {
		this.urlFoto = urlFoto;
	}

	public List<ContactoIndividual> getIntegrantes() {
		return integrantes;
	}

	public void setAdmin(Usuario admin) {
		this.admin = admin;
	}

	public void setFoto(ImageIcon foto) {
		this.foto = foto;
	}
	
}