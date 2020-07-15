/*PROYECTO ANALIZADOR LEXICO Y SINTACTICO
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
	//private Palabritas lexico;
	private String[] columnas;
	private String[][] filas;
	private String errorL = "";
	private ArrayList<Integer> alcance; // Alcance para las variables, este actuara como pila...
	private ArrayList<String> valorIdentificador=new ArrayList<String>();
	private ArrayList<ArrayList<Token>> tokens;
	private boolean isDeclaracion = false, isused=false;
	public Semantico(ArrayList<ArrayList<Token>> tokens) {
		this.tokens = tokens;
		columnas = new String[4];
		filas = new String[tokens.size()][4];
		tablaSimbolos = new HashMap<String, TablaSimbolos>();
		alcance = new ArrayList<Integer>();
	}
	
	public String[] getColumnas() {
		return columnas;
	}
	public String[][] getFilas(){
		return filas;
	}
	String errorcin = "";
	int fila = 0, column = 0;
	// Generar la tabla de simbolos
	public String generarTablaSimbolos() {
		
		String columnas[] = {"Nombre","Tipo de Dato","Posicion","Valor"};
		this.columnas = columnas;
		
		for(int i = 0;i<tokens.size();i++) {
			switch(tokens.get(i).get(0).getTipo()) {
				case PUBLIC:
					alcance.add(i); // por si hay muchos metodos xd
				break;
				case WHILE: // También pueden existir muchas sentencias while, y por eso su valor
					alcance.add(i);
				break;
				case CLASS: // Dioquis pero para decir que se asigna
					alcance.add(CLASS);
				break; 
				// NO ES QUE VAYAN A TENER LA MISMA FUNCIONALIDAD, AUN NO LES ASIGNO UNA
				case LLAVE_A:
				case LLAVE_C:
				break;
				// Falta uno por si es LLAVE_A
				default: // declaracion o uso de variable	
					declaracionVariable(tokens.get(i));
				break;
			}

		}
		if(!errorcin.isEmpty()) errorL+="ERRORES SEMANTICOS ENCONTRADOS: \n"+ errorcin;
		return errorL;
	}
	// Si es declaración de variable
	private void declaracionVariable(ArrayList<Token> varDeclar) {
		String valor = "",variable = "";
		int i = 0, renglon = 0, igual = 0;
		boolean datoCorrecto = false;
		//boolean isObject = false;
		String tipo = "";
		System.out.println(varDeclar.get(0).getValor());
		renglon = varDeclar.get(0).getRenglon();
		switch(varDeclar.get(0).getTipo()) {
		case IDENT:
			if(varDeclar.get(1).getTipo() == IGUAL) { // si es directamente una variable
				variable = varDeclar.get(0).getValor();
				i = 2;
			}
			break;
			default:
				tipo = varDeclar.get(0).getValor();
				variable = varDeclar.get(1).getValor();
				i = 3;
				igual = i-1;
			break;
		}
		 
		System.out.println("variable: " +  variable);
		// OBTENER EL VALOR DE LA VARIABLE
		while(i<varDeclar.size()) {
			if(varDeclar.get(i).getTipo() != PUNTO_COMA) {
				valor += varDeclar.get(i).getValor() + " ";
			}
			i++;
		}
		if(!tipo.isEmpty()) { // DECLARACION VARIABLE
			if(tipo.equals("int")) {
				datoCorrecto = isEntero(variable,valor,renglon);
			}
			if(tipo.equals("boolean")) {
				datoCorrecto = verificarBoolean(variable,valor,renglon,tipo);
			}
			boolean existe = tablaSimbolos.containsKey(variable);
			if(existe) errorcin += errorcin+= "LA VARIABLE: ***"+ variable+ "*** QUE SE INTENTA DECLARAR EN LA POSICION: ***"+renglon+"*** YA EXISTE EN LA POSICION: ***" + tablaSimbolos.get(variable).getPosicion() +"***.\n"; 
			else {
				if(datoCorrecto) {
					filas[fila][0] = variable;
					filas[fila][1] = tipo;
					filas[fila][2] = renglon +"";
					filas[fila][3] = valor;
						
					tablaSimbolos.put(variable, new TablaSimbolos(variable, tipo, renglon, valor,fila, CLASS));
					fila++;
				}
			}
		}else { // USO DE UNA VARIABLE
			if(tablaSimbolos.containsKey(variable)) {
				if(tablaSimbolos.get(variable).getTipoDato().equals( "int" )) {
					datoCorrecto=isEntero(variable,valor,renglon);
				}
				if(tablaSimbolos.get(variable).getTipoDato().equals("boolean")) {
					datoCorrecto=usoBoolean(variable,valor,renglon,tipo);
				}
			
				if(datoCorrecto) { // ModificaciÃ³n en la tabla de simbolos :D
					filas[tablaSimbolos.get(variable).getFila()][3] = valor;
					tablaSimbolos.get(variable).setValor(valor);
				}
			}else {
				errorcin+= "ERROR SEMANTICO, LA VARIABLE: "+ variable+ " QUE SE INTENTA USAR EN LA POSICION: "+renglon+" NO SE ENCUENTRA DECLARADA"+".\n";
			}
		}
			
	}
	
	
	public boolean usoBoolean (String variable, String valor,int renglon,String tipo) {
		boolean cambiarValor=true;
		//Ver si estÃƒÂ¡ declarada
		if(!tablaSimbolos.containsKey(variable))
			errorcin+= "ERROR SEMANTICO, LA VARIABLE: "+ variable+ " QUE SE INTENTA USAR EN LA POSICION: "+renglon+" NO SE ENCUENTRA DECLARADA"+".\n";
		else {
			//Darle un valor nuevo al que tenÃƒÂ­a
			TablaSimbolos verificar= tablaSimbolos.get(variable);
			
			if(!verificar.getValor().equals(valor)) {
					cambiarValor=verificarBoolean(variable,  valor, renglon, tipo);
			}
		}
		if(cambiarValor)
			tablaSimbolos.get(variable).setValor(valor);
		return cambiarValor;
	}
	
	//VERIFICACIÓN DE LAS DECLARACIONES DE VARIABLES
		 //Tipo INT
		private boolean isEntero(String variable, String valor,int renglon) {
			boolean valido=true;
			StringTokenizer token = new StringTokenizer(valor, " ");
		    while(token.hasMoreTokens()) {
		    	valorIdentificador.clear();
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

		private boolean verificarBoolean (String variable, String valor,int renglon,String tipo) {
			boolean cambiarValor=true;
			int numerito=0; int identificador=0;
			if ((!(valor.equals("true "))) && (!(valor.equals("false ")))) {
				//Verificar que sea un identificador
				if(!valorIdentificador.isEmpty()) {
					for(int r=0; r<valorIdentificador.size();r++) {
						
						//Verificar que exista
						if(tablaSimbolos.containsKey(valorIdentificador.get(r))) {
							identificador++;
							//Si existe verificamos el tipo de dato
							if(!tablaSimbolos.get(valorIdentificador.get(r)).getTipoDato().equals(tipo)){
								//Verificar si se trata de int
								if(tablaSimbolos.get(valorIdentificador.get(r)).getTipoDato().equals("int")) {
									identificador++;
									//Verifico que haya un numeros o signos
									char numeros []= {'0','1','2','3','4','5','6','7','8','9','&','<','>','!'};
									for (int a=0; a<valor.length();a++) {
										for (int b=0; b<numeros.length;b++) {
											if (valor.charAt(a)==numeros[b]) {
												numerito=300;
												break;
											}
											if (b==numeros.length-1&&numerito!=300&&identificador==0) {
												cambiarValor=false;
												break;
											}
											if(valor.charAt(a)=='+'||valor.charAt(a)=='-'||valor.charAt(a)=='*'||valor.charAt(a)=='/') {
												errorcin+="Intenta asignar un tipo de operando*** "+ " int "+ " ***en un dato*** "+ tipo+ " ***linea: "+ renglon+ ".\n";
												cambiarValor=false;
												break;
											}
										}
								}
								
							}
								else {
									errorcin+="Intenta asignar un tipo de dato*** "+tablaSimbolos.get(valorIdentificador.get(r)).getTipoDato()+ " ***al usar la variable*** "+ valorIdentificador.get(r)+ " ***en un dato*** "+ tipo+ " ***linea: "+ renglon+ ".\n";
									cambiarValor=false;
									break;
								}
						}
							
						
					}
						else
						{
							cambiarValor=false;
							
							errorcin+="La variable: "+valorIdentificador.get(r)+ " que intenta usar en la linea "+ renglon+ " no existe"+".\n";
							break;
						}
					valorIdentificador.clear();
				}
					
				
			}
				//Si no son identificadores y son solo numeros
				//Verifico que haya un numeros o signos
				char numeros []= {'0','1','2','3','4','5','6','7','8','9','&','<','>','!'};
				for (int a=0; a<valor.length();a++) {
					for (int b=0; b<numeros.length;b++) {
						if (valor.charAt(a)==numeros[b]) {
							numerito=300;
							break;
						}
						if (b==numeros.length-1&&numerito!=300&&identificador==0) {
							cambiarValor=false;
						}
						if(valor.charAt(a)=='+'||valor.charAt(a)=='-'||valor.charAt(a)=='*'||valor.charAt(a)=='/') {
							errorcin+="Intenta asignar un tipo de operando*** "+ " int "+ " ***en un dato*** "+ tipo+ " ***linea: "+ renglon+ ".\n";
							cambiarValor=false;
							
						}
					}
			}
			
		}
			
			return cambiarValor;
		}
		
	
}


