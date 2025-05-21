package umu.tds.app.persistencia;

import java.util.*;
import java.util.stream.Collectors;

import javax.swing.ImageIcon;

import beans.Entidad;
import beans.Propiedad;
import tds.driver.FactoriaServicioPersistencia;
import tds.driver.ServicioPersistencia;
import umu.tds.app.AppChat.Usuario;
import umu.tds.app.AppChat.Contacto;
import umu.tds.app.AppChat.ContactoIndividual;
import umu.tds.app.AppChat.Grupo;
import umu.tds.app.AppChat.Status;

public class AdaptadorUsuarioTDS implements UsuarioDAO {

	private static ServicioPersistencia sp;
	private static AdaptadorUsuarioTDS unicaInstancia = null;
	private static final String ENTIDAD_USUARIO = "usuario";

	private AdaptadorUsuarioTDS() {
		sp = FactoriaServicioPersistencia.getInstance().getServicioPersistencia();
	}

	public static AdaptadorUsuarioTDS getInstancia() {
		if (unicaInstancia == null)
			unicaInstancia = new AdaptadorUsuarioTDS();
		return unicaInstancia;
	}

	@Override
	public void registrarUsuario(Usuario usuario) {
		if (usuario == null || usuario.registrado()) return;

		Entidad eUsuario = new Entidad();
		eUsuario.setNombre(ENTIDAD_USUARIO);
		eUsuario.setPropiedades(List.of(
				new Propiedad("nombre", usuario.getName()),
				new Propiedad("telefono", usuario.getTelefono()),
				new Propiedad("password", usuario.getPassword()),
				new Propiedad("email", usuario.getEmail()),
				new Propiedad("saludo", usuario.getSaludo()),
				new Propiedad("premium", String.valueOf(usuario.isPremium())),
				new Propiedad("foto", usuario.getFoto().getDescription() != null ? usuario.getFoto().getDescription() : "")
		));

		eUsuario = sp.registrarEntidad(eUsuario);
		usuario.registrarId(eUsuario.getId());
	}

	@Override
	public void borrarUsuario(Usuario usuario) {
		Entidad e = sp.recuperarEntidad(usuario.getId());
		sp.borrarEntidad(e);
	}

	@Override
	public void modificarUsuario(Usuario usuario) {
		Entidad e = sp.recuperarEntidad(usuario.getId());
		sp.eliminarPropiedadEntidad(e, "nombre");
		sp.anadirPropiedadEntidad(e, "nombre", usuario.getName());
		sp.eliminarPropiedadEntidad(e, "telefono");
		sp.anadirPropiedadEntidad(e, "telefono", usuario.getTelefono());
		sp.eliminarPropiedadEntidad(e, "password");
		sp.anadirPropiedadEntidad(e, "password", usuario.getPassword());
		sp.eliminarPropiedadEntidad(e, "email");
		sp.anadirPropiedadEntidad(e, "email", usuario.getEmail());
		sp.eliminarPropiedadEntidad(e, "saludo");
		sp.anadirPropiedadEntidad(e, "saludo", usuario.getSaludo());
		sp.eliminarPropiedadEntidad(e, "premium");
		sp.anadirPropiedadEntidad(e, "premium", String.valueOf(usuario.isPremium()));
		sp.eliminarPropiedadEntidad(e, "foto");
		sp.anadirPropiedadEntidad(e, "foto", usuario.getFoto().getDescription() != null ? usuario.getFoto().getDescription() : "");
	}

	@Override
	public Usuario recuperarUsuario(int codigo) {
		Entidad e = sp.recuperarEntidad(codigo);
		String nombre = sp.recuperarPropiedadEntidad(e, "nombre");
		String telefono = sp.recuperarPropiedadEntidad(e, "telefono");
		String password = sp.recuperarPropiedadEntidad(e, "password");
		String email = sp.recuperarPropiedadEntidad(e, "email");
		String saludo = sp.recuperarPropiedadEntidad(e, "saludo");
		boolean premium = Boolean.parseBoolean(sp.recuperarPropiedadEntidad(e, "premium"));
		String rutaFoto = sp.recuperarPropiedadEntidad(e, "foto");
		ImageIcon icono = new ImageIcon();
		if (rutaFoto != null && !rutaFoto.isEmpty()) {
			icono = new ImageIcon(rutaFoto);
			icono.setDescription(rutaFoto);
		}

		Usuario u = new Usuario(telefono, nombre, password, email, saludo, icono, premium);
		//u.registrarId(codigo);
		return u;
	}

	@Override
	public List<Usuario> recuperarTodosUsuarios() {
		return sp.recuperarEntidades(ENTIDAD_USUARIO).stream()
				.map(e -> recuperarUsuario(e.getId()))
				.collect(Collectors.toList());
	}
}
