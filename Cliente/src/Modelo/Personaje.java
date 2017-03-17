package Modelo;

public abstract class Personaje implements Mostrable {
	private String nombre;
	private int posX;
	private int posY;
	private int posXAnterior;
	private int posYAnterior;
	private int vida;

	public Personaje(String nombre, int posx, int posy, int posxAnterior, int posyAnterior) {
		this.nombre = nombre;
		this.posX = posx;
		this.posY = posy;
		this.posXAnterior = posxAnterior;
		this.posYAnterior = posyAnterior;
	}

	public Personaje() {
		this.nombre = "nuevo";
		this.posX = 0;
		this.posY = 0;
		this.posXAnterior = 0;
		this.posYAnterior = 0;
	}

	public Personaje(int posX, int posY) {
		this.setPosX(posX);
		this.setPosY(posY);
	}

	public int getPosX() {
		return posX;
	}

	public void setPosX(int posX) {
		this.posX = posX;
	}

	public int getPosY() {
		return posY;
	}

	public void setPosY(int posY) {
		this.posY = posY;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public abstract int estaVivo();

	public int getVida() {
		return vida;
	}

	public void setVida(int vida) {
		this.vida = vida;
	}

	public int getPosXAnterior() {
		return posXAnterior;
	}

	public void setPosXAnterior(int posXAnterior) {
		this.posXAnterior = posXAnterior;
	}

	public int getPosYAnterior() {
		return posYAnterior;
	}

	public void setPosYAnterior(int posYAnterior) {
		this.posYAnterior = posYAnterior;
	}
}
