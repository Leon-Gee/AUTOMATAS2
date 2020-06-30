package Java.ArbolSintactico;

public class Readx extends Statx{
    Expx s1;
    public Readx(Expx st1) {
        s1 = st1;
    }

    public Expx getS1() {
        return s1;
    }

    public String toString(){
        return "Readx: "+s1;
    }
}