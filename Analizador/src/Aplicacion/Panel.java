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
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.*;

import event.eventMngr;

public class Panel extends JPanel {

	private JTextArea txtEscribir;
	private JTextArea txtResultado;
	private JTextArea txtRenglones;
	private JScrollPane scbContiene;
	private JScrollPane scbContiene2;
	private JButton btnAbrirArchivo;
	private JButton btnGuardarArchivo;
	private JButton btnAnalizar;
	private JButton btnSalir;
	private JButton btnModoObscuro;
	private JTabbedPane tpnConsolaTabla;
	private eventMngr eventos;
	private int lineas = 1;
	private boolean obs = false;
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
	
		
		scbRenglones = new JScrollPane(txtRenglones);
		scbRenglones.setBounds(6, 10, 24, 400);
		scbRenglones.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		// -----------------------------------------
		 
		//-----------------------------------------
		//---- CONTENEDOR PARA ESCRIBIR CODIGO ----
		//-----------------------------------------
		txtEscribir = new JTextArea(2000,1000);
		txtEscribir.setFont(new Font("Consolas",0,22));
		
		
		
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
		        //Intento por implementar que se reduzcan las lineas cuando borras toda una linea de codigo
		     //   if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE ) {
		        //	txtRenglones.setText(""+txtRenglones.getText().substring(0, txtRenglones.getText ().length()-1));
		        	
		     //   }
		    }     
		});
		
	      
		// ------------------------------------------
		// ---- SELECCIONAR UN ARCHIVO EXISTENTE ----
		// ------------------------------------------
		
		btnAbrirArchivo = new JButton(" Abrir Archivo");
		btnAbrirArchivo.setBounds(640,10,130,70);
		btnAbrirArchivo.addActionListener(new eventMngr(this));
		ImageIcon iconobtnabrir = new ImageIcon(this.getClass().getResource("/Imagenes/abrir.png"));
		btnAbrirArchivo.setIcon(iconobtnabrir);
		btnAbrirArchivo.setIconTextGap(1);
		btnAbrirArchivo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		btnAbrirArchivo.setVerticalAlignment(javax.swing.SwingConstants.CENTER);
		btnAbrirArchivo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		btnAbrirArchivo.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
		// ------------------------------------------
		
		// ------------------------------------------
		// ------ GUARDAR UN ARCHIVO CREADO ---------
		// ------------------------------------------
		btnGuardarArchivo = new JButton("Guardar Archivo");
		btnGuardarArchivo.setIcon(iconobtnabrir);
		btnGuardarArchivo.setBounds(640, 90, 130,70);
		btnGuardarArchivo.addActionListener(new eventMngr(this));
		ImageIcon iconobtnguardar = new ImageIcon(this.getClass().getResource("/Imagenes/guardar.png"));
		btnGuardarArchivo.setIcon(iconobtnguardar);
		btnGuardarArchivo.setIconTextGap(1);
		btnGuardarArchivo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		btnGuardarArchivo.setVerticalAlignment(javax.swing.SwingConstants.CENTER);
		btnGuardarArchivo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		btnGuardarArchivo.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
		//-------------------------------------------
		// ANALIZAR
		btnAnalizar = new JButton("Analizar");
		btnAnalizar.setBounds(640, 170, 130, 70);
		btnAnalizar.addActionListener(new eventMngr(this));
		ImageIcon iconobtnanalizar = new ImageIcon(this.getClass().getResource("/Imagenes/compilar.png"));
		btnAnalizar.setIcon(iconobtnanalizar);
		btnAnalizar.setIconTextGap(1);
		btnAnalizar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		btnAnalizar.setVerticalAlignment(javax.swing.SwingConstants.CENTER);
		btnAnalizar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		btnAnalizar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
		// SALIR 
		btnSalir = new JButton("Salir");
		btnSalir.setBounds(640,330,130,70);
		btnSalir.addActionListener(new eventMngr(this));
		ImageIcon iconobtnsalir = new ImageIcon(this.getClass().getResource("/Imagenes/salir.png"));
		btnSalir.setIcon(iconobtnsalir);
		btnSalir.setIconTextGap(1);
		btnSalir.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		btnSalir.setVerticalAlignment(javax.swing.SwingConstants.CENTER);
		btnSalir.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		btnSalir.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
		//Modo Obscuro
		btnModoObscuro = new JButton("Modo Obscuro");
		btnModoObscuro.setBounds(640,250,130,70);
		btnModoObscuro.addActionListener(new eventMngr(this));
		ImageIcon iconobtnObscuro = new ImageIcon(this.getClass().getResource("/Imagenes/obs.png"));
		btnModoObscuro.setIcon(iconobtnObscuro);
		btnModoObscuro.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		btnModoObscuro.setVerticalAlignment(javax.swing.SwingConstants.CENTER);
		btnModoObscuro.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		btnModoObscuro.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
		
	
		
		
		scbContiene2 = new JScrollPane(txtResultado);
		
		scbContiene2.setWheelScrollingEnabled(true);
		scbContiene2.setSize(750, 120);
		
		tpnConsolaTabla.add("Consola",scbContiene2);
		tpnConsolaTabla.setBounds(30,425,600,150);
		txtResultado.setEditable(false);
		
		

		add(scbRenglones);
		add(scbContiene);
		add(btnAbrirArchivo);
		add(btnGuardarArchivo);
	    add(btnAnalizar);
	    add(btnModoObscuro);
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
	public void setLineas(int linita) {
		lineas = linita;
	}

	public JButton getBtnModoObscuro() {
		return btnModoObscuro;
	}

	public void setTxtEscribir(JTextArea txtEscribir) {
		this.txtEscribir = txtEscribir;
	}

	public void setTxtResultado(JTextArea txtResultado) {
		this.txtResultado = txtResultado;
	}

	public void setTxtRenglones(JTextArea txtRenglones) {
		this.txtRenglones = txtRenglones;
	}
	public boolean isObs() {
		return obs;
	}

	public void setObs(boolean obs) {
		this.obs = obs;
	}
	
	
}