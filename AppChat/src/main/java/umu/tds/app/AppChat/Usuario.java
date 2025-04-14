package umu.tds.app.AppChat;

import javax.swing.ImageIcon;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Usuario {
    private ImageIcon profilePhoto;
    private String name;
    private LocalDate fechaNacimiento;
    private int numTelefono;
    private String password;
    private String email;
    private boolean premium;
    private Optional<String> saludo;
    private List<Grupo> gruposAdmin;
    private List<Contacto> contactos;

    public Usuario(ImageIcon icono, String name, LocalDate fechaNacimiento, int numTelefono,
                   String password, String email, boolean premium, String saludo, 
                   List<Grupo> gruposAdmin, List<Contacto> contactos) {
        this.profilePhoto = icono;
        this.name = name;
        this.fechaNacimiento = fechaNacimiento;
        this.numTelefono = numTelefono;
        this.password = password;
        this.email = email;
        this.premium = premium;
        this.saludo = Optional.ofNullable(saludo);
        this.gruposAdmin = gruposAdmin != null ? gruposAdmin : new ArrayList<>();
        this.contactos = contactos != null ? contactos : new ArrayList<>();
    }

    // Getters
    public ImageIcon getProfilePhotos() { return profilePhoto; }
    public String getName() { return name; }
    public String getPassword() { return password; }
    public String getEmail() { return email; }
    public int getNumTelefono() { return numTelefono; }
    public List<Contacto> getContactos() { return contactos; }
    public boolean isPremium() { return premium; }
    public Optional<String> getSaludo() { return saludo; }

    // Setters for editable fields
    public void setProfilePhoto(ImageIcon profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setSaludo(String saludo) {
        this.saludo = Optional.ofNullable(saludo);
    }
}