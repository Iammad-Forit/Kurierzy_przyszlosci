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

    public static void symulujTury(Mapa mapa, int maxTur) {
        System.out.println("\n--- ROZPOCZĘCIE SYMULACJI ---");
        java.util.Random random = new java.util.Random();

        java.time.format.DateTimeFormatter dta = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String czas = dta.format(java.time.LocalDateTime.now());
        ZapiszDoPliku("\n============================================");
        ZapiszDoPliku("Nowa symulacja: " + czas);
        ZapiszDoPliku("\n============================================");

        for (int tura = 1; tura <= maxTur; tura++) {
            System.out.println("\n--- Tura " + tura + " ---");

            ZapiszDoPliku("\n--- Tura: " + String.format("%3d", tura) + " ---");

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

                    if (k.getZdrowie() <= 0) continue;
                    Lokacja celKuriera = k.getEkwipunek().isEmpty() ? null : k.getEkwipunek().get(0).getPunktDocelowy();
                    if (celKuriera == null || k.getObecnaPozycja() == celKuriera) continue;

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

                        k.obliczTrase(mapa, celKuriera);
                    }

                    // Losujemy wartość od 0.0 do 1.0. Jeśli jest mniejsza niż inteligencja kuriera adaptuje się on do zmian.
                    if (random.nextDouble() < k.getInteligencja()) {
                        System.out.println("-> [Inteligencja] Kurier k" + k.getId() + " analizuje sytuację na mapie i aktualizuje trasę");
                        ZapiszDoPliku("   -> [Inteligencja] Kurier k" + k.getId() + " analizuje sytuację na mapie i aktualizuje trasę");

                        k.obliczTrase(mapa, celKuriera);
                    } else {
                        System.out.println("-> [Inteligencja] Kurier K " + k.getId() + " podąża starą trasą");
                        ZapiszDoPliku("   -> [Inteligencja] Kurier K " + k.getId() + " podąża starą trasą");


                    }

                    k.ruch(celKuriera);

                    if (k.getObecnaPozycja() == celKuriera) {
                        System.out.println("\nSukces | Kurier "+ k.getId() +" dostarczył paczkę do miasta " + ((Miasto)celKuriera).getNazwa());
                        ZapiszDoPliku("\nSukces | Kurier "+ k.getId() +" dostarczył paczkę do miasta " + ((Miasto)celKuriera).getNazwa());
                        if (!k.getEkwipunek().isEmpty()) {
                            k.getEkwipunek().get(0).setCzyDostarczona(true);
                        }
                    }

                } else if (agent instanceof Przeciwnik) {
                    Przeciwnik p = (Przeciwnik) agent;
                    Kurier najblizszyCel = null;
                    int minDystans = Integer.MAX_VALUE;

                    for (Agent a : mapa.getAgenci()) {
                        if (a instanceof Kurier) {
                            Kurier k = (Kurier) a;
                            Lokacja celK = k.getEkwipunek().isEmpty() ? null : k.getEkwipunek().get(0).getPunktDocelowy();

                            if (k.getZdrowie() > 0 && k.getObecnaPozycja() != celK) {
                                int dystans = Math.abs(p.getObecnaPozycja().getX() - k.getObecnaPozycja().getX()) +
                                        Math.abs(p.getObecnaPozycja().getY() - k.getObecnaPozycja().getY());
                                if (dystans < minDystans) {
                                    minDystans = dystans;
                                    najblizszyCel = k;
                                }
                            }
                        }
                    }
                    if (najblizszyCel != null) {
                        int hpPrzedAtakiem = najblizszyCel.getZdrowie();
                        p.patroluj(mapa, najblizszyCel);

                        // Sprawdzenie czy Kurier przeżył
                        if (hpPrzedAtakiem > 0 && najblizszyCel.getZdrowie() <= 0) {
                            System.out.println("\nPorażka | Kurier " + najblizszyCel.getId() + " zginął w drodze");
                            ZapiszDoPliku("\nPorażka | Kurier " + najblizszyCel.getId() + " zginął w drodze");
                        }
                    }
                }
            }

            rysujPlansze(mapa);

            boolean kurierzyWstrefie = false;
            for (Agent a : mapa.getAgenci()) {
                if (a instanceof Kurier) {
                    Kurier k = (Kurier) a;
                    Lokacja celK = k.getEkwipunek().isEmpty() ? null : k.getEkwipunek().get(0).getPunktDocelowy();
                    if (k.getZdrowie() > 0 && k.getObecnaPozycja() != celK) {
                        kurierzyWstrefie = true;
                        break;
                    }
                }
            }

            //
            if (!kurierzyWstrefie) {
                System.out.println("\n--- KONIEC SYMULACJI: Wszyscy kurierzy zakończyli swoje zadania ---");
                ZapiszDoPliku("\n--- KONIEC SYMULACJI: Wszyscy kurierzy zakończyli swoje zadania ---");
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
        int rozmiarMapy = 20;
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

                {2, 17}, {3, 17}, {4, 17}, {2, 18}, {3, 18}, {4, 18}, {2, 19}, {3, 19}, {4, 19},
                {7, 18}, {8, 18}, {9, 18}, {11, 18}, {12, 18}, {13, 18}, {14, 18}, {15, 18},
                {7, 19}, {8, 19}, {9, 19}, {10, 19}, {11, 19}, {12, 19}, {13, 19}, {14, 19}, {15, 19},
                {9, 10}, {9, 11}, {10, 10}, {10, 11}, {11, 10}, {11, 11}
        };
        for (int[] poz : pozycjeGor) {
            Strefa gora = new Strefa();
            gora.setTeren(TypTerenu.Góra);
            mapa.ustawLokacje(gora, poz[0], poz[1]);
        }


        int[][] pozycjeLasu = {

                {17, 6}, {18, 6}, {19, 6}, {17, 7}, {18, 7}, {19, 7},
                {18, 8}, {19, 8}, {18, 9}, {19, 9},
                {13, 2}, {14, 2}, {15, 2}, {16, 2}, {17, 2},
                {13, 3}, {14, 3}, {15, 3}, {6, 2}, {7, 2}, {8, 2},
                {7, 8}, {8, 8}, {7, 9}, {8, 9},
                {11, 12}, {12, 12}, {11, 13}, {12, 13}
        };
        for (int[] poz : pozycjeLasu) {
            Strefa las = new Strefa();
            las.setTeren(TypTerenu.Las);
            mapa.ustawLokacje(las, poz[0], poz[1]);
        }

        int[][] pozycjeRzeki = {
                // Wisła
                {10, 18}, {10, 17}, {11, 16}, {12, 15}, {13, 14}, {14, 13},
                {14, 12}, {14, 11}, {13, 10}, {13, 9}, {13, 8}, {12, 7},
                {11, 6}, {10, 5}, {9, 4}, {9, 3}, {10, 2}, {10, 1}, {10, 0},
                // Odra
                {1, 19}, {1, 18}, {1, 17}, {2, 16}, {3, 15}, {3, 14},
                {2, 13}, {1, 12}, {1, 11}, {1, 10}, {1, 9}, {1, 8},
                {1, 7}, {1, 6}, {1, 5}, {2, 4}, {2, 3}, {2, 2}, {3, 1}, {4, 0},
                // Warta
                {7, 10}, {6, 9}, {5, 9}, {5, 8}, {4, 8}, {3, 7}, {2, 7}
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
        mapa.ustawLokacje(wroclaw, 4, 14);

        Miasto krakow = new Miasto();
        krakow.setNazwa("Kraków");
        krakow.setCzyBezpiecznaStrefa(true);
        mapa.ustawLokacje(krakow, 10, 16);

        Miasto warszawa = new Miasto();
        warszawa.setNazwa("Warszawa");
        warszawa.setCzyBezpiecznaStrefa(true);
        mapa.ustawLokacje(warszawa, 12, 8);

        Miasto gdansk = new Miasto();
        gdansk.setNazwa("Gdańsk");
        gdansk.setCzyBezpiecznaStrefa(true);
        mapa.ustawLokacje(gdansk, 11, 1);

        // Inicjalizacja Paczki
        Paczka paczka1 = new Paczka();
        paczka1.setIdPaczki("1");
        paczka1.setPunktStartowy(wroclaw);
        paczka1.setPunktDocelowy(krakow);
        paczka1.setWaga(25);

        Paczka paczka2 = new Paczka();
        paczka2.setIdPaczki("2");
        paczka2.setPunktStartowy(krakow);
        paczka2.setPunktDocelowy(gdansk);

        paczka2.setWaga(30);

        // Inicjalizacja Kuriera
        Kurier kurier1 = new Kurier();
        kurier1.setId("1");
        // <1.0; infinity)
        kurier1.setDoswiadczenie(1.2f);
        // <0.0(Słaba tolerancja na ryzyko); 1.0(Dobra tolerancja na ryzyko)>
        kurier1.setTolerancjaRyzyka(0.1f);
        // <0.0; 1.0>
        kurier1.setInteligencja(0.75f);
        kurier1.setZasiegWidzenia(4);
        kurier1.getEkwipunek().add(paczka1);
        kurier1.setObecnaPozycja(mapa.getPlansza()[4][14]);
        kurier1.obliczTrase(mapa,krakow);
        mapa.getAgenci().add(kurier1);

        Kurier kurier2 = new Kurier();
        kurier2.setId("2");
        // <1.0; infinity)
        kurier2.setDoswiadczenie(1.6f);
        // <0.0(Słaba tolerancja na ryzyko); 1.0(Dobra tolerancja na ryzyko)>
        kurier2.setTolerancjaRyzyka(0.5f);
        // <0.0; 1.0>
        kurier2.setInteligencja(0.50f);
        kurier2.setZasiegWidzenia(3);
        kurier2.getEkwipunek().add(paczka2);
        kurier2.setObecnaPozycja(mapa.getPlansza()[10][16]);
        kurier2.obliczTrase(mapa,gdansk);
        mapa.getAgenci().add(kurier2);

        // Inicjalizacja Przeciwnika
        Przeciwnik przeciwnik1 = new Przeciwnik();
        przeciwnik1.setId("1");
        przeciwnik1.setSilaAtaku(40);
        przeciwnik1.setZasiegWykrywania(3);
        przeciwnik1.setObecnaPozycja(mapa.getPlansza()[7][8]);
        mapa.getAgenci().add(przeciwnik1);

        Przeciwnik przeciwnik2 = new Przeciwnik();
        przeciwnik2.setId("2");
        przeciwnik2.setSilaAtaku(60);
        przeciwnik2.setZasiegWykrywania(2);
        przeciwnik2.setObecnaPozycja(mapa.getPlansza()[8][14]);
        mapa.getAgenci().add(przeciwnik2);

        Przeciwnik przeciwnik3 = new Przeciwnik();
        przeciwnik3.setId("3");
        przeciwnik3.setSilaAtaku(30);
        przeciwnik3.setZasiegWykrywania(4);
        przeciwnik3.setObecnaPozycja(mapa.getPlansza()[16][12]);
        mapa.getAgenci().add(przeciwnik3);



        // Uruchomenie symulacji
        symulujTury(mapa, 30);



        // Aktualne pozycje
        System.out.println("Pozycja Kuriera K "+kurier1.getId()+": (" + kurier1.getObecnaPozycja().getX() + ", " + kurier1.getObecnaPozycja().getY() + ")");
        System.out.println("Pozycja Kuriera K "+kurier2.getId()+": (" + kurier2.getObecnaPozycja().getX() + ", " + kurier2.getObecnaPozycja().getY() + ")");
        System.out.println("Pozycja Przeciwnika P"+przeciwnik1.getId()+": (" + przeciwnik1.getObecnaPozycja().getX() + ", " + przeciwnik1.getObecnaPozycja().getY() + ")");
        System.out.println("Pozycja Przeciwnika P"+przeciwnik2.getId()+": (" + przeciwnik2.getObecnaPozycja().getX() + ", " + przeciwnik2.getObecnaPozycja().getY() + ")");
        System.out.println("Pozycja Przeciwnika P"+przeciwnik3.getId()+": (" + przeciwnik3.getObecnaPozycja().getX() + ", " + przeciwnik3.getObecnaPozycja().getY() + ")");

        rysujPlansze(mapa);
    }
}