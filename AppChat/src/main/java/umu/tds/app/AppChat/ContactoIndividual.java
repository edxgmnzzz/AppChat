package umu.tds.app.AppChat;

import javax.swing.ImageIcon;

public class ContactoIndividual extends Contacto {
    private String telefono;
    private boolean esDesconocido; // Flag para saber si fue creado automáticamente

    /**
     * Constructor para contactos creados explícitamente por el usuario.
     * @param nombre El alias que el usuario le da al contacto.
     * @param telefono El número de teléfono del contacto.
     */
    public ContactoIndividual(String nombre, String telefono) {
        super(nombre);
        this.telefono = telefono;
        this.esDesconocido = false; // Un contacto creado con nombre no es desconocido
    }

    /**
     * Constructor para contactos "desconocidos", creados automáticamente al recibir un mensaje.
     * @param telefono El número de teléfono del contacto.
     */
    public ContactoIndividual(String telefono) {
        super(telefono); // Por defecto, el nombre es el propio teléfono
        this.telefono = telefono;
        this.esDesconocido = true;
    }

    public String getTelefono() {
        return telefono;
    }

    public boolean isDesconocido() {
        return esDesconocido;
    }

    /**
     * "Promueve" un contacto desconocido a uno conocido, asignándole un nombre definitivo.
     * @param nuevoNombre El nombre proporcionado por el usuario.
     */
    public void registrarComoConocido(String nuevoNombre) {
        this.setNombre(nuevoNombre); // setNombre es heredado de la clase Contacto
        this.esDesconocido = false;
    }

    // Este método debería ser manejado por la vista llamando al controlador
    @Override
    public ImageIcon getFoto() {
        // La vista debería obtener el usuario a través del controlador y luego su foto.
        // Devolvemos un icono por defecto para que el modelo no dependa del controlador.
        return new ImageIcon(); 
    }
    
    // hashCode y equals se basan en el teléfono, que es el identificador único del otro usuario.
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((telefono == null) ? 0 : telefono.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        ContactoIndividual other = (ContactoIndividual) obj;
        if (telefono == null) {
            if (other.telefono != null) return false;
        } else if (!telefono.equals(other.telefono)) return false;
        return true;
    }
}