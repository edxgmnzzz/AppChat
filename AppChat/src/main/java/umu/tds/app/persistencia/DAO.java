package umu.tds.app.persistencia;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import umu.tds.app.AppChat.Persistencia;


/**
 * Interfaz DAO genérica
 *
 * @param <T> Tipo sobre el que se implementará la interfaz DAO. Debe
 *            implementar la interfaz {@link Persistencia}.
 */
public interface DAO<T extends Persistencia> {

	/**
	 * Separador utilizado en las representaciones de listas de ids de objetos
	 * Persistenciaes {@link DAO#PersistenciasToString(List)}.
	 */
	public static final String REPRESENTATION_STRING_SEPARATOR = " ";

	/**
	 * Dada una lista de objetos Persistenciaes obtiene una cadena de texto con las
	 * ids de los objetos de la lista.
	 *
	 * @param list Lista de la que se desea obtener la representacion en texto.
	 *
	 * @return Cadena de texto con ids de los objetos Persistenciaes.
	 */
	public static <T extends Persistencia> String PersistenciasToString(List<T> list) {
		return list.stream().map(p -> String.valueOf(p.getId()))
				.collect(Collectors.joining(REPRESENTATION_STRING_SEPARATOR));
	}

	/**
	 * Elimina una entidad registrada en persistencia.
	 *
	 * @param t Objeto que se desea eliminar de persistencia.
	 *
	 * @return {@code true} si la eliminación fue exitosa. {@code false} si el
	 *         objeto es {@code null} o no está registrado.
	 *
	 * @implNote La implementación por defecto proporciona la comprobación de
	 *           requisitos básicos comunes que, en su mayoría, comprobarían todas
	 *           las implementaciones de esta interfaz.
	 */
	public default boolean delete(T t) {

		// Checks obligatorios
		return (t != null && t.registrado());
		// Continuar en las implementaciones.
	}

	/**
	 * Modifica el objeto registrado para actualizar los datos Persistenciaes.
	 *
	 * @param t Objeto que se desea modificar en persistencia.
	 *
	 * @return {@code true} si la modificación fue exitosa. {@code false} si el
	 *         objeto es {@code null} o no está registrado.
	 *
	 * @implNote La implementación por defecto proporciona la comprobación de
	 *           requisitos básicos comunes que, en su mayoría, comprobarían todas
	 *           las implementaciones de esta interfaz.
	 */
	public default boolean modify(T t) {

		// Checks obligatorios
		return (t != null && t.registrado());
		// Continuar en las implementaciones.
	}

	/**
	 * Obtiene el objeto identificado mediante el id dado.
	 *
	 * @param id Identificador único del objeto que se desea recuperar.
	 *
	 * @return Devuelve el objeto identificado por id en persistencia en el caso de
	 *         estar registrado.
	 */
	public Optional<T> recover(int id);

	/**
	 * Obtiene una lista con todos los objetos registrados.
	 *
	 * @return Lista de solo lectura de todos los objetos de este tipo registrados.
	 */
	public List<T> recoverAll();

	/**
	 * Registra un nuevo objeto Persistenciae y establece la id Persistenciae del objeto
	 * proporcionado.
	 *
	 * @param t Objeto que se desea registrar y que recibirá una id.
	 *
	 * @return {@code true} si el registro es exitoso. {@code false} si t es
	 *         {@code null}, su id es mayor a 0 (solo se permite registrar objetos
	 *         sin id positivas) o ya se encontraba registrado.
	 *
	 * @implNote La implementación por defecto proporciona la comprobación de
	 *           requisitos básicos comunes que, en su mayoría, comprobarían todas
	 *           las implementaciones de esta interfaz.
	 */
	public default boolean register(T t) {

		// Checks iniciales obligatorios.
		return (t != null && !t.registrado());
		// Continuar checks y registro en implementaciones.
	}

	/**
	 * Obtiene una lista de objetos Persistencia a partir de una cadena de texto que
	 * contiene la ids de los objetos a recuperar.
	 *
	 * @param representation Cadena que indica los objetos que recuperar. La cadena
	 *                       debe haberse generado mediante
	 *                       {@link DAO#PersistenciasToString(List)}
	 *
	 * @return Lista de objetos recuperados. Si algun id de la cadena de texto no
	 *         estaba registrado en persistencia o se falla en obtención es
	 *         ignorado.
	 */
	
	public default List<T> stringToPersistents(String representation) {
		return Arrays.stream(representation.split(REPRESENTATION_STRING_SEPARATOR)).filter(s -> !s.isEmpty())
				.map(id -> recover(Integer.valueOf(id))).filter(o -> o.isPresent()) // Ignorar fallos de obtención.
				.map(o -> o.get()).toList();
	}
	
}