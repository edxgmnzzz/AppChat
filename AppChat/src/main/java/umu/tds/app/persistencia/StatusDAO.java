package umu.tds.app.persistencia;

import java.util.List;

import umu.tds.app.AppChat.Status;

public interface StatusDAO {
	public void registrarEstado(Status status);
	public void borrarEstado(Status status);
	public void modificarEstado(Status status);
	public Status recuperarEstado(int codigo);
	public List<Status> recuperarTodosEstados();
}