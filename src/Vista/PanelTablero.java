package Vista;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * PanelTablero — componente visual del tablero de ajedrez.
 *
 * Dibuja el tablero 8x8 y los caballos encima.
 * También maneja el clic del usuario para elegir la posición inicial.
 *
 * Valores del tablero (int[][]):
 *   0 = celda vacía
 *   1 = caballo puesto por el algoritmo  → se pinta en ROJO
 *   2 = caballo inicial del usuario      → se pinta en AZUL
 */

public class PanelTablero extends JPanel {

    // --- Estado Interno --

    /** Matriz que representa el tablero. El controlador la actualiza con setTablero() */
    private int[][] tablero;

    /* Posición inical elegida por el usuario, es -1 si no ha elegido nada */
    private int filaInicial = -1;
    private int colInicial  = -1;

    /*Cuando es true, el panel respinde a los clics del mouse para que el
    * usuario elija una celda. se activa con activarModoSeleccion()*/
    private boolean modoSeleccion = false;

    private static final Color CELDA_CLARA            = new Color(240, 217, 181);
    private static final Color CELDA_OSCURA           = new Color(181, 136, 99);
    private static final Color COLOR_CABALLO_INICIAL  = new Color(30, 144, 255);
    private static final Color COLOR_CABALLO_ALGORITMO = new Color(220, 50, 50);
    private static final Color COLOR_HIGHLIGHT        = new Color(100, 200, 100, 120);

    /*Celda sobre la que esta el mouse en modo seleccion*/
    private int filaHover = -1;
    private int colHover  = -1;

    // ----- Constructor -----
    public PanelTablero() {
        tablero = new int[8][8]; //Empieza vacío
        setBackground(new Color(40, 40, 40));
        setPreferredSize(new Dimension(560, 560)); //Tamaño fijo para que se dibuje bien

        // -- Hover: Resalta la celda cuando el mouse pasa por encima ---
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (!modoSeleccion) return;
                int[] celda = getCeldaDesde(e.getX(), e.getY());
                if (celda != null) { filaHover = celda[0]; colHover = celda[1]; }
                else               { filaHover = -1;       colHover = -1; }
                repaint(); //Vuelve  a dibuja para mostrar el hover
            }
        });

        // Clic : Registra la posición inicial elegida por el usuario
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!modoSeleccion) return;
                int[] celda = getCeldaDesde(e.getX(), e.getY());
                if (celda != null) {
                    filaInicial = celda[0];
                    colInicial  = celda[1];
                    modoSeleccion = false;
                    filaHover = -1;
                    colHover  = -1;
                    setCursor(Cursor.getDefaultCursor());
                    repaint();
                    // Notifica a VentanaPrincipal que el usuario ya eligió una celda.
                    // VentanaPrincipal escucha este evento con addPropertyChangeListener.
                    firePropertyChange("posicionElegida", null, new int[]{filaInicial, colInicial});
                }
            }
        });
    }

    // --- API pública ----

    /** Recibe el tablero resuelto por el algoritmo y lo muestra en pantalla. */

    public void setTablero(int[][] tablero) {
        this.tablero = tablero;
        repaint(); // redibuja el panel con los nuevos valores
    }

    /*Activa el modo selección: el panel espera que el usuario haga clic
     * en una celda para fijar la posición inicial del caballo.
     * VentanaPrincipal llama esto cuando el usuario pulsa "Elegir posición inicial".
     */
    public void activarModoSeleccion() {
        limpiarTablero();
        modoSeleccion = true;
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // cursor de manito
        repaint();
    }

    /**
     * Resetea el tablero a su estado vacío.
     * VentanaPrincipal llama esto cuando el usuario pulsa "Limpiar tablero".
     */
    public void limpiarTablero() {
        tablero       = new int[8][8];
        filaInicial   = -1;
        colInicial    = -1;
        modoSeleccion = false;
        filaHover     = -1;
        colHover      = -1;
        setCursor(Cursor.getDefaultCursor());
        repaint();
    }

    /**
     * Marca una celda como caballo inicial (valor 2) para pintarla en azul.
     * Se llama justo después de que el usuario elige la celda, antes de resolver.
     */
    public void marcarPosicionInicial(int fila, int col) {
        filaInicial        = fila;
        colInicial         = col;
        tablero[fila][col] = 2; // 2 = caballo inicial → azul
        repaint();
    }

    /** Retorna la fila de la posición inicial elegida por el usuario. */
    public int getFilaInicial() { return filaInicial; }

    /** Retorna la columna de la posición inicial elegida por el usuario. */
    public int getColInicial()  { return colInicial; }

    // ----------------------------------------------------------------
    //  Pintado — Java llama este método cada vez que hay que redibujar
    // ----------------------------------------------------------------

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Activa antialiasing para que los bordes se vean suaves
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Calcula el tamaño del tablero como cuadrado perfecto y lo centra
        int size    = Math.min(getWidth(), getHeight()) - 20;
        int offsetX = (getWidth()  - size) / 2;
        int offsetY = (getHeight() - size) / 2;
        int cell    = size / 8; // tamaño de cada celda

        // Recorre las 64 celdas y dibuja cada una
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                int x = offsetX + j * cell;
                int y = offsetY + i * cell;

                // Color alternado tipo ajedrez
                g2.setColor((i + j) % 2 == 0 ? CELDA_CLARA : CELDA_OSCURA);
                g2.fillRect(x, y, cell, cell);

                // Hover verde cuando el mouse está encima en modo selección
                if (modoSeleccion && i == filaHover && j == colHover) {
                    g2.setColor(COLOR_HIGHLIGHT);
                    g2.fillRect(x, y, cell, cell);
                }

                // Dibuja el caballo según el valor de la celda
                if (tablero[i][j] == 2) {
                    dibujarCaballo(g2, x, y, cell, COLOR_CABALLO_INICIAL);   // azul = inicial
                } else if (tablero[i][j] == 1) {
                    dibujarCaballo(g2, x, y, cell, COLOR_CABALLO_ALGORITMO); // rojo = algoritmo
                }
            }
        }

        // Mensaje de instrucción en la parte inferior cuando está en modo selección
        if (modoSeleccion) {
            g2.setFont(new Font("SansSerif", Font.BOLD, 14));
            String msg = "Haz clic en una celda para colocar el caballo inicial";
            int tw = g2.getFontMetrics().stringWidth(msg);
            g2.setColor(new Color(0, 0, 0, 140));
            g2.fillRoundRect((getWidth() - tw) / 2 - 8, offsetY + size + 6, tw + 16, 24, 8, 8);
            g2.setColor(new Color(100, 220, 100));
            g2.drawString(msg, (getWidth() - tw) / 2, offsetY + size + 22);
        }
    }

    // ----------------------------------------------------------------
    //  Métodos privados de apoyo
    // ----------------------------------------------------------------

    /**
     * Dibuja el símbolo ♞ centrado dentro de la celda.
     * Usa drawString con un carácter Unicode en lugar de una imagen.
     * Primero dibuja una sombra negra desplazada 2px para dar profundidad.
     */
    private void dibujarCaballo(Graphics2D g2, int x, int y, int cell, Color color) {
        int fontSize = (int) (cell * 0.65);
        g2.setFont(new Font("Serif", Font.PLAIN, fontSize));
        FontMetrics fm   = g2.getFontMetrics();
        String      simbolo = "\u265E"; // ♞

        // Centra el símbolo dentro de la celda
        int sx = x + (cell - fm.stringWidth(simbolo)) / 2;
        int sy = y + (cell + fm.getAscent() - fm.getDescent()) / 2 - 2;

        // Sombra
        g2.setColor(new Color(0, 0, 0, 100));
        g2.drawString(simbolo, sx + 2, sy + 2);

        // Caballo con el color correspondiente
        g2.setColor(color);
        g2.drawString(simbolo, sx, sy);
    }

    /**
     * Convierte coordenadas de píxeles (clic del mouse) a índices del tablero.
     * Retorna {fila, col} o null si el clic fue fuera del tablero.
     */
    private int[] getCeldaDesde(int px, int py) {
        int size    = Math.min(getWidth(), getHeight()) - 20;
        int offsetX = (getWidth()  - size) / 2;
        int offsetY = (getHeight() - size) / 2;
        int cell    = size / 8;

        int col  = (px - offsetX) / cell;
        int fila = (py - offsetY) / cell;

        if (fila >= 0 && fila < 8 && col >= 0 && col < 8) return new int[]{fila, col};
        return null; // clic fuera del tablero
    }
}