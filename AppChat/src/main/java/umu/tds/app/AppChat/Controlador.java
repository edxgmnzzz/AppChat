package umu.tds.app.AppChat;

import java.awt.Image;
import java.io.FileOutputStream;
import java.io.IOException;
import java.awt.image.BufferedImage;
import java.net.URI;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import umu.tds.app.persistencia.*;

/**
 * Controlador principal de la aplicación AppChat.
 * Implementa el patrón Singleton para garantizar una única instancia global.
 * Actúa como intermediario entre la vista (GUI) y el modelo (clases de dominio y persistencia),
 * centralizando toda la lógica de negocio y el flujo de datos de la aplicación.
 */
public class Controlador {
    private static final Controlador instancia;
    private static final int PROFILE_IMAGE_SIZE = 100;

    private Usuario usuarioActual;
    private Contacto contactoActual;
    private Map<String, Usuario> usuariosRegistrados;
    private AdaptadorUsuarioTDS usuarioDAO;
    private AdaptadorContactoIndividualTDS contactoDAO;
    private AdaptadorGrupoTDS grupoDAO;
    private AdaptadorMensajeTDS mensajeDAO;
    private AdaptadorStatusTDS statusDAO;
    private List<ObserverChats> observersChats;
    private List<ObserverContactos> observersContactos;

    static {
        try {
            instancia = new Controlador();
        } catch (Exception e) {
            throw new ExceptionInInitializerError("Error al inicializar Controlador: " + e.getMessage());
        }
    }

    /**
     * Constructor privado para implementar el patrón Singleton.
     * Llama al método de inicialización.
     */
    private Controlador() {
        initialize();
    }

    /**
     * Devuelve la única instancia del Controlador.
     * @return La instancia Singleton del Controlador.
     */
    public static Controlador getInstancia() {
        return instancia;
    }

    // ########################################################################
    // SECCIÓN 1: INICIALIZACIÓN Y CARGA DE DATOS
    // ########################################################################

    /**
     * Orquesta el proceso de inicialización del controlador.
     * Inicializa los DAOs, las colecciones, realiza una simulación de datos si la base de datos está vacía,
     * y carga todos los datos desde la capa de persistencia.
     */
    private void initialize() {
        initializeDaosAndCollections();
        realizarSimulacionInicialSiEsNecesario();
        cargarDatosDesdePersistencia();
        notifyObserversContactoActual(this.contactoActual);
    }

    /**
     * Inicializa todas las instancias de los adaptadores DAO y las colecciones de datos en memoria.
     */
    private void initializeDaosAndCollections() {
        usuarioDAO = AdaptadorUsuarioTDS.getInstancia();
        contactoDAO = AdaptadorContactoIndividualTDS.getInstancia();
        grupoDAO = AdaptadorGrupoTDS.getInstancia();
        mensajeDAO = AdaptadorMensajeTDS.getInstancia();
        statusDAO = AdaptadorStatusTDS.getInstancia();
        usuariosRegistrados = new HashMap<>();
        observersChats = new ArrayList<>();
        observersContactos = new ArrayList<>();
    }

    /**
     * Comprueba si la base de datos está vacía. Si es así, crea y persiste un conjunto
     * inicial de usuarios para permitir el funcionamiento de la aplicación desde el primer uso.
     */
    private void realizarSimulacionInicialSiEsNecesario() {
        if (!usuarioDAO.recuperarTodosUsuarios().isEmpty()) {
            return;
        }
        try {
            String path = "https://widget-assets.geckochat.io/69d33e2bd0ca2799b2c6a3a3870537a9.png";
            BufferedImage image = ImageIO.read(new URI(path.trim()).toURL());
            ImageIcon foto = new ImageIcon(image);
            Usuario florentino = new Usuario("600111222", "Florentino Pérez", "pass1", "f@p.com", "Hala Madrid", foto, false);
            florentino.setUrlFoto(path);
            usuarioDAO.registrarUsuario(florentino);
            
            Usuario laporta = new Usuario("600333444", "Joan Laporta", "pass2", "j@l.com", "Visca Barça", foto, true);
            usuarioDAO.registrarUsuario(laporta);
            
            Usuario cerezo = new Usuario("600555666", "Enrique Cerezo", "pass3", "e@c.com", "Aupa Atleti", null, true);
            usuarioDAO.registrarUsuario(cerezo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Carga todos los datos desde la persistencia y reconstruye el estado completo de la aplicación.
     * Selecciona el chat más reciente como chat activo al iniciar.
     */
    private void cargarDatosDesdePersistencia() {
        cargarUsuarios();
        refrescarEstadoDesdePersistencia();
        
        contactoActual = obtenerContactos().stream()
            .filter(c -> c != null && !c.getMensajes().isEmpty())
            .max(Comparator.comparing(this::getUltimoMensajeTiempo))
            .orElse(null);
    }
    
    /**
     * Proceso principal para reconstruir las relaciones entre usuarios, contactos y mensajes
     * a partir de los datos recuperados de la base de datos.
     */
    private void refrescarEstadoDesdePersistencia() {
        for (Usuario u : usuariosRegistrados.values()) {
            if (u.getContactos() != null) {
                u.getContactos().forEach(c -> {
                    if (c != null && c.getMensajes() != null) c.getMensajes().clear();
                });
            }
            u.setContactos(new ArrayList<>());
        }
        Map<Integer, Contacto> todosLosContactosDelSistema = cargarContactosYGrupos();
        vincularContactosAUsuarios(todosLosContactosDelSistema);
        vincularMensajesAContactos(todosLosContactosDelSistema);
    }

    /**
     * Carga todos los usuarios de la base de datos a la colección en memoria `usuariosRegistrados`.
     * Intenta cargar la foto de perfil de cada usuario desde su URL almacenada.
     */
    private void cargarUsuarios() {
        for (Usuario u : usuarioDAO.recuperarTodosUsuarios()) {
        	try {
				String urlFoto = u.getUrlFoto();
				if (urlFoto != null && !urlFoto.isBlank() && !"null".equalsIgnoreCase(urlFoto)) {
					u.setFoto(loadImageFromUrl(urlFoto));
				}
			} catch (Exception e) {
                // Silently fail if image loading fails, user will have default icon
			}
            usuariosRegistrados.put(u.getTelefono(), u);
        }
    }

    /**
     * Carga todos los contactos individuales y grupos de la base de datos.
     * @return Un mapa que asocia el código de cada contacto/grupo a su objeto correspondiente.
     */
    private Map<Integer, Contacto> cargarContactosYGrupos() {
        Map<Integer, Contacto> mapaContactos = new HashMap<>();
        List<ContactoIndividual> individuales = contactoDAO.recuperarTodosContactos();
        List<Grupo> grupos = grupoDAO.recuperarTodosGrupos();
        individuales.forEach(c -> mapaContactos.put(c.getCodigo(), c));
        grupos.forEach(g -> mapaContactos.put(g.getCodigo(), g));
        return mapaContactos;
    }

    /**
     * Asocia a cada usuario su lista de contactos correspondiente, utilizando el mapa de todos
     * los contactos del sistema para resolver las referencias por ID.
     * @param todosLosContactosDelSistema Mapa con todos los contactos y grupos disponibles.
     */
    private void vincularContactosAUsuarios(Map<Integer, Contacto> todosLosContactosDelSistema) {
        for (Usuario usuario : usuariosRegistrados.values()) {
            List<Contacto> listaPersonal = usuario.getContactosID().stream()
                .map(todosLosContactosDelSistema::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
            usuario.setContactos(listaPersonal);
        }
    }
    
    /**
     * Cuenta el número total de mensajes enviados por un usuario específico a través de todos sus chats.
     * @param usuario El usuario cuyos mensajes se van a contar.
     * @return El número total de mensajes enviados.
     */
    public int contarMensajesDelUsuario(Usuario usuario) {
    	return (int) obtenerContactos().stream().flatMap(c -> c.getMensajes().stream())
    			.filter(m -> m.getEmisor().equals(usuario)).count();
    }
    
    /**
     * Asocia cada mensaje recuperado de la base de datos a su chat correspondiente (contacto o grupo).
     * @param todosLosContactosDelSistema Mapa con todos los contactos y grupos disponibles.
     */
    private void vincularMensajesAContactos(Map<Integer, Contacto> todosLosContactosDelSistema) {
        List<Mensaje> mensajes = mensajeDAO.recuperarTodosMensajes();
        for (Mensaje mensaje : mensajes) {
            Usuario emisor = usuariosRegistrados.get(mensaje.getEmisor().getTelefono());
            Contacto contactoDelEmisor = todosLosContactosDelSistema.get(mensaje.getReceptor().getCodigo());

            if (emisor == null || contactoDelEmisor == null) continue;

            if (!contactoDelEmisor.getMensajes().contains(mensaje)) {
                contactoDelEmisor.sendMensaje(mensaje);
            }

            if (contactoDelEmisor instanceof ContactoIndividual ci) {
                Usuario receptorReal = usuariosRegistrados.get(ci.getTelefono());
                if (receptorReal != null) {
                    receptorReal.getContactos().stream()
                        .filter(c -> c instanceof ContactoIndividual)
                        .map(c -> (ContactoIndividual) c)
                        .filter(c -> c.getTelefono().equals(emisor.getTelefono()))
                        .findFirst()
                        .ifPresent(contactoEspejo -> {
                            if (!contactoEspejo.getMensajes().contains(mensaje)) {
                                contactoEspejo.sendMensaje(mensaje);
                            }
                        });
                }
            }
        }
    }
    
    /**
     * Notifica a todos los observers registrados sobre el estado actual de la aplicación.
     * Este método es llamado típicamente después de iniciar sesión para cargar la interfaz
     * con los datos correctos.
     */
    public void notifyObservers() {
        notifyObserversListaContactos(); 
        notifyObserversChatsRecientes();
        notifyObserversContactoActual(contactoActual);
    }
    
    // ########################################################################
    // SECCIÓN 2: LÓGICA DE USUARIO Y SESIÓN
    // ########################################################################
    
    /**
     * Intenta iniciar sesión con un teléfono y contraseña.
     * Si las credenciales son válidas, establece el usuario como activo, carga sus datos
     * y notifica a la interfaz.
     * @param telefono El número de teléfono del usuario.
     * @param password La contraseña del usuario.
     * @return {@code true} si el inicio de sesión es exitoso, {@code false} en caso contrario.
     */
    public boolean iniciarSesion(String telefono, String password) {
        Usuario usuario = usuariosRegistrados.get(telefono);
        if (usuario != null && usuario.getPassword().equals(password)) {
            usuarioActual = usuario;
            refrescarEstadoDesdePersistencia();
            contactoActual = obtenerContactos().stream()
                .filter(c -> !c.getMensajes().isEmpty())
                .max(Comparator.comparing(this::getUltimoMensajeTiempo))
                .orElse(null);
            
            SwingUtilities.invokeLater(this::notifyObservers);
            return true;
        }
        return false;
    }

    /**
     * Cierra la sesión del usuario actual, limpiando los datos de sesión y notificando a la interfaz.
     */
    public void cerrarSesion() {
        usuarioActual = null;
        contactoActual = null;
        PoolDAO.getInstancia().limpiarPool();
        notifyObserversChatsRecientes();
        notifyObserversContactoActual(null);
    }

    /**
     * Registra un nuevo usuario en el sistema.
     * Realiza validaciones de los datos de entrada antes de crear y persistir el usuario.
     * @param nombreReal El nombre completo del usuario.
     * @param nombreUsuario El nombre de usuario (actualmente no utilizado de forma distinta al nombre real).
     * @param password La contraseña del usuario.
     * @param confirmarPassword La confirmación de la contraseña.
     * @param email El correo electrónico del usuario.
     * @param telefono El número de teléfono del usuario (actúa como ID único).
     * @param rutaFoto La URL de la foto de perfil.
     * @param saludo El mensaje de saludo del usuario.
     * @return {@code true} si el registro es exitoso, {@code false} si hay un error de validación.
     */
    public boolean registrarUsuario(String nombreReal, String nombreUsuario, String password, String confirmarPassword, String email, String telefono, String rutaFoto, String saludo) {
    	String error = validateRegistration(nombreReal, nombreUsuario, password, confirmarPassword, email, telefono);
		if (error != null) {
			JOptionPane.showMessageDialog(null, error, "Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		ImageIcon profileIcon = loadImageFromUrl(rutaFoto);
		Usuario nuevoUsuario = new Usuario(telefono, nombreReal, password, email, saludo, profileIcon, false);
		nuevoUsuario.setUrlFoto(rutaFoto);
		usuarioDAO.registrarUsuario(nuevoUsuario);
		usuariosRegistrados.put(telefono, nuevoUsuario);
		return true;
    }

    /**
     * Actualiza los datos del perfil del usuario actualmente logueado.
     * @param nuevoNombre El nuevo nombre para el usuario.
     * @param nuevaPassword La nueva contraseña para el usuario.
     * @param nuevoSaludo El nuevo mensaje de saludo.
     * @param rutaFoto La nueva URL de la foto de perfil.
     * @return {@code true} si la actualización es exitosa, {@code false} en caso contrario.
     */
    public boolean actualizarUsuario(String nuevoNombre, String nuevaPassword, String nuevoSaludo, String rutaFoto) {
    	if (usuarioActual == null || nuevaPassword == null || nuevaPassword.trim().isEmpty())
			return false;
		usuarioActual.setName(nuevoNombre);
		usuarioActual.setPassword(nuevaPassword);
		usuarioActual.setSaludo(nuevoSaludo);
		usuarioActual.setFoto(loadImageFromUrl(rutaFoto));
		usuarioDAO.modificarUsuario(usuarioActual);
		notifyObserversChatsRecientes();
		return true;
    }

    /**
     * Valida los datos de entrada para el registro de un nuevo usuario.
     * @return Una cadena con el mensaje de error si la validación falla, o {@code null} si es exitosa.
     */
    private String validateRegistration(String nombreReal, String nombreUsuario, String password,
			String confirmarPassword, String email, String telefono) {
		if (nombreReal.isEmpty() || nombreUsuario.isEmpty() || password.isEmpty() || email.isEmpty()
				|| telefono.isEmpty())
			return "Por favor, complete todos los campos obligatorios";
		if (!password.equals(confirmarPassword))
			return "Las contraseñas no coinciden";
		if (usuariosRegistrados.containsKey(telefono))
			return "El número de teléfono ya está registrado";
		if (usuariosRegistrados.values().stream().anyMatch(u -> u.getEmail().equals(email)))
			return "El correo electrónico ya está registrado";
		return null;
	}
    
    /**
     * Crea y persiste un nuevo estado para el usuario actual.
     * @param mensaje El texto del estado.
     * @param rutaImagen La URL de la imagen del estado.
     * @return {@code true} si se establece el estado correctamente, {@code false} si no hay un usuario logueado.
     */
    public boolean establecerStatus(String mensaje, String rutaImagen) {
		if (usuarioActual == null)
			return false;
		Status status = new Status(loadImageFromUrl(rutaImagen), mensaje);
		statusDAO.registrarEstado(status);
		return true;
	}
    
    /**
     * Carga una imagen desde una URL y la escala al tamaño de perfil estándar.
     * @param urlString La URL de la imagen.
     * @return Un objeto {@code ImageIcon} con la imagen cargada y escalada, o un icono vacío si falla.
     */
    public ImageIcon loadImageFromUrl(String urlString) {
		if (urlString == null || urlString.isBlank())
			return new ImageIcon();
		try {
			URL url = new URI(urlString).toURL();
			BufferedImage image = ImageIO.read(url);
			if (image != null) {
				Image scaledImage = image.getScaledInstance(PROFILE_IMAGE_SIZE, PROFILE_IMAGE_SIZE, Image.SCALE_SMOOTH);
				return new ImageIcon(scaledImage);
			}
		} catch (Exception e) {
            // Falla silenciosamente y devuelve un icono vacío
		}
		return new ImageIcon();
	}
    
    // ########################################################################
    // SECCIÓN 3: LÓGICA DE CONTACTOS Y GRUPOS
    // ########################################################################

    /**
     * Agrega un nuevo contacto individual a la lista del usuario actual.
     * Realiza validaciones para asegurar que el usuario a agregar existe y no es un duplicado.
     * @param nombre El nombre para el nuevo contacto.
     * @param telefono El número de teléfono del usuario a agregar.
     * @return {@code true} si el contacto se agrega con éxito, {@code false} en caso contrario.
     */
    public boolean agregarContacto(String nombre, String telefono) {
        if (usuarioActual == null || nombre.isBlank() || telefono.isBlank()) return false;
        if (!usuariosRegistrados.containsKey(telefono)) {
            JOptionPane.showMessageDialog(null, "No existe ningún usuario con ese teléfono.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (existeContacto(nombre) || existeContactoConTelefono(telefono)) {
            JOptionPane.showMessageDialog(null, "Ya tienes un contacto con ese nombre o teléfono.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        ContactoIndividual nuevoContacto = new ContactoIndividual(nombre, telefono);
        contactoDAO.registrarContacto(nuevoContacto);
        usuarioActual.addContacto(nuevoContacto);
        usuarioDAO.modificarUsuario(usuarioActual);
        notifyObserversListaContactos();
        return true;
    }

    /**
     * Devuelve una lista de todos los contactos que han sido explícitamente guardados
     * por el usuario, excluyendo los contactos desconocidos generados automáticamente.
     * Ideal para ventanas de gestión de contactos.
     * @return Una lista de Contactos conocidos (individuales y grupos).
     */
    public List<Contacto> obtenerContactosConocidos() {
        if (usuarioActual == null) {
            return Collections.emptyList();
        }
        
        return usuarioActual.getContactos().stream()
            .filter(contacto -> {
                if (contacto instanceof ContactoIndividual) {
                    return !((ContactoIndividual) contacto).isDesconocido();
                }
                return true; 
            })
            .sorted(Comparator.comparing(Contacto::getNombre))
            .collect(Collectors.toList());
    }
    
    /**
     * Agrega un contacto desconocido.
     * @param contactoDesconocido El objeto {@code ContactoIndividual} marcado como desconocido.
     * @param nuevoNombre El nombre que el usuario desea asignar a este contacto.
     * @return {@code true} si el proceso es exitoso, {@code false} en caso contrario.
     */
    public boolean registrarContactoDesconocido(ContactoIndividual contactoDesconocido, String nuevoNombre) {
        if (usuarioActual == null || !contactoDesconocido.isDesconocido() || nuevoNombre.isBlank()) return false;
        if (existeContacto(nuevoNombre)) {
            JOptionPane.showMessageDialog(null, "Ya existe un contacto con el nombre '" + nuevoNombre + "'.", "Nombre duplicado", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        int idAntiguo = contactoDesconocido.getCodigo();

        contactoDesconocido.registrarComoConocido(nuevoNombre);
        contactoDAO.modificarContacto(contactoDesconocido);
        
        int idNuevo = contactoDesconocido.getCodigo();

        if (usuarioActual.removeContactoID(idAntiguo)) {
            usuarioActual.addContactoID(idNuevo);
            usuarioDAO.modificarUsuario(usuarioActual);
        }
        
        notifyObserversListaContactos();
        notifyObserversChatsRecientes();
        notifyObserversContactoActual(contactoDesconocido);
        return true;
    }
    
    /**
     * Crea un nuevo grupo, lo persiste y lo añade a la lista de contactos del usuario actual.
     * @param nombre El nombre del grupo.
     * @param miembros La lista de contactos individuales que formarán parte del grupo.
     * @param foto El icono del grupo.
     * @param urlFoto La URL de la imagen del grupo para persistencia.
     */
    public void crearGrupo(String nombre, List<ContactoIndividual> miembros, ImageIcon foto, String urlFoto) {
        if (usuarioActual == null || nombre.isBlank() || miembros.isEmpty() || existeContacto(nombre)) return;
        Grupo grupo = new Grupo(nombre, miembros, usuarioActual, foto);
        grupo.setUrlFoto(urlFoto); 
        grupoDAO.registrarGrupo(grupo);
        usuarioActual.addContacto(grupo);
        usuarioDAO.modificarUsuario(usuarioActual);
        notifyObserversListaContactos();
    }
    
    /**
     * Añade un contacto a un grupo ya existente
     * @param grupo El nombre del grupo
     * @param nuevoMiembro El contacto a añadir
     * @return
     */
    public boolean añadirContactoAGrupo(Grupo grupo, ContactoIndividual nuevoMiembro) {
        if (grupo == null || nuevoMiembro == null) return false;

        // Si ya es miembro, no se añade
        if (grupo.getParticipantes().contains(nuevoMiembro)) return false;

        grupo.addIntegrante(nuevoMiembro);
        grupoDAO.modificarGrupo(grupo); // Persistimos el cambio
        notifyObserversListaContactos(); // Si quieres refrescar
        notifyObserversContactoActual(grupo); // Si es el contacto actual

        return true;
    }

    
    /**
     * Elimina un contacto individual de la lista del usuario actual.
     * @param contacto El contacto a eliminar.
     */
    public void eliminarContacto(ContactoIndividual contacto) {
    	if (usuarioActual == null || contacto == null)
			return;
		if (usuarioActual.removeContacto(contacto)) {
			usuarioDAO.modificarUsuario(usuarioActual);
			notifyObserversListaContactos();
			if (contacto.equals(contactoActual)) {
				setContactoActual(null);
			}
			notifyObserversChatsRecientes();
		}
    }
    
    /**
     * Persiste los cambios realizados en un objeto Grupo (p. ej., añadir/eliminar miembros).
     * @param grupo El grupo con los datos modificados.
     */
    public void modificarGrupo(Grupo grupo) {
        grupoDAO.modificarGrupo(grupo);
    }

    // ########################################################################
    // SECCIÓN 4: LÓGICA DE MENSAJERÍA
    // ########################################################################

    /**
     * Envía un mensaje de un solo uso a un contacto individual.
     * Persiste el mensaje y lo añade al historial del chat correspondiente.
     * @param contactoDestino El contacto que recibirá el mensaje.
     * @param contenido El texto del mensaje.
     */
    public void enviarMensaje(ContactoIndividual contactoDestino, String contenido) {
        Usuario emisor = usuarioActual;
        Mensaje mensaje = new Mensaje(contenido, LocalDateTime.now(), emisor, contactoDestino);
        mensajeDAO.registrarMensaje(mensaje);
        contactoDestino.sendMensaje(mensaje);
        crearContactoReversoSiNoExiste(emisor, contactoDestino);
        notifyObserversChatsRecientes();
        notifyObserversContactoActual(contactoActual);
    }
    
    /**
     * Si el receptor de un mensaje no tiene al emisor en sus contactos, este método crea
     * un "contacto desconocido" para que el receptor pueda ver el chat.
     * @param emisor El usuario que envía el mensaje.
     * @param contactoOriginal El contacto del emisor que representa al receptor.
     */
    private void crearContactoReversoSiNoExiste(Usuario emisor, ContactoIndividual contactoOriginal) {
        Usuario receptor = usuariosRegistrados.get(contactoOriginal.getTelefono());
        if (receptor == null) return;
        boolean reversoExiste = receptor.getContactos().stream()
            .filter(c -> c instanceof ContactoIndividual)
            .map(c -> (ContactoIndividual) c)
            .anyMatch(c -> c.getTelefono().equals(emisor.getTelefono()));
        if (!reversoExiste) {
            ContactoIndividual nuevoReverso = new ContactoIndividual(emisor.getTelefono());
            contactoDAO.registrarContacto(nuevoReverso);
            receptor.addContacto(nuevoReverso);
            usuarioDAO.modificarUsuario(receptor);
        }
    }

    /**
     * Envía un mensaje a todos los miembros de un grupo.
     * Internamente, se traduce en enviar mensajes individuales a cada participante.
     * @param grupo El grupo destino.
     * @param contenido El texto del mensaje.
     */
    public void enviarMensajeAGrupo(Grupo grupo, String contenido) {
        if (grupo == null || contenido.isBlank() || usuarioActual == null) return;
        for (ContactoIndividual miembro : grupo.getParticipantes()) {
            if (miembro.getTelefono().equals(usuarioActual.getTelefono())) continue;
            usuarioActual.getContactos().stream()
                .filter(c -> c instanceof ContactoIndividual)
                .map(c -> (ContactoIndividual) c)
                .filter(c -> c.getTelefono().equals(miembro.getTelefono()))
                .findFirst()
                .ifPresent(contactoDirecto -> enviarMensaje(contactoDirecto, contenido));
        }
    }
    
    /**
     * Busca mensajes en todos los chats del usuario actual que coincidan con los criterios de búsqueda.
     * @param texto Texto a buscar dentro del contenido del mensaje.
     * @param telefono Teléfono del emisor o receptor.
     * @param nombre Nombre del emisor o receptor.
     * @return Una lista de mensajes que cumplen los filtros, ordenados por fecha.
     */
    public List<Mensaje> buscarMensajes(String texto, String telefono, String nombre) {
    	return obtenerContactos().stream().flatMap(contacto -> contacto.getMensajes().stream())
				.filter(mensaje -> (texto == null || texto.isBlank()
						|| mensaje.getTexto().toLowerCase().contains(texto.toLowerCase()))
						&& (telefono == null || telefono.isBlank() || mensaje.getEmisor().getTelefono().equals(telefono)
								|| (mensaje.getReceptor() instanceof ContactoIndividual ci
										&& ci.getTelefono().equals(telefono)))
						&& (nombre == null || nombre.isBlank()
								|| mensaje.getEmisor().getNombre().equalsIgnoreCase(nombre)
								|| mensaje.getReceptor().getNombre().equalsIgnoreCase(nombre)))
				.sorted().collect(Collectors.toList());
    }
    
    // ########################################################################
    // SECCIÓN 5: FUNCIONALIDADES PREMIUM Y EXTRAS
    // ########################################################################
    
    /**
     * Inicia el proceso para que el usuario actual se convierta en Premium.
     * Calcula los descuentos aplicables y, si el usuario acepta, actualiza su estado.
     */
    public void activarPremiumConDescuento() {
    	if (usuarioActual == null)
			return;
		double precioBase = 100.0;
		CalculadoraDescuentos calculadora = new CalculadoraDescuentos(usuarioActual, this);
		String resultado = calculadora.calcularDescuentos(precioBase);
		if (JOptionPane.showConfirmDialog(null, resultado + "\n¿Desea activar Premium?", "Resumen de descuentos",
				JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
			usuarioActual.setPremium(true);
			usuarioDAO.modificarUsuario(usuarioActual);
			JOptionPane.showMessageDialog(null, "¡Felicidades! Ya eres usuario Premium.");
		}
    }

    /**
     * Exporta un informe en PDF con la agenda completa del usuario y el historial detallado
     * de un chat específico. Esta función solo está disponible para usuarios Premium.
     * @param rutaDestino La ruta del archivo donde se guardará el PDF.
     * @param contactoADetallar El contacto cuyo historial de chat se incluirá en el informe.
     * @return {@code true} si la exportación es exitosa, {@code false} en caso contrario.
     */
    public boolean exportarPdfConDatos(String rutaDestino, ContactoIndividual contactoADetallar) {
        if (usuarioActual == null || !usuarioActual.isPremium()) {
            JOptionPane.showMessageDialog(null, "Solo los usuarios Premium pueden exportar.", "Acceso Denegado", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(rutaDestino));
            document.open();

            Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Font fontSubtitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.DARK_GRAY);
            Font fontSeccion = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            Font fontNormal = FontFactory.getFont(FontFactory.HELVETICA, 11);
            Font fontMensajePropio = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, BaseColor.BLUE);
            Font fontMensajeAjeno = FontFactory.getFont(FontFactory.HELVETICA, 9, BaseColor.BLACK);

            // SECCIÓN 1: AGENDA
            Paragraph titulo = new Paragraph("Informe de Usuario y Agenda", fontTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.setSpacingAfter(20);
            document.add(titulo);
            document.add(new Paragraph("Propietario: " + usuarioActual.getNombre(), fontNormal));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Agenda: Contactos Individuales", fontSubtitulo));
            PdfPTable tablaIndividuales = new PdfPTable(2);
            tablaIndividuales.addCell("Nombre");
            tablaIndividuales.addCell("Teléfono");

            for (Contacto c : usuarioActual.getContactos()) {
                if (c instanceof ContactoIndividual ci) {
                    tablaIndividuales.addCell(ci.getNombre());
                    tablaIndividuales.addCell(ci.getTelefono());
                }
            }
            document.add(tablaIndividuales);

            document.add(new Paragraph(" "));
            document.add(new Paragraph("Agenda: Grupos", fontSubtitulo));
            for (Contacto c : usuarioActual.getContactos()) {
                if (c instanceof Grupo g) {
                    document.add(new Paragraph("Grupo: " + g.getNombre(), fontSeccion));
                    PdfPTable tablaGrupo = new PdfPTable(2);
                    tablaGrupo.addCell("Nombre");
                    tablaGrupo.addCell("Teléfono");

                    for (ContactoIndividual miembro : g.getParticipantes()) {
                        tablaGrupo.addCell(miembro.getNombre());
                        tablaGrupo.addCell(miembro.getTelefono());
                    }
                    document.add(tablaGrupo);
                    document.add(new Paragraph(" "));
                }
            }

            // SECCIÓN 2: CHAT DETALLADO
            document.newPage();
            Paragraph chatTitulo = new Paragraph("Historial de Chat Detallado", fontTitulo);
            chatTitulo.setAlignment(Element.ALIGN_CENTER);
            chatTitulo.setSpacingAfter(20);
            document.add(chatTitulo);

            document.add(new Paragraph("Conversación con: " + contactoADetallar.getNombre(), fontSubtitulo));
            document.add(new Paragraph(" "));

            PdfPTable tablaChat = new PdfPTable(1);
            tablaChat.setWidthPercentage(100f);

            List<Mensaje> mensajes = contactoADetallar.getMensajes();
            mensajes.sort(Comparator.comparing(Mensaje::getHora));

            for (Mensaje m : mensajes) {
                boolean esPropio = m.getEmisor().equals(usuarioActual);
                Font estilo = esPropio ? fontMensajePropio : fontMensajeAjeno;
                String encabezado = (esPropio ? "Tú" : m.getEmisor().getNombre()) + " (" + m.getHora().toString() + "): ";
                Paragraph parrafo = new Paragraph(encabezado + m.getTexto(), estilo);
                PdfPCell celda = new PdfPCell(parrafo);
                celda.setBorder(0);
                celda.setPaddingBottom(5);
                tablaChat.addCell(celda);
            }

            document.add(tablaChat);
            document.close();

            JOptionPane.showMessageDialog(null, "El informe PDF ha sido guardado con éxito.", "Exportación Completa", JOptionPane.INFORMATION_MESSAGE);
            return true;
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ########################################################################
    // SECCIÓN 6: MÉTODOS GETTER Y AUXILIARES
    // ########################################################################
    /**
     * Devuelve el usuario actualmente autenticado.
     * @return el usuario actual o {@code null} si no hay sesión iniciada.
     */
    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    /**
     * Devuelve el contacto actualmente seleccionado.
     * @return el contacto actual, o {@code null} si no hay contacto activo.
     */
    public Contacto getContactoActual() {
        return contactoActual;
    }

    /**
     * Establece el contacto actual y notifica a los observadores.
     * @param contacto contacto que se va a activar en la vista.
     */
    public void setContactoActual(Contacto contacto) {
        this.contactoActual = contacto;
        notifyObserversContactoActual(contacto);
    }

    /**
     * Busca un usuario registrado por su número de teléfono.
     * @param telefono número de teléfono a buscar.
     * @return el usuario correspondiente o {@code null} si no existe.
     */
    public Usuario buscarUsuarioPorTelefono(String telefono) {
        return usuariosRegistrados.get(telefono);
    }

    /**
     * Devuelve la lista de contactos del usuario actual.
     * @return lista de contactos o vacía si no hay sesión iniciada.
     */
    public List<Contacto> obtenerContactos() {
        return (usuarioActual != null) ? usuarioActual.getContactos() : Collections.emptyList();
    }

    /**
     * Busca un contacto conocido por su nombre.
     * @param nombre nombre del contacto a buscar.
     * @return el contacto si existe, o {@code null} si no se encuentra.
     */
    public Contacto obtenerContactoPorNombre(String nombre) {
        return obtenerContactos().stream()
                .filter(c -> c.getNombre().equalsIgnoreCase(nombre))
                .findFirst()
                .orElse(null);
    }

    /**
     * Devuelve los mensajes ordenados cronológicamente de un contacto.
     * @param contacto contacto del que obtener los mensajes.
     * @return lista de mensajes ordenada o vacía si es {@code null}.
     */
    public List<Mensaje> getMensajes(Contacto contacto) {
        return (contacto != null) ?
                contacto.getMensajes().stream().sorted().collect(Collectors.toList()) :
                Collections.emptyList();
    }

    /**
     * Genera la lista de chats recientes para el usuario actual.
     * @return array de cadenas con la descripción de los chats recientes.
     */
    public String[] getChatsRecientes() {
        if (usuarioActual == null) {
            return new String[]{"No hay chats recientes"};
        }

        List<String> chats = usuarioActual.getContactos().stream()
                .filter(c -> c != null && !c.getMensajes().isEmpty())
                .sorted(Comparator.comparing(this::getUltimoMensajeTiempo).reversed())
                .map(c -> "Chat con " + c.getNombre())
                .collect(Collectors.toList());

        return chats.isEmpty() ? new String[]{"No hay chats recientes"} : chats.toArray(new String[0]);
    }

    /**
     * Verifica si ya existe un contacto con el nombre dado.
     * @param nombre nombre del contacto.
     * @return {@code true} si ya existe, {@code false} en caso contrario.
     */
    public boolean existeContacto(String nombre) {
        if (usuarioActual == null) return false;
        return obtenerContactos().stream()
                .anyMatch(c -> c.getNombre().equalsIgnoreCase(nombre.trim()));
    }

    /**
     * Verifica si ya existe un contacto individual con el número de teléfono dado.
     * @param telefono número de teléfono a verificar.
     * @return {@code true} si ya existe, {@code false} si no.
     */
    public boolean existeContactoConTelefono(String telefono) {
        if (usuarioActual == null) return false;
        return obtenerContactos().stream()
                .filter(c -> c instanceof ContactoIndividual)
                .map(c -> (ContactoIndividual) c)
                .anyMatch(c -> c.getTelefono().equals(telefono));
    }

    /**
     * Obtiene el tiempo del último mensaje enviado o recibido con un contacto.
     * @param contacto contacto del cual obtener el último tiempo de mensaje.
     * @return {@link LocalDateTime} del mensaje más reciente o {@code LocalDateTime.MIN} si no hay mensajes.
     */
    private LocalDateTime getUltimoMensajeTiempo(Contacto contacto) {
        return contacto.getMensajes().stream()
                .map(Mensaje::getHora)
                .max(LocalDateTime::compareTo)
                .orElse(LocalDateTime.MIN);
    }

    /**
     * Obtiene el nombre del usuario actualmente autenticado.
     * @return nombre del usuario actual o "Desconectado" si no hay sesión iniciada.
     */
    public String getNombreUserActual() {
        return (usuarioActual != null) ? usuarioActual.getNombre() : "Desconectado";
    }

    /**
     * Verifica si el usuario actual tiene cuenta Premium.
     * @return {@code true} si es Premium, {@code false} en caso contrario.
     */
    public boolean isPremiumUserActual() {
        return (usuarioActual != null) && usuarioActual.isPremium();
    }

    /**
     * Obtiene el icono de perfil del usuario actual.
     * @return {@link ImageIcon} correspondiente o icono vacío si no está definido.
     */
    public ImageIcon getIconoUserActual() {
        if (usuarioActual == null) {
            return new ImageIcon();
        }
        ImageIcon foto = usuarioActual.getFoto();
        return (foto != null) ? foto : new ImageIcon();
    }

    /**
     * Busca un contacto individual por teléfono y lo crea si no existe.
     * @param telefono teléfono del contacto a buscar o crear.
     * @return el contacto individual asociado o {@code null} si no se encuentra o falla la creación.
     */
    public ContactoIndividual obtenerOcrearContactoParaTelefono(String telefono) {
        if (usuarioActual == null || telefono.isBlank()) return null;

        Optional<ContactoIndividual> contactoExistente = obtenerContactos().stream()
                .filter(c -> c instanceof ContactoIndividual)
                .map(c -> (ContactoIndividual) c)
                .filter(c -> c.getTelefono().equals(telefono))
                .findFirst();

        if (contactoExistente.isPresent()) return contactoExistente.get();

        Usuario usuarioDestino = buscarUsuarioPorTelefono(telefono);
        if (usuarioDestino == null) {
            JOptionPane.showMessageDialog(null,
                    "No existe ningún usuario con el teléfono " + telefono,
                    "Usuario no encontrado", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        if (agregarContacto(usuarioDestino.getNombre(), telefono)) {
            return (ContactoIndividual) obtenerContactoPorNombre(usuarioDestino.getNombre());
        }

        return null;
    }

    // ########################################################################
    // SECCIÓN 7: OBSERVERS
    // ########################################################################

    /**
     * Añade un observador para actualizaciones del contacto actual o chats recientes.
     * @param o observador a registrar.
     */
    public void addObserverChats(ObserverChats o) {
        if (!observersChats.contains(o)) observersChats.add(o);
    }

    /**
     * Elimina un observador de actualizaciones de chats.
     * @param o observador a eliminar.
     */
    public void removeObserverChats(ObserverChats o) {
        observersChats.remove(o);
    }

    /**
     * Añade un observador de la lista de contactos.
     * @param o observador a registrar.
     */
    public void addObserverContactos(ObserverContactos o) {
        if (!observersContactos.contains(o)) observersContactos.add(o);
    }

    /**
     * Elimina un observador de la lista de contactos.
     * @param o observador a eliminar.
     */
    public void removeObserverContactos(ObserverContactos o) {
        observersContactos.remove(o);
    }

    /**
     * Notifica a todos los observadores que ha cambiado el contacto actual.
     * @param c nuevo contacto actual.
     */
    private void notifyObserversContactoActual(Contacto c) {
        observersChats.forEach(o -> o.updateContactoActual(c));
    }

    /**
     * Notifica a todos los observadores que ha cambiado la lista de contactos.
     */
    private void notifyObserversListaContactos() {
        observersContactos.forEach(ObserverContactos::updateListaContactos);
    }

    /**
     * Notifica a todos los observadores que deben actualizar la vista de chats recientes.
     */
    private void notifyObserversChatsRecientes() {
        String[] chatsRecientes = getChatsRecientes();
        observersChats.forEach(observer -> observer.updateChatsRecientes(chatsRecientes));
    }
}