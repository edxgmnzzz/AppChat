package umu.tds.app.persistencia;

import java.util.*;
import java.util.stream.Collectors;

import beans.Entidad;
import beans.Propiedad;
import tds.driver.FactoriaServicioPersistencia;
import tds.driver.ServicioPersistencia;
import umu.tds.app.AppChat.ContactoIndividual;
import umu.tds.app.AppChat.Grupo;
import umu.tds.app.AppChat.Mensaje;

public class AdaptadorGrupoTDS implements GrupoDAO {
	private static final String ENTIDAD_GRUPO = "grupo";
	private ServicioPersistencia servPersistencia;
	private static AdaptadorGrupoTDS unicaInstancia = null;

	private AdaptadorGrupoTDS() {
		servPersistencia = FactoriaServicioPersistencia.getInstance().getServicioPersistencia();
	}

	public static AdaptadorGrupoTDS getInstancia() {
		if (unicaInstancia == null)
			unicaInstancia = new AdaptadorGrupoTDS();
		return unicaInstancia;
	}

	@Override
	public void registrarGrupo(Grupo grupo) {
	    if (grupo == null || grupo.getCodigo() > 0) {
	        //System.out.println("⚠️ Grupo nulo o ya registrado. Cancelando.");
	        return;
	    }

	    //System.out.println("✅ Registrando grupo: " + grupo.getNombre());

	    Entidad eGrupo = new Entidad();
	    eGrupo.setNombre(ENTIDAD_GRUPO);
	    eGrupo.setPropiedades(List.of(
	            new Propiedad("nombre", grupo.getNombre()),
	            new Propiedad("integrantes", obtenerCodigosContactos(grupo.getParticipantes())),
	            new Propiedad("mensajes", obtenerCodigosMensajes(grupo.getMensajesEnviados()))
	    ));

	    eGrupo = servPersistencia.registrarEntidad(eGrupo);
	    grupo.setCodigo(eGrupo.getId());
	    //System.out.println("✅ Grupo registrado con ID: " + grupo.getCodigo());
	}

	@Override
	public void borrarGrupo(Grupo grupo) {
		if (grupo == null || grupo.getCodigo() <= 0) return;
		Entidad eGrupo = servPersistencia.recuperarEntidad(grupo.getCodigo());
		servPersistencia.borrarEntidad(eGrupo);
	}

	@Override
	public void modificarGrupo(Grupo grupo) {
		Entidad eGrupo = servPersistencia.recuperarEntidad(grupo.getCodigo());
		servPersistencia.eliminarPropiedadEntidad(eGrupo, "nombre");
		servPersistencia.anadirPropiedadEntidad(eGrupo, "nombre", grupo.getNombre());
		servPersistencia.eliminarPropiedadEntidad(eGrupo, "integrantes");
		servPersistencia.anadirPropiedadEntidad(eGrupo, "integrantes", obtenerCodigosContactos(grupo.getParticipantes()));
		servPersistencia.eliminarPropiedadEntidad(eGrupo, "mensajes");
		servPersistencia.anadirPropiedadEntidad(eGrupo, "mensajes", obtenerCodigosMensajes(grupo.getMensajesEnviados()));
	}

	@Override
	public Grupo recuperarGrupo(int codigo) {
	    //System.out.println("🔁 Recuperando grupo con ID: " + codigo);
	    Entidad eGrupo = servPersistencia.recuperarEntidad(codigo);
	    if (eGrupo == null) {
	        //System.out.println("❌ No se encontró entidad para ID: " + codigo);
	        return null;
	    }

	    String nombre = servPersistencia.recuperarPropiedadEntidad(eGrupo, "nombre");
	    String codIntegrantes = servPersistencia.recuperarPropiedadEntidad(eGrupo, "integrantes");
	    String codMensajes = servPersistencia.recuperarPropiedadEntidad(eGrupo, "mensajes");

	    //System.out.println("🔍 Nombre del grupo: " + nombre);
	    //System.out.println("👥 Códigos integrantes: " + codIntegrantes);
	    //System.out.println("💬 Códigos mensajes: " + codMensajes);

	    List<ContactoIndividual> integrantes = obtenerContactosDesdeCodigos(codIntegrantes);
	    List<Mensaje> mensajes = obtenerMensajesDesdeCodigos(codMensajes);

	    Grupo grupo = new Grupo(nombre, integrantes, null);
	    mensajes.forEach(grupo::sendMensaje);
	    return grupo;
	}


	@Override
	public List<Grupo> recuperarTodosGrupos() {
		List<Entidad> entidades = servPersistencia.recuperarEntidades(ENTIDAD_GRUPO);
		return entidades.stream()
				.map(e -> recuperarGrupo(e.getId()))
				.collect(Collectors.toList());
	}

	private String obtenerCodigosContactos(List<ContactoIndividual> contactos) {
		return contactos.stream()
				.map(c -> String.valueOf(c.getCodigo()))
				.collect(Collectors.joining(" "));
	}

	private String obtenerCodigosMensajes(List<Mensaje> mensajes) {
		return mensajes.stream()
				.map(m -> String.valueOf(m.getCodigo()))
				.collect(Collectors.joining(" "));
	}

	private List<ContactoIndividual> obtenerContactosDesdeCodigos(String codigos) {
		List<ContactoIndividual> contactos = new ArrayList<>();
		StringTokenizer st = new StringTokenizer(codigos);
		AdaptadorContactoIndividualTDS adaptador = AdaptadorContactoIndividualTDS.getInstancia();
		while (st.hasMoreTokens()) {
			contactos.add(adaptador.recuperarContacto(Integer.parseInt(st.nextToken())));
		}
		return contactos;
	}

	private List<Mensaje> obtenerMensajesDesdeCodigos(String codigos) {
		List<Mensaje> mensajes = new ArrayList<>();
		StringTokenizer st = new StringTokenizer(codigos);
		AdaptadorMensajeTDS adaptador = AdaptadorMensajeTDS.getInstancia();
		while (st.hasMoreTokens()) {
			mensajes.add(adaptador.recuperarMensaje(Integer.parseInt(st.nextToken())));
		}
		return mensajes;
	}
}
