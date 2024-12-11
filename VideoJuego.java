class Mapa {
    private static final int TAMANIO = 10;
    private Soldado[][] tablero;
    private String territorio;
    private Ejercito ejercito1;
    private Ejercito ejercito2;
    private Random random;

    private final String[] TERRITORIOS = {
        "Bosque",
        "Campo Abierto",
        "Montaña",
        "Desierto",
        "Playa"
    };

    public Mapa(Ejercito ejercito1, Ejercito ejercito2) {
        this.ejercito1 = ejercito1;
        this.ejercito2 = ejercito2;
        this.tablero = new Soldado[TAMANIO][TAMANIO];
        this.random = new Random();
        this.territorio = TERRITORIOS[random.nextInt(TERRITORIOS.length)];
        posicionarSoldados(ejercito1);
        posicionarSoldados(ejercito2);
        aplicarBonusTerritorio();
    }