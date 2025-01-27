package umu.tds.app.persistencia;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import beans.Entidad;
import beans.Propiedad;
import tds.driver.FactoriaServicioPersistencia;
import tds.driver.ServicioPersistencia;
import umu.tds.app.AppChat.Persistencia;
import umu.tds.app.AppChat.*;


public class AdaptadorGrupoTDS implements GrupoDAO {
	private static Persistencia pers;
	private static AdaptadorGrupoTDS unicaInstancia = null;

	private AdaptadorGrupoTDS() {
		pers = FactoriaServicioPersistencia.getInstance().getServicioPersistencia();
	}

	public static AdaptadorGrupoTDS getInstancia() {
		if (unicaInstancia == null)
			unicaInstancia = new AdaptadorGrupoTDS();
		return unicaInstancia;
	}

	@Override
	public void registrarGrupo(Grupo Grupo) {
		Entidad eGrupo = new Entidad();
		boolean existe = true;

		// Si la entidad está registrada no la registra de nuevo
		try {
			eGrupo = pers.recuperarEntidad(Grupo.getCodigo());
		} catch (NullPointerException e) {
			existe = false;
		}
		if (existe)
			return;

		// Registramos primero los atributos que son objetos
		// Registrar los mensajes del grupo
		registrarSiNoExistenMensajes(Grupo.getMensajesEnviados());

		// Registramos los contactos del grupo si no existen (Integrantes)
		registrarSiNoExistenContactos(Grupo.getParticipantes());

		// Registramos a nuestro usuario administrador si no existe.
		registrarSiNoExisteAdmin(Grupo.getAdmin());

		// Atributos propios del grupo
		eGrupo.setNombre("grupo");
		eGrupo.setPropiedades(new ArrayList<Propiedad>(Arrays.asList(new Propiedad("nombre", Grupo.getNombre()),
				new Propiedad("mensajesRecibidos", obtenerCodigosMensajesRecibidos(Grupo.getMensajesEnviados())),
				new Propiedad("integrantes", obtenerCodigosContactosIndividual(Grupo.getParticipantes())),
				new Propiedad("admin", String.valueOf(Grupo.getAdmin().getCodigo())))));

		// Registrar entidad usuario
		eGrupo = pers.registrarEntidad(eGrupo);

		// Identificador unico
		Grupo.setCodigo(eGrupo.getId());
		
		// Guardamos en el pool
		PoolDAO.getInstancia().addObjeto(Grupo.getCodigo(), Grupo);
	}

	@Override
	public void borrarGrupo(Grupo Grupo) {
		// Borramos los elementos de las tablas que lo componen (En este caso borramos
		// los mensajes del grupo a eliminar)
		Entidad eGrupo;
		AdaptadorMensajeTDS adaptadorMensaje = AdaptadorMensajeTDS.getInstancia();

		for (Mensaje mensaje : Grupo.getMensajesEnviados()) {
			adaptadorMensaje.borrarMensaje(mensaje);
		}

		eGrupo = pers.recuperarEntidad(Grupo.getCodigo());
		pers.borrarEntidad(eGrupo);
		
		// Si está en el pool, borramos del pool
		if (PoolDAO.getInstancia().contiene(Grupo.getCodigo()))
			PoolDAO.getInstancia().removeObjeto(Grupo.getCodigo());
	}

	@Override
	public void modificarGrupo(Grupo Grupo) {
		Entidad eGrupo = pers.recuperarEntidad(Grupo.getCodigo());

		// Se da el cambiazo a las propiedades del grupo
		pers.eliminarPropiedadEntidad(eGrupo, "nombre");
		pers.anadirPropiedadEntidad(eGrupo, "nombre", Grupo.getNombre());
		pers.eliminarPropiedadEntidad(eGrupo, "mensajesRecibidos");
		pers.anadirPropiedadEntidad(eGrupo, "mensajesRecibidos",
				obtenerCodigosMensajesRecibidos(Grupo.getMensajesEnviados()));
		pers.eliminarPropiedadEntidad(eGrupo, "integrantes");
		pers.anadirPropiedadEntidad(eGrupo, "integrantes",
				obtenerCodigosContactosIndividual(Grupo.getParticipantes()));
		pers.eliminarPropiedadEntidad(eGrupo, "admin");
		pers.anadirPropiedadEntidad(eGrupo, "admin", String.valueOf(Grupo.getAdmin().getCodigo()));
	}

	@Override
	public Grupo recuperarGrupo(int codigo) {
		// Si la entidad esta en el pool la devuelve directamente
		if (PoolDAO.getInstancia().contiene(codigo))
			return (Grupo) PoolDAO.getInstancia().getObjeto(codigo);

		// Sino, la recupera de la base de datos
		// Recuperamos la entidad
		Entidad eGrupo = pers.recuperarEntidad(codigo);

		// recuperar propiedades que no son objetos
		String nombre = null;
		nombre = pers.recuperarPropiedadEntidad(eGrupo, "nombre");

		Grupo Grupo = new Grupo(nombre, new LinkedList<Mensaje>(), new LinkedList<ContactoIndividual>(), null);
		Grupo.setCodigo(codigo);

		// Metemos al grupo en el pool antes de llamar a otros adaptadores
		PoolDAO.getInstancia().addObjeto(codigo, Grupo);
		
		// Mensajes que el grupo tiene
		List<Mensaje> mensajes = obtenerMensajesDesdeCodigos(pers.recuperarPropiedadEntidad(eGrupo, "mensajesRecibidos"));
		for (Mensaje m : mensajes)
			Grupo.sendMensaje(m);
				
		// Contactos que el grupo tiene
		List<ContactoIndividual> contactos = obtenerIntegrantesDesdeCodigos(pers.recuperarPropiedadEntidad(eGrupo, "integrantes"));
		for (ContactoIndividual c : contactos)
			Grupo.addIntegrante(c);

		// Obtener admin
		Grupo.cambiarAdmin(obtenerUsuarioDesdeCodigo(pers.recuperarPropiedadEntidad(eGrupo, "admin")));

		// Devolvemos el objeto grupo
		return Grupo;
	}

	@Override
	public List<Grupo> recuperarTodosGrupos() {
		List<Grupo> grupos = new LinkedList<>();
		List<Entidad> eGrupos = pers.recuperarEntidades("grupo");

		for (Entidad eGrupo : eGrupos) {
			grupos.add(recuperarGrupo(eGrupo.getId()));
		}
		
		return grupos;
	}

	
	// Funciones auxiliares.
	private void registrarSiNoExistenMensajes(List<Mensaje> Mensajes) {
		AdaptadorMensajeTDS adaptadorMensajes = AdaptadorMensajeTDS.getInstancia();
		Mensajes.stream().forEach(m -> adaptadorMensajes.registrarMensaje(m));
	}

	private void registrarSiNoExistenContactos(List<ContactoIndividual> contacts) {
		AdaptadorContactoIndividualTDS adaptadorContactos = AdaptadorContactoIndividualTDS.getInstancia();
		contacts.stream().forEach(c -> adaptadorContactos.registrarContacto(c));
	}

	private void registrarSiNoExisteAdmin(User admin) {
		AdaptadorUserTDS adaptadorUsuarios = AdaptadorUserTDS.getInstancia();
		adaptadorUsuarios.registrarUsuario(admin);
	}

	private String obtenerCodigosContactosIndividual(List<ContactoIndividual> contactosIndividuales) {
		return contactosIndividuales.stream().map(c -> String.valueOf(c.getCodigo())).reduce("", (l, c) -> l + c + " ")
				.trim();
	}

	private String obtenerCodigosMensajesRecibidos(List<Mensaje> mensajesRecibidos) {
		return mensajesRecibidos.stream().map(m -> String.valueOf(m.getCodigo())).reduce("", (l, m) -> l + m + " ")
				.trim();
	}
	
	private List<Mensaje> obtenerMensajesDesdeCodigos(String codigos) {
		List<Mensaje> mensajes = new LinkedList<>();
		StringTokenizer strTok = new StringTokenizer(codigos, " ");
		AdaptadorMensajeTDS adaptadorMensajes = AdaptadorMensajeTDS.getInstancia();
		while (strTok.hasMoreTokens()) {
			mensajes.add(adaptadorMensajes.recuperarMensaje(Integer.valueOf((String) strTok.nextElement())));
		}
		return mensajes;
	}
	
	private List<ContactoIndividual> obtenerIntegrantesDesdeCodigos(String codigos) {
		List<ContactoIndividual> contactos = new LinkedList<>();
		StringTokenizer strTok = new StringTokenizer(codigos, " ");
		AdaptadorContactoIndividualTDS adaptadorC = AdaptadorContactoIndividualTDS.getInstancia();
		while (strTok.hasMoreTokens()) {
			contactos.add(adaptadorC.recuperarContacto(Integer.valueOf((String) strTok.nextElement())));
		}
		return contactos;
	}
	
	private User obtenerUsuarioDesdeCodigo(String codigo) {
		AdaptadorUserTDS adaptadorUsuarios = AdaptadorUserTDS.getInstancia();
		return adaptadorUsuarios.recuperarUsuario(Integer.valueOf(codigo));
	}
}
