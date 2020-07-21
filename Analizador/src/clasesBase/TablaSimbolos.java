package clasesBase;

public class TablaSimbolos {


	private int fila;
	private String nombre;
	private String tipoDato;
	private int posicion;
	private String valor;
	private int alcance;
	
	public TablaSimbolos() {
		
	}
	
	public TablaSimbolos(String nom, String tipo, int pos, String val, int filaTabla, int alcance) {
		nombre = nom;
		tipoDato = tipo;
		posicion = pos;
		valor = val;
		fila = filaTabla;
		this.alcance = alcance;
	}
	public int getAlcance() {
		return alcance;
	}
	public int getFila() {
		return fila;
	}
	public void setFila(int fila) {
		this.fila = fila;
	}

	public void setAlcance(int alcance) {
		this.alcance = alcance;
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
