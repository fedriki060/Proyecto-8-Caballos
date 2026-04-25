package Backtracking;

/**
 * Clase que implementa el algoritmo de backtracking para resolver el problema de los 8 caballos.
 */
public class ColocacionCaballos {

    private int[][] tablero;

    /**
     * Array que contiene todos los posibles movimientos que puede hacer un caballo en ajedrez.
     * Cada movimiento es una combinación de dos valores: el cambio en filas (primer valor)
     * y el cambio en columnas (segundo valor).
     *
     * Los 8 movimientos posibles del caballo forman una "L":
     * - 2 casillas en una dirección y 1 en la perpendicular, o
     * - 1 casilla en una dirección y 2 en la perpendicular
     *
     * Por ejemplo:
     * Si el caballo está en (3,3), puede atacar: (1,2), (1,4), (2,1), (2,5), (4,1), (4,5), (5,2), (5,4)
     */
    private static final int[][] MOVIMIENTOS_CABALLO = {
            {-2, -1},  // 2 arriba, 1 izquierda
            {-2, 1},   // 2 arriba, 1 derecha
            {-1, -2},  // 1 arriba, 2 izquierda
            {-1, 2},   // 1 arriba, 2 derecha
            {1, -2},   // 1 abajo, 2 izquierda
            {1, 2},    // 1 abajo, 2 derecha
            {2, -1},   // 2 abajo, 1 izquierda
            {2, 1}     // 2 abajo, 1 derecha
    };

    public ColocacionCaballos() {
        tablero = new int[8][8];
    }

    /**
     * FUNCIÓN PRINCIPAL DE BACKTRACKING

     * Este método es el corazón del algoritmo de backtracking. Intenta colocar caballos
     * recursivamente en el tablero hasta lograr colocar los 8 caballos en posiciones seguras,
     * o retrocede si encuentra que no es posible continuar.

     * @param caballosColocados Número de caballos ya colocados exitosamente en el tablero.
     *                          En la primera llamada será 0, en la segunda 1, etc.

     * @return true si se logró colocar todos los 8 caballos en posiciones válidas,
     *         false si no fue posible encontrar una solución válida desde este estado.
     */
    public boolean ColocarCaballos(int caballosColocados) {

        /*
         * CASO BASE DEL BACKTRACKING - CONDICIÓN DE ÉXITO
         *
         * Si ya hemos colocado 8 caballos (caballosColocados == 8),
         * significa que hemos encontrado una solución válida al problema.
         * En este caso, retornamos true para indicar éxito y salimos de la recursión.
         */
        if (caballosColocados == 8) {
            return true;
        }


        /*
         * EXPLORACIÓN EXHAUSTIVA - INTENTAR TODAS LAS POSICIONES
         *
         * Estos dos bucles anidados recorren TODAS las casillas del tablero (8x8 = 64 casillas)
         * de izquierda a derecha, de arriba a abajo. En cada casilla, intentaremos colocar
         * el siguiente caballo si la posición es "segura".
         *
         * El orden de exploración es importante para el backtracking:
         * - Fila 0: (0,0), (0,1), (0,2), ..., (0,7)
         * - Fila 1: (1,0), (1,1), (1,2), ..., (1,7)
         * - ... y así sucesivamente hasta la fila 7
         */
        for (int fila = 0; fila < 8; fila++) {
            for (int columna = 0; columna < 8; columna++) {

                /*
                 * VERIFICACIÓN DE SEGURIDAD - ¿PUEDO COLOCAR UN CABALLO AQUÍ?
                 *
                 * Antes de intentar colocar un caballo en la posición (fila, columna),
                 * verificamos si esta posición es "segura" llamando al método esSegura().
                 *
                 * Una posición es segura si:
                 * 1. La casilla está vacía (no hay otro caballo aquí)
                 * 2. Ningún caballo ya colocado puede atacar esta posición
                 *    (es decir, ningún caballo está en uno de los 8 movimientos posibles)
                 *
                 * Solo si la posición es segura, continuamos; de lo contrario, pasamos a la siguiente casilla.
                 */
                if (esSegura(fila, columna)) {

                    /*
                     * DECISIÓN / PRIMER PASO DEL BACKTRACKING
                     *
                     * Si la posición (fila, columna) es segura, colocamos un caballo aquí (asignamos 1).
                     * Este es el momento donde "hacemos una elección" en el backtracking.
                     * Estamos comprometiéndonos con esta posición para el siguiente caballo.
                     */
                    tablero[fila][columna] = 1;

                    /*
                     * RECURSIÓN - INTENTAR COLOCAR EL SIGUIENTE CABALLO
                     *
                     * Después de colocar un caballo en (fila, columna), llamamos recursivamente
                     * a ColocarCaballos() para intentar colocar el SIGUIENTE caballo (caballosColocados + 1).
                     *
                     * Si la recursión retorna true, significa que encontramos una solución válida
                     * completa desde este punto, por lo que retornamos true inmediatamente
                     * sin explorar otras posiciones.
                     */
                    if (ColocarCaballos(caballosColocados + 1)) {
                        return true;
                    }

                    /*
                     * RETROCESO / BACKTRACKING - DESHACER LA DECISIÓN
                     *
                     * Si la llamada recursiva retorna false, significa que no fue posible colocar
                     * los caballos restantes con el caballo actualmente ubicado en (fila, columna).
                     * La rama del árbol de búsqueda que estamos explorando NO lleva a una solución válida.
                     *
                     * DESHACEMOS la decisión anterior removiendo el caballo de la posición
                     * asignándole el valor 0 (vacío). Esto permite que el bucle continúe
                     * intentando otras posiciones. Este es el paso más importante del backtracking.
                     */
                    tablero[fila][columna] = 0;
                }
            }
        }

        /*
         * RETORNO FINAL - NO HAY SOLUCIÓN DESDE ESTE ESTADO
         *
         * Si llegamos aquí, significa que exploramos TODAS las 64 casillas del tablero
         * y NINGUNA de las combinaciones posibles llevó a una solución válida.
         * No es posible colocar los caballos restantes a partir del estado actual del tablero.
         *
         * Retornamos false para indicar que esta rama no tiene solución,
         * lo que causará que el nivel anterior de recursión retroceda y pruebe otras opciones.
         */
        return false;
    }


    /**
     * FUNCIÓN AUXILIAR - VERIFICAR SI UNA POSICIÓN ES SEGURA

     * Este método determina si es seguro colocar un caballo en la posición (fila, columna).
     * Una posición es segura solo si:
     * 1. La casilla está vacía (no contiene otro caballo)
     * 2. Ninguno de los caballos ya colocados puede atacar esta posición
     *
     * @param fila La fila de la posición a verificar (0-7)
     * @param columna La columna de la posición a verificar (0-7)
     *
     * @return true si la posición es segura para colocar un caballo,
     *         false si ya hay un caballo o si algún caballo puede atacarla.
     */
    private boolean esSegura(int fila, int columna) {

        if (tablero[fila][columna] != 0) {
            return false;
        }

        /*
         * Para cada uno de los 8 movimientos posibles del caballo,
         * calculamos si hay un caballo en esa posición que pueda atacar (fila, columna).
         *
         * Si algún caballo puede atacar esta posición, no es segura y retornamos false.
         *
         * Ejemplo: Si (fila, columna) = (3, 3), verificamos si hay caballos en:
         * (1,2), (1,4), (2,1), (2,5), (4,1), (4,5), (5,2), (5,4)
         */
        for (int[] movimiento : MOVIMIENTOS_CABALLO) {

            int filaAtaque = fila + movimiento[0];
            int columnaAtaque = columna + movimiento[1];

            /*
             * VALIDACIÓN DE LÍMITES - ¿LA POSICIÓN ESTÁ DENTRO DEL TABLERO?
             *
             * Antes de acceder a tablero[filaAtaque][columnaAtaque], verificamos que
             * estas coordenadas estén dentro de los límites del tablero (0-7).
             *
             * Si la posición está fuera del tablero, no hay caballo allí,
             * así que continuamos con el siguiente movimiento.
             */
            if (filaAtaque >= 0 && filaAtaque < 8 &&
                    columnaAtaque >= 0 && columnaAtaque < 8) {

                /*
                 * Si hay un caballo en (filaAtaque, columnaAtaque),
                 * significa que ese caballo puede atacar la posición (fila, columna),
                 * por lo que NO es segura. Retornamos false inmediatamente.
                 */
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