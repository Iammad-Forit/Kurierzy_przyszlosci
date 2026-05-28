import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class Kurier extends Agent {
    private float inteligencja;
    private float doswiadczenie;
    private float tolerancjaRyzyka;
    private List<Paczka> ekwipunek = new ArrayList<>();
    private int zasiegWidzenia;

    private List<Lokacja> zaplanowanaTrasa = new ArrayList<>();

    //Algorytm Dijkstry
    private static class Wezel implements Comparable<Wezel> {
        Lokacja lokacja;
        float kosztOdStartu;

        public Wezel(Lokacja lokacja, float kosztOdStartu) {
            this.lokacja = lokacja;
            this.kosztOdStartu = kosztOdStartu;
        }

        @Override
        public int compareTo(Wezel inny) {
            return Float.compare(this.kosztOdStartu, inny.kosztOdStartu);
        }
    }

    @Override
    public void ruch(Lokacja cel) {
        if (!zaplanowanaTrasa.isEmpty()) {

            Lokacja nastepnyKrok = zaplanowanaTrasa.remove(0);
            this.obecnaPozycja = nastepnyKrok;
            System.out.println("Kurier " + this.id + " przemieszcza się na: (" + nastepnyKrok.getX() + ", " + nastepnyKrok.getY() + ")");

            if (this.obecnaPozycja == cel) {
                System.out.println("Kurier " + this.id + " dotarł do celu podróży");
            }
        } else {
            System.out.println("Kurier " + this.id + " nie ma zaplanowanej trasy lub już dotarł do celu.");
        }
    }

    public void obliczTrase(Mapa mapa, Lokacja cel) {
        PriorityQueue<Wezel> otwarte = new PriorityQueue<>();
        Map<Lokacja, Float> koszty = new HashMap<>();
        Map<Lokacja, Lokacja> poprzednicy = new HashMap<>();

        otwarte.add(new Wezel(this.obecnaPozycja, 0f));
        koszty.put(this.obecnaPozycja, 0f);

        while (!otwarte.isEmpty()) {
            Wezel aktualnyWezel = otwarte.poll();
            Lokacja aktualna = aktualnyWezel.lokacja;

            if (aktualna == cel) {
                this.zaplanowanaTrasa = odtworzTrase(poprzednicy, aktualna);
                return;
            }


            for (Lokacja sasiad : pobierzSasiadow(mapa, aktualna)) {

                float nowyKoszt = koszty.get(aktualna) + mapa.obliczKosztRuchu(this, sasiad);

                if (!koszty.containsKey(sasiad) || nowyKoszt < koszty.get(sasiad)) {
                    koszty.put(sasiad, nowyKoszt);
                    poprzednicy.put(sasiad, aktualna);
                    otwarte.add(new Wezel(sasiad, nowyKoszt));
                }
            }
        }
    }

    private List<Lokacja> odtworzTrase(Map<Lokacja, Lokacja> poprzednicy, Lokacja aktualna) {
        List<Lokacja> trasa = new ArrayList<>();
        while (poprzednicy.containsKey(aktualna)) {
            trasa.add(aktualna);
            aktualna = poprzednicy.get(aktualna);
        }
        Collections.reverse(trasa);
        return trasa;
    }

    private List<Lokacja> pobierzSasiadow(Mapa mapa, Lokacja lokacja) {
        List<Lokacja> sasiedzi = new ArrayList<>();
        int x = lokacja.getX();
        int y = lokacja.getY();
        Lokacja[][] plansza = mapa.getPlansza();

        // Sprawdzanie granic mapy przed dodaniem sąsiada do sprawdzenia
        if (x > 0) sasiedzi.add(plansza[x - 1][y]);
        if (x < mapa.getSzerokosc() - 1) sasiedzi.add(plansza[x + 1][y]);
        if (y > 0) sasiedzi.add(plansza[x][y - 1]);
        if (y < mapa.getWysokosc() - 1) sasiedzi.add(plansza[x][y + 1]);

        return sasiedzi;
    }


    public double getInteligencja() { return inteligencja; }
    public void setInteligencja(float inteligencja) { this.inteligencja = inteligencja; }
    public double getDoswiadczenie() { return doswiadczenie; }

    // Zmiana parametru zdrowia na podstawie doswiadczenia kuriera
    public void setDoswiadczenie(float doswiadczenie) {
        this.doswiadczenie = doswiadczenie;
        int Zdrowie = (int) (100 * doswiadczenie);
        this.setZdrowie(Zdrowie);
    }

    public int getZasiegWidzenia() {return zasiegWidzenia;}
    public void setZasiegWidzenia(int zasiegWidzenia) {this.zasiegWidzenia = zasiegWidzenia;}
    public float getTolerancjaRyzyka() { return tolerancjaRyzyka; }
    public void setTolerancjaRyzyka(float tolerancjaRyzyka) { this.tolerancjaRyzyka = tolerancjaRyzyka; }
    public List<Paczka> getEkwipunek() { return ekwipunek; }
}