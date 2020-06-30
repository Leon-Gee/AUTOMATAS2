package Java.ArbolSintactico;

public class Constx extends Expx {
    private String s1;
    private Typex tp ;

    public String getS1() {
        return s1;
    }

    public Constx(String st1, Typex tp) {
        s1 = st1;
        this.tp = tp ;
    }
    public String toString(){
        return "Constx: "+s1+", "+tp;
    }
    public String getType(){
        return tp.getTypex();
    }
}
