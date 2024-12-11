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

    }
}
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
    private void realizarBatalla(Soldado atacante, Soldado defensor, int filaDefensor, int columnaDefensor, int filaNuevo, int columnaNuevo, Ejercito e1, Ejercito e2) {
        int sumaVida = atacante.getNivelVida() + defensor.getNivelVida();
        double probAtacante = ((double) atacante.getNivelVida() / sumaVida) * 100;
        double probDefensor = ((double) defensor.getNivelVida() / sumaVida) * 100;
        double aleatorio = new Random().nextDouble() * 100;
        Soldado ganador, perdedor;
        if (aleatorio <= probAtacante) {
            ganador = atacante;
            perdedor = defensor;
        } else {
            ganador = defensor;
            perdedor = atacante;
        }

        tablero[filaNuevo][columnaNuevo] = ganador;
        ganador.setFila(filaNuevo + 1);
        ganador.setColumna(columnaNuevo + 1);
        tablero[filaDefensor][columnaDefensor] = null;

        if (e1.getSoldados().contains(perdedor)) {
            e1.eliminarSoldado(perdedor);
        } else {
            e2.eliminarSoldado(perdedor);
        }

        System.out.println("Resultado de la batalla: " + ganador.getNombre() + " ha vencido a " + perdedor.getNombre() + ".");
        System.out.println();

    }
}
