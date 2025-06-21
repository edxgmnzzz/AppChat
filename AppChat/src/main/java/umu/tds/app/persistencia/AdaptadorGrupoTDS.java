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

/**
 * Adaptador DAO para la entidad {@link Grupo} usando el servicio de
 * persistencia TDS. Permite registrar, modificar, borrar y recuperar grupos,
 * así como convertir a/desde los formatos de persistencia.
 */
public class AdaptadorGrupoTDS implements GrupoDAO {

    /** Nombre de la entidad en la base de datos TDS. */
    private static final String ENTIDAD_GRUPO = "grupo";

    private final ServicioPersistencia servPersistencia;
    private static AdaptadorGrupoTDS unicaInstancia = null;

    /** Constructor privado para el patrón Singleton. */
    private AdaptadorGrupoTDS() {
        servPersistencia = FactoriaServicioPersistencia
                               .getInstance()
                               .getServicioPersistencia();
    }

    /**
     * Devuelve la instancia única del adaptador.
     *
     * @return {@code AdaptadorGrupoTDS} singleton.
     */
    public static AdaptadorGrupoTDS getInstancia() {
        if (unicaInstancia == null) {
            unicaInstancia = new AdaptadorGrupoTDS();
        }
        return unicaInstancia;
    }

    // ─────────────────────── Registro ───────────────────────

    /**
     * Registra un nuevo grupo en la base de datos si aún no existe.
     *
     * @param grupo Objeto {@link Grupo} a registrar.
     */
    @Override
    public void registrarGrupo(Grupo grupo) {
        if (grupo == null) return;

        if (grupo.getCodigo() > 0 &&
            servPersistencia.recuperarEntidad(grupo.getCodigo()) != null) {
            // Ya estaba registrado; no hacemos nada.
            return;
        }

        Entidad eGrupo = new Entidad();
        eGrupo.setNombre(ENTIDAD_GRUPO);

        List<Propiedad> props = new ArrayList<>();
        props.add(new Propiedad("nombre", grupo.getNombre()));
        props.add(new Propiedad("integrantes",
                                codigosContactos(grupo.getParticipantes())));
        props.add(new Propiedad("mensajes",
                                codigosMensajes(grupo.getMensajesEnviados())));
        props.add(new Propiedad("urlFoto",
                                grupo.getUrlFoto() != null ? grupo.getUrlFoto()
                                                           : ""));
        props.add(new Propiedad("admin",
                                String.valueOf(grupo.getAdmin().getId())));
        eGrupo.setPropiedades(props);

        eGrupo = servPersistencia.registrarEntidad(eGrupo);
        grupo.setCodigo(eGrupo.getId());
    }

    // ─────────────────────── Borrado ───────────────────────

    /** {@inheritDoc} */
    @Override
    public void borrarGrupo(Grupo grupo) {
        if (grupo == null || grupo.getCodigo() <= 0) return;
        Entidad eGrupo = servPersistencia.recuperarEntidad(grupo.getCodigo());
        servPersistencia.borrarEntidad(eGrupo);
    }

    // ─────────────────────── Modificación ───────────────────────

    /** {@inheritDoc} */
    @Override
    public void modificarGrupo(Grupo grupo) {
        Entidad eGrupo = servPersistencia.recuperarEntidad(grupo.getCodigo());
        if (eGrupo == null) return;

        // Sobre-escribimos las propiedades relevantes
        servPersistencia.eliminarPropiedadEntidad(eGrupo, "nombre");
        servPersistencia.anadirPropiedadEntidad(eGrupo, "nombre",
                                                grupo.getNombre());

        servPersistencia.eliminarPropiedadEntidad(eGrupo, "integrantes");
        servPersistencia.anadirPropiedadEntidad(eGrupo, "integrantes",
                                codigosContactos(grupo.getParticipantes()));

        servPersistencia.eliminarPropiedadEntidad(eGrupo, "mensajes");
        servPersistencia.anadirPropiedadEntidad(eGrupo, "mensajes",
                                codigosMensajes(grupo.getMensajesEnviados()));

        servPersistencia.eliminarPropiedadEntidad(eGrupo, "urlFoto");
        servPersistencia.anadirPropiedadEntidad(eGrupo, "urlFoto",
                                grupo.getUrlFoto() != null ? grupo.getUrlFoto()
                                                           : "");
    }

    // ─────────────────────── Recuperación ───────────────────────

    /**
     * Recupera un grupo desde su código de entidad.
     *
     * @param codigo ID del grupo.
     * @return Instancia reconstruida o {@code null} si no existe.
     */
    @Override
    public Grupo recuperarGrupo(int codigo) {
        Entidad eGrupo = servPersistencia.recuperarEntidad(codigo);
        if (eGrupo == null) return null;

        String nombre      = servPersistencia.recuperarPropiedadEntidad(eGrupo, "nombre");
        String codIntegr   = servPersistencia.recuperarPropiedadEntidad(eGrupo, "integrantes");
        String codMensajes = servPersistencia.recuperarPropiedadEntidad(eGrupo, "mensajes");
        String urlFoto     = servPersistencia.recuperarPropiedadEntidad(eGrupo, "urlFoto");
        String adminIdStr  = servPersistencia.recuperarPropiedadEntidad(eGrupo, "admin");

        List<ContactoIndividual> integrantes = contactosDesdeCodigos(codIntegr);
        List<Mensaje> mensajes                = mensajesDesdeCodigos(codMensajes);

        Usuario admin = null;
        try {
            admin = AdaptadorUsuarioTDS.getInstancia()
                                       .recuperarUsuario(Integer.parseInt(adminIdStr));
        } catch (NumberFormatException ignored) { }

        ImageIcon foto = new ImageIcon();
        if (urlFoto != null && !urlFoto.isBlank()) {
            try {
                BufferedImage img = javax.imageio.ImageIO
                                      .read(new java.net.URI(urlFoto).toURL());
                foto = new ImageIcon(img);
            } catch (Exception ignored) { }
        }

        Grupo grupo = new Grupo(nombre, integrantes, admin, foto);
        grupo.setCodigo(codigo);
        grupo.setUrlFoto(urlFoto);
        mensajes.forEach(grupo::sendMensaje);
        return grupo;
    }

    /**
     * Recupera todos los grupos registrados.
     *
     * @return Lista de grupos.
     */
    @Override
    public List<Grupo> recuperarTodosGrupos() {
        return servPersistencia.recuperarEntidades(ENTIDAD_GRUPO).stream()
                .map(e -> recuperarGrupo(e.getId()))
                .collect(Collectors.toList());
    }

    // ─────────────────────── Utilidades ───────────────────────

    private String codigosContactos(List<ContactoIndividual> contactos) {
        return contactos.stream()
                        .map(c -> String.valueOf(c.getCodigo()))
                        .collect(Collectors.joining(" "));
    }

    private String codigosMensajes(List<Mensaje> mensajes) {
        return mensajes.stream()
                       .map(m -> String.valueOf(m.getCodigo()))
                       .collect(Collectors.joining(" "));
    }

    private List<ContactoIndividual> contactosDesdeCodigos(String codigos) {
        List<ContactoIndividual> lista = new ArrayList<>();
        StringTokenizer st = new StringTokenizer(codigos);
        AdaptadorContactoIndividualTDS adapt =
                AdaptadorContactoIndividualTDS.getInstancia();
        while (st.hasMoreTokens()) {
            lista.add(adapt.recuperarContacto(Integer.parseInt(st.nextToken())));
        }
        return lista;
    }

    private List<Mensaje> mensajesDesdeCodigos(String codigos) {
        List<Mensaje> lista = new ArrayList<>();
        StringTokenizer st  = new StringTokenizer(codigos);
        AdaptadorMensajeTDS adapt = AdaptadorMensajeTDS.getInstancia();
        while (st.hasMoreTokens()) {
            lista.add(adapt.recuperarMensaje(Integer.parseInt(st.nextToken())));
        }
        return lista;
    }
}
