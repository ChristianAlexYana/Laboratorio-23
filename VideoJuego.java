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
public String getResumen() {
    int espadachines = 0, arqueros = 0, caballeros = 0, lanceros = 0;
    int espadachinReal = 0, caballeroFranco = 0, espadachinTeutonico = 0, espadachinConquistador = 0, caballeroMoro = 0;
    for (Soldado s : soldados) {
        if (s instanceof EspadachinReal) espadachinReal++;
        else if (s instanceof CaballeroFranco) caballeroFranco++;
        else if (s instanceof EspadachinTeutonico) espadachinTeutonico++;
        else if (s instanceof EspadachinConquistador) espadachinConquistador++;
        else if (s instanceof CaballeroMoro) caballeroMoro++;
        else if (s instanceof Espadachin) espadachines++;
        else if (s instanceof Arquero) arqueros++;
        else if (s instanceof Caballero) caballeros++;
        else if (s instanceof Lancero) lanceros++;
    }

    StringBuilder sb = new StringBuilder();
    sb.append("Ejército: ").append(reino).append("\n");
    sb.append("Cantidad total de soldados: ").append(soldados.size()).append("\n");
    sb.append("Espadachines: ").append(espadachines).append("\n");
    sb.append("Arqueros: ").append(arqueros).append("\n");
    sb.append("Caballeros: ").append(caballeros).append("\n");
    sb.append("Lanceros: ").append(lanceros).append("\n");
    sb.append("Espadachín Real: ").append(espadachinReal).append("\n");
    sb.append("Caballero Franco: ").append(caballeroFranco).append("\n");
    sb.append("Espadachín Teutónico: ").append(espadachinTeutonico).append("\n");
    sb.append("Espadachín Conquistador: ").append(espadachinConquistador).append("\n");
    sb.append("Caballero Moro: ").append(caballeroMoro).append("\n");

    return sb.toString();
}


