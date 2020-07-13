package clasesBase;

public class TablaSimbolos {
	private String nombre;
	private String tipoDato;
	private int posicion;
	private String valor;
	
	public TablaSimbolos() {
		
	}
	
	public TablaSimbolos(String nom, String tipo, int pos, String val) {
		nombre = nom;
		tipoDato = tipo;
		posicion = pos;
		valor = val;
	}
	
	public String getNombre() {
		return nombre;
	}
	
	public void setNombre(String nom) {
		nombre = nom;
	}
	
	public String getTipoDato() {
		return tipoDato;
	}
	
	public void setTipoDato(String tipo) {
		tipoDato = tipo;
	} 
	
	public int getPosicion() {
		return posicion;
	}
	
	public void setPosicion(int pos) {
		posicion = pos;
	} 
	
	public String getValor() {
		return valor;
	}
	
	public void setValor(String val) {
		valor = val;
	} 
	
}