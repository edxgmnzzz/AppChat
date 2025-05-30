package umu.tds.app.AppChat;

public class SinDescuento implements Descuento {
    @Override
    public double getDescuento(double precioInicial) {
        return precioInicial;
    }
}

