package umu.tds.app.AppChat;

import java.time.LocalDateTime;

public class Mensaje implements Comparable<Mensaje> {
	private int codigo;
	private String texto;
	private LocalDateTime hora;
	private int emoticono;
	private Usuario emisor;
	private Contacto receptor;

	// Constructor.
	public Mensaje(String texto, LocalDateTime hora, Usuario emisor, Contacto receptor) {
		this.texto = texto;
		this.hora = hora;
		this.setEmisor(emisor);
		this.setReceptor(receptor);
	}

	public Mensaje(int emoticono, LocalDateTime hora, Usuario emisor, Contacto receptor) {
		this.texto = "";
		this.hora = hora;
		this.emoticono = emoticono;
		this.setEmisor(emisor);
		this.setReceptor(receptor);
	}

	public Mensaje(String texto, int emoticono, LocalDateTime hora) {
		this.texto = texto;
		this.emoticono = emoticono;
		this.hora = hora;
	}

	// Getters.
	public String getTexto() {
		return texto;
	}

	public LocalDateTime getHora() {
		return hora;
	}

	public int getEmoticono() {
		return emoticono;
	}

	public Usuario getEmisor() {
		return emisor;
	}

	public Contacto getReceptor() {
		return receptor;
	}

	public int getCodigo() {
		return codigo;
	}

	public void setCodigo(int codigo) {
		this.codigo = codigo;
	}

	public void setReceptor(Contacto receptor) {
		this.receptor = receptor;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	public void setEmisor(Usuario emisor) {
		this.emisor = emisor;
	}

	@Override
	public int compareTo(Mensaje o) {
		return hora.compareTo(o.hora);
	}
}