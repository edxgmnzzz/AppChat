package umu.tds.app.AppChat;

import java.awt.Image;
import java.io.FileOutputStream;
import java.awt.image.BufferedImage;
import java.net.URI;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import umu.tds.app.persistencia.*;

public class Controlador {
	private static final Controlador instancia;
	private static final Logger LOGGER;
	private static final int PROFILE_IMAGE_SIZE = 100;

	static {
		LOGGER = Logger.getLogger(Controlador.class.getName());
		try {
			instancia = new Controlador();
		} catch (Exception e) {
			throw new ExceptionInInitializerError("Error al inicializar Controlador: " + e.getMessage());
		}
	}

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

	private Controlador() {
		initialize();
	}

	public static Controlador getInstancia() {
		return instancia;
	}

	// ########################################################################
	// SECCIÓN 1: INICIALIZACIÓN
	// ########################################################################

	private void initialize() {
		LOGGER.info("Inicializando Controlador...");
		initializeDaosAndCollections();
		realizarSimulacionInicialSiEsNecesario();
		cargarDatosDesdePersistencia();
		LOGGER.info("Inicialización completada.");
	}

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

	private void realizarSimulacionInicialSiEsNecesario() {
		if (!usuarioDAO.recuperarTodosUsuarios().isEmpty()) {
			return;
		}
		LOGGER.info("--- BASE DE DATOS VACÍA: REALIZANDO SIMULACIÓN INICIAL ---");

		Usuario florentino = new Usuario("600111222", "Florentino Pérez", "pass1", "f@p.com", "Hala Madrid", null,
				false);
		usuarioDAO.registrarUsuario(florentino);
		Usuario laporta = new Usuario("600333444", "Joan Laporta", "pass2", "j@l.com", "Visca Barça", null, false);
		usuarioDAO.registrarUsuario(laporta);
		
		Usuario cerezo = new Usuario("600555666", "Enrique Cerezo", "pass3", "j@l.com", "Aupa Atleti", null, false);
		usuarioDAO.registrarUsuario(cerezo);

		/*
		 * ContactoIndividual convDeFlorentino = new ContactoIndividual("Joan Laporta",
		 * 0, laporta.getTelefono(), laporta);
		 * contactoDAO.registrarContacto(convDeFlorentino);
		 * 
		 * ContactoIndividual convDeLaporta = new ContactoIndividual("Florentino Pérez",
		 * 0, florentino.getTelefono(), florentino);
		 * contactoDAO.registrarContacto(convDeLaporta);
		 * 
		 * florentino.addContacto(convDeFlorentino);
		 * usuarioDAO.modificarUsuario(florentino);
		 * 
		 * laporta.addContacto(convDeLaporta); usuarioDAO.modificarUsuario(laporta);
		 */
		/*
		 * Mensaje mensaje = new Mensaje("¡Hola Joan! ¿Qué tal por Barcelona?",
		 * LocalDateTime.now(), florentino, convDeFlorentino);
		 * mensajeDAO.registrarMensaje(mensaje);
		 */

		LOGGER.info("--- SIMULACIÓN INICIAL COMPLETADA Y PERSISTIDA ---");
	}

	private void cargarDatosDesdePersistencia() {
		cargarUsuarios();
		Map<Integer, Contacto> todosLosContactosDelSistema = cargarContactosYGrupos();
		vincularContactosAUsuarios(todosLosContactosDelSistema);
		vincularMensajesAContactos(todosLosContactosDelSistema);

		contactoActual = todosLosContactosDelSistema.values().stream()
				.filter(c -> c != null && !c.getMensajes().isEmpty())
				.max(Comparator.comparing(this::getUltimoMensajeTiempo)).orElse(null);
	}

	private void cargarUsuarios() {
		for (Usuario u : usuarioDAO.recuperarTodosUsuarios()) {
			try {
				String urlFoto = u.getUrlFoto();
				if (urlFoto != null && !urlFoto.isBlank() && !"null".equalsIgnoreCase(urlFoto)) {
					u.setFoto(loadImageFromUrl(urlFoto));
				}
			} catch (Exception e) {
				LOGGER.warning("Fallo al cargar imagen para usuario " + u.getTelefono() + ": " + e.getMessage());
			}
			usuariosRegistrados.put(u.getTelefono(), u);
		}
		for (Usuario u : usuariosRegistrados.values()) {
			LOGGER.info("📦 Usuario recuperado: " + u.getNombre() + " [ID=" + u.getId() + "]");
			LOGGER.info("📋 ContactosID del usuario: " + u.getContactosID());
		}

	}

	private Map<Integer, Contacto> cargarContactosYGrupos() {
		LOGGER.info("🔄 Iniciando carga de contactos y grupos...");

		Map<Integer, Contacto> mapaContactos = new HashMap<>();

		List<ContactoIndividual> individuales = contactoDAO.recuperarTodosContactos();
		if (individuales != null) {
			for (ContactoIndividual c : individuales) {
				mapaContactos.put(c.getCodigo(), c);
				LOGGER.info("📥 ContactoIndividual añadido: " + c.getNombre() + " [ID=" + c.getCodigo() + "]");
			}
		} else {
			LOGGER.warning("⚠️ La lista de contactos individuales es null");
		}

		List<Grupo> grupos = grupoDAO.recuperarTodosGrupos();
		if (grupos != null) {
			for (Grupo g : grupos) {
				mapaContactos.put(g.getCodigo(), g);
				LOGGER.info("👥 Grupo añadido: " + g.getNombre() + " [ID=" + g.getCodigo() + "]");
			}
		} else {
			LOGGER.warning("⚠️ La lista de grupos es null");
		}

		LOGGER.info("✅ Contactos totales cargados: " + mapaContactos.size());
		return mapaContactos;
	}

	private void vincularContactosAUsuarios(Map<Integer, Contacto> todosLosContactosDelSistema) {
		for (Usuario usuario : usuariosRegistrados.values()) {
			List<Contacto> listaPersonalDeContactos = new ArrayList<>();
			for (int idContacto : usuario.getContactosID()) {
				Contacto contacto = todosLosContactosDelSistema.get(idContacto);
				if (contacto != null) {
					listaPersonalDeContactos.add(contacto);
				}
			}
			usuario.setContactos(listaPersonalDeContactos);
			LOGGER.info("🔗 Contactos vinculados a usuario " + usuario.getNombre() + ":");
			for (Contacto c : listaPersonalDeContactos) {
				LOGGER.info("➡️ " + c.getNombre() + " [ID=" + c.getCodigo() + "]");
			}
		}

	}

	private void vincularMensajesAContactos(Map<Integer, Contacto> todosLosContactosDelSistema) {
	    List<Mensaje> mensajes = mensajeDAO.recuperarTodosMensajes();
	    int totalVinculados = 0;

	    for (Mensaje mensaje : mensajes) {
	        LOGGER.info("🔄 Procesando mensaje ID=" + mensaje.getCodigo() + " → \"" + mensaje.getTexto() + "\"");

	        Usuario emisor = usuariosRegistrados.get(mensaje.getEmisor().getTelefono());
	        if (emisor == null) {
	            LOGGER.warning("❌ Emisor no encontrado para mensaje: " + mensaje.getTexto());
	            continue;
	        }
	        mensaje.setEmisor(emisor);

	        Contacto receptor = todosLosContactosDelSistema.get(mensaje.getReceptor().getCodigo());
	        if (receptor == null) {
	            LOGGER.warning("❌ Receptor no encontrado para mensaje: " + mensaje.getTexto());
	            continue;
	        }
	        mensaje.setReceptor(receptor);

	        // ➤ Añadir mensaje al contacto del emisor hacia el receptor
	        if (receptor instanceof ContactoIndividual ciReceptor) {
	            String telefonoReceptor = ciReceptor.getTelefono();

	            ContactoIndividual contactoDelEmisor = emisor.getContactos().stream()
	                .filter(c -> c instanceof ContactoIndividual)
	                .map(c -> (ContactoIndividual) c)
	                .filter(c -> c.getTelefono().equals(telefonoReceptor))
	                .findFirst()
	                .orElse(null);

	            if (contactoDelEmisor != null) {
	                LOGGER.info("🧩 Contacto emisor → receptor encontrado: " + contactoDelEmisor.getNombre());

	                if (!contactoDelEmisor.getMensajes().contains(mensaje)) {
	                    contactoDelEmisor.sendMensaje(mensaje);
	                    LOGGER.info("✅ Mensaje añadido al contacto del emisor (" + emisor.getNombre() + " → " + contactoDelEmisor.getNombre() + ")");
	                    totalVinculados++;
	                }
	            } else {
	                LOGGER.warning("⚠️ Contacto del emisor hacia " + telefonoReceptor + " no encontrado.");
	            }

	            // ➤ Añadir mensaje al contacto del receptor hacia el emisor
	            Usuario receptorUsuario = ciReceptor.getUsuario();
	            if (receptorUsuario != null) {
	                String telefonoEmisor = emisor.getTelefono();

	                ContactoIndividual contactoDelReceptor = receptorUsuario.getContactos().stream()
	                    .filter(c -> c instanceof ContactoIndividual)
	                    .map(c -> (ContactoIndividual) c)
	                    .filter(c -> c.getTelefono().equals(telefonoEmisor))
	                    .findFirst()
	                    .orElse(null);

	                if (contactoDelReceptor != null) {
	                    LOGGER.info("🧩 Contacto receptor → emisor encontrado: " + contactoDelReceptor.getNombre());

	                    if (!contactoDelReceptor.getMensajes().contains(mensaje)) {
	                        contactoDelReceptor.sendMensaje(mensaje);
	                        LOGGER.info("✅ Mensaje añadido al contacto del receptor (" + receptorUsuario.getNombre() + " → " + contactoDelReceptor.getNombre() + ")");
	                        totalVinculados++;
	                    }
	                } else {
	                    LOGGER.warning("⚠️ Contacto del receptor hacia " + telefonoEmisor + " no encontrado.");
	                }
	            } else {
	                LOGGER.warning("❌ Usuario receptor nulo en ContactoIndividual: " + ciReceptor.getNombre());
	            }
	        } else {
	            LOGGER.warning("⚠️ Receptor no es ContactoIndividual, ignorado para vinculación directa.");
	        }
	    }

	    LOGGER.info("✅ Vinculación completada. Total de mensajes vinculados: " + totalVinculados);
	}



	// ########################################################################
	// SECCIÓN 2: LÓGICA DE NEGOCIO Y API PÚBLICA
	// ########################################################################

	public boolean iniciarSesion(String telefono, String password) {
		if (telefono == null || password == null)
			return false;
		Usuario usuario = usuariosRegistrados.get(telefono);
		if (usuario != null && usuario.getPassword().equals(password)) {
			usuarioActual = usuario;
			contactoActual = obtenerContactos().stream().filter(c -> !c.getMensajes().isEmpty())
					.max(Comparator.comparing(this::getUltimoMensajeTiempo)).orElse(null);
			notifyObserversChatsRecientes();
			notifyObserversListaContactos();
			notifyObserversContactoActual(contactoActual);
			return true;
		}
		return false;
	}

	public void cerrarSesion() {
		usuarioActual = null;
		contactoActual = null;
		notifyObserversChatsRecientes();
		notifyObserversContactoActual(null);
	}

	public boolean registrarUsuario(String nombreReal, String nombreUsuario, String password, String confirmarPassword,
			String email, String telefono, String rutaFoto, String saludo) {
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

	public boolean agregarContacto(ContactoIndividual contacto) {
	    if (usuarioActual == null || contacto == null || existeContacto(contacto.getNombre())) {
	        return false;
	    }

	    // Registrar y vincular contacto al usuario actual
	    contactoDAO.registrarContacto(contacto);
	    usuarioActual.addContacto(contacto);
	    usuarioDAO.modificarUsuario(usuarioActual);

	    // 🔁 Ya no se crea contacto espejo en el otro usuario. Eso se hace automáticamente al enviar un mensaje.
	    LOGGER.info("✅ Contacto " + contacto.getNombre() + " añadido a " + usuarioActual.getNombre());

	    notifyObserversListaContactos();
	    return true;
	}


	public void crearGrupo(String nombre, List<ContactoIndividual> miembros) {
		if (usuarioActual == null || nombre == null || nombre.trim().isEmpty() || miembros == null || miembros.isEmpty()
				|| existeContacto(nombre)) {
			return;
		}
		Grupo grupo = new Grupo(nombre, 0, miembros, usuarioActual);
		grupoDAO.registrarGrupo(grupo);
		usuarioActual.addContacto(grupo);
		usuarioDAO.modificarUsuario(usuarioActual);
		notifyObserversListaContactos();
	}

	/*public void enviarMensaje(Contacto contactoEmisor, String contenido) {
	    Usuario emisor = usuarioActual;

	    if (contactoEmisor instanceof ContactoIndividual contactoIndividualEmisor) {
	        Usuario receptor = contactoIndividualEmisor.getUsuario();
	        Mensaje mensaje = new Mensaje(contenido, LocalDateTime.now(), emisor, contactoIndividualEmisor);
	        contactoIndividualEmisor.sendMensaje(mensaje);
	        mensajeDAO.registrarMensaje(mensaje);
	        contactoDAO.modificarContacto(contactoIndividualEmisor);

	        ContactoIndividual contactoReverso = receptor.getContactos().stream()
	            .filter(c -> c instanceof ContactoIndividual)
	            .map(c -> (ContactoIndividual) c)
	            .filter(c -> c.getTelefono().equals(emisor.getTelefono()))
	            .findFirst()
	            .orElse(null);

	        if (contactoReverso == null) {
	            contactoReverso = new ContactoIndividual("", emisor.getTelefono(), receptor);
	            receptor.addContacto(contactoReverso);
	            contactoDAO.registrarContacto(contactoReverso);
	        }

	        contactoReverso.sendMensaje(mensaje);
	        contactoDAO.modificarContacto(contactoReverso);
	    }

	    else if (contactoEmisor instanceof Grupo grupo) {
	        for (ContactoIndividual contactoIntegrante : grupo.getParticipantes()) {
	            Usuario miembro = contactoIntegrante.getUsuario();
	            if (miembro.equals(emisor)) continue;

	            // Buscar o crear un contacto hacia el grupo para este miembro
	            Contacto contactoDelGrupoEnMiembro = miembro.getContactos().stream()
	                .filter(c -> c instanceof Grupo)
	                .map(c -> (Grupo) c)
	                .filter(g -> g.getCodigo() == grupo.getCodigo())
	                .findFirst()
	                .orElse(null);

	            if (contactoDelGrupoEnMiembro == null) {
	                miembro.addContacto(grupo); // El grupo ya es compartido, solo se añade referencia
	                // No se persiste aquí porque el grupo ya está registrado
	            }

	            // Crear mensaje con receptor = grupo (mismo objeto)
	            Mensaje mensajeGrupo = new Mensaje(contenido, LocalDateTime.now(), emisor, grupo);

	            // Añadir mensaje al grupo (centralizado)
	            grupo.sendMensaje(mensajeGrupo);

	            // Añadir mensaje al miembro
	            contactoIntegrante.sendMensaje(mensajeGrupo);  // para poder visualizarlo si el diseño lo requiere
	            mensajeDAO.registrarMensaje(mensajeGrupo);

	            contactoDAO.modificarContacto(contactoIntegrante);
	        }

	    }

	    else {
	        throw new IllegalArgumentException("Tipo de contacto no soportado: " + contactoEmisor.getClass().getSimpleName());
	    }

	    notifyObserversChatsRecientes();
	    notifyObserversContactoActual(contactoActual);
	}*/
	
	public void enviarMensaje(ContactoIndividual contactoDestino, String contenido) {
	    Usuario emisor = usuarioActual;
	    Usuario receptor = contactoDestino.getUsuario();

	    // Crear mensaje
	    Mensaje mensaje = new Mensaje(contenido, LocalDateTime.now(), emisor, contactoDestino);

	    // Añadir mensaje al contacto del emisor
	    contactoDestino.sendMensaje(mensaje);
	    mensajeDAO.registrarMensaje(mensaje);
	    contactoDAO.modificarContacto(contactoDestino);

	    // Buscar o crear contacto inverso en el receptor hacia el emisor
	    ContactoIndividual contactoReverso = receptor.getContactos().stream()
	        .filter(c -> c instanceof ContactoIndividual)
	        .map(c -> (ContactoIndividual) c)
	        .filter(c -> c.getTelefono().equals(emisor.getTelefono()))
	        .findFirst()
	        .orElse(null);

	    if (contactoReverso == null) {
	        contactoReverso = new ContactoIndividual(emisor.getNombre(), emisor.getTelefono(), receptor);
	        receptor.addContacto(contactoReverso);
	        contactoDAO.registrarContacto(contactoReverso); // ❗ importante: persistirlo
	    }

	    contactoReverso.sendMensaje(mensaje);
	    contactoDAO.modificarContacto(contactoReverso);
	    
	    notifyObserversChatsRecientes();
	    notifyObserversContactoActual(contactoActual);
	}


	public void enviarMensajeAGrupo(Grupo grupo, String contenido) {
	    for (ContactoIndividual contacto : grupo.getParticipantes()) {
	        Usuario receptor = contacto.getUsuario();
	        if (!receptor.equals(usuarioActual)) {
	            enviarMensaje(contacto, contenido);
	        }
	    }

	    notifyObserversChatsRecientes();
	    notifyObserversContactoActual(contactoActual);
	}



	public void eliminarContacto(ContactoIndividual contacto) {
		if (usuarioActual == null || contacto == null)
			return;
		if (usuarioActual.removeContacto(contacto)) {
			usuarioDAO.modificarUsuario(usuarioActual);
			notifyObserversListaContactos();
			// Opcional: Notificar para actualizar chats si se elimina el chat actual
			if (contacto.equals(contactoActual)) {
				setContactoActual(null);
			}
			notifyObserversChatsRecientes();
		}
	}

	// ########################################################################
	// SECCIÓN 3: MÉTODOS GETTER Y AUXILIARES
	// ########################################################################

	public Usuario getUsuarioActual() {
		return usuarioActual;
	}

	public Contacto getContactoActual() {
		return contactoActual;
	}

	public void setContactoActual(Contacto contacto) {
		this.contactoActual = contacto;
		notifyObserversContactoActual(contacto);
	}

	public Map<String, Usuario> getusuariosRegistrados() {
		return usuariosRegistrados;
	}

	public Usuario buscarUsuarioPorTelefono(String telefono) {
		return usuariosRegistrados.get(telefono);
	}

	public List<Contacto> obtenerContactos() {
		return usuarioActual != null ? usuarioActual.getContactos() : Collections.emptyList();
	}

	public Contacto obtenerContactoPorNombre(String nombre) {
		return obtenerContactos().stream().filter(c -> c.getNombre().equalsIgnoreCase(nombre)).findFirst().orElse(null);
	}

	public List<Mensaje> getMensajes(Contacto contacto) {
		return (contacto != null) ? contacto.getMensajes().stream().sorted().collect(Collectors.toList())
				: Collections.emptyList();
	}

	public String[] getChatsRecientes() {
		if (usuarioActual == null) {
			return new String[0];
		}
		return obtenerContactos().stream().filter(c -> !c.getMensajes().isEmpty())
				.sorted(Comparator.comparing(this::getUltimoMensajeTiempo).reversed())
				.map(c -> "Chat con " + c.getNombre()).toArray(String[]::new);
	}

	private LocalDateTime getUltimoMensajeTiempo(Contacto contacto) {
		return contacto.getMensajes().stream().map(Mensaje::getHora).max(LocalDateTime::compareTo)
				.orElse(LocalDateTime.MIN);
	}

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
			LOGGER.severe("Error al cargar imagen: " + e.getMessage());
		}
		return new ImageIcon();
	}

	public int generarCodigoContacto() {
		Set<Integer> codigosExistentes = new HashSet<>();
		contactoDAO.recuperarTodosContactos().forEach(c -> codigosExistentes.add(c.getCodigo()));
		grupoDAO.recuperarTodosGrupos().forEach(g -> codigosExistentes.add(g.getCodigo()));
		int codigoGenerado;
		do {
			codigoGenerado = (int) (System.nanoTime() % Integer.MAX_VALUE);
		} while (codigosExistentes.contains(codigoGenerado));
		return codigoGenerado;
	}

	public boolean existeUsuario(String telefono) {
		return usuariosRegistrados.containsKey(telefono);
	}

	public boolean existeContacto(String nombre) {
		if (usuarioActual == null || nombre == null || nombre.trim().isEmpty()) {
			return false;
		}
		return obtenerContactos().stream().anyMatch(c -> c.getNombre().equalsIgnoreCase(nombre.trim()));
	}

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

	// ########################################################################
	// SECCIÓN 4: OBSERVERS
	// ########################################################################

	public void addObserverChats(ObserverChats o) {
		if (!observersChats.contains(o))
			observersChats.add(o);
	}

	public void removeObserverChats(ObserverChats o) {
		observersChats.remove(o);
	}

	public void addObserverContactos(ObserverContactos o) {
		if (!observersContactos.contains(o))
			observersContactos.add(o);
	}

	public void removeObserverContactos(ObserverContactos o) {
		observersContactos.remove(o);
	}

	private void notifyObserversContactoActual(Contacto c) {
		observersChats.forEach(o -> o.updateContactoActual(c));
	}

	private void notifyObserversListaContactos() {
		observersContactos.forEach(ObserverContactos::updateListaContactos);
	}

	private void notifyObserversChatsRecientes() {
		String[] chatsRecientes = getChatsRecientes();
		observersChats.forEach(observer -> observer.updateChatsRecientes(chatsRecientes));
	}

	// ########################################################################
	// SECCIÓN 5: FUNCIONALIDADES ADICIONALES
	// ########################################################################

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

	public boolean establecerStatus(String mensaje, String rutaImagen) {
		if (usuarioActual == null)
			return false;
		Status status = new Status(loadImageFromUrl(rutaImagen), mensaje);
		statusDAO.registrarEstado(status);
		return true;
	}

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

	public boolean exportarPdfConDatos(String rutaDestino) {
		if (usuarioActual == null || !usuarioActual.isPremium()) {
			JOptionPane.showMessageDialog(null, "Solo los usuarios Premium pueden exportar a PDF.", "Acceso denegado",
					JOptionPane.ERROR_MESSAGE);
			return false;
		}
		Document document = new Document();
		try {
			PdfWriter.getInstance(document, new FileOutputStream(rutaDestino));
			document.open();
			/* ... lógica de escritura ... */ document.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

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

	public int contarMensajesDelUsuario(Usuario usuario) {
		return (int) obtenerContactos().stream().flatMap(c -> c.getMensajes().stream())
				.filter(m -> m.getEmisor().equals(usuario)).count();
	}

	public ContactoIndividual obtenerContactoPorUsuario(Usuario usuario) {
		if (usuarioActual == null || usuario == null) {
			return null;
		}

		return obtenerContactos().stream().filter(c -> c instanceof ContactoIndividual) // Nos aseguramos de que es un
																						// contacto individual
				.map(c -> (ContactoIndividual) c) // Lo convertimos al tipo correcto
				.filter(ci -> ci.getUsuario() != null && ci.getUsuario().equals(usuario)) // Comparamos el usuario
																							// interno
				.findFirst().orElse(null);
	}
	// En Controlador.java

	public String getNombreUserActual() {
		return (usuarioActual != null) ? usuarioActual.getNombre() : "Desconectado";
	}

	public ImageIcon getIconoUserActual() {
		return (usuarioActual != null && usuarioActual.getFoto() != null) ? usuarioActual.getFoto() : new ImageIcon();
	}

	public boolean isPremiumUserActual() {
		return (usuarioActual != null) && usuarioActual.isPremium();
	}

	public String getEmailUserActual() {
		return (usuarioActual != null) ? usuarioActual.getEmail() : "";
	}

	public int getNumTelefonoUserActual() {
		return (usuarioActual != null && usuarioActual.getTelefono() != null)
				? Integer.parseInt(usuarioActual.getTelefono())
				: -1;
	}
}