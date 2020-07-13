/*
 PROYECTO ANALIZADOR LEXICO Y SINTACTICO
 INTEGRANTES:
 - Garcia Aispuro Alan Gerardo.
 - Meza Leon Oscar Oswaldo.
 - Osuna Lizarraga Rubi Guadalupe.
 - Rodelo Cardenas Graciela.
*/
package clasesBase;

import java.util.*;
import java.util.regex.*;

import arbolSintactico.Sintactico;

public class Palabritas implements Tipo {
	private final String[] palabras = {"public", "class", "int", "boolean", 
			"if", "else", "while", "true", "false", "this", "new", "length",
			"System","out","print","return"};
	private final String[] signos = {"+","-", "*", "<", "=", /* / */ 
			"(", ")", "[", "]", "{", "}", "&&", ";", ",", ".","!"}; //OMG TENGO CONTROOOOL
	private ArrayList<ArrayList<Token>> tokens = new ArrayList<ArrayList<Token>>();
	private String codigo, token;
	private StringTokenizer tokenizador, lexico;
	private Pattern patron;
	private Pattern patron2;
	private Pattern patron3;
	private Matcher verificar, veri;
	private String columnas[];
	private String filas[][];
	private String errorL = " LECTURA DE CODIGO COMPLETADA.\n";
	int numerito;
	
	public Palabritas(String cod) {
		codigo = cod;
	}
	public void analizador() {
		tokenizador = new StringTokenizer(codigo);
		token = "";
		int rengloncito = 1;
		numerito = 1;
		patron = Pattern.compile("[//][\\sa-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+");
		patron2 = Pattern.compile("[/*][\\sa-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+");
		patron3 = Pattern.compile("[\\sa-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+[*/]");
		boolean comentarioMas = false;
		while(tokenizador.hasMoreTokens()) {
			token = tokenizador.nextToken("\n");
			if(true) {
				tokens.add(new ArrayList<Token>());
				analizadorLexico(token, rengloncito);
				
			}
			
			rengloncito++;
			
		}
		
		errorL += Sintactico.VerificadorSintactico(tokens);
		columnas = new String[4];
		filas = new String[tokens.size()][4];
		imprimirTabla();
		for(int x = tokens.size()-1;x>=0;x--)
			tokens.remove(x);
		
		System.out.print("\n\n\n");
	}
	private boolean comentarioLargo = false;
	private int comen = 0;
	public void analizadorLexico(String linea, int renglon) {
		
		if(linea.length() >= 2)
			if(linea.charAt(0) == '/' && linea.charAt(1)=='*') {
				comentarioLargo = true;
				return;
			}
		linea = espacios(linea);
		lexico = new StringTokenizer(linea);
		//patron = Pattern.compile("[a-zA-Z]+([a-zA-Z])*");
		int posicion = -1;
		int columna = 1;
		int tipin = ERROR;
		String tipo = "";
		String bigTipo = "";
		String nombre = "";
		String valor = "";
		
		while(lexico.hasMoreTokens()) {
			String palabra = lexico.nextToken();
			patron = Pattern.compile("[a-zA-Z]+([a-zA-Z0-9])*");
			verificar = patron.matcher(palabra);
			if(!comentarioLargo)
				if(palabra.charAt(0) == '/' && palabra.charAt(1) == '/') {
					return;
				}
			if(palabra.charAt(0) == '*') {
				++comen;
			}
			if(palabra.charAt(0) == '/' && comen > 0) {
				comen = 0;
				comentarioLargo = false;
				break;
			}
			
			if(comentarioLargo) { 
				continue;
			
			}
				if(verificar.matches()) {
					for(int i=0; i<palabras.length; i++) {
						if(palabras[i].contentEquals(palabra)) {
							tipin = (i+1)*-1;
							valor = palabras[i];
							if(palabra.equals(palabras[0])) {
								bigTipo = "Modifi.";
								tipo = palabras[i];	
								posicion = i;
								break;
							}else {
								if(i!=0 && palabra.equals(palabras[i])) {
									tipo = "Palabra reservada";
									nombre = palabras[i];
									posicion = i;
									break; 
								}
							}
						
						}
					}
					if(posicion == -1){ nombre = palabra; tipin = IDENT; valor = palabra;}
				}else {
					patron = Pattern.compile("[0-9]+([0-9])*");
					verificar = patron.matcher(palabra);
					if(verificar.matches()) {// = ; + { } public{ ( ) " " D: // /**/
	 					tipo = "Numero";
						valor = palabra;
						tipin = NUM;
					}else {
						tipo = palabra;
						valor = tipo;
						switch(palabra) {
						case "+":
							tipin = SUMA;
							break;
						case "-":
							tipin = RESTA;
							break;
						case "*":
							tipin = POR;
							break;
						case "<":
							tipin = MENOR;
							break;
						case "=":
							tipin = IGUAL;
							break;
						case "(":
							tipin = PARENTESIS_A;
							break;
						case ")":
							tipin = PARENTESIS_C;
							break;
						case "[":
							tipin = CORCHETE_A;
							break;
						case "]":
							tipin = CORCHETE_C;
							break;
						case "{":
							tipin = LLAVE_A;
							break;
						case "}":
							tipin = LLAVE_C;
							break;
						case "&&":
							tipin = AND;
							break;
						case ";":
							tipin = PUNTO_COMA;
							break;
						case ",":
							tipin = COMA;
							break;
						case ".":
							tipin = PUNTO;
							break;
						case "!":
							tipin = NEGACION;
							break;
						default:
							tipo = "ERROR";
								errorL += " ERROR LEXICO EN LA LINEA "+renglon+" Y EN LA COLUMNA "+columna+". ENTRADA INVALIDA: "+palabra+".\n";
							valor = tipo;
							break;
						}
					}
				}
				tokens.get(tokens.size()-1).add((new Token(renglon, columna, valor, tipin)));
				bigTipo = "";
				nombre = "";
				tipo = "";
				valor = "";
				posicion = -1;
				columna++;
			
			}
		
	}
	private void imprimirTabla() {
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
						variable = tokens.get(i).get(a).getValor();
						renglon = tokens.get(i).get(a).getRenglon();
						tipo = tokens.get(i).get(0).getValor();
						if(!tipo.equals("class") &&  !tipo.equals("public")&&!tipo.equals(variable))
							isDeclaracion =true;
						if (tipo.equals(variable))
							isused=true;
					}	
					
					
				
			}
			
			column = 0;
			if(isDeclaracion) {
				boolean existe=false, datoCorrecto=true;
				//Buscar si ya se encuentra agregada
				for (int j=0; j<fila; j++) {
					if (filas[j][column].equals(variable)){
					errorL+= "LA VARIABLE: ***"+ variable+ "*** QUE SE INTENTA DECLARAR EN LA POSICIÓN: ***"+renglon+"*** YA EXISTE EN LA POSICIÓN: ***" + filas[j][2] +"***.\n"; 
					existe=true;
					break;
						}
					}
				//Verificar el tipo de dato
				if (tipo.equals("int")) {
					boolean valido=false;
					char numeros []= {'0','1','2','3','4','5','6','7','8','9','+','-','*','/'};
					for (int a=0; a<valor.length();a++) {
						for (int b=0; b<numeros.length;b++) {
							if (valor.charAt(a)==numeros[b]) {
								valido=true;
								break;
							}
						}
						if (!valido) {
							errorL+="EL VALOR DE LA VARIABLE: ***"+ variable+ "*** DEBE SER DE TIPO INT Y LE INTENTA COLOCAR EL VALOR: ***" + valor+"***.\n"; 	
							valido=false;
							datoCorrecto=false;
							break;
						}
					}
	
				}
				if (tipo.equals("boolean")) {
					if ((!(valor.equals("true "))) && (!(valor.equals("false ")))) {
						errorL+="EL VALOR DE LA VARIABLE: ***"+ variable+ "*** DEBE SER DE TIPO BOOLEAN Y LE INTENTA COLOCAR EL VALOR: ***" + valor+"***.\n"; 	
						datoCorrecto=false;
					}
				}
				if (!existe&&datoCorrecto) {
				filas[fila][column] = variable;
				++column;
				filas[fila][column] = tipo;
				++column;
				filas[fila][column] = renglon +"";
				++column;
				filas[fila][column] = valor;
				
				fila++;
				}
				existe=false;
				datoCorrecto=true;
			}
			if (isused) {
				//Ver si está declarada
				boolean existe=false;
				for (int j=0; j<fila; j++) {
					if (filas[j][column].equals(variable)){ 
					existe=true;
					break;
						}
					}
				if (existe==false)
					errorL+= "ERROR SEMÁNTICO, LA VARIABLE: "+ variable+ " QUE SE INTENTA USAR EN LA POSICIÓN: "+renglon+" NO SE ENCUENTRA DECLARADA"+".\n";
				existe=false;
				
			}
		}
	}
	private boolean isReservada(String tipo) {
		boolean validar = false;
		for(int i = 0;i<palabras.length;i++) {
			if(tipo.contentEquals(palabras[i])) {
				validar = true;
				break;
			}
		}
		return validar;
	}
	private String espacios(String cadena) {
		
		for(int i = 0;;i++) {
			for(int a = 0; a<signos.length;a++) {
				boolean simon = false;
				if(cadena.charAt(i) == '&') {
					if(cadena.charAt(i-1) != ' ') {
						if(cadena.charAt(i) == cadena.charAt(i+1)){
							simon = true;
						}
					}else {
						if(cadena.charAt(i) == signos[a].charAt(0) &&cadena.length()-2 >= (i+1)) {
							simon = true;
						}
					}
					if(simon) {
						String caracter = Character.toString(cadena.charAt(i));
						cadena = cadena.replace(Character.toString(cadena.charAt(i)),"@");
						cadena = cadena.replace("@@", " " + caracter + caracter + " ");
						cadena = cadena.replace("@", "&");
						++i;
						break;
					}
				}else {
					if(i!=0) {
					if(cadena.charAt(i) == signos[a].charAt(0) && cadena.charAt(i-1) != ' ') {
						simon = true;
					}else {
						if((i+1) <= cadena.length()-2 )
							if(cadena.charAt(i) == signos[a].charAt(0) && cadena.charAt(i+1) != ' ') {
								simon = true;
							}
					}
					}else {
						if(cadena.charAt(i) == signos[a].charAt(0)) {
							simon = true;
						}
					}
				}
				if(simon) {
					cadena =  cadena.replace(signos[a], " " + signos[a] + " ");
					break;
				}
			}
			if(i==cadena.length()-1) break;
		}
				
		
		// algo=algo;
		// =algo
		// asi;
		
		return cadena;
	}
	// Alan se fue por una silla uwu
	// Y yo que le rayo el codigo xd
	// Muahahaha
	// Si ves esto Alan, hola uwu
	// No se uando diste el control uwu
	public String getErrorL() {
		return errorL;
	}
	public void setErrorL(String errorL) {
		this.errorL = errorL;
	}
	public String[] getColumnas() {
		return columnas;
	}
	public void setColumnas(String[] columnas) {
		this.columnas = columnas;
	}
	public String[][] getFilas() {
		return filas;
	}
	public void setFilas(String[][] filas) {
		this.filas = filas;
	}
	
	
	
}