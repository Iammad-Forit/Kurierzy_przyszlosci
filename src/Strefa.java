public class Strefa extends Lokacja {
    private TypTerenu teren;

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