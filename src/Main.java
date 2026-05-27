import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Main {

    public static void ZapiszDoPliku(String tekst) {
        String nazwaPliku = "Przebieg_symulacji.txt";
        try(PrintWriter out = new PrintWriter(new FileWriter(nazwaPliku, true))) {
            out.println(tekst);
        } catch (IOException e) {
            System.out.println("Błąd zapisu do pliku: " + e.getMessage());
        }
    }

    public static void rysujPlansze(Mapa mapa) {
         String ANSI_RESET = "\u001B[0m";
         String ANSI_BROWN = "\u001B[38;2;139;69;19m";
         String ANSI_GREY = "\u001B[37m";
         String ANSI_GREEN = "\u001B[32m";
         String ANSI_BLUE = "\u001B[34m";


        System.out.println("\n--- MAPA ŚWIATA ---");

        //Tworzenie planszy
        for (int y = 0; y < mapa.getWysokosc(); y++) {

            for (int x = 0; x < mapa.getSzerokosc(); x++) {

                String symbol = ANSI_GREEN + " . " + ANSI_RESET; //Równiny

                Lokacja lokacja = mapa.getPlansza()[x][y];
                if (lokacja instanceof Miasto) {
                    symbol = " M ";
                } else if (lokacja instanceof Strefa) {
                    Strefa strefa = (Strefa) lokacja;
                    if (strefa.getTeren() == TypTerenu.Las) {
                        symbol = ANSI_BROWN + " L " + ANSI_RESET;
                    } else if (strefa.getTeren() == TypTerenu.Góra) {
                        symbol = ANSI_GREY + " ^ " + ANSI_RESET;
                    } else if (strefa.getTeren() == TypTerenu.Rzeka) {
                        symbol = ANSI_BLUE + " ~ " + ANSI_RESET;
                    }
                }

                //Agent stojacy na danym polu przykrywa je
                for (Agent agent : mapa.getAgenci()) {
                    if (agent.getObecnaPozycja() != null &&
                            agent.getObecnaPozycja().getX() == x &&
                            agent.getObecnaPozycja().getY() == y) {

                        if (agent instanceof Kurier) {
                            symbol = " K" + agent.getId();
                        } else if (agent instanceof Przeciwnik) {
                            symbol = " P" + agent.getId();
                        }
                    }
                }


                System.out.print(symbol);
            }

            System.out.println();
        }

        System.out.println("-------------------");
        System.out.println("Legenda: K - Kurier, P - Przeciwnik, M - Miasto, L - Las, ^ - Góry, ~ - Rzeka,. - Równiny\n");
    }

    public static void symulujTury(Mapa mapa, Kurier kurier, Lokacja cel, int maxTur, Przeciwnik przeciwnik) {
        System.out.println("\n--- ROZPOCZĘCIE SYMULACJI ---");
        java.util.Random random = new java.util.Random();

        java.time.format.DateTimeFormatter dta = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String czas = dta.format(java.time.LocalDateTime.now());
        ZapiszDoPliku("\n============================================");
        ZapiszDoPliku("Nowa symulacja: " + czas);
        ZapiszDoPliku("\n============================================");

        for (int tura = 1; tura <= maxTur; tura++) {
            System.out.println("\n--- Tura " + tura + " ---");

            ZapiszDoPliku("--- Tura: " + String.format("%3d", tura) + " ---");

            for (Agent agent : mapa.getAgenci()) {
                String pozycja = "(" + agent.getObecnaPozycja().getX() + ", " + agent.getObecnaPozycja().getY() + ")";
                if (agent instanceof Kurier) {
                    Kurier k = (Kurier) agent;

                    String logK = String.format("   Kurier     ID: %2s | HP: %3d | Pozycja: %s",
                            k.getId(), Math.max(0, k.getZdrowie()), pozycja);
                    ZapiszDoPliku(logK);

                } else if (agent instanceof Przeciwnik) {
                    Przeciwnik p = (Przeciwnik) agent;

                    String logP = String.format("   Przeciwnik ID: %2s |         | Pozycja: %s",
                            p.getId(), pozycja);
                    ZapiszDoPliku(logP);
                }
            }

            ZapiszDoPliku("   Logi:");

            for (Agent agent : mapa.getAgenci()) {
                if (agent instanceof Kurier) {
                    Kurier k = (Kurier) agent;

                    boolean wrogWzasiegu = false;
                    for(Agent a : mapa.getAgenci()) {
                        if (a instanceof Przeciwnik) {
                            int dystans = Math.abs(k.getObecnaPozycja().getX() - a.getObecnaPozycja().getX()) +
                                    Math.abs(k.getObecnaPozycja().getY() - a.getObecnaPozycja().getY());

                            if(dystans <= k.getZasiegWidzenia()) {
                                wrogWzasiegu = true;
                                break;
                            }
                        }
                    }
                    if(wrogWzasiegu) {
                        System.out.println("-> Kurier k" + k.getId() + " zauważył przeciwnika, aktualizuje trasę");
                        ZapiszDoPliku("   -> Kurier k" + k.getId() + " zauważył przeciwnika, aktualizuje trasę");

                        k.obliczTrase(mapa, cel);
                    }

                    // Losujemy wartość od 0.0 do 1.0. Jeśli jest mniejsza niż inteligencja kuriera adaptuje się on do zmian.
                    if (random.nextDouble() < k.getInteligencja()) {
                        System.out.println("-> [Inteligencja] Kurier k" + k.getId() + " analizuje sytuację na mapie i aktualizuje trasę");
                        ZapiszDoPliku("   -> [Inteligencja] Kurier k" + k.getId() + " analizuje sytuację na mapie i aktualizuje trasę");

                        k.obliczTrase(mapa, cel);
                    } else {
                        System.out.println("-> [Inteligencja] Kurier] " + k.getId() + " podąża starą trasą");
                        ZapiszDoPliku("   -> [Inteligencja] Kurier] " + k.getId() + " podąża starą trasą");


                    }

                    agent.ruch(cel);

                } else if (agent instanceof Przeciwnik) {
                    ((Przeciwnik) agent).patroluj(mapa, kurier);
                }
            }

            rysujPlansze(mapa);

            // Sprawdzenie czy Kurier przeżył
            if (kurier.getZdrowie() <= 0) {
                System.out.println("\nPorażka | Kurier " + kurier.getId() + " zginął w drodze");
                ZapiszDoPliku("\nPorażka | Kurier " + kurier.getId() + " zginął w drodze");
                return;
            }

            // Kurier dotarł do celu
            if (kurier.getObecnaPozycja() == cel) {
                System.out.println("\nSukces | Kurier "+ kurier.getId() +" dostarczył paczkę do miasta " + ((Miasto)cel).getNazwa() + "!");
                ZapiszDoPliku("\nSukces | Kurier "+ kurier.getId() +" dostarczył paczkę do miasta " + ((Miasto)cel).getNazwa() + "!");
                if (!kurier.getEkwipunek().isEmpty()) {
                    kurier.getEkwipunek().get(0).setCzyDostarczona(true);
                }
                return;
            }

            // Uśpienie symulacji na 1 sekunde
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("\nOsiągnięto limit tur." + maxTur);
        ZapiszDoPliku("\nOsiągnięto limit tur." + maxTur);
    }

    public static void main(String[] args) {

        System.out.println("Generowanie planszy...\n");

        // Tworzenie Mapy
        int rozmiarMapy = 10;
        Mapa mapa = new Mapa(rozmiarMapy, rozmiarMapy);

        // Wypełnianie całej planszy domyślnym terenem (Równiny)
        for (int x = 0; x < rozmiarMapy; x++) {
            for (int y = 0; y < rozmiarMapy; y++) {
                Strefa rowniny = new Strefa();
                rowniny.setTeren(TypTerenu.Równina);
                mapa.ustawLokacje(rowniny, x, y);
            }
        }

        // Dodanie specyficznego terenu
        int[][] pozycjeGor = {
                {3, 6}, {4, 6}, {6, 3}, {7, 3}, {7, 4}, {7, 7}, {7, 8}, {8, 7}
        };
        for (int[] poz : pozycjeGor) {
            Strefa gora = new Strefa();
            gora.setTeren(TypTerenu.Góra);
            mapa.ustawLokacje(gora, poz[0], poz[1]);
        }


        int[][] pozycjeLasu = {
                {4, 4}, {4, 5}, {5, 4}, {5, 5}, {5, 6}, {6, 5}, {6, 6},
                {1, 2}, {2, 2}, {2, 1}
        };
        for (int[] poz : pozycjeLasu) {
            Strefa las = new Strefa();
            las.setTeren(TypTerenu.Las);
            mapa.ustawLokacje(las, poz[0], poz[1]);
        }

        int[][] pozycjeRzeki = {
                {0,6}, {0,5}, {1,5}, {1,4}, {2,4}, {3,4}, {3,3}, {4,3}, {4,3}, {5,3}
        };

        for (int[] poz : pozycjeRzeki) {
            Strefa rzeka = new Strefa();
            rzeka.setTeren(TypTerenu.Rzeka);
            mapa.ustawLokacje(rzeka, poz[0], poz[1]);
        }

        // Umieszczanie Miast
        Miasto wroclaw = new Miasto();
        wroclaw.setNazwa("Wrocław");
        wroclaw.setCzyBezpiecznaStrefa(true);
        mapa.ustawLokacje(wroclaw, 0, 0); // Wrocław w lewym górnym rogu

        Miasto krakow = new Miasto();
        krakow.setNazwa("Kraków");
        krakow.setCzyBezpiecznaStrefa(true);
        mapa.ustawLokacje(krakow, 9, 9); // Kraków w prawym dolnym rogu

        // Inicjalizacja Paczki
        Paczka paczka1 = new Paczka();
        paczka1.setIdPaczki("1");
        paczka1.setPunktStartowy(wroclaw);
        paczka1.setPunktDocelowy(krakow);
        paczka1.setWaga(25);

        // Inicjalizacja Kuriera
        Kurier kurier = new Kurier();
        kurier.setId("1");
        // <1.0; infinity)
        kurier.setDoswiadczenie(1.2f);
        // <0.0(Słaba tolerancja na ryzyko); 1.0(Dobra tolerancja na ryzyko)>
        kurier.setTolerancjaRyzyka(0.1f);
        // <0.0; 1.0>
        kurier.setInteligencja(0.75f);

        kurier.setZasiegWidzenia(4);

        kurier.getEkwipunek().add(paczka1);
        kurier.setObecnaPozycja(mapa.getPlansza()[0][0]);
        mapa.getAgenci().add(kurier);

        // Inicjalizacja Przeciwnika
        Przeciwnik przeciwnik = new Przeciwnik();
        przeciwnik.setId("1");
        przeciwnik.setSilaAtaku(40);
        przeciwnik.setZasiegWykrywania(2);
        przeciwnik.setObecnaPozycja(mapa.getPlansza()[1][1]);
        mapa.getAgenci().add(przeciwnik);

        // Cel kuriera (Kraków)
        Lokacja cel = krakow;
        kurier.obliczTrase(mapa, cel);

        // Uruchomenie symulacji
        symulujTury(mapa, kurier, cel, 30, przeciwnik);



        // Aktualne pozycje
        System.out.println("Pozycja Kuriera: (" + kurier.getObecnaPozycja().getX() + ", " + kurier.getObecnaPozycja().getY() + ")");
        System.out.println("Pozycja Przeciwnika: (" + przeciwnik.getObecnaPozycja().getX() + ", " + przeciwnik.getObecnaPozycja().getY() + ")");
        rysujPlansze(mapa);
    }
}