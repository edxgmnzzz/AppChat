package umu.tds.app.persistencia;

import java.util.List;

import umu.tds.app.AppChat.Grupo;

public interface GrupoDAO {

		public void registrarGrupo(Grupo grupo);
		public void borrarGrupo(Grupo grupo);
		public void modificarGrupo(Grupo grupo);
		public Grupo recuperarGrupo(int codigo);
		public List<Grupo> recuperarTodosGrupos();
}

