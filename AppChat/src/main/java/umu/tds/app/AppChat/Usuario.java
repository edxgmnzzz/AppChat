package umu.tds.app.AppChat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javax.swing.ImageIcon;

/**
 * Representa un usuario registrado en la aplicación de mensajería.
 * Contiene su información de perfil, estado premium, fecha de registro y contactos.
 */
public class Usuario {

    private int id;
    private String telefono;
    private String nombre;
    private String password;
    private String email;
    private String saludo;
    private ImageIcon foto;
    private String urlFoto;
    private boolean premium;
    private LocalDate fechaRegistro;

    // Gestión de contactos
    private List<Contacto> contactos;
    private List<Integer> contactosID;

    /**
     * Crea un nuevo usuario.
     *
     * @param telefono Número de teléfono del usuario.
     * @param nombre Nombre real o visible del usuario.
     * @param password Contraseña de acceso.
     * @param email Correo electrónico.
     * @param saludo Mensaje de estado personalizado.
     * @param foto Imagen de perfil (puede ser null).
     * @param premium true si el usuario es premium.
     */
    public Usuario(String telefono, String nombre, String password, String email, String saludo, ImageIcon foto, boolean premium) {
        this.id = 0; // No persistido aún
        this.telefono = telefono;
        this.nombre = nombre;
        this.password = password;
        this.email = email;
        this.saludo = saludo;
        this.foto = (foto != null) ? foto : new ImageIcon();
        this.premium = premium;
        this.contactos = new ArrayList<>();
        this.contactosID = new ArrayList<>();
        this.fechaRegistro = LocalDate.now();
    }

    // ─────────────────────── Getters / Setters ───────────────────────

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setName(String nombre) { this.nombre = nombre; } // alias

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSaludo() { return saludo != null ? saludo : ""; }
    public void setSaludo(String saludo) { this.saludo = saludo; }

    public ImageIcon getFoto() { return foto; }
    public void setFoto(ImageIcon foto) { this.foto = foto; }

    public ImageIcon getProfilePhotos() { return getFoto(); }

    public String getUrlFoto() { return urlFoto; }
    public void setUrlFoto(String url) { this.urlFoto = url; }

    public boolean isPremium() { return premium; }
    public void setPremium(boolean premium) { this.premium = premium; }

    public LocalDate getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDate fecha) { this.fechaRegistro = fecha; }

    // ─────────────────────── Contactos ───────────────────────

    /**
     * Devuelve una copia inmutable de la lista de contactos del usuario.
     * 
     * @return Lista no modificable de contactos.
     */
    public List<Contacto> getContactos() {
        return Collections.unmodifiableList(contactos);
    }

    /**
     * Devuelve la lista interna de contactos (editable).
     * Se usa principalmente en persistencia.
     *
     * @return Lista de contactos.
     */
    public List<Contacto> getContactosInternal() {
        return this.contactos;
    }

    /**
     * Reemplaza toda la lista de contactos del usuario.
     *
     * @param contactos Lista completa de contactos.
     */
    public void setContactos(List<Contacto> contactos) {
        this.contactos = contactos;
    }

    /**
     * Añade un contacto si no está ya presente.
     * También añade su ID a la lista de referencias.
     *
     * @param contacto Contacto a añadir.
     * @return true si se añadió correctamente.
     */
    public boolean addContacto(Contacto contacto) {
        if (contacto == null || this.contactos.contains(contacto)) return false;
        this.contactos.add(contacto);
        this.contactosID.add(contacto.getCodigo());
        return true;
    }

    /**
     * Elimina un contacto y su ID asociado.
     *
     * @param contacto Contacto a eliminar.
     * @return true si se eliminó correctamente.
     */
    public boolean removeContacto(Contacto contacto) {
        boolean removed = this.contactos.remove(contacto);
        if (removed) {
            this.contactosID.remove(Integer.valueOf(contacto.getCodigo()));
        }
        return removed;
    }

    /**
     * Devuelve la lista de IDs de contactos.
     *
     * @return Lista de IDs.
     */
    public List<Integer> getContactosID() {
        return contactosID;
    }

    /**
     * Establece la lista de IDs de contactos.
     *
     * @param contactosID Lista de códigos leídos desde persistencia.
     */
    public void setContactosID(List<Integer> contactosID) {
        this.contactosID = contactosID;
    }

    /**
     * Añade un ID a la lista si no existe.
     *
     * @param id ID del contacto a añadir.
     */
    public void addContactoID(int id) {
        if (!this.contactosID.contains(id)) {
            this.contactosID.add(id);
        }
    }

    /**
     * Elimina un ID de la lista de referencias.
     *
     * @param id ID del contacto a eliminar.
     * @return true si se eliminó correctamente.
     */
    public boolean removeContactoID(int id) {
        return this.contactosID.remove(Integer.valueOf(id));
    }

    // ─────────────────────── Comparación ───────────────────────

    /**
     * Devuelve el código hash basado en el número de teléfono.
     *
     * @return Hash del usuario.
     */
    @Override
    public int hashCode() {
        return Objects.hash(telefono);
    }

    /**
     * Compara este usuario con otro por su teléfono.
     *
     * @param obj Objeto a comparar.
     * @return true si los teléfonos coinciden.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Usuario other = (Usuario) obj;
        return Objects.equals(telefono, other.telefono);
    }
}
