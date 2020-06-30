package Java;
public enum Tokens {
    CLASS ("class"),
    INT ("int"),
    FLOAT ("float"),
    BOOLEAN ("boolean"),
    DO ("do"),
    UNTIL ("until"),
    SIR ("System.in.readln"),
    LBRACE ("{"),
    RBRACE ("}"),
    LPAREN ("("),
    RPARENT (")"),
    PLUS ("+"),
    MINUS ("-"),
    ASTER ("*"),
    MIN ("<"),
    EQ ("="),
    ID ("Identificador"),
    NINTE ("Numero entero"),
    NFLOA ("Numero float"),
    SEMI (";"),
    FALSE ("false"),
    TRUE ("true");
    private String cad;
    public String getCad(){
        return cad;
    }
    Tokens(String cad) {
        this.cad=cad;
    }
}