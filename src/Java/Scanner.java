package Java;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Scanner {
    private int lineaNo;
    private int indice;
    private String[] tokens;
    private String token;
    private ArrayList<String> estatutos;

    final Token getToken() {
        if (indice == misTokens.size())
            return new Token("EOF",null, -2,-1);
        token = misTokens.get(indice);
        int linea = lineas.get(indice);
        indice++;
        Token thisToken = new Token(token,null, -1,linea);
        for (Tokens e : Tokens.values()) {
            if (token.equals(e.getCad())) {
                thisToken.setTipo(e);
                if (Arrays.asList("do", "until", "class", "System.in.readln").contains(token)) {
                    thisToken.setClasificacion(0);
                    return thisToken;
                }
                if (Arrays.asList("boolean", "int", "float").contains(token)) {
                    thisToken.setClasificacion(1);
                    return thisToken;
                }
                if (Arrays.asList("*", "+", "-", "<", "=").contains(token)) {
                    thisToken.setClasificacion(2);
                    return thisToken;
                }
                if (Arrays.asList("(", ")", "{", "}", ";").contains(token)) {
                    thisToken.setClasificacion(3);
                    return thisToken;
                }
                if (token.equals("true") || token.equals("false")) {
                    thisToken.setClasificacion(4);
                    return thisToken;
                }
            }
        }
        if (validaIdentificador(token)) {
            thisToken.setClasificacion(5);
            thisToken.setTipo(Tokens.ID);
            return thisToken;
        }
        if (validaInteger(token)) {
            thisToken.setClasificacion(6);
            thisToken.setTipo(Tokens.NINTE);
            return thisToken;
        }
        if (validaFloat(token)) {
            thisToken.setClasificacion(7);
            thisToken.setTipo(Tokens.NFLOA);
            return thisToken;
        }
        return thisToken;
    }

    public final void analizar() {
        Token token = null;
       while (true) {
            token = getToken();
            if (token.getClasificacion() == -2) {
                indice = 0;
                return;
            }
            estatutos.add(token.toString());
        }
    }

    private boolean validaIdentificador(String t) {
        Pattern pat = Pattern.compile("^[a-zA-Z][a-zA-Z[0-9]]*+$");
        Matcher mat = pat.matcher(t);
        return mat.find();
    }

    private boolean validaInteger(String t) {
        Pattern pat = Pattern.compile("^[0-9]+$");
        Matcher mat = pat.matcher(t);
        return mat.find();
    }

    private boolean validaFloat(String t) {
        Pattern pat = Pattern.compile("^[+-][0-9]+.[0-9]++$");
        Matcher mat = pat.matcher(t);
        return mat.find();
    }

    public ArrayList<String> dameSalidas() {
        return estatutos;
    }
    ArrayList<String> misTokens = new ArrayList<String>();
    ArrayList<Integer> lineas = new ArrayList<Integer>();
    public void generaTokens(String ruta) {
        String linea="", token="";
        StringTokenizer tokenizer;
        try{
            FileReader file = new FileReader(ruta);
            BufferedReader archivoEntrada = new BufferedReader(file);
            linea = archivoEntrada.readLine();
            while (linea != null){
                tokenizer = new StringTokenizer(linea);
                while(tokenizer.hasMoreTokens()) {
                    misTokens.add(tokenizer.nextToken());
                    lineas.add(lineaNo);
                }
                lineaNo++;
                linea=archivoEntrada.readLine();
            }
            archivoEntrada.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
    public Scanner(String ruta) {
        estatutos = new ArrayList<String>();
        lineaNo = 1;
        indice = 0;
        token = "";
        generaTokens(ruta);
        estatutos.add("Iniciando scanning");
    }
}
