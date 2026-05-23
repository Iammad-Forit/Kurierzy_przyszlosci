import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Przeciwnik extends Agent {
    private int silaAtaku;
    private int zasiegWykrywania;
    private Random random = new Random();


    public void ruch(Lokacja cel) {

        akcja(cel);
    }
    // do dokonczenia
    public void akcja(Lokacja cel) {
        System.out.println("Przeciwnik " + this.id + " rozgląda się w poszukiwaniu kuriera...");
    }

    public void wykonajLosowyRuch(Mapa mapa) {
        int x = this.obecnaPozycja.getX();
        int y = this.obecnaPozycja.getY();
        List<Lokacja> dostepnePola = new ArrayList<>();

        Lokacja[][] plansza = mapa.getPlansza();

        if (x > 0 && !(plansza[x - 1][y] instanceof Miasto)) {
            dostepnePola.add(plansza[x - 1][y]);
        }
        if (x < mapa.getSzerokosc() - 1 && !(plansza[x + 1][y] instanceof Miasto)) {
            dostepnePola.add(plansza[x + 1][y]);
        }
        if (y > 0 && !(plansza[x][y - 1] instanceof Miasto)) {
            dostepnePola.add(plansza[x][y - 1]);
        }
        if (y < mapa.getWysokosc() - 1 && !(plansza[x][y + 1] instanceof Miasto)) {
            dostepnePola.add(plansza[x][y + 1]);
        }
        if (!dostepnePola.isEmpty()) {
            Lokacja wybranePole = dostepnePola.get(random.nextInt(dostepnePola.size()));
            this.obecnaPozycja = wybranePole;
            System.out.println("Przeciwnik " + this.id + " patroluje teren, przemieszcza się na: (" + wybranePole.getX() + ", " + wybranePole.getY() + ")");
        }
    }

    public void patroluj(Mapa mapa, Kurier cel) {
        int dystans = Math.abs(this.obecnaPozycja.getX() - cel.obecnaPozycja.getX()) + Math.abs(this.obecnaPozycja.getY() - cel.obecnaPozycja.getY());

        if (dystans <= this.zasiegWykrywania) {

            boolean czyKurierBezpieczny = false;
            Lokacja pozycjaKuriera = cel.getObecnaPozycja();

            if(pozycjaKuriera instanceof Miasto) {
                Miasto miasto = (Miasto) pozycjaKuriera;
                czyKurierBezpieczny = miasto.isCzyBezpiecznaStrefa();
            }

            if (czyKurierBezpieczny) {
                wykonajLosowyRuch(mapa);
            } else {
                System.out.println("   -> Przeciwnik P" + this.id + " zauważył kuriera w odległości " + dystans + " pól");
                atakuj(cel);
            }
        } else {
            wykonajLosowyRuch(mapa);
        }
    }


    public void atakuj(Agent cel) {
        System.out.println("   -> Przeciwnik P" + this.id + " atakuje K" + cel.getId() + " zadając " + this.silaAtaku + " obrażeń");

        int obecneZdrowie = cel.getZdrowie();
        cel.setZdrowie(obecneZdrowie - this.silaAtaku);

        System.out.println("   -> Zdrowie kuriera " + cel.getId() + " spada do: " + Math.max(0, cel.getZdrowie()) + " HP.");
    }


    public int getSilaAtaku() { return silaAtaku; }
    public void setSilaAtaku(int silaAtaku) { this.silaAtaku = silaAtaku; }
    public int getZasiegWykrywania() { return zasiegWykrywania; }
    public void setZasiegWykrywania(int zasiegWykrywania) { this.zasiegWykrywania = zasiegWykrywania; }
}