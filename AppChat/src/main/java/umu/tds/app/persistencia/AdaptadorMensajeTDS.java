package umu.tds.app.persistencia;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import beans.Entidad;
import beans.Propiedad;
import tds.driver.FactoriaServicioPersistencia;
import tds.driver.ServicioPersistencia;
import umu.tds.app.AppChat.Contacto;
import umu.tds.app.AppChat.ContactoIndividual;
import umu.tds.app.AppChat.Grupo;
import umu.tds.app.AppChat.Mensaje;
import umu.tds.app.AppChat.Usuario;

public class AdaptadorMensajeTDS implements MensajeDAO {
	private static AdaptadorMensajeTDS unicaInstancia = null;
	private static ServicioPersistencia sp;
	private static final String ENTIDAD_MENSAJE = "mensaje";

	private AdaptadorMensajeTDS() {
		sp = FactoriaServicioPersistencia.getInstance().getServicioPersistencia();
	}

	public static AdaptadorMensajeTDS getInstancia() {
		if (unicaInstancia == null)
			unicaInstancia = new AdaptadorMensajeTDS();
		return unicaInstancia;
	}

	public void registrarMensaje(Mensaje mensaje) {
		if (mensaje == null || mensaje.getCodigo() > 0) return;

		Entidad e = new Entidad();
		e.setNombre(ENTIDAD_MENSAJE);
		e.setPropiedades(List.of(
				new Propiedad("texto", mensaje.getTexto()),
				new Propiedad("hora", mensaje.getHora().toString()),
				new Propiedad("emoticono", String.valueOf(mensaje.getEmoticono())),
				new Propiedad("emisor", String.valueOf(mensaje.getEmisor().getId())),
				new Propiedad("receptor", String.valueOf(mensaje.getReceptor().getCodigo())),
				new Propiedad("grupo", String.valueOf(mensaje.getReceptor() instanceof Grupo))
		));

		e = sp.registrarEntidad(e);
		mensaje.setCodigo(e.getId());
	}

	public void borrarMensaje(Mensaje mensaje) {
		Entidad e = sp.recuperarEntidad(mensaje.getCodigo());
		sp.borrarEntidad(e);
	}

	public void modificarMensaje(Mensaje mensaje) {
		Entidad e = sp.recuperarEntidad(mensaje.getCodigo());
		sp.eliminarPropiedadEntidad(e, "texto");
		sp.anadirPropiedadEntidad(e, "texto", mensaje.getTexto());
		sp.eliminarPropiedadEntidad(e, "hora");
		sp.anadirPropiedadEntidad(e, "hora", mensaje.getHora().toString());
		sp.eliminarPropiedadEntidad(e, "emoticono");
		sp.anadirPropiedadEntidad(e, "emoticono", String.valueOf(mensaje.getEmoticono()));
		sp.eliminarPropiedadEntidad(e, "emisor");
		sp.anadirPropiedadEntidad(e, "emisor", String.valueOf(mensaje.getEmisor().getId()));
		sp.eliminarPropiedadEntidad(e, "receptor");
		sp.anadirPropiedadEntidad(e, "receptor", String.valueOf(mensaje.getReceptor().getCodigo()));
		sp.eliminarPropiedadEntidad(e, "grupo");
		sp.anadirPropiedadEntidad(e, "grupo", String.valueOf(mensaje.getReceptor() instanceof Grupo));
	}

	public Mensaje recuperarMensaje(int codigo) {
		Entidad e = sp.recuperarEntidad(codigo);

		String texto = sp.recuperarPropiedadEntidad(e, "texto");
		LocalDateTime hora = LocalDateTime.parse(sp.recuperarPropiedadEntidad(e, "hora"));
		int emoticono = Integer.parseInt(sp.recuperarPropiedadEntidad(e, "emoticono"));
		int idEmisor = Integer.parseInt(sp.recuperarPropiedadEntidad(e, "emisor"));
		int codReceptor = Integer.parseInt(sp.recuperarPropiedadEntidad(e, "receptor"));
		boolean esGrupo = Boolean.parseBoolean(sp.recuperarPropiedadEntidad(e, "grupo"));

		Usuario emisor = AdaptadorUsuarioTDS.getInstancia().recuperarUsuario(codigo);
		Contacto receptor;
		if (esGrupo)
			receptor = AdaptadorGrupoTDS.getInstancia().recuperarGrupo(codReceptor);
		else
			receptor = AdaptadorContactoIndividualTDS.getInstancia().recuperarContacto(codReceptor);

		Mensaje m = new Mensaje(texto, hora, emisor, receptor);
		//m.setEmoticono(emoticono);
		m.setCodigo(codigo);
		return m;
	}

	public List<Mensaje> recuperarTodosMensajes() {
		return sp.recuperarEntidades(ENTIDAD_MENSAJE).stream()
				.map(e -> recuperarMensaje(e.getId()))
				.collect(Collectors.toList());
	}
}