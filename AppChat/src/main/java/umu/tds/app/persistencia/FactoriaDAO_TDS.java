package umu.tds.app.persistencia;

public class FactoriaDAO_TDS extends FactoriaDAO {
	public FactoriaDAO_TDS () {
	}
	
	@Override
	public GrupoDAO getGrupoDAO() {
		return AdaptadorGrupoTDS.getInstancia();
	}

	@Override
	public ContactoIndividualDAO getContactoIndividualDAO() {
		return AdaptadorContactoIndividualTDS.getInstancia();
	}

	@Override
	public MensajeDAO getMensajeDAO() {
		return AdaptadorMensajeTDS.getInstancia();
	}

	@Override
	public StatusDAO getEstadoDAO() {
		return AdaptadorStatusTDS.getInstancia();
	}

	@Override
	public UsuarioDAO getUsuarioDAO() {
		return AdaptadorUsuarioTDS.getInstancia();
	}

}