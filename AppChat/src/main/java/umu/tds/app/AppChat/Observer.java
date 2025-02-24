package umu.tds.app.AppChat;


public interface Observer {
    void updateChatsRecientes(String[] chatsRecientes);
    void updateContactoActual(Contacto contacto);
    void updateListaContactos(); // Nuevo método
}
