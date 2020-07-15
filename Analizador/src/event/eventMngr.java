
package event;

import java.awt.event.*;
import java.io.*;
import java.util.StringTokenizer;

import javax.swing.*;

import Aplicacion.Panel;
import clasesBase.*;

public class eventMngr implements ActionListener{
	private Panel panel;
	private JFileChooser escoger;
	private JTextArea renglones;
	private JTextArea texto;
	private JTextArea resultado;
	private JTabbedPane consolaTabla;
	
	public eventMngr(Panel pan) {
		panel = pan;
		texto = panel.getTxtEscribir();
		resultado = panel.getTxtResultado();
		escoger = new JFileChooser();
		renglones = panel.getTxtRenglones();
		consolaTabla = panel.getTpnConsolaTabla();
	}
	
	public void actionPerformed(ActionEvent e) {
		
		//evento para boton de guardar archivo
		if(e.getSource() == panel.getBtnGuardarArchivo()) {
			
			escoger.setDialogTitle("");
			int seleccion = escoger.showSaveDialog(texto);
			if(seleccion == escoger.APPROVE_OPTION) {
				
				String guardado = escoger.getSelectedFile().getName();
				String direccion = escoger.getCurrentDirectory().toString();
				
				try {
					FileWriter archivoEs = new FileWriter(direccion + "/" + guardado + ".txt");
					PrintWriter escribir = new PrintWriter(archivoEs);
					String contenido = texto.getText();
					StringTokenizer tokenizar = new StringTokenizer(contenido,"\n");
					while(tokenizar.hasMoreTokens()) {
						String linea = tokenizar.nextToken();
						escribir.println(linea);
					}
					
					escribir.close();
					
				}catch(IOException z) {
					z.printStackTrace();
				}
			}
		}
		//evento para boton analizar
		if(e.getSource() == panel.getBtnAnalizar()) {
			
			Palabritas palabritas = new Palabritas(texto.getText());

			palabritas.analizador();
			resultado.setText(palabritas.getErrorL());
			
			if(consolaTabla.getTabCount()> 1)
				consolaTabla.removeTabAt(1);
			
			JScrollPane contiene1;
			contiene1 = new JScrollPane(palabritas.tablaSimbolos());

			consolaTabla.add("Tabla Simbolos",contiene1);
			
			
		}
		//evento para boton salir
		if(e.getSource() == panel.getBtnSalir()) {
			System.exit(0);
		}
		//Evento boton para abrir archivo
		if(e.getSource() == panel.getBtnAbrirArchivo()) {
			
			int lineas = 1;
			
			if(consolaTabla.getTabCount()> 1)
				consolaTabla.removeTabAt(1);
			
			escoger.setDialogTitle("");
			int seleccion = escoger.showOpenDialog(texto);
			
			if(seleccion == escoger.APPROVE_OPTION) {
				String guardado = escoger.getSelectedFile().getName();
				String direccion = escoger.getCurrentDirectory().toString();
				try {
					FileReader lector = new FileReader(direccion+"/"+guardado);
					BufferedReader archivin = new BufferedReader(lector);
					String linea = archivin.readLine();
					StringTokenizer tokenizar;
					texto.setText("");
					lineas = 0;
					renglones.setText("");
					resultado.setText("Building in process...");
					
					while(linea != null) {
						
						tokenizar = new StringTokenizer(linea,"\n");
						String linita = "";
						renglones.append((++lineas) +"\n");
						
						try {
							linita = tokenizar.nextToken();
						}catch(Exception z) {
							
						}
						
						panel.setLineas(lineas);
						texto.append(linita+"\n");
						linea = archivin.readLine();
						
					}
					archivin.close();
				}catch(IOException z) {
					
				}
			}
		}
		
	}
	

}
