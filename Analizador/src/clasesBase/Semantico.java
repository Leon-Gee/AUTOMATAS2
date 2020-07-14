package clasesBase;

import java.util.HashMap;
import java.util.*;


public class Semantico implements Tipo {
	private HashMap<String, TablaSimbolos> tablaSimbolos;
	private Palabritas lexico;
	private String[] columnas;
	private String[][] filas;
	private String errorL;
	private ArrayList<ArrayList<Token>> tokens;
	public Semantico(ArrayList<ArrayList<Token>> tokens) {
		this.tokens = tokens;
		columnas = new String[4];
		filas = new String[tokens.size()][4];
		tablaSimbolos = new HashMap<String, TablaSimbolos>();
	}
	
	public String[] getColumnas() {
		return columnas;
	}
	public String[][] getFilas(){
		return filas;
	}
	public String generarTablaSimbolos() {
		TablaSimbolos simboloAtributos;
		errorL = "";
		errorL+= "ERRORES SEMÁNTICOS ENCONTRADOS: \n";
		int noToken = 0, renglon = 0;
		int x = 0;
		columnas[0] = "Nombre";
		columnas[1] = "Tipo de Dato";
		columnas[2] = "Posicion";
		columnas[3] = "Valor";
		int column = 0, fila = 0;
		for(int i = 0;i<tokens.size();i++) {
			String variable = "", valor = "", tipo = "";
			int ident = 0;
			int tipito=0;
			int something = 0;
			boolean isTipo = false, isIgual = false, isDeclaracion = false, isused=false;
			
			for(int a = 0;a<tokens.get(i).size();a++) {
					something = tokens.get(i).get(a).getTipo();
					if(isIgual&& something != PUNTO_COMA)
						valor += tokens.get(i).get(a).getValor() + " ";
					
					if(tokens.get(i).get(a).getValor().equals("=")) {
						isIgual = true;
					}
					
					if(something == IDENT) {
						if(!isIgual)
							variable = tokens.get(i).get(a).getValor();
						renglon = tokens.get(i).get(a).getRenglon();
						tipo = tokens.get(i).get(0).getValor();
						isDeclaracion = !tipo.equals("class") &&  !tipo.equals("public")&&!tipo.equals(variable); 
						isused = tipo.equals(variable);
					}	
					
			}
			
			column = 0;
			if(isDeclaracion) {
				boolean existe=false, datoCorrecto=true;
				//Buscar si ya se encuentra agregada
				if(tablaSimbolos.containsKey(variable)) {
					errorL+= "LA VARIABLE: ***"+ variable+ "*** QUE SE INTENTA DECLARAR EN LA POSICIÓN: ***"+renglon+"*** YA EXISTE EN LA POSICIÓN: ***" + tablaSimbolos.get(variable).getPosicion() +"***.\n"; 
					existe=true;
				}
				//Verificar el tipo de dato
				if (tipo.equals("int")) {
					datoCorrecto = isEntero(variable,valor,renglon);
					
				}
				if (tipo.equals("boolean")) {
					datoCorrecto = isBoolean(variable,valor);
					if(!datoCorrecto) 
						errorL+="EL VALOR DE LA VARIABLE: ***"+ variable+ "*** DEBE SER DE TIPO BOOLEAN Y LE INTENTA COLOCAR EL VALOR: *** " + valor +" ***.\n"; 	
				
				}
				if (!existe&&datoCorrecto) {
					filas[fila][column] = variable;
					++column;
					filas[fila][column] = tipo;
					++column;
					filas[fila][column] = renglon +"";
					++column;
					filas[fila][column] = valor;
					
					
					
					tablaSimbolos.put(variable, new TablaSimbolos(variable, tipo, renglon, valor,fila));
					fila++;
				}
				existe=false;
				datoCorrecto=true;
			}
			if (isused) {
				//Ver si está declarada
				boolean existe = tablaSimbolos.containsKey(variable), datoCorrecto=true;
				
				
				if (!existe)
					errorL+= "ERROR SEMÁNTICO, LA VARIABLE: "+ variable+ " QUE SE INTENTA USAR EN LA POSICIÓN: "+renglon+" NO SE ENCUENTRA DECLARADA"+".\n";
				else {
					if(tablaSimbolos.get(variable).getTipoDato() == "int") {
						datoCorrecto = isEntero(variable,valor,renglon);
					}
					if(tablaSimbolos.get(variable).getTipoDato() == "boolean") {
						datoCorrecto = isBoolean(variable,valor);
						if(!datoCorrecto) 
							errorL+="EL VALOR DE LA VARIABLE: ***"+ variable+ "*** DEBE SER DE TIPO BOOLEAN Y LE INTENTA COLOCAR EL VALOR: *** " + valor+" ***.\n"; 	
					}
					
						if(datoCorrecto) {
							System.out.println(tablaSimbolos.get(variable).getFila());
							filas[tablaSimbolos.get(variable).getFila()][3] = valor;
							tablaSimbolos.get(variable).setValor(valor);
						}
				}
				
				existe=false;
				
			}
		}
		return errorL;
	}
	/*
	 * 
	 * */
	private boolean isEntero(String variable, String valor,int renglon) {
		boolean valido=true;
		StringTokenizer token = new StringTokenizer(valor, " ");
	    while(token.hasMoreTokens()) {
	    	String tok = token.nextToken(" ");
	    	// SI SE TRATA DE UN IDENTIFICADOR
	    	if(tok.matches("[a-zA-Z]+([a-zA-Z0-9])*")) {
	    		if(tablaSimbolos.containsKey(tok)) {
	    			if(!tablaSimbolos.get(tok).getTipoDato().equals("int")) {
	    				errorL+="SE INTENTO USAR LA VARIABLE: ***"+ tok + "*** QUE ES DE TIPO: *** " +tablaSimbolos.get(tok).getTipoDato()+" *** EN UNA OPERACION DE INT EN LA LINEA "+ renglon + ".\n"; 	
	    				valido = false;
	    			}
	    		}else {
	    			errorL+= "ERROR SEMÁNTICO, LA VARIABLE: "+ tok+ " QUE SE INTENTA USAR EN LA POSICIÓN: "+renglon+" NO SE ENCUENTRA DECLARADA"+".\n";
	    			valido = false;
	    		}
	    		
	    	}else {
	    		// SI HAY OPERADORES LOGICOS
	    		if(tok.matches("[\\&&\\!\\<\\>]")) {
	    			errorL+="ERROR SEMANTICO: INCOMPATIBILIDAD DE OPERANDOS *** " + tok + " *** NO SE PUEDEN USAR OPERADORES LOGICOS CON EL TIPO DE VARIABLE INT. \n"; 
	    			valido = false;
	    			continue;
	    		}
	    		// EN CASO DE QUE EXISTAN MAS COINCIDENCIAS.... 
	    		if(!tok.matches("[0-9]+([0-9])*"))
	    			if(!tok.matches("[\\+\\-\\*\\/]"))  
	    				if(!tok.matches("[\\(\\)]")) {
	    					errorL+="EL VALOR DE LA VARIABLE: ***"+ variable+ "*** DEBE SER DE TIPO INT Y LE INTENTA COLOCAR EL VALOR: *** " + valor+" ***.\n"; 
	    					valido = false;
	    				}
	    		}
	    	
	    }
		return valido;
	}
	private boolean isBoolean(String variable, String valor) {
		boolean datoCorrecto = true; 
		StringTokenizer token = new StringTokenizer(valor, " ");
		
		if ((!(valor.equals("true "))) && (!(valor.equals("false ")))) {
			datoCorrecto = false;
		}
		return datoCorrecto;
	}
}
