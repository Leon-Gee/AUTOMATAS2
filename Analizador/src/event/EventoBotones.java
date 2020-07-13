
package event;

import java.awt.event.*;
import java.io.*;
import java.util.StringTokenizer;

import javax.swing.*;

import clasesBase.Palabritas;

public class EventoBotones {
	private AbrirArchivo abrir;
	private GuardarArchivo guardar;
	private Analizar analizar;
	private Salir salir;
	private Lineas linea;
	private JFileChooser escoger;
	private JTextArea renglones;
	private int lineas;
	private JTextArea texto;
	private JTextArea resultado;
	private	JTabbedPane consolaTabla;
	
	public EventoBotones(JTextArea txt, JTextArea reng, JTextArea result, JTabbedPane cT) {
		
		texto = txt;
		resultado = result;
		abrir = new AbrirArchivo();
		salir = new Salir();
		guardar = new GuardarArchivo();
		escoger = new JFileChooser();
		analizar = new Analizar();
		renglones = reng;
		linea = new Lineas();
		consolaTabla = cT;
		
		lineas = 1;
	}
	
	public class GuardarArchivo implements ActionListener{

	
		public void actionPerformed(ActionEvent event) {
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
				}catch(IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	public class Analizar implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			Palabritas palabritas = new Palabritas(texto.getText());
			palabritas.analizador();
			resultado.setText(palabritas.getErrorL());
			if(consolaTabla.getTabCount()> 1)
				consolaTabla.removeTabAt(1);
			
			JScrollPane contiene1;
			contiene1 = new JScrollPane(new JTable(palabritas.getFilas(),palabritas.getColumnas()));
			contiene1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
			consolaTabla.add("Tabla Simbolos",contiene1);
		}
		
	}
	public class Salir implements ActionListener{
		public void actionPerformed(ActionEvent event) {
			System.exit(0);
		}
	}
	public class Lineas implements KeyListener{

		@Override
		public void keyPressed(KeyEvent e) {
	        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
	        	renglones.append(++lineas + "\n");
	        }
	    }

		@Override
		public void keyReleased(KeyEvent e) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void keyTyped(KeyEvent e) {
			// TODO Auto-generated method stub
		}
		
	}
	public class AbrirArchivo implements ActionListener{

	
		public void actionPerformed(ActionEvent event) {
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
						}catch(Exception e) {
							
						}
						texto.append(linita+"\n");
						linea = archivin.readLine();
					}
					archivin.close();
				}catch(IOException e) {
					
				}
			}
			
		}
		
	}
}
