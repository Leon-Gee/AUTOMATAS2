package Java.ArbolSintactico;

public class Declarax {
    public String s1;
    public Typex s2;

    public Declarax(String st1, Typex st2) {
        s1 = st1;
        s2 = st2;
    }
    public String toString(){
        return "Declarax: "+s1+", "+s2;
    }
}
