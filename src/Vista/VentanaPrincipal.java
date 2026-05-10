package Vista;

import controlador.Controlador;
import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * VentanaPrincipal — ventana principal de la aplicación.
 *
 * Organiza los componentes en tres zonas (BorderLayout):
 *   NORTH  → encabezado con título
 *   CENTER → PanelTablero (el tablero)
 *   SOUTH  → label de estado + 4 botones
 *
 * Usa SwingWorker para correr el backtracking en un hilo aparte
 * y que la ventana no se congele mientras el algoritmo trabaja.
 */

public class VentanaPrincipal extends JFrame {

    // --- Componentes ---

    /* El tablero visual */
    private PanelTablero panelTablero;
    // Botones de acción
    private JButton btnResolverVacio; // modo 1: tablero vacío
    private JButton btnElegirInicial; // Activa el clic en el tablero
    private JButton btnResolverConInicial; //modo 2: con posición inicial
    private JButton btnLimpiar;  // resetea todo

    /** Muestra el estado actual: neutral (gris), éxito (verde) o error (rojo). */
    private JLabel  lblEstado;

    // Indica si el usuario ya eligió una celda inicial haciendo clic en el tablero.
    private boolean posicionInicialElegida = false;


    // --- Constructor ---
    public VentanaPrincipal() {
        setTitle("Problema de los 8 Caballos — Backtracking");
        setSize(680, 780);
        setMinimumSize(new Dimension(500, 620));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(30, 30, 30));

        // Se construye en orden: encabezado → tablero → panel sur
        construirEncabezado();
        construirTablero();
        construirPanelSur();

        //Centra la ventana en la pantalla
        setLocationRelativeTo(null);
        setVisible(true);
    }

    //Construcción de zonas

    //zona  norte: titulo y subtíyulo decorativos
    private void construirEncabezado() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(20, 20, 20));
        header.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));

        JLabel titulo = new JLabel("♞  Problema de los 8 Caballos");
        titulo.setFont(new Font("Serif", Font.BOLD, 22));
        titulo.setForeground(new Color(230, 195, 130));

        JLabel subtitulo = new JLabel("Backtracking — ningún caballo ataca a otro");
        subtitulo.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitulo.setForeground(new Color(150, 150, 150));

        JPanel textos = new JPanel(new GridLayout(2, 1, 0, 2));
        textos.setOpaque(false);
        textos.add(titulo);
        textos.add(subtitulo);

        header.add(textos, BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);
    }

    //Zona Central: El tablero, registra el listener de posición inicial también
    private void construirTablero() {
        panelTablero = new PanelTablero();

        /* Cuando el usuario hace clic en el tablero en modo selección,
         * PanelTablero lanza el evento "posicionElegida".
         * Aquí lo capturamos para actualizar el estado y habilitar el botón.
         */
        panelTablero.addPropertyChangeListener("posicionElegida", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                posicionInicialElegida = true;

                int fila = panelTablero.getFilaInicial();
                int col  = panelTablero.getColInicial();
                panelTablero.marcarPosicionInicial(fila, col); // Pinta la celda elegida en azul antes de resolver
                char letra = (char) ('A' + col);
                int  num   = 8 - fila;
                setEstado("Caballo inicial en " + letra + num + " — pulsa «Resolver con inicial»", TipoEstado.INFO);
                btnResolverConInicial.setEnabled(true);
                btnElegirInicial.setText("Cambiar posición inicial");
            }
        });

        panelTablero.setPreferredSize(new Dimension(560, 560));
        add(panelTablero, BorderLayout.CENTER);
    }

    //Zona sur: Label de el estado junto con lo grilla de 4 botonos
    private void construirPanelSur() {

        // Label de estado
        lblEstado = new JLabel("Elige un modo para comenzar", SwingConstants.CENTER);
        lblEstado.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblEstado.setForeground(new Color(180, 180, 180));
        lblEstado.setBorder(BorderFactory.createEmptyBorder(6, 20, 4, 20));

        // Botones
        btnResolverVacio      = crearBoton("Resolver (tablero vacío)",  new Color(60, 140, 80));
        btnElegirInicial      = crearBoton("Elegir posición inicial",   new Color(50, 100, 180));
        btnResolverConInicial = crearBoton("Resolver con inicial ♞",    new Color(160, 90, 30));
        btnLimpiar            = crearBoton("Limpiar tablero",           new Color(90, 30, 30));

        //empieza deshabilitado hasta que el usuario elija una celda
        btnResolverConInicial.setEnabled(false);

        //Grilla 2x2 de botones
        JPanel gridBotones = new JPanel(new GridLayout(2, 2, 10, 8));
        gridBotones.setBackground(new Color(30, 30, 30));
        gridBotones.setBorder(BorderFactory.createEmptyBorder(4, 20, 14, 20));
        gridBotones.add(btnResolverVacio);
        gridBotones.add(btnElegirInicial);
        gridBotones.add(btnResolverConInicial);
        gridBotones.add(btnLimpiar);

        JPanel sur = new JPanel(new BorderLayout());
        sur.setBackground(new Color(30, 30, 30));
        sur.add(lblEstado,   BorderLayout.NORTH);
        sur.add(gridBotones, BorderLayout.CENTER);
        add(sur, BorderLayout.SOUTH);

        // --- Acciones de los botones ---

        /* Modo 1: Tablero vacio
        * Limpia el tablero, lanza el algoritmo en un hilo aparte
        * y muestra el resultado cuando termina
        */

        btnResolverVacio.addActionListener(e -> {
            panelTablero.limpiarTablero();
            posicionInicialElegida = false;
            btnResolverConInicial.setEnabled(false);
            setEstado("Buscando solución...", TipoEstado.INFO);
            setBotonesHabilitados(false); //Deshabilita botones mienntras trabaja

            new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() {
                    //Este bloque corre un hilo aparte -> la ventana no se congela
                    Controlador ctrl = new Controlador(panelTablero);
                    return ctrl.resolverVacio();
                }
                @Override
                protected void done() {
                    try {
                        boolean ok = get();
                        setEstado(ok ? "✔  Solución encontrada — 8 caballos colocados"
                                        : "✘  No se encontró solución",
                                ok ? TipoEstado.EXITO : TipoEstado.ERROR);
                    } catch (Exception ex) {
                        setEstado("Error: " + ex.getMessage(), TipoEstado.ERROR);
                    } finally {
                        setBotonesHabilitados(true);
                    }
                }
            }.execute();
        });

        /*
         * Activa el modo selección en el tablero.
         * El usuario hace clic en una celda y PanelTablero lanza el evento
         * "posicionElegida" que capturamos arriba en construirTablero().
         */
        btnElegirInicial.addActionListener(e -> {
            posicionInicialElegida = false;
            btnResolverConInicial.setEnabled(false);
            panelTablero.activarModoSeleccion();
            setEstado("Haz clic en el tablero para elegir la posición inicial", TipoEstado.INFO);
        });

        /*
         * MODO 2: con posición inicial.
         * Solo se puede pulsar después de que el usuario eligió una celda.
         * Igual que el modo vacío pero pasa fila y col al controlador.
         */
        btnResolverConInicial.addActionListener(e -> {
            int fila = panelTablero.getFilaInicial();
            int col  = panelTablero.getColInicial();
            setEstado("Buscando solución desde la posición inicial...", TipoEstado.INFO);
            setBotonesHabilitados(false);

            new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() {
                    Controlador ctrl = new Controlador(panelTablero);
                    return ctrl.resolverConInicial(fila, col);
                }
                @Override
                protected void done() {
                    try {
                        boolean ok  = get();
                        char letra  = (char) ('A' + col);
                        int  num    = 8 - fila;
                        setEstado(ok ? "✔  Solución encontrada partiendo de " + letra + num
                                        : "✘  No existe solución partiendo de "  + letra + num,
                                ok ? TipoEstado.EXITO : TipoEstado.ERROR);
                    } catch (Exception ex) {
                        setEstado("Error: " + ex.getMessage(), TipoEstado.ERROR);
                    } finally {
                        setBotonesHabilitados(true);
                    }
                }
            }.execute();
        });

        /** Resetea todo: tablero, estado y botones. */
        btnLimpiar.addActionListener(e -> {
            panelTablero.limpiarTablero();
            posicionInicialElegida = false;
            btnResolverConInicial.setEnabled(false);
            btnElegirInicial.setText("Elegir posición inicial");
            setEstado("Tablero limpiado — elige un modo para comenzar", TipoEstado.INFO);
        });
    }

    private JButton crearBoton(String texto, Color colorFondo) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setBackground(colorFondo);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);

        Color hover = colorFondo.brighter();
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(hover); }
            @Override public void mouseExited (java.awt.event.MouseEvent e) { btn.setBackground(colorFondo); }
        });
        return btn;
    }

    // --- Utilidades ---

    // Los tres estados posibles
    private enum TipoEstado { INFO, EXITO, ERROR }

    /**
     * Actualiza el texto y color del label de estado.
     * INFO = gris | EXITO = verde | ERROR = rojo
     */
    protected void setEstado(String mensaje, TipoEstado tipo) {
        lblEstado.setText(mensaje);
        switch (tipo) {
            case EXITO -> lblEstado.setForeground(new Color(100, 220, 100));
            case ERROR -> lblEstado.setForeground(new Color(220, 80, 80));
            default    -> lblEstado.setForeground(new Color(180, 180, 180));
        }
    }

    /* Habilita o deshabilita los botones mientras el algoritmo trabaja.
     * btnResolverConInicial se maneja aparte porque depende de posicionInicialElegida.
     */
    protected void setBotonesHabilitados(boolean h) {
        btnResolverVacio.setEnabled(h);
        btnElegirInicial.setEnabled(h);
        btnLimpiar.setEnabled(h);
        if (h) btnResolverConInicial.setEnabled(posicionInicialElegida);
    }

    protected PanelTablero getPanelTablero()              { return panelTablero; }
    protected boolean isPosicionInicialElegida()          { return posicionInicialElegida; }
    protected JButton getBtnResolverVacio()               { return btnResolverVacio; }
    protected JButton getBtnElegirInicial()               { return btnElegirInicial; }
    protected JButton getBtnResolverConInicial()          { return btnResolverConInicial; }
    protected JButton getBtnLimpiar()                     { return btnLimpiar; }
}