package umu.tds.app.AppChat;


import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.swing.ImageIcon;

public class Grupo extends Contacto {
	// Properties
	public static final String GROUP_ICON_PATH = "/umu/tds/app/recursos/grupo.png";

	private List<ContactoIndividual> integrantes;
	private Usuario admin;

	// Constructor.
	/**
	 * Constructor de la clase grupo
	 * 
	 * @param nombre    Nombre del grupo
	 * @param Contactos Participantes del grupo
	 * @param admin     Usuario admin del grupo
	 */
	public Grupo(String nombre, List<ContactoIndividual> Contactos, Usuario admin) {
		super(nombre);
		this.integrantes = Contactos;
		this.admin = admin;
	}

	/**
	 * Constructor sobrecargado de la clase grupo
	 * 
	 * @param nombre    Nombre del grupo
	 * @param mensajes  Mensajes intercambiados en el grupo
	 * @param Contactos Participantes del grupo
	 * @param admin     Usuario admin del grupo
	 */
	public Grupo(String nombre, List<Mensaje> mensajes, List<ContactoIndividual> Contactos, Usuario admin) {
		super(nombre, mensajes);
		this.integrantes = Contactos;
		this.admin = admin;
	}

	// Getters
	/**
	 * Devuelve los participantes del grupo
	 * 
	 * @return Lista con los participantes del grupo
	 */
	public List<ContactoIndividual> getParticipantes() {
		return integrantes;
	}

	/**
	 * Devuelvo el usuario administrador del grupo
	 * 
	 * @return Usuario administrador del grupo
	 */
	public Usuario getAdmin() {
		return admin;
	}

	/**
	 * Devuelve la foto de perfil del grupo
	 * 
	 * @return Imagen de perfil del grupo
	 */
	@Override
	public ImageIcon getFoto() {
		ImageIcon imagen = new ImageIcon(Grupo.class.getResource(GROUP_ICON_PATH));
		imagen.setDescription(GROUP_ICON_PATH);
		return imagen;
	}

	// Methods
	/**
	 * Añade un nuevo integrante del grupo
	 * 
	 * @param c Contactoo a añadir al grupo
	 */
	public void addIntegrante(ContactoIndividual c) {
		integrantes.add(c);
	}

	/**
	 * Cambia el admin del grupo
	 * 
	 * @param u Usuario que será el nuevo admin
	 */
	public void cambiarAdmin(Usuario u) {
		admin = u;
	}

	/**
	 * Insertar integrantes para el grupo
	 * 
	 * @param Contactos Lista de Contactos que serán los participantes del grupo
	 */
	public void setIntegrantes(List<ContactoIndividual> Contactos) {
		this.integrantes = Contactos;
	}

	/**
	 * Devuelve los mensajes que han enviado el resto de usuarios por el grupo. El
	 * valor del parametro pasado como parámetro no importa
	 * 
	 * @param emptyOpt Usuario opcional del que obtengo los mensajes recibidos
	 * @return Lista con los mensajes recibidos de ese usuario
	 */
	@Override
	public List<Mensaje> getMensajesRecibidos(Optional<Usuario> emptyOpt) {
		return this.integrantes.stream().flatMap(c -> c.getUsuario().getContactos().stream())
				.filter(c -> c instanceof Grupo).map(c -> (Grupo) c).filter(g -> this.equals(g))
				.flatMap(g -> g.getMensajesEnviados().stream()).collect(Collectors.toList());
	}

	/**
	 * Devuelve los mensajes que ha enviado el usuario al grupo
	 * 
	 * @param usuario Usuario del que cojo los mensajes que el ha enviado a su grupo
	 * @return Lista con los mensajes que el usuario ha enviado al grupo
	 */
	public List<Mensaje> getMisMensajesGrupo(Usuario usuario) {
		return getMensajesEnviados().stream().filter(m -> m.getEmisor().getCodigo() == usuario.getCodigo())
				.collect(Collectors.toList());
	}

	/**
	 * Borra todos los mensajes del grupo
	 */
	public List<Mensaje> removeMensajesRecibidos() {
		List<Mensaje> recibidos = getMensajesRecibidos(Optional.empty());
		List<Mensaje> copia = new LinkedList<Mensaje>(recibidos);
		recibidos.clear();
		return copia;
	}

	/**
	 * Indica si el usuario pertenece al grupo
	 * 
	 * @param usuario Usuario a comprobar si está en el grupo
	 * @return Devuelve si el usuario pertenece al grupo
	 */
	public boolean hasParticipante(Usuario usuario) {
		return integrantes.stream().anyMatch(i -> i.getUsuario().equals(usuario));
	}

	// HashCode e Equals
	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getNombre() == null) ? 0 : getNombre().hashCode());
		return result;
	}

	/**
	 * Dos grupos son iguales si tienen el mismo nombre.
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
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