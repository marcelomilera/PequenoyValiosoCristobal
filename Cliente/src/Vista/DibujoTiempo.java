package Vista;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import Controlador.ControladorTiempoHilos;

public class DibujoTiempo extends Canvas {
	private ControladorTiempoHilos controlador;
	
	public DibujoTiempo(ControladorTiempoHilos controlador){		
		this.controlador = controlador;
	}
	
	public void paint(Graphics g){
		super.paint(g);
		g.setColor(Color.black);
		g.drawString("" + controlador.getContadorSeg(), 20, 20);
	}
	
	public void update(Graphics g){
		super.paint(g);		
		g.setColor(Color.black);
		g.drawString("" + controlador.getContadorSeg(), 20, 20);			
	}
}
