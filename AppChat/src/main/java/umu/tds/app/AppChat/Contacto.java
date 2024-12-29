package umu.tds.app.AppChat;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import javax.swing.ImageIcon;

public abstract class Contacto {
	// Properties.
	private int codigo;
	private String nombre;
	private List<Mensaje> mensajes;

	// Constructor.
	/**
	 * Constructor de la clase Contactoo
	 * 
	 * @param nombre Nombre del Contactoo
	 */
	public Contacto(String nombre) {
		this(nombre, new LinkedList<>());
	}

	/**
	 * Constructor sobrecargado de la clase Contactoo
	 * 
	 * @param nombre   Nombre del Contactoo
	 * @param mensajes Lista de mensajes intercambiados con el usuario
	 */
	public Contacto(String nombre, List<Mensaje> mensajes) {
		this.nombre = nombre;
		this.mensajes = mensajes;
	}

	// Getters.
	/**
	 * Devuelve el nombre del Contactoo
	 * 
	 * @return Nombre del Contactoo
	 */
	public String getNombre() {
		return nombre;
	}

	/**
	 * Devuelve los mensajes que ese Contactoo recibe de mi
	 * 
	 * @return Lista con todos los mensajes
	 */
	public List<Mensaje> getMensajesEnviados() {
		return mensajes;
	}

	/**
	 * Devuelve los mensajes que recibo de ese Contactoo
	 * 
	 * @param usuario Usuario al que se le mandaron los mensajes
	 * @return Lista con los mensajes que este Contactoo envió al usuario. Estará
	 *         vacía si no le envió ninguno
	 */
	public abstract List<Mensaje> getMensajesRecibidos(Optional<Usuario> usuario);

	/**
	 * Devuelve el código del Contactoo
	 * 
	 * @return Código del Contactoo
	 */
	public int getCodigo() {
		return codigo;
	}

	/**
	 * Devuelve la foto del Contactoo
	 * 
	 * @return Devuelve la foto del Contactoo
	 */
	public abstract ImageIcon getFoto();

	// Setters
	/**
	 * Setter para el código
	 * 
	 * @param codigo Codigo del Contactoo
	 */
	public void setCodigo(int codigo) {
		this.codigo = codigo;
	}

	/**
	 * Setter para el nombre
	 * 
	 * @param nombre Nombre del Contactoo
	 */
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	/**
	 * Elimina los mensajes eliminados
	 * 
	 * @return Lista con los mensajes que le envié a ese Contactoo.
	 */
	public List<Mensaje> removeMensajesEnviados() {
		List<Mensaje> lista = new LinkedList<>(mensajes); // copia
		mensajes.clear();
		return lista;
	}

	/**
	 * Método para añadir mensajes con ese Contactoo
	 * 
	 * @param mensajes Mensajes a añadir a la lista de mensajes
	 */
	public void addMensajes(List<Mensaje> mensajes) {
		this.mensajes.addAll(mensajes);
	}

	// Methods
	/**
	 * Método para añadir un mensajes con ese Contactoo
	 * 
	 * @param mensaje Mensaje a añadir a la lista de mensajes que le he enviado a
	 *                ese contaacto
	 */
	public void sendMensaje(Mensaje Mensaje) {
		mensajes.add(Mensaje);
	}

	@Override
	public String toString() {
		return nombre;
	}
}