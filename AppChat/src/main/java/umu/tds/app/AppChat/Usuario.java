package umu.tds.app.AppChat;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import javax.swing.ImageIcon;

public class Usuario {
    private static final double PRECIO_PREMIUM = 19.90;
    private static final LocalDate FECHA_JOVEN = LocalDate.of(2003, 1, 1);
    private static final LocalDate FECHA_ADULTO = LocalDate.of(1955, 1, 1);
    private static final String SALUDO_INICIAL = "Hello World!";

    // Propiedades
    private int codigo;
    private List<ImageIcon> profilePhotos;
    private String name;
    private LocalDate fechaNacimiento;
    private int numTelefono;
    private String nick;
    private String password;
    private boolean premium;
    private String saludo;
    private Optional<Status> estado;
    private List<Grupo> gruposAdmin;
    private List<Contacto> Contactos;
    private Optional<Descuento> descuento;
    private String email; // Nuevo atributo

    // Constructores
    /**
     * Constructor para la creación de un usuario nuevo en el sistema
     * 
     * @param icono           Foto de perfil del usuario
     * @param name            Nombre completo del usuario
     * @param fechaNacimiento Fecha de nacimiento
     * @param numTelefono     Telefono
     * @param nick            Alias elegido por el usuario
     * @param password        Contraseña para autenticarse el usuario
     * @param email           Correo electrónico del usuario
     */
    public Usuario(ImageIcon icono, String name, LocalDate fechaNacimiento, int numTelefono, String nick,
            String password, String email) {
        this(new LinkedList<>(Arrays.asList(icono)), name, fechaNacimiento, numTelefono, nick, password, false, null,
                SALUDO_INICIAL, new LinkedList<>(), new LinkedList<>(), null, email);
    }

    /**
     * Constructor empleado en persistencia para recuperar los objetos
     * 
     * @param iconList        Lista con las fotos de perfil del usuario
     * @param name            Nombre completo del usuario
     * @param fechaNacimiento Fecha de nacimiento
     * @param numTelefono     Telefono
     * @param nick            Alias elegido por el usuario
     * @param password        Contraseña para autenticarse el usuario
     * @param premium         Indica si el usuario es premium
     * @param descuento       Porcentaje de descuento que el usuario tendrá
     * @param saludo          Saludo del usuario
     * @param email           Correo electrónico del usuario
     */
    public Usuario(List<ImageIcon> iconList, String name, LocalDate fechaNacimiento, int numTelefono, String nick,
            String password, boolean premium, Descuento descuento, String saludo, String email) {
        this(iconList, name, fechaNacimiento, numTelefono, nick, password, premium, null, saludo, new LinkedList<>(),
                new LinkedList<>(), descuento, email);
    }

    /**
     * Constructor principal
     * 
     * @param iconList        Lista con las fotos de perfil del usuario
     * @param name            Nombre completo del usuario
     * @param fechaNacimiento Fecha de nacimiento
     * @param numTelefono     Telefono
     * @param nick            Alias elegido por el usuario
     * @param password        Contraseña para autenticarse el usuario
     * @param premium         Indica si el usuario es premium
     * @param estado          Estado que el usuario tendrá
     * @param saludo          Saludo del usuario
     * @param gruposAdmin     Grupos en los que el usuario es administrador
     * @param Contactos       Contactos que tiene guardados el usuario
     * @param descuento       Porcentaje de descuento que el usuario tendrá
     * @param email           Correo electrónico del usuario
     */
    public Usuario(List<ImageIcon> iconList, String name, LocalDate fechaNacimiento, int numTelefono, String nick,
            String password, boolean premium, Status estado, String saludo, List<Grupo> gruposAdmin,
            List<Contacto> Contactos, Descuento descuento, String email) {
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
        this.Contactos = Contactos;
        this.email = email; // Inicializamos el email

        if (descuento == null) {
            // Si es joven descuento para jóvenes, si es muy mayor para mayores
            if (fechaNacimiento.isAfter(FECHA_JOVEN)) {
                descuento = new DescuentoJunior();
            } else if (fechaNacimiento.isBefore(FECHA_ADULTO)) {
                descuento = new DescuentoSenior();
            }
        }
        this.descuento = Optional.ofNullable(descuento);
    }

    // Getters
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
        return Contactos;
    }

    public int getCodigo() {
        return codigo;
    }

    public Optional<Descuento> getDescuento() {
        return descuento;
    }

    public String getEmail() {
        return email; // Getter para el email
    }

    public void setEmail(String email) {
        this.email = email; // Setter para el email
    }

    public double getPrecio() {
        if (descuento.isPresent()) {
            return descuento.get().getDescuento(PRECIO_PREMIUM);
        } else
            return PRECIO_PREMIUM;
    }

    // Otros métodos siguen igual...
    
}
