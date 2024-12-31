package umu.tds.app.AppChat;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import javax.swing.ImageIcon;

/**
 * Clase abstracta base que representa un contacto en una aplicación de mensajería.
 * Gestiona la información del contacto y el historial de mensajes entre usuarios.
 */


public abstract class Contacto {
    /** Identificador único del contacto */
    private int codigo;
    
    /** Nombre del contacto */
    private String nombre;
    
    /** Lista que contiene el historial de mensajes */
    private List<Mensaje> mensajes;

    /**
     * Crea un nuevo contacto con el nombre especificado y una lista de mensajes vacía.
     *
     * @param nombre Nombre del contacto
     */
    public Contacto(String nombre) {
        this(nombre, new LinkedList<>());
    }

    /**
     * Crea un nuevo contacto con el nombre especificado y una lista inicial de mensajes.
     *
     * @param nombre   Nombre del contacto
     * @param mensajes Lista inicial de mensajes
     */
    public Contacto(String nombre, List<Mensaje> mensajes) {
        this.nombre = nombre;
        this.mensajes = mensajes;
    }

    /**
     * Obtiene el nombre del contacto.
     *
     * @return Nombre del contacto
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Obtiene todos los mensajes enviados a este contacto.
     *
     * @return Lista de mensajes enviados
     */
    public List<Mensaje> getMensajesEnviados() {
        return mensajes;
    }

    /**
     * Obtiene los mensajes recibidos de este contacto para un usuario específico.
     * La implementación varía según el tipo de contacto.
     *
     * @param usuario Usuario opcional para el cual recuperar los mensajes
     * @return Lista de mensajes recibidos de este contacto para el usuario especificado
     */
    public abstract List<Mensaje> getMensajesRecibidos(Optional<Usuario> usuario);

    /**
     * Obtiene el identificador único del contacto.
     *
     * @return Código identificador del contacto
     */
    public int getCodigo() {
        return codigo;
    }

    /**
     * Obtiene la foto de perfil del contacto.
     * La implementación varía según el tipo de contacto.
     *
     * @return Imagen de perfil del contacto
     */
    public abstract ImageIcon getFoto();

    /**
     * Establece el identificador único del contacto.
     *
     * @param codigo Nuevo código identificador del contacto
     */
    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    /**
     * Actualiza el nombre del contacto.
     *
     * @param nombre Nuevo nombre del contacto
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Elimina y devuelve todos los mensajes enviados.
     * La lista original de mensajes se vacía después de esta operación.
     *
     * @return Copia de los mensajes enviados antes de eliminarlos
     */
    public List<Mensaje> removeMensajesEnviados() {
        List<Mensaje> lista = new LinkedList<>(mensajes); // crea una copia
        mensajes.clear();
        return lista;
    }

    /**
     * Añade múltiples mensajes al historial del contacto.
     *
     * @param mensajes Lista de mensajes a añadir
     */
    public void addMensajes(List<Mensaje> mensajes) {
        this.mensajes.addAll(mensajes);
    }

    /**
     * Añade un único mensaje al historial del contacto.
     *
     * @param mensaje Mensaje a añadir a la lista de mensajes del contacto
     */
    public void sendMensaje(Mensaje mensaje) {
        mensajes.add(mensaje);
    }

    /**
     * Proporciona una representación en cadena del contacto.
     *
     * @return Nombre del contacto
     */
    @Override
    public String toString() {
        return nombre;
    }
}