package umu.tds.app.persistencia;

import java.awt.image.BufferedImage;
import java.util.*;
import java.util.stream.Collectors;

import javax.swing.ImageIcon;

import beans.Entidad;
import beans.Propiedad;
import tds.driver.FactoriaServicioPersistencia;
import tds.driver.ServicioPersistencia;
import umu.tds.app.AppChat.ContactoIndividual;
import umu.tds.app.AppChat.Grupo;
import umu.tds.app.AppChat.Mensaje;
import umu.tds.app.AppChat.Usuario;

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
		if (grupo == null) return;

		if (grupo.getCodigo() > 0 && servPersistencia.recuperarEntidad(grupo.getCodigo()) != null) {
		    System.out.println("[GRUPO-DEBUG] ⚠️ Grupo con ID " + grupo.getCodigo() + " ya registrado, saltando registro.");
		    return;
		}


	    Entidad eGrupo = new Entidad();
	    eGrupo.setNombre(ENTIDAD_GRUPO);

	    List<Propiedad> propiedades = new ArrayList<>();
	    propiedades.add(new Propiedad("nombre", grupo.getNombre()));
	    propiedades.add(new Propiedad("integrantes", obtenerCodigosContactos(grupo.getParticipantes())));
	    propiedades.add(new Propiedad("mensajes", obtenerCodigosMensajes(grupo.getMensajesEnviados())));
	    propiedades.add(new Propiedad("urlFoto", grupo.getUrlFoto() != null ? grupo.getUrlFoto() : ""));
	    propiedades.add(new Propiedad("admin", String.valueOf(grupo.getAdmin().getId()))); // <-- AÑADIDO

	    eGrupo.setPropiedades(propiedades);
	    eGrupo = servPersistencia.registrarEntidad(eGrupo);
	    grupo.setCodigo(eGrupo.getId());

	    System.out.println("[GRUPO-DEBUG] ✅ Grupo '" + grupo.getNombre() + "' registrado con ID: " + grupo.getCodigo() + " y admin ID: " + grupo.getAdmin().getId());
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

	    servPersistencia.eliminarPropiedadEntidad(eGrupo, "urlFoto");
	    servPersistencia.anadirPropiedadEntidad(eGrupo, "urlFoto", grupo.getUrlFoto() != null ? grupo.getUrlFoto() : "");
	}


	@Override
	public Grupo recuperarGrupo(int codigo) {
	    System.out.println("[GRUPO-DEBUG] 🔁 Recuperando grupo con ID: " + codigo);

	    Entidad eGrupo = servPersistencia.recuperarEntidad(codigo);
	    if (eGrupo == null) {
	        System.out.println("[GRUPO-DEBUG] ❌ No se encontró la entidad del grupo con ID: " + codigo);
	        return null;
	    }

	    String nombre = servPersistencia.recuperarPropiedadEntidad(eGrupo, "nombre");
	    String codIntegrantes = servPersistencia.recuperarPropiedadEntidad(eGrupo, "integrantes");
	    String codMensajes = servPersistencia.recuperarPropiedadEntidad(eGrupo, "mensajes");
	    String urlFoto = servPersistencia.recuperarPropiedadEntidad(eGrupo, "urlFoto");
	    String adminIdStr = servPersistencia.recuperarPropiedadEntidad(eGrupo, "admin");

	    List<ContactoIndividual> integrantes = obtenerContactosDesdeCodigos(codIntegrantes);
	    List<Mensaje> mensajes = obtenerMensajesDesdeCodigos(codMensajes);

	    Usuario admin = null;
	    try {
	        int idAdmin = Integer.parseInt(adminIdStr);
	        admin = AdaptadorUsuarioTDS.getInstancia().recuperarUsuario(idAdmin);
	        System.out.println("[GRUPO-DEBUG] 👤 Admin del grupo '" + nombre + "' recuperado: ID " + idAdmin);
	    } catch (Exception e) {
	        System.err.println("[GRUPO-DEBUG] ❌ Error al recuperar admin con ID: " + adminIdStr);
	        e.printStackTrace();
	    }

	    ImageIcon foto = new ImageIcon();
	    if (urlFoto != null && !urlFoto.isBlank()) {
	        try {
	            BufferedImage image = javax.imageio.ImageIO.read(new java.net.URI(urlFoto).toURL());
	            foto = new ImageIcon(image);
	            System.out.println("[GRUPO-DEBUG] 🖼️ Imagen del grupo cargada desde URL: " + urlFoto);
	        } catch (Exception e) {
	            System.err.println("[GRUPO-DEBUG] ⚠️ Error al cargar imagen desde URL: " + urlFoto);
	            e.printStackTrace();
	        }
	    }

	    Grupo grupo = new Grupo(nombre, integrantes, admin, foto);
	    grupo.setCodigo(codigo);
	    grupo.setUrlFoto(urlFoto);
	    mensajes.forEach(grupo::sendMensaje);

	    System.out.println("[GRUPO-DEBUG] ✅ Grupo '" + nombre + "' reconstruido con " + integrantes.size() + " integrantes y " + mensajes.size() + " mensajes.");
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
