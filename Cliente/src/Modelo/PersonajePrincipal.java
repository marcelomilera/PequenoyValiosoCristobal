package Modelo;

import java.util.Scanner;

import Vista.MiGameOver;

public class PersonajePrincipal extends Personaje implements Mostrable {

	public PersonajePrincipal(String nombre, int posx, int posy, int posxAnterior, int posyAnterior, int vida) {
		super.setVida(vida);
		setNombre(nombre);
		setPosX(posx);
		setPosY(posy);
		setPosXAnterior(posxAnterior);
		setPosYAnterior(posyAnterior);
	}

	public PersonajePrincipal() {
		super.setVida(100);
	}

	public void reducir_vida_movimiento() {
		setVida(getVida() - 1);
	}

	public String Imprimir() {
		String temporal = "";
		temporal = temporal + "Vida: " + this.getVida();
		temporal = temporal + "Personaje: " + getNombre();
		return temporal;
	}

	public int estaVivo() {
		if (super.getVida() > 0) { // Esta vivo
			return 1;
		} else {
			MiGameOver gameOver = new MiGameOver();
			return 0;
		}
	}
}