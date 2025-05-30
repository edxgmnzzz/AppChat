package umu.tds.app.AppChat;

public class CalculadoraDescuentos {

    private Usuario usuario;
    private Controlador controlador;

    public CalculadoraDescuentos(Usuario usuario, Controlador controlador) {
        this.usuario = usuario;
        this.controlador = controlador;
    }

    public String calcularDescuentos(double precioInicial) {
        double precio = precioInicial;
        StringBuilder resumen = new StringBuilder();
        resumen.append("Precio base: ").append(precioInicial).append("€\n");

        // Descuento por antigüedad
        DescuentoAntiguedad descAntiguedad = new DescuentoAntiguedad(usuario, 0.20);
        double nuevoPrecio = descAntiguedad.getDescuento(precio);
        if (nuevoPrecio != precio) {
            resumen.append("- Descuento antigüedad (20%): -").append(precio - nuevoPrecio).append("€\n");
            precio = nuevoPrecio;
        }

        // Descuento por mensajes
        int totalMensajes = controlador.contarMensajesDelUsuario(usuario);
        DescuentoPorMensajes descMensajes = new DescuentoPorMensajes(totalMensajes, 0.10);
        nuevoPrecio = descMensajes.getDescuento(precio);
        if (nuevoPrecio != precio) {
            resumen.append("- Descuento mensajes (10%): -").append(precio - nuevoPrecio).append("€\n");
            precio = nuevoPrecio;
        }

        resumen.append("Precio final: ").append(precio).append("€");
        return resumen.toString();
    }
}
