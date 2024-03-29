/*PROYECTO ANALIZADOR LEXICO Y SINTACTICO
 INTEGRANTES:
 - Garcia Aispuro Alan Gerardo.
 - Meza Leon Oscar Oswaldo.
 - Osuna Lizarraga Rubi Guadalupe.
 - Rodelo Cardenas Graciela.
*/

package clasesBase;

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
	private boolean expresion = false;
	private String expresiones;
	private CodigoIntermedio codigoInter;
	public Semantico(ArrayList<ArrayList<Token>> tokens) {
		this.tokens = tokens;
		columnas = new String[5];
		filas = new String[tokens.size()][5];
		tablaSimbolos = new HashMap<String, TablaSimbolos>();
		alcance = new ArrayList<Integer>();
		expresiones =  "";
		codigoInter = new CodigoIntermedio(this);
	}
	public String getExpresion(String varible) {
		return tablaSimbolos.get(varible).getValor();
	}
	public String getPosicion(String variable) {
		return tablaSimbolos.get(variable).getPosicion()+"";
	}
	public String getExpresiones() {
		return expresiones;
	}
	public String[] getColumnas() {
		return columnas;
	}
	public String[][] getFilas(){
		return filas;
	}
	public HashMap<String, TablaSimbolos> getTabla()
	{
		return tablaSimbolos;
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
			try {
				switch(tokens.get(i).get(0).getTipo()) {
					case PUBLIC:
						alcance.add(i); // por si hay muchos metodos xd
						declaracionMetodo(tokens.get(i));
					break;
					case WHILE: // TambiÃ©n pueden existir muchas sentencias while, y por eso su valor
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
			}catch(IndexOutOfBoundsException e) {
				// Esto se ignora 
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
			String valor = tok.nextToken();
			
			if(valor.equals("&&") || valor.equals("!")) { tipo = ""; orden=0;} // si llegamos al operador and posiblemente cambie de tipo..
			
			if(!(valor.equals("&&") || valor.equals("!"))&& (tipo.isEmpty() || i == 0)) { // Para ver de que tipo es la expresion
				if(valor.matches("[a-zA-Z]+([a-zA-Z]*)") && !valor.matches("[\\true\\false]")) {
					if(tablaSimbolos.containsKey(valor)) {
						tipo = tablaSimbolos.get(valor).getTipoDato();
						
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
					}
					if(valor.equals("!"))
							errorcin+="ERROR SEMANTICO: INCOMPATIBILIDAD DE OPERANDOS *** " + valor + " *** NO SE PUEDEN USAR ESTOS OPERADORES LOGICOS PARA LA COMPARACION DEL TIPO INT EN LA LINEA "+renglon+".\n"; 
					if(valor.matches("[0-9]+([0-9])*")) {++orden;
					}
					if(valor.matches("[a-zA-Z]+([a-zA-Z]*)")) {
						if(tablaSimbolos.containsKey(valor)) { // QUE EL IDENTIFICADOR SE ENCUENTRE PREVIAMENTE DECLARADO
			    			if(!comprobarAlcance(obtenerAlcance(),valor))
			    				errorcin+="SE INTENTO USAR LA VARIABLE: ***"+ valor + "*** QUE ES DE TIPO: *** " +tablaSimbolos.get(valor).getTipoDato()+" *** EN UNA COMPARACION DE INT EN LA LINEA "+ renglon + " Y SE ENCUENTRA FUERA DEL ALCANCE.\n"; 	
							if(!tablaSimbolos.get(valor).getTipoDato().equals("int")) { // EL IDENTIFICADOR COINCIDE CON EL TIPO
			    				errorcin+="SE INTENTO USAR LA VARIABLE: ***"+ valor + "*** QUE ES DE TIPO: *** " +tablaSimbolos.get(valor).getTipoDato()+" *** EN UNA COMPARACION DE INT EN LA LINEA "+ renglon + ".\n"; 	
			    			}else {
			    				++orden;
			    			}
			    		}else {
			    			if(valor.equals("true")||valor.equals("false")) 
			    				errorcin+="INTENTA COMPARAR UN VALOR ***boolean*** EN UNA EXPRESION DEL TIPO  ***int***"+ " EN LA LINEA: "+renglon+ ".\n";
			    			else
			    				errorcin+= "ERROR SEMÃ�NTICO, LA VARIABLE: "+ valor+ " QUE SE INTENTA USAR EN LA POSICIÃ“N: "+renglon+" NO SE ENCUENTRA DECLARADA"+".\n";
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
								if(comprobarAlcance(obtenerAlcance(),valor)) {
									if(!tablaSimbolos.get(valor).getTipoDato().equals("boolean")) { // EL IDENTIFICADOR COINCIDE CON EL TIPO
					    				errorcin+="SE INTENTO USAR LA VARIABLE: ***"+ valor + "*** QUE ES DE TIPO: *** " +tablaSimbolos.get(valor).getTipoDato()+" *** EN UNA OPERACION DE BOOLEAN EN LA LINEA "+ renglon + ".\n"; 	
					    			}
								}else {
									errorcin+="SE INTENTO USAR LA VARIABLE: ***"+ valor + "*** QUE ES DE TIPO: *** " +tablaSimbolos.get(valor).getTipoDato()+" *** EN UNA OPERACION DE BOOLEAN EN LA LINEA "+ renglon + " Y SE ENCUENTRA FUERA DEL ALCANCE.\n"; 	
								}
				    		}
						}
					}
				}
				
			}
			i++;
		}
			if(tipo.equals("int")&&!(orden==3)) errorcin+= "SE INTENTO COMPARAR UN VALOR INT A LA EXPRESION ***" + expresion + "*** EN LA LINEA " + renglon + " QUE ES DEL TIPO BOOLEAN.\n";
		
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
				if(!tablaSimbolos.containsKey(variable))
					agregarValor(variable,tipo,meDeclar.get(0).getRenglon(),"");
				else
					errorcin+= "LA VARIABLE: ***"+ variable+ "*** QUE SE INTENTA DECLARAR EN LA POSICION: ***"+meDeclar.get(0).getRenglon()+"*** YA EXISTE EN LA POSICION: ***" + tablaSimbolos.get(variable).getPosicion() +"***.\n"; 
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
	private boolean comprobarAlcance(int alc,String variableExistente) {
		boolean siAlcanza = alc==tablaSimbolos.get(variableExistente).getAlcance() || tablaSimbolos.get(variableExistente).getAlcance() == CLASS;
		
		ArrayList<Integer> alcances = new ArrayList<Integer>();
		if(alcance.size()>1 && !siAlcanza) {
			for(int i = alcance.size()-1;i>=0;i--) {
				alcances.add(alcance.get(i));
				if(alcance.get(i).equals("CLASS")) {
					break;
				}
			}
			for(int i = 0;i<alcances.size();i++) {
				if(tablaSimbolos.get(variableExistente).getAlcance() == alcances.get(i)) {
					siAlcanza = true;
					break;
				}
			}
		}
		

		return siAlcanza;
	}
	public HashMap<Integer,ArrayList<Vector>> tablaCuadruplos() {
		return codigoInter.tablaCuadruplos();
	}
	public Vector<String> columnName(){
		return codigoInter.columnName();
	}
	private void agregarValor(String variable, String tipo, int renglon, String valor) {
		filas[fila][0] = variable;
		filas[fila][1] = tipo;
		filas[fila][2] = renglon +"";
		filas[fila][3] = valor;
		filas[fila][4] = obtenerAlcance()+"";
		
		
		tablaSimbolos.put(variable, new TablaSimbolos(variable, tipo, renglon, valor,fila, obtenerAlcance()));
		if(expresion) {
			codigoInter.addExpresion(variable);
			codigoInter.addExpresiones_var(valor);
			codigoInter.addRenglon(renglon+"");
			expresion = false;
			codigoInter.evaluarExpresion(variable,valor);
			codigoInter.removeExpresion(variable);
			
		}
		fila++;
	}
	// Si es declaraciÃ³n de variable
	private void declaracionVariable(ArrayList<Token> varDeclar) {
		String valor = "",variable = "";
		int i = 0, renglon = 0, igual = 0;
		boolean datoCorrecto = false;
		//boolean isObject = false;
		String tipo = "";
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
		 
		
		// OBTENER EL VALOR DE LA VARIABLE
		while(i<varDeclar.size()) {
			if(varDeclar.get(i).getTipo() != PUNTO_COMA) {
				valor += varDeclar.get(i).getValor() + " ";
				if (varDeclar.get(i).getTipo()==IDENT)
					valorIdentificador.add(varDeclar.get(i).getValor());
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
			if(existe)  errorcin+= "LA VARIABLE: ***"+ variable+ "*** QUE SE INTENTA DECLARAR EN LA POSICION: ***"+renglon+"*** YA EXISTE EN LA POSICION: ***" + tablaSimbolos.get(variable).getPosicion() +"***.\n"; 
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
				
					if(datoCorrecto) { // ModificaciÃƒÂ³n en la tabla de simbolos :D
						
						/**/
						if(expresion) {
							codigoInter.addExpresion(variable);
							codigoInter.addExpresiones_var(valor);
							codigoInter.addRenglon(renglon+"");
							expresion = false;
							codigoInter.evaluarExpresion(variable,valor);
							codigoInter.removeExpresion(variable);
						}else {
							filas[tablaSimbolos.get(variable).getFila()][3] = valor;
							tablaSimbolos.get(variable).setValor(valor);
						}
					}
				}else {
					errorcin+= "ERROR SEMANTICO, LA VARIABLE: "+ variable+ " QUE SE INTENTA USAR EN LA POSICION: "+renglon+" SE ENCUENTRA FUERA DEL ALCANCE.\n";
				}
			}else {
				errorcin+= "ERROR SEMANTICO, LA VARIABLE: "+ variable+ " QUE SE INTENTA USAR EN LA POSICION: "+renglon+" NO SE ENCUENTRA DECLARADA"+".\n";
			}
		}
		/*if(expresion) { // La variable se agrega a una cadena, que contiene las variables uwu
			expresiones = expresiones.replace(variable, "");
			expresiones+= " " + variable;
			expresion = false;
		}else { // Se elimina de la cadena si llega a perder el hecho de que es.. expresión, shale :c
			expresiones = expresiones.replace(variable, "");
		}*/
	}
	
	
	public boolean usoBoolean (String variable, String valor,int renglon,String tipo) {
		boolean cambiarValor=true;
		//Ver si estÃƒÆ’Ã‚Â¡ declarada
		if(!tablaSimbolos.containsKey(variable))
			errorcin+= "ERROR SEMANTICO, LA VARIABLE: "+ variable+ " QUE SE INTENTA USAR EN LA POSICION: "+renglon+" NO SE ENCUENTRA DECLARADA"+".\n";
		else {
			//Darle un valor nuevo al que tenÃƒÆ’Ã‚Â­a
			TablaSimbolos verificar= tablaSimbolos.get(variable);
			
			if(!verificar.getValor().equals(valor)) {
					cambiarValor=verificarBoolean(variable,  valor, renglon, tipo);
			}
		}
		if(cambiarValor)
			tablaSimbolos.get(variable).setValor(valor);
		return cambiarValor;
	}
	
	//VERIFICACIÃ“N DE LAS DECLARACIONES DE VARIABLES
		 //Tipo INT
		private boolean isEntero(String variable, String valor,int renglon) {
			String exp = "";
			boolean valido=true;
			StringTokenizer token = new StringTokenizer(valor, " ");
			int countTokens = token.countTokens();
		    while(token.hasMoreTokens()) {
		    	valorIdentificador.clear();
		    	String tok = token.nextToken(" ");
		    	// SI SE TRATA DE UN IDENTIFICADOR
		    	if(tok.matches("[a-zA-Z]+([a-zA-Z0-9])*")) {
		    		if(tablaSimbolos.containsKey(tok)) { // QUE EL IDENTIFICADOR SE ENCUENTRE PREVIAMENTE DECLARADO
		    			if(!comprobarAlcance(obtenerAlcance(),tok)) {
		    				errorcin+="SE INTENTO USAR LA VARIABLE: ***"+ tok + "*** QUE ES DE TIPO: *** " +tablaSimbolos.get(tok).getTipoDato()+" *** EN UNA OPERACION DE INT EN LA LINEA "+ renglon + " Y SE ENCUENTRA FUERA DEL ALCANCE.\n"; 	
		    				valido = false;
		    			}
		    				
		    			if(!tablaSimbolos.get(tok).getTipoDato().equals("int")) { // EL IDENTIFICADOR COINCIDE CON EL TIPO
		    				errorcin+="SE INTENTO USAR LA VARIABLE: ***"+ tok + "*** QUE ES DE TIPO: *** " +tablaSimbolos.get(tok).getTipoDato()+" *** EN UNA OPERACION DE INT EN LA LINEA "+ renglon + ".\n"; 	
		    				valido = false;
		    			}
		    		}else {
		    			if(tok.equals("true")||tok.equals("false")) 
		    				errorcin+="INTENTA ASIGNAR UN VALOR ***boolean*** EN LA VARIABLE *** "+variable+"*** QUE ES ***int***"+ " EN LA LINEA: "+renglon+ ".\n";
		    			else
		    				errorcin+= "ERROR SEMÃ�NTICO, LA VARIABLE: "+ tok+ " QUE SE INTENTA USAR EN LA POSICION: "+renglon+" NO SE ENCUENTRA DECLARADA"+".\n";
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
		    			if(!tok.matches("[\\+\\-\\*\\/]")) {  
		    				if(!tok.matches("[\\(\\)]")) {
		    					errorcin+="EL VALOR DE LA VARIABLE: ***"+ variable+ "*** DEBE SER DE TIPO INT Y LE INTENTA COLOCAR EL VALOR: *** " + valor+" ***.\n"; 
		    					valido = false;
		    					expresion = false;
		    				}else {
		    					expresion = true;
		    				}
		    			}else {
		    				expresion = true;
		    			}
		    		
		    			
		    	}
		    	
		    }
		    
		    if(!valido) expresion = valido;
		    else expresion = !valor.isEmpty();
		    
 			return valido;
		}

		private boolean verificarBoolean (String variable, String valor,int renglon,String tipo) {
			int unNumero=0;
			boolean cambiarValor=true;
			int opInc=0;
			int numerito=0; int identificador=0;
			int opLogica=0;
			
				//hayNumeros=true;
			if ((!(valor.equals("true "))) && (!(valor.equals("false ")))) {
				//Verificar que sea un identificador
				if(!valorIdentificador.isEmpty()) {
					for(int r=0; r<valorIdentificador.size();r++) {
						//Verificar que exista
						if(tablaSimbolos.containsKey(valorIdentificador.get(r))) {
							identificador++;
							if(!comprobarAlcance(obtenerAlcance(),valorIdentificador.get(r))) {
								errorcin+="SE INTENTO USAR LA VARIABLE: ***"+ valorIdentificador.get(r) + "*** QUE ES DE TIPO: *** " +tablaSimbolos.get(valorIdentificador.get(r)).getTipoDato()+" *** EN UNA OPERACION DE BOOLEAN EN LA LINEA "+ renglon + " Y SE ENCUENTRA FUERA DEL ALCANCE.\n"; 	
								cambiarValor = false;
							}
							//Si existe verificamos el tipo de dato
							if(!tablaSimbolos.get(valorIdentificador.get(r)).getTipoDato().equals(tipo)){
								//Verificar si se trata de int
								if(tablaSimbolos.get(valorIdentificador.get(r)).getTipoDato().equals("int")) {
									identificador++;
									
									//Verifico que haya un numeros o signos
									char numeros []= {'0','1','2','3','4','5','6','7','8','9','&','<','>','!','(',')'};
									for (int a=0; a<valor.length();a++) {
										if(valor.charAt(a)=='<'||valor.charAt(a)=='!'||valor.charAt(a)=='&')
											opLogica++;
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
												
												if(opLogica==0) {
													opInc++;
													cambiarValor=false;
													break;
												}
												else if(expresion(valor)!=0)
													errorcin+="Hay un error en una expresión **int** que se usa en asignación ***boolean** renglón: "+renglon+ ".\n";
												
											}
										}
									}
									if (valorIdentificador.size()==1&&numerito!=300) {
										errorcin+="Intenta asignar un tipo de dato*** "+ " int "+ " ***en un dato*** "+ tipo+ " ***linea: "+ renglon+ ".\n";
										cambiarValor=false;
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
					
				}
					valorIdentificador.clear();
				
			}
				//Si no son identificadores y son solo numeros
				//Verifico que haya un numeros o signos
				char numeros []= {'0','1','2','3','4','5','6','7','8','9','&','<','>','!','(',')'};
				for (int a=0; a<valor.length();a++) {
					if(valor.charAt(a)=='<'||valor.charAt(a)=='!'||valor.charAt(a)=='&')
						opLogica++;
					for (int b=0; b<numeros.length;b++) {
						if (valor.charAt(a)==numeros[b]) {
							numerito=300;
							unNumero++;
							break;
						}
						if (b==numeros.length-1&&numerito!=300&&identificador==0) {
							cambiarValor=false;
							break;
						}
						if(valor.charAt(a)=='+'||valor.charAt(a)=='-'||valor.charAt(a)=='*'||valor.charAt(a)=='/') {
							if(opLogica==0) {
								opInc++;
								cambiarValor=false;
								break;
							}
							else if(expresion(valor)!=0)
								errorcin+="Hay un error en una expresión **int** que se usa en asignación ***boolean** renglón: "+renglon+ ".\n";
							
						}
					}
			}
			
		}
			if(numerito==300&&unNumero==1) {
				errorcin+="Intenta asignar un dato el dato: *** "+ valor+ " ***que es *** int "+ " ***en un dato*** "+ tipo+ " ***linea: "+ renglon+ ".\n";
				cambiarValor=false;
			}
				
			if(opInc!=0&&opLogica==0)
			errorcin+="Intenta asignar un tipo de operando*** "+ " int "+ " ***en un dato*** "+ tipo+ " ***linea: "+ renglon+ ".\n";
			return cambiarValor;
		}
		private int expresion (String valor) {
			int expresion=0;
			//Verifico que se trate de una operacion matematica
			ArrayList <String>operandos=new ArrayList<String>();
			String operador="";
			int letra=0;
			valor.replace(" ","");
			while(valor.length()-1>=letra) {
				if(valor.charAt(letra)!='('&&valor.charAt(letra)!=')'&&valor.charAt(letra)!='+'&&valor.charAt(letra)!='-'&&valor.charAt(letra)!='*'&&valor.charAt(letra)!='<'&&valor.charAt(letra)!=' ')
				operador+=valor.charAt(letra);
				if(valor.charAt(letra)=='+'||valor.charAt(letra)=='-'||valor.charAt(letra)=='*'||valor.charAt(letra)!='<') {
					operandos.add(operador);
					operador="";
				}
				letra++;
			}
			
			//Cualquier problema con los operandos
			for (int h=0; h<operandos.size();h++) {
				if(!(operandos.get(h).matches("([0-9])*"))&&!(operandos.get(h).matches("[a-zA-Z]+([a-zA-Z0-9])*")))
					expresion++;
			}
			operandos.clear();
			return expresion;
		}
	
}