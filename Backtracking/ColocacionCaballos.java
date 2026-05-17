package Backtracking;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Clase que implementa el algoritmo de backtracking para resolver
 * el problema de los 8 caballos con posiciones aleatorias.
 *
 * Un caballo en ajedrez se mueve en forma de "L":
 * 2 casillas en una dirección y 1 en la perpendicular.
 * El objetivo es colocar 8 caballos en el tablero de modo que ninguno ataque a otro.
 */
public class ColocacionCaballos {

    private int[][] tablero;
    private final Random rng;

    private static final int[][] MOVIMIENTOS_CABALLO = {
            {-2, -1},  // 2 arriba, 1 izquierda
            {-2,  1},  // 2 arriba, 1 derecha
            {-1, -2},  // 1 arriba, 2 izquierda
            {-1,  2},  // 1 arriba, 2 derecha
            { 1, -2},  // 1 abajo,  2 izquierda
            { 1,  2},  // 1 abajo,  2 derecha
            { 2, -1},  // 2 abajo,  1 izquierda
            { 2,  1}   // 2 abajo,  1 derecha
    };

    public ColocacionCaballos() {
        tablero = new int[8][8];
        rng = new Random();
    }

    /**
     * Resuelve el problema desde un tablero vacío con orden aleatorio.
     */
    public boolean resolverVacio() {
        tablero = new int[8][8];
        return colocarCaballosAleatorio(0);
    }

    /**
     * Resuelve el problema colocando primero un caballo inicial fijo (valor 2).
     */
    public boolean resolverConInicial(int fila, int col) {
        if (fila < 0 || fila >= 8 || col < 0 || col >= 8) {
            throw new IllegalArgumentException("Posición fuera del tablero");
        }
        tablero = new int[8][8];
        tablero[fila][col] = 2; // caballo inicial
        return colocarCaballosAleatorio(1);
    }

    /**
     * Backtracking con orden aleatorio de casillas.
     * Siempre encuentra solución si existe.
     */
    private boolean colocarCaballosAleatorio(int caballosColocados) {
        if (caballosColocados == 8) {
            return true;
        }

        // Generamos lista de todas las casillas y las barajamos
        List<int[]> posiciones = new ArrayList<>();
        for (int f = 0; f < 8; f++) {
            for (int c = 0; c < 8; c++) {
                posiciones.add(new int[]{f, c});
            }
        }
        Collections.shuffle(posiciones, rng);

        // Probamos en orden aleatorio
        for (int[] pos : posiciones) {
            int fila = pos[0];
            int columna = pos[1];

            if (esSegura(fila, columna)) {
                tablero[fila][columna] = 1;

                if (colocarCaballosAleatorio(caballosColocados + 1)) {
                    return true;  // Solución encontrada → propagamos éxito
                }

                // Backtrack
                tablero[fila][columna] = 0;
            }
        }

        return false;
    }

    /**
     * Verifica si un caballo puede colocarse en (fila, columna)
     * sin ser atacado por otro.
     */
    private boolean esSegura(int fila, int columna) {

        // La casilla debe estar vacía
        if (tablero[fila][columna] != 0) {
            return false;
        }

        // Comprobamos si algún caballo en el tablero ataca (fila, columna)
        for (int[] mov : MOVIMIENTOS_CABALLO) {
            int filaAtaque    = fila    + mov[0];
            int columnaAtaque = columna + mov[1];

            if (filaAtaque >= 0 && filaAtaque < 8 &&
                    columnaAtaque >= 0 && columnaAtaque < 8) {

                if (tablero[filaAtaque][columnaAtaque] != 0) {
                    return false;
                }
            }
        }

        return true;
    }

    // -----------------------------------------------------------------------
    // ACCESO AL TABLERO
    // -----------------------------------------------------------------------

    public int[][] obtenerTablero() {
        return tablero;
    }

    public void imprimirTablero() {
        System.out.println("\n=== TABLERO FINAL ===");
        for (int fila = 0; fila < 8; fila++) {
            for (int columna = 0; columna < 8; columna++) {
                System.out.print(tablero[fila][columna] != 0 ? "C " : ". ");
            }
            System.out.println();
        }
        System.out.println();
    }
}