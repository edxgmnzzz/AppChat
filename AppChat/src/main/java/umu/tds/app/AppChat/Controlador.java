package umu.tds.app.AppChat;

import java.awt.Image;
import java.io.FileOutputStream;
import java.io.IOException;
import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.itextpdf.awt.geom.Rectangle;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import umu.tds.app.persistencia.*;

public class Controlador {
    private static final Controlador instancia;
    private static final Logger LOGGER = Logger.getLogger(Controlador.class.getName());
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

    private Controlador() {
        initialize();
    }

    public static Controlador getInstancia() {
        return instancia;
    }

    // ########################################################################
    // SECCIÓN 1: INICIALIZACIÓN Y CARGA DE DATOS
    // ########################################################################

    private void initialize() {
        LOGGER.info("Inicializando Controlador...");
        initializeDaosAndCollections();
        realizarSimulacionInicialSiEsNecesario();
        cargarDatosDesdePersistencia();
        LOGGER.info("Inicialización completada.");
        notifyObserversContactoActual(this.contactoActual);
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

 // EN: Controlador.java -> realizarSimulacionInicialSiEsNecesario()

    private void realizarSimulacionInicialSiEsNecesario() {
        if (!usuarioDAO.recuperarTodosUsuarios().isEmpty()) {
            return;
        }
        LOGGER.info("--- BASE DE DATOS VACÍA: REALIZANDO SIMULACIÓN INICIAL ---");
        try {
            String path = "https://widget-assets.geckochat.io/69d33e2bd0ca2799b2c6a3a3870537a9.png";
            BufferedImage image = ImageIO.read(new URI(path).toURL());
            ImageIcon foto = new ImageIcon(image);
            Usuario florentino = new Usuario("600111222", "Florentino Pérez", "pass1", "f@p.com", "Hala Madrid", foto, false);
            florentino.setUrlFoto(path); // ← importante para persistencia

            LOGGER.info("[DEBUG] Registrando a Florentino con URL: " + florentino.getUrlFoto());
            usuarioDAO.registrarUsuario(florentino);
            
         // Los otros usuarios no tienen foto, así que no necesitan setUrlFoto
            Usuario laporta = new Usuario("600333444", "Joan Laporta", "pass2", "j@l.com", "Visca Barça", foto, true);
            usuarioDAO.registrarUsuario(laporta);
            LOGGER.info("[DEBUG] Florentino registrado con ID: " + florentino.getId());
        } catch (Exception e) {
            LOGGER.severe("[ERROR] Fallo al crear o registrar a Florentino: " + e.getMessage());
            e.printStackTrace();
        }



        
        Usuario cerezo = new Usuario("600555666", "Enrique Cerezo", "pass3", "e@c.com", "Aupa Atleti", null, true);
        usuarioDAO.registrarUsuario(cerezo);
        LOGGER.info("--- SIMULACIÓN INICIAL COMPLETADA Y PERSISTIDA ---");
    }

    private void cargarDatosDesdePersistencia() {
        cargarUsuarios();
        refrescarEstadoDesdePersistencia();
        
        // Seleccionar el chat más reciente como chat actual al iniciar
        contactoActual = obtenerContactos().stream()
            .filter(c -> c != null && !c.getMensajes().isEmpty())
            .max(Comparator.comparing(this::getUltimoMensajeTiempo))
            .orElse(null);
    }
    
    private void refrescarEstadoDesdePersistencia() {
        LOGGER.info("Refrescando estado desde la persistencia...");
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

    private void cargarUsuarios() {
        for (Usuario u : usuarioDAO.recuperarTodosUsuarios()) {
        	// LOG 4: Verificamos qué URL se recupera para cada usuario
            LOGGER.info("[FOTO-DEBUG] Procesando usuario: " + u.getNombre() + " con URL: '" + u.getUrlFoto() + "'");
        	try {
				String urlFoto = u.getUrlFoto();
				if (urlFoto != null && !urlFoto.isBlank() && !"null".equalsIgnoreCase(urlFoto)) {
					// LOG 5: Entramos al bloque de carga de imagen
	                LOGGER.info("[FOTO-DEBUG] Intentando cargar imagen desde URL: " + urlFoto);
					u.setFoto(loadImageFromUrl(urlFoto));
				}
			} catch (Exception e) {
				LOGGER.warning("Fallo al cargar imagen para usuario " + u.getTelefono() + ": " + e.getMessage());
			}
            usuariosRegistrados.put(u.getTelefono(), u);
        }
    }

    private Map<Integer, Contacto> cargarContactosYGrupos() {
        Map<Integer, Contacto> mapaContactos = new HashMap<>();
        contactoDAO.recuperarTodosContactos().forEach(c -> mapaContactos.put(c.getCodigo(), c));
        grupoDAO.recuperarTodosGrupos().forEach(g -> mapaContactos.put(g.getCodigo(), g));
        return mapaContactos;
    }

    private void vincularContactosAUsuarios(Map<Integer, Contacto> todosLosContactosDelSistema) {
        for (Usuario usuario : usuariosRegistrados.values()) {
            List<Contacto> listaPersonal = usuario.getContactosID().stream()
                .map(todosLosContactosDelSistema::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
            usuario.setContactos(listaPersonal);
        }
    }

    public int contarMensajesDelUsuario(Usuario usuario) {
    	return (int) obtenerContactos().stream().flatMap(c -> c.getMensajes().stream())
    			.filter(m -> m.getEmisor().equals(usuario)).count();
    }
    
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
    
 // EN: Controlador.java

    public void notifyObservers() {
        //System.out.println("--- CONTROLADOR: INICIO NOTIFICACIÓN INICIAL ---");
        //System.out.println("--- CONTROLADOR: Notificando lista de contactos. Total conocidos: " + obtenerContactosConocidos().size());
        notifyObserversListaContactos(); 
        
        //System.out.println("--- CONTROLADOR: Notificando chats recientes. Total: " + getChatsRecientes().length);
        notifyObserversChatsRecientes();
        
        // ESTA ES LA PARTE MÁS IMPORTANTE
        if (contactoActual != null) {
            //System.out.println("--- CONTROLADOR: Notificando contacto actual: " + contactoActual.getNombre() + " con " + contactoActual.getMensajes().size() + " mensajes.");
        } else {
            //System.out.println("--- CONTROLADOR: Notificando contacto actual: NULL");
        }
        notifyObserversContactoActual(contactoActual);
        //System.out.println("--- CONTROLADOR: FIN NOTIFICACIÓN INICIAL ---");
    }
    
    // ########################################################################
    // SECCIÓN 2: LÓGICA DE USUARIO Y SESIÓN
    // ########################################################################
    
    public boolean iniciarSesion(String telefono, String password) {
        Usuario usuario = usuariosRegistrados.get(telefono);
        if (usuario != null && usuario.getPassword().equals(password)) {
            usuarioActual = usuario;
            refrescarEstadoDesdePersistencia();
            contactoActual = obtenerContactos().stream()
                .filter(c -> !c.getMensajes().isEmpty())
                .max(Comparator.comparing(this::getUltimoMensajeTiempo))
                .orElse(null);
            
            SwingUtilities.invokeLater(() -> {
                //System.out.println("--- CONTROLADOR: Notificación inicial encolada en EDT ---");
                notifyObservers();
            });

            return true;
        }
        return false;
    }

    public void cerrarSesion() {
        usuarioActual = null;
        contactoActual = null;
        PoolDAO.getInstancia().limpiarPool();
        notifyObserversChatsRecientes();
        notifyObserversContactoActual(null);
    }

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
    
    public boolean establecerStatus(String mensaje, String rutaImagen) {
		if (usuarioActual == null)
			return false;
		Status status = new Status(loadImageFromUrl(rutaImagen), mensaje);
		statusDAO.registrarEstado(status);
		return true;
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
    
    // ########################################################################
    // SECCIÓN 3: LÓGICA DE CONTACTOS Y GRUPOS
    // ########################################################################

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

 // AÑADIR ESTE NUEVO MÉTODO A Controlador.java

    /**
     * Devuelve una lista de todos los contactos que han sido explícitamente guardados
     * por el usuario, excluyendo los contactos desconocidos generados automáticamente.
     * Ideal para ventanas de gestión de contactos.
     * 
     * @return Una lista de Contactos conocidos (individuales y grupos).
     */
    public List<Contacto> obtenerContactosConocidos() {
        if (usuarioActual == null) {
            return Collections.emptyList();
        }
        
        return usuarioActual.getContactos().stream()
            // Filtramos para quedarnos solo con los que NO son desconocidos
            .filter(contacto -> {
                if (contacto instanceof ContactoIndividual) {
                    // Si es individual, solo lo incluimos si NO es desconocido
                    return !((ContactoIndividual) contacto).isDesconocido();
                }
                // Si es un Grupo, siempre lo incluimos
                return true; 
            })
            .sorted(Comparator.comparing(Contacto::getNombre)) // Opcional: devolver la lista ordenada
            .collect(Collectors.toList());
    }
    
    /*public boolean registrarContactoDesconocido(ContactoIndividual contactoDesconocido, String nuevoNombre) {
        if (usuarioActual == null || !contactoDesconocido.isDesconocido() || nuevoNombre.isBlank()) return false;
        if (existeContacto(nuevoNombre)) {
            JOptionPane.showMessageDialog(null, "Ya existe un contacto con el nombre '" + nuevoNombre + "'.", "Nombre duplicado", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        System.out.println("--- REGISTRANDO CONTACTO DESCONOCIDO ---");
        System.out.println("Contacto ANTES de modificar: Nombre=" + contactoDesconocido.getNombre() + ", EsDesconocido=" + contactoDesconocido.isDesconocido());
        contactoDesconocido.registrarComoConocido(nuevoNombre);
        System.out.println("Contacto DESPUÉS de modificar: Nombre=" + contactoDesconocido.getNombre() + ", EsDesconocido=" + contactoDesconocido.isDesconocido());
        contactoDAO.modificarContacto(contactoDesconocido);
        
        if (!usuarioActual.getContactos().contains(contactoDesconocido)) {
            usuarioActual.addContacto(contactoDesconocido);
        }
        usuarioDAO.modificarUsuario(usuarioActual);
        
        PoolDAO.getInstancia().removeObjeto(contactoDesconocido.getCodigo());
        PoolDAO.getInstancia().addObjeto(contactoDesconocido.getCodigo(), contactoDesconocido);
        
        // Leemos de nuevo el contacto DIRECTAMENTE desde la BD
        ContactoIndividual contactoLeido = contactoDAO.recuperarContacto(contactoDesconocido.getCodigo());
        
        if (contactoLeido != null) {
            //System.out.println("Contacto releído de BD: Nombre=" + contactoLeido.getNombre() + ", EsDesconocido=" + contactoLeido.isDesconocido());
        } else {
            //System.out.println("ERROR: No se pudo releer el contacto de la BD.");
        }
        notifyObserversListaContactos();
        notifyObserversChatsRecientes();
        notifyObserversContactoActual(contactoDesconocido);
        return true;
    }*/
    
    public boolean registrarContactoDesconocido(ContactoIndividual contactoDesconocido, String nuevoNombre) {
        if (usuarioActual == null || !contactoDesconocido.isDesconocido() || nuevoNombre.isBlank()) return false;
        if (existeContacto(nuevoNombre)) {
            JOptionPane.showMessageDialog(null, "Ya existe un contacto con el nombre '" + nuevoNombre + "'.", "Nombre duplicado", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        // Guardamos el ID antiguo antes de que se modifique
        int idAntiguo = contactoDesconocido.getCodigo();

        // Promovemos el contacto y lo modificamos en la BD (esto ahora borra y crea)
        contactoDesconocido.registrarComoConocido(nuevoNombre);
        contactoDAO.modificarContacto(contactoDesconocido); // Esto cambiará el ID del objeto contactoDesconocido
        
        // El ID nuevo ahora está en el objeto
        int idNuevo = contactoDesconocido.getCodigo();

        // ¡PASO CLAVE! Actualizamos la referencia en la lista de IDs del usuario
        if (usuarioActual.removeContactoID(idAntiguo)) {
            usuarioActual.addContactoID(idNuevo);
            usuarioDAO.modificarUsuario(usuarioActual); // Persistimos el cambio en el usuario
            LOGGER.info("Usuario actualizado para reemplazar ID de contacto " + idAntiguo + " por " + idNuevo);
        }
        
        // Las notificaciones ya estaban bien
        notifyObserversListaContactos();
        notifyObserversChatsRecientes();
        notifyObserversContactoActual(contactoDesconocido);
        return true;
    }
    
    public void crearGrupo(String nombre, List<ContactoIndividual> miembros) {
        if (usuarioActual == null || nombre.isBlank() || miembros.isEmpty() || existeContacto(nombre)) return;
        Grupo grupo = new Grupo(nombre, miembros, usuarioActual);
        grupoDAO.registrarGrupo(grupo);
        usuarioActual.addContacto(grupo);
        usuarioDAO.modificarUsuario(usuarioActual);
        notifyObserversListaContactos();
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
    // SECCIÓN 4: LÓGICA DE MENSAJERÍA
    // ########################################################################

    public void enviarMensaje(ContactoIndividual contactoDestino, String contenido) {
        Usuario emisor = usuarioActual;
        Mensaje mensaje = new Mensaje(contenido, LocalDateTime.now(), emisor, contactoDestino);
        mensajeDAO.registrarMensaje(mensaje);
        contactoDestino.sendMensaje(mensaje);
        crearContactoReversoSiNoExiste(emisor, contactoDestino);
        notifyObserversChatsRecientes();
        notifyObserversContactoActual(contactoActual);
    }
    
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
        //notifyObserversChatsRecientes();
	    //notifyObserversContactoActual(contactoActual);
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
    
    // ########################################################################
    // SECCIÓN 5: FUNCIONALIDADES PREMIUM Y EXTRAS
    // ########################################################################
    
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
            Font fontMiembro = FontFactory.getFont(FontFactory.COURIER, 10);
            Font fontMensajePropio = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, BaseColor.BLUE);
            Font fontMensajeAjeno = FontFactory.getFont(FontFactory.HELVETICA, 9, BaseColor.BLACK);
            Font fontFecha = FontFactory.getFont(FontFactory.HELVETICA, 8, BaseColor.GRAY);

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

    public Usuario getUsuarioActual() { return usuarioActual; }
    public Contacto getContactoActual() { return contactoActual; }
    public void setContactoActual(Contacto contacto) { this.contactoActual = contacto; notifyObserversContactoActual(contacto); }
    public Usuario buscarUsuarioPorTelefono(String telefono) { return usuariosRegistrados.get(telefono); }
    public List<Contacto> obtenerContactos() { return usuarioActual != null ? usuarioActual.getContactos() : Collections.emptyList(); }
    public Contacto obtenerContactoPorNombre(String nombre) { return obtenerContactos().stream().filter(c -> c.getNombre().equalsIgnoreCase(nombre)).findFirst().orElse(null); }
    public List<Mensaje> getMensajes(Contacto contacto) { return (contacto != null) ? contacto.getMensajes().stream().sorted().collect(Collectors.toList()) : Collections.emptyList(); }
    public String[] getChatsRecientes() {
        if (usuarioActual == null) {
            return new String[]{"No hay chats recientes"};
        }

        List<String> chats = usuarioActual.getContactos().stream()
                // 1. Filtrar solo los contactos que tienen mensajes
                .filter(c -> c != null && !c.getMensajes().isEmpty())
                // 2. Ordenarlos por el tiempo del último mensaje (más reciente primero)
                .sorted(Comparator.comparing(this::getUltimoMensajeTiempo).reversed())
                // 3. Mapear cada contacto a un String con el formato deseado
                .map(c -> "Chat con " + c.getNombre())
                // 4. Recolectar los resultados en una lista
                .collect(Collectors.toList());

        if (chats.isEmpty()) {
            return new String[]{"No hay chats recientes"};
        }
        
        // Convertir la lista a un array de Strings
        return chats.toArray(new String[0]);
    }
    public boolean existeContacto(String nombre) { if (usuarioActual == null) return false; return obtenerContactos().stream().anyMatch(c -> c.getNombre().equalsIgnoreCase(nombre.trim())); }
    public boolean existeContactoConTelefono(String telefono) { if (usuarioActual == null) return false; return obtenerContactos().stream().filter(c -> c instanceof ContactoIndividual).map(c -> (ContactoIndividual) c).anyMatch(c -> c.getTelefono().equals(telefono)); }
    private LocalDateTime getUltimoMensajeTiempo(Contacto contacto) { return contacto.getMensajes().stream().map(Mensaje::getHora).max(LocalDateTime::compareTo).orElse(LocalDateTime.MIN); }
    public String getNombreUserActual() { return (usuarioActual != null) ? usuarioActual.getNombre() : "Desconectado"; }
    public boolean isPremiumUserActual() { return (usuarioActual != null) && usuarioActual.isPremium(); }

    public ImageIcon getIconoUserActual() {
        if (usuarioActual == null) {
            // LOG 7: Si la vista pide el icono pero no hay usuario
            LOGGER.warning("[FOTO-DEBUG] getIconoUserActual llamado, pero usuarioActual es NULL.");
            return new ImageIcon();
        }
        
        ImageIcon foto = usuarioActual.getFoto();
        
        if (foto != null && foto.getIconWidth() > 0) {
            // LOG 7: Verificamos que estamos devolviendo un icono válido
            LOGGER.info("[FOTO-DEBUG] getIconoUserActual devuelve un ImageIcon VÁLIDO de " + foto.getIconWidth() + "x" + foto.getIconHeight());
        } else {
            // LOG 7: Verificamos si el icono es nulo o está vacío
            LOGGER.warning("[FOTO-DEBUG] getIconoUserActual devuelve un ImageIcon NULO o VACÍO. URL del usuario: " + usuarioActual.getUrlFoto());
        }
        
        return foto != null ? foto : new ImageIcon();
    }    
    
    public ContactoIndividual obtenerOcrearContactoParaTelefono(String telefono) {
        if (usuarioActual == null || telefono.isBlank()) return null;
        Optional<ContactoIndividual> contactoExistente = obtenerContactos().stream()
            .filter(c -> c instanceof ContactoIndividual).map(c -> (ContactoIndividual) c)
            .filter(c -> c.getTelefono().equals(telefono)).findFirst();
        if (contactoExistente.isPresent()) return contactoExistente.get();
        Usuario usuarioDestino = buscarUsuarioPorTelefono(telefono);
        if (usuarioDestino == null) {
            JOptionPane.showMessageDialog(null, "No existe ningún usuario con el teléfono " + telefono, "Usuario no encontrado", JOptionPane.WARNING_MESSAGE);
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

    public void addObserverChats(ObserverChats o) { if (!observersChats.contains(o)) observersChats.add(o); }
    public void removeObserverChats(ObserverChats o) { observersChats.remove(o); }
    public void addObserverContactos(ObserverContactos o) { if (!observersContactos.contains(o)) observersContactos.add(o); }
    public void removeObserverContactos(ObserverContactos o) { observersContactos.remove(o); }
    private void notifyObserversContactoActual(Contacto c) { observersChats.forEach(o -> o.updateContactoActual(c)); }
    private void notifyObserversListaContactos() { observersContactos.forEach(ObserverContactos::updateListaContactos); }
    private void notifyObserversChatsRecientes() { String[] chatsRecientes = getChatsRecientes(); observersChats.forEach(observer -> observer.updateChatsRecientes(chatsRecientes)); }
}