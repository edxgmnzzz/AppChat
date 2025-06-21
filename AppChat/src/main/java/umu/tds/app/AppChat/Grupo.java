package umu.tds.app.AppChat;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.swing.ImageIcon;

/**
 * Representa un grupo de usuarios en la aplicación de mensajería.
 * Extiende la clase {@link Contacto} y permite gestionar participantes, mensajes
 * y un administrador del grupo.
 */
public class Grupo extends Contacto {

    /** Ruta al icono por defecto para grupos. */
    public static final String GROUP_ICON_PATH = "/umu/tds/app/recursos/grupo.png";

    private List<ContactoIndividual> integrantes;
    private Usuario admin;
    private ImageIcon foto;
    private String urlFoto;

    /**
     * Crea un nuevo grupo con un nombre, una lista de integrantes, un administrador y una foto opcional.
     *
     * @param nombre     Nombre del grupo.
     * @param contactos  Lista de contactos individuales que forman parte del grupo.
     * @param admin      Usuario administrador del grupo.
     * @param foto       Foto del grupo (puede ser null).
     */
    public Grupo(String nombre, List<ContactoIndividual> contactos, Usuario admin, ImageIcon foto) {
        super(nombre);
        this.integrantes = new ArrayList<>(contactos);
        this.admin = admin;
        this.foto = (foto != null) ? foto : new ImageIcon();
    }

    /**
     * Devuelve una copia de la lista de participantes del grupo.
     *
     * @return Lista de integrantes del grupo.
     */
    public List<ContactoIndividual> getParticipantes() {
        return new ArrayList<>(integrantes);
    }

    /**
     * Devuelve el administrador actual del grupo.
     *
     * @return Usuario administrador.
     */
    public Usuario getAdmin() {
        return admin;
    }

    /**
     * Devuelve el icono del grupo desde el recurso predeterminado.
     *
     * @return {@link ImageIcon} representando el grupo.
     */
    @Override
    public ImageIcon getFoto() {
        ImageIcon imagen = new ImageIcon(Grupo.class.getResource(GROUP_ICON_PATH));
        imagen.setDescription(GROUP_ICON_PATH);
        return imagen;
    }

    /**
     * Añade un integrante al grupo si no está ya incluido.
     *
     * @param contacto El contacto a añadir.
     */
    public void addIntegrante(ContactoIndividual contacto) {
        if (!integrantes.contains(contacto)) {
            integrantes.add(contacto);
        }
    }

    /**
     * Cambia el administrador del grupo.
     *
     * @param usuario Nuevo administrador.
     */
    public void cambiarAdmin(Usuario usuario) {
        this.admin = usuario;
    }

    /**
     * Reemplaza la lista completa de integrantes del grupo.
     *
     * @param contactos Lista nueva de integrantes.
     */
    public void setIntegrantes(List<ContactoIndividual> contactos) {
        this.integrantes = new ArrayList<>(contactos);
    }

    /**
     * Devuelve los mensajes recibidos por este grupo.
     *
     * @param usuario (Opcional) Si se proporciona, filtra los mensajes enviados por ese usuario.
     * @return Lista de mensajes recibidos.
     */
    public List<Mensaje> getMensajesRecibidos(Optional<Usuario> usuario) {
        List<Mensaje> recibidos = new ArrayList<>();
        for (Mensaje msg : mensajes) {
            if (msg.getReceptor() == this &&
                (!usuario.isPresent() || msg.getEmisor() == usuario.get())) {
                recibidos.add(msg);
            }
        }
        return recibidos;
    }

    /**
     * Elimina todos los mensajes recibidos por el grupo.
     *
     * @return Lista de mensajes eliminados.
     */
    public List<Mensaje> removeMensajesRecibidos() {
        List<Mensaje> recibidos = getMensajesRecibidos(Optional.empty());
        List<Mensaje> copia = new ArrayList<>(recibidos);
        mensajes.removeAll(recibidos);
        return copia;
    }

    /**
     * Devuelve el código hash del grupo basado en su nombre.
     */
    @Override
    public int hashCode() {
        return nombre != null ? nombre.hashCode() : 0;
    }

    /**
     * Determina si dos grupos son iguales por su nombre.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Grupo other = (Grupo) obj;
        return nombre != null ? nombre.equals(other.nombre) : other.nombre == null;
    }

    /**
     * Establece manualmente el código del grupo (normalmente usado por la capa de persistencia).
     *
     * @param id Nuevo código.
     */
    public void setCodigo(int id) {
        this.codigo = id;
    }

    /**
     * Devuelve la URL asociada a la foto del grupo.
     *
     * @return Cadena con la URL.
     */
    public String getUrlFoto() {
        return urlFoto;
    }

    /**
     * Establece la URL de la foto del grupo.
     *
     * @param urlFoto Nueva URL de imagen.
     */
    public void setUrlFoto(String urlFoto) {
        this.urlFoto = urlFoto;
    }

    /**
     * Devuelve la lista de integrantes (sin copiar).
     * Se recomienda usar {@link #getParticipantes()} para evitar modificar internamente el grupo.
     *
     * @return Lista interna de integrantes.
     */
    public List<ContactoIndividual> getIntegrantes() {
        return integrantes;
    }

    /**
     * Establece el nuevo administrador del grupo.
     *
     * @param admin Usuario que será el nuevo administrador.
     */
    public void setAdmin(Usuario admin) {
        this.admin = admin;
    }

    /**
     * Cambia la foto del grupo.
     *
     * @param foto Nueva imagen como {@link ImageIcon}.
     */
    public void setFoto(ImageIcon foto) {
        this.foto = foto;
    }
}
