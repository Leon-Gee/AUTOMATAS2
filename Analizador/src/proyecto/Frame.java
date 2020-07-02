/*
 PROYECTO ANALIZADOR LEXICO Y SINTACTICO
 INTEGRANTES:
 - Garcia Aispuro Alan Gerardo.
 - Osuna Lizarraga Rubi Guadalupe.
 - Rodelo Cardenas Graciela.
*/

package proyecto;

import java.awt.Color;

import javax.swing.JFrame;

public class Frame extends JFrame{
	Panel a = new Panel();
	public Frame() {
		this.setVisible(true);
		this.setResizable(false);
		this.setSize(800, 600);
		this.setTitle("Compilador");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		a.setSize(800, 600);
		this.add(a);
		
	}
}
