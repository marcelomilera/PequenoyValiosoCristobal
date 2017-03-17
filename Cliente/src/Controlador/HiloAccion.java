package Controlador;

import Vista.Juego;

public class HiloAccion extends Thread {

	private Juego juego;
	
	public HiloAccion(Juego juego) {
		this.juego = juego;		
	}
	
	public void run(){
		juego.AccionEspecial(juego.getMapaActual());
		
	}

}
