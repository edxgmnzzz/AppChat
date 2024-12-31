package umu.tds.app.AppChat;

import javax.swing.ImageIcon;
import java.util.Objects;

/**
 * Clase que representa el estado de un usuario en la aplicación de chat.
 * Un estado consiste en una imagen y una frase que el usuario puede personalizar.
 */
public class Status {
    /** Identificador único del estado */
    private int codigo;

    /** Imagen asociada al estado */
    private ImageIcon img;

    /** Frase o texto descriptivo del estado */
    private String frase;

    /**
     * Crea un nuevo estado con una imagen y una frase.
     *
     * @param img   Imagen que representa el estado
     * @param frase Texto descriptivo del estado
     * @throws IllegalArgumentException Si alguno de los parámetros es nulo
     */
    public Status(ImageIcon img, String frase) {
        if (img == null || frase == null) {
            throw new IllegalArgumentException("La imagen y la frase no pueden ser nulas");
        }
        this.img = img;
        this.frase = frase;
    }

    /**
     * Obtiene la imagen asociada al estado.
     *
     * @return Imagen del estado
     */
    public ImageIcon getImg() {
        return img;
    }

    /**
     * Establece la imagen asociada al estado.
     *
     * @param img Nueva imagen del estado
     * @throws IllegalArgumentException Si la imagen es nula
     */
    public void setImg(ImageIcon img) {
        if (img == null) {
            throw new IllegalArgumentException("La imagen no puede ser nula");
        }
        this.img = img;
    }

    /**
     * Obtiene el texto descriptivo del estado.
     *
     * @return Frase del estado
     */
    public String getFrase() {
        return frase;
    }

    /**
     * Establece el texto descriptivo del estado.
     *
     * @param frase Nueva frase del estado
     * @throws IllegalArgumentException Si la frase es nula
     */
    public void setFrase(String frase) {
        if (frase == null) {
            throw new IllegalArgumentException("La frase no puede ser nula");
        }
        this.frase = frase;
    }

    /**
     * Obtiene el identificador único del estado.
     *
     * @return Código identificador del estado
     */
    public int getCodigo() {
        return codigo;
    }

    /**
     * Establece el identificador único del estado.
     *
     * @param codigo Nuevo código identificador
     */
    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    /**
     * Calcula el código hash del estado basado en sus propiedades.
     *
     * @return Código hash calculado
     */
    @Override
    public int hashCode() {
        return Objects.hash(codigo, frase, img == null ? null : img.getDescription());
    }

    /**
     * Compara si dos estados son iguales basándose en su código, frase e imagen.
     *
     * @param obj Objeto a comparar
     * @return true si los estados son iguales, false en caso contrario
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Status other = (Status) obj;
        return codigo == other.codigo &&
               Objects.equals(frase, other.frase) &&
               Objects.equals(img == null ? null : img.getDescription(), 
                              other.img == null ? null : other.img.getDescription());
    }

    /**
     * Genera una representación en texto del estado incluyendo su código,
     * descripción de la imagen y frase.
     *
     * @return Cadena que representa el estado
     */
    @Override
    public String toString() {
        String imgDescription = img == null ? "null" : img.getDescription();
        return "Status [codigo=" + codigo + ", img=" + imgDescription + ", frase=" + frase + "]";
    }
}
