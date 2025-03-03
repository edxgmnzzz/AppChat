package umu.tds.app.AppChat;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

public class Theme {
    // Colores existentes
    public static final Color COLOR_FONDO = new Color(41, 128, 185);      // Azul medio (fondo principal)
    public static final Color COLOR_PRINCIPAL = new Color(52, 152, 219);  // Azul claro (elementos principales)
    public static final Color COLOR_SECUNDARIO = new Color(236, 240, 241); // Gris claro (fondo secundario)
    public static final Color COLOR_ACENTO = new Color(231, 76, 60);       // Rojo coral (acentos)
    public static final Color COLOR_TEXTO = Color.WHITE;                   // Texto principal
    public static final Color COLOR_PLACEHOLDER = Color.GRAY;              // Texto placeholder

    // Nuevos colores para estética
    public static final Color COLOR_HOVER = new Color(46, 139, 87);        // Verde oscuro (hover y enviado)
    public static final Color COLOR_BUBBLE_SENT = new Color(144, 238, 144); // Verde claro (burbujas enviadas)
    public static final Color COLOR_BUBBLE_RECEIVED = new Color(211, 211, 211); // Gris claro (burbujas recibidas)
    public static final Color COLOR_HEADER = new Color(33, 37, 41);        // Gris oscuro (encabezados)
    public static final Color COLOR_SHADOW = new Color(0, 0, 0, 50);       // Sombra ligera

    // Dimensiones de ventanas
    public static final int LOGIN_WINDOW_WIDTH = 400;
    public static final int LOGIN_WINDOW_HEIGHT = 350;
    public static final int MAIN_WINDOW_WIDTH = 800;
    public static final int MAIN_WINDOW_HEIGHT = 600;

    // Dimensiones de componentes
    public static final Dimension BUTTON_SIZE = new Dimension(100, 30);
    public static final int BORDER_RADIUS = 10;
    public static final int TITLE_BAR_HEIGHT = 40;
    public static final int SPLIT_PANE_DIVIDER = 250;

    // Paddings y márgenes
    public static final int PADDING_SMALL = 5;
    public static final int PADDING_MEDIUM = 10;
    public static final int PADDING_LARGE = 20;

    // Fuentes
    public static final Font FONT_BOLD_LARGE = new Font("Arial", Font.BOLD, 18);
    public static final Font FONT_BOLD_MEDIUM = new Font("Arial", Font.BOLD, 14);
    public static final Font FONT_PLAIN_MEDIUM = new Font("Arial", Font.PLAIN, 14);
	public static final Color COLOR_CHAT_BACKGROUND = Color.WHITE;
	public static final Color COLOR_MESSAGE_TEXT = Color.BLACK;
}