package Java;

import Java.ArbolSintactico.*;

import java.util.*;

public final class Parser{
    private Tokens tkn;
    private Token thisToken;
    private ArrayList<String> estatutos;
    private ArrayList<String> semantico;
    private String ins;
    public ArrayList<Declarax> declaraciones = new ArrayList<Declarax>();
    public ArrayList<Statx> statements = new ArrayList<Statx>();
    public LinkedHashMap<String, String> tablaSimbolos = new LinkedHashMap<String, String>();
    public ArrayList<ByteLine>byteCode = new ArrayList<ByteLine>();
    private Queue<Integer> lineas = new LinkedList<Integer>();
    private Scanner scan;
    private int top=0;
    Programax p;
    private int linea;
    private void advance() {
        thisToken = scan.getToken();
        tkn = thisToken.getTipo();
    }
    private void creaTabladeSimbolos(){
        for(Declarax d:declaraciones) {
            linea= lineas.remove();
            if(tablaSimbolos.containsKey(d.s1)){
                semantico.add("Error semantico en la linea "+ linea+" la variable " +d.s1+" ya ha sido declarada$");
                return;
            }
            tablaSimbolos.put(d.s1,d.s2.getTypex());
        }
    }
    private String validaDeclarada(Idx id){
        if(!tablaSimbolos.containsKey(id.getIdx())){
            semantico.add("Error semantico la variable "+id.getIdx()+ " no declarada linea "+linea+"$");
            return "";
        }
        return tablaSimbolos.get(id.getS1());
    }
    private String validaTipos(Idx id,Constx cons){
        String tipo1 = tablaSimbolos.get(id.getIdx());
        String tipo2 = cons.getType();
        if(tipo1==null||tipo2==null){
            return "";
        }
        if(!tipo1.equals(tipo2)){
            semantico.add("Error semantico en la expresion la variable " +id.getIdx() +" y " + cons.getS1() +" no son compatibles linea "+linea+"$");
            return "";
        }
        return tipo1;

    }
    private String validaTipos(Idx id1,Idx id2){
        String tipo1 = tablaSimbolos.get(id1.getIdx());
        String tipo2 = tablaSimbolos.get(id2.getIdx());
        if(tipo1==null||tipo2==null){
            return "";
        }
        if(!tipo1.equals(tipo2)){
            semantico.add("Error semantico en la expresion la variable " +id1.getIdx() +" y " + id2.getIdx() +" no son compatibles linea "+linea+"$");
            return "";
        }
        return tipo1;

    }
    private String validaExpresion(Expx ex,Statx sta){
        String tipo;
        if(ex instanceof Idx){
            Idx id = (Idx) ex;
            tipo = validaDeclarada(id);
            if(tipo.equals("float"))
                byteCode.add(new ByteLine("fload_",indicevar(id)+"",1));
            else
                byteCode.add(new ByteLine("iload_",indicevar(id)+"",1));
            return tipo;
        } else if(ex instanceof Sumax ){
            Sumax sum = (Sumax) ex;
            validaDeclarada( sum.getS1());
            tipo = validaDeclarada( sum.getS2());
            if(tipo.equals("float")){
                byteCode.add(new ByteLine("fload_", indicevar(sum.getS1()) + "", 1));
                byteCode.add(new ByteLine("fload_", indicevar(sum.getS2()) + "", 1));
                byteCode.add(new ByteLine("fadd", "", 1));
            }else {
                byteCode.add(new ByteLine("iload_", indicevar(sum.getS1()) + "", 1));
                byteCode.add(new ByteLine("iload_", indicevar(sum.getS2()) + "", 1));
                byteCode.add(new ByteLine("iadd", "", 1));
            }
            return validaTipos(sum.getS1(),sum.getS2());
        } else if (ex instanceof  Restax){
            Restax resta = (Restax) ex;
            validaDeclarada( resta.getS1());
            tipo =validaDeclarada( resta.getS2());
            if(tipo.equals("float")){
                byteCode.add(new ByteLine("fload_",indicevar(resta.getS1())+"",1));
                byteCode.add(new ByteLine("fload_",indicevar(resta.getS2())+"",1));
                byteCode.add(new ByteLine("fsub","",1));
            }else {
                byteCode.add(new ByteLine("iload_",indicevar(resta.getS1())+"",1));
                byteCode.add(new ByteLine("iload_",indicevar(resta.getS2())+"",1));
                byteCode.add(new ByteLine("isub","",1));
            }
            return validaTipos(resta.getS1(),resta.getS2());
        } else if (ex instanceof  Multiplicax) {
            Multiplicax multi = (Multiplicax) ex;
            validaDeclarada(multi.getS1());
            tipo = validaDeclarada(multi.getS2());
            if(tipo.equals("float")) {
                byteCode.add(new ByteLine("fload_", indicevar(multi.getS1()) + "", 1));
                byteCode.add(new ByteLine("fload_", indicevar(multi.getS2()) + "", 1));
                byteCode.add(new ByteLine("fmul", "", 1));
            }
            else {
                byteCode.add(new ByteLine("iload_", indicevar(multi.getS1()) + "", 1));
                byteCode.add(new ByteLine("iload_", indicevar(multi.getS2()) + "", 1));
                byteCode.add(new ByteLine("imul", "", 1));
            }
            return validaTipos(multi.getS1(),multi.getS2());
        } else if (ex instanceof  Comparax) {
            Comparax compa = (Comparax) ex;
            validaDeclarada(compa.getS1());
            tipo = validaDeclarada(compa.getS2());
            if(!(sta instanceof Dox))
            {
                if(!tipo.equals("float")) {
                    byteCode.add(new ByteLine("iload_",indicevar(compa.getS1())+"",1));
                    byteCode.add(new ByteLine("iload_",indicevar(compa.getS2())+"",1));
                    byteCode.add(new ByteLine(ByteLine.ponBlancos("if_icmpge",15),""+(ByteLine.siguiente+7),3));
                    byteCode.add(new ByteLine("iconst_","1",1));
                    byteCode.add(new ByteLine(ByteLine.ponBlancos("goto",15),""+(ByteLine.siguiente+4),3));
                    byteCode.add(new ByteLine("iconst_","0",1));
                }
                else{
                    byteCode.add(new ByteLine("fload_",indicevar(compa.getS1())+"",1));
                    byteCode.add(new ByteLine("fload_",indicevar(compa.getS2())+"",1));
                    byteCode.add(new ByteLine("fcmpg","",1));
                    byteCode.add(new ByteLine(ByteLine.ponBlancos("ifge",15),""+(ByteLine.siguiente+7),3));
                    byteCode.add(new ByteLine("iconst_","1",1));
                    byteCode.add(new ByteLine(ByteLine.ponBlancos("goto",15),""+(ByteLine.siguiente+4),3));
                    byteCode.add(new ByteLine("iconst_","0",1));
                }
            }
            return validaTipos(compa.getS1(), compa.getS2());
        }else if(ex instanceof Constx){
            Constx constante =(Constx)ex;
            if(sta instanceof Readx){
                if(!constante.getType().equals("float")){
                    byteCode.add(new ByteLine("iconst_",valconst(constante),1));
                }
                else {
                    byteCode.add(new ByteLine(ByteLine.ponBlancos("ldc",15),""+valconst(constante),2));
                }
            }
            return constante.getType();
        }
        return "";
    }
    private void validaStatement(ArrayList<Statx> statements){
        String tipo;
        for (Statx s:statements) {
            linea = lineas.remove();
            if(s instanceof Dox){
                Dox ciclo = (Dox)s;
                int aux = ByteLine.siguiente;
                validaStatement(ciclo.getS2());
                tipo =validaExpresion(ciclo.getS1(),ciclo);
                if(ciclo.getS1() instanceof Comparax){
                    if(tipo.equals("float")) {
                        byteCode.add(new ByteLine("fcmpg", "", 1));
                        byteCode.add(new ByteLine(ByteLine.ponBlancos("iflt",15), "" + aux, 3));
                    }else
                        byteCode.add(new ByteLine(ByteLine.ponBlancos("if_icmplt",15),""+aux,3));
                } else if(ciclo.getS1() instanceof Constx){
                    if(((Constx)ciclo.getS1()).getS1().equals("true")){
                        byteCode.add(new ByteLine(ByteLine.ponBlancos("goto",15),""+aux,3));
                    }
                } else {
                    byteCode.add(new ByteLine(ByteLine.ponBlancos("ifne",15),""+aux,3));
                }

            }else if( s instanceof Readx){
                Readx  read = (Readx) s;
                validaExpresion(read.getS1(),read);
                byteCode.add(new ByteLine("readln","",3));
            } else if( s instanceof Asignax){
                Asignax asignacion = (Asignax) s;
                validaDeclarada(asignacion.getS1());
                String tipo1 = tablaSimbolos.get(asignacion.getS1().getS1());
                if(asignacion.getS2() instanceof Constx){
                    validaTipos(asignacion.getS1(),(Constx) asignacion.getS2());
                    if(((Constx) asignacion.getS2()).getType().equals("float"))
                    {
                        byteCode.add(new ByteLine(ByteLine.ponBlancos("ldc",15),""+valconst((Constx) asignacion.getS2()),2));
                        byteCode.add(new ByteLine("fstore_",indicevar(asignacion.getS1())+"",1));
                        continue;
                    }
                    byteCode.add(new ByteLine("iconst_",valconst((Constx) asignacion.getS2()),1));
                    byteCode.add(new ByteLine("istore_",indicevar(asignacion.getS1())+"",1));
                    continue;
                }
                String tipo2 = validaExpresion(asignacion.getS2(),asignacion);
                if(!tipo2.equals(tipo1)){
                    semantico.add("Error semantico en la expresion la variable " +asignacion.getS1().getS1() +" y " +tipo2  +" no son compatibles linea "+linea+"$");
                }

            }
        }
    }
    private String valconst(Constx cons){
        if(cons.getType().equals("boolean"))
            return cons.getS1().equals("false")?"0":"1";
        return cons.getS1();
    }
    private void eat(Tokens t) {
        if (thisToken.getClasificacion() == -2) // Si el token es Fin del archivo
        {
            estatutos.add("Token Esperado " + t.getCad() + " Token Recibido <EOF>");
            estatutos.add("Error..... Se esperaba " + t.getCad() + " no <EOF>$");
            return;
        }
        estatutos.add("Token Esperado " + t.getCad() + " Token Recibido " + tkn.getCad()+" en la linea "+thisToken.getLinea());
        if (tkn == t)
            advance();
        else
            estatutos.add("Error..... Se esperaba " + t.getCad() + " no " + tkn.getCad() +" en la linea "+thisToken.getLinea()+ "$");
    }

    public final void program() {
        if (tkn == Tokens.CLASS) {
            ByteLine.siguiente=0;
            eat(Tokens.CLASS);
            eat(Tokens.ID);
            varDeclaration();
            eat(Tokens.LBRACE);
            while (tkn == Tokens.DO || tkn == Tokens.SIR || tkn == Tokens.ID || tkn == Tokens.LBRACE) {
                statement(this.statements);
            }
            eat(Tokens.RBRACE);
            p = new Programax(declaraciones,this.statements);
            creaTabladeSimbolos();
            validaStatement(this.statements);
            byteCode.add(new ByteLine("return","",1));
            //recorre();
        } else {
            estatutos.add("Error$");
        }

    }


    private int indicevar(Idx var){
        int conta = 0;
        for (Map.Entry<String, String> var2 : tablaSimbolos.entrySet()){
            if(var2.getKey().equals(var.getS1()))
                return conta;
            conta++;
        }
        return -1;
    }
    private Statx statement(ArrayList<Statx> statements ) {
        Expx ex;
        if (tkn == Tokens.LBRACE) {
            eat(Tokens.LBRACE);
            while (tkn == Tokens.DO || tkn == Tokens.SIR || tkn == Tokens.ID) {
                statement(statements);
            }
            eat(Tokens.RBRACE);
            return null;
        } else if (tkn == Tokens.DO) {
            ArrayList<Statx>s = new ArrayList<Statx>();
            Dox dox;
            lineas.add(thisToken.getLinea());
            eat(Tokens.DO);
            do {
                statement(s);
            }while (tkn == Tokens.DO || tkn == Tokens.SIR || tkn == Tokens.ID || tkn == Tokens.LBRACE);
            eat(Tokens.UNTIL);
            eat(Tokens.LPAREN);
            ex = expresion();
            eat(Tokens.RPARENT);
            dox = new Dox(s, ex);
            statements.add(dox);
            return dox;
        } else if (tkn == Tokens.SIR) {
            lineas.add(thisToken.getLinea());
            Readx read;
            eat(Tokens.SIR);
            eat(Tokens.LPAREN);
            ex = expresion();
            eat(Tokens.RPARENT);
            eat(Tokens.SEMI);
            read = new Readx(ex);
            statements.add(read);
           return read;
        } else if (tkn == Tokens.ID) {
            lineas.add(thisToken.getLinea());
            Asignax as;
            Idx idx = new Idx(thisToken.getValor());
            eat(Tokens.ID);
            eat(Tokens.EQ);
            ex = expresion();
            eat(Tokens.SEMI);
            as = new Asignax(idx, ex);
            statements.add(as);
            return as;
        }
        return null;
    }

    public final Expx expresion() {
        Idx idx1;
        Idx idx2;
        if (tkn == Tokens.ID) {
            idx1 = new Idx(thisToken.getValor());
            eat(Tokens.ID);
            if (tkn == Tokens.MIN) {
                eat(Tokens.MIN);
                idx2 = new Idx(thisToken.getValor());
                eat(Tokens.ID);
                return new Comparax(idx1, idx2);
            } else if (tkn == Tokens.PLUS) {
                eat(Tokens.PLUS);
                idx2 = new Idx(thisToken.getValor());
                eat(Tokens.ID);
                return new Sumax(idx1, idx2);
            } else if (tkn == Tokens.MINUS) {
                eat(Tokens.MINUS);
                idx2 = new Idx(thisToken.getValor());
                eat(Tokens.ID);
                return new Restax(idx1, idx2);
            } else if (tkn == Tokens.ASTER) {
                eat(Tokens.ASTER);
                idx2 = new Idx(thisToken.getValor());
                eat(Tokens.ID);
                return new Multiplicax(idx1, idx2);
            }
            else{
                return idx1;
            }
        } else if (tkn == Tokens.FALSE) {
            eat(Tokens.FALSE);
            return new Constx( "false",new Typex("boolean"));
        } else if (tkn == Tokens.TRUE) {
            eat(Tokens.TRUE);
            return new Constx( "true",new Typex("boolean"));
        } else if (tkn == Tokens.NFLOA) {
            String valor =thisToken.getValor();
            eat(Tokens.NFLOA);
            return new Constx(valor,new Typex("float"));
        } else if (tkn == Tokens.NINTE) {
            String valor =thisToken.getValor();
            eat(Tokens.NINTE);
            return new Constx(valor, new Typex("int"));
        } else {
            estatutos.add("Error... falta una expresion en la linea "+thisToken.getLinea()+"$");
        }
        return null;
    }

    public final ArrayList<String> dameSalida() {
        return estatutos;
    }
    public final ArrayList<String> dameSemantico() {
        return semantico;
    }

    private final void varDeclaration() {
        if (tkn == Tokens.BOOLEAN) {
            lineas.add(thisToken.getLinea());
            eat(Tokens.BOOLEAN);
            String var = thisToken.getValor();
            eat(Tokens.ID);
            eat(Tokens.SEMI);
            declaraciones.add(new Declarax(var, new Typex("boolean")));

            varDeclaration();
        } else if (tkn == Tokens.INT) {
            lineas.add(thisToken.getLinea());
            eat(Tokens.INT);
            String var = thisToken.getValor();
            eat(Tokens.ID);
            eat(Tokens.SEMI);
            declaraciones.add(new Declarax(var, new Typex("int")));
            varDeclaration();
        } else if (tkn == Tokens.FLOAT) {
            lineas.add(thisToken.getLinea());
            eat(Tokens.FLOAT);
            String var = thisToken.getValor();
            eat(Tokens.ID);
            eat(Tokens.SEMI);
            declaraciones.add(new Declarax(var, new Typex("float")));
            varDeclaration();
        }

    }

    public Parser(Scanner scan) {
        this.scan = scan;
        estatutos = new ArrayList<String>();
        semantico = new ArrayList<String>();
        thisToken = scan.getToken();
        tkn = thisToken.getTipo();
        estatutos.add("Iniciando el parsing");
    }
}
