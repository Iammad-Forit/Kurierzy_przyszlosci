public class Paczka {
    private String idPaczki;
    private Miasto punktStartowy;
    private Miasto punktDocelowy;
    private float waga;
    private boolean czyDostarczona;


    public void setIdPaczki(String idPaczki) { this.idPaczki = idPaczki; }
    public void setPunktStartowy(Miasto punktStartowy) { this.punktStartowy = punktStartowy; }
    public void setPunktDocelowy(Miasto punktDocelowy) {
        this.punktDocelowy = punktDocelowy;
    }
    public Lokacja getPunktDocelowy() { return this.punktDocelowy; }
    public float getWaga() { return waga; }
    public void setWaga(float waga) { this.waga = waga; }
    public void setCzyDostarczona(boolean czyDostarczona) { this.czyDostarczona = czyDostarczona; }
}