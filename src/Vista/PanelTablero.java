package Vista;

import javax.swing.*;
import java.awt.*;

public class PanelTablero extends JPanel {

    private int[][] tablero;

    private static final Color CELDA_CLARA           = new Color(240, 217, 181);
    private static final Color CELDA_OSCURA          = new Color(181, 136, 99);
    private static final Color COLOR_CABALLO_INICIAL  = new Color(30, 144, 255);  // azul
    private static final Color COLOR_CABALLO_ALGORITMO = new Color(220, 50, 50); // rojo

    public PanelTablero() {
        tablero = new int[8][8];
        setBackground(new Color(40, 40, 40));
    }

    // 0 = vacío | 1 = caballo del algoritmo | 2 = caballo inicial del usuario
    public void setTablero(int[][] tablero) {
        this.tablero = tablero;
        repaint();
    }

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

                if (tablero[i][j] == 2) {
                    dibujarCaballo(g2, x, y, cell, COLOR_CABALLO_INICIAL);
                } else if (tablero[i][j] == 1) {
                    dibujarCaballo(g2, x, y, cell, COLOR_CABALLO_ALGORITMO);
                }
            }
        }
    }

    private void dibujarCaballo(Graphics2D g2, int x, int y, int cell, Color color) {
        int fontSize = (int) (cell * 0.65);
        g2.setFont(new Font("Serif", Font.PLAIN, fontSize));
        FontMetrics fm = g2.getFontMetrics();
        String simbolo = "\u265E"; // ♞

        int sx = x + (cell - fm.stringWidth(simbolo)) / 2;
        int sy = y + (cell + fm.getAscent() - fm.getDescent()) / 2 - 2;

        // sombra
        g2.setColor(new Color(0, 0, 0, 100));
        g2.drawString(simbolo, sx + 2, sy + 2);

        // caballo
        g2.setColor(color);
        g2.drawString(simbolo, sx, sy);
    }
}