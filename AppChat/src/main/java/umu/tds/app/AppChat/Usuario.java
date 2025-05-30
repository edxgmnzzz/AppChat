package umu.tds.app.AppChat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.ImageIcon;

public class Usuario {
    private String telefono;
    private String nombre;
    private String password;
    private String email;
    private String saludo;
    private ImageIcon foto;
    private String urlFoto;
    private boolean premium;
    private List<Contacto> contactos;
    private int id;
    private LocalDate fechaRegistro;

    
    public Usuario(String telefono, String nombre, String password, String email, String saludo, ImageIcon foto, boolean premium) {
    	this.id = -1;
        this.telefono = telefono;
        this.nombre = nombre;
        this.password = password;
        this.email = email;
        this.saludo = saludo;
        this.foto = foto;
        this.premium = premium;
        this.contactos = new ArrayList<>();
        this.fechaRegistro = LocalDate.now();

    }

    public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTelefono() {
        return telefono;
    }

    public String getName() {
        return nombre;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getSaludo() {
        return saludo != null ? saludo : "";
    }

    public ImageIcon getFoto() {
        return foto != null ? foto : new ImageIcon();
    }

    public boolean isPremium() {
        return premium;
    }

    public List<Contacto> getContactos() {
        return contactos != null ? Collections.unmodifiableList(contactos) : Collections.emptyList();
    }


    public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public LocalDate getFechaRegistro() { return fechaRegistro; }
	public void setFechaRegistro(LocalDate fecha) { this.fechaRegistro = fecha; }

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public ImageIcon getProfilePhotos() {
        return getFoto();
    }

    public void setName(String nombre) {
        this.nombre = nombre;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setSaludo(String saludo) {
        this.saludo = saludo;
    }

    public void setFoto(ImageIcon foto) {
        this.foto = foto;
    }

    public void setPremium(boolean premium) {
        this.premium = premium;
    }
    public String getUrlFoto() {
        return urlFoto;
    }

    public void setUrlFoto(String url) {
        this.urlFoto = url;
    }

    public void setContactos(List<Contacto> contactos) {
        this.contactos = new ArrayList<>(contactos);
    }
}