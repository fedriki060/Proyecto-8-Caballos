package Vista;

import controlador.Controlador;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.ExecutionException;

public class VentanaPrincipal extends JFrame {

    private PanelTablero panelTablero;
    private JButton btnResolverVacio;
    private JButton btnElegirInicial;
    private JButton btnResolverConInicial;
    private JButton btnLimpiar;
    private JLabel  lblEstado;
    private JLabel  lblContador;

    private boolean posicionInicialElegida = false;

    public VentanaPrincipal() {
        setTitle("Problema de los 8 Caballos - Backtracking");
        setSize(700, 820);
        setMinimumSize(new Dimension(540, 680));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(0, 0));
        getContentPane().setBackground(new Color(28, 28, 32));

        construirEncabezado();
        construirTablero();
        construirPanelSur();

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void construirEncabezado() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(18, 18, 22));
        header.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));

        JLabel titulo = new JLabel("\u265E  Problema de los 8 Caballos");
        titulo.setFont(new Font("Serif", Font.BOLD, 24));
        titulo.setForeground(new Color(235, 200, 130));

        JLabel subtitulo = new JLabel("Backtracking - ningun caballo puede atacar a otro");
        subtitulo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitulo.setForeground(new Color(140, 140, 155));

        lblContador = new JLabel("0 / 8 caballos colocados");
        lblContador.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblContador.setForeground(new Color(100, 200, 100));
        lblContador.setHorizontalAlignment(SwingConstants.RIGHT);

        JPanel textos = new JPanel(new GridLayout(2, 1, 0, 3));
        textos.setOpaque(false);
        textos.add(titulo);
        textos.add(subtitulo);

        header.add(textos,      BorderLayout.CENTER);
        header.add(lblContador, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);
    }

    private void construirTablero() {
        panelTablero = new PanelTablero();

        panelTablero.addPropertyChangeListener("posicionElegida", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                posicionInicialElegida = true;
                int fila = panelTablero.getFilaInicial();
                int col  = panelTablero.getColInicial();
                panelTablero.marcarPosicionInicial(fila, col);

                char letra = (char) ('A' + col);
                int  num   = 8 - fila;
                setEstado("Caballo inicial en " + letra + num + " - pulsa Resolver con inicial", TipoEstado.INFO);
                btnResolverConInicial.setEnabled(true);
                btnElegirInicial.setText("Cambiar posicion inicial");
                lblContador.setText("1 / 8 caballos colocados");
            }
        });

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setBackground(new Color(28, 28, 32));
        wrap.setBorder(BorderFactory.createEmptyBorder(10, 20, 0, 20));
        wrap.add(panelTablero, BorderLayout.CENTER);
        add(wrap, BorderLayout.CENTER);
    }

    private void construirPanelSur() {
        lblEstado = new JLabel("Elige un modo para comenzar", SwingConstants.CENTER);
        lblEstado.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblEstado.setForeground(new Color(170, 170, 185));
        lblEstado.setBorder(BorderFactory.createEmptyBorder(10, 20, 6, 20));

        btnResolverVacio      = crearBoton("Resolver (tablero vacio)", new Color(45, 130, 70));
        btnElegirInicial      = crearBoton("Elegir posicion inicial",  new Color(40,  90, 170));
        btnResolverConInicial = crearBoton("Resolver con inicial",     new Color(150, 80,  20));
        btnLimpiar            = crearBoton("Limpiar tablero",          new Color(90,  30,  30));

        btnResolverConInicial.setEnabled(false);

        JPanel gridBotones = new JPanel(new GridLayout(2, 2, 10, 8));
        gridBotones.setBackground(new Color(28, 28, 32));
        gridBotones.setBorder(BorderFactory.createEmptyBorder(4, 20, 16, 20));
        gridBotones.add(btnResolverVacio);
        gridBotones.add(btnElegirInicial);
        gridBotones.add(btnResolverConInicial);
        gridBotones.add(btnLimpiar);

        JLabel lblInfo = new JLabel(
            "<html><center><font color='#777788'>Algoritmo: Backtracking recursivo | Estructura de Datos</font></center></html>",
            SwingConstants.CENTER);
        lblInfo.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblInfo.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

        JPanel sur = new JPanel(new BorderLayout());
        sur.setBackground(new Color(28, 28, 32));
        sur.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(60, 60, 70)));
        sur.add(lblEstado,   BorderLayout.NORTH);
        sur.add(gridBotones, BorderLayout.CENTER);
        sur.add(lblInfo,     BorderLayout.SOUTH);
        add(sur, BorderLayout.SOUTH);

        // Resolver tablero vacio
        btnResolverVacio.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                panelTablero.limpiarTablero();
                posicionInicialElegida = false;
                btnResolverConInicial.setEnabled(false);
                btnElegirInicial.setText("Elegir posicion inicial");
                lblContador.setText("Resolviendo...");
                setEstado("Ejecutando backtracking...", TipoEstado.INFO);
                setBotonesHabilitados(false);

                new SwingWorker<int[][], Void>() {
                    protected int[][] doInBackground() throws Exception {
                        Controlador ctrl = new Controlador(panelTablero);
                        boolean ok = ctrl.resolverVacio();
                        if (ok) return panelTablero.getTablero();
                        return null;
                    }
                    protected void done() {
                        try {
                            int[][] resultado = get();
                            if (resultado != null) {
                                panelTablero.setTablero(resultado);
                                int total = contarCaballos();
                                lblContador.setText(total + " / 8 caballos colocados");
                                setEstado("Solucion encontrada - 8 caballos sin ataques", TipoEstado.EXITO);
                            } else {
                                lblContador.setText("Sin solucion");
                                setEstado("No se encontro solucion", TipoEstado.ERROR);
                            }
                        } catch (InterruptedException ex) {
                            setEstado("Error: " + ex.getMessage(), TipoEstado.ERROR);
                        } catch (ExecutionException ex) {
                            setEstado("Error: " + ex.getMessage(), TipoEstado.ERROR);
                        } finally {
                            setBotonesHabilitados(true);
                        }
                    }
                }.execute();
            }
        });

        // Elegir posicion inicial
        btnElegirInicial.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                posicionInicialElegida = false;
                btnResolverConInicial.setEnabled(false);
                panelTablero.activarModoSeleccion();
                lblContador.setText("0 / 8 caballos colocados");
                setEstado("Haz clic en el tablero para elegir la posicion inicial", TipoEstado.INFO);
            }
        });

        // Resolver con posicion inicial
        btnResolverConInicial.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                final int fila = panelTablero.getFilaInicial();
                final int col  = panelTablero.getColInicial();
                final char letra = (char) ('A' + col);
                final int  num   = 8 - fila;

                lblContador.setText("Resolviendo...");
                setEstado("Ejecutando backtracking desde " + letra + num + "...", TipoEstado.INFO);
                setBotonesHabilitados(false);

                new SwingWorker<int[][], Void>() {
                    protected int[][] doInBackground() throws Exception {
                        Controlador ctrl = new Controlador(panelTablero);
                        boolean ok = ctrl.resolverConInicial(fila, col);
                        if (ok) return panelTablero.getTablero();
                        return null;
                    }
                    protected void done() {
                        try {
                            int[][] resultado = get();
                            if (resultado != null) {
                                panelTablero.setTablero(resultado);
                                int total = contarCaballos();
                                lblContador.setText(total + " / 8 caballos colocados");
                                setEstado("Solucion desde " + letra + num + " - 8 caballos sin ataques", TipoEstado.EXITO);
                            } else {
                                lblContador.setText("Sin solucion");
                                setEstado("No existe solucion partiendo de " + letra + num, TipoEstado.ERROR);
                            }
                        } catch (InterruptedException ex) {
                            setEstado("Error: " + ex.getMessage(), TipoEstado.ERROR);
                        } catch (ExecutionException ex) {
                            setEstado("Error: " + ex.getMessage(), TipoEstado.ERROR);
                        } finally {
                            setBotonesHabilitados(true);
                            btnResolverConInicial.setEnabled(posicionInicialElegida);
                        }
                    }
                }.execute();
            }
        });

        // Limpiar tablero
        btnLimpiar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                panelTablero.limpiarTablero();
                posicionInicialElegida = false;
                btnResolverConInicial.setEnabled(false);
                btnElegirInicial.setText("Elegir posicion inicial");
                lblContador.setText("0 / 8 caballos colocados");
                setEstado("Tablero limpiado - elige un modo para comenzar", TipoEstado.INFO);
            }
        });
    }

    private int contarCaballos() {
        int[][] t = panelTablero.getTablero();
        int c = 0;
        for (int[] fila : t)
            for (int v : fila)
                if (v != 0) c++;
        return c;
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
        btn.setPreferredSize(new Dimension(0, 40));

        final Color normal = colorFondo;
        final Color hover  = colorFondo.brighter();
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (btn.isEnabled()) btn.setBackground(hover);
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(normal);
            }
        });
        return btn;
    }

    private enum TipoEstado { INFO, EXITO, ERROR }

    private void setEstado(String mensaje, TipoEstado tipo) {
        lblEstado.setText(mensaje);
        if (tipo == TipoEstado.EXITO) {
            lblEstado.setForeground(new Color(80, 210, 80));
        } else if (tipo == TipoEstado.ERROR) {
            lblEstado.setForeground(new Color(220, 70, 70));
        } else {
            lblEstado.setForeground(new Color(170, 170, 185));
        }
    }

    private void setBotonesHabilitados(boolean h) {
        btnResolverVacio.setEnabled(h);
        btnElegirInicial.setEnabled(h);
        btnLimpiar.setEnabled(h);
        if (h) {
            btnResolverConInicial.setEnabled(posicionInicialElegida);
        } else {
            btnResolverConInicial.setEnabled(false);
        }
    }
}
