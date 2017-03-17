package Modelo;

import java.io.*;

public class Mapa implements Mostrable {
	private int numFil;
	private int numCol;
	private Celda[][] mapa;
	private int nivel;
	private boolean enemigo;

	public boolean isEnemigo() {
		return enemigo;
	}

	public void setEnemigo(boolean enemigo) {
		this.enemigo = enemigo;
	}

	public int getNumCol() {
		return numCol;
	}

	public void setNumCol(int numCol) {
		this.numCol = numCol;
	}

	public int getNumFil() {
		return numFil;
	}

	public void setNumFil(int numFil) {
		this.numFil = numFil;
	}

	public int getNivel() {
		return nivel;
	}

	public void setNivel(int nivel) {
		this.nivel = nivel;
	}

	public Celda[][] getMapa() {
		return mapa;
	}

	public void setMapa(Celda[][] mapa) {
		this.mapa = mapa;
	}

	public Celda ObtenerCelda(int x, int y) {
		return getMapa()[x][y];
	}

	public Mapa() { // Crea el mapa con todas las celdas en 0
		enemigo = false;
		numFil = 12;
		numCol = 16;
		setMapa(new Celda[numFil][numCol]);
		for (int i = 0; i < numFil; i++) {
			for (int j = 0; j < numCol; j++) {
				getMapa()[i][j] = new Celda();
				getMapa()[i][j].setCelda('0');
			}
		}
		nivel = 0;
	}

	public void EliminaRata() {
		int i, j;
		for (i = 0; i < numFil; i++) {
			for (j = 0; j < numCol; j++)
				if (this.isEnemigo()) {
					this.mapa[i][j].setTipo('N');
					this.mapa[i][j].setCelda('N');
				}
		}
	}

	public String Imprimir() {
		String temporal = "";
		if (nivel > 0)
			temporal = "Nivel: " + nivel;
		else
			temporal = "Nivel: Tutorial";

		return temporal;
	}
}