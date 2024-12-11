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