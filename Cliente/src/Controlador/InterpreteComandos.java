package Controlador;

import Modelo.*;
import java.util.Scanner;

public class InterpreteComandos {

	public InterpreteComandos() {
	}

	public void revisarTrigger(Mapa mapa, int x, int y) {
		if (mapa.getMapa()[y][x].getCelda() == 'T') {
			for (int i = 0; i < mapa.getNumFil(); ++i)
				if (mapa.getMapa()[i][x].getCelda() == 'T') {
					mapa.getMapa()[i][x].setCelda('N');
					mapa.getMapa()[i][x].setTipo('N');
				}
			mapa.setEnemigo(true);
		}		
	}

	public void InterpretarAccion(char key, Personaje cristobal, Personaje hermana, Mapa mapa, int dir[]) {
		int x, y, xH, yH, tipoCelda, numFil, numCol;
		x = cristobal.getPosX();// posicion de cristobal
		y = cristobal.getPosY();
		xH = hermana.getPosX(); // POSICION DE HERMANA
		yH = hermana.getPosY();

		numFil = mapa.getNumFil();// limites del mapa
		numCol = mapa.getNumCol();
		dir[0]=0;
		dir[1]=0;
		switch (key) {
		case 'w': // Mover arriba
		case 'W':
			if (y >= 1 && y < numFil) {
				tipoCelda = mapa.getMapa()[y - 1][x].getTipo();
				if (tipoCelda >= 1 && tipoCelda <= 4) {
					cristobal.setPosYAnterior(y);
					--y;
					dir[0] = 1;
					cristobal.setPosY(y);
				}
			}			
			break;
		case 's': // Mover abajo
		case 'S':
			if (y >= 0 && y < numFil - 1) {
				tipoCelda = mapa.getMapa()[y + 1][x].getTipo();
				if (tipoCelda >= 1 && tipoCelda <= 4) {
					cristobal.setPosYAnterior(y);
					y++;
					dir[0] = 3;
					cristobal.setPosY(y);
				}
			}			
			break;
		case 'd': // Mover derecha
		case 'D':
			if (x >= 0 && x < numCol - 1) {
				tipoCelda = mapa.getMapa()[y][x + 1].getTipo();
				if (tipoCelda >= 1 && tipoCelda <= 4) {
					cristobal.setPosXAnterior(x);
					x++;
					dir[0] = 2;
					cristobal.setPosX(x);
				}
			}			
			break;
		case 'a':// Mover izquierda
		case 'A':
			if (x >= 1 && x < numCol) {
				tipoCelda = mapa.getMapa()[y][x - 1].getTipo();
				if (tipoCelda >= 1 && tipoCelda <= 4) {
					cristobal.setPosXAnterior(x);
					x--;
					dir[0] = 4;
					cristobal.setPosX(x);
				}
			}			
			break;
		// MOVER HERMANA
		case 'i': // Mover arriba
		case 'I':
			if (mapa.isEnemigo() == true) {
				((PersonajePrincipal)hermana).reducir_vida_movimiento();				
				break;
			}
			if (yH >= 1 && yH < numFil) {
				tipoCelda = mapa.getMapa()[yH - 1][xH].getTipo();
				if (tipoCelda >= 1 && tipoCelda <= 4) {
					hermana.setPosYAnterior(yH);
					--yH;
					dir[1] = 1;
					hermana.setPosY(yH);
				}
			}
			revisarTrigger(mapa, xH, yH);			
			break;
		case 'k': // Mover abajo
		case 'K':
			if (mapa.isEnemigo() == true) {
				((PersonajePrincipal)hermana).reducir_vida_movimiento();				
				break;
			}
			if (yH >= 0 && yH < numFil - 1) {
				tipoCelda = mapa.getMapa()[yH + 1][xH].getTipo();
				if (tipoCelda >= 1 && tipoCelda <= 4) {
					hermana.setPosYAnterior(yH);
					yH++;
					dir[1] = 3;
					hermana.setPosY(yH);
				}
			}
			revisarTrigger(mapa, xH, yH);			
			break;
		case 'l': // Mover derecha
		case 'L':
			if (mapa.isEnemigo() == true) {
				((PersonajePrincipal)hermana).reducir_vida_movimiento();			
				break;
			}
			if (xH >= 0 && xH < numCol - 1) {
				tipoCelda = mapa.getMapa()[yH][xH + 1].getTipo();
				if (tipoCelda >= 1 && tipoCelda <= 4) {
					hermana.setPosXAnterior(xH);
					xH++;
					dir[1] = 2;
					hermana.setPosX(xH);
				}
			}
			revisarTrigger(mapa, xH, yH);
			break;
		case 'j':// Mover izquierda
		case 'J':			
			if (mapa.isEnemigo() == true) {
				((PersonajePrincipal)hermana).reducir_vida_movimiento();				
				break;
			}
			if (xH >= 1 && xH < numCol) {
				tipoCelda = mapa.getMapa()[yH][xH - 1].getTipo();
				if (tipoCelda >= 1 && tipoCelda <= 4) {
					hermana.setPosXAnterior(xH);
					xH--;
					dir[1] = 4;
					hermana.setPosX(xH);
				}
			}
			revisarTrigger(mapa, xH, yH);
			break;
		}		
	}
}