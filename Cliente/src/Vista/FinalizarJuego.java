package Vista;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import Controlador.ControladorTiempoHilos;
import Network.MiPaquete;

import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class FinalizarJuego extends JFrame {

	private JPanel contentPane;
	public ControladorTiempoHilos ct=null; 

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FinalizarJuego frame = new FinalizarJuego();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public FinalizarJuego() {
		setTitle("Tiempo Restante para cerrar el Juego");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 410, 161);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JButton btnSalirDelJuego = new JButton("Salir del Juego");
		btnSalirDelJuego.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//Program.game.Rend.CerrarVentana();
				//System.exit(0);
				MiPaquete mp = new MiPaquete();
				mp.tipoEnvio=4;
				mp.cerrarFinalizarJuego = true;
				Juego.net.client.sendUDP(mp);
			}
		});
		btnSalirDelJuego.setBounds(30, 85, 128, 23);
		contentPane.add(btnSalirDelJuego);
		
		JButton btnContinuarJuego = new JButton("Continuar Juego");
		btnContinuarJuego.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MiPaquete mp = new MiPaquete(); 
				mp.tipoEnvio=4;
				mp.continuarJugando = true;
				Juego.net.client.sendUDP(mp);
			}
		});
		btnContinuarJuego.setBounds(218, 85, 154, 23);
		contentPane.add(btnContinuarJuego);
		
		JLabel lblTiempoRestante = new JLabel("Tiempo restante:");
		lblTiempoRestante.setBounds(80, 38, 99, 14);
		contentPane.add(lblTiempoRestante);
		
		JLabel lblContadorRestante = new JLabel("");
		lblContadorRestante.setBounds(189, 38, 46, 14);
		/*Simulacion del hilo*/
		ct = new ControladorTiempoHilos(10, null);
		ct.setlblContadorTiempo(lblContadorRestante);
		contentPane.add(lblContadorRestante);
		ct.start();
		setLocationRelativeTo(null);
	}
}
