package umu.tds.app.persistencia;

import java.util.*;

import javax.swing.ImageIcon;

import beans.Entidad;
import beans.Propiedad;
import tds.driver.FactoriaServicioPersistencia;
import tds.driver.ServicioPersistencia;
import umu.tds.app.AppChat.Status;

public class AdaptadorStatusTDS implements StatusDAO {

	private static ServicioPersistencia sp;
	private static AdaptadorStatusTDS unicaInstancia = null;
	private static final String ENTIDAD_STATUS = "estado";

	private AdaptadorStatusTDS() {
		sp = FactoriaServicioPersistencia.getInstance().getServicioPersistencia();
	}

	public static AdaptadorStatusTDS getInstancia() {
		if (unicaInstancia == null)
			unicaInstancia = new AdaptadorStatusTDS();
		return unicaInstancia;
	}

	@Override
	public void registrarEstado(Status status) {
		if (status == null || status.getCodigo() > 0) return;

		Entidad eStatus = new Entidad();
		eStatus.setNombre(ENTIDAD_STATUS);
		eStatus.setPropiedades(List.of(
				new Propiedad("mensaje", status.getFrase()),
				new Propiedad("imagen", status.getImg().getDescription() != null ? status.getImg().getDescription() : "")
		));

		eStatus = sp.registrarEntidad(eStatus);
		status.setCodigo(eStatus.getId());
	}

	@Override
	public void borrarEstado(Status status) {
		Entidad e = sp.recuperarEntidad(status.getCodigo());
		sp.borrarEntidad(e);
	}

	@Override
	public void modificarEstado(Status status) {
		Entidad e = sp.recuperarEntidad(status.getCodigo());
		sp.eliminarPropiedadEntidad(e, "mensaje");
		sp.anadirPropiedadEntidad(e, "mensaje", status.getFrase());
		sp.eliminarPropiedadEntidad(e, "imagen");
		sp.anadirPropiedadEntidad(e, "imagen", status.getImg().getDescription() != null ? status.getImg().getDescription() : "");
	}

	@Override
	public Status recuperarEstado(int codigo) {
		Entidad e = sp.recuperarEntidad(codigo);
		String mensaje = sp.recuperarPropiedadEntidad(e, "mensaje");
		String ruta = sp.recuperarPropiedadEntidad(e, "imagen");

		ImageIcon icon = new ImageIcon();
		if (ruta != null && !ruta.isEmpty()) {
			icon = new ImageIcon(ruta);
			icon.setDescription(ruta);
		}

		Status status = new Status(icon, mensaje);
		status.setCodigo(codigo);
		return status;
	}

	@Override
	public List<Status> recuperarTodosEstados() {
		List<Entidad> entidades = sp.recuperarEntidades(ENTIDAD_STATUS);
		List<Status> estados = new ArrayList<>();
		for (Entidad e : entidades) {
			estados.add(recuperarEstado(e.getId()));
		}
		return estados;
	}
}
