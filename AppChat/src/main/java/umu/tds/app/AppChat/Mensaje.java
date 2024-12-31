package umu.tds.app.AppChat;

import java.time.LocalDateTime;

/**
 * Clase que representa un mensaje en la aplicación de chat.
 * Implementa Comparable para permitir la ordenación de mensajes por fecha y hora.
 */
public class Mensaje implements Comparable<Mensaje> {
    /** Identificador único del mensaje */
    private int codigo;
    
    /** Contenido textual del mensaje */
    private String texto;
    
    /** Fecha y hora en que se envió el mensaje */
    private LocalDateTime hora;
    
    /** Código del emoticono usado en el mensaje */
    private int emoticono;
    
    /** Usuario que envía el mensaje */
    private Usuario emisor;
    
    /** Contacto que recibe el mensaje */
    private Contacto receptor;

    /**
     * Constructor para mensajes de texto.
     *
     * @param texto    Contenido del mensaje
     * @param hora     Fecha y hora del envío
     * @param emisor   Usuario que envía el mensaje
     * @param receptor Contacto que recibe el mensaje
     */
    public Mensaje(String texto, LocalDateTime hora, Usuario emisor, Contacto receptor) {
        this.texto = texto;
        this.hora = hora;
        this.setEmisor(emisor);
        this.setReceptor(receptor);
    }

    /**
     * Constructor para mensajes que solo contienen un emoticono.
     *
     * @param emoticono Código del emoticono a enviar
     * @param hora      Fecha y hora del envío
     * @param emisor    Usuario que envía el mensaje
     * @param receptor  Contacto que recibe el mensaje
     */
    public Mensaje(int emoticono, LocalDateTime hora, Usuario emisor, Contacto receptor) {
        this.texto = "";
        this.hora = hora;
        this.emoticono = emoticono;
        this.setEmisor(emisor);
        this.setReceptor(receptor);
    }

    /**
     * Constructor para mensajes que contienen texto y emoticono.
     *
     * @param texto     Contenido del mensaje
     * @param emoticono Código del emoticono
     * @param hora      Fecha y hora del mensaje
     */
    public Mensaje(String texto, int emoticono, LocalDateTime hora) {
        this.texto = texto;
        this.emoticono = emoticono;
        this.hora = hora;
    }

    /**
     * Obtiene el contenido textual del mensaje.
     *
     * @return Texto del mensaje
     */
    public String getTexto() {
        return texto;
    }

    /**
     * Obtiene la fecha y hora del mensaje.
     *
     * @return Fecha y hora en que se envió el mensaje
     */
    public LocalDateTime getHora() {
        return hora;
    }

    /**
     * Obtiene el código del emoticono del mensaje.
     *
     * @return Código del emoticono utilizado
     */
    public int getEmoticono() {
        return emoticono;
    }

    /**
     * Obtiene el usuario que envió el mensaje.
     *
     * @return Usuario emisor del mensaje
     */
    public Usuario getEmisor() {
        return emisor;
    }

    /**
     * Obtiene el contacto que recibió el mensaje.
     *
     * @return Contacto receptor del mensaje
     */
    public Contacto getReceptor() {
        return receptor;
    }

    /**
     * Obtiene el identificador único del mensaje.
     *
     * @return Código identificador del mensaje
     */
    public int getCodigo() {
        return codigo;
    }

    /**
     * Establece el identificador único del mensaje.
     *
     * @param codigo Nuevo código identificador
     */
    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    /**
     * Establece el contacto receptor del mensaje.
     *
     * @param receptor Nuevo contacto receptor
     */
    public void setReceptor(Contacto receptor) {
        this.receptor = receptor;
    }

    /**
     * Actualiza el contenido textual del mensaje.
     *
     * @param texto Nuevo texto del mensaje
     */
    public void setTexto(String texto) {
        this.texto = texto;
    }

    /**
     * Establece el usuario emisor del mensaje.
     *
     * @param emisor Nuevo usuario emisor
     */
    public void setEmisor(Usuario emisor) {
        this.emisor = emisor;
    }

    /**
     * Compara este mensaje con otro basándose en su fecha y hora.
     * Implementación requerida por la interfaz Comparable.
     *
     * @param o Mensaje con el que comparar
     * @return valor negativo si este mensaje es anterior,
     *         cero si son simultáneos,
     *         valor positivo si este mensaje es posterior
     */
    @Override
    public int compareTo(Mensaje o) {
        return hora.compareTo(o.hora);
    }
}