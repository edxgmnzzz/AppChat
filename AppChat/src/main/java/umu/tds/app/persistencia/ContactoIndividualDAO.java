package umu.tds.app.persistencia;

import java.util.List;
import umu.tds.app.AppChat.ContactoIndividual;

public interface ContactoIndividualDAO {
	public ContactoIndividual registrarContacto(ContactoIndividual contact);
	public void borrarContacto(ContactoIndividual contact);
	public void modificarContacto(ContactoIndividual contact);
	public ContactoIndividual recuperarContacto(int codigo);
	public List<ContactoIndividual> recuperarTodosContactos();
}