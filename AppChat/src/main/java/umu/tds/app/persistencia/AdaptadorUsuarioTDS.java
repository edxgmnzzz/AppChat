package umu.tds.app.persistencia;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.swing.ImageIcon;

import beans.Entidad;
import beans.Propiedad;
import tds.driver.FactoriaServicioPersistencia;
import tds.driver.ServicioPersistencia;
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
        if (unicaInstancia == null)
            unicaInstancia = new AdaptadorUsuarioTDS();
        return unicaInstancia;
    }

    @Override
    public void registrarUsuario(Usuario usuario) {
        if (usuario == null) {
            LOGGER.warning("Intento de registrar usuario nulo");
            return;
        }

        if (usuario.getId() > 0) {
            LOGGER.info("Usuario ya persistido (ID=" + usuario.getId() + "), no se registra de nuevo.");
            return;
        }

        if (usuario.getTelefono() == null || usuario.getTelefono().isBlank()) {
            LOGGER.severe("Usuario sin teléfono. Registro cancelado: " + usuario.getName());
            return;
        }

        LOGGER.info("Registrando usuario: " + usuario.getName() + ", teléfono: " + usuario.getTelefono());

        Entidad eUsuario = new Entidad();
        eUsuario.setNombre(ENTIDAD_USUARIO);
        eUsuario.setPropiedades(List.of(
            new Propiedad("nombre", usuario.getName()),
            new Propiedad("telefono", usuario.getTelefono()),
            new Propiedad("password", usuario.getPassword()),
            new Propiedad("email", usuario.getEmail() != null ? usuario.getEmail() : ""),
            new Propiedad("saludo", usuario.getSaludo() != null ? usuario.getSaludo() : ""),
            new Propiedad("premium", String.valueOf(usuario.isPremium())),
            new Propiedad("foto", usuario.getFoto().getDescription() != null ? usuario.getFoto().getDescription() : ""),
            new Propiedad("urlFoto", usuario.getUrlFoto() != null ? usuario.getUrlFoto() : "")
        ));

        eUsuario = sp.registrarEntidad(eUsuario);
        usuario.setId(eUsuario.getId());
        LOGGER.info("Usuario registrado con ID: " + usuario.getId());
    }

    @Override
    public void borrarUsuario(Usuario usuario) {
        LOGGER.info("Borrando usuario con ID: " + usuario.getId());
        Entidad e = sp.recuperarEntidad(usuario.getId());
        sp.borrarEntidad(e);
    }

    @Override
    public void modificarUsuario(Usuario usuario) {
        LOGGER.info("Modificando usuario: " + usuario.getTelefono());

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

        sp.eliminarPropiedadEntidad(e, "urlFoto");
        sp.anadirPropiedadEntidad(e, "urlFoto", usuario.getUrlFoto() != null ? usuario.getUrlFoto() : "");

        LOGGER.info("Usuario modificado correctamente: " + usuario.getTelefono());
    }

    @Override
    public Usuario recuperarUsuario(int codigo) {
        Entidad e = sp.recuperarEntidad(codigo);
        if (e == null) {
            LOGGER.warning("Entidad de usuario no encontrada para ID: " + codigo);
            return null;
        }

        String nombre = sp.recuperarPropiedadEntidad(e, "nombre");
        String telefono = sp.recuperarPropiedadEntidad(e, "telefono");
        String password = sp.recuperarPropiedadEntidad(e, "password");
        String email = sp.recuperarPropiedadEntidad(e, "email");
        String saludo = sp.recuperarPropiedadEntidad(e, "saludo");
        boolean premium = Boolean.parseBoolean(sp.recuperarPropiedadEntidad(e, "premium"));
        String rutaFoto = sp.recuperarPropiedadEntidad(e, "foto");

        if (telefono == null || telefono.isBlank()) {
            LOGGER.severe("Usuario recuperado con ID " + codigo + " tiene teléfono NULL o vacío");
            return null;
        }

        ImageIcon icono = new ImageIcon();
        if (rutaFoto != null && !rutaFoto.isEmpty()) {
            icono = new ImageIcon(rutaFoto);
            icono.setDescription(rutaFoto);
        }

        Usuario u = new Usuario(telefono, nombre, password, email, saludo, icono, premium);
        u.setId(codigo);

        String urlFoto = sp.recuperarPropiedadEntidad(e, "urlFoto");
        u.setUrlFoto(urlFoto);

        LOGGER.info("Usuario recuperado: " + nombre + " (" + telefono + ")");
        return u;
    }

    @Override
    public List<Usuario> recuperarTodosUsuarios() {
        LOGGER.info("Recuperando todos los usuarios...");
        return sp.recuperarEntidades(ENTIDAD_USUARIO).stream()
            .map(e -> recuperarUsuario(e.getId()))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }
}
