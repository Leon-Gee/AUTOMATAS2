/*
 PROYECTO ANALIZADOR LEXICO Y SINTACTICO
 INTEGRANTES:
 - Garcia Aispuro Alan Gerardo.
 - Osuna Lizarraga Rubi Guadalupe.
 - Rodelo Cardenas Graciela.
*/

package proyecto;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.*;

public class Panel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L; // Esto me lo pedía por heredar de JPanel xD
	JTextArea escribir;
	JTextArea resultado;
	JTextArea renglones;
	JScrollPane contiene1;
	JScrollPane contiene2;
	JButton abrirArchivo;
	JButton guardarArchivo;
	JButton analizar;
	JButton salir;
	EventoBotones eventos;
	int lineas = 1;
	JScrollPane scrollRenglones;
	
	public Panel() {
		setLayout(null); 
		
		//--------------------------------------------------
		//---- CONTENEDOR PARA VER EL NUMERO DE RENGLON ----
		//--------------------------------------------------
		renglones = new JTextArea();
		renglones.setFont(new Font("Consolas",0,12));
		renglones.setBorder(BorderFactory.createLineBorder( Color.BLACK, 1 ));
		renglones.setEditable(false);
		renglones.setText("1\n");
		
		scrollRenglones = new JScrollPane(renglones);
		scrollRenglones.setBounds(6, 10, 24, 400);
		scrollRenglones.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		// -----------------------------------------
		 
		//-----------------------------------------
		//---- CONTENEDOR PARA ESCRIBIR CODIGO ----
		//-----------------------------------------
		escribir = new JTextArea(2000,1000);
		escribir.setFont(new Font("Consolas",0,12));
		
		
		
		contiene1 = new JScrollPane(escribir);
		contiene1.setWheelScrollingEnabled(true);
		contiene1.setBounds(30,10,600,400);
		contiene1.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                scrollRenglones.getVerticalScrollBar().setValue(contiene1.getVerticalScrollBar().getValue());
            }
        });
		
		// -----------------------------------------
		
		resultado = new JTextArea("Building in process...");
		
		eventos = new EventoBotones(escribir, renglones, resultado);
		escribir.addKeyListener(eventos.linea);
		
		
	      
		// ------------------------------------------
		// ---- SELECCIONAR UN ARCHIVO EXISTENTE ----
		// ------------------------------------------
		
		abrirArchivo = new JButton(" Abrir Archivo");
		abrirArchivo.setBounds(640,10,130,20);
		abrirArchivo.addActionListener(eventos.abrir);
		
		// ------------------------------------------
		
		// ------------------------------------------
		// ------ GUARDAR UN ARCHIVO CREADO ---------
		// ------------------------------------------
		guardarArchivo = new JButton("Guardar Archivo");
		guardarArchivo.setBounds(640, 50, 130,20);
		guardarArchivo.addActionListener(eventos.guardar);
		//-------------------------------------------
		// ANALIZAR
		analizar = new JButton("Analizar");
		analizar.setBounds(640, 90, 130, 20);
		analizar.addActionListener(eventos.analizar);
		// SALIR 
		salir = new JButton("Salir");
		salir.setBounds(640,130,130,20);
		salir.addActionListener(eventos.salir);
		
		
		
		contiene2 = new JScrollPane(resultado);
		contiene2.setBounds(30,425,600,120);
		contiene2.setWheelScrollingEnabled(true);
		contiene2.setSize(750, 120);
		resultado.setEditable(false);
		
		
		add(scrollRenglones);
		add(contiene1);
		add(abrirArchivo);
	    add(guardarArchivo);
	    add(analizar);
	    add(contiene2);
		add(salir);
	    
	}
	

	
}
