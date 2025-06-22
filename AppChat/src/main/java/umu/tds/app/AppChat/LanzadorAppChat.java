package umu.tds.app.AppChat;

import umu.tds.app.persistencia.AdaptadorUsuarioTDS;
import umu.tds.app.ventanas.VentanaLogin;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import java.awt.image.BufferedImage;
import java.net.URI;

public class LanzadorAppChat {

    public static void main(String[] args) {
        realizarSimulacionInicialSiEsNecesario();

        SwingUtilities.invokeLater(() -> new VentanaLogin().setVisible(true));
    }

    private static void realizarSimulacionInicialSiEsNecesario() {
        AdaptadorUsuarioTDS usuarioDAO = AdaptadorUsuarioTDS.getInstancia();

        if (!usuarioDAO.recuperarTodosUsuarios().isEmpty()) {
            return;
        }

        try {
            String path = "https://widget-assets.geckochat.io/69d33e2bd0ca2799b2c6a3a3870537a9.png";
            BufferedImage image = ImageIO.read(new URI(path.trim()).toURL());
            ImageIcon foto = new ImageIcon(image);

            Usuario florentino = new Usuario("600111222", "Florentino Pérez", "pass1", "f@p.com", "Hala Madrid", foto, false);
            florentino.setUrlFoto(path);
            usuarioDAO.registrarUsuario(florentino);

            Usuario laporta = new Usuario("600333444", "Joan Laporta", "pass2", "j@l.com", "Visca Barça", foto, true);
            laporta.setUrlFoto(path);
            usuarioDAO.registrarUsuario(laporta);

            Usuario cerezo = new Usuario("600555666", "Enrique Cerezo", "pass3", "e@c.com", "Aupa Atleti", null, true);
            usuarioDAO.registrarUsuario(cerezo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
