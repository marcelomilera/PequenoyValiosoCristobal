package Vista;

import Controlador.*;
import Modelo.*;

public class HiloEnemigo extends Thread {
	public int tiempoRestante = -1;
	public Juego j;
	public Enemigo enem;
	public Renderizador rend;
	private Thread thisTread = Thread.currentThread();
	private volatile Thread blinker;

	public HiloEnemigo(Juego j1, Renderizador r) {
		j = j1;
		rend = r;
		blinker = thisTread;
	}

	public void Stop() {
		this.blinker = null;
		interrupt();
	}

	public void run() {
		try {
			while (j.isJugando() && blinker == thisTread) {
				//System.out.println("Hilo Enemigo Funcionando");
				if (enem != null && enem.isActivo()) {					
					sleep(2000);  //Cada dos segundos
					if (j.getCristobal().estaVivo() == 1 && enem.isActivo()) {
						((PersonajePrincipal) j.getCristobal())
								.setVida(((PersonajePrincipal) j.getCristobal()).getVida() - 1);
						j.limpiarYActualizarVida();
					}

					if (j.getCristobal().estaVivo() == 0) {
						j.StopThemThreads();
						break;
					}
				}
			}
		} catch (InterruptedException e) {
		}
	}
}
