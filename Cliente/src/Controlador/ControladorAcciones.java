package Controlador;

import javax.swing.JOptionPane;

import Modelo.PersonajePrincipal;

//import java.util.Scanner;
import Modelo.PersonajePrincipal;

import Vista.Juego;
import Vista.MiGameOver;

public class ControladorAcciones extends Thread {
	// private PersonajePrincipal personaje;
	private Juego miJuego;
	private volatile Thread blinker;
	private Thread thisTread = Thread.currentThread();
	private int duracionSegAccion;

	public ControladorAcciones(Juego miJuego, int duracionSegAccion) {
		this.miJuego = miJuego;
		this.duracionSegAccion=duracionSegAccion;
		blinker = thisTread;
	}

	public void Stop() {
		this.blinker = null;
		interrupt();
	}

	public void run() {
		Tiempo t = new Tiempo();
		try {
			while (miJuego.getCristobal().getVida() > 0 && blinker == thisTread) {
				//System.out.println("Hilo Controlador de Funciones Funcionando");
				sleep(duracionSegAccion * 1000); // la cantidad de segundos para
													// la accion(N*1000) 1 seg =
													// 1000 ms
				(miJuego.getCristobal()).setVida((miJuego.getCristobal()).getVida() - 2);
				miJuego.limpiarYActualizarVida();
				if (miJuego.getCristobal().estaVivo() == 0) {								
					miJuego.StopThemThreads();															
					miJuego.setCasillaAccionDuo(false); // Se da la condicion de
														// que ya no esta en
														// casilla duo y por lo
														// tanto no debe entrar
														// una vez mas
														// En caso no se ponga
														// esto imprimira el
														// mensaje de game over
														// 2 veces
				}
			}
		} catch (InterruptedException e) {
			 miJuego.limpiarYActualizarVida();
		}

	}
	
}
