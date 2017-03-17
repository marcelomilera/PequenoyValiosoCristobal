package Vista;

import java.awt.EventQueue;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JLabel;

public class MiYouWin extends JFrame {

	private JFrame frame;

	public MiYouWin() {
		//initialize();
		setSize(600, 600); //Barra de título ocupa 30 pix y bordes ocupan 8 pix
		
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
		
		JLayeredPane layeredPane = new JLayeredPane();
		getContentPane().add(layeredPane, BorderLayout.CENTER);
		
		JLabel lblNewLabel = new JLabel("New label");
		ImageIcon imgThisImg = new ImageIcon("Componentes\\youWin.jpg");
		lblNewLabel.setIcon(imgThisImg);
		lblNewLabel.setBounds(0, 0, 614, 591);
		layeredPane.add(lblNewLabel);
		
		setVisible(true);
	}
}
