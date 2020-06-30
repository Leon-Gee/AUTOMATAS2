package Java.ArbolSintactico;

public class Restax extends Expx {
    private Idx s1;
    private Idx s2;

    public Idx getS1() {
        return s1;
    }

    public Idx getS2() {
        return s2;
    }

    public Restax(Idx s1, Idx s2){
        this.s1 = s1;
        this.s2 = s2;
    }
    public String toString(){
        return "Restax: "+s1+", "+s2;
    }
}