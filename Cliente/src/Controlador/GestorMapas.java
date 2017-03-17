package Controlador;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.ArrayList;
import Modelo.*;

public class GestorMapas {
	private List<String> nombresMapas;

	private List<Objeto> listaObjetos = null;

	public GestorMapas() {
		nombresMapas = new ArrayList<String>();
		for (int i = 1; i <= 3; i++) {
			String nombre = "Mapas\\mapa";
			nombre = nombre + i;
			nombre = nombre + ".txt";
			nombresMapas.add(nombre);
			nombre = "";
		}
	}

	public void CargarMapa(Mapa mapa, Personaje cristobal, Personaje hermana) { // Constructor
																				// de
																				// mapa
		char celda;
		File archivo = null;
		FileReader fr = null;
		BufferedReader br = null;
		int nivel = mapa.getNivel();
		setListaObjetos(new ArrayList<Objeto>());
		try {
			archivo = new File(nombresMapas.get(nivel));
			fr = new FileReader(archivo);
			br = new BufferedReader(fr);
			// Lectura del archivo de texto
			String linea;
			int fila = 0;
			while ((linea = br.readLine()) != null) {
				for (int index = 0; index < linea.length(); index++) {
					mapa.getMapa()[fila][index].setCelda(linea.charAt(index));
					mapa.getMapa()[fila][index].setTipo(linea.charAt(index));
					celda = mapa.getMapa()[fila][index].getCelda();
					if (celda == 'A') { // Posicion inicial de cristobal
						cristobal.setPosX(index); // guardo en cristobal sus
													// coordenadas iniciales
						cristobal.setPosY(fila);
						mapa.getMapa()[fila][index].setCelda('S');
						mapa.getMapa()[fila][index].setTipo('S');
					}
					if (celda == 'B') {// le hermana
						hermana.setPosX(index);
						hermana.setPosY(fila);
						mapa.getMapa()[fila][index].setCelda('N');
						mapa.getMapa()[fila][index].setTipo('N');
					}
					if (celda == 'v') {
						Objeto vaca = new Objeto("vaca", index, fila);
						getListaObjetos().add(vaca);
					}
					if (celda == 'p') {
						Objeto pasto = new Objeto("pasto", index, fila);
						getListaObjetos().add(pasto);
					}
					if (celda == 'g') {
						Objeto piedra = new Objeto("piedra", index, fila);
						getListaObjetos().add(piedra);
					}
					if (celda == 'n' || celda == 'H') {
						Objeto hueco = new Objeto("hueco", index, fila);
						getListaObjetos().add(hueco);
					}
					if (celda == 'h') {
						Objeto pared = new Objeto("pared", index, fila);
						getListaObjetos().add(pared);
					}
				}
				fila++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != fr) {
					fr.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}

	public void CargarObjetos(Mapa mapa,boolean Nuevo) { // Constructor de mapa
		char celda;
		File archivo = null;
		FileReader fr = null;
		BufferedReader br = null;
		int nivel = mapa.getNivel();
		setListaObjetos(new ArrayList<Objeto>());
		try {
			archivo = new File(nombresMapas.get(nivel));
			fr = new FileReader(archivo);
			br = new BufferedReader(fr);
			// Lectura del archivo de texto
			String linea;
			int fila = 0;
			while ((linea = br.readLine()) != null) {
				for (int index = 0; index < linea.length(); index++) {
					if(Nuevo){
						mapa.getMapa()[fila][index].setCelda(linea.charAt(index));
						mapa.getMapa()[fila][index].setTipo(linea.charAt(index));
					}					
					celda = mapa.getMapa()[fila][index].getCelda();
					if (celda == 'v') {
						Objeto vaca = new Objeto("vaca", index, fila);
						getListaObjetos().add(vaca);
					}
					if (celda == 'p') {
						Objeto pasto = new Objeto("pasto", index, fila);
						getListaObjetos().add(pasto);
					}
					if (celda == 'g') {
						Objeto piedra = new Objeto("piedra", index, fila);
						getListaObjetos().add(piedra);
					}
					if (celda == 'n' || celda == 'H') {
						Objeto hueco = new Objeto("hueco", index, fila);
						getListaObjetos().add(hueco);
					}
					if (celda == 'h') {
						Objeto pared = new Objeto("pared", index, fila);
						getListaObjetos().add(pared);
					}
				}
				fila++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != fr) {
					fr.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}

	public void removerEnemigo(Mapa map, Enemigo enemigo) {
		for (int i = enemigo.getPosX(); i < enemigo.getPosX() + enemigo.getDimensionY(); i++)
			for (int j = enemigo.getPosY(); j < enemigo.getPosY() + enemigo.getDimensionX(); j++) {
				map.getMapa()[i][j].setTipo('N');
				map.getMapa()[i][j].setCelda('N');
			}
	}

	public List<Objeto> getListaObjetos() {
		return listaObjetos;
	}

	public void setListaObjetos(List<Objeto> listaObjetos) {
		this.listaObjetos = listaObjetos;
	}
}