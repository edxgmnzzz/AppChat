package umu.tds.app.AppChat;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.swing.ImageIcon;

/**
 * Clase que representa un grupo de chat en la aplicación de mensajería.
 * Extiende de la clase Contacto y gestiona las funcionalidades específicas
 * de los grupos como sus integrantes y administrador.
 */
public class Grupo extends Contacto {
    /** Ruta del icono predeterminado para grupos */
    public static final String GROUP_ICON_PATH = "/umu/tds/app/recursos/grupo.png";

    /** Lista de contactos individuales que conforman el grupo */
    private List<ContactoIndividual> integrantes;
    
    /** Usuario administrador del grupo */
    private Usuario admin;

    /**
     * Crea un nuevo grupo con nombre, lista de participantes y administrador.
     *
     * @param nombre     Nombre identificativo del grupo
     * @param contactos  Lista de participantes iniciales del grupo
     * @param admin      Usuario que administrará el grupo
     */
    public Grupo(String nombre, List<ContactoIndividual> contactos, Usuario admin) {
        super(nombre);
        this.integrantes = contactos;
        this.admin = admin;
    }

    /**
     * Crea un nuevo grupo con historial de mensajes inicial.
     *
     * @param nombre     Nombre identificativo del grupo
     * @param mensajes   Lista inicial de mensajes del grupo
     * @param contactos  Lista de participantes iniciales del grupo
     * @param admin      Usuario que administrará el grupo
     */
    public Grupo(String nombre, List<Mensaje> mensajes, List<ContactoIndividual> contactos, Usuario admin) {
        super(nombre, mensajes);
        this.integrantes = contactos;
        this.admin = admin;
    }

    /**
     * Obtiene la lista de participantes actuales del grupo.
     *
     * @return Lista de contactos individuales que son miembros del grupo
     */
    public List<ContactoIndividual> getParticipantes() {
        return integrantes;
    }

    /**
     * Obtiene el usuario administrador actual del grupo.
     *
     * @return Usuario que administra el grupo
     */
    public Usuario getAdmin() {
        return admin;
    }

    /**
     * Obtiene el icono predeterminado del grupo.
     *
     * @return Imagen que representa al grupo en la interfaz
     */
    @Override
    public ImageIcon getFoto() {
        ImageIcon imagen = new ImageIcon(Grupo.class.getResource(GROUP_ICON_PATH));
        imagen.setDescription(GROUP_ICON_PATH);
        return imagen;
    }

    /**
     * Incorpora un nuevo participante al grupo.
     *
     * @param contacto Contacto individual a añadir como miembro
     */
    public void addIntegrante(ContactoIndividual contacto) {
        integrantes.add(contacto);
    }

    /**
     * Modifica el administrador del grupo.
     *
     * @param usuario Nuevo usuario que será administrador
     */
    public void cambiarAdmin(Usuario usuario) {
        admin = usuario;
    }

    /**
     * Actualiza la lista completa de integrantes del grupo.
     *
     * @param contactos Nueva lista de participantes que reemplazará a la actual
     */
    public void setIntegrantes(List<ContactoIndividual> contactos) {
        this.integrantes = contactos;
    }

    /**
     * Recopila todos los mensajes enviados por los miembros del grupo.
     * El parámetro usuario es ignorado en esta implementación.
     *
     * @param emptyOpt Parámetro no utilizado en esta implementación
     * @return Lista de todos los mensajes enviados al grupo
     */
    @Override
    public List<Mensaje> getMensajesRecibidos(Optional<Usuario> emptyOpt) {
        return this.integrantes.stream()
                .flatMap(c -> c.getUsuario().getContactos().stream())
                .filter(c -> c instanceof Grupo)
                .map(c -> (Grupo) c)
                .filter(g -> this.equals(g))
                .flatMap(g -> g.getMensajesEnviados().stream())
                .collect(Collectors.toList());
    }

    /**
     * Obtiene los mensajes enviados por un usuario específico al grupo.
     *
     * @param usuario Usuario del cual se quieren obtener los mensajes
     * @return Lista de mensajes enviados por el usuario al grupo
     */
    public List<Mensaje> getMisMensajesGrupo(Usuario usuario) {
        return getMensajesEnviados().stream()
                .filter(m -> m.getEmisor().getCodigo() == usuario.getCodigo())
                .collect(Collectors.toList());
    }

    /**
     * Elimina y devuelve todos los mensajes del grupo.
     *
     * @return Copia de los mensajes eliminados
     */
    public List<Mensaje> removeMensajesRecibidos() {
        List<Mensaje> recibidos = getMensajesRecibidos(Optional.empty());
        List<Mensaje> copia = new LinkedList<>(recibidos);
        recibidos.clear();
        return copia;
    }

    /**
     * Verifica si un usuario es miembro del grupo.
     *
     * @param usuario Usuario a verificar
     * @return true si el usuario es miembro del grupo, false en caso contrario
     */
    public boolean hasParticipante(Usuario usuario) {
        return integrantes.stream()
                .anyMatch(i -> i.getUsuario().equals(usuario));
    }

    /**
     * Calcula el código hash del grupo basado en su nombre.
     *
     * @return Código hash calculado
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getNombre() == null) ? 0 : getNombre().hashCode());
        return result;
    }

    /**
     * Compara si dos grupos son iguales basándose en su nombre.
     * Dos grupos se consideran iguales si tienen exactamente el mismo nombre.
     *
     * @param obj Objeto a comparar
     * @return true si los grupos son iguales, false en caso contrario
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Grupo other = (Grupo) obj;
        if (getNombre() == null) {
            if (other.getNombre() != null)
                return false;
        } else if (!getNombre().equals(other.getNombre()))
            return false;
        return true;
    }
}