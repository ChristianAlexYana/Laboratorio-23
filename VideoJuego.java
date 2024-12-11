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

    public String getTerritorio() { return territorio; }
    public Soldado[][] getTablero() { return tablero; }

    private void posicionarSoldados(Ejercito ejercito) {
        int maxRegularSoldados = 5;
        int regularSoldados = random.nextInt(maxRegularSoldados + 1);
        int totalSoldados = 1 + regularSoldados;
        for (int i = 0; i < totalSoldados; i++) {
            Soldado soldado;
            if (i == 0) {
                soldado = crearUnidadEspecial(ejercito.getReino(), i + 1);
            } else {
                soldado = generarSoldado(ejercito.getReino(), i + 1);
            }
            while (true) {
                int fila = random.nextInt(TAMANIO);
                int columna = random.nextInt(TAMANIO);
                if (tablero[fila][columna] == null) {
                    soldado.setFila(fila + 1);
                    soldado.setColumna(columna + 1);
                    tablero[fila][columna] = soldado;
                    ejercito.agregarSoldado(soldado);
                    break;
                }
            }
        }
    }

    private Soldado generarSoldado(String reino, int contador) {
        int tipo = random.nextInt(4);
        String nombre;
        Soldado soldado;
        switch (tipo) {
            case 0:
                nombre = "E" + contador;
                soldado = new Espadachin(nombre, generarNivelVida("Espadachin"), 10, 8, reino, 1.2);
                break;
            case 1:
                nombre = "A" + contador;
                soldado = new Arquero(nombre, generarNivelVida("Arquero"), 7, 3, reino, random.nextInt(5) + 1);
                break;
            case 2:
                nombre = "C" + contador;
                soldado = new Caballero(nombre, generarNivelVida("Caballero"), 13, 7, reino, "Espada", false);
                break;
            case 3:
                nombre = "L" + contador;
                soldado = new Lancero(nombre, generarNivelVida("Lancero"), 5, 10, reino, 1.5);
                break;
            default:
                nombre = "E" + contador;
                soldado = new Espadachin(nombre, generarNivelVida("Espadachin"), 10, 8, reino, 1.2);
        }
        return soldado;
    }