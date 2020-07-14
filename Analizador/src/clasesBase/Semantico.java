/*
 PROYECTO ANALIZADOR LEXICO Y SINTACTICO
 INTEGRANTES:
 - Garcia Aispuro Alan Gerardo.
 - Meza Leon Oscar Oswaldo.
 - Osuna Lizarraga Rubi Guadalupe.
 - Rodelo Cardenas Graciela.
*/

package clasesBase;

import java.util.HashMap;
import java.util.*;


public class Semantico implements Tipo {
	private HashMap<String, TablaSimbolos> tablaSimbolos;
	private Palabritas lexico;
	private String[] columnas;
	private String[][] filas;
	private String errorL;
	ArrayList<String> valorIdentificador=new ArrayList<String>();
	private ArrayList<ArrayList<Token>> tokens;
	private boolean isDeclaracion = false, isused=false;
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
	private String errorcin = "";
	public String generarTablaSimbolos() {
		TablaSimbolos simboloAtributos;
		errorL = "";
		
		//errorL+= "ERRORES SEMÃ�NTICOS ENCONTRADOS: \n";
		int noToken = 0, renglon = 0;
		int x = 0;
		columnas[0] = "Nombre";
		columnas[1] = "Tipo de Dato";
		columnas[2] = "Posicion";
		columnas[3] = "Valor";
		int column = 0, fila = 0;
		for(int i = 0;i<tokens.size();i++) {
			isDeclaracion=false; isused=false;
			String variable = "", valor = "", tipo = "";
			int ident = 0;
			int tipito=0;
			int something = 0;
			boolean isTipo = false, isIgual = false;
			
			for(int a = 0;a<tokens.get(i).size();a++) {
				something = tokens.get(i).get(a).getTipo();
				if(isIgual&& something != PUNTO_COMA) {
					valor += tokens.get(i).get(a).getValor() + " ";
					if(something ==IDENT)
						valorIdentificador.add(tokens.get(i).get(a).getValor());
				}
				
				if(tokens.get(i).get(a).getValor().equals("=")) {
					isIgual = true;
				}
				
				if(something == IDENT&&!isIgual) {
					variable = tokens.get(i).get(a).getValor();
					renglon = tokens.get(i).get(a).getRenglon();
					tipo = tokens.get(i).get(0).getValor();
					tipito=something;
					if(!tipo.equals("class") &&  !tipo.equals("public")&&!tipo.equals(variable)&&tokens.get(i).get(0).getTipo()!=IDENT)
						isDeclaracion =true;
					if (tipo.equals(variable))
						isused=true;
					
				
					}	
					
			}
			
			column = 0;
			if(isDeclaracion) {
				boolean existe=false, datoCorrecto=true;
				//Buscar si ya se encuentra agregada
				if(tablaSimbolos.containsKey(variable)) {
					errorcin+= "LA VARIABLE: ***"+ variable+ "*** QUE SE INTENTA DECLARAR EN LA POSICIÓN: ***"+renglon+"*** YA EXISTE EN LA POSICIÓN: ***" + tablaSimbolos.get(variable).getPosicion() +"***.\n"; 
					existe=true;
				}
				//Verificar el tipo de dato
				if (tipo.equals("int")) {
					datoCorrecto = isEntero(variable,valor,renglon);
					
				}
				if (tipo.equals("boolean")) {
					datoCorrecto = declaracionBoolean(variable,valor,renglon);
					
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
				//Ver si esta declarada
				boolean existe = tablaSimbolos.containsKey(variable), datoCorrecto=true;
				
				
				if (!existe)
					errorcin+= "ERROR SEMANTICO, LA VARIABLE: "+ variable+ " QUE SE INTENTA USAR EN LA POSICIÃ“N: "+renglon+" NO SE ENCUENTRA DECLARADA"+".\n";
				else {
					if(tablaSimbolos.get(variable).getTipoDato() == "int") {
						datoCorrecto=isEntero(variable,valor,renglon);
					}
					if(tablaSimbolos.get(variable).getTipoDato() == "boolean") {
						datoCorrecto=usoBoolean(variable,valor,renglon);
					}
					
						if(datoCorrecto) { // Modificación en la tabla de simbolos :D
							System.out.println(tablaSimbolos.get(variable).getFila());
							filas[tablaSimbolos.get(variable).getFila()][3] = valor;
							tablaSimbolos.get(variable).setValor(valor);
						}
				}
				
				existe=false;
				
			}
			
		}
		if(!errorcin.isEmpty()) errorL+="ERRORES SEMÃ�NTICOS ENCONTRADOS: \n" + errorcin;
		return errorL;
	}
	
	//VERIFICACIÓN DE LAS DECLARACIONES DE VARIABLES
	 //Tipo INT
	// Comprobación de errores para tipo de variables entero
		private boolean isEntero(String variable, String valor,int renglon) {
			boolean valido=true;
			StringTokenizer token = new StringTokenizer(valor, " ");
		    while(token.hasMoreTokens()) {
		    	String tok = token.nextToken(" ");
		    	// SI SE TRATA DE UN IDENTIFICADOR
		    	if(tok.matches("[a-zA-Z]+([a-zA-Z0-9])*")) {
		    		if(tablaSimbolos.containsKey(tok)) { // QUE EL IDENTIFICADOR SE ENCUENTRE PREVIAMENTE DECLARADO
		    			if(!tablaSimbolos.get(tok).getTipoDato().equals("int")) { // EL IDENTIFICADOR COINCIDE CON EL TIPO
		    				errorcin+="SE INTENTO USAR LA VARIABLE: ***"+ tok + "*** QUE ES DE TIPO: *** " +tablaSimbolos.get(tok).getTipoDato()+" *** EN UNA OPERACION DE INT EN LA LINEA "+ renglon + ".\n"; 	
		    				valido = false;
		    			}
		    		}else {
		    			if(tok.equals("true")||tok.equals(false)) 
		    				errorcin+="INTENTA ASIGNAR UN VALOR ***boolean*** EN LA VARIABLE *** "+variable+"*** QUE ES ***int***"+ " EN LA LINEA: "+renglon+ ".\n";
		    			else
		    				errorcin+= "ERROR SEMÁNTICO, LA VARIABLE: "+ tok+ " QUE SE INTENTA USAR EN LA POSICIÓN: "+renglon+" NO SE ENCUENTRA DECLARADA"+".\n";
		    			valido = false;
		    		}
		    		
		    	}else {
		    		// SI HAY OPERADORES LOGICOS
		    		if(tok.matches("[\\&&\\!\\<\\>]")) { // PARA VER SI LA EXPRESION POSEE OPERANDOS LOGICOS
		    			errorcin+="ERROR SEMANTICO: INCOMPATIBILIDAD DE OPERANDOS *** " + tok + " *** NO SE PUEDEN USAR OPERADORES LOGICOS CON EL TIPO DE VARIABLE INT. \n"; 
		    			valido = false;
		    			continue;
		    		}
		    		// EN CASO DE QUE EXISTAN MAS COINCIDENCIAS.... 
		    		if(!tok.matches("[0-9]+([0-9])*")) 
		    			if(!tok.matches("[\\+\\-\\*\\/]"))  
		    				if(!tok.matches("[\\(\\)]")) {
		    					errorcin+="EL VALOR DE LA VARIABLE: ***"+ variable+ "*** DEBE SER DE TIPO INT Y LE INTENTA COLOCAR EL VALOR: *** " + valor+" ***.\n"; 
		    					valido = false;
		    				}
		    		}
		    	
		    }
			return valido;
		}
	//Tipo Boolean
	private boolean declaracionBoolean(String variable, String valor,int renglon) {
		boolean insertar=true;
		boolean valido=false;
		if ((!(valor.equals("true "))) && (!(valor.equals("false ")))) {
			errorcin+="EL VALOR DE LA VARIABLE: ***"+ variable+ "*** DEBE SER DE TIPO BOOLEAN Y LE INTENTA COLOCAR EL VALOR: ***" + valor+"***.\n"; 	
			insertar=false;
		}
		return insertar;
	}
	
	//TipoBoolean
	public boolean usoBoolean (String variable, String valor,int renglon) {
		boolean cambiarValor=true;
		//Ver si está declarada
		if(!tablaSimbolos.containsKey(variable))
			errorcin+= "ERROR SEMÁNTICO, LA VARIABLE: "+ variable+ " QUE SE INTENTA USAR EN LA POSICIÓN: "+renglon+" NO SE ENCUENTRA DECLARADA"+".\n";
		else {
			//Darle un valor nuevo al que tenía
			TablaSimbolos verificar= tablaSimbolos.get(variable);
			
			if(!verificar.getValor().equals(valor)) {
						if(!valorIdentificador.isEmpty()) {
						for(int r=0; r<valorIdentificador.size();r++) {
							//Verificar que exista
							if(tablaSimbolos.containsKey(valorIdentificador.get(r))) {
								//Si existe verificamos el tipo de dato
								if(!tablaSimbolos.get(valorIdentificador.get(r)).getTipoDato().equals(tablaSimbolos.get(variable).getTipoDato())){
									errorcin+="Intenta asignar un tipo de dato*** "+tablaSimbolos.get(valorIdentificador.get(r)).getTipoDato()+ " ***al usar la variable*** "+ valorIdentificador.get(r)+ " ***en un dato*** "+ tablaSimbolos.get(variable).getTipoDato()+ " ***línea: "+ renglon+ ".\n";
									cambiarValor=false;
								}
							}
							else
							{
								cambiarValor=false;
								errorcin+="La variable: "+valorIdentificador.get(r)+ " que intenta usar en la linea "+ renglon+ " no existe"+".\n";
							}
						}
						valorIdentificador.clear();
					}
						
					else {
						errorcin+="Intenta asignar un valor ***int*** en la variable *** "+ variable+"*** que es ***boolean***"+ " en la línea: "+renglon+ ".\n";
						cambiarValor=false;
					}
				
				
			}
		}
		if(cambiarValor)
			tablaSimbolos.get(variable).setValor(valor);
		return cambiarValor;
	}
	
}
