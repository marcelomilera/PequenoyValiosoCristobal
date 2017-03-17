package Controlador;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import Vista.DibujoTiempo;
import Vista.MiGameOver;
import Vista.Renderizador;

public class ControladorTiempoHilos extends Thread{
		private volatile Thread blinker;
		private Thread thisTread =  Thread.currentThread();
		private int contadorSeg;
		private DibujoTiempo canvas;
		private Renderizador rend;
		private JLabel lblContadorTiempo = new JLabel();
		
		public ControladorTiempoHilos(int duracionSegMapa, Renderizador rend){
			if(rend!=null){
				contadorSeg = duracionSegMapa;
				blinker = thisTread;
				this.setCanvas(canvas);
				this.rend = rend;
			}else{
				contadorSeg = duracionSegMapa+1;
				blinker = thisTread;
				this.setCanvas(canvas);
				this.rend = rend;
			}
		}
		
		public void Stop(){
			this.blinker = null;
			interrupt();
		}
		
		public void run() {						
			while (contadorSeg > 0 && blinker == thisTread){
			//for(int i=0;i<1;i++){
				try {
					sleep(1*1000);
					//System.out.println(Integer.toString(contadorSeg));
					contadorSeg--;
					lblContadorTiempo.setText(Integer.toString(contadorSeg));
					//ContadorTiempo.setText(Integer.toString(contadorSeg));
					if(getContadorSeg() == 0){
						MiGameOver perdisteVentana = new MiGameOver();
						if(rend != null)	
							rend.dispose();
						
						//int selectedOption = JOptionPane.showMessageDialog(null, "Te quedaste sin tiempo");
						String[] options = {"Aceptar"};
						JPanel panel = new JPanel();
						JLabel lbl = new JLabel("Te quedaste sin tiempo");
						panel.add(lbl);
						int selectedOption = JOptionPane.showOptionDialog(null, panel, "FIN DEL JUEGO", JOptionPane.NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options , options[0]);
						if(selectedOption == 0)
						{
							System.exit(0);
						}
					}
					if(rend != null)
						getCanvas().repaint();										
				}
				catch( InterruptedException e ) {
				}
			}
			
		}

		public int getContadorSeg() {
			return contadorSeg;
		}

		public void setContadorSeg(int contadorSeg) {
			this.contadorSeg = contadorSeg;
		}

		public DibujoTiempo getCanvas() {
			return canvas;
		}

		public void setCanvas(DibujoTiempo canvas) {
			this.canvas = canvas;
		}

		public JLabel setlblContadorTiempo() {
			return lblContadorTiempo;
		}

		public void setlblContadorTiempo(JLabel contadorTiempo) {
			lblContadorTiempo = contadorTiempo;
		}
}
