package umu.tds.app.AppChat;

import java.time.LocalDate;
import java.time.Period;

public class DescuentoAntiguedad implements Descuento {
    private Usuario usuario;
    private double porcentaje;

    public DescuentoAntiguedad(Usuario usuario, double porcentaje) {
        this.usuario = usuario;
        this.porcentaje = porcentaje;
    }

    @Override
    public double getDescuento(double precioInicial) {
        if (usuario.getFechaRegistro() == null) return precioInicial;
        Period periodo = Period.between(usuario.getFechaRegistro(), LocalDate.now());
        if (periodo.getMonths() >= 6) { // Ejemplo: más de 6 meses registrado
            return precioInicial * (1 - porcentaje);
        }
        return precioInicial;
    }
}
