import java.util.ArrayList;
import java.util.List;

public class Mapa {
    private int szerokosc;
    private int wysokosc;
    private Lokacja[][] plansza;
    private List<Agent> agenci = new ArrayList<>();


    public Mapa(int szerokosc, int wysokosc) {
        this.szerokosc = szerokosc;
        this.wysokosc = wysokosc;
        this.plansza = new Lokacja[szerokosc][wysokosc];
    }

    public float obliczKosztRuchu(Kurier k, Lokacja l) {
        float bazowyKoszt = l.pobierzKosztRuchu();


        int lacznaWaga = 10;
        if (!k.getEkwipunek().isEmpty()) {
            lacznaWaga = 0;
            for (Paczka p : k.getEkwipunek()) {
                lacznaWaga += p.getWaga();
            }
        }

        double wykladnik = lacznaWaga / 10.0;

        float kosztPoWadze = (float) Math.pow(bazowyKoszt, wykladnik);

        //Wpływ doświadczenia
        float mnoznikDoswiadczenia = (float) Math.max(0.1, k.getDoswiadczenie());
        float zmodyfikowanyKoszt = kosztPoWadze / mnoznikDoswiadczenia;

        //Wpływ tolerancji ryzyka
        float karaZaRyzyko = 0;
        boolean bezpiecznePole = (l instanceof Miasto) && ((Miasto)l).isCzyBezpiecznaStrefa();

        if (!bezpiecznePole) {
            for (Agent agent : this.agenci) {
                if (agent instanceof Przeciwnik) {
                    Przeciwnik przeciwnik = (Przeciwnik) agent;
                    int dystans = Math.abs(l.getX() - przeciwnik.getObecnaPozycja().getX()) +
                            Math.abs(l.getY() - przeciwnik.getObecnaPozycja().getY());

                    if (dystans <= k.getZasiegWidzenia()) {
                        float wskaznikStrachu = Math.max(0.0f, 1.0f - k.getTolerancjaRyzyka());
                        karaZaRyzyko += 20.0f * wskaznikStrachu;
                    }
                }
            }
        }

        return Math.max(1.0f, zmodyfikowanyKoszt + karaZaRyzyko);
    }

    public void ustawLokacje(Lokacja lokacja, int x, int y) {
        if (x >= 0 && x < szerokosc && y >= 0 && y < wysokosc) {
            lokacja.setX(x);
            lokacja.setY(y);
            plansza[x][y] = lokacja;
        }
    }

    public Lokacja[][] getPlansza() { return plansza; }
    public List<Agent> getAgenci() { return agenci; }
    public int getSzerokosc() { return szerokosc; }
    public int getWysokosc() { return wysokosc; }
}