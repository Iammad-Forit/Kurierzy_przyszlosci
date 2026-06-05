/**
 * Abstrakcyjna klasa bazowa reprezentująca każdą ruchomą jednostkę w symulacji (Kuriera lub Przeciwnika).
 * Definiuje podstawowe atrybuty agenta, takie jak identyfikator, obecna pozycja na mapie oraz punkty zdrowia.
 */
public abstract class Agent {
    protected String id;
    protected Lokacja obecnaPozycja;
    protected int zdrowie;

    public abstract void ruch(Lokacja cel);


    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public Lokacja getObecnaPozycja() { return obecnaPozycja; }
    public void setObecnaPozycja(Lokacja obecnaPozycja) { this.obecnaPozycja = obecnaPozycja; }
    public int getZdrowie() { return zdrowie; }
    public void setZdrowie(int zdrowie) { this.zdrowie = zdrowie; }

}