package backtracking;

/**
 * Clase que implementa el algoritmo de backtracking para resolver el problema de los 8 caballos.
 *
 * Un caballo en ajedrez se mueve en forma de "L":
 * 2 casillas en una dirección y 1 en la perpendicular.
 * El objetivo es colocar 8 caballos en el tablero de modo que ninguno ataque a otro.
 */
public class ColocacionCaballos {

    private int[][] tablero;

    /**
     * Los 8 posibles movimientos de un caballo en ajedrez (forma de L).
     * Cada par {df, dc} representa un desplazamiento en fila y columna.
     */
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
    }

    // -----------------------------------------------------------------------
    // MÉTODO PRINCIPAL: tablero vacío
    // -----------------------------------------------------------------------

    /**
     * Intenta resolver el problema desde un tablero completamente vacío.
     * @return true si encontró una solución válida, false si no existe.
     */
    public boolean resolverVacio() {
        tablero = new int[8][8];
        return ColocarCaballos(0);
    }

    // -----------------------------------------------------------------------
    // MÉTODO PRINCIPAL: con posición inicial fija
    // -----------------------------------------------------------------------

    /**
     * Intenta resolver el problema con un caballo ya colocado en (fila, col).
     * Ese caballo queda marcado con valor 2 para distinguirlo visualmente.
     *
     * @param fila Fila del caballo inicial (0-7)
     * @param col  Columna del caballo inicial (0-7)
     * @return true si encontró una solución válida, false si no existe.
     */
    public boolean resolverConInicial(int fila, int col) {
        if (fila < 0 || fila >= 8 || col < 0 || col >= 8) {
            throw new IllegalArgumentException("Posición fuera del tablero");
        }
        tablero = new int[8][8];
        // El caballo inicial se marca con 2 (color azul en la vista)
        tablero[fila][col] = 2;
        // Ya tenemos 1 caballo colocado; el algoritmo coloca los 7 restantes
        return ColocarCaballos(1);
    }

    // -----------------------------------------------------------------------
    // BACKTRACKING CORE
    // -----------------------------------------------------------------------

    /**
     * FUNCIÓN PRINCIPAL DE BACKTRACKING.
     *
     * Recorre todas las casillas del tablero intentando colocar el caballo número
     * (caballosColocados + 1). Si lo logra, llama recursivamente para el siguiente.
     * Si ninguna posición funciona, retrocede (backtrack) y retorna false.
     *
     * @param caballosColocados Cantidad de caballos ya colocados correctamente.
     * @return true si se logró colocar los 8 caballos; false en caso contrario.
     */
    public boolean ColocarCaballos(int caballosColocados) {

        // ── CASO BASE ──────────────────────────────────────────────────────────
        // Si ya colocamos los 8 caballos, encontramos una solución válida.
        if (caballosColocados == 8) {
            return true;
        }

        // ── EXPLORACIÓN EXHAUSTIVA ─────────────────────────────────────────────
        // Recorremos todas las 64 casillas del tablero en orden fila × columna.
        for (int fila = 0; fila < 8; fila++) {
            for (int columna = 0; columna < 8; columna++) {

                // VERIFICACIÓN DE SEGURIDAD
                // Solo intentamos colocar si la casilla está vacía y
                // ningún caballo ya ubicado puede atacarla.
                if (esSegura(fila, columna)) {

                    // DECISIÓN: colocamos el caballo en (fila, columna)
                    tablero[fila][columna] = 1;

                    // RECURSIÓN: intentamos colocar el siguiente caballo
                    if (ColocarCaballos(caballosColocados + 1)) {
                        return true;  // Solución encontrada → propagamos éxito
                    }

                    // RETROCESO (BACKTRACKING): la decisión no llevó a solución,
                    // deshacemos la colocación y probamos la siguiente casilla.
                    tablero[fila][columna] = 0;
                }
            }
        }

        // Ninguna casilla funcionó → no hay solución desde este estado
        return false;
    }

    // -----------------------------------------------------------------------
    // VERIFICACIÓN DE SEGURIDAD
    // -----------------------------------------------------------------------

    /**
     * Determina si es seguro colocar un caballo en la posición (fila, columna).
     *
     * Una posición es segura si:
     *  1. La casilla está vacía (valor 0).
     *  2. Ningún caballo ya colocado (valor 1 o 2) puede atacar esa posición
     *     usando el movimiento en L del caballo.
     *
     * @param fila    Fila a verificar (0-7)
     * @param columna Columna a verificar (0-7)
     * @return true si la posición es segura; false si está ocupada o bajo ataque.
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

            // Verificamos que la posición de ataque esté dentro del tablero
            if (filaAtaque >= 0 && filaAtaque < 8 &&
                    columnaAtaque >= 0 && columnaAtaque < 8) {

                // Si hay un caballo (1 = algoritmo, 2 = inicial), la posición no es segura
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

    /** Devuelve el estado actual del tablero. */
    public int[][] obtenerTablero() {
        return tablero;
    }

    /** Imprime el tablero en consola (útil para depuración). */
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
