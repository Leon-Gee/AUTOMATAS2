/*
 PROYECTO ANALIZADOR LEXICO Y SINTACTICO
 INTEGRANTES:
 - Garcia Aispuro Alan Gerardo.
 - Meza Leon Oscar Oswaldo.
 - Osuna Lizarraga Rubi Guadalupe.
 - Rodelo Cardenas Graciela.
*/
package clasesBase;

public interface Tipo {
	
	int IDENT = 1;
	// OPERADORES LOGICOS Y ARITMETICOS
	int IGUAL = 2;
	int SUMA = 3;
	int RESTA = 4;
	int MENOR = 5;
	int POR = 6;
	int AND = 7;
	// SIMBOLOS RADOS
	int LLAVE_A = 8;
	int LLAVE_C = 9;
	int CORCHETE_A = 10;
	int CORCHETE_C = 11;
	int PARENTESIS_A = 12;
	int PARENTESIS_C = 13;
	int COMA = 14;
	int PUNTO_COMA = 15;
	int NEGACION = 16;
	int PUNTO = 17;
	int NUM = 18;
	// PALABRAS RESERVADAS
	int PUBLIC = -1;
	int CLASS = -2;
	int INT = -3;
	int BOOLEAN = -4;
	int IF = -5;
	int ELSE = -6;
	int WHILE = -7;
	int TRUE = -8;
	int FALSE = -9;
	int THIS = -10;
	int NEW = -11;
	int LENGTH = -12;
	int SYSTEM = -13;
	int OUT = -14;
	int PRINT = -15;
	int RETURN = -16;

	int ERROR = -666;
	
	
	

}