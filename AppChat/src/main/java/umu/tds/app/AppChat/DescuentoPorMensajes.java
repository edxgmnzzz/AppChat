package umu.tds.app.AppChat;
public class DescuentoPorMensajes implements Descuento {
    private int totalMensajes;
    private double porcentaje;

    public DescuentoPorMensajes(int totalMensajes, double porcentaje) {
        this.totalMensajes = totalMensajes;
        this.porcentaje = porcentaje;
    }

    @Override
    public double getDescuento(double precioInicial) {
        if (totalMensajes >= 100) { // Ejemplo: si envió más de 100 mensajes
            return precioInicial * (1 - porcentaje);
        }
        return precioInicial;
    }
}
