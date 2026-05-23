public class Paczka {
    private String idPaczki;
    private Miasto punktStartowy;
    private Miasto punktDocelowy;
    private float waga;
    private boolean czyDostarczona;


    public String getIdPaczki() { return idPaczki; }
    public void setIdPaczki(String idPaczki) { this.idPaczki = idPaczki; }
    public Miasto getPunktStartowy() { return punktStartowy; }
    public void setPunktStartowy(Miasto punktStartowy) { this.punktStartowy = punktStartowy; }
    public Miasto getPunktDocelowy() { return punktDocelowy; }
    public void setPunktDocelowy(Miasto punktDocelowy) { this.punktDocelowy = punktDocelowy; }
    public float getWaga() { return waga; }
    public void setWaga(float waga) { this.waga = waga; }
    public boolean isCzyDostarczona() { return czyDostarczona; }
    public void setCzyDostarczona(boolean czyDostarczona) { this.czyDostarczona = czyDostarczona; }
}