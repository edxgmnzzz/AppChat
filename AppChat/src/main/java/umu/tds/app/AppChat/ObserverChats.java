package umu.tds.app.AppChat;


public interface ObserverChats {
    void updateChatsRecientes(String[] chatsRecientes);
    void updateContactoActual(Contacto contacto);
    //void updateListaContactos(); // Nuevo método
}
