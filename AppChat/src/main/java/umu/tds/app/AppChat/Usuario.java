package umu.tds.app.AppChat;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import javax.swing.ImageIcon;

/**
 * Representa un usuario en el sistema de la aplicación de chat.
 * Contiene información personal, configuraciones de estado, fotos de perfil, 
 * contactos y detalles de suscripciones premium.
 */
public class Usuario {
    // Constantes relacionadas con la suscripción y descuentos
    private static final double PRECIO_PREMIUM = 19.90;
    private static final LocalDate FECHA_JOVEN = LocalDate.of(2003, 1, 1);
    private static final LocalDate FECHA_ADULTO = LocalDate.of(1955, 1, 1);
    private static final String SALUDO_INICIAL = "Hello World!";

    // Propiedades del usuario
    private ImageIcon profilePhoto;
    private String name; // Nombre completo
    private LocalDate fechaNacimiento; // Fecha de nacimiento
    private int numTelefono; // Número de teléfono
    private String email; // Dirección de correo electrónico
    
    
    private String password; // Contraseña
    private boolean premium; // Indica si el usuario tiene suscripción premium
    private Optional<String> saludo; // Mensaje personalizado de saludo
    
    private List<Grupo> gruposAdmin; // Grupos en los que el usuario es administrador
    private List<Contacto> contactos; // Lista de contactos del usuario    

    public Usuario(ImageIcon icono, String name, LocalDate fechaNacimiento, int numTelefono,
                   String password, String email, boolean premium, String saludo, List<Grupo> gruposAdmin,
                   List<Contacto> contactos) {
        this.profilePhoto = icono;
        this.name = name;
        this.fechaNacimiento = fechaNacimiento;
        this.numTelefono = numTelefono;
        this.password = password;
        this.premium = premium;
        this.saludo = Optional.ofNullable(saludo);
        this.gruposAdmin = gruposAdmin;
        this.contactos = contactos;
        this.email = email;
    }

    // Métodos de acceso (Getters y Setters)
    public ImageIcon getProfilePhotos() {
        return profilePhoto;
    }

    public String getName() {
        return name;
    }

    public Optional<String> getSaludo() {
        return saludo;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public int getNumTelefono() {
        return numTelefono;
    }

    public String getPassword() {
        return password;
    }

    public boolean isPremium() {
        return premium;
    }

    public List<Grupo> getGruposAdmin() {
        return gruposAdmin;
    }

    public List<Contacto> getContactos() {
        return contactos;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /*public double getPrecio() {
        return descuento.map(d -> d.getDescuento(PRECIO_PREMIUM)).orElse(PRECIO_PREMIUM);
    }*/
}
