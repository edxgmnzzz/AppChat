package umu.tds.app.persistencia;

import java.util.List;

import umu.tds.app.AppChat.Mensaje;

public interface MensajeDAO {
	public void registrarMensaje(Mensaje mensaje);
	public void borrarMensaje(Mensaje mensaje);
	public void modificarMensaje(Mensaje mensaje);
	public Mensaje recuperarMensaje(int codigo);
	public List<Mensaje> recuperarTodosMensajes();
}