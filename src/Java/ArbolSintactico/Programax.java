package Java.ArbolSintactico;
import java.util.ArrayList;

public class Programax {
    public ArrayList<Declarax> declaraciones;
    public ArrayList<Statx> statement;
    public Programax(ArrayList<Declarax> declaraciones,ArrayList<Statx> statement){
        this.declaraciones = declaraciones;
        this.statement = statement;
    }
    public String toString(){
        return "Programax: "+declaraciones +", "+ statement;
    }
}