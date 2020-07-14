package clasesBase;

import java.util.HashMap;
import java.util.*;


public class Semantico implements Tipo {
	private HashMap<String, TablaSimbolos> tablaSimbolos;
	private Palabritas lexico;
	private String[] columnas;
	private String[][] filas;
	private ArrayList<ArrayList<Token>> tokens;
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
		String errorL = "";
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
				if(tablaSimbolos.containsKey(variable)) {
					errorL+= "LA VARIABLE: ***"+ variable+ "*** QUE SE INTENTA DECLARAR EN LA POSICIÓN: ***"+renglon+"*** YA EXISTE EN LA POSICIÓN: ***" + tablaSimbolos.get(variable).getPosicion() +"***.\n"; 
					existe=true;
				}
				//Verificar el tipo de dato
				if (tipo.equals("int")) {
					datoCorrecto = isEntero(variable,valor);
					if(!datoCorrecto) 
						errorL+="EL VALOR DE LA VARIABLE: ***"+ variable+ "*** DEBE SER DE TIPO INT Y LE INTENTA COLOCAR EL VALOR: ***" + valor+"***.\n"; 	
				}
				if (tipo.equals("boolean")) {
					datoCorrecto = isBoolean(variable,valor);
					if(!datoCorrecto) 
						errorL+="EL VALOR DE LA VARIABLE: ***"+ variable+ "*** DEBE SER DE TIPO BOOLEAN Y LE INTENTA COLOCAR EL VALOR: ***" + valor+"***.\n"; 	
				
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
					
					tablaSimbolos.put(variable, new TablaSimbolos(variable, tipo, renglon, valor,fila));
					
				}
				existe=false;
				datoCorrecto=true;
			}
			if (isused) {
				//Ver si está declarada
				boolean existe= tablaSimbolos.containsKey(variable);
				
				if (existe==false)
					errorL+= "ERROR SEMÁNTICO, LA VARIABLE: "+ variable+ " QUE SE INTENTA USAR EN LA POSICIÓN: "+renglon+" NO SE ENCUENTRA DECLARADA"+".\n";
				existe=false;
				
			}
		}
		return errorL;
	}
	/*
	 * 
	 * */
	private boolean isEntero(String variable, String valor) {
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
				valido=false;
				break;
			}
		}
		return valido;
	}
	private boolean isBoolean(String variable, String valor) {
		boolean datoCorrecto = true; 
		if ((!(valor.equals("true "))) && (!(valor.equals("false ")))) {
			datoCorrecto = false;
		}
		return datoCorrecto;
	}
}
