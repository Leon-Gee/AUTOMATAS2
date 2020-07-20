package clasesBase;

import java.util.StringTokenizer;

public class CodigoIntermedio {
	private Semantico semantico;
	private String expresiones;
	public CodigoIntermedio(Semantico sem) {
		sem = semantico;
		expresiones = sem.getExpresiones();
	}
	
	private void hacerIntermedio() { // Proximo a pensar
		StringTokenizer var = new StringTokenizer(expresiones," ");
		String variable = "";
		String expresion = "";
		while(var.hasMoreTokens()) {
			variable = var.nextToken();
			expresion = semantico.getExpresion(variable);
			
			// Y ya aquí comienza lo demás xd
			
		}
	}
	
	
}
