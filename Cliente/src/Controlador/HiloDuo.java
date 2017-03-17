package Controlador;

import Vista.Juego;

public class HiloDuo extends Thread {

	private Juego juego;
	
	public HiloDuo(Juego juego) {
		this.juego = juego;		
	}
	
	public void run(){
		juego.AccionDuo(juego.getMapaActual());
	}

}