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
	public String generarTablaSimbolos() {
		TablaSimbolos simboloAtributos;
		errorL = "";
		errorL+= "ERRORES SEMANTICOS ENCONTRADOS: \n";
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
					errorL+= "LA VARIABLE: ***"+ variable+ "*** QUE SE INTENTA DECLARAR EN LA POSICIÓN: ***"+renglon+"*** YA EXISTE EN LA POSICIÓN: ***" + tablaSimbolos.get(variable).getPosicion() +"***.\n"; 
					existe=true;
				}
				//Verificar el tipo de dato
				if (tipo.equals("int")) {
					datoCorrecto = verificarInt(variable,valor,renglon,tipo);
					
				}
				if (tipo.equals("boolean")) {
					datoCorrecto = verificarBoolean(variable,valor,renglon,tipo);
					
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
					errorL+= "ERROR SEMANTICO, LA VARIABLE: "+ variable+ " QUE SE INTENTA USAR EN LA POSICIÃ“N: "+renglon+" NO SE ENCUENTRA DECLARADA"+".\n";
				else {
					if(tablaSimbolos.get(variable).getTipoDato() == "int") {
						datoCorrecto=usoEntero(variable,valor,renglon,tipo);
					}
					if(tablaSimbolos.get(variable).getTipoDato() == "boolean") {
						datoCorrecto=usoBoolean(variable,valor,renglon,tipo);
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
	
	//Verificación cuando las variables son usadas
	//Tipo Int
	private boolean usoEntero(String variable, String valor,int renglon, String tipo) {
		boolean modificar=true;
		//Ver si está declarada
		if(!tablaSimbolos.containsKey(variable))
			errorL+= "ERROR SEMÁNTICO, LA VARIABLE: "+ variable+ " QUE SE INTENTA USAR EN LA POSICIÓN: "+renglon+" NO SE ENCUENTRA DECLARADA"+".\n";
		else {
			//Darle un valor nuevo al que tenía
			TablaSimbolos verificar= tablaSimbolos.get(variable);
			if(!verificar.getValor().equals(valor)) {
				modificar=verificarInt(variable,valor,renglon,tipo);
			}
		
		}

		if(modificar) {
			tablaSimbolos.get(variable).setValor(valor);
			return true;
		}
		else
			return false;
	}
	
	//TipoBoolean
	public boolean usoBoolean (String variable, String valor,int renglon,String tipo) {
		boolean cambiarValor=true;
		//Ver si está declarada
		if(!tablaSimbolos.containsKey(variable))
			errorL+= "ERROR SEMÁNTICO, LA VARIABLE: "+ variable+ " QUE SE INTENTA USAR EN LA POSICIÓN: "+renglon+" NO SE ENCUENTRA DECLARADA"+".\n";
		else {
			//Darle un valor nuevo al que tenía
			TablaSimbolos verificar= tablaSimbolos.get(variable);
			
			if(!verificar.getValor().equals(valor)) {
						cambiarValor=verificarBoolean(variable,  valor, renglon, tipo);
			}
		}
		if(cambiarValor)
			tablaSimbolos.get(variable).setValor(valor);
		return cambiarValor;
	}
	
	//VERIFICACION DE VARIABLES
	//Verificación de los Int
	private boolean verificarInt (String variable, String valor,int renglon,String tipo) {
		int cambiar=0, identCorrec=0;
			//VERIFICACION DE OPERADORES LÓGICOS
				for (int a=0; a<valor.length();a++) {
					if(valor.charAt(a)=='&' && valor.charAt(a+1)=='&') {
						a++;
						errorL+="Intenta usar el operador lógico: && para asignar valor a la variable*** "+variable+"*** de tipo ***Int*** en el renglon "+renglon+".\n";
						cambiar++;
					}
					else if(valor.charAt(a)=='!'||valor.charAt(a)=='<'||valor.charAt(a)=='>') {
						errorL+="Intenta usar el operador lógico: "+valor.charAt(a)+ " para asignar valor a la variable*** "+variable+"*** de tipo ***Int***en el renglon: "+renglon+".\n";
						cambiar++;
					}
				}
				
				//Ver que el valor que le pongo no es boleano
					if(valor.equals("true ")||valor.equals("false ")) {
						errorL+="Intenta asignar un valor ***boolean*** en la variable *** "+variable+"*** que es ***int***"+ " en la línea: "+renglon+ ".\n";
						cambiar++;
						
					}
					//Buscar que los identificadores usados estén correctos
					else {
						if(!valorIdentificador.isEmpty()) {
							for(int r=0; r<valorIdentificador.size();r++) {
								//Verificar que exista
								if(tablaSimbolos.containsKey(valorIdentificador.get(r))) {
									System.out.println("entro aqui");
									System.out.println(valor);
									//Si existe verificamos el tipo de dato
									if(!tablaSimbolos.get(valorIdentificador.get(r)).getTipoDato().equals(tipo)){
									errorL+="Intenta asignar un tipo de dato*** "+tablaSimbolos.get(valorIdentificador.get(r)).getTipoDato()+ " ***al usar la variable*** "+ valorIdentificador.get(r)+ " ***en un dato*** "+ tipo + " ***línea: "+ renglon+ ".\n";
										cambiar++;
									}
									else identCorrec++;
								}
								else
								{
									cambiar++;
									errorL+="La variable: "+valorIdentificador.get(r)+ " que intenta usar en la linea "+ renglon+ " no existe"+".\n";
								}
							}
						}
						

					}
						if(cambiar==0) {
			//Verificar si hay números u operadores 
			char numeros []= {'0','1','2','3','4','5','6','7','8','9','+','-','*','/'};
			for (int a=0; a<valor.length();a++) {
				for (int b=0; b<numeros.length;b++) {
					if (valor.charAt(a)==numeros[b]) {
						cambiar=1000;
						break;
					}
				}
				if (cambiar!=1000 && identCorrec==0) {
					errorL+="EL VALOR DE LA VARIABLE: ***"+ variable+ "*** DEBE SER DE TIPO INT Y LE INTENTA COLOCAR EL VALOR: ***" + valor+"***.\n"; 	
					break;
				}
		
			}
		}
		if(cambiar==0||cambiar==1000) {
			valorIdentificador.clear();
			return true;
		}
		else {
			valorIdentificador.clear();
			return false;
		}
		
	}
	private boolean verificarBoolean (String variable, String valor,int renglon,String tipo) {
		boolean cambiarValor=true;
		int numerito=0; int identificador=0;
		if ((!(valor.equals("true "))) && (!(valor.equals("false ")))) {
			//Verificar que sea un identificador
			if(!valorIdentificador.isEmpty()) {
				for(int r=0; r<valorIdentificador.size();r++) {
					System.out.println(valorIdentificador.get(r));
					//Verificar que exista
					if(tablaSimbolos.containsKey(valorIdentificador.get(r))) {
						identificador++;
						//Si existe verificamos el tipo de dato
						if(!tablaSimbolos.get(valorIdentificador.get(r)).getTipoDato().equals(tipo)){
							//Verificar si se trata de int
							if(tablaSimbolos.get(valorIdentificador.get(r)).getTipoDato().equals("int")) {
								identificador++;
								//Verifico que haya un números o signos
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
											errorL+="Intenta asignar un tipo de operando*** "+ " int "+ " ***en un dato*** "+ tipo+ " ***línea: "+ renglon+ ".\n";
											cambiarValor=false;
											
										}
									}
							}
							
						}
							else {
								errorL+="Intenta asignar un tipo de dato*** "+tablaSimbolos.get(valorIdentificador.get(r)).getTipoDato()+ " ***al usar la variable*** "+ valorIdentificador.get(r)+ " ***en un dato*** "+ tipo+ " ***línea: "+ renglon+ ".\n";
							cambiarValor=false;
							}
					}
						
					
				}
					else
					{
						cambiarValor=false;
						
						errorL+="La variable: "+valorIdentificador.get(r)+ " que intenta usar en la linea "+ renglon+ " no existe"+".\n";
					}
				valorIdentificador.clear();
			}
				
			
		}
			//Si no son identificadores y son solo números
			//Verifico que haya un números o signos
			char numeros []= {'0','1','2','3','4','5','6','7','8','9','&','<','>','!'};
			for (int a=0; a<valor.length();a++) {
				for (int b=0; b<numeros.length;b++) {
					if (valor.charAt(a)==numeros[b]) {
						numerito=300;
						break;
					}
					if (b==numeros.length-1&&numerito!=300&&identificador==0) {
						cambiarValor=false;
						System.out.println(identificador);
					}
					if(valor.charAt(a)=='+'||valor.charAt(a)=='-'||valor.charAt(a)=='*'||valor.charAt(a)=='/') {
						errorL+="Intenta asignar un tipo de operando*** "+ " int "+ " ***en un dato*** "+ tipo+ " ***línea: "+ renglon+ ".\n";
						cambiarValor=false;
						
					}
				}
		}
		
	}
		
		return cambiarValor;
	}
	
}
