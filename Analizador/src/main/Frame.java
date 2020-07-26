/*
 PROYECTO ANALIZADOR LEXICO Y SINTACTICO
 INTEGRANTES:
 - Garcia Aispuro Alan Gerardo.
 - Meza Leon Oscar Oswaldo.
 - Osuna Lizarraga Rubi Guadalupe.
 - Rodelo Cardenas Graciela.
*/

package main;

import java.awt.*;
import java.io.File;

import javax.swing.*;
 

import Aplicacion.Panel;

public class Frame extends JFrame{
	
	Panel a = new Panel();
	
	public Frame() {
		ImageIcon ImageIcon = new ImageIcon(this.getClass().getResource("/Imagenes/logo.png"));
	    Image logo = ImageIcon.getImage();
	    this.setIconImage(logo);
		this.setVisible(true);
		this.setResizable(false);
		this.setSize(800, 650);
		this.setTitle("Compilador");
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		a.setSize(800, 650);
		this.add(a);
		
	}
	public static void main(String[] args) {
		new Frame();
	}
}
