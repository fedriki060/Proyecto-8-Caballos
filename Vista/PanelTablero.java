package Vista;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Panel que dibuja el tablero 8×8 y los caballos colocados.
 *
 * Convención de valores en el tablero:
 *   0 = casilla vacía
 *   1 = caballo colocado por el algoritmo (rojo)
 *   2 = caballo inicial elegido por el usuario (azul)
 */
public class PanelTablero extends JPanel {

    private int[][] tablero;
    private int filaInicial = -1;
    private int colInicial  = -1;
    private boolean modoSeleccion = false;

    // Colores del tablero de ajedrez
    private static final Color CELDA_CLARA             = new Color(240, 217, 181);
    private static final Color CELDA_OSCURA            = new Color(181, 136,  99);

    // Colores de los caballos
    private static final Color COLOR_CABALLO_INICIAL   = new Color( 30, 144, 255);  // azul
    private static final Color COLOR_CABALLO_ALGORITMO = new Color(220,  50,  50);  // rojo

    // Highlight al pasar el mouse durante selección
    private static final Color COLOR_HIGHLIGHT         = new Color(100, 220, 100, 140);

    // Borde al seleccionar la celda con click
    private static final Color COLOR_BORDE_SELECCION  = new Color(255, 215,   0);   // dorado

    private int filaHover = -1;
    private int colHover  = -1;

    public PanelTablero() {
        tablero = new int[8][8];
        setBackground(new Color(40, 40, 40));
        setPreferredSize(new Dimension(560, 560));

        // Hover: resalta la celda mientras se mueve el mouse en modo selección
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (!modoSeleccion) return;
                int[] celda = getCeldaDesde(e.getX(), e.getY());
                if (celda != null) { filaHover = celda[0]; colHover = celda[1]; }
                else               { filaHover = -1;       colHover = -1; }
                repaint();
            }
        });

        // Click: elige la posición inicial del caballo
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!modoSeleccion) return;
                int[] celda = getCeldaDesde(e.getX(), e.getY());
                if (celda != null) {
                    filaInicial   = celda[0];
                    colInicial    = celda[1];
                    modoSeleccion = false;
                    filaHover     = -1;
                    colHover      = -1;
                    setCursor(Cursor.getDefaultCursor());
                    repaint();
                    firePropertyChange("posicionElegida", null, new int[]{filaInicial, colInicial});
                }
            }
        });
    }

    // -----------------------------------------------------------------------
    // API pública
    // -----------------------------------------------------------------------

    /** Actualiza el tablero y repinta. */
    public void setTablero(int[][] tablero) {
        this.tablero = tablero;
        repaint();
    }

    /** Activa el modo de selección de posición inicial (cursor mano + highlight). */
    public void activarModoSeleccion() {
        limpiarTablero();
        modoSeleccion = true;
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        repaint();
    }

    /** Limpia todo el tablero y resetea el estado. */
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

    /** Marca la posición inicial con valor 2 (se dibuja en azul). */
    public void marcarPosicionInicial(int fila, int col) {
        filaInicial        = fila;
        colInicial         = col;
        tablero[fila][col] = 2;
        repaint();
    }

    public int getFilaInicial() { return filaInicial; }
    public int getColInicial()  { return colInicial;  }
    public int[][] getTablero() { return tablero; }

    // -----------------------------------------------------------------------
    // Pintado del tablero
    // -----------------------------------------------------------------------

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,         RenderingHints.VALUE_RENDER_QUALITY);

        int size    = Math.min(getWidth(), getHeight()) - 40;
        int offsetX = (getWidth()  - size) / 2;
        int offsetY = (getHeight() - size) / 2;
        int cell    = size / 8;

        // Sombra del tablero
        g2.setColor(new Color(0, 0, 0, 80));
        g2.fillRoundRect(offsetX + 4, offsetY + 4, size, size, 6, 6);

        // Borde exterior del tablero
        g2.setColor(new Color(100, 70, 30));
        g2.setStroke(new BasicStroke(3));
        g2.drawRect(offsetX - 2, offsetY - 2, size + 4, size + 4);

        // Celdas
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                int x = offsetX + j * cell;
                int y = offsetY + i * cell;

                // Color base de la celda
                g2.setColor((i + j) % 2 == 0 ? CELDA_CLARA : CELDA_OSCURA);
                g2.fillRect(x, y, cell, cell);

                // Hover en modo selección
                if (modoSeleccion && i == filaHover && j == colHover) {
                    g2.setColor(COLOR_HIGHLIGHT);
                    g2.fillRect(x, y, cell, cell);
                }

                // Dibujar caballo según valor
                if (tablero[i][j] == 2) {
                    dibujarCaballo(g2, x, y, cell, COLOR_CABALLO_INICIAL);
                    // Borde dorado para el caballo inicial
                    g2.setColor(COLOR_BORDE_SELECCION);
                    g2.setStroke(new BasicStroke(2.5f));
                    g2.drawRect(x + 2, y + 2, cell - 4, cell - 4);
                    g2.setStroke(new BasicStroke(1));
                } else if (tablero[i][j] == 1) {
                    dibujarCaballo(g2, x, y, cell, COLOR_CABALLO_ALGORITMO);
                }

                // Coordenadas del tablero (letras A-H y números 1-8)
                if (i == 7) {
                    g2.setFont(new Font("SansSerif", Font.BOLD, (int)(cell * 0.18)));
                    g2.setColor(new Color(0, 0, 0, 90));
                    String letra = String.valueOf((char)('A' + j));
                    g2.drawString(letra, x + cell - (int)(cell * 0.22), y + cell - 3);
                }
                if (j == 0) {
                    g2.setFont(new Font("SansSerif", Font.BOLD, (int)(cell * 0.18)));
                    g2.setColor(new Color(0, 0, 0, 90));
                    g2.drawString(String.valueOf(8 - i), x + 3, y + (int)(cell * 0.22));
                }
            }
        }

        // Mensaje flotante en modo selección
        if (modoSeleccion) {
            String msg = "Haz clic en una celda para colocar el caballo inicial";
            g2.setFont(new Font("SansSerif", Font.BOLD, 13));
            int tw = g2.getFontMetrics().stringWidth(msg);
            int bx = (getWidth() - tw) / 2 - 10;
            int by = offsetY + size + 8;
            g2.setColor(new Color(10, 10, 10, 180));
            g2.fillRoundRect(bx, by, tw + 20, 26, 10, 10);
            g2.setColor(new Color(100, 220, 100));
            g2.drawString(msg, bx + 10, by + 18);
        }

        // Leyenda inferior
        dibujarLeyenda(g2, offsetX, offsetY + size + (modoSeleccion ? 42 : 10), size);
    }

    /** Dibuja la leyenda de colores debajo del tablero. */
    private void dibujarLeyenda(Graphics2D g2, int x, int y, int size) {
        int dotSize = 14;
        int gap     = 8;
        g2.setFont(new Font("SansSerif", Font.PLAIN, 12));

        String[] etiquetas = {"Caballo inicial (tú)", "Caballo del algoritmo"};
        Color[]  colores   = {COLOR_CABALLO_INICIAL, COLOR_CABALLO_ALGORITMO};

        int totalW = 0;
        for (String e : etiquetas) totalW += g2.getFontMetrics().stringWidth(e) + dotSize + gap + 20;
        int startX = x + (size - totalW) / 2;

        for (int k = 0; k < etiquetas.length; k++) {
            g2.setColor(colores[k]);
            g2.fillOval(startX, y + 2, dotSize, dotSize);
            g2.setColor(new Color(200, 200, 200));
            g2.drawString(etiquetas[k], startX + dotSize + 5, y + dotSize - 1);
            startX += dotSize + 5 + g2.getFontMetrics().stringWidth(etiquetas[k]) + 20;
        }
    }

    /** Dibuja el símbolo del caballo (♞) en la celda indicada. */
    private void dibujarCaballo(Graphics2D g2, int x, int y, int cell, Color color) {
        int fontSize = (int) (cell * 0.62);
        g2.setFont(new Font("Serif", Font.PLAIN, fontSize));
        FontMetrics fm  = g2.getFontMetrics();
        String simbolo  = "\u265E";

        int sx = x + (cell - fm.stringWidth(simbolo)) / 2;
        int sy = y + (cell + fm.getAscent() - fm.getDescent()) / 2 - 2;

        // Sombra
        g2.setColor(new Color(0, 0, 0, 110));
        g2.drawString(simbolo, sx + 2, sy + 2);

        // Caballo
        g2.setColor(color);
        g2.drawString(simbolo, sx, sy);
    }

    /** Convierte coordenadas de píxel a celda del tablero. */
    private int[] getCeldaDesde(int px, int py) {
        int size    = Math.min(getWidth(), getHeight()) - 40;
        int offsetX = (getWidth()  - size) / 2;
        int offsetY = (getHeight() - size) / 2;
        int cell    = size / 8;

        int col  = (px - offsetX) / cell;
        int fila = (py - offsetY) / cell;

        if (fila >= 0 && fila < 8 && col >= 0 && col < 8) return new int[]{fila, col};
        return null;
    }
}
