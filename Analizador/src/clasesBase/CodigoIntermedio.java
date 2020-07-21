package clasesBase;

import java.awt.BorderLayout;
import java.util.*;

import javax.swing.*;

public class CodigoIntermedio {
	private Semantico semantico;
	private String expresiones;
	private ArrayList<String> pila;
	private ArrayList<String> posfija;
	private Vector<String> columnName;
	private HashMap<Integer,ArrayList<Vector>> jTCuadruplos;
	public CodigoIntermedio(Semantico sem) {
		semantico = sem;
		expresiones = semantico.getExpresiones();
		pila = new ArrayList<String>();
		posfija = new ArrayList<String>();
		columnName = new Vector<String>();
		jTCuadruplos = new HashMap<Integer,ArrayList<Vector>>();
		
		columnName.add("Operador");
		columnName.add("Operando");
		columnName.add("Operando2");
		columnName.add("Resultado");
	}
	
	public void evaluarExpresion() { // Proximo a pensar
		StringTokenizer var = new StringTokenizer(expresiones," ");
		String variable = "";
		while(var.hasMoreTokens()) {
			variable = var.nextToken();
			posfijo(variable);
		}
		cuadruplos();
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
	
	// ire a comer uwu
	private void cuadruplos() {
		StringTokenizer variable = new StringTokenizer(expresiones, " ");
		for(int i = 0;i<posfija.size();i++) {
			Vector<Vector<String>> rowData = new Vector<Vector<String>>();
			StringTokenizer posfijo = new StringTokenizer(posfija.get(i)," ");
			
			int tempCount = 1;
			
			while(posfijo.hasMoreTokens()) {
				Vector<String> expresion = new Vector<String>();
				String valor = posfijo.nextToken();
				if(valor.matches("[0-9]+([0-9])*")||valor.matches("[a-zA-Z]+([a-zA-Z0-9])*")) {
					pila.add(valor);
				}else {
					String operand2 = pila.remove(pila.size()-1),operand1 = pila.remove(pila.size()-1);
					expresion.add(valor);
					expresion.add(operand1);
					expresion.add(operand2);
					expresion.add("T"+tempCount);
					
					pila.add("T"+tempCount);
					tempCount++;
				}
				if(!expresion.isEmpty())
					rowData.add(expresion);
				
			}
			String var = "";
			if(variable.hasMoreTokens()) {
				var = variable.nextToken();
			}
				
				rowData.add(new Vector<String>());
				rowData.get(rowData.size()-1).add(":=");
				rowData.get(rowData.size()-1).add(pila.remove(pila.size()-1));
				rowData.get(rowData.size()-1).add(" ");
				rowData.get(rowData.size()-1).add(var);
			
			jTCuadruplos.put(i,new ArrayList<Vector>());
			Vector<String> expresioncita = new Vector<String>();
			expresioncita.add(var);
			expresioncita.add(semantico.getExpresion(var));
			expresioncita.add(semantico.getPosicion(var));
			
			jTCuadruplos.get(i).add(expresioncita); // Es para poder hacer los JTable
			jTCuadruplos.get(i).add(rowData);
			
			
			System.out.println(rowData.toString());
			
		}
		// Interacciones para mostrar los cuadruplos XD...
		// Es mucho rollo <3
		// Solo es un ejemplo.. Please no se fien de que esto lo muestra por que ni lo muestra bien xD...
		for(int i = 0;i<jTCuadruplos.size();i++) {
			JFrame ventana = new JFrame("PRUEBA " + ((Vector<String>)jTCuadruplos.get(i).get(0)).get(0) + " = " + ((Vector<String>)jTCuadruplos.get(i).get(0)).get(1) + " #" + ((Vector<String>)jTCuadruplos.get(i).get(0)).get(2));
			ventana.setVisible(true);
			
			JTable table = new JTable((Vector<Vector<String>>)jTCuadruplos.get(i).get(1), columnName);
	
		    JScrollPane scrollPane = new JScrollPane(table);
		    ventana.add(scrollPane, BorderLayout.CENTER);
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
