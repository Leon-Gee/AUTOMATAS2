package Java;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Map;

public class Principal  {

    private static File archivo;

    public static void main(String[] args) {
        archivo = new File("./ejemplo.txt");
        hacerTarea();
    }

    public static void hacerTarea() {
            // fase analisis
            Scanner analisis = new Scanner(archivo.getAbsolutePath());
            analisis.analizar();
            Parser parser = new Parser(analisis);
            for (String salida : analisis.dameSalidas()) {
                if (salida.charAt(salida.length() - 1) == '$') {
                        System.out.println(salida.substring(0, salida.length() - 1) + "\n");
                    JOptionPane.showMessageDialog(null, salida.substring(0, salida.length() - 1));
                    return;
                }
                    System.out.println(salida + "\n");
            }
            System.out.println("El Scanning no tuvo errores \n");
            // fase parser
            parser.program();
            for (String salida :(ArrayList<String>)  parser.dameSalida()) {
                if (salida.charAt(salida.length() - 1) == '$') {
                    // mostrar errores
                    System.out.println(salida.substring(0, salida.length() - 1) + "\n");
                    return;
                }
                System.out.println(salida + "\n");
            }
            System.out.println( "El Parsing no tuvo errores \n");
            // fase semantico
                for (String salida :(ArrayList<String>)  parser.dameSemantico()) {
                    if (salida.charAt(salida.length() - 1) == '$') {
                        System.out.println( salida.substring(0, salida.length() - 1) + "\n");
                        // JOptionPane.showMessageDialog(null, salida.substring(0, salida.length() - 1));
                        return;
                    }
                    System.out.println( salida + "\n");
                }
        System.out.println("El semantico no tuvo errores \n");
                for (Map.Entry<String, String> var : parser.tablaSimbolos.entrySet()) {
                    // tabla de simbolos aquí iria pero no recuerdo como hacer tablas xd
                    // var.getKey() | var.getValue()
                    // System.out.println();
                }
            // fase código intermedio o bytecode (esto lo vas a necesitar el prox semestre)
                String codigoIntermedio = "";
                for (ByteLine linea : parser.byteCode) {
                    codigoIntermedio += linea.toString() + "\n";
                }
        System.out.println(codigoIntermedio);
        System.out.println("Codigo intermedio generado");

    }
}
