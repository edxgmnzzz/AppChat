// Controlador con persistencia completa integrada y funcionalidad completa

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
    
    private Map<String, Usuario> usuariosRegistrados;
    private List<Contacto> contactos;
    private Usuario usuarioActual;
    private Contacto contactoActual;
    private List<ObserverChats> observersChats;
    private List<ObserverContactos> observersContactos;
    private AdaptadorUsuarioTDS usuarioDAO;
    private AdaptadorContactoIndividualTDS contactoDAO;
    private AdaptadorGrupoTDS grupoDAO;
    private AdaptadorMensajeTDS mensajeDAO;
    private AdaptadorStatusTDS statusDAO;

    private Controlador() {
        initialize();
    }

    private void initialize() {
        LOGGER.info("Inicializando Controlador...");
        usuarioDAO = AdaptadorUsuarioTDS.getInstancia();
        contactoDAO = AdaptadorContactoIndividualTDS.getInstancia();
        grupoDAO = AdaptadorGrupoTDS.getInstancia();
        mensajeDAO = AdaptadorMensajeTDS.getInstancia();
        statusDAO = AdaptadorStatusTDS.getInstancia();

        usuariosRegistrados = new HashMap<>();
        contactos = new ArrayList<>();
        observersChats = new ArrayList<>();
        observersContactos = new ArrayList<>();

        LOGGER.info("Cargando usuarios desde la base de datos...");
        for (Usuario u : usuarioDAO.recuperarTodosUsuarios()) {
            try {
                String urlFoto = u.getUrlFoto();
                LOGGER.info("Procesando usuario: " + u.getTelefono() + ", URL foto: " + urlFoto);
                if (urlFoto != null && !urlFoto.isBlank() && !"null".equalsIgnoreCase(urlFoto)) {
                    u.setFoto(loadImageFromUrl(urlFoto));
                } else {
                    u.setFoto(new ImageIcon());
                    LOGGER.warning("URL de foto vacía o nula para usuario: " + u.getTelefono());
                }
            } catch (Exception e) {
                LOGGER.warning("Fallo al cargar imagen para usuario " + u.getTelefono() + ": " + e.getMessage());
                u.setFoto(new ImageIcon());
            }
            usuariosRegistrados.put(u.getTelefono(), u);
        }

        LOGGER.info("Cargando contactos individuales y grupos...");
        try {
            List<ContactoIndividual> individuales = contactoDAO.recuperarTodosContactos();
            LOGGER.info("Se han recuperado " + (individuales != null ? individuales.size() : 0) + " contactos individuales.");

            List<Grupo> grupos = grupoDAO.recuperarTodosGrupos();
            LOGGER.info("Se han recuperado " + (grupos != null ? grupos.size() : 0) + " grupos.");

            if (individuales != null) {
                contactos.addAll(individuales);
                LOGGER.info("Contactos individuales añadidos a la lista de contactos.");
            }

            if (grupos != null) {
                for (Grupo g : grupos) {
                    LOGGER.info("Grupo cargado: " + g.getNombre() + " con " + g.getParticipantes().size() + " miembros.");
                }
                contactos.addAll(grupos);
                LOGGER.info("Grupos añadidos a la lista de contactos.");
            } else {System.out.println("No hay grupos socio");}
        } catch (Exception e) {
            LOGGER.severe("❌ Error al cargar contactos o grupos: " + e.getMessage());
        }


        LOGGER.info("Cargando mensajes...");
        try {
            List<Mensaje> mensajes = mensajeDAO.recuperarTodosMensajes();
            System.out.println("Mensajes cargados desde la base de datos: " + mensajes.size());
            for (Mensaje mensaje : mensajes) {
                // Asignar al receptor
                Contacto receptorReal = contactos.stream()
                    .filter(c -> c.getCodigo() == mensaje.getReceptor().getCodigo())
                    .findFirst()
                    .orElse(null);
                if (receptorReal != null) {
                    receptorReal.sendMensaje(mensaje);
                    System.out.println("Asignado mensaje a receptor: " + receptorReal.getNombre());
                } else {
                    LOGGER.warning("No se encontró contacto real para receptor con ID: " 
                                   + mensaje.getReceptor().getCodigo());
                }

                // Asignar al emisor (null-safe para contactos sin usuario)
                Contacto emisorReal = contactos.stream()
                    .filter(c -> c instanceof ContactoIndividual ci
                                 && ci.getUsuario() != null
                                 && ci.getUsuario().getTelefono().equals(mensaje.getEmisor().getTelefono()))
                    .findFirst()
                    .orElse(null);
                if (emisorReal != null) {
                    emisorReal.sendMensaje(mensaje);
                    System.out.println("Asignado mensaje a emisor: " + emisorReal.getNombre());
                } else {
                    LOGGER.warning("No se encontró contacto real para emisor con teléfono: " 
                                   + mensaje.getEmisor().getTelefono());
                }
            }
        } catch (Exception e) {
            LOGGER.severe("Error al cargar mensajes: " + e.getMessage());
        }

        LOGGER.info("Determinando contacto más reciente para selección inicial...");
        contactoActual = contactos.stream()
            .filter(c -> !c.getMensajes().isEmpty())
            .max(Comparator.comparing(this::getUltimoMensajeTiempo))
            .orElse(null);
        notifyObserversChatsRecientes();
        notifyObserversContactoActual(contactoActual);
        LOGGER.info("Inicialización completada.");
    }

    public static Controlador getInstancia() {
        return instancia;
    }
    public boolean iniciarSesion(String telefono, String password) {
        if (telefono == null || password == null) return false;
        Usuario usuario = usuariosRegistrados.get(telefono);
        if (usuario != null && usuario.getPassword().equals(password)) {
            usuarioActual = usuario;
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
				LOGGER.info("Intentando registrar usuario: " + nombreUsuario + ", teléfono: " + telefono);
				String error = validateRegistration(nombreReal, nombreUsuario, password, confirmarPassword, email, telefono);
				if (error != null) {
				LOGGER.warning("Registro fallido: " + error);
				JOptionPane.showMessageDialog(null, error, "Error", JOptionPane.ERROR_MESSAGE);
				return false;
				}
				ImageIcon profileIcon = loadImageFromUrl(rutaFoto);
				Usuario nuevoUsuario = new Usuario(telefono, nombreReal, password, email, saludo, profileIcon, false);
				nuevoUsuario.setUrlFoto(rutaFoto);
				usuarioDAO.registrarUsuario(nuevoUsuario);
				usuariosRegistrados.put(telefono, nuevoUsuario);
				LOGGER.info("Usuario registrado correctamente: " + nombreUsuario);
				notifyObserversChatsRecientes();
				return true;
	}

    private String validateRegistration(String nombreReal, String nombreUsuario, String password,
            String confirmarPassword, String email, String telefono) {
		if (nombreReal.isEmpty() || nombreUsuario.isEmpty() || password.isEmpty() || email.isEmpty() || telefono.isEmpty()) {
		return "Por favor, complete todos los campos obligatorios";
		}
		if (!password.equals(confirmarPassword)) {
		return "Las contraseñas no coinciden";
		}
		if (usuariosRegistrados.containsKey(telefono)) {
		return "El número de teléfono ya está registrado";
		}
		if (usuariosRegistrados.values().stream().anyMatch(u -> u.getEmail().equals(email))) {
		return "El correo electrónico ya está registrado";
		}
		return null;
		}

    public boolean actualizarUsuario(String nuevoNombre, String nuevaPassword, String nuevoSaludo, String rutaFoto) {
        if (usuarioActual == null || nuevaPassword == null || nuevaPassword.trim().isEmpty()) return false;
        usuarioActual.setName(nuevoNombre);
        usuarioActual.setPassword(nuevaPassword);
        usuarioActual.setSaludo(nuevoSaludo);
        usuarioActual.setFoto(loadImageFromUrl(rutaFoto));
        usuarioDAO.modificarUsuario(usuarioActual);
        notifyObserversChatsRecientes();
        return true;
    }
    public ImageIcon loadImageFromUrl(String urlString) {
        if (urlString == null || urlString.isBlank() || urlString.equalsIgnoreCase("null")) {
            LOGGER.warning("URL inválida para cargar imagen: " + urlString);
            return new ImageIcon();
        }

        try {
            URI uri = new URI(urlString);
            URL url = uri.toURL();
            BufferedImage image = ImageIO.read(url);
            if (image != null) {
                ImageIcon icon = new ImageIcon(image);
                Image scaledImage = icon.getImage().getScaledInstance(PROFILE_IMAGE_SIZE, PROFILE_IMAGE_SIZE, Image.SCALE_SMOOTH);
                LOGGER.info("Imagen cargada correctamente desde URL: " + urlString);
                return new ImageIcon(scaledImage);
            } else {
                LOGGER.warning("ImageIO.read devolvió null para URL: " + urlString);
            }
        } catch (Exception e) {
            LOGGER.severe("Error al cargar imagen desde URL " + urlString + ": " + e.getMessage());
        }
        return new ImageIcon();
    }

    public void enviarMensaje(Contacto contacto, String texto) {
        if (usuarioActual == null || contacto == null || texto == null || texto.trim().isEmpty()) return;
        if (contacto instanceof Grupo grupo) {
            for (ContactoIndividual c : grupo.getParticipantes()) {
                enviarMensaje(c, texto);
            }
            return;
        }
        Mensaje mensaje = new Mensaje(texto, LocalDateTime.now(), usuarioActual, contacto);
        contacto.sendMensaje(mensaje);
        mensajeDAO.registrarMensaje(mensaje);
        notifyObserversChatsRecientes();
        notifyObserversContactoActual(contacto);
    }

    public boolean establecerStatus(String mensaje, String rutaImagen) {
        if (usuarioActual == null) return false;
        ImageIcon icon = loadImageFromUrl(rutaImagen);
        Status status = new Status(icon, mensaje);
        statusDAO.registrarEstado(status);
        return true;
    }

    public boolean agregarContacto(ContactoIndividual contacto) {
        if (contacto == null || existeContacto(contacto.getNombre())) return false;
        contactos.add(contacto);
        contactoDAO.registrarContacto(contacto);
        notifyObserversChatsRecientes();
        notifyObserversListaContactos();
        return true;
    }

    public void eliminarContacto(ContactoIndividual contacto) {
        if (contacto != null && contactos.remove(contacto)) {
            notifyObserversChatsRecientes();
            notifyObserversListaContactos();
        }
    }

    public void crearGrupo(String nombre, List<ContactoIndividual> miembros) {
        LOGGER.info("Intentando crear grupo: " + nombre + " con " + miembros.size() + " miembros.");
        
        if (usuarioActual == null || nombre == null || nombre.trim().isEmpty() || miembros == null || miembros.isEmpty()) {
            LOGGER.warning("Datos inválidos para crear grupo.");
            return;
        }
        if (contactos.stream().anyMatch(c -> c.getNombre().equalsIgnoreCase(nombre))) {
            LOGGER.warning("Ya existe un contacto con nombre: " + nombre);
            return;
        }

        Grupo grupo = new Grupo(nombre, 0, miembros, usuarioActual);
        contactos.add(grupo);

        LOGGER.info("Registrando grupo en la base de datos con código: " + 0);
        grupoDAO.registrarGrupo(grupo);  // <- Este debe funcionar
        notifyObserversChatsRecientes();
        notifyObserversListaContactos();
    }

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

    public Contacto obtenerContactoPorNombre(String nombre) {
        return contactos.stream().filter(c -> c.getNombre().equalsIgnoreCase(nombre)).findFirst().orElse(null);
    }

    public ContactoIndividual obtenerContactoPorUsuario(Usuario usuario) {
        return usuarioActual.getContactos().stream()
            .filter(c -> c instanceof ContactoIndividual ci && ci.getUsuario().equals(usuario))
            .map(c -> (ContactoIndividual) c).findFirst().orElse(null);
    }

    public List<Contacto> obtenerContactos() {
        return Collections.unmodifiableList(contactos);
    }

    public boolean nuevoContacto(ContactoIndividual contacto) {
        return agregarContacto(contacto);
    }

    public boolean existeContacto(String nombre) {
        return contactos.stream().anyMatch(c -> c.getNombre().equalsIgnoreCase(nombre));
    }

    public boolean existeUsuario(String telefono) {
        return usuariosRegistrados.containsKey(telefono);
    }
    public List<String> obtenerMensajes(Contacto contacto) {
        if (contacto == null || usuarioActual == null) {
            System.out.println("Error: contacto o usuarioActual es null");
            return Collections.emptyList();
        }
        List<String> mensajes = contacto.getMensajes().stream()
            .sorted(Comparator.comparing(Mensaje::getHora))
            .map(msg -> {
                boolean enviadoPorMi = msg.getEmisor().getTelefono().equals(usuarioActual.getTelefono());
                String autor = enviadoPorMi ? "Tú" : msg.getEmisor().getName();
                String texto = msg.getTexto();
                return autor + ": " + texto;
            })
            .collect(Collectors.toList());
        System.out.println("Mensajes para " + contacto.getNombre() + ": " + mensajes);
        return mensajes;
    }
    public String[] getChatsRecientes() {
        return contactos.stream()
            .filter(c -> !c.getMensajes().isEmpty())
            .sorted((a, b) ->
                getUltimoMensajeTiempo(b).compareTo(getUltimoMensajeTiempo(a)))
            .map(c -> "Chat con " + c.getNombre())
            .toArray(String[]::new);
    }




    private LocalDateTime getUltimoMensajeTiempo(Contacto contacto) {
        if (contacto == null) return LocalDateTime.MIN;
        return contacto.getMensajes().stream().map(Mensaje::getHora).max(LocalDateTime::compareTo).orElse(LocalDateTime.MIN);
    }

    public void addObserverChats(ObserverChats o) {
        if (!observersChats.contains(o)) observersChats.add(o);
    }

    public void removeObserverChats(ObserverChats o) {
        observersChats.remove(o);
    }

    public void addObserverContactos(ObserverContactos o) {
        if (!observersContactos.contains(o)) observersContactos.add(o);
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

    public String getNombreUserActual() {
        return usuarioActual != null ? usuarioActual.getName() : "Usuario no autenticado";
    }

    public ImageIcon getIconoUserActual() {
        return usuarioActual != null ? usuarioActual.getFoto() : new ImageIcon();
    }

    public int getNumTelefonoUserActual() {
        return usuarioActual != null ? Integer.parseInt(usuarioActual.getTelefono()) : -1;
    }

    public String getEmailUserActual() {
        return usuarioActual != null ? usuarioActual.getEmail() : "";
    }

    public boolean isPremiumUserActual() {
        return usuarioActual != null && usuarioActual.isPremium();
    }

    public Map<String, Usuario> getusuariosRegistrados() {
        return usuariosRegistrados;
    }

    public int generarCodigoContacto() {
        while (true) {
            int codigo = (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
            final int finalCodigo = codigo; 
            if (contactos.stream().noneMatch(c -> c.getCodigo() == finalCodigo)) {
                return finalCodigo;
            }
        }
    }
    
    public boolean exportarPdfConDatos(String rutaDestino) {
        if (usuarioActual == null || !usuarioActual.isPremium()) {
            JOptionPane.showMessageDialog(null, "Solo los usuarios Premium pueden exportar a PDF.", "Acceso denegado", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(rutaDestino));
            document.open();

            Font tituloFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
            Font seccionFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
            Font normalFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);

            document.add(new Paragraph("Exportación de datos AppChat", tituloFont));
            document.add(new Paragraph("Usuario: " + usuarioActual.getName() + " (" + usuarioActual.getTelefono() + ")\n\n", normalFont));

            // Exportar contactos individuales
            document.add(new Paragraph("Contactos Individuales", seccionFont));
            for (Contacto c : obtenerContactos()) {
                if (c instanceof ContactoIndividual ci) {
                    document.add(new Paragraph("Nombre: " + ci.getNombre(), normalFont));
                    document.add(new Paragraph("Teléfono: " + ci.getTelefono() + "\n", normalFont));
                }
            }

            // Exportar grupos
            document.add(new Paragraph("\nGrupos", seccionFont));
            for (Contacto c : obtenerContactos()) {
                if (c instanceof Grupo grupo) {
                    document.add(new Paragraph("Grupo: " + grupo.getNombre(), normalFont));
                    for (ContactoIndividual miembro : grupo.getParticipantes()) {
                        document.add(new Paragraph("  - " + miembro.getNombre() + " (" + miembro.getTelefono() + ")", normalFont));
                    }
                    document.add(new Paragraph("\n", normalFont));
                }
            }

            // Exportar mensajes de conversaciones individuales
            document.add(new Paragraph("\nMensajes Intercambiados", seccionFont));
            for (Contacto c : obtenerContactos()) {
                if (c instanceof ContactoIndividual ci) {
                    document.add(new Paragraph("Conversación con: " + ci.getNombre(), normalFont));

                    List<String> mensajes = obtenerMensajes(ci);
                    for (String mensaje : mensajes) {
                        document.add(new Paragraph("  " + mensaje, normalFont));
                    }
                    document.add(new Paragraph("\n", normalFont));
                }
            }

            document.close();
            JOptionPane.showMessageDialog(null, "PDF generado correctamente en: " + rutaDestino, "Éxito", JOptionPane.INFORMATION_MESSAGE);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al generar el PDF: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    public Usuario buscarUsuarioPorTelefono(String telefono) {
        return usuariosRegistrados.get(telefono);
    }
    
    public void activarPremiumConDescuento() {
        if (usuarioActual == null) return;

        double precioBase = 100.0;

        CalculadoraDescuentos calculadora = new CalculadoraDescuentos(usuarioActual, this);
        String resultado = calculadora.calcularDescuentos(precioBase);

        int confirm = JOptionPane.showConfirmDialog(null, resultado + "\n¿Desea activar Premium?", 
                                                     "Resumen de descuentos", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            usuarioActual.setPremium(true);
            usuarioDAO.modificarUsuario(usuarioActual);  // Persistimos el cambio
            JOptionPane.showMessageDialog(null, "¡Felicidades! Ya eres usuario Premium.");
        }
    }
    public List<Mensaje> buscarMensajes(String textoFiltro, String telefonoFiltro, String nombreFiltro) {
        List<Mensaje> resultados = new ArrayList<>();

        for (Contacto contacto : obtenerContactos()) {
            for (Mensaje mensaje : contacto.getMensajes()) {
                boolean cumpleTexto = (textoFiltro == null || textoFiltro.isBlank()) 
                        || mensaje.getTexto().toLowerCase().contains(textoFiltro.toLowerCase());

                boolean cumpleTelefono = (telefonoFiltro == null || telefonoFiltro.isBlank()) 
                        || mensaje.getEmisor().getTelefono().equals(telefonoFiltro) 
                        || (mensaje.getReceptor() instanceof ContactoIndividual ci && ci.getTelefono().equals(telefonoFiltro));

                boolean cumpleNombre = (nombreFiltro == null || nombreFiltro.isBlank())
                        || mensaje.getEmisor().getName().equalsIgnoreCase(nombreFiltro)
                        || (mensaje.getReceptor() instanceof ContactoIndividual ci && ci.getNombre().equalsIgnoreCase(nombreFiltro))
                        || (mensaje.getReceptor() instanceof Grupo g && g.getNombre().equalsIgnoreCase(nombreFiltro));

                if (cumpleTexto && cumpleTelefono && cumpleNombre) {
                    resultados.add(mensaje);
                }
            }
        }

        // Puedes devolver ordenado si lo deseas
        resultados.sort(Comparator.comparing(Mensaje::getHora));
        return resultados;
    }



    public int contarMensajesDelUsuario(Usuario usuario) {
        return (int) obtenerContactos().stream()
                .flatMap(c -> c.getMensajes().stream())
                .filter(m -> m.getEmisor().equals(usuario))
                .count();
    }



} 