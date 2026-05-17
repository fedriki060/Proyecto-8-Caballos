import Vista.VentanaPrincipal;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Punto de entrada del programa "Problema de los 8 Caballos".
 *
 * Lanza la interfaz gráfica en el hilo de eventos de Swing (EDT),
 * tal como recomienda la documentación oficial de Java.
 */
public class Main {
    public static void main(String[] args) {
        // Intentamos aplicar el look & feel del sistema operativo
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Si falla, usamos el look & feel por defecto de Swing
        }

        // Toda la UI de Swing debe crearse en el Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(VentanaPrincipal::new);
    }
}
