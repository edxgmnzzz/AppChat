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
    private int codigo; // Identificador único del usuario
    private List<ImageIcon> profilePhotos; // Lista de fotos de perfil
    private String name; // Nombre completo
    private LocalDate fechaNacimiento; // Fecha de nacimiento
    private int numTelefono; // Número de teléfono
    private String nick; // Alias o nombre de usuario
    private String password; // Contraseña
    private boolean premium; // Indica si el usuario tiene suscripción premium
    private String saludo; // Mensaje personalizado de saludo
    private Optional<Status> estado; // Estado actual del usuario
    private List<Grupo> gruposAdmin; // Grupos en los que el usuario es administrador
    private List<Contacto> contactos; // Lista de contactos del usuario
    private Optional<Descuento> descuento; // Descuento aplicable al usuario
    private String email; // Dirección de correo electrónico

    // Constructores
    /**
     * Constructor simplificado para crear un usuario nuevo.
     * 
     * @param icono           Foto de perfil inicial
     * @param name            Nombre completo del usuario
     * @param fechaNacimiento Fecha de nacimiento del usuario
     * @param numTelefono     Número de teléfono
     * @param nick            Alias del usuario
     * @param password        Contraseña del usuario
     * @param email           Correo electrónico
     */
    public Usuario(ImageIcon icono, String name, LocalDate fechaNacimiento, int numTelefono, String nick,
                   String password, String email) {
        this(new LinkedList<>(Arrays.asList(icono)), name, fechaNacimiento, numTelefono, nick, password, false, null,
             SALUDO_INICIAL, new LinkedList<>(), new LinkedList<>(), null, email);
    }

    /**
     * Constructor detallado, útil para cargar un usuario desde la persistencia.
     * 
     * @param iconList        Lista de fotos de perfil
     * @param name            Nombre completo del usuario
     * @param fechaNacimiento Fecha de nacimiento del usuario
     * @param numTelefono     Número de teléfono
     * @param nick            Alias del usuario
     * @param password        Contraseña del usuario
     * @param premium         Indica si el usuario es premium
     * @param descuento       Descuento aplicable al usuario
     * @param saludo          Mensaje personalizado de saludo
     * @param email           Correo electrónico
     */
    public Usuario(List<ImageIcon> iconList, String name, LocalDate fechaNacimiento, int numTelefono, String nick,
                   String password, boolean premium, Descuento descuento, String saludo, String email) {
        this(iconList, name, fechaNacimiento, numTelefono, nick, password, premium, null, saludo, new LinkedList<>(),
             new LinkedList<>(), descuento, email);
    }

    /**
     * Constructor completo para inicializar todas las propiedades del usuario.
     * 
     * @param iconList        Lista de fotos de perfil
     * @param name            Nombre completo
     * @param fechaNacimiento Fecha de nacimiento
     * @param numTelefono     Número de teléfono
     * @param nick            Alias del usuario
     * @param password        Contraseña del usuario
     * @param premium         Indica si el usuario tiene suscripción premium
     * @param estado          Estado actual del usuario
     * @param saludo          Mensaje personalizado de saludo
     * @param gruposAdmin     Grupos donde el usuario es administrador
     * @param contactos       Lista de contactos del usuario
     * @param descuento       Descuento aplicable al usuario
     * @param email           Correo electrónico
     */
    public Usuario(List<ImageIcon> iconList, String name, LocalDate fechaNacimiento, int numTelefono, String nick,
                   String password, boolean premium, Status estado, String saludo, List<Grupo> gruposAdmin,
                   List<Contacto> contactos, Descuento descuento, String email) {
        this.codigo = 0;
        this.profilePhotos = iconList;
        this.name = name;
        this.fechaNacimiento = fechaNacimiento;
        this.numTelefono = numTelefono;
        this.nick = nick;
        this.password = password;
        this.premium = premium;
        this.estado = Optional.ofNullable(estado);
        this.saludo = saludo;
        this.gruposAdmin = gruposAdmin;
        this.contactos = contactos;
        this.email = email;

        // Asignar descuento según la edad si no se especifica
        if (descuento == null) {
            if (fechaNacimiento.isAfter(FECHA_JOVEN)) {
                descuento = new DescuentoJunior();
            } else if (fechaNacimiento.isBefore(FECHA_ADULTO)) {
                descuento = new DescuentoSenior();
            }
        }
        this.descuento = Optional.ofNullable(descuento);
    }

    // Métodos de acceso (Getters y Setters)
    public List<ImageIcon> getProfilePhotos() {
        return profilePhotos;
    }

    public ImageIcon getProfilePhoto() {
        return profilePhotos.get(profilePhotos.size() - 1);
    }

    public String getName() {
        return name;
    }

    public String getSaludo() {
        return saludo;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public int getNumTelefono() {
        return numTelefono;
    }

    public String getNick() {
        return nick;
    }

    public String getPassword() {
        return password;
    }

    public boolean isPremium() {
        return premium;
    }

    public Optional<Status> getEstado() {
        return estado;
    }

    public List<Grupo> getGruposAdmin() {
        return gruposAdmin;
    }

    public List<Contacto> getContactos() {
        return contactos;
    }

    public int getCodigo() {
        return codigo;
    }

    public Optional<Descuento> getDescuento() {
        return descuento;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Calcula el precio con descuento para un usuario premium.
     * 
     * @return Precio final después de aplicar el descuento.
     */
    public double getPrecio() {
        return descuento.map(d -> d.getDescuento(PRECIO_PREMIUM)).orElse(PRECIO_PREMIUM);
    }
}
