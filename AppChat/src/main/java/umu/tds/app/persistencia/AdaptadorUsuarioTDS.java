package umu.tds.app.persistencia;

import java.awt.image.BufferedImage;
import java.net.URI;
import java.net.URL;
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

    public static AdaptadorUsuarioTDS getInstancia() {
        if (unicaInstancia == null) {
            unicaInstancia = new AdaptadorUsuarioTDS();
        }
        return unicaInstancia;
    }
    
    // --- Métodos Auxiliares para manejar la lista de contactos ---
    
    private String codigosContactos(List<Contacto> contactos) {
        if (contactos == null || contactos.isEmpty()) {
            return "";
        }
        return contactos.stream()
                        .map(c -> String.valueOf(c.getCodigo()))
                        .collect(Collectors.joining(" "));
    }

    private List<Integer> idsContactosDesdeString(String codigos) {
        if (codigos == null || codigos.isBlank()) {
            return new ArrayList<>();
        }
        List<Integer> ids = new ArrayList<>();
        StringTokenizer st = new StringTokenizer(codigos);
        while (st.hasMoreTokens()) {
            try {
                ids.add(Integer.parseInt(st.nextToken()));
            } catch (NumberFormatException e) {
                LOGGER.warning("ID de contacto mal formado en la lista: " + e.getMessage());
            }
        }
        return ids;
    }

    // --- Métodos DAO Modificados ---

 // En la clase AdaptadorUsuarioTDS

    @Override
    public void registrarUsuario(Usuario usuario) {
        if (usuario == null) return;
        if (usuario.getId() > 0) {
            modificarUsuario(usuario);
            return;
        }

        ////LOGGER.Info("[registrarUsuario] Registrando nuevo usuario: " + usuario.getNombre());
        Entidad eUsuario = new Entidad();
        eUsuario.setNombre(ENTIDAD_USUARIO);
        
        // Creamos la lista de propiedades desde cero.
        List<Propiedad> propiedades = new ArrayList<>();
        propiedades.add(new Propiedad("nombre", usuario.getNombre()));
        propiedades.add(new Propiedad("telefono", usuario.getTelefono()));
        propiedades.add(new Propiedad("password", usuario.getPassword()));
        propiedades.add(new Propiedad("email", usuario.getEmail()));
        propiedades.add(new Propiedad("saludo", usuario.getSaludo()));
        propiedades.add(new Propiedad("premium", String.valueOf(usuario.isPremium())));
        String urlParaGuardar = usuario.getUrlFoto() != null ? usuario.getUrlFoto() : "";
        // LOG 2: Verificamos qué URL se está a punto de guardar en la BD
        LOGGER.info("[FOTO-DEBUG] REGISTRANDO usuario " + usuario.getNombre() + " con urlFoto: '" + urlParaGuardar + "'");
        propiedades.add(new Propiedad("urlFoto", urlParaGuardar));
        //propiedades.add(new Propiedad("urlFoto", usuario.getUrlFoto() != null ? usuario.getUrlFoto() : ""));
        // Aquí usamos getContactosInternal para acceder a la lista mutable.
        propiedades.add(new Propiedad("contactos", codigosContactos(usuario.getContactosInternal())));

        eUsuario.setPropiedades(propiedades);

        try {
            eUsuario = sp.registrarEntidad(eUsuario);
            usuario.setId(eUsuario.getId());
            //////LOGGER.Info("[registrarUsuario] Usuario registrado con ID: " + usuario.getId());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "[registrarUsuario] Error al registrar entidad.", e);
        }
    }
    
   @Override
public void modificarUsuario(Usuario usuario) {
    if (usuario == null || usuario.getId() <= 0) return;

    Entidad e = sp.recuperarEntidad(usuario.getId());
    if (e == null) return;

    // Estrategia "Nuclear": Reemplazar toda la lista de propiedades.
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
    ////LOGGER.Info("[modificarUsuario] Usuario modificado con ID: " + usuario.getId());
}

// En la clase AdaptadorUsuarioTDS

    @Override
    public Usuario recuperarUsuario(int codigo) {
        Entidad e = sp.recuperarEntidad(codigo);
        if (e == null) {
            LOGGER.warning("[recuperarUsuario] No se encontró Entidad para ID: " + codigo);
            return null;
        }

        String nombre = sp.recuperarPropiedadEntidad(e, "nombre");
        String telefono = sp.recuperarPropiedadEntidad(e, "telefono");
        String password = sp.recuperarPropiedadEntidad(e, "password");
        String email = sp.recuperarPropiedadEntidad(e, "email");
        String saludo = sp.recuperarPropiedadEntidad(e, "saludo");
        boolean premium = Boolean.parseBoolean(sp.recuperarPropiedadEntidad(e, "premium"));
        String urlFoto = sp.recuperarPropiedadEntidad(e, "urlFoto");
        LOGGER.info("[FOTO-DEBUG] RECUPERANDO usuario con ID " + codigo + ". URL leída de BD: '" + urlFoto + "'");

        String contactosStr = sp.recuperarPropiedadEntidad(e, "contactos");
        List<Integer> contactosID = idsContactosDesdeString(contactosStr);

        ImageIcon icono = new ImageIcon(); // por defecto

        if (urlFoto != null && !urlFoto.isBlank()) {
            try {
            	BufferedImage img = ImageIO.read(new URI(urlFoto).toURL());
                icono = new ImageIcon(img);
                LOGGER.info("[FOTO-DEBUG] Imagen cargada correctamente desde URL: " + urlFoto);
            } catch (Exception ex) {
                LOGGER.warning("[FOTO-DEBUG] Error al cargar imagen desde URL: " + urlFoto + " -> " + ex.getMessage());
            }
        }

        Usuario u = new Usuario(telefono, nombre, password, email, saludo, icono, premium);
        u.setId(codigo);
        u.setUrlFoto(urlFoto);
        u.setContactosID(contactosID);

        return u;
    }

    
    @Override
    public List<Usuario> recuperarTodosUsuarios() {
        ////LOGGER.Info("[recuperarTodosUsuarios] Solicitando todos los usuarios...");
        List<Entidad> entidades = sp.recuperarEntidades(ENTIDAD_USUARIO);
        
        return entidades.stream()
                        .map(e -> recuperarUsuario(e.getId()))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
    }
    
    @Override
    public void borrarUsuario(Usuario usuario) {
        if (usuario == null || usuario.getId() <= 0) return;
        Entidad e = sp.recuperarEntidad(usuario.getId());
        if (e != null) {
            sp.borrarEntidad(e);
        }
    }
    
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