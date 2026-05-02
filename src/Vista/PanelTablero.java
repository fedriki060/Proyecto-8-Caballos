package Vista;

import javax.swing.*;
import java.awt.*;

public class PanelTablero extends JPanel {

    private int[][] tablero;

    public PanelTablero() {
        tablero = new int[8][8];
        setBackground(new Color(40, 40, 40));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int size    = Math.min(getWidth(), getHeight()) - 20;
        int offsetX = (getWidth()  - size) / 2;
        int offsetY = (getHeight() - size) / 2;
        int cell    = size / 8;

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if ((i + j) % 2 == 0) {
                    g.setColor(new Color(240, 217, 181));
                } else {
                    g.setColor(new Color(181, 136, 99));
                }
                int x = offsetX + j * cell;
                int y = offsetY + i * cell;
                g.fillRect(x, y, cell, cell);
            }
        }
    }
}