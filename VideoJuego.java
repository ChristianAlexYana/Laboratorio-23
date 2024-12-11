class Juego {
    private Ejercito ejercito1;
    private Ejercito ejercito2;
    private Mapa mapa;

    public Juego(String reino1, String reino2) {
        ejercito1 = new Ejercito(reino1);
        ejercito2 = new Ejercito(reino2);
        mapa = new Mapa(ejercito1, ejercito2);
    }

    public Ejercito getEjercito1() { return ejercito1; }
    public Ejercito getEjercito2() { return ejercito2; }
    public Mapa getMapa() { return mapa; }

    public String verificarGanador() {
        if (ejercito1.estaVacio()) return ejercito2.getReino();
        if (ejercito2.estaVacio()) return ejercito1.getReino();
        return null;
    }
}

class PanelMapa extends JPanel {
    private Mapa mapa;
    private JLabel[][] celdas;
    private static final int TAM = 10;
    private Juego juego;

    public PanelMapa(Juego juego) {
        this.juego = juego;
        this.mapa = juego.getMapa();
        setLayout(new GridLayout(TAM, TAM));
        celdas = new JLabel[TAM][TAM];
        for (int i = 0; i < TAM; i++) {
            for (int j = 0; j < TAM; j++) {
                celdas[i][j] = new JLabel("", SwingConstants.CENTER);
                celdas[i][j].setBorder(BorderFactory.createLineBorder(Color.BLACK));
                celdas[i][j].setOpaque(true);
                celdas[i][j].setBackground(Color.WHITE);
                add(celdas[i][j]);
            }
        }
        actualizarMapa();
    }
    private int obtenerEjercito(Soldado s) {
        if (juego.getEjercito1().getSoldados().contains(s)) return 1;
        if (juego.getEjercito2().getSoldados().contains(s)) return 2;
        return 0;
    }

    private String generarEtiqueta(Soldado s) {
        int ejercitoNum = obtenerEjercito(s);
        String rep = s.representar();
        String nombre = s.getNombre();
        // El nombre termina con un número. Por ejemplo: E1, E2, A1, EC1
        // Tomamos el último caracter como índice
        char ultimoChar = nombre.charAt(nombre.length()-1);
        return ejercitoNum + ":" + rep + ultimoChar;
    }

    public void actualizarMapa() {
        Soldado[][] tablero = mapa.getTablero();
        for (int i = 0; i < TAM; i++) {
            for (int j = 0; j < TAM; j++) {
                if (tablero[i][j] != null) {
                    celdas[i][j].setText(generarEtiqueta(tablero[i][j]));
                    celdas[i][j].setBackground(Color.LIGHT_GRAY);
                } else {
                    celdas[i][j].setText("");
                    celdas[i][j].setBackground(Color.WHITE);
                }
            }
        }
        repaint();
    }
}

class PanelInformacion extends JPanel {
    private JTextArea areaTexto;
    private Juego juego;

    public PanelInformacion(Juego juego) {
        this.juego = juego;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Información"));

        areaTexto = new JTextArea(10, 50);
        areaTexto.setEditable(false);
        JScrollPane scroll = new JScrollPane(areaTexto);
        add(scroll, BorderLayout.CENTER);

        actualizarInfo();
    }
    public void actualizarInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("Territorio: ").append(juego.getMapa().getTerritorio()).append("\n\n");
        sb.append("=== Jugador 1 ===\n");
        sb.append(juego.getEjercito1().getResumen()).append("\n");
        sb.append("=== Jugador 2 ===\n");
        sb.append(juego.getEjercito2().getResumen()).append("\n");
        areaTexto.setText(sb.toString());
    }
}

class PanelControl extends JPanel {
    private JTextField txtFila;
    private JTextField txtColumna;
    private JComboBox<String> cbDireccion;
    private JCheckBox chkHabilidadEspecial;
    private JButton btnMover;
    private JButton btnFinTurno;

    private Juego juego;
    private PanelMapa panelMapa;
    private PanelInformacion panelInformacion;
    private boolean turnoJugador1 = true;

    public PanelControl(Juego juego, PanelMapa panelMapa, PanelInformacion panelInformacion) {
        this.juego = juego;
        this.panelMapa = panelMapa;
        this.panelInformacion = panelInformacion;

        setLayout(new GridLayout(9, 1, 5, 5));
        setBorder(BorderFactory.createTitledBorder("Control"));

        add(new JLabel("Fila (1-10):"));
        txtFila = new JTextField();
        add(txtFila);

        add(new JLabel("Columna (1-10):"));
        txtColumna = new JTextField();
        add(txtColumna);

        add(new JLabel("Dirección:"));
        cbDireccion = new JComboBox<>(new String[]{"arriba", "abajo", "izquierda", "derecha"});
        add(cbDireccion);

        chkHabilidadEspecial = new JCheckBox("Usar habilidad especial");
        add(chkHabilidadEspecial);

        btnMover = new JButton("Mover");
        add(btnMover);

        btnFinTurno = new JButton("Fin de Turno");
        add(btnFinTurno);

        btnMover.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                moverSoldado();
            }
        });

        btnFinTurno.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cambiarTurno();
            }
        });
    }

    private void moverSoldado() {
        try {
            int fila = Integer.parseInt(txtFila.getText());
            int columna = Integer.parseInt(txtColumna.getText());
            String direccion = (String) cbDireccion.getSelectedItem();

            Ejercito atacante = turnoJugador1 ? juego.getEjercito1() : juego.getEjercito2();
            Ejercito defensor = turnoJugador1 ? juego.getEjercito2() : juego.getEjercito1();

            Soldado soldadoSeleccionado = null;
            for (Soldado s : atacante.getSoldados()) {
                if (s.getFila() == fila && s.getColumna() == columna) {
                    soldadoSeleccionado = s;
                    break;
                }
            }

            if (soldadoSeleccionado == null) {
                JOptionPane.showMessageDialog(this, "No hay ningún soldado suyo en esa posición.");
                return;
            }

            boolean movido = juego.getMapa().moverSoldado(fila, columna, direccion, juego.getEjercito1(), juego.getEjercito2());
            if (movido && chkHabilidadEspecial.isSelected() && soldadoSeleccionado instanceof UnidadEspecial) {
                ((UnidadEspecial)soldadoSeleccionado).habilidadEspecial();
            }

            panelMapa.actualizarMapa();
            panelInformacion.actualizarInfo();

            String ganador = juego.verificarGanador();
            if (ganador != null) {
                JOptionPane.showMessageDialog(this, "¡Ha ganado " + ganador + "!");
                btnMover.setEnabled(false);
                btnFinTurno.setEnabled(false);
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Por favor ingrese números válidos.");
        }
    }

    private void cambiarTurno() {
        turnoJugador1 = !turnoJugador1;
        String turnoActual = turnoJugador1 ? juego.getEjercito1().getReino() : juego.getEjercito2().getReino();
        JOptionPane.showMessageDialog(this, "Turno del ejército: " + turnoActual);
    }
}

class VentanaPrincipal extends JFrame {
    public VentanaPrincipal(Juego juego) {
        setTitle("Juego de Estrategia");
        setSize(1200, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        PanelMapa panelMapa = new PanelMapa(juego);
        PanelInformacion panelInformacion = new PanelInformacion(juego);
        PanelControl panelControl = new PanelControl(juego, panelMapa, panelInformacion);

        add(panelMapa, BorderLayout.CENTER);
        add(panelControl, BorderLayout.EAST);
        add(panelInformacion, BorderLayout.SOUTH);

        setVisible(true);
    }
}

public class Videojuego {
    public static void main(String[] args) {
        String[] reinos = {
            "Inglaterra",
            "Francia",
            "Castilla-Aragón",
            "Moros",
            "Sacro Imperio Romano Germánico"
        };

        String reinoJugador1 = (String) JOptionPane.showInputDialog(null, "Seleccione el reino del Jugador 1:",
                "Selección de Reino", JOptionPane.QUESTION_MESSAGE, null, reinos, reinos[0]);
        if (reinoJugador1 == null) return;

        String reinoJugador2;
        do {
            reinoJugador2 = (String) JOptionPane.showInputDialog(null, "Seleccione el reino del Jugador 2:",
                    "Selección de Reino", JOptionPane.QUESTION_MESSAGE, null, reinos, reinos[1]);
            if (reinoJugador2 == null) return;
        } while (reinoJugador2.equals(reinoJugador1));

        Juego juego = new Juego(reinoJugador1, reinoJugador2);

        SwingUtilities.invokeLater(() -> {
            new VentanaPrincipal(juego);
            JOptionPane.showMessageDialog(null, "Inicio del juego. Turno del ejército: " + reinoJugador1);
        });
    }
}
