
/**
 * Clase que implementa el algoritmo de backtracking para resolver el problema de los 8 caballos.
 */
public class ColocacionCaballos {

    private int[][] tablero;

    private static final int[][] MOVIMIENTOS_CABALLO = {
            {-2, -1},
            {-2, 1},
            {-1, -2},
            {-1, 2},
            {1, -2},
            {1, 2},
            {2, -1},
            {2, 1}
    };

    public ColocacionCaballos() {
        tablero = new int[8][8];
    }

    public boolean ColocarCaballos(int caballosColocados) {

        // Acá corregí la Validación de entrada
        if (caballosColocados < 0 || caballosColocados > 8) {
            throw new IllegalArgumentException("El número de caballos debe estar entre 0 y 8");
        }
       //

        if (caballosColocados == 8) {
            return true;
        }

        for (int fila = 0; fila < 8; fila++) {
            for (int columna = 0; columna < 8; columna++) {

                if (esSegura(fila, columna)) {

                    tablero[fila][columna] = 1;

                    if (ColocarCaballos(caballosColocados + 1)) {
                        return true;
                    }

                    tablero[fila][columna] = 0;
                }
            }
        }

        return false;
    }

    private boolean esSegura(int fila, int columna) {

        // acá corregí Validación de límites
        if (fila < 0 || fila >= 8 || columna < 0 || columna >= 8) {
            throw new IllegalArgumentException("Posición fuera del tablero");
        }
        //

        if (tablero[fila][columna] != 0) {
            return false;
        }

        for (int[] movimiento : MOVIMIENTOS_CABALLO) {

            int filaAtaque = fila + movimiento[0];
            int columnaAtaque = columna + movimiento[1];

            if (filaAtaque >= 0 && filaAtaque < 8 &&
                    columnaAtaque >= 0 && columnaAtaque < 8) {

                if (tablero[filaAtaque][columnaAtaque] == 1) {
                    return false;
                }
            }
        }

        return true;
    }

    public int[][] obtenerTablero() {
        return tablero;
    }

    public void imprimirTablero() {
        System.out.println("\n=== TABLERO FINAL ===");
        for (int fila = 0; fila < 8; fila++) {
            for (int columna = 0; columna < 8; columna++) {
                if (tablero[fila][columna] == 1) {
                    System.out.print("C ");
                } else {
                    System.out.print(". ");
                }
            }
            System.out.println();
        }
        System.out.println();
    }
}