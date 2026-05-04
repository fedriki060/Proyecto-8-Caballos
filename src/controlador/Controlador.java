package controlador;

import Vista.PanelTablero;

public class Controlador {

    private PanelTablero panelTablero;

    public Controlador(PanelTablero panelTablero) {
        this.panelTablero = panelTablero;
    }

    public boolean resolverVacio() {
        // Caballos falsos para probar que la interfaz los dibuja
        int[][] tablero = new int[8][8];
        tablero[0][0] = 1;
        tablero[2][1] = 1;
        tablero[4][2] = 1;
        tablero[6][3] = 1;
        tablero[1][4] = 1;
        tablero[3][5] = 1;
        tablero[5][6] = 1;
        tablero[7][7] = 1;
        panelTablero.setTablero(tablero);
        return true;
    }

    public boolean resolverConInicial(int fila, int col) {
        // Reutiliza el mismo tablero falso
        return resolverVacio();
    }
}