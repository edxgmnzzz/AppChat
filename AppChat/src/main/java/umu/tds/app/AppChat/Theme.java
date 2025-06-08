package umu.tds.app.AppChat;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

public class Theme {
    // 🎨 Paleta de colores vibrante y moderna
    public static final Color COLOR_FONDO = new Color(255, 234, 167);          // Amarillo pastel (fondo general)
    public static final Color COLOR_HEADER = new Color(116, 185, 255);         // Azul cielo para encabezados
    public static final Color COLOR_PRINCIPAL = new Color(72, 219, 251);       // Azul brillante (botones principales)
    public static final Color COLOR_SECUNDARIO = new Color(255, 255, 255);     // Blanco puro (fondos de campos)
    public static final Color COLOR_ACENTO = new Color(255, 118, 117);         // Coral vibrante (errores y bordes)
    public static final Color COLOR_TEXTO = new Color(45, 52, 54);             // Gris oscuro (texto general)
    public static final Color COLOR_PLACEHOLDER = new Color(149, 165, 166);    // Gris suave para hints
    public static final Color COLOR_HOVER = new Color(253, 203, 110);          // Naranja suave (hover botones)

    public static final Color COLOR_BUBBLE_SENT = new Color(129, 236, 236);    // Burbujas enviadas (verde-azul)
    public static final Color COLOR_BUBBLE_RECEIVED = new Color(200, 214, 229);// Burbujas recibidas (gris azul)
    public static final Color COLOR_CHAT_BACKGROUND = new Color(245, 246, 250); 
    public static final Color COLOR_MESSAGE_TEXT = new Color(45, 52, 54);

    public static final Color COLOR_SHADOW = new Color(0, 0, 0, 40);

    // 📐 Dimensiones y tamaños
    public static final int LOGIN_WINDOW_WIDTH = 400;
    public static final int LOGIN_WINDOW_HEIGHT = 400;
    public static final int MAIN_WINDOW_WIDTH = 900;
    public static final int MAIN_WINDOW_HEIGHT = 650;

    public static final Dimension BUTTON_SIZE = new Dimension(110, 35);
    public static final int BORDER_RADIUS = 20;
    public static final int TITLE_BAR_HEIGHT = 40;
    public static final int SPLIT_PANE_DIVIDER = 260;

    public static final int PADDING_SMALL = 8;
    public static final int PADDING_MEDIUM = 15;
    public static final int PADDING_LARGE = 25;

    // 🔤 Fuentes
    public static final Font FONT_BOLD_LARGE = new Font("SansSerif", Font.BOLD, 20);
    public static final Font FONT_BOLD_MEDIUM = new Font("SansSerif", Font.BOLD, 16);
    public static final Font FONT_PLAIN_MEDIUM = new Font("SansSerif", Font.PLAIN, 14);
}