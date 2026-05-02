package Vista;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PanelTablero extends JPanel {

    private int[][] tablero;
    private int filaInicial = -1;
    private int colInicial  = -1;
    private boolean modoSeleccion = false;

    private static final Color CELDA_CLARA            = new Color(240, 217, 181);
    private static final Color CELDA_OSCURA           = new Color(181, 136, 99);
    private static final Color COLOR_CABALLO_INICIAL  = new Color(30, 144, 255);
    private static final Color COLOR_CABALLO_ALGORITMO = new Color(220, 50, 50);
    private static final Color COLOR_HIGHLIGHT        = new Color(100, 200, 100, 120);

    private int filaHover = -1;
    private int colHover  = -1;

    public PanelTablero() {
        tablero = new int[8][8];
        setBackground(new Color(40, 40, 40));
        setPreferredSize(new Dimension(560, 560));

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
                    firePropertyChange("posicionElegida", null, new int[]{filaInicial, colInicial});
                }
            }
        });
    }

    public void setTablero(int[][] tablero) {
        this.tablero = tablero;
        repaint();
    }

    public void activarModoSeleccion() {
        limpiarTablero();
        modoSeleccion = true;
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        repaint();
    }

    public void limpiarTablero() {
        tablero    = new int[8][8];
        filaInicial = -1;
        colInicial  = -1;
        modoSeleccion = false;
        filaHover  = -1;
        colHover   = -1;
        setCursor(Cursor.getDefaultCursor());
        repaint();
    }

    public void marcarPosicionInicial(int fila, int col) {
        filaInicial      = fila;
        colInicial       = col;
        tablero[fila][col] = 2;
        repaint();
    }

    public int getFilaInicial() { return filaInicial; }
    public int getColInicial()  { return colInicial; }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int size    = Math.min(getWidth(), getHeight()) - 20;
        int offsetX = (getWidth()  - size) / 2;
        int offsetY = (getHeight() - size) / 2;
        int cell    = size / 8;

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                g2.setColor((i + j) % 2 == 0 ? CELDA_CLARA : CELDA_OSCURA);
                int x = offsetX + j * cell;
                int y = offsetY + i * cell;
                g2.fillRect(x, y, cell, cell);

                if (modoSeleccion && i == filaHover && j == colHover) {
                    g2.setColor(COLOR_HIGHLIGHT);
                    g2.fillRect(x, y, cell, cell);
                }

                if (tablero[i][j] == 2) {
                    dibujarCaballo(g2, x, y, cell, COLOR_CABALLO_INICIAL);
                } else if (tablero[i][j] == 1) {
                    dibujarCaballo(g2, x, y, cell, COLOR_CABALLO_ALGORITMO);
                }
            }
        }

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

    private void dibujarCaballo(Graphics2D g2, int x, int y, int cell, Color color) {
        int fontSize = (int) (cell * 0.65);
        g2.setFont(new Font("Serif", Font.PLAIN, fontSize));
        FontMetrics fm = g2.getFontMetrics();
        String simbolo = "\u265E";

        int sx = x + (cell - fm.stringWidth(simbolo)) / 2;
        int sy = y + (cell + fm.getAscent() - fm.getDescent()) / 2 - 2;

        g2.setColor(new Color(0, 0, 0, 100));
        g2.drawString(simbolo, sx + 2, sy + 2);
        g2.setColor(color);
        g2.drawString(simbolo, sx, sy);
    }

    private int[] getCeldaDesde(int px, int py) {
        int size    = Math.min(getWidth(), getHeight()) - 20;
        int offsetX = (getWidth()  - size) / 2;
        int offsetY = (getHeight() - size) / 2;
        int cell    = size / 8;

        int col  = (px - offsetX) / cell;
        int fila = (py - offsetY) / cell;

        if (fila >= 0 && fila < 8 && col >= 0 && col < 8) return new int[]{fila, col};
        return null;
    }
}