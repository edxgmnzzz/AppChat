package umu.tds.app.persistencia;

// Cache de la base de datos
import java.util.Hashtable;
import java.util.Map; // Importar Map
import java.util.logging.Level; // Para niveles de log
import java.util.logging.Logger; // Importar Logger

public class PoolDAO {
	private static PoolDAO unicaInstancia;
	private Hashtable<Integer, Object> pool; // Los objetos Entidad se guardan aquí
	private static final Logger LOGGER = Logger.getLogger(PoolDAO.class.getName());

	static {
		// Configurar el nivel de logging para este logger si es necesario
		// LOGGER.setLevel(Level.INFO); // O Level.FINE, Level.ALL para más detalle
	}

	private PoolDAO() {
		pool = new Hashtable<Integer, Object>();
		LOGGER.info("PoolDAO inicializado.");
	}

	public static PoolDAO getInstancia() {
		if (unicaInstancia == null) {
			unicaInstancia = new PoolDAO();
		}
		return unicaInstancia;
	}

	public Object getObjeto(int id) {
		Object obj = pool.get(id);
		if (obj != null) {
			LOGGER.fine("[PoolDAO] Objeto recuperado del pool con ID " + id + ": " + obj.getClass().getSimpleName());
		} else {
			LOGGER.warning("[PoolDAO] Objeto NO encontrado en el pool con ID " + id);
		}
		return obj;
	}

	public void addObjeto(int id, Object objeto) {
		if (objeto == null) {
			LOGGER.warning("[PoolDAO] Intento de añadir objeto nulo al pool con ID " + id);
			return;
		}
		// Si el objeto ya existe, se reemplaza. Hashtable.put() hace esto.
		Object oldObj = pool.put(id, objeto);
		if (oldObj != null) {
			LOGGER.info("[PoolDAO] Objeto REEMPLAZADO en el pool con ID " + id + ". Nuevo: " + objeto.getClass().getSimpleName());
		} else {
			LOGGER.info("[PoolDAO] Objeto AÑADIDO al pool con ID " + id + ": " + objeto.getClass().getSimpleName());
		}

		// Para depuración, imprimir detalles si es una Entidad
		if (objeto instanceof beans.Entidad) {
			beans.Entidad entidad = (beans.Entidad) objeto;
			LOGGER.fine("[PoolDAO] Detalles de la Entidad (ID " + id + ", Nombre=" + entidad.getNombre() + ") en el pool:");
			for (beans.Propiedad p : entidad.getPropiedades()) {
				LOGGER.fine("    Propiedad: " + p.getNombre() + " = '" + p.getValor() + "'");
			}
		}
	}

	public boolean contiene(int id) {
		boolean contiene = pool.containsKey(id);
		LOGGER.fine("[PoolDAO] Pool contiene ID " + id + ": " + contiene);
		return contiene;
	}

	public void removeObjeto(int id) {
		Object obj = pool.remove(id);
		if (obj != null) {
			LOGGER.info("[PoolDAO] Objeto eliminado del pool con ID " + id + ": " + obj.getClass().getSimpleName());
		} else {
			LOGGER.warning("[PoolDAO] Intento de eliminar objeto no existente del pool con ID " + id);
		}
	}

	// Método para depuración: imprimir todo el contenido del pool
	public void imprimirContenidoPool(String callerContext) {
		LOGGER.info("----- [PoolDAO] Contenido Actual del PoolDAO (llamado desde: " + callerContext + ") -----");
		if (pool.isEmpty()) {
			LOGGER.info("[PoolDAO] El pool está vacío.");
			return;
		}
		for (Map.Entry<Integer, Object> entry : pool.entrySet()) {
			Integer id = entry.getKey();
			Object obj = entry.getValue();
			if (obj instanceof beans.Entidad) {
				beans.Entidad entidad = (beans.Entidad) obj;
				LOGGER.info("[PoolDAO] ID: " + id + " -> Entidad: " + entidad.getNombre());
				for (beans.Propiedad p : entidad.getPropiedades()) {
					LOGGER.info("    " + p.getNombre() + ": '" + p.getValor() + "'");
				}
			} else if (obj != null) {
				LOGGER.info("[PoolDAO] ID: " + id + " -> Objeto: " + obj.getClass().getSimpleName());
			} else {
				LOGGER.info("[PoolDAO] ID: " + id + " -> Objeto: null");
			}
		}
		LOGGER.info("----- [PoolDAO] Fin del Contenido del PoolDAO -----");
	}

	public void limpiarPool() {
		pool.clear();
		LOGGER.info("[PoolDAO] PoolDAO ha sido limpiado.");
	}
}