package umu.tds.app.AppChat;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import javax.swing.ImageIcon;

/**
 * Clase que representa un contacto individual en la aplicación de mensajería.
 * Extiende de la clase Contacto y añade funcionalidades específicas para 
 * contactos individuales como número de móvil y usuario asociado.
 */
public class ContactoIndividual extends Contacto {
    /** Número de teléfono móvil del contacto */
    private int movil;
    
    /** Usuario asociado al contacto */
    private Usuario usuario;

    /**
     * Constructor principal para crear un contacto individual.
     *
     * @param nombre  Nombre del contacto
     * @param movil   Número de teléfono móvil del contacto
     * @param usuario Usuario asociado al contacto
     */
    public ContactoIndividual(String nombre, int movil, Usuario usuario) {
        super(nombre);
        this.movil = movil;
        this.usuario = usuario;
    }

    /**
     * Constructor sobrecargado que permite inicializar con una lista de mensajes.
     *
     * @param nombre   Nombre del contacto
     * @param mensajes Lista inicial de mensajes
     * @param movil    Número de teléfono móvil del contacto
     * @param usuario  Usuario asociado al contacto
     */
   /* public ContactoIndividual(String nombre, LinkedList<Mensaje> mensajes, int movil, Usuario usuario) {
        super(nombre, mensajes);
        this.movil = movil;
        this.usuario = usuario;
    }*/

    /**
     * Obtiene el número de teléfono móvil del contacto.
     *
     * @return Número de móvil del contacto
     */
    public int getMovil() {
        return movil;
    }

    /**
     * Obtiene el usuario asociado al contacto.
     *
     * @return Usuario asociado
     */
    public Usuario getUsuario() {
        return usuario;
    }

    /**
     * Obtiene la foto de perfil del contacto.
     *
     * @return Imagen de perfil del usuario asociado
     */
    @Override
    public ImageIcon getFoto() {
        return usuario.getProfilePhotos();
    }

    /**
     * Actualiza el usuario asociado al contacto.
     *
     * @param usuario Nuevo usuario a asociar
     */
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    /**
     * Obtiene el contacto desde la perspectiva de un usuario específico.
     * 
     * @param usuario Usuario cuyo contacto se quiere obtener
     * @return Contacto individual desde la perspectiva del usuario especificado,
     *         o null si no existe
     */
    public ContactoIndividual getContacto(Usuario usuario) {
        return this.usuario.getContactos().stream()
                .filter(c -> c instanceof ContactoIndividual)
                .map(c -> (ContactoIndividual) c)
                .filter(c -> c.getUsuario().equals(usuario))
                .findAny()
                .orElse(null);
    }

    /**
     * Obtiene los mensajes recibidos de un usuario específico.
     *
     * @param usuario Usuario opcional del que se quieren obtener los mensajes
     * @return Lista de mensajes recibidos del usuario especificado
     */
    @Override
    public List<Mensaje> getMensajesRecibidos(Optional<Usuario> usuario) {
        ContactoIndividual contacto = getContacto(usuario.orElse(null));
        if (contacto != null) {
            return contacto.getMensajesEnviados();
        } else {
            return new LinkedList<>();
        }
    }

    /**
     * Elimina y devuelve los mensajes recibidos de un usuario específico.
     *
     * @param usuarioActual Usuario del que se quieren eliminar los mensajes
     * @return Lista de mensajes eliminados
     */
    public List<Mensaje> removeMensajesRecibidos(Usuario usuarioActual) {
        List<Mensaje> recibidos = getContacto(usuarioActual).getMensajesEnviados();
        List<Mensaje> copia = new LinkedList<>(recibidos);
        recibidos.clear();
        return copia;
    }

    /**
     * Calcula el código hash del contacto basado en su número de móvil.
     *
     * @return Código hash calculado
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + movil;
        return result;
    }

    /**
     * Compara si dos contactos son iguales basándose en su número de móvil.
     * Dos contactos se consideran iguales si tienen el mismo número de teléfono.
     *
     * @param obj Objeto a comparar
     * @return true si los contactos son iguales, false en caso contrario
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ContactoIndividual other = (ContactoIndividual) obj;
        if (movil != other.movil)
            return false;
        return true;
    }

    /**
     * Verifica si el contacto corresponde a un usuario específico.
     *
     * @param otherUsuario Usuario a verificar
     * @return true si el usuario asociado es el mismo que el especificado,
     *         false en caso contrario
     */
    public boolean isUsuario(Usuario otherUsuario) {
        return usuario.equals(otherUsuario);
    }
}