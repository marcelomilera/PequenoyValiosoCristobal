package Modelo;

public class Celda {
	private char celda;
	private int tipo;

	public char getCelda() {
		return celda;
	}

	public void setCelda(char celda) {
		this.celda = celda;
	}

	public int getTipo() {
		return tipo;
	}

	public void setTipo(int tipo) {
		this.tipo = tipo;
	}
	
	public void setTipo(char caracterCelda) {
		switch(caracterCelda){
		case 'D':  //Terreno duo
			tipo = 1;
			break;
		case 'C':  //Terreno acción
			tipo = 2;
			break;
		case 'S':  //Terreno A
			tipo = 3;
			break;
		case 'N':  //Terreno B
			tipo = 4;
			break;
		case ' ':  //Terreno vacio
			tipo = 5;
			break;
		case 'g': //Obstaculo A: Rocas gigantes
			tipo = 6;
			break;
		case 'n': //Obstaculo B:  Un barranco
			tipo = 7;
			break;
		case 'p': //Paso alto(p) - Terreno Impasable
			tipo = 8;
			break;
		case 'h': //Es un surco o hueco.
			tipo = 9;
			break;
		case 'H': //Hueco o surco grande.(H)
			tipo = 10;
			break;
		case 'T':
			tipo=1;//Trigger
			break;
		}		
	}
	
}
