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
		columnas = new String[5];
		filas = new String[tokens.size()][5];
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
	int esperado = 0;
	int noLlave = Integer.MAX_VALUE;
	
	// Generar la tabla de simbolos
	public String generarTablaSimbolos() {
		
		String columnas[] = {"Nombre","Tipo de Dato","Posicion","Valor","Alcance"};
		this.columnas = columnas;
		
		for(int i = 0;i<tokens.size();i++) {
			switch(tokens.get(i).get(0).getTipo()) {
				case PUBLIC:
					alcance.add(i); // por si hay muchos metodos xd
					declaracionMetodo(tokens.get(i));
				break;
				case WHILE: // También pueden existir muchas sentencias while, y por eso su valor
					alcance.add(i);
					ifWhileVar(tokens.get(i),i);
				break;
				case IF:
					alcance.add(i);
					ifWhileVar(tokens.get(i),i);
					break;
				case CLASS: // Dioquis pero para decir que se asigna
					alcance.add(CLASS);
				break; 
				// NO ES QUE VAYAN A TENER LA MISMA FUNCIONALIDAD, AUN NO LES ASIGNO UNA
				case RETURN:// solo para validar que lo que se manda existe...
					break;
				case LLAVE_A:
					++esperado;
					if(i==(noLlave+1))
						noLlave = Integer.MAX_VALUE;
					break;
				case LLAVE_C:
					if(esperado>0)
						sacarAlcance();
					break;
				// Falta uno por si es LLAVE_A
				default: // declaracion o uso de variable	
					declaracionVariable(tokens.get(i));
					if(i==(noLlave+1) ) {
						sacarAlcance();
						noLlave = Integer.MAX_VALUE;

					}
				break;
			}

		}
		if(!errorcin.isEmpty()) errorL+="ERRORES SEMANTICOS ENCONTRADOS: \n"+ errorcin;
		return errorL;
	}

	private void ifWhileVar(ArrayList<Token> iwexpresion,int x) {
		boolean siLlave = false;
		String expresion = "";
		for(int i = 1;i<iwexpresion.size();i++) {
			if(iwexpresion.get(i).getTipo() == LLAVE_A) { ++esperado; siLlave = true;}
			if(iwexpresion.get(i).getTipo()!=PARENTESIS_A &&iwexpresion.get(i).getTipo()!=PARENTESIS_C && iwexpresion.get(i).getTipo()!=LLAVE_A )
				expresion+= iwexpresion.get(i).getValor()+" ";
		}
		
		if(!expresion.isEmpty())
			evaluarExpresiones(expresion,iwexpresion.get(0).getRenglon());
		if(!siLlave) noLlave = x;
	}
	private void evaluarExpresiones(String expresion,int renglon) { // ando dormida xDD entre los dos nos completamos
		StringTokenizer tok = new StringTokenizer(expresion," ");
		String tipo = "";
		int i = 0,orden = 0;
		while(tok.hasMoreTokens()) {
		 // b<3 && z
			System.out.println(i);
			String valor = tok.nextToken();
			
			if(valor.equals("&&") || valor.equals("!")) { tipo = ""; orden=0;} // si llegamos al operador and posiblemente cambie de tipo..
			//System.out.println(tipo + " " + valor);
			if(!(valor.equals("&&") || valor.equals("!"))&& (tipo.isEmpty() || i == 0)) { // Para ver de que tipo es la expresion
				if(valor.matches("[a-zA-Z]+([a-zA-Z]*)") && !valor.matches("[\\true\\false]")) {
					if(tablaSimbolos.containsKey(valor)) {
						tipo = tablaSimbolos.get(valor).getTipoDato();
						System.out.println(tipo);
					}else {
						errorcin+= "ERROR SEMANTICO, LA VARIABLE: "+ valor+ " :c QUE SE INTENTA USAR EN LA POSICION: "+renglon+" NO SE ENCUENTRA DECLARADA"+".\n";
					}
				}else {
					if(valor.equals("true")||valor.equals("false")) {
						tipo = "boolean";
					}else {
						if(valor.matches("[0-9]+([0-9])*") || valor.equals("-")) {
							tipo = "int";
						}
					}
				}
			}
			if(!tipo.isEmpty()) {
				// Tipo int
				if(tipo.equals("int")) {
					if(valor.equals("<")) {
						++orden;
					System.out.println("<:"+orden);
					}
					if(valor.equals("!"))
							errorcin+="ERROR SEMANTICO: INCOMPATIBILIDAD DE OPERANDOS *** " + valor + " *** NO SE PUEDEN USAR ESTOS OPERADORES LOGICOS PARA LA COMPARACION DEL TIPO INT EN LA LINEA "+renglon+".\n"; 
					if(valor.matches("[0-9]+([0-9])*")) {++orden;
					System.out.println("num:"+orden);
					}
					if(valor.matches("[a-zA-Z]+([a-zA-Z]*)")) {
						if(tablaSimbolos.containsKey(valor)) { // QUE EL IDENTIFICADOR SE ENCUENTRE PREVIAMENTE DECLARADO
			    			if(!tablaSimbolos.get(valor).getTipoDato().equals("int")) { // EL IDENTIFICADOR COINCIDE CON EL TIPO
			    				errorcin+="SE INTENTO USAR LA VARIABLE: ***"+ valor + "*** QUE ES DE TIPO: *** " +tablaSimbolos.get(valor).getTipoDato()+" *** EN UNA COMPARACION DE INT EN LA LINEA "+ renglon + ".\n"; 	
			    			}else {
			    				++orden;
			    			}
			    		}else {
			    			if(valor.equals("true")||valor.equals("false")) 
			    				errorcin+="INTENTA COMPARAR UN VALOR ***boolean*** EN UNA EXPRESION DEL TIPO  ***int***"+ " EN LA LINEA: "+renglon+ ".\n";
			    			else
			    				errorcin+= "ERROR SEMÁNTICO, LA VARIABLE: "+ valor+ " QUE SE INTENTA USAR EN LA POSICIÓN: "+renglon+" NO SE ENCUENTRA DECLARADA"+".\n";
			    		}
					}
				}else{
					// tipo boolean
					if(tipo.equals("boolean")) {
						if(valor.matches("[\\+\\-\\*\\<]"))
							errorcin+="ERROR SEMANTICO: INCOMPATIBILIDAD DE OPERANDOS *** " + valor + " *** NO SE PUEDEN USAR OPERADORES ARITMETICOS PARA LA COMPARACION DEL TIPO BOOLEAN EN LA LINEA "+ renglon+ ". \n"; 
						if(valor.matches("[0-9]+([0-9])*"))
							errorcin+="ERROR SEMANTICO: INCOMPATIBILIDAD DE TIPOS, NO SE PUEDE COMPARAR UN TIPO NUMERICO CON UN BOOLEAN, EN LA LINEA "+ renglon + ". \n" ; 
						if(valor.matches("[a-zA-Z]+([a-zA-Z]*)")) {
							if(tablaSimbolos.containsKey(valor)) { // QUE EL IDENTIFICADOR SE ENCUENTRE PREVIAMENTE DECLARADO
				    			if(!tablaSimbolos.get(valor).getTipoDato().equals("boolean")) { // EL IDENTIFICADOR COINCIDE CON EL TIPO
				    				errorcin+="SE INTENTO USAR LA VARIABLE: ***"+ valor + "*** QUE ES DE TIPO: *** " +tablaSimbolos.get(valor).getTipoDato()+" *** EN UNA OPERACION DE BOOLEAN EN LA LINEA "+ renglon + ".\n"; 	
				    			}
				    		}
						}
					}
				}
				
			}
			System.out.println(orden + " " + valor);
			i++;
		}
		System.out.println("orden: " + orden);
			if(tipo.equals("int")&&!(orden==3)) errorcin+= "SE INTENTO ASIGNAR UN VALOR INT A LA EXPRESION ***" + expresion + "*** EN LA LINEA " + renglon + ".\n";
		
	}
	private void declaracionMetodo(ArrayList<Token> meDeclar) {
		ArrayList<String> varMetodos = new ArrayList<String>();
		varMetodos.add(" ");
		
		for(int i = 4;i<meDeclar.size();i++) {
			if(meDeclar.get(i).getTipo() == LLAVE_A)++esperado;
			
			if(meDeclar.get(i).getTipo() == COMA) {
				varMetodos.add(" ");
			}
			if(meDeclar.get(i).getTipo() != COMA && meDeclar.get(i).getTipo() != LLAVE_A && meDeclar.get(i).getTipo() != PARENTESIS_C)
			varMetodos.set(varMetodos.size()-1, varMetodos.get(varMetodos.size()-1) + " " + meDeclar.get(i).getValor());
			
		}
		int x = 0;
		while(varMetodos.size()>x) {
			StringTokenizer tok = new StringTokenizer(varMetodos.get(x)," ");
			while(tok.hasMoreElements()) {
				String tipo = tok.nextToken();
				String variable = tok.nextToken();
				// se agregan las variables a la tabla de simbolos
				agregarValor(variable,tipo,meDeclar.get(0).getRenglon(),"");
			}
			x++;
		}
		
	}
	private int obtenerAlcance() {
		return alcance.get(alcance.size()-1);
	}
	private void sacarAlcance() {
		esperado--;
		if(alcance.size()!=0)
			alcance.remove(alcance.size()-1);
	}
	private void agregarValor(String variable, String tipo, int renglon, String valor) {
		filas[fila][0] = variable;
		filas[fila][1] = tipo;
		filas[fila][2] = renglon +"";
		filas[fila][3] = valor;
		filas[fila][4] = obtenerAlcance()+"";
		
		tablaSimbolos.put(variable, new TablaSimbolos(variable, tipo, renglon, valor,fila, obtenerAlcance()));
		fila++;
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
					agregarValor(variable,tipo,renglon,valor);
				}
			}
		}else { // USO DE UNA VARIABLE
			if(tablaSimbolos.containsKey(variable)) {
				if(tablaSimbolos.get(variable).getAlcance() == obtenerAlcance()) {
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
					errorcin+= "ERROR SEMANTICO, LA VARIABLE: "+ variable+ " QUE SE INTENTA USAR EN LA POSICION: "+renglon+" SE ENCUENTRA FUERA DEL ALCANCE.\n";
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
		    			if(tok.equals("true")||tok.equals("false")) 
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


