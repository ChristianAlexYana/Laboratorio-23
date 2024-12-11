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
private int generarNivelVida(String tipo) {
        switch (tipo) {
            case "Espadachin": return new Random().nextInt(3) + 8;
            case "Arquero": return new Random().nextInt(3) + 3;
            case "Caballero": return new Random().nextInt(3) + 10;
            case "Lancero": return new Random().nextInt(4) + 5;
            default: return 5;
        }
    }

    private Soldado crearUnidadEspecial(String reino, int contador) {
        String nombre;
        Soldado soldado;
        switch (reino) {
            case "Inglaterra":
                nombre = "ER" + contador;
                soldado = new EspadachinReal(nombre, 12, 10, 8, reino, 1.5);
                break;
            case "Francia":
                nombre = "CF" + contador;
                soldado = new CaballeroFranco(nombre, 15, 13, 7, reino, "Espada", false);
                break;
            case "Sacro Imperio Romano Germánico":
                nombre = "ET" + contador;
                soldado = new EspadachinTeutonico(nombre, 13, 10, 8, reino, 1.4);
                break;
            case "Castilla-Aragón":
                nombre = "EC" + contador;
                soldado = new EspadachinConquistador(nombre, 14, 10, 8, reino, 1.3);
                break;
            case "Moros":
                nombre = "CM" + contador;
                soldado = new CaballeroMoro(nombre, 13, 13, 7, reino, "Lanza", true);
                break;
            default:
                nombre = "E" + contador;
                soldado = new Espadachin(nombre, generarNivelVida("Espadachin"), 10, 8, reino, 1.2);
        }
        return soldado;
    }
    private void aplicarBonusTerritorio() {
        for (Ejercito ejercito : Arrays.asList(ejercito1, ejercito2)) {
            boolean bonus = false;
            switch (ejercito.getReino()) {
                case "Inglaterra":
                    if (territorio.equals("Bosque")) bonus = true;
                    break;
                case "Francia":
                    if (territorio.equals("Campo Abierto")) bonus = true;
                    break;
                case "Castilla-Aragón":
                    if (territorio.equals("Montaña")) bonus = true;
                    break;
                case "Moros":
                    if (territorio.equals("Desierto")) bonus = true;
                    break;
                case "Sacro Imperio Romano Germánico":
                    if (territorio.equals("Bosque") || territorio.equals("Playa") || territorio.equals("Campo Abierto"))
                        bonus = true;
                    break;
            }
            if (bonus) {
                for (Soldado s : ejercito.getSoldados()) {
                    s.aplicarBonus();
                }
                System.out.println("El reino " + ejercito.getReino() + " recibe bono por territorio");
            }
        }
        System.out.println();
    }
    public boolean moverSoldado(int fila, int columna, String direccion, Ejercito e1, Ejercito e2) {
        if (fila < 1 || fila > TAMANIO || columna < 1 || columna > TAMANIO) {
            System.out.println("Coordenadas fuera del mapa.");
            return false;
        }
        Soldado soldado = tablero[fila - 1][columna - 1];
        if (soldado == null) {
            System.out.println("No hay ningún soldado en la posición especificada.");
            return false;
        }

        int nuevaFila = fila;
        int nuevaColumna = columna;
        switch (direccion.toLowerCase()) {
            case "arriba": nuevaFila -= 1; break;
            case "abajo": nuevaFila += 1; break;
            case "izquierda": nuevaColumna -= 1; break;
            case "derecha": nuevaColumna += 1; break;
            default:
                System.out.println("Dirección inválida.");
                return false;
        }

        if (nuevaFila < 1 || nuevaFila > TAMANIO || nuevaColumna < 1 || nuevaColumna > TAMANIO) {
            System.out.println("Movimiento fuera del mapa.");
            return false;
        }

        Soldado objetivo = tablero[nuevaFila - 1][nuevaColumna - 1];
        if (objetivo == null) {
            tablero[nuevaFila - 1][nuevaColumna - 1] = soldado;
            tablero[fila - 1][columna - 1] = null;
            soldado.setFila(nuevaFila);
            soldado.setColumna(nuevaColumna);
            System.out.println(soldado.getNombre() + " se ha movido " + direccion + ".");
        } else {
            if (!soldado.getReino().equals(objetivo.getReino())) {
                System.out.println("¡Batalla!");
                realizarBatalla(soldado, objetivo, fila - 1, columna - 1, nuevaFila - 1, nuevaColumna - 1, e1, e2);
            } else {
                System.out.println("No se puede mover a una posición ocupada por un soldado aliado.");
                return false;
            }
        }
        return true;
    }

