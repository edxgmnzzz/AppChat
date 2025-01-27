package umu.tds.app.persistencia;

public class TDSFactoriaDAO extends FactoriaDAO {

	public TDSFactoriaDAO () {
	}
	
	@Override
	public GrupoDAO getGrupoDAO() {
		return AdaptadorGrupoTDS.getInstancia();
	}

	@Override
	public IndividualContactDAO getContactoIndividualDAO() {
		return AdaptadorIndividualContactTDS.getInstancia();
	}

	@Override
	public MessageDAO getMensajeDAO() {
		return AdaptadorMessageTDS.getInstancia();
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
