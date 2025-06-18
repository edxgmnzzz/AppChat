package umu.tds.app.persistencia;

import java.util.List;

import umu.tds.app.AppChat.Usuario;

public interface UsuarioDAO {
	public void registrarUsuario(Usuario usuario);
	public void borrarUsuario(Usuario usuario);
	public void modificarUsuario(Usuario usuario);
	public Usuario recuperarUsuario(int codigo);
	public List<Usuario> recuperarTodosUsuarios();
	public Usuario recuperarUsuarioPorTelefono(String telefono);
}