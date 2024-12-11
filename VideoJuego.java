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