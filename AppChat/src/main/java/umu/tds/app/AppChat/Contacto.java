package umu.tds.app.AppChat;

import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;

/**
 * Clase base abstracta que representa un contacto (individual o de grupo).
 * Contiene la información común a todos los tipos de contacto, como nombre,
 * código identificador y lista de mensajes asociados.
 */
public abstract class Contacto {

    /** Alias o nombre del contacto. */
    protected String nombre;

    /** Código único interno que identifica al contacto. */
    protected int codigo;

    /** Lista de mensajes asociados al contacto (enviados o recibidos). */
    protected List<Mensaje> mensajes;

    /**
     * Crea un contacto con un nombre y sin mensajes previos.
     *
     * @param nombre Nombre o alias del contacto.
     */
    public Contacto(String nombre) {
        this.nombre = nombre;
        this.codigo = generateUniqueCode();
        this.mensajes = new ArrayList<>();
    }

    /**
     * Crea un contacto con un nombre y una lista inicial de mensajes.
     *
     * @param nombre Nombre o alias del contacto.
     * @param mensajes Lista de mensajes que ya tenía el contacto.
     */
    public Contacto(String nombre, List<Mensaje> mensajes) {
        this.nombre = nombre;
        this.codigo = generateUniqueCode();
        this.mensajes = new ArrayList<>(mensajes);
    }

    /**
     * Constructor protegido usado internamente cuando ya se dispone
     * del código único (por ejemplo, al recuperar de la base de datos).
     *
     * @param nombre Nombre o alias del contacto.
     * @param codigo Código único asignado al contacto.
     */
    protected Contacto(String nombre, int codigo) {
        this.nombre = nombre;
        this.codigo = codigo;
        this.mensajes = new ArrayList<>();
    }

    /**
     * Genera un código único pseudo-aleatorio basado en la hora del sistema.
     *
     * @return Un entero que se puede usar como identificador del contacto.
     */
    private static int generateUniqueCode() {
        return (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
    }

    // ─────────────────────────── Getters / Setters ──────────────────────────

    /**
     * Devuelve el nombre o alias del contacto.
     *
     * @return El nombre del contacto.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Devuelve el código interno del contacto.
     *
     * @return El código único.
     */
    public int getCodigo() {
        return codigo;
    }

    /**
     * Establece (o reescribe) el código interno del contacto.
     * Útil si el código lo asigna la capa de persistencia.
     *
     * @param codigo Nuevo código único.
     */
    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    /**
     * Cambia el nombre del contacto.
     *
     * @param nombre Nuevo nombre o alias.
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    // ───────────────────────────── Funcionalidad ────────────────────────────

    /**
     * Añade un mensaje a la lista de mensajes asociados al contacto.
     *
     * @param mensaje Mensaje a añadir.
     */
    public void sendMensaje(Mensaje mensaje) {
        mensajes.add(mensaje);
    }

    /**
     * Obtiene los mensajes enviados por este contacto (donde él es emisor).
     *
     * @return Lista de mensajes enviados.
     */
    public List<Mensaje> getMensajesEnviados() {
        List<Mensaje> enviados = new ArrayList<>();
        for (Mensaje msg : mensajes) {
            if (msg.getEmisor() != null && msg.getReceptor() == this) {
                enviados.add(msg);
            }
        }
        return enviados;
    }

    /**
     * Devuelve una copia de la lista completa de mensajes asociados.
     *
     * @return Lista de mensajes (copia defensiva).
     */
    public List<Mensaje> getMensajes() {
        return new ArrayList<>(mensajes);
    }

    /**
     * Devuelve la imagen asociada al contacto.
     * Por defecto, se devuelve un icono vacío; las subclases o la vista
     * pueden sobreescribir este método para proporcionar imágenes reales.
     *
     * @return {@link ImageIcon} con la foto del contacto.
     */
    public ImageIcon getFoto() {
        return new ImageIcon();
    }
}
