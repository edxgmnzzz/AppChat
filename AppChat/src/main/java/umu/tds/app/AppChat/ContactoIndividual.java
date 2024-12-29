package umu.tds.app.AppChat;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import javax.swing.ImageIcon;

public class ContactoIndividual extends Contacto {
	// Properties.
	private int movil;
	private Usuario usuario;

	// Constructor.
	public ContactoIndividual(String nombre, int movil, Usuario usuario) {
		super(nombre);
		this.movil = movil;
		this.usuario = usuario;
	}

	public ContactoIndividual(String nombre, LinkedList<Mensaje> mensajes, int movil, Usuario usuario) {
		super(nombre, mensajes);
		this.movil = movil;
		this.usuario = usuario;
	}

	// Getters.
	public int getMovil() {
		return movil;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	@Override
	public ImageIcon getFoto() {
		return usuario.getProfilePhoto();
	}

	// Setters.
	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	// Methods
	/**
	 * Dado un usuario me devuelve el Contacto que este usuario tiene (como lo ve
	 * desde su perspectiva)
	 * 
	 * @param usuario Usuario cuyo Contacto quiero obtener
	 * @return Devuelve el Contacto que tengo guardado para el usuario pasado como
	 *         parámetro. Null si no lo tengo guardado
	 */
	public ContactoIndividual getContacto(Usuario usuario) {
		return this.usuario.getContactos().stream().filter(c -> c instanceof ContactoIndividual)
				.map(c -> (ContactoIndividual) c).filter(c -> c.getUsuario().equals(usuario)).findAny().orElse(null);
	}

	@Override
	public List<Mensaje> getMensajesRecibidos(Optional<Usuario> usuario) {
		ContactoIndividual Contacto = getContacto(usuario.orElse(null));
		if (Contacto != null) {
			return Contacto.getMensajesEnviados();
		} else
			return new LinkedList<>();
	}

	// Devuelve el estado del Contacto
	public Optional<Status> getEstado() {
		return usuario.getEstado();
	}

	// Añade al Contacto al grupo en cuestion
//	public void addGrupo(Grupo grupo) {
//		usuario.addGrupo(grupo);
//	}
//
//	// Expulsamos al Contacto del grupo en cuestión
//	public void eliminarGrupo(Grupo grupo) {
//		usuario.removeContacto(grupo);
//	}
//
//	/**
//	 * Modifica el grupo del Contacto
//	 * 
//	 * @param g Grupo ya modificado
//	 */
//	public void modificarGrupo(Grupo g) {
//		List<Grupo> grupos = usuario.getGrupos();
//
//		grupos.remove(g);
//		grupos.add(g);
//	}

	// Borra los mensajes que le ha mandado este Contacto al usuarioActual
	public List<Mensaje> removeMensajesRecibidos(Usuario usuarioActual) {
		List<Mensaje> recibidos = getContacto(usuarioActual).getMensajesEnviados();
		List<Mensaje> copia = new LinkedList<>(recibidos);
		recibidos.clear();
		return copia;
	}

	// HashCode e Equals
	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + movil;
		return result;
	}

	/**
	 * Dos Contactos son iguales si tienen el mismo número de teléfono
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
		ContactoIndividual other = (ContactoIndividual) obj;
		if (movil != other.movil)
			return false;
		return true;
	}

	/**
	 * Comprueba si se corresponde con el usuario pasado como parámetro
	 * 
	 * @param otherUsuario Usuario con el que realizar la comprobación
	 * @return Devuelve si el usuario asociado al Contacto es el mismo que el pasado
	 *         como parámetro
	 */
	public boolean isUsuario(Usuario otherUsuario) {
		return usuario.equals(otherUsuario);
	}

}