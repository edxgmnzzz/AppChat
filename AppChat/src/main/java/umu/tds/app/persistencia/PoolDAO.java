package umu.tds.app.persistencia;

import java.util.Hashtable;
import java.util.Map;

/**
 * Clase PoolDAO que actúa como caché de objetos recuperados de la base de datos,
 * evitando recuperaciones redundantes. Implementa el patrón Singleton.
 */
public class PoolDAO {

	private static PoolDAO unicaInstancia;
	private Hashtable<Integer, Object> pool;

	/**
	 * Constructor privado que inicializa el pool.
	 */
	private PoolDAO() {
		pool = new Hashtable<>();
	}

	/**
	 * Devuelve la instancia única del PoolDAO.
	 * @return la instancia singleton del PoolDAO.
	 */
	public static PoolDAO getInstancia() {
		if (unicaInstancia == null) {
			unicaInstancia = new PoolDAO();
		}
		return unicaInstancia;
	}

	/**
	 * Recupera un objeto del pool dado su ID.
	 * @param id identificador del objeto.
	 * @return el objeto si está presente, o null en caso contrario.
	 */
	public Object getObjeto(int id) {
		return pool.get(id);
	}

	/**
	 * Añade un objeto al pool. Si ya existía uno con el mismo ID, lo reemplaza.
	 * @param id identificador del objeto.
	 * @param objeto objeto a almacenar en el pool.
	 */
	public void addObjeto(int id, Object objeto) {
		if (objeto != null) {
			pool.put(id, objeto);
		}
	}

	/**
	 * Verifica si un objeto con el ID dado está en el pool.
	 * @param id identificador del objeto.
	 * @return true si está presente, false en caso contrario.
	 */
	public boolean contiene(int id) {
		return pool.containsKey(id);
	}

	/**
	 * Elimina un objeto del pool dado su ID.
	 * @param id identificador del objeto a eliminar.
	 */
	public void removeObjeto(int id) {
		pool.remove(id);
	}

	/**
	 * Limpia todos los objetos del pool.
	 */
	public void limpiarPool() {
		pool.clear();
	}

	/**
	 * Método opcional para depuración: imprime el contenido actual del pool.
	 * @param callerContext contexto que invoca la depuración.
	 */
	public void imprimirContenidoPool(String callerContext) {
		System.out.println("----- [PoolDAO] Contenido actual del Pool (desde: " + callerContext + ") -----");
		if (pool.isEmpty()) {
			System.out.println("[PoolDAO] El pool está vacío.");
			return;
		}
		for (Map.Entry<Integer, Object> entry : pool.entrySet()) {
			Integer id = entry.getKey();
			Object obj = entry.getValue();
			System.out.println("[PoolDAO] ID: " + id + " -> Objeto: " + (obj != null ? obj.getClass().getSimpleName() : "null"));
		}
		System.out.println("----- [PoolDAO] Fin del contenido del Pool -----");
	}
}
