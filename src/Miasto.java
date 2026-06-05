/**
 * Klasa reprezentująca miasto.
 * Jest to lokacja będąca punktem startowym lub docelowym paczki.
 * Koszt bazowy ruchu przez miasto wynosi 1.
 */

public class Miasto extends Lokacja {
    private String nazwa;
    private boolean czyBezpiecznaStrefa;

    @Override
    public float pobierzKosztRuchu() {
        return 1.0f;
    }


    public String getNazwa() { return nazwa; }
    public void setNazwa(String nazwa) { this.nazwa = nazwa; }
    public boolean isCzyBezpiecznaStrefa() { return czyBezpiecznaStrefa; }
    public void setCzyBezpiecznaStrefa(boolean czyBezpiecznaStrefa) { this.czyBezpiecznaStrefa = czyBezpiecznaStrefa; }
}