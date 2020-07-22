package clasesBase;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.*;

import javax.swing.*;

public class CodigoIntermedio {
	private Semantico semantico;
	private String expresiones;
	private ArrayList<String> pila;
	private ArrayList<String> posfija;
	private Vector<String> columnName;
	private HashMap<Integer,ArrayList<Vector>> jTCuadruplos;
	private HashMap<String, TablaSimbolos> tablaSimbolos;
	Vector<Vector<String>> cuadruplo=new Vector<Vector<String>>();
	public CodigoIntermedio(Semantico sem) {
		semantico = sem;
		expresiones = semantico.getExpresiones();
		pila = new ArrayList<String>();
		posfija = new ArrayList<String>();
		columnName = new Vector<String>();
		jTCuadruplos = new HashMap<Integer,ArrayList<Vector>>();
		tablaSimbolos= semantico.getTabla();
		columnName.add("Operador");
		columnName.add("Operando");
		columnName.add("Operando2");
		columnName.add("Resultado");
	}
	//Pruebita de evaluaci�n de expresiones
	public void evaluacionExpresion (Vector<Vector<String>> cuadruplo) {
		System.out.println("Tabla:" +tablaSimbolos.size());
		System.out.println(cuadruplo.toString());
		int resultado=0;
		int num1=0, num2=0;
		String temp="";
		int i=0;
		boolean isTemporal=false;
		ArrayList<String>variabes=new ArrayList<String>();
		ArrayList<Integer>evaluacion=new ArrayList<Integer>();
		ArrayList<String>varTemp=new ArrayList<String>();
		ArrayList<Integer>valorVar=new ArrayList<Integer>();
		char operador=' ';
			while(i<cuadruplo.size()) {
				System.out.println("Cuadruplo: "+cuadruplo.get(i).toString());
					for (int j=0; j<cuadruplo.get(i).size();j++) {
						System.out.println(j);
					if(j==0&&cuadruplo.get(i).get(j).equals("*"))
						operador='*';
					else if(j==0&&cuadruplo.get(i).get(j).equals("+")) 
						operador='+';
					else if (j==0&&cuadruplo.get(i).get(j).equals("-"))
						operador='-';
					else if(j==0&&cuadruplo.get(i).get(j).equals(":=")) { //Se trata de una asignacion
						//System.out.println("introduce Valor: ");
						//System.out.println((cuadruplo.get(i).get(2).equals(" ")));
						if(cuadruplo.get(i).get(2).equals(" ")&&valorVar.isEmpty()) {
							tablaSimbolos.get(cuadruplo.get(i).get(3)).setValor(cuadruplo.get(i).get(1));
							//Falta cambiar valor en la impresion 
							semantico.getFilas()[tablaSimbolos.get(cuadruplo.get(i).get(3)).getFila()][3] = cuadruplo.get(i).get(1);
							System.out.println(cuadruplo.get(i).get(1));
						}
						else {
							tablaSimbolos.get(cuadruplo.get(i).get(3)).setValor(valorVar.get(valorVar.size()-1).toString());
							//System.out.println(valorVar.get(0));
							semantico.getFilas()[tablaSimbolos.get(cuadruplo.get(i).get(3)).getFila()][3] = valorVar.get(valorVar.size()-1).toString();
							
							varTemp.clear();
							valorVar.clear();
						}
					}
					else if(j==1&&cuadruplo.get(i).get(j).matches("[0-9]+([0-9])*")) {
						num1=Integer.parseInt(cuadruplo.get(i).get(j));
					}else if(j==1) {//Se trata de un identificador
							//Buscar en la tabla de variables temporales 
							for (int g=0;g<varTemp.size();g++) {
								if (varTemp.get(g).equals(cuadruplo.get(i).get(j)))
									num1=valorVar.get(g);
								isTemporal=true;
							}
							
							if(!isTemporal&&tablaSimbolos.get(cuadruplo.get(i).get(j))!=null) {
								
								num1=Integer.parseInt(tablaSimbolos.get(cuadruplo.get(i).get(j)).getValor());
							    isTemporal=false;
							}

						}
						else if(j==2&&cuadruplo.get(i).get(j).matches("[0-9]+([0-9])*")) {
						num2=Integer.parseInt(cuadruplo.get(i).get(j));
					}else {
						if(j==2) {//Se trata de un identificador
							//Buscar en la tabla de variables temporales 
							for (int g=0;g<varTemp.size();g++) {
								if (cuadruplo.get(i).get(j).toString().equals(varTemp.get(g)))
									num2=valorVar.get(g);
								isTemporal=true;
							}
							if(!isTemporal&&tablaSimbolos.get(cuadruplo.get(i).get(j))!=null) {
								//System.out.println("valorcin: :"+tablaSimbolos.get(cuadruplo.get(i).get(j)));
								num2=Integer.parseInt(tablaSimbolos.get(cuadruplo.get(i).get(j)).getValor());
							isTemporal=false;
							}
						}
					}
					if (j==3) {
						if(cuadruplo.get(i).get(j).toString().charAt(0)=='T') {
						temp=cuadruplo.get(i).get(j).toString();
						//System.out.println(temp);
						varTemp.add(temp);
						}
						switch(operador) {
						case '*':
							valorVar.add(num1*num2);
							break;
						case '-':
							valorVar.add(num1-num2);
						break;
						case '+':
							valorVar.add(num1+num2);
							break;
						}
						
								}
							
						
					}
				
				
					i++;
					/*System.out.println("num1"+num1);
					System.out.println("num2"+num2);
					System.out.println("op1"+operador);
					System.out.println("CambiaVAlor:"+tablaSimbolos.get("b").getValor());
					*/
			}
			
		//System.out.println(resultado);
		varTemp.clear();
		valorVar.clear();
		//System.out.println("CambiaVAlor:"+tablaSimbolos.get("c").getValor());
	}
	
	public void evaluarExpresion() { // Proximo a pensar
		StringTokenizer var = new StringTokenizer(expresiones," ");
		String variable = "";
		while(var.hasMoreTokens()) {
			variable = var.nextToken();
			posfijo(variable);
		}
		System.out.println(posfija.toString());
		cuadruplo=cuadruplos();
		evaluacionExpresion(cuadruplo);
		
	}
	public HashMap<Integer,ArrayList<Vector>> tablaCuadruplos() {
		return jTCuadruplos;
	}
	public Vector<String> columnName(){
		return columnName;
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
			//La pila contiene todos los operadores y los par�ntesis que abren, salen cuando hay un par�ntesis que cierra o un operador de menor importancia =)
			// Recordando que las expresiones se separan con espacios...
			if(token.matches("[0-9]+([0-9])*") || token.matches("[a-zA-Z]+([a-zA-Z0-9])*")) { // Si son numeros.. que hace?
				posfija.set(posfija.size()-1, posfija.get(posfija.size()-1) + " " + token);
				op = false;
			}else {
				if(countTokens == 1 && token.equals("-")) {//Si se trata de un n�mero negativo =)
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
						pila.remove(i);//Quita par�ntesis que abre 
					}else {
						if(token.equals("*"))
							op = true;
						if(token.equals("-")&&op) {
							posfija.set(posfija.size()-1, posfija.get(posfija.size()-1) + " " + "0");//Multiplicaci�n de n�meros negativos
							pila.add(token);
						}else {
							if(importanciaOperador(watchElemento()) == importanciaOperador(token)) {//Si se trata de operadores con igual importancia
								posfija.set(posfija.size()-1, posfija.get(posfija.size()-1) + " " + pila.remove(pila.size()-1));
								pila.add(token);
							}else {
								if(importanciaOperador(token) > importanciaOperador(watchElemento())) { //Si el operador que entra tiene mayor importancia
									pila.add(token);
								}else { //Cuando la importancia del operador es menor
									int i = pila.size()-1;
									while(!pila.isEmpty()) {//Vac�a y a�ado lo que entra
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
		while(!pila.isEmpty()) { //Vaciar la pila
			posfija.set(posfija.size()-1, posfija.get(posfija.size()-1) + " " + pila.remove(i));
			i--;
		}		
		
	}
	
	// ire a comer uwu
	private Vector<Vector<String>> cuadruplos() {
		StringTokenizer variable = new StringTokenizer(expresiones, " ");
		JTable table = null;
		Vector<Vector<String>> rowData = new Vector<Vector<String>>();
		for(int i = 0;i<posfija.size();i++) {
			
			StringTokenizer posfijo = new StringTokenizer(posfija.get(i)," ");
			
			int tempCount = 1;
			
			while(posfijo.hasMoreTokens()) {
				Vector<String> expresion = new Vector<String>();
				String valor = posfijo.nextToken();
				if(valor.matches("[0-9]+([0-9])*")||valor.matches("[a-zA-Z]+([a-zA-Z0-9])*")) {//Si se trata de un number o identificador
					pila.add(valor);
				}else {//Es un signo =)
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
		
		return rowData;
	}
	private String watchElemento() { //Retorna el elemento m�s arriba en la pila
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
