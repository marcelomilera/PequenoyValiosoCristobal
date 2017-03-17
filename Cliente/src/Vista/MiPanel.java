package Vista;

import java.awt.Graphics;
import java.awt.Image;


import javax.swing.JPanel;

public class MiPanel extends JPanel {
	private Image imgFondo;
	
	public MiPanel() {
		super();		
		this.setOpaque(false);

	}
	
	public void ponerFondo(Image img){
		this.imgFondo = img;
	}
	
	public void paint(Graphics g){
		g.drawImage(imgFondo, 0, 4, null);
		super.paint(g);
	}
	
	
}