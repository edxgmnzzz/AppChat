package umu.tds.app.AppChat;

import java.time.LocalDate;

/**
 * Clase que permite calcular descuentos aplicables a un usuario
 * en función de su antigüedad y actividad dentro de la aplicación.
 */
public class CalculadoraDescuentos {

    private Usuario usuario;
    private Controlador controlador;

    /**
     * Crea una nueva instancia de la calculadora de descuentos.
     *
     * @param usuario El usuario al que se le aplicarán los descuentos.
     * @param controlador El controlador para acceder a información como los mensajes enviados.
     */
    public CalculadoraDescuentos(Usuario usuario, Controlador controlador) {
        this.usuario = usuario;
        this.controlador = controlador;
    }

    /**
     * Calcula el precio final aplicando los descuentos disponibles para el usuario,
     * e informa de los descuentos aplicados en un resumen textual.
     *
     * @param precioInicial El precio base antes de aplicar descuentos.
     * @return Un resumen con los descuentos aplicados y el precio final.
     */
    public String calcularDescuentos(double precioInicial) {
        double precio = precioInicial;
        StringBuilder resumen = new StringBuilder();
        resumen.append("Precio base: ").append(precioInicial).append("€\n");

        // Descuento por antigüedad
        DescuentoPorIntervaloRegistro descAntiguedad = new DescuentoPorIntervaloRegistro(
            usuario, 0.20, LocalDate.of(2024, 12, 1), LocalDate.of(2025, 12, 31)
        );
        double nuevoPrecio = descAntiguedad.getDescuento(precio);
        if (nuevoPrecio != precio) {
            resumen.append("- Descuento por fecha (20%): -").append(precio - nuevoPrecio).append("€\n");
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
