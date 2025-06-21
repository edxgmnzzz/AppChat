package umu.tds.app.AppChat;

import java.time.LocalDate;

/**
 * Implementación de un descuento que se aplica si el usuario se registró
 * dentro de un intervalo de fechas determinado.
 */
public class DescuentoPorIntervaloRegistro implements Descuento {

    private final Usuario usuario;
    private final double porcentaje;
    private final LocalDate fechaInicio;
    private final LocalDate fechaFin;

    /**
     * Crea un descuento basado en la fecha de registro del usuario.
     *
     * @param usuario      Usuario al que se le aplica el descuento.
     * @param porcentaje   Porcentaje de descuento (por ejemplo, 0.20 para 20%).
     * @param fechaInicio  Fecha de inicio del intervalo válido para aplicar el descuento.
     * @param fechaFin     Fecha de fin del intervalo válido para aplicar el descuento.
     */
    public DescuentoPorIntervaloRegistro(Usuario usuario, double porcentaje, LocalDate fechaInicio, LocalDate fechaFin) {
        this.usuario = usuario;
        this.porcentaje = porcentaje;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }

    /**
     * Calcula el precio final aplicando el descuento si la fecha de registro del usuario
     * se encuentra dentro del intervalo establecido.
     *
     * @param precioInicial Precio antes del descuento.
     * @return Precio con descuento si aplica, o el precio original si no.
     */
    @Override
    public double getDescuento(double precioInicial) {
        LocalDate fechaRegistro = usuario.getFechaRegistro();
        if (fechaRegistro == null) return precioInicial;

        if ((fechaRegistro.isEqual(fechaInicio) || fechaRegistro.isAfter(fechaInicio)) &&
            (fechaRegistro.isEqual(fechaFin) || fechaRegistro.isBefore(fechaFin))) {
            return precioInicial * (1 - porcentaje);
        }

        return precioInicial;
    }
}
