package umu.tds.app.persistencia;

public abstract class FactoriaDAO {
	private static FactoriaDAO INSTANCE;

	public static final String DAO_TDS = "umu.tds.apps.persistencia.TDSFactoriaDAO";

	/**
	 * Crea un tipo de factoria DAO. Solo existe el tipo TDSFactoriaDAO
	 */
	public static FactoriaDAO getInstancia(String nombre) throws DAOException {
	    if (INSTANCE == null) {
	        try {
	            INSTANCE = (FactoriaDAO) Class.forName(nombre).getDeclaredConstructor().newInstance();
	        } catch (Exception e) {
	            throw new DAOException("No se pudo instanciar la clase: " + nombre, e);
	        }
	    }
	    return INSTANCE;
	}


	public static FactoriaDAO getInstancia() throws DAOException {
		if (INSTANCE == null)
			return getInstancia(FactoriaDAO.DAO_TDS);
		else
			return INSTANCE;
	}

	/* Constructor */
	protected FactoriaDAO() {
	}

	// Metodos factoria que devuelven adaptadores que implementen estos interfaces
	public abstract GrupoDAO getGrupoDAO();

	public abstract ContactoIndividualDAO getContactoIndividualDAO();

	public abstract MensajeDAO getMensajeDAO();

	public abstract StatusDAO getEstadoDAO();
	
	public abstract UsuarioDAO getUsuarioDAO();

}