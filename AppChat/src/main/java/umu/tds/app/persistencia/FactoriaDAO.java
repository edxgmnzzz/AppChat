package umu.tds.app.persistencia;

public abstract class FactoriaDAO {
	/**
	 * Enumerado que lista las implementaciones de factorias para las diferentes
	 * familias de adaptadores DAO.
	 */
	public enum ImplementacionDAO {
		/**
		 * Familia de adapatadores DAO que utiliza el driver de persistencia de TDS.
		 *
		 * {@link TDSFactoriaDAO}
		 */
		TDS_FAMILY
	}

	private static FactoriaDAO instance = null;

	private static final String unimplemented = "Factoría de familia no implementada";

	/**
	 * Obtiene la instancia única de la factoria de adaptadores. Si es la primera
	 * vez que se invoca se inicializará la instancia única utilizando la familia
	 * {@link ImplementacionDAO#TDS_FAMILY}.
	 *
	 * @return Instancia única de la fabrica de adaptadores.
	 */
	public static FactoriaDAO getInstance() {
		if (instance != null) {
			return instance;
		}
		return getInstance(ImplementacionDAO.TDS_FAMILY);
	}

	/**
	 * Retorna la instancia única de la factoria de adaptadores o establece una
	 * instacia de la familia de adaptadores deseada como instancia única en caso de
	 * no haber sido inicializada.
	 *
	 * @param implementation Familia de adaptadores DAO que se desea que produzca la
	 *                       fábrica obtenida.
	 *
	 * @return Fábrica de adaptadores actual o la fábrica de adaptadores de la
	 *         familia especificada en el caso de que la instancia única no esté
	 *         inicializada.
	 *
	 */
	public static FactoriaDAO getInstance(ImplementacionDAO implementation) {

		if (instance != null) {
			return instance;
		}

		switch (implementation) {
		case TDS_FAMILY:
			instance = new TDSFactoriaDAO();
			break;
		default:
			throw new RuntimeException(unimplemented);
		}

		return instance;
	}

}
