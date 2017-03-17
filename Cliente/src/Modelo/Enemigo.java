package Modelo;

import java.util.Scanner;

public class Enemigo extends Personaje implements Mostrable {
	private int dimensionX;
	private int dimensionY;
	private boolean muerte;
	private boolean activo = false;

	public Enemigo() {

	}

	public Enemigo(int dimX, int dimY) {
		this.setDimensionX(dimX);
		this.setDimensionY(dimY);
		this.setMuerte(false);
	}

	public String Imprimir() {
		String temporal = "";
		temporal = temporal + "Enemigo: " + getNombre() + "\n";
		return temporal;
	}

	public void setMuerte(boolean muerte) {
		this.muerte = muerte;
	}

	public int getDimensionX() {
		return dimensionX;
	}

	public void setDimensionX(int dimensionX) {
		this.dimensionX = dimensionX;
	}

	public int getDimensionY() {
		return dimensionY;
	}

	public void setDimensionY(int dimensionY) {
		this.dimensionY = dimensionY;
	}

	public int estaVivo() {
		if (super.getVida() > 0) { // Esta vivo
			return 1;
		} else
			return 0;
	}

	public boolean isActivo() {
		return activo;
	}

	public void setActivo(boolean activo) {
		this.activo = activo;
	}
}
