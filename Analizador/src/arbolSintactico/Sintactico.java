/*
 PROYECTO ANALIZADOR LEXICO Y SINTACTICO
 INTEGRANTES:
 - Garcia Aispuro Alan Gerardo.
 - Meza Leon Oscar Oswaldo.
 - Osuna Lizarraga Rubi Guadalupe.
 - Rodelo Cardenas Graciela.
*/
package arbolSintactico;

import java.util.ArrayList;

import clasesBase.Tipo;
import clasesBase.Token;

public class Sintactico implements Tipo {
	private static ArrayList<Integer> llaves = new ArrayList<Integer>();
	static boolean finalizar = false, inicio = false, siIf = false;;
	static int checar=0;
	static char lastCharacter = ' ';
	static boolean isIfThere = false, isWhileThere = false, isMethodThere = false;
	public static String VerificadorSintactico(ArrayList<ArrayList<Token>> tokens) { // Solo vine a robarme esto xd Espera que por que tu hermano, tiene clase, no? para ir pensando uwu
		inicio = false;
		String errores = "";

		int esperado = 0;
		int linea = 0;
		if(!tokens.isEmpty()) {
			while(linea<tokens.size()) {
				if(!tokens.get(linea).isEmpty()) {
					while(checar < tokens.get(linea).size()) {
						finalizar = false;
						if(linea == 0 && checar == 0) {
							if(tokens.get(linea).get(checar).getTipo() != CLASS )
								errores = error(null,linea,"class");
						}
						if(linea!=0 && tokens.get(linea).get(checar).getTipo() != CLASS)
							if(!inicio) {
								errores += error(null,linea," SE ESPERABA LA INICIALIZACION DE UNA CLASE.");
							}else {
								if(tokens.get(linea).get(checar).getTipo() == RETURN||tokens.get(linea).get(checar).getTipo() == WHILE || tokens.get(linea).get(checar).getTipo() == IF || tokens.get(linea).get(checar).getTipo() == SYSTEM)
									if(!isMethodThere) errores+= error(tokens.get(linea).get(checar),0,"SE ESPERABA LA DECLARACION DEL METODO");
							}
						switch(tokens.get(linea).get(checar).getTipo()) {
							case CLASS:
								esperado = 0;
								errores += clase(esperado,tokens.get(linea));
							break; 
							case PUBLIC:
								errores+=publica(tokens.get(linea)); 
								break;
							case WHILE:
								errores+=ifSi_While(tokens.get(linea));
								break;
							case IF:
								errores += ifSi_While(tokens.get(linea));
								siIf = true;
								break;
							case ELSE:
								if(!siIf)
									errores += error(tokens.get(linea).get(checar),0,"if");
								errores += ifElse(tokens.get(linea));
								break;
							case INT:
								errores += intcito(tokens.get(linea));
								break;
							case BOOLEAN:
								errores += booleancito(tokens.get(linea));
								break;
							case IDENT:
								errores += identificador(tokens.get(linea));
								break;
							case SYSTEM:
								errores+=syso(tokens.get(linea));
								break;
							case THIS:
								errores+=identificador(tokens.get(linea));
								break;
							case RETURN:
									errores += returncito(tokens.get(linea));
								break;
							case LLAVE_C:
								errores+=llave_C(tokens.get(linea));
								if(linea == tokens.size()-1 && llaves.size()!=0)
									errores+=error(tokens.get(linea).get(checar),0,"}");
								break;
							default: // SEGUN YO AQUI ES YA CUALQUIER COSA NO ADMITIDA O ALGO AS�
								errores += error(tokens.get(linea).get(checar),0,"");
								break;
							
						}
						if(finalizar) {
							break;
						}
						checar++;

					}
				}
				checar = 0;
				linea++;		
			}	
		
			if(!llaves.isEmpty()) errores+= error(null,linea,"{");
			vaciarLlaves();
		}
		return errores;
	}
	private static String error(Token token,int linea,String noEsperado) {
		if(token != null)
			return " ERROR SINTACTICO EN LA LINEA " + token.getRenglon() + " EN LA LA COLUMNA "+ token.getNoToken() + " NO SE ESPERABA *** " + token.getValor() + " ***.\n";
		else 
			return " ERROR SINTACTICO EN LA LINEA " + linea + " NO SE ESPERABA *** " + noEsperado + " ***.\n";
	}
	public static void vaciarLlaves() {
		if(!llaves.isEmpty())
			for(int a = llaves.size()-1;a>=0;a--)
				llaves.remove(a);
	}
	public static void agregarLlaveA() {
		llaves.add(LLAVE_A);
	}
	public static boolean quitarLlaves() {
		if(llaves.size()!=0){
			llaves.remove(llaves.size()-1);
			if(isIfThere) {
				isIfThere = false;
			}else {
				if(isWhileThere) {
					isWhileThere = false;
				}else {
					isMethodThere = false;
				}
			}
		}else
			return true;
		
		return false;
	}
	public static String llave_C(ArrayList<Token> tokens) {
		String error = "";
		boolean validar = false;
		int posicion = 0;
		for(int i = checar;i<tokens.size();i++) {
			switch(tokens.get(i).getTipo()) {
				case LLAVE_C:
					checar = i;
					if(quitarLlaves())
						error+=error(null,tokens.get(i).getRenglon(),"{");
					break;
				default:
					checar = i;
					validar = true;
					break;
			}
			if(validar) {
				finalizar = false;
				break;
			}
		}
		return error;
	}
	// Comprobar la class
	public static String clase(int esp, ArrayList<Token> tokens) {
		String error = "";
		String esperado = " ";
		boolean parar = false;
		int posicion = 0;
		for(int i = checar; i<tokens.size();i++) {
			if(esp == 0 || esp == tokens.get(i).getTipo()) {
				switch(tokens.get(i).getTipo()) {
					case CLASS:
						esp = IDENT;
						esperado = "IDENTIFICADOR";
					break;
					// Este es un cambio innecesario
					case IDENT:
						esp = LLAVE_A;
						esperado = "{";
					break;
					case LLAVE_A:
						agregarLlaveA();
						parar = true;
						inicio = true;
						finalizar = true;
						esp = Integer.MAX_VALUE;
					break;
				}
			}else {
				error += error(tokens.get(i),0,"");
				
			}
			
		}
		if(esp != Integer.MAX_VALUE)
			error += error(null,tokens.get(0).getRenglon(),esperado);
		return error;
	}
	// Comprobar public
	public static String publica (ArrayList<Token> tokens) {
		int posicion=0;
		String error = "";
		ArrayList<Integer> esp = new ArrayList<Integer>();
		ArrayList<Integer> P_A = new ArrayList<Integer>();

		for(int i = checar;i<tokens.size();i++) {
			for(int q = 0;q<esp.size();q++) {
				if(esp.get(q) == tokens.get(i).getTipo()) {
					for(int a = esp.size()-1;a>=0;a--) {
						esp.remove(a);
					}
					break;
				}
			}
			int tipo = tokens.get(i).getTipo();
			if(esp.size() == 0) {
				if(i!=0)
				if(tokens.get(i-1).getTipo() == PUBLIC || tokens.get(i-1).getTipo() == COMA || tokens.get(i-1).getTipo() == PARENTESIS_A ) {
					if(tipo == IDENT)
						tipo = 20;
				}
				switch(tipo) {
				case 20:
					esp.add(IDENT);
					break;
				case PUBLIC:
					esp.add(INT);
					esp.add(BOOLEAN);
					esp.add(IDENT);
					break;
				case INT:
					esp.add(IDENT);
					break;
				case BOOLEAN:
					esp.add(IDENT);
					break;
				case IDENT:
					esp.add(PARENTESIS_A);
					esp.add(COMA);
					esp.add(PARENTESIS_C);
					break;
				case COMA:
					esp.add(IDENT);
					esp.add(INT);
					esp.add(BOOLEAN);
					break;
				case PARENTESIS_A:
					esp.add(PARENTESIS_A);
					esp.add(IDENT);
					esp.add(PARENTESIS_C);
					esp.add(INT);
					esp.add(BOOLEAN);
					P_A.add(1);
					break;
				case PARENTESIS_C:
					esp.add(LLAVE_A);
					if(P_A.size()!=0)
						P_A.remove(P_A.size()-1);
					else 
						error+=error(null,tokens.get(i).getRenglon(),")");
					break;
				case LLAVE_A:
					isMethodThere = true;
					agregarLlaveA();
					posicion=i;
					break;
				}
			}else {
				error+= error(tokens.get(i),0,"");
				for(int a=esp.size()-1;a>=0;a--)
					esp.remove(a);
			}
			if (posicion==tokens.size()-1)finalizar=true;
		}	
		if(esp.size() != 0) 
			if(esp.get(0) == LLAVE_A)
				error += error(null,tokens.get(0).getRenglon(),"{");
		if(P_A.size()!=0) error+= " ERROR SINTACTICO NO SE CERRARON " + P_A.size() + " PARENTESIS. \n";
		return error;
	}
	// Comprueba el if y while
	public static String ifSi_While(ArrayList<Token> tokens) {
		String error = "";
		ArrayList<Integer> C_A = new ArrayList<Integer>();
		ArrayList<Integer> esp = new ArrayList<Integer>();
		ArrayList<Integer> P_A = new ArrayList<Integer>();

		boolean vacio = true;
		int posicion =0 ;
		
		esp.add(PARENTESIS_A);
		for(int i = checar+1;i<tokens.size();i++) {
			for(int q = 0;q<esp.size();q++) {
				if(esp.get(q) == tokens.get(i).getTipo()) {
					for(int a = esp.size()-1;a>=0;a--) {
						esp.remove(a);
					}
					break;
				}
			}
			if(esp.size() == 0) {
				switch(tokens.get(i).getTipo()) {
					case PARENTESIS_A:
						esp.add(IDENT);
						esp.add(NUM);
						esp.add(TRUE);
						esp.add(FALSE);
						esp.add(NEGACION);
						esp.add(PARENTESIS_A);
						P_A.add(1);
						vacio = true;
					break;
					case IDENT:
						esp.add(AND);
						esp.add(CORCHETE_A);
						esp.add(PUNTO);
						esp.add(PARENTESIS_C);
						esp.add(MENOR);
						esp.add(RESTA);
						esp.add(SUMA);
						esp.add(POR);
						vacio = false;
					break;
					case PUNTO:
						esp.add(LENGTH);
					break;
					case LENGTH:
						esp.add(MENOR);
						esp.add(RESTA);
						esp.add(SUMA);
						esp.add(POR);
						esp.add(AND);
						esp.add(PARENTESIS_C);
					break;
					case TRUE:
					case FALSE:
						esp.add(AND);
						esp.add(PARENTESIS_C);
						vacio = false;
					break;
					case AND:
						esp.add(IDENT);
						esp.add(TRUE);
						esp.add(FALSE);
						esp.add(NUM);
						esp.add(PARENTESIS_A);
						esp.add(NEGACION);
						vacio = false;
						break;
					case MENOR:
						esp.add(IDENT);
						esp.add(NUM);
						esp.add(PARENTESIS_A);
						esp.add(NEGACION);
						vacio = false;
						break;
					case SUMA:
					case RESTA:
					case POR:
						esp.add(IDENT);
						esp.add(NUM);
						esp.add(PARENTESIS_A);
						break;
					case NUM:
						esp.add(MENOR);
						esp.add(AND);
						esp.add(PARENTESIS_C);
						esp.add(SUMA);
						esp.add(RESTA);
						esp.add(POR);
						vacio = false;
						break;
					case NEGACION: 
						esp.add(IDENT);
						esp.add(PARENTESIS_A);
						break;
					case CORCHETE_A:
						esp.add(NUM);
						esp.add(IDENT);
						C_A.add(1);
						vacio = false;
						break;
					case CORCHETE_C:
						esp.add(PARENTESIS_C);
						esp.add(MENOR);
						esp.add(SUMA);
						esp.add(RESTA);
						esp.add(POR);
						esp.add(AND);
						vacio = false;
						C_A.remove(C_A.size()-1);
						break;
					case PARENTESIS_C:
						esp.add(PARENTESIS_C);
						esp.add(LLAVE_A);
						esp.add(AND);
						esp.add(MENOR);
						esp.add(SUMA);
						esp.add(RESTA);
						esp.add(POR);
						posicion = i;
						if(P_A.size()!=0)
							P_A.remove(P_A.size()-1);
						else 
							error += error(null, tokens.get(i).getRenglon(), ")");
					break;
					case LLAVE_A:
						
						if(tokens.get(0).getTipo() == WHILE)
							isWhileThere=true;
						else
							isIfThere = true;
						
						agregarLlaveA();
						posicion = i;
						break;
				}
			}else {
				error+= error(tokens.get(i),0,"");
				for(int a=esp.size()-1;a>=0;a--)
					esp.remove(a);
			}
			if(posicion == tokens.size()-1) finalizar = true;
		}	
		
		if(vacio)
			error+=" ERROR SINTACTICO EN LA LINEA "+ tokens.get(0).getRenglon() + " SE ESPERABA UNA EXPRESION DENTRO DE **()**. \n";
		
		if(P_A.size()!=0) error+= " ERROR SINTACTICO NO SE CERRARON " + P_A.size() + " PARENTESIS. \n";
		return error;
	}
	// Comprueba el else
	public static String ifElse(ArrayList<Token> tokens) {
		String error = "";
		int posicion = 0; // FALTA :C
		siIf = false;
		int esp = 0;
		for(int i = checar;i<tokens.size();i++) {
			if(esp == 0 || esp == tokens.get(i).getTipo()) {
				switch(tokens.get(i).getTipo()) {
				case ELSE:
					esp = LLAVE_A;
					posicion = i;
					break;
				case LLAVE_A:
					agregarLlaveA();
					posicion = i;
					break;
				}
			}else {
				error += error(tokens.get(i),0,"");
				
			}
			if(posicion == tokens.size()-1) finalizar = true;
		}
		return error;
	}
	// Comprueba int
	public static String intcito(ArrayList<Token> tokens) {
		String error = "";
		ArrayList<Integer> C_A = new ArrayList<Integer>();
		ArrayList<Integer> esp = new ArrayList<Integer>();
		ArrayList<Integer> P_A = new ArrayList<Integer>();

		int posicion = 0;
		for(int i = checar;i<tokens.size();i++) {
			for(int q = 0;q<esp.size();q++) {
				if(esp.get(q) == tokens.get(i).getTipo()) {
					esp.clear();
					break;
				}
			}
			if(esp.size() == 0) {
				switch(tokens.get(i).getTipo()) {
				case INT:
					esp.add(IDENT);
					esp.add(CORCHETE_A);
					break;
				case PARENTESIS_A:
					esp.add(NUM);
					esp.add(IDENT);
					P_A.add(1);
					break;
				case IDENT:
					esp.add(IGUAL);
					esp.add(SUMA);
					esp.add(RESTA);
					esp.add(POR);
					esp.add(PUNTO);
					esp.add(PUNTO_COMA);
					break;
				case IGUAL:
					esp.add(NUM);
					esp.add(RESTA);
					esp.add(NEW);
					esp.add(IDENT);
					esp.add(PARENTESIS_A);
					break;
				case NEW:
					esp.add(INT);
					break;
				case NUM:
					esp.add(SUMA);
					esp.add(RESTA);
					esp.add(POR);
					esp.add(PARENTESIS_C);
					esp.add(PUNTO_COMA);
					break;
				case SUMA:
				case RESTA:
				case POR:
					esp.add(IDENT);
					esp.add(NUM);
					esp.add(PARENTESIS_A);
					if(tokens.get(i).getTipo() == POR) esp.add(RESTA);
					break;
				case CORCHETE_A:
					esp.add(NUM);
					esp.add(IDENT);
					esp.add(CORCHETE_C);
					C_A.add(1);
					break;
				case CORCHETE_C: // no se :c
					esp.add(SUMA);
					esp.add(RESTA);
					esp.add(POR);
					if(C_A.size()!=0)
						C_A.remove(C_A.size()-1);
					else 
						error += error(null,tokens.get(i).getRenglon(),"]");
					break;
				case PARENTESIS_C:
					esp.add(POR);
					esp.add(SUMA);
					esp.add(RESTA);
					esp.add(PUNTO_COMA);
					posicion = i;
					if(P_A.size()!=0)
						P_A.remove(P_A.size()-1);
					else 
						error += error(null,tokens.get(i).getRenglon(),")");
					break;
				case PUNTO_COMA:
					posicion = i;
					break;
				}
			}else {
				error+= error(tokens.get(i),0,"");
				for(int a=esp.size()-1;a>=0;a--)
					esp.remove(a);
			}
			
			if(posicion == tokens.size()-1) {
				checar = posicion;
				finalizar = true;
			}
		}
		return error;
	}
	// Comprobar return
	public static String returncito(ArrayList<Token> tokens) {
		String error = "";
		ArrayList<Integer> C_A = new ArrayList<Integer>();
		ArrayList<Integer> esp = new ArrayList<Integer>();
		ArrayList<Integer> P_A = new ArrayList<Integer>();
		
		int posicion = 0;
		for(int i = checar;i<tokens.size();i++) {
			for(int q = 0;q<esp.size();q++) {
				if(esp.get(q) == tokens.get(i).getTipo()) {
					for(int a = esp.size()-1;a>=0;a--) {
						esp.remove(a);
					}
					break;
				}
			}//khe? nose xdxdxd
			int tipo = tokens.get(i).getTipo();
			if(esp.size() == 0) {
				if(tipo == IDENT) {
					esp.add(CORCHETE_A);
				}
				if(tipo == THIS || tipo == IDENT) {
					esp.add(PUNTO);
				}
				if(tipo == TRUE || tipo == FALSE){
					esp.add(PUNTO_COMA);
				}
					
				if(tipo != NUM && tipo != LENGTH && tipo != IGUAL && tipo != CORCHETE_C && tipo != PARENTESIS_C && tipo != PUNTO_COMA && tipo != TRUE && tipo != FALSE) {
					esp.add(IDENT);
				}
				if(tipo == RETURN || tipo == PARENTESIS_A  || tipo == CORCHETE_A || tipo == AND || tipo == MENOR ||tipo == COMA || tipo == SUMA || tipo == RESTA  || tipo == POR ) {
					esp.add(PARENTESIS_A);
					esp.add(NUM);
					esp.add(IDENT);
				}
				if(tipo == IDENT || tipo == NUM || tipo == LENGTH || tipo == CORCHETE_C || tipo == PARENTESIS_C) {
					esp.add(SUMA);
					esp.add(RESTA);
					esp.add(POR);
					esp.add(COMA);
				}
				if(tipo == AND || tipo == COMA) {
					esp.add(TRUE);
					esp.add(FALSE);
					if(tipo != AND) {
						esp.add(NEW);
						esp.add(THIS);
					}
				}
				
				if(tipo == IDENT || tipo == NUM || tipo == LENGTH || tipo == CORCHETE_C || tipo == PUNTO_COMA || tipo == PARENTESIS_C) {
					esp.add(PUNTO_COMA);
					esp.add(MENOR);
				}
				if(tipo == TRUE || tipo == LENGTH || tipo == FALSE || tipo == PARENTESIS_C || tipo == CORCHETE_C  ||  tipo == NUM || tipo == IDENT) {
					esp.add(PARENTESIS_C);
					esp.add(AND);
				}
				if(tipo == PUNTO) {
					esp.add(LENGTH);
					esp.add(IDENT);
				}
				if(tipo == CORCHETE_A || tipo == CORCHETE_C) {
					esp.add(CORCHETE_C);
					if(tipo == CORCHETE_A) {
						C_A.add(1);
					}else {
						if(C_A.size() != 0)
							C_A.remove(C_A.size()-1);
						else
							error += error(null,tokens.get(i).getRenglon(),"]");
					}
				}
				if(tipo == PARENTESIS_A) {
					P_A.add(1);
				}
				if(tipo == PARENTESIS_C) {
					esp.add(PARENTESIS_A);
					esp.add(PUNTO_COMA);
					if(P_A.size() !=  0) 
						P_A.remove(P_A.size()-1);
					else
						error += error(null,tokens.get(i).getRenglon(),")");
				}
				if(tipo == PUNTO_COMA) {
					esp.add(CORCHETE_C);
					posicion = i;
					for(int a=esp.size()-1;a>=0;a--)
						esp.remove(a);					
				}
				if(tipo == CORCHETE_C) {
					quitarLlaves();
					posicion = i;
					for(int a=esp.size()-1;a>=0;a--)
						esp.remove(a);
				}
	
			}else {
				error+= error(tokens.get(i),0,"");
				for(int a=esp.size()-1;a>=0;a--)
					esp.remove(a);
			}
			if(posicion == tokens.size()-1) finalizar = true;
		}
		if(P_A.size() != 0 ) // MENSAJE DE RERROR SINTACTICO PARENTESIS
			error += error(null,tokens.get(posicion).getRenglon(),")");
			if(C_A.size() != 0) // MENSAJE ERROR SISNTACTICO CORCHETES
				error += error(null,tokens.get(posicion).getRenglon(),"]");
				if(esp.size()!=0) {
					if(esp.get(0) == PUNTO_COMA) {
						error += error(null,tokens.get(posicion).getRenglon(),";");
					}else {
						error+=" ERROR SINTACTICO NO SE FINALIZO LA SENTENCIA EN LINEA:" +tokens.get(posicion).getRenglon()+"\n";
					
					}		
				}
		return error;
	}
	// Comprobar boolean
	public static String booleancito(ArrayList<Token> tokens) {
		String error = "";
		ArrayList<Integer> esp = new ArrayList<Integer>();
		ArrayList<Integer> P_A = new ArrayList<Integer>();

		int posicion = 0;
		boolean isMenor = false;
		for(int i = checar;i<tokens.size();i++) {
			
			for(int q = 0;q<esp.size();q++) {
				if(esp.get(q) == tokens.get(i).getTipo()) {
					for(int a = esp.size()-1;a>=0;a--) {
						esp.remove(a);
					}
					break;
				}
			}
			int tipo = tokens.get(i).getTipo();
			
			if(esp.size() == 0) {
				if(tipo == IDENT)
					esp.add(IGUAL);
				if(tipo == IDENT || tipo == NUM || tipo == PARENTESIS_C) {
					esp.add(SUMA);
					esp.add(RESTA);
					esp.add(POR);
				}
				if(tipo == BOOLEAN || tipo == PARENTESIS_A || tipo == IGUAL || tipo == MENOR || tipo == AND) {
					esp.add(IDENT);
					if(tipo == PARENTESIS_A || tipo == MENOR || tipo == IGUAL)
						esp.add(NUM);
					if(tipo==MENOR)
						esp.add(PARENTESIS_A);
					if(tipo==IGUAL) {
						esp.add(NUM);
						esp.add(NEGACION);
					}
				}
				if(tipo == IDENT || tipo == NUM) {
					esp.add(MENOR);
					esp.add(AND); 
					esp.add(PARENTESIS_C);
				}
				if(tipo == IDENT || tipo == TRUE || tipo == FALSE || tipo == PARENTESIS_C) {
					esp.add(PUNTO_COMA);
					
				}
				if(tipo == NUM) {
					esp.add(PUNTO_COMA);
					isMenor = false;
				}
				if(tipo==MENOR)
					isMenor = true;
				if(tipo == IGUAL || tipo == AND) {
					esp.add(TRUE);
					esp.add(FALSE);
					esp.add(PARENTESIS_A);
				}
				if(tipo == PARENTESIS_A)
					P_A.add(1);
				if(tipo == PARENTESIS_C) {
					if(P_A.size()!=0)
						P_A.remove(P_A.size()-1);
					else 
						error += error(null,tokens.get(i).getRenglon(),")");
				}
				if(tipo == PUNTO_COMA)
					posicion = i;
			}else {
				error+= error(tokens.get(i),0,"");
				for(int a=esp.size()-1;a>=0;a--)
					esp.remove(a);
			}// Este se valida la entrada de ;? dop uwu
			if(posicion == tokens.size()-1) finalizar = true;
		}
		return error;
	}
	
	// Comprobar identificador
	public static String identificador(ArrayList<Token> tokens) {
		String error = "";
		ArrayList<Integer> C_A = new ArrayList<Integer>();
		ArrayList<Integer> esp = new ArrayList<Integer>();
		ArrayList<Integer> P_A = new ArrayList<Integer>();
		boolean noTiene= false;
		int posicion = 0;
		for(int i = checar;i<tokens.size();i++) {
			for(int q = 0;q<esp.size();q++) {
				if(esp.get(q) == tokens.get(i).getTipo()) {
					for(int a = esp.size()-1;a>=0;a--) {
						esp.remove(a);
					}
					break;
				}
			}//khe? nose xdxdxd
			int tipo = tokens.get(i).getTipo();
			boolean isIgual = false;
			if(esp.size() == 0) {
				if(tipo == IDENT ) {
					esp.add(IGUAL);
					esp.add(CORCHETE_A);
					esp.add(MENOR);
					if(i!=checar && isIgual)
						esp.add(PUNTO_COMA);
				}
				isIgual = ( tipo == IGUAL );
				if(tipo == THIS || tipo == IDENT) {
					esp.add(PUNTO);
				}
				if(tipo == TRUE || tipo == FALSE){
					esp.add(PUNTO_COMA);
				}
					
				if(tipo != NUM && tipo != THIS &&tipo != LENGTH && tipo != CORCHETE_C && tipo != PARENTESIS_C && tipo != PUNTO_COMA && tipo != TRUE && tipo != FALSE) {
					esp.add(IDENT);
				}
				if(tipo == CORCHETE_A || tipo == IGUAL)
					noTiene = true;
				if(tipo == IGUAL || tipo == IDENT ||tipo == PARENTESIS_A  || tipo == CORCHETE_A || tipo == AND || tipo == MENOR ||tipo == COMA || tipo == SUMA || tipo == RESTA  || tipo == POR ) {
					esp.add(PARENTESIS_A); // num = num*(this.ALGo);M 
					esp.add(THIS);
					esp.add(NUM);
				}
				if(tipo == POR || tipo == IGUAL) {
					esp.add(RESTA);
				}
				if(tipo == IDENT || tipo == NUM || tipo == LENGTH || tipo == CORCHETE_C || tipo == PARENTESIS_C) {
					esp.add(SUMA);
					esp.add(RESTA);
					esp.add(POR);
					esp.add(COMA);
				}
				if(tipo == IGUAL || tipo == AND || tipo == COMA) {
					esp.add(TRUE);
					esp.add(FALSE);
					if(tipo != AND) {
						esp.add(NEW);
						esp.add(THIS);
					}
					if (tipo==IGUAL)
						esp.add(NUM);
					
				}
				
				if(tipo == NUM || tipo == LENGTH || tipo == CORCHETE_C || tipo == PUNTO_COMA || tipo == PARENTESIS_C) {
					esp.add(PUNTO_COMA);
					esp.add(MENOR);
				}
				if(tipo == TRUE || tipo == LENGTH || tipo == FALSE || tipo == PARENTESIS_C || tipo == CORCHETE_C  ||  tipo == NUM || tipo == IDENT) {
					esp.add(PARENTESIS_C);
					esp.add(AND);
				}
				if(tipo == PUNTO) {
					esp.add(LENGTH);
					esp.add(IDENT);
				}
				if(tipo == CORCHETE_A || tipo == CORCHETE_C) {
					esp.add(CORCHETE_C);
					if(tipo == CORCHETE_A) {
						C_A.add(1);
					}else {
						if(C_A.size() != 0)
							C_A.remove(C_A.size()-1);
						else
							error += error(null,tokens.get(i).getRenglon(),"]");
					}
				}
				if(tipo == PARENTESIS_A) {
					P_A.add(1);
				}
				if(tipo == PARENTESIS_C) {
					esp.add(PARENTESIS_A);
					esp.add(PUNTO_COMA);
					if(P_A.size() !=  0) 
						P_A.remove(P_A.size()-1);
					else
						error += error += error(null,tokens.get(i).getRenglon(),")");
				}
				if(tipo == PUNTO_COMA) {
					posicion = i;
					for(int a=esp.size()-1;a>=0;a--)
						esp.remove(a);
				}
			}else {
				error+=error(tokens.get(i),0,"");
				for(int a=esp.size()-1;a>=0;a--)
					esp.remove(a);
			}
			if(posicion == tokens.size()-1) finalizar = true;
		}
		if(P_A.size() != 0 ) // MENSAJE DE RERROR SINTACTICO PARENTESIS
			error +=error(null,tokens.get(posicion).getRenglon(),")");
			if(C_A.size() != 0) // MENSAJE ERROR SISNTACTICO CORCHETES
				error += error(null,tokens.get(posicion).getRenglon(),"]");
				if(esp.size()!=0) {
					if(esp.get(0) == PUNTO_COMA) {
						error += error(null,tokens.get(posicion).getRenglon(),";");;
					}else {
						error+=" ERROR SINTACTICO NO SE FINALIZO LA SENTENCIA EN LINEA:" +tokens.get(posicion).getRenglon()+"\n";
					
					}		
				}
		return error;
	}
	public static String syso(ArrayList<Token> tokens) {
		String error = "";
		ArrayList<Integer> C_A = new ArrayList<Integer>();
		ArrayList<Integer> esp = new ArrayList<Integer>();
		ArrayList<Integer> P_A = new ArrayList<Integer>();
		boolean isOutThere = false;
		int posicion = 0;
		for(int i = checar;i<tokens.size();i++) {
			for(int q = 0;q<esp.size();q++) {
				if(esp.get(q) == tokens.get(i).getTipo()) {
					for(int a = esp.size()-1;a>=0;a--) {
						esp.remove(a);
					}
					break;
				}
			}
			int tipo = tokens.get(i).getTipo();
			if(esp.size() == 0) {
				if(SYSTEM == tipo || OUT == tipo || PRINT == tipo) {
					esp.add(PUNTO);
					if(tipo == OUT) {
						isOutThere = true;
					}
				}
				if(PUNTO == tipo) {
					esp.add(OUT);
					if(isOutThere) {
						esp.add(PRINT);
					}
				}
				if(PRINT == tipo || SUMA == tipo || POR == tipo || MENOR == tipo || PARENTESIS_A == tipo) {
					esp.add(PARENTESIS_A);
				}
				if(SUMA == tipo || POR == tipo || MENOR == tipo || PARENTESIS_A == tipo) {
					esp.add(NUM);
					esp.add(IDENT);
					esp.add(PARENTESIS_A);
				}
				if(PARENTESIS_A == tipo) {
					esp.add(TRUE);
					esp.add(FALSE);
					P_A.add(1);
				}
				if(TRUE == tipo || FALSE == tipo) {
					esp.add(PARENTESIS_C);
				}
				if(PARENTESIS_C==tipo || NUM == tipo || IDENT == tipo) {
					esp.add(POR);
					esp.add(SUMA);
					esp.add(RESTA);
					esp.add(PARENTESIS_C);
				}
				if(PARENTESIS_C==tipo) {
					esp.add(PUNTO_COMA);
					
					if(P_A.size() !=  0) 
						P_A.remove(P_A.size()-1);
					else
						error += error(null,tokens.get(i).getRenglon(),")");
				
				}
				if(PUNTO_COMA == tipo) {
					posicion = i;
				}
			}else {
				error+= error(tokens.get(i),0,"");
				for(int a=esp.size()-1;a>=0;a--)
					esp.remove(a);
			}
			if(posicion == tokens.size()-1) finalizar = true;
		}
		return error;
	}
}