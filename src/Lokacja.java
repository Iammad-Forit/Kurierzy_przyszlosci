/**
 * Abstrakcyjna klasa bazowa dla każdego pojedynczego pola na mapie.
 * Lokacją może być konkretny typ terenu (Strefa) lub bezpieczny punkt (Miasto).
 */
public abstract class Lokacja {
    protected int x;
    protected int y;

    /**
     * Pobiera bazowy koszt wejścia na dane pole.
     * Im wyższy koszt, tym trudniej przebyć tę lokację.
     * @return Wartość reprezentująca trudność terenu.
     */
    public abstract float pobierzKosztRuchu();


    public int getX() { return x; }
    public void setX(int x) { this.x = x; }
    public int getY() { return y; }
    public void setY(int y) { this.y = y; }
}
