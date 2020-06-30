package Java.ArbolSintactico;

import java.util.ArrayList;

public class Dox extends Statx{
    private Expx s1;
    private ArrayList<Statx> s2;

    public Expx getS1() {
        return s1;
    }

    public ArrayList<Statx> getS2() {
        return s2;
    }

    public Dox(ArrayList<Statx> st2, Expx st1) {
        s1 = st1;
        s2 = st2;
    }
    public String toString(){
        return "Dox: "+s2+", "+s1;
    }
}