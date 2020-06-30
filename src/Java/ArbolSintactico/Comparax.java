package Java.ArbolSintactico;

public class Comparax extends Expx {
    private Idx s1;
    private Idx s2;

    public Idx getS1() {
        return s1;
    }

    public Idx getS2() {
        return s2;
    }

    public Comparax(Idx st1, Idx st2) {
        s1 = st1;
        s2 = st2;
    }
    public String toString(){
        return "Menorx "+s1 +", "+s2;
    }
}
