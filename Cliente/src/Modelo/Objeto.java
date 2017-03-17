package Modelo;

public class Objeto {
	private String nombre;
	private int posX;
	private int posY;

	public Objeto() {

	}

	public Objeto(String nombre, int posX, int posY) {
		this.setNombre(nombre);
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

	public void Imprimir() {
		System.out.println("Nombre: " + getNombre() + " PosX: " + getPosX() + " PosY: " + getPosY());
	}

}

class ObjObstaculo extends Objeto {

	public ObjObstaculo() {

	}

}

class ObjDeApoyo extends Objeto {

	public ObjDeApoyo() {

	}
}