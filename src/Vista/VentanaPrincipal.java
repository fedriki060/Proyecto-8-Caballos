package Vista;

import javax.swing.*;
import java.awt.*;

public class VentanaPrincipal extends JFrame {

    private PanelTablero panelTablero;
    private JButton btnResolver;

    public VentanaPrincipal() {
        setTitle("Problema de los 8 Caballos");
        setSize(600, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        panelTablero = new PanelTablero();
        add(panelTablero, BorderLayout.CENTER);

        btnResolver = new JButton("Resolver");
        add(btnResolver, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
        setVisible(true);
    }
}