/*
 PROYECTO ANALIZADOR LEXICO Y SINTACTICO
 INTEGRANTES:
 - Garcia Aispuro Alan Gerardo.
 - Meza Leon Oscar Oswaldo.
 - Osuna Lizarraga Rubi Guadalupe.
 - Rodelo Cardenas Graciela.
*/
package Aplicacion;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.*;

import event.eventMngr;

public class Panel extends JPanel {
	/**
	 * 
	 */
	
	private JTextArea txtEscribir;
	private JTextArea txtResultado;
	private JTextArea txtRenglones;
	private JScrollPane scbContiene;
	private JScrollPane scbContiene2;
	private JButton btnAbrirArchivo;
	private JButton btnGuardarArchivo;
	private JButton btnAnalizar;
	private JButton btnSalir;
	private JTabbedPane tpnConsolaTabla;
	private eventMngr eventos;
	private int lineas = 1;
	private JScrollPane scbRenglones;
	
	public Panel() {
		setLayout(null); 
		tpnConsolaTabla = new JTabbedPane();
		//--------------------------------------------------
		//---- CONTENEDOR PARA VER EL NUMERO DE RENGLON ----
		//--------------------------------------------------
		txtRenglones = new JTextArea();
		
		txtRenglones.setFont(new Font("Consolas",0,12));
		txtRenglones.setBorder(BorderFactory.createLineBorder( Color.BLACK, 1 ));
		txtRenglones.setText("1\n");
	
		
		
		scbRenglones = new JScrollPane(scbRenglones);
		scbRenglones.setBounds(6, 10, 24, 400);
		scbRenglones.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		// -----------------------------------------
		 
		//-----------------------------------------
		//---- CONTENEDOR PARA ESCRIBIR CODIGO ----
		//-----------------------------------------
		txtEscribir = new JTextArea(2000,1000);
		txtEscribir.setFont(new Font("Consolas",0,12));
		
		
		
		scbContiene = new JScrollPane(txtEscribir);
		scbContiene.setWheelScrollingEnabled(true);
		scbContiene.setBounds(30,10,600,400);
		scbContiene.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                scbRenglones.getVerticalScrollBar().setValue(scbContiene.getVerticalScrollBar().getValue());
            }
        });
		
		// -----------------------------------------
		
		txtResultado = new JTextArea("Building in process...");
		
		eventos = new eventMngr(this);
		txtEscribir.addKeyListener(new KeyAdapter() {
		    @Override
		    public void keyPressed(KeyEvent e) {
		        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
		        	txtRenglones.append(++lineas + "\n");
		        }
		    }     
		});
	
		
	      
		// ------------------------------------------
		// ---- SELECCIONAR UN ARCHIVO EXISTENTE ----
		// ------------------------------------------
		
		btnAbrirArchivo = new JButton(" Abrir Archivo");
		btnAbrirArchivo.setBounds(640,10,130,20);
		btnAbrirArchivo.addActionListener(new eventMngr(this));
		
		// ------------------------------------------
		
		// ------------------------------------------
		// ------ GUARDAR UN ARCHIVO CREADO ---------
		// ------------------------------------------
		btnGuardarArchivo = new JButton("Guardar Archivo");
		btnGuardarArchivo.setBounds(640, 50, 130,20);
		btnGuardarArchivo.addActionListener(new eventMngr(this));
		//-------------------------------------------
		// ANALIZAR
		btnAnalizar = new JButton("Analizar");
		btnAnalizar.setBounds(640, 90, 130, 20);
		btnAnalizar.addActionListener(new eventMngr(this));
		// SALIR 
		btnSalir = new JButton("Salir");
		btnSalir.setBounds(640,130,130,20);
		btnSalir.addActionListener(new eventMngr(this));
		
		
		
		scbContiene2 = new JScrollPane(txtResultado);
		
		scbContiene2.setWheelScrollingEnabled(true);
		scbContiene2.setSize(750, 120);
		
		tpnConsolaTabla.add("Consola",scbContiene2);
		tpnConsolaTabla.setBounds(30,425,600,120);
		txtResultado.setEditable(false);
		
		
		add(scbRenglones);
		add(scbContiene);
		add(btnAbrirArchivo);
	    add(btnGuardarArchivo);
	    add(btnAnalizar);
	    add(tpnConsolaTabla);
		add(btnSalir);
	    
	}

	public JTextArea getTxtEscribir() {
		return txtEscribir;
	}

	public JTextArea getTxtResultado() {
		return txtResultado;
	}

	public JTextArea getTxtRenglones() {
		return txtRenglones;
	}

	public JScrollPane getScbContiene() {
		return scbContiene;
	}

	public JScrollPane getScbContiene2() {
		return scbContiene2;
	}

	public JButton getBtnAbrirArchivo() {
		return btnAbrirArchivo;
	}

	public JButton getBtnGuardarArchivo() {
		return btnGuardarArchivo;
	}

	public JButton getBtnAnalizar() {
		return btnAnalizar;
	}

	public JButton getBtnSalir() {
		return btnSalir;
	}

	public JTabbedPane getTpnConsolaTabla() {
		return tpnConsolaTabla;
	}

	public JScrollPane getScbRenglones() {
		return scbRenglones;
	}
	
	
	

	
}
