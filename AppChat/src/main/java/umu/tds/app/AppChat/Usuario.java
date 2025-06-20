package umu.tds.app.AppChat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.swing.ImageIcon;

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

    // --- Atributos para la gestión de contactos ---
    // Almacena la lista de objetos Contacto una vez cargados.
    private List<Contacto> contactos;
    // Almacena los IDs de los contactos leídos de la persistencia, para ser procesados por el Controlador.
    private List<Integer> contactosID;

    public Usuario(String telefono, String nombre, String password, String email, String saludo, ImageIcon foto, boolean premium) {
        this.id = 0; // Se inicializa a 0 para indicar que no está persistido.
        this.telefono = telefono;
        this.nombre = nombre;
        this.password = password;
        this.email = email;
        this.saludo = saludo;
        this.foto = foto != null ? foto : new ImageIcon();
        this.premium = premium;
        this.contactos = new ArrayList<>(); // Lista de contactos del usuario
        this.contactosID = new ArrayList<>(); // Lista de IDs para la carga inicial
        this.fechaRegistro = LocalDate.now();
    }
    
    // --- Getters y Setters ---

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setName(String nombre) { this.nombre = nombre; } // Alias para consistencia

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

    // --- Métodos para la gestión de contactos (crucial para la nueva lógica) ---

    /**
     * Devuelve una copia inmutable de la lista de contactos del usuario.
     * Esto previene modificaciones externas no controladas.
     * @return Una lista de contactos no modificable.
     */
    public List<Contacto> getContactos() {
        return Collections.unmodifiableList(contactos);
    }
    
    /**
     * Devuelve la lista interna de contactos.
     * Este método es para ser usado por el Adaptador de persistencia para guardar los IDs.
     * @return La lista interna de contactos.
     */
    public List<Contacto> getContactosInternal() {
        return this.contactos;
    }

    /**
     * Reemplaza la lista de contactos del usuario. Usado por el Controlador durante la inicialización.
     * @param contactos La nueva lista de contactos.
     */
    public void setContactos(List<Contacto> contactos) {
        this.contactos = contactos;
    }

    /**
     * Añade un nuevo contacto a la lista personal del usuario si no existe ya.
     * @param contacto El contacto a añadir.
     * @return true si el contacto fue añadido, false en caso contrario.
     */
    public boolean addContacto(Contacto contacto) {
        if (contacto == null || this.contactos.contains(contacto)) {
            return false;
        }
        this.contactos.add(contacto);
        this.contactosID.add(contacto.getCodigo()); // 🔥 esto es CLAVE para la persistencia
        return true;
    }


    /**
     * Elimina un contacto de la lista personal del usuario.
     * @param contacto El contacto a eliminar.
     * @return true si el contacto fue eliminado, false en caso contrario.
     */
    /**
     * Elimina un contacto de la lista personal del usuario, asegurando que tanto
     * el objeto como su ID sean eliminados para mantener la consistencia.
     * @param contacto El contacto a eliminar.
     * @return true si el contacto fue eliminado, false en caso contrario.
     */
    public boolean removeContacto(Contacto contacto) {
        boolean removed = this.contactos.remove(contacto);
        if (removed) {
            // Si se eliminó el objeto, también eliminamos su ID de la lista de IDs.
            this.contactosID.remove(Integer.valueOf(contacto.getCodigo()));
        }
        return removed;
    }

    /**
     * Obtiene la lista de IDs de contactos. Usado por el Controlador durante la carga inicial.
     * @return Lista de IDs de contactos.
     */
    public List<Integer> getContactosID() {
        return contactosID;
    }
    
    /**
     * Establece la lista de IDs de contactos. Usado por el Adaptador al recuperar el usuario.
     * @param contactosID La lista de IDs leída de la base de datos.
     */
    public void setContactosID(List<Integer> contactosID) {
        this.contactosID = contactosID;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(telefono);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Usuario other = (Usuario) obj;
        return Objects.equals(telefono, other.telefono);
    }
    
    /**
     * Añade un ID de contacto a la lista de IDs.
     * Este método debe ser usado por el Controlador cuando se realizan operaciones
     * que solo afectan a los IDs, como la actualización tras una modificación.
     * @param id El ID del contacto a añadir.
     */
    public void addContactoID(int id) {
        if (!this.contactosID.contains(id)) {
            this.contactosID.add(id);
        }
    }

    /**
     * Elimina un ID de contacto de la lista de IDs.
     * Es crucial para actualizar la lista de referencias del usuario cuando un contacto
     * cambia su ID en la base de datos.
     * @param id El ID del contacto a eliminar.
     * @return true si el ID fue encontrado y eliminado, false en caso contrario.
     */
    public boolean removeContactoID(int id) {
        // Integer.valueOf(id) es necesario para que Java no confunda el 'id'
        // con un índice de la lista, sino con el objeto a eliminar.
        return this.contactosID.remove(Integer.valueOf(id));
    }
    
}