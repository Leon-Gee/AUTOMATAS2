package Java;

public class ByteLine {
    static int siguiente =0;
    int ind;
    String inst,var;
    ByteLine(String inst,String var, int lenght){
        this.ind=siguiente;
        this.inst=inst;
        this.var=var;
        siguiente+=lenght;

    }
    public String toString (){
        return ponBlancos(ind+":",8)+inst+""+var;
    }
    public static String ponBlancos(String texto,int cuantos){
        while (texto.length()<cuantos)
            texto=texto+" ";
        return texto;
    }
}
