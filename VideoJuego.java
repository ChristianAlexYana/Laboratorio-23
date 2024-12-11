// Creacion de la clase Ejercito
class Ejercito{
    private String reino;
    private List<Soldado> soldados;
}
public Ejercito(String reino) {
    this.reino = reino;
    this.soldados = new ArrayList<>();
}

public String getReino() { return reino; }
public List<Soldado> getSoldados() { return soldados; }

public void agregarSoldado(Soldado soldado) {
    soldados.add(soldado);
}

public void eliminarSoldado(Soldado soldado) {
    soldados.remove(soldado);
}

public boolean estaVacio() {
    return soldados.isEmpty();
}
