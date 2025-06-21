/**
 * Adaptador de persistencia para la entidad Usuario usando el servicio TDS.
 * Permite registrar, modificar, recuperar y borrar usuarios, así como
 * gestionar sus relaciones con otros contactos.
 */
package umu.tds.app.persistencia;

import java.awt.image.BufferedImage;
import java.net.URI;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import beans.Entidad;
import beans.Propiedad;
import tds.driver.FactoriaServicioPersistencia;
import tds.driver.ServicioPersistencia;
import umu.tds.app.AppChat.Contacto;
import umu.tds.app.AppChat.Usuario;

public class AdaptadorUsuarioTDS implements UsuarioDAO {

    private static ServicioPersistencia sp;
    private static AdaptadorUsuarioTDS unicaInstancia = null;
    private static final String ENTIDAD_USUARIO = "usuario";
    private static final Logger LOGGER = Logger.getLogger(AdaptadorUsuarioTDS.class.getName());

    private AdaptadorUsuarioTDS() {
        sp = FactoriaServicioPersistencia.getInstance().getServicioPersistencia();
    }

    /**
     * Obtiene la instancia singleton del adaptador.
     * @return instancia de AdaptadorUsuarioTDS
     */
    public static AdaptadorUsuarioTDS getInstancia() {
        if (unicaInstancia == null) {
            unicaInstancia = new AdaptadorUsuarioTDS();
        }
        return unicaInstancia;
    }

    private String codigosContactos(List<Contacto> contactos) {
        if (contactos == null || contactos.isEmpty()) return "";
        return contactos.stream()
                .map(c -> String.valueOf(c.getCodigo()))
                .collect(Collectors.joining(" "));
    }

    private List<Integer> idsContactosDesdeString(String codigos) {
        List<Integer> ids = new ArrayList<>();
        if (codigos == null || codigos.isBlank()) return ids;
        StringTokenizer st = new StringTokenizer(codigos);
        while (st.hasMoreTokens()) {
            try {
                ids.add(Integer.parseInt(st.nextToken()));
            } catch (NumberFormatException e) {
                LOGGER.warning("ID de contacto mal formado: " + e.getMessage());
            }
        }
        return ids;
    }

    /**
     * Registra un nuevo usuario en la base de datos.
     * Si el usuario ya tiene ID, se realiza una modificación en su lugar.
     * @param usuario el usuario a registrar
     */
    @Override
    public void registrarUsuario(Usuario usuario) {
        if (usuario == null) return;
        if (usuario.getId() > 0) {
            modificarUsuario(usuario);
            return;
        }

        Entidad eUsuario = new Entidad();
        eUsuario.setNombre(ENTIDAD_USUARIO);

        List<Propiedad> propiedades = new ArrayList<>();
        propiedades.add(new Propiedad("nombre", usuario.getNombre()));
        propiedades.add(new Propiedad("telefono", usuario.getTelefono()));
        propiedades.add(new Propiedad("password", usuario.getPassword()));
        propiedades.add(new Propiedad("email", usuario.getEmail()));
        propiedades.add(new Propiedad("saludo", usuario.getSaludo()));
        propiedades.add(new Propiedad("premium", String.valueOf(usuario.isPremium())));
        propiedades.add(new Propiedad("urlFoto", usuario.getUrlFoto() != null ? usuario.getUrlFoto() : ""));
        propiedades.add(new Propiedad("contactos", codigosContactos(usuario.getContactosInternal())));

        eUsuario.setPropiedades(propiedades);

        try {
            eUsuario = sp.registrarEntidad(eUsuario);
            usuario.setId(eUsuario.getId());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "[registrarUsuario] Error al registrar entidad", e);
        }
    }

    /**
     * Modifica un usuario existente en la base de datos.
     * @param usuario el usuario a modificar
     */
    @Override
    public void modificarUsuario(Usuario usuario) {
        if (usuario == null || usuario.getId() <= 0) return;

        Entidad e = sp.recuperarEntidad(usuario.getId());
        if (e == null) return;

        List<Propiedad> nuevasPropiedades = new ArrayList<>();
        nuevasPropiedades.add(new Propiedad("nombre", usuario.getNombre()));
        nuevasPropiedades.add(new Propiedad("telefono", usuario.getTelefono()));
        nuevasPropiedades.add(new Propiedad("password", usuario.getPassword()));
        nuevasPropiedades.add(new Propiedad("email", usuario.getEmail()));
        nuevasPropiedades.add(new Propiedad("saludo", usuario.getSaludo()));
        nuevasPropiedades.add(new Propiedad("premium", String.valueOf(usuario.isPremium())));
        nuevasPropiedades.add(new Propiedad("urlFoto", usuario.getUrlFoto() != null ? usuario.getUrlFoto() : ""));
        nuevasPropiedades.add(new Propiedad("contactos", codigosContactos(usuario.getContactosInternal())));

        e.setPropiedades(nuevasPropiedades);
        sp.modificarEntidad(e);
    }

    /**
     * Recupera un usuario de la base de datos por su ID.
     * @param codigo ID del usuario
     * @return objeto Usuario o null si no existe
     */
    @Override
    public Usuario recuperarUsuario(int codigo) {
        Entidad e = sp.recuperarEntidad(codigo);
        if (e == null) return null;

        String nombre = sp.recuperarPropiedadEntidad(e, "nombre");
        String telefono = sp.recuperarPropiedadEntidad(e, "telefono");
        String password = sp.recuperarPropiedadEntidad(e, "password");
        String email = sp.recuperarPropiedadEntidad(e, "email");
        String saludo = sp.recuperarPropiedadEntidad(e, "saludo");
        boolean premium = Boolean.parseBoolean(sp.recuperarPropiedadEntidad(e, "premium"));
        String urlFoto = sp.recuperarPropiedadEntidad(e, "urlFoto");

        List<Integer> contactosID = idsContactosDesdeString(sp.recuperarPropiedadEntidad(e, "contactos"));

        ImageIcon icono = new ImageIcon();
        if (urlFoto != null && !urlFoto.isBlank()) {
            try {
                BufferedImage img = ImageIO.read(new URI(urlFoto).toURL());
                icono = new ImageIcon(img);
            } catch (Exception ex) {
                LOGGER.warning("Error al cargar imagen desde URL: " + urlFoto + " -> " + ex.getMessage());
            }
        }

        Usuario u = new Usuario(telefono, nombre, password, email, saludo, icono, premium);
        u.setId(codigo);
        u.setUrlFoto(urlFoto);
        u.setContactosID(contactosID);

        return u;
    }

    /**
     * Recupera todos los usuarios almacenados en la base de datos.
     * @return lista de usuarios
     */
    @Override
    public List<Usuario> recuperarTodosUsuarios() {
        List<Entidad> entidades = sp.recuperarEntidades(ENTIDAD_USUARIO);
        return entidades.stream()
                .map(e -> recuperarUsuario(e.getId()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Elimina un usuario de la base de datos.
     * @param usuario el usuario a borrar
     */
    @Override
    public void borrarUsuario(Usuario usuario) {
        if (usuario == null || usuario.getId() <= 0) return;
        Entidad e = sp.recuperarEntidad(usuario.getId());
        if (e != null) sp.borrarEntidad(e);
    }

    /**
     * Recupera un usuario a partir de su número de teléfono.
     * @param telefono número de teléfono del usuario
     * @return objeto Usuario o null si no se encuentra
     */
    public Usuario recuperarUsuarioPorTelefono(String telefono) {
        List<Entidad> entidades = sp.recuperarEntidades(ENTIDAD_USUARIO);
        for (Entidad e : entidades) {
            String tel = sp.recuperarPropiedadEntidad(e, "telefono");
            if (telefono.equals(tel)) {
                return recuperarUsuario(e.getId());
            }
        }
        LOGGER.warning("[recuperarUsuarioPorTelefono] No se encontró usuario con teléfono: " + telefono);
        return null;
    }
}
