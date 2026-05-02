package Vista;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class VentanaPrincipal extends JFrame {

    private PanelTablero panelTablero;
    private JButton btnResolverVacio;
    private JButton btnElegirInicial;
    private JButton btnResolverConInicial;
    private JButton btnLimpiar;
    private JLabel  lblEstado;

    private boolean posicionInicialElegida = false;

    public VentanaPrincipal() {
        setTitle("Problema de los 8 Caballos — Backtracking");
        setSize(680, 780);
        setMinimumSize(new Dimension(500, 620));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(30, 30, 30));

        construirEncabezado();
        construirTablero();
        construirPanelSur();

        setLocationRelativeTo(null);
        setVisible(true);
    }

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

    private void construirTablero() {
        panelTablero = new PanelTablero();

        panelTablero.addPropertyChangeListener("posicionElegida", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                posicionInicialElegida = true;
                int fila = panelTablero.getFilaInicial();
                int col  = panelTablero.getColInicial();
                panelTablero.marcarPosicionInicial(fila, col);
                char letra = (char) ('A' + col);
                int  num   = 8 - fila;
                setEstado("Caballo inicial en " + letra + num + " — pulsa «Resolver con inicial»", TipoEstado.INFO);
                btnResolverConInicial.setEnabled(true);
                btnElegirInicial.setText("Cambiar posición inicial");
            }
        });

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(new Color(30, 30, 30));
        wrapper.add(panelTablero);
        add(wrapper, BorderLayout.CENTER);
    }

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

        btnResolverConInicial.setEnabled(false);

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

    private enum TipoEstado { INFO, EXITO, ERROR }

    protected void setEstado(String mensaje, TipoEstado tipo) {
        lblEstado.setText(mensaje);
        switch (tipo) {
            case EXITO -> lblEstado.setForeground(new Color(100, 220, 100));
            case ERROR -> lblEstado.setForeground(new Color(220, 80, 80));
            default    -> lblEstado.setForeground(new Color(180, 180, 180));
        }
    }

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