/**
 * Klasa reprezentująca konkretny typ dzikiego terenu na mapie (Las, Góra, Rzeka, Równina).
 * Koszt wejścia na to pole jest uzależniony od typu terenu.
 */
public class Strefa extends Lokacja {
    private TypTerenu teren;

    /**
     * Zwraca koszt ruchu w zależności od przypisanego typu terenu.
     * Lasy (3.0), Rzeki (5.0), Góry (10.0), Równiny (1.0).
     *
     * @return Bazowy koszt punktowy przejścia przez strefę.
     */
    @Override
    public float pobierzKosztRuchu() {
        switch (teren) {
            case Las: return 3.0f;
            case Rzeka: return 5.0f;
            case Góra: return 10.0f;
            case Równina: return 1.0f;
            default: return 1.0f;
        }
    }


    public TypTerenu getTeren() { return teren; }
    public void setTeren(TypTerenu teren) { this.teren = teren; }
}