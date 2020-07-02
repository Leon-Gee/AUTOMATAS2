/*
 PROYECTO ANALIZADOR LEXICO Y SINTACTICO
 INTEGRANTES:
 - Garcia Aispuro Alan Gerardo.
 - Osuna Lizarraga Rubi Guadalupe.
 - Rodelo Cardenas Graciela.
*/

package proyecto;

import java.util.*;
import java.util.regex.*;

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
	String errorL = " LECTURA DE CODIGO COMPLETADA.\n";
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
		imprimirTabla();
		errorL += Sintactico.VerificadorSintactico(tokens);
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
		int noToken = 0, renglon = 0;
		System.out.printf(" | %3s | %20s | %20s | %20s | %40s | %20s | %20s | \n", "No.", "Modificador", "Tipo", "Nombre", "Valor", "Renglon", "No. de Token");
		int x = 0;
		for(int i = 0;i<tokens.size();i++) {
			String variable = "", valor = "", tipo = "";
			int ident = 0;
			int tipito=0;
			boolean isTipo = false, isIgual = false;
			for(int a = 0;a<tokens.get(i).size();a++) {
					int something = tokens.get(i).get(a).getTipo();
					if(isIgual&& something != PUNTO_COMA)
						valor += tokens.get(i).get(a).getValor() + " ";
					
					if(tokens.get(i).get(a).getValor().equals("=")) {
						isIgual = true;
					}
					
					if(something == IDENT) {
						variable = tokens.get(i).get(a).getValor();
						noToken = tokens.get(i).get(a).getNoToken();
						renglon = tokens.get(i).get(a).getRenglon();
						tipo = tokens.get(i).get(0).getValor();
						tipito = tokens.get(i).get(0).getTipo();
					}	
					
					
				
			}
				
			if(!tipo.isEmpty()&&(tipito == IDENT || tipito == INT || tipito == BOOLEAN))
				System.out.printf(" | %3d | %20s | %20s | %20s | %40s | %20d | %20d | \n", (++x), "", tipo, variable, valor, renglon, noToken);
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
	
}