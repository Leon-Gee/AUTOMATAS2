package clasesBase;

import java.util.*;

public class CodigoIntermedio {
	private Semantico semantico;
	private String expresiones;
	private ArrayList<String> pila;
	private ArrayList<String> posfija;
	public CodigoIntermedio(Semantico sem) {
		semantico = sem;
		expresiones = semantico.getExpresiones();
		pila = new ArrayList<String>();
		posfija = new ArrayList<String>();
	}
	
	public void evaluarExpresion() { // Proximo a pensar
		StringTokenizer var = new StringTokenizer(expresiones," ");
		String variable = "";
		while(var.hasMoreTokens()) {
			variable = var.nextToken();
			posfijo(variable);
		}
		System.out.println(posfija.toString());
	}
	private void posfijo(String variable) {
		String expresion = semantico.getExpresion(variable);
		StringTokenizer exp = new StringTokenizer(expresion, " ");
		// Comenzando a analizar al expresión...
		posfija.add("");
		boolean op = false;
		int countTokens = 0;
		while(exp.hasMoreTokens()) {
			++countTokens;
			String token = exp.nextToken();
			// Aquí debería empezar la evaluación postfija, ó la que se escoja...
			// Recordando que las expresiones se separan con espacios...
			if(token.matches("[0-9]+([0-9])*") || token.matches("[a-zA-Z]+([a-zA-Z0-9])*")) { // Si son numeros.. que hace?
				posfija.set(posfija.size()-1, posfija.get(posfija.size()-1) + " " + token);
				op = false;
			}else {
				if(countTokens == 1 && token.equals("-")) {
					posfija.set(posfija.size()-1, posfija.get(posfija.size()-1) + " " + "0");	
				}
				if(token.equals("(")) {
					pila.add(token);
					op = false;
				}else {
					if(token.equals(")")){
						int i = pila.size()-1;
						while(!pila.get(i).equals("(")) {
							posfija.set(posfija.size()-1, posfija.get(posfija.size()-1) + " " + pila.remove(i));
							i--;
						}
						pila.remove(i);
					}else {
						if(token.equals("*"))
							op = true;
						if(token.equals("-")&&op) {
							posfija.set(posfija.size()-1, posfija.get(posfija.size()-1) + " " + "0");
							pila.add(token);
						}else {
							if(importanciaOperador(watchElemento()) == importanciaOperador(token)) {
								posfija.set(posfija.size()-1, posfija.get(posfija.size()-1) + " " + pila.remove(pila.size()-1));
								pila.add(token);
							}else {
								if(importanciaOperador(token) > importanciaOperador(watchElemento())) {
									pila.add(token);
								}else {
									int i = pila.size()-1;
									while(!pila.isEmpty()) {
										posfija.set(posfija.size()-1, posfija.get(posfija.size()-1) + " " + pila.remove(i));
										i--;
									}
									System.out.println();
									pila.add(token);
								}
							}
						}
						
					}
				}
			}
		}
		int i = pila.size()-1;
		while(!pila.isEmpty()) {
			posfija.set(posfija.size()-1, posfija.get(posfija.size()-1) + " " + pila.remove(i));
			i--;
		}
	}
	private String watchElemento() {
		if(!pila.isEmpty())
			return pila.get(pila.size()-1);
		else
			return "";
	}

	// Para obtener el valor de el operador
	private int importanciaOperador(String operador) {
		int importancia = 0;
		switch(operador) {
		case "*":
			importancia = 4;
			break;
		case "+":
			importancia = 3;
			break;
		case "-":
			importancia = 3;
			break;
		}
		
		return importancia;
	}
	
	
}
