package umu.tds.app.AppChat;

import javax.swing.ImageIcon;

/**
 * Representa a un contacto individual, que puede ser conocido o desconocido.
 * Un contacto desconocido es aquel que se ha creado automáticamente al recibir un mensaje
 * de un número no registrado por el usuario.
 */
public class ContactoIndividual extends Contacto {
    private String telefono;
    private boolean esDesconocido;

    /**
     * Constructor para contactos creados explícitamente por el usuario.
     *
     * @param nombre El alias que el usuario le da al contacto.
     * @param telefono El número de teléfono del contacto.
     */
    public ContactoIndividual(String nombre, String telefono) {
        super(nombre);
        this.telefono = telefono;
        this.esDesconocido = false;
    }

    /**
     * Constructor para contactos desconocidos, creados automáticamente al recibir un mensaje.
     *
     * @param telefono El número de teléfono del contacto.
     */
    public ContactoIndividual(String telefono) {
        super(telefono); // Se usa el número como nombre por defecto
        this.telefono = telefono;
        this.esDesconocido = true;
    }

    /**
     * Devuelve el número de teléfono del contacto.
     *
     * @return El número de teléfono.
     */
    public String getTelefono() {
        return telefono;
    }

    /**
     * Indica si el contacto es desconocido (creado automáticamente).
     *
     * @return true si el contacto es desconocido, false si ha sido registrado por el usuario.
     */
    public boolean isDesconocido() {
        return esDesconocido;
    }

    /**
     * Convierte un contacto desconocido en uno conocido asignándole un nombre.
     *
     * @param nuevoNombre El nombre proporcionado por el usuario.
     */
    public void registrarComoConocido(String nuevoNombre) {
        this.setNombre(nuevoNombre);
        this.esDesconocido = false;
    }

    /**
     * Devuelve una imagen por defecto como icono de contacto.
     *
     * @return Un ImageIcon por defecto.
     */
    @Override
    public ImageIcon getFoto() {
        return new ImageIcon();
    }

    /**
     * Calcula el código hash del contacto basándose en el número de teléfono.
     *
     * @return Código hash único.
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((telefono == null) ? 0 : telefono.hashCode());
        return result;
    }

    /**
     * Compara este contacto con otro para determinar si son iguales.
     * Dos contactos son iguales si tienen el mismo número de teléfono.
     *
     * @param obj Objeto a comparar.
     * @return true si los contactos tienen el mismo número de teléfono.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        ContactoIndividual other = (ContactoIndividual) obj;
        if (telefono == null) {
            return other.telefono == null;
        } else {
            return telefono.equals(other.telefono);
        }
    }
}
