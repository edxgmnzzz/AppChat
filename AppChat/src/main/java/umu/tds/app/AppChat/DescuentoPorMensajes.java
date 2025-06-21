package umu.tds.app.AppChat;

/**
 * Descuento que se aplica cuando el usuario ha enviado un número mínimo
 * de mensajes dentro de la aplicación.
 */
public class DescuentoPorMensajes implements Descuento {

    private final int totalMensajes;
    private final double porcentaje;

    /**
     * Construye un descuento basado en el número total de mensajes enviados.
     *
     * @param totalMensajes Número de mensajes enviados por el usuario.
     * @param porcentaje    Porcentaje de descuento a aplicar (por ejemplo, 0.10 para 10 %).
     */
    public DescuentoPorMensajes(int totalMensajes, double porcentaje) {
        this.totalMensajes = totalMensajes;
        this.porcentaje = porcentaje;
    }

    /**
     * Calcula el precio tras aplicar el descuento si procede.
     * <p>
     * El descuento se activa si {@code totalMensajes} es mayor o igual que 3.
     *
     * @param precioInicial Precio antes del descuento.
     * @return Precio con descuento si se cumplen las condiciones; de lo contrario, el precio original.
     */
    @Override
    public double getDescuento(double precioInicial) {
        if (totalMensajes >= 3) {
            return precioInicial * (1 - porcentaje);
        }
        return precioInicial;
    }
}

