package umu.tds.app.persistencia;

import java.util.*;
import java.util.stream.Collectors;

import beans.Entidad;
import beans.Propiedad;
import tds.driver.FactoriaServicioPersistencia;
import tds.driver.ServicioPersistencia;
import umu.tds.app.AppChat.ContactoIndividual;
import umu.tds.app.AppChat.Mensaje;
import umu.tds.app.AppChat.Usuario;

public class AdaptadorContactoIndividualTDS implements ContactoIndividualDAO {
	private static ServicioPersistencia sp;
	private static AdaptadorContactoIndividualTDS unicaInstancia = null;
	private static final String ENTIDAD_CONTACTO = "contacto";

	private AdaptadorContactoIndividualTDS() {
		sp = FactoriaServicioPersistencia.getInstance().getServicioPersistencia();
	}

	public static AdaptadorContactoIndividualTDS getInstancia() {
		if (unicaInstancia == null)
			unicaInstancia = new AdaptadorContactoIndividualTDS();
		return unicaInstancia;
	}

	public void registrarContacto(ContactoIndividual contacto) {
		if (contacto == null || contacto.getCodigo() > 0) return;

		Entidad eContacto = new Entidad();
		eContacto.setNombre(ENTIDAD_CONTACTO);
		eContacto.setPropiedades(List.of(
				new Propiedad("nombre", contacto.getNombre()),
				new Propiedad("movil", String.valueOf(contacto.getTelefono())),
				new Propiedad("usuario", String.valueOf(contacto.getUsuario().getId())),
				new Propiedad("mensajes", codigosMensajes(contacto.getMensajesEnviados()))
		));

		eContacto = sp.registrarEntidad(eContacto);
		contacto.setCodigo(eContacto.getId());
	}

	public void borrarContacto(ContactoIndividual contacto) {
		Entidad e = sp.recuperarEntidad(contacto.getCodigo());
		sp.borrarEntidad(e);
	}

	public void modificarContacto(ContactoIndividual contacto) {
		Entidad e = sp.recuperarEntidad(contacto.getCodigo());
		sp.eliminarPropiedadEntidad(e, "nombre");
		sp.anadirPropiedadEntidad(e, "nombre", contacto.getNombre());
		sp.eliminarPropiedadEntidad(e, "movil");
		sp.anadirPropiedadEntidad(e, "movil", String.valueOf(contacto.getTelefono()));
		sp.eliminarPropiedadEntidad(e, "usuario");
		sp.anadirPropiedadEntidad(e, "usuario", String.valueOf(contacto.getUsuario().getId()));
		sp.eliminarPropiedadEntidad(e, "mensajes");
		sp.anadirPropiedadEntidad(e, "mensajes", codigosMensajes(contacto.getMensajesEnviados()));
	}

	public ContactoIndividual recuperarContacto(int codigo) {
		Entidad e = sp.recuperarEntidad(codigo);
		String nombre = sp.recuperarPropiedadEntidad(e, "nombre");
		String movil = sp.recuperarPropiedadEntidad(e, "movil");
		int usuarioId = Integer.parseInt(sp.recuperarPropiedadEntidad(e, "usuario"));
		String codigosMensajes = sp.recuperarPropiedadEntidad(e, "mensajes");

		Usuario usuario = AdaptadorUsuarioTDS.getInstancia().recuperarUsuario(codigo);
		ContactoIndividual c = new ContactoIndividual(nombre, codigo, movil, usuario);

		obtenerMensajes(codigosMensajes).forEach(c::sendMensaje);
		return c;
	}

	private String codigosMensajes(List<Mensaje> mensajes) {
		return mensajes.stream()
				.map(m -> String.valueOf(m.getCodigo()))
				.collect(Collectors.joining(" "));
	}

	private List<Mensaje> obtenerMensajes(String codigos) {
		List<Mensaje> mensajes = new ArrayList<>();
		StringTokenizer st = new StringTokenizer(codigos);
		AdaptadorMensajeTDS adaptador = AdaptadorMensajeTDS.getInstancia();
		while (st.hasMoreTokens()) {
			mensajes.add(adaptador.recuperarMensaje(Integer.parseInt(st.nextToken())));
		}
		return mensajes;
	}

	@Override
	public List<ContactoIndividual> recuperarTodosContactos() {
		// TODO Auto-generated method stub
		return null;
	}
}