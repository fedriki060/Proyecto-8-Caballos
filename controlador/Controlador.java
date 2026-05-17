package controlador;

import Vista.PanelTablero;
import Backtracking.ColocacionCaballos;

/**
 * Controlador que conecta el algoritmo de backtracking (ColocacionCaballos)
 * con la interfaz grafica (PanelTablero).
 *
 * IMPORTANTE: este controlador NO actualiza la vista directamente.
 * El resultado del tablero se lee despues desde el EDT (hilo de Swing)
 * via panelTablero.getTablero() en el metodo done() del SwingWorker.
 */
public class Controlador {

    private PanelTablero panelTablero;
    private ColocacionCaballos solver;

    public Controlador(PanelTablero panelTablero) {
        this.panelTablero = panelTablero;
    }

    /**
     * Resuelve el problema con tablero completamente vacio.
     * Instancia el algoritmo, ejecuta el backtracking y guarda el resultado
     * internamente. La vista debe ser actualizada por quien llama a este metodo,
     * usando panelTablero.setTablero(solver.obtenerTablero()).
     *
     * @return true si encontro solucion, false si no existe ninguna.
     */
    public boolean resolverVacio() {
        solver = new ColocacionCaballos();
        boolean encontrado = solver.resolverVacio();
        if (encontrado) {
            // Guardamos el resultado en el panel para que el SwingWorker lo lea
            panelTablero.setTablero(solver.obtenerTablero());
            solver.imprimirTablero();
        }
        return encontrado;
    }

    /**
     * Resuelve el problema con un caballo ya colocado en (fila, col).
     *
     * @param fila Fila del caballo inicial (0-7)
     * @param col  Columna del caballo inicial (0-7)
     * @return true si encontro solucion, false si no existe ninguna.
     */
    public boolean resolverConInicial(int fila, int col) {
        solver = new ColocacionCaballos();
        boolean encontrado = solver.resolverConInicial(fila, col);
        if (encontrado) {
            panelTablero.setTablero(solver.obtenerTablero());
            solver.imprimirTablero();
        }
        return encontrado;
    }
}
