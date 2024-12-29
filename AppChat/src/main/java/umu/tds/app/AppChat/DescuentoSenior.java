package umu.tds.app.AppChat;

public class DescuentoSenior implements Descuento {
	@Override
	public double getDescuento(double precioInicial) {
		return 0.9 * precioInicial;
	}
}