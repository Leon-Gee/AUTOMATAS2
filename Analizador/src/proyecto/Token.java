/*
 PROYECTO ANALIZADOR LEXICO Y SINTACTICO
 INTEGRANTES:
 - Garcia Aispuro Alan Gerardo.
 - Osuna Lizarraga Rubi Guadalupe.
 - Rodelo Cardenas Graciela.
*/

package proyecto;

public class Token {
	private int renglon;
	private int noToken;
	private String valor;
	private int tipo;
	public Token(int renglin, int noTokin, String value, int tipin) {
		renglon = renglin;
		noToken = noTokin;
		valor = value;
		tipo = tipin;
	}
	public int getRenglon() {
		return renglon;
	}
	public void setRenglon(int renglon) {
		this.renglon = renglon;
	}
	public int getNoToken() {
		return noToken;
	}
	public void setNoToken(int noToken) {
		this.noToken = noToken;
	}
	public String getValor() {
		return valor;
	}
	public void setValor(String valor) {
		this.valor = valor;
	}
	public int getTipo() {
		return tipo;
	}
	public void setTipo(int tipo) {
		this.tipo = tipo;
	}
	public String toString() {
		return " "+renglon+" | "+noToken+" | "+valor+" | "+tipo;
	}
	
}
