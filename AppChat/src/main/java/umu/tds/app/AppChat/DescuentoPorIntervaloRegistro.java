package umu.tds.app.AppChat;

import java.time.LocalDate;

public class DescuentoPorIntervaloRegistro implements Descuento {
    private final Usuario usuario;
    private final double porcentaje;
    private final LocalDate fechaInicio;
    private final LocalDate fechaFin;

    public DescuentoPorIntervaloRegistro(Usuario usuario, double porcentaje, LocalDate fechaInicio, LocalDate fechaFin) {
        this.usuario = usuario;
        this.porcentaje = porcentaje;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }

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
