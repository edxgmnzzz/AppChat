package umu.tds.app.AppChat;


public class DescuentoJunior implements Descuento {
	@Override
	public double getDescuento(double precioInicial) {
		return 0.6 * precioInicial;
	}
}