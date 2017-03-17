package Vista;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import Controlador.ControladorAcciones;
import Controlador.GestorMapas;
import Controlador.HiloAccion;
import Controlador.HiloDuo;
import Controlador.InterpreteComandos;
import Controlador.Tiempo;
import Modelo.Enemigo;
import Modelo.Mapa;
import Modelo.Objeto;
import Modelo.Personaje;
import Modelo.PersonajePrincipal;
import Network.MiPaquete;
import Network.Network;

public class Juego implements KeyListener {

	public static Network net;
	public boolean multijugador;
	private Personaje cristobal;
	private Personaje hermana;
	private GestorMapas gestor;
	private Mapa mapaActual;
	private InterpreteComandos Interprete;
	public Renderizador Rend;
	private boolean jugando = true;
	private boolean ReiniciarHiloEnemigo;
	private boolean CasillaAccionDuo = false; // Verifica si los cuyes estan en
												// las casillas de accion
												// conjunta
	private boolean CasillaCristobal = false; // Verifica si Cristobal ingreso a
												// su accion Individual
	private boolean CasillaHermana = false;// Verifica Si Cristina ingreso a su
											// accion Individual
	private String CadenaIngresada = ""; // Almacenara la cadena de la accion
											// duo para validarla

	private List<Personaje> listaEnemigos;
	private List<Objeto> listaObjetos = null;
	private MiGameOver gameOver;
	private int dir[] = new int[2];
	/* # */
	public ControladorAcciones controladorAccionesHilo;
	public HiloAccion hiloAccion;
	public HiloDuo hiloDuo;
	public HiloEnemigo hiloEnemAct;
	public boolean pausaKeyListener = false;
	Personaje enemigo;
	Enemigo enemigo2;
	private int aux[] = new int[2];
	private int vivo;
	private boolean jugandoCristobal = false;
	private boolean jugandoCristina = false;
	private int cont=0;
	private String subClave="";
	public boolean cheatUsed = false;
	
	public boolean verificarComandoCristobal(char c) {
		if (c == 'W' || c == 'A' || c == 'S' || c == 'D' || c == 'w' || c == 'a' || c == 's' || c == 'd' || c == 'Q'
				|| c == 'q' || c == 'E' || c == 'e')
			return true;
		return false;
	}

	public void recibirChooser(boolean temp) {
		if (jugandoCristobal == false) {
			jugandoCristobal = true;
			jugandoCristina = false;
		} else {
			jugandoCristina = true;
			jugandoCristobal = false;
		}
	}

	public void keyTyped(KeyEvent e) {
	}

	public void StopThemThreads() {

		if (controladorAccionesHilo != null)
			controladorAccionesHilo.Stop();
		if (hiloEnemAct != null)
			hiloEnemAct.Stop();
		Rend.controlador.Stop();
		Rend.dispose();

	}

	public void limpiarYActualizarVida() {

		if (cristobal.getVida() > 0) {
			String txtVida;
			txtVida = Integer.toString(cristobal.getVida());
			// Primero la volvemos vacia la vida del jugador y luego la
			// modificaremos con un repaint

			Rend.getLblVidaJugador().setForeground(Color.lightGray);
			Rend.getLblVidaJugador().setLocation(-1, -1);
			JLabel lblVidJugador = new JLabel(txtVida);
			Rend.setLblVidaJugador(lblVidJugador);
			Rend.getLblVidaJugador().setBounds(62, 122, 142, 14);
			Rend.getPanelMenu().add(lblVidJugador);

			Rend.repaint();
		} else {
			StopThemThreads();
		}

	}

	public void keyPressed(KeyEvent e) {
		if(Program.conectado){
			aux[0] = 0;
			aux[1] = 0;
			if (!pausaKeyListener) {
				char c = e.getKeyChar();
				if (multijugador) {
					if (c == '\n' && (jugandoCristina || jugandoCristobal)) {
						MiPaquete mp = new MiPaquete();
						mp.caracter = c;
						net.client.sendUDP(mp);
					} else if (jugandoCristobal && verificarComandoCristobal(c)) {
						MiPaquete mp = new MiPaquete();
						mp.caracter = c;
						/* Program. */net.client.sendUDP(mp);
					} else if (jugandoCristina && !verificarComandoCristobal(c)) {
						MiPaquete mp = new MiPaquete();
						mp.caracter = c;
						/* Program. */net.client.sendUDP(mp);
					}					
				} else
					recibirKeyPressed(c);
			} else
				Rend.pintar(cristobal.getPosX(), cristobal.getPosY(), hermana.getPosX(), hermana.getPosY(),
						mapaActual.getNivel(), getListaEnemigos(), aux);
		}
	}

	public void recibirKeyPressed(char c) {
		aux[0] = 0;
		aux[1] = 0;
		String temp = "" + c;
		if (Rend.getFlgHistoria() == 1) {
			Rend.setFlgHistoria(0);
			Rend.CambiarFondo(mapaActual.getNivel());
			Rend.pintar(cristobal.getPosX(), cristobal.getPosY(), hermana.getPosX(), hermana.getPosY(),
					mapaActual.getNivel(), getListaEnemigos(), aux);
			Rend.pintarObjetos(listaObjetos, getMapaActual().getNivel());
		} else {
			if (isCasillaAccionDuo()) {
				if (temp.compareTo("\n") == 0) {
					if (VerificarAccionDuo()) {
						cont = 0;
						Rend.getTxtMensaje().setText("");
						hiloDuo = new HiloDuo(this);
						hiloDuo.start();
						controladorAccionesHilo.Stop();
						if (hiloEnemAct != null)
							hiloEnemAct.Stop();
						ReiniciarHiloEnemigo = true;
						setCasillaAccionDuo(false);
					} else {
						((PersonajePrincipal) cristobal).setVida(((PersonajePrincipal) cristobal).getVida() - 2);
						limpiarYActualizarVida();
						if (cristobal.estaVivo() == 0) {
							StopThemThreads();
						}
					}
					setCadenaIngresada("");
				} else{
					setCadenaIngresada(getCadenaIngresada() + temp);
					mostrarSubCadena();
				}
			} else if (isCasillaCristobal()) {
				if (temp.compareTo("\n") == 0) {
					if (MatarEnemigo() == 1) {
						Rend.getTxtMensaje().setText("");
						controladorAccionesHilo.Stop();
						if (hiloEnemAct != null)
							hiloEnemAct.Stop();
						ReiniciarHiloEnemigo = true;
						setCasillaCristobal(false);
					} else {
						((PersonajePrincipal) cristobal).setVida(((PersonajePrincipal) cristobal).getVida() - 2);
						limpiarYActualizarVida();
						if (cristobal.estaVivo() == 0) {
							StopThemThreads();
						}
					}
					setCadenaIngresada("");
				} else
					setCadenaIngresada(getCadenaIngresada() + temp);
			} else if (isCasillaHermana()) {
				if (temp.compareTo("\n") == 0) {
					if (AccionHermana() == 1) {
						Rend.getTxtMensaje().setText("");
						controladorAccionesHilo.Stop();
						if (hiloEnemAct != null)
							hiloEnemAct.Stop();
						ReiniciarHiloEnemigo = true;
						setCasillaHermana(false);
					} else {
						((PersonajePrincipal) cristobal).setVida(((PersonajePrincipal) cristobal).getVida() - 2);
						limpiarYActualizarVida();
						if (cristobal.estaVivo() == 0) {
							StopThemThreads();
						}
					}
					setCadenaIngresada("");
				} else
					setCadenaIngresada(getCadenaIngresada() + temp);
			} else
				this.Jugar(temp);
		}
	}

	public void keyReleased(KeyEvent e) {
	}

	public Juego(Personaje Cristobal, Personaje Hermana, Mapa MapaActual) {

		setCristobal(Cristobal);
		setHermana(Hermana);
		gestor = new GestorMapas();
		setMapaActual(MapaActual);
		Interprete = new InterpreteComandos();
		Rend = new Renderizador(this);
		gestor.CargarObjetos(mapaActual, false);
		CargarEnemigos(mapaActual);
		listaObjetos = gestor.getListaObjetos();
		Rend.addWindowListener(new MiObservadorVentana(Rend));
		Rend.addKeyListener(this);
		Rend.mostrarHistoria(mapaActual.getNivel());

		JLabel lblNombreJugador = new JLabel(Cristobal.getNombre());
		Rend.setLblNombreJugador(lblNombreJugador);
		Rend.getLblNombreJugador().setBounds(64, 95, 140, 14);

		Rend.getPanelMenu().add(lblNombreJugador);

		String txtVida;
		txtVida = Integer.toString(cristobal.getVida());
		JLabel lblVidJugador = new JLabel(txtVida);
		Rend.setLblVidaJugador(lblVidJugador);
		Rend.getLblVidaJugador().setBounds(62, 122, 142, 14);
		Rend.getPanelMenu().add(lblVidJugador);

		Rend.getPanelMenu().add(lblVidJugador);
		dir[0] = 0;
		dir[1] = 0;

		// hiloEnemAct = new HiloEnemigo(this, this.Rend);
		// hiloEnemAct.start();

	}

	public Juego(String nombreJugador, boolean multi, String ip) {
		this.multijugador = multi;
		
		setCristobal(new PersonajePrincipal());
		setHermana(new PersonajePrincipal());
		gestor = new GestorMapas();
		setMapaActual(new Mapa());
		gestor.CargarMapa(getMapaActual(), cristobal, hermana);

		listaObjetos = gestor.getListaObjetos();
		Interprete = new InterpreteComandos();
		Rend = new Renderizador(this);
		Rend.mostrarHistoria(mapaActual.getNivel());

		Rend.addWindowListener(new MiObservadorVentana(Rend));
		Rend.addKeyListener(this);

		cristobal.setNombre(nombreJugador);
		JLabel lblNombreJugador = new JLabel(nombreJugador);
		Rend.setLblNombreJugador(lblNombreJugador);
		Rend.getLblNombreJugador().setBounds(64, 95, 140, 14);
		Rend.getPanelMenu().add(lblNombreJugador);

		String txtVida;
		txtVida = Integer.toString(cristobal.getVida());
		JLabel lblVidJugador = new JLabel(txtVida);
		Rend.setLblVidaJugador(lblVidJugador);
		Rend.getLblVidaJugador().setBounds(62, 122, 142, 14);
		Rend.getPanelMenu().add(lblVidJugador);

		// hiloEnemAct = new HiloEnemigo(this, this.Rend);
		// hiloEnemAct.start();	
		if(!multi){
			jugandoCristina=true;
			jugandoCristobal=true;			
			Program.conectado=true;
			
		}
		else{
			net = new Network();
			net.ip=ip;
			net.connect();
		}
	}

	public void CrearEnemigos(JsonElement elemento) {

		JsonArray arreglo = elemento.getAsJsonArray();
		for (int i = 0; i < arreglo.size(); i++) {
			enemigo = new Enemigo();
			JsonObject obj = arreglo.get(i).getAsJsonObject();
			((Enemigo) enemigo).setNombre(obj.get("Nombre").getAsString());
			((Enemigo) enemigo).setPosX(obj.get("PosX").getAsInt());
			((Enemigo) enemigo).setPosY(obj.get("PosY").getAsInt());
			((Enemigo) enemigo).setDimensionX(obj.get("DimensionX").getAsInt());
			((Enemigo) enemigo).setDimensionY(obj.get("DimensionY").getAsInt());
			getListaEnemigos().add(enemigo);
		}
	}

	public void CargarEnemigos(Mapa mapa_actual) {
		int nivel = mapa_actual.getNivel() + 1;
		String nombArch = "Informacion de los enemigos\\enemigos" + nivel + ".json";
		setListaEnemigos(new ArrayList<Personaje>());

		JsonParser parser = new JsonParser();

		try {
			FileReader fr = new FileReader(nombArch);
			JsonElement datos = parser.parse(fr);
			CrearEnemigos(datos);
		} catch (FileNotFoundException ex) {

		}
	}

	public int MatarEnemigo() {

		int nivel = getMapaActual().getNivel();
		int posCX = cristobal.getPosX(), posCY = cristobal.getPosY();
		int posHX = hermana.getPosX(), posHY = hermana.getPosY();
		char tipoH = getMapaActual().getMapa()[posHY][posHX].getCelda(),
				tipoC = getMapaActual().getMapa()[posCY][posCX].getCelda();
		boolean celdaIni = false;
		int posEnemigoX = 0, posEnemigoY = 0;
		char tipo;

		if (tipoH == 'N') {
			while (celdaIni == false) {
				for (int j = posHY; j >= 0; j--) {
					tipo = getMapaActual().getMapa()[j][posHX + 1].getCelda();
					if (tipo != 'E') {
						celdaIni = true;
						posEnemigoX = posHX + 1;
						posEnemigoY = j + 1;
						break;
					}
				}
				if (celdaIni == true)
					break;
				for (int j = posHY; j <= 11; j++) {
					tipo = getMapaActual().getMapa()[j][posHX + 1].getCelda();
					if (tipo != 'E') {
						celdaIni = true;
						posEnemigoX = posHX + 1;
						posEnemigoY = j - 1;
						break;
					}
				}

			}
		}

		// enemigo = getListaEnemigos().get(0);
		// getMapaActual().setEnemigo(true);

		for (int i = 0; i < getListaEnemigos().size(); i++) {
			enemigo = getListaEnemigos().get(i);
			if ((enemigo.getPosX() == posEnemigoY) && (enemigo.getPosY() == posEnemigoX)) {
				getMapaActual().setEnemigo(true);
				break;
			}
		}

		/**/
		hiloAccion = new HiloAccion(this);
		/**/

		switch (nivel) {
		case 0: // Tutorial
			if (CadenaIngresada.contains("WDEWW") || CadenaIngresada.contains("wdeww")) {
				/**/
				hiloAccion.start();
				/**/
				Rend.getTxtMensaje().setText("");

				if (vivo == 0)
					return 1;
			}
			break;
		case 1: // Nivel 1
			if (getCadenaIngresada().contains("QWEASD") || getCadenaIngresada().contains("qweasd")) {
				/**/
				hiloAccion.start();
				/**/
				Rend.getTxtMensaje().setText("");

				if (vivo == 0)
					return 1;
			}
			break;
		case 2: // nivel 2
			if (getCadenaIngresada().contains("WQED") || getCadenaIngresada().contains("wqed")) {
				/**/
				hiloAccion.start();
				/**/
				Rend.getTxtMensaje().setText("");

				return 1;
			}
			break;
		}
		return 0;
	}

	public int AccionHermana() {
		if (getCadenaIngresada().contains("IUOL") || getCadenaIngresada().contains("iuol")) {
			dir[1] = 2;
			Rend.getTxtMensaje().setText("");
			hermana.setPosX(hermana.getPosX() + 1);
			Rend.pintar(cristobal.getPosX(), cristobal.getPosY(), hermana.getPosX(), hermana.getPosY(),
					mapaActual.getNivel(), getListaEnemigos(), dir);
			hermana.setPosX(hermana.getPosX() + 1);
			Rend.pintar(cristobal.getPosX(), cristobal.getPosY(), hermana.getPosX(), hermana.getPosY(),
					mapaActual.getNivel(), getListaEnemigos(), dir);
			return 1;
		}
		return 0;
	}

	/* # */
	public int VerificarDir(int posX, int posY) {

		if (posX != 0) {
			switch (posX) {
			case -1:
				return 4;
			case 1:
				return 2;
			}
		} else
			switch (posY) {
			case -1:
				return 1;
			case 1:
				return 3;
			}
		return 0;

	}

	/* # */
	public void AccionEspecial(Mapa mapaActual) {
		pausaKeyListener = true;
		try {
			JsonParser parser = new JsonParser();
			FileReader fr = new FileReader("Movimientos Acciones\\movAcciones.json");
			JsonElement datos = parser.parse(fr);
			ArrayList movAccion = aMovimientosAccion(datos);

			int tipoMapa = mapaActual.getNivel();
			int longi = movAccion.size();
			movAccion = (ArrayList) movAccion.get(tipoMapa);

			int numAccionNivel = ((ArrayList) movAccion.get(0)).size();

			for (int i = 0; i < numAccionNivel; i++) {

				ArrayList movCris = (ArrayList) movAccion.get(0);
				ArrayList movHerm = (ArrayList) movAccion.get(1);
				movCris = (ArrayList) movCris.get(i);
				movHerm = (ArrayList) movHerm.get(i);
				for (int j = 0; j < movCris.size(); j++) {// numeros de
															// Movimiento x
															// accion
					int crisX = (int) (((ArrayList) movCris.get(j))).get(0);
					int crisY = (int) (((ArrayList) movCris.get(j))).get(1);
					int hermX = (int) (((ArrayList) movHerm.get(j))).get(0);
					int hermY = (int) (((ArrayList) movHerm.get(j))).get(1);
					cristobal.setPosX(cristobal.getPosX() + crisX);
					cristobal.setPosY(cristobal.getPosY() + crisY);
					hermana.setPosX(hermana.getPosX() + hermX);
					hermana.setPosY(hermana.getPosY() + hermY);

					if (getMapaActual().getMapa()[cristobal.getPosY()][cristobal.getPosX()].getCelda() == 'E') {

						getMapaActual().setEnemigo(false);
						enemigo2 = (Enemigo) getListaEnemigos().get(0);
						enemigo2.setActivo(false);
						if (getMapaActual().getNivel() != 2) {
							((Enemigo) enemigo).setVida(((Enemigo) enemigo).getVida() - 1);
							vivo = enemigo.estaVivo();
							gestor.removerEnemigo(getMapaActual(), (Enemigo) enemigo);
							((Enemigo) enemigo).setMuerte(true);
						}
					}

					dir[0] = VerificarDir(crisX, crisY);
					dir[1] = VerificarDir(hermX, hermY);
					Rend.pintar(cristobal.getPosX(), cristobal.getPosY(), hermana.getPosX(), hermana.getPosY(),
							mapaActual.getNivel(), getListaEnemigos(), dir);
					hiloAccion.sleep(500);
				}

			}
		} catch (FileNotFoundException ex) {
		} catch (InterruptedException e) {
		}
		pausaKeyListener = false;
	}
	/* # */

	/* # */

	public void AccionDuo(Mapa mapaActual) {
		pausaKeyListener = true;
		try {

			JsonParser parser = new JsonParser();
			FileReader fr = new FileReader("Movimientos Duos\\movDuos.json");
			JsonElement datos = parser.parse(fr);
			ArrayList posDuo = aPosicionesDuo(datos);
			ArrayList movDuo = aMovimientosDuo(datos);

			int tipoMapa = mapaActual.getNivel();
			int longi = movDuo.size();

			boolean seMovio = false;
			movDuo = (ArrayList) movDuo.get(tipoMapa);
			ArrayList posDuoCris = (ArrayList) ((ArrayList) posDuo.get(0)).get(tipoMapa);
			ArrayList posDuoHerm = (ArrayList) ((ArrayList) posDuo.get(1)).get(tipoMapa);
			int numDuosNivel = ((ArrayList) movDuo.get(0)).size();

			for (int i = 0; i < numDuosNivel; i++) {
				if (seMovio)
					break;
				if (verficaPosicion((ArrayList) posDuoCris.get(i), (ArrayList) posDuoHerm.get(i))) {
					seMovio = true;
					ArrayList movCris = (ArrayList) movDuo.get(0);
					ArrayList movHerm = (ArrayList) movDuo.get(1);
					movCris = (ArrayList) movCris.get(i);
					movHerm = (ArrayList) movHerm.get(i);
					for (int j = 0; j < movCris.size(); j++) {// numeros de
																// Movimiento x
																// DUO
						int crisX = (int) (((ArrayList) movCris.get(j))).get(0);
						int crisY = (int) (((ArrayList) movCris.get(j))).get(1);
						int hermX = (int) (((ArrayList) movHerm.get(j))).get(0);
						int hermY = (int) (((ArrayList) movHerm.get(j))).get(1);
						cristobal.setPosX(cristobal.getPosX() + crisX);
						cristobal.setPosY(cristobal.getPosY() + crisY);
						hermana.setPosX(hermana.getPosX() + hermX);
						hermana.setPosY(hermana.getPosY() + hermY);

						/* # */
						dir[0] = VerificarDir(crisX, crisY);
						dir[1] = VerificarDir(hermX, hermY);
						Rend.pintar(cristobal.getPosX(), cristobal.getPosY(), hermana.getPosX(), hermana.getPosY(),
								mapaActual.getNivel(), getListaEnemigos(), dir);
						hiloDuo.sleep(500);
						/* # */

					}
					if (tipoMapa == (longi - 1) && (i == numDuosNivel - 1)) {// Ultimo
																				// nivel
																				// y
																				// accion
																				// contra
																				// la
																				// rata
						enemigo = getListaEnemigos().get(0);
						((Enemigo) enemigo).setVida(((Enemigo) enemigo).getVida() - 1);
						/* int */ vivo = enemigo.estaVivo();
						gestor.removerEnemigo(mapaActual, (Enemigo) enemigo);
						Rend.pintar(cristobal.getPosX(), cristobal.getPosY(), hermana.getPosX(), hermana.getPosY(),
								mapaActual.getNivel(), getListaEnemigos(), dir);
						Rend.mostrarHistoria(3);
						ReiniciarJuego();
						setJugando(false);
					}
				}
			}
		} catch (FileNotFoundException ex) {
		} catch (InterruptedException e) {
		}
		pausaKeyListener = false;
	}

	public void enviar_comando2(char comando) {
		int posCX = cristobal.getPosX(), posCY = cristobal.getPosY(), posHX = hermana.getPosX(),
				posHY = hermana.getPosY(); // posiciones anteriores
		Interprete.InterpretarAccion(comando, cristobal, hermana, getMapaActual(), dir);
		if (mapaActual.isEnemigo()) {
			Enemigo enemigo = (Enemigo) getListaEnemigos().get(0);
			enemigo.setActivo(true);
			if (ReiniciarHiloEnemigo) {
				hiloEnemAct = new HiloEnemigo(this, this.Rend);
				hiloEnemAct.start();
				hiloEnemAct.enem = enemigo;
				ReiniciarHiloEnemigo = false;
			}
		}
		Rend.pintar(cristobal.getPosX(), cristobal.getPosY(), hermana.getPosX(), hermana.getPosY(),
				mapaActual.getNivel(), getListaEnemigos(), dir);
	}

	/*
	 * public void enviar_comando(char comando) { MiPaquete mp = new
	 * MiPaquete(); mp.caracter = comando; Program.net.client.sendUDP(mp); }
	 */

	public boolean VerificarAccionDuo() {
		String clave = "";
		String clave_min = "";
		int nivel = getMapaActual().getNivel();

		switch (nivel) {
		case 0:
			clave = "WSIKDDLL";
			clave_min = "wsikddll";
			break;
		case 1:
			if (cristobal.getPosX() == 5 && hermana.getPosX() == 5) {
				clave = "IWOELD";
				clave_min = "iwoeld";
			} else {
				clave_min = "wuoqei";
				clave = "WUOQEI";
			}
			break;
		case 2:
			clave_min = "ldoequoe";
			clave = "LDOEQUOE";
			break;
		}
		//#MOD2 - Inicio
		if (cheatUsed == false && (CadenaIngresada.contains("dada") || CadenaIngresada.contains("yiyi"))){
			cheatUsed = true;
			MiPaquete mp = new MiPaquete();
			mp.tipoEnvio=5;
			mp.cheatUsed=true;
			Juego.net.client.sendUDP(mp);
			return true;
		}
		//#MOD2 - Fin
		else
			return (CadenaIngresada.contains(clave) || CadenaIngresada.contains(clave_min));
	}

	public void ReiniciarJuego() {
		hiloEnemAct.interrupt();
		getMapaActual().setNivel(0);
		gestor.CargarMapa(getMapaActual(), cristobal, hermana);
		listaObjetos = gestor.getListaObjetos();
		this.FinalizarJuego();
	}

	public void Jugar(String str) {
		EmpezarJuego(str);
	}

	public int EmpezarJuego(String str) {
		Tiempo tiempo = new Tiempo();
		setJugando(true);
		CargarEnemigos(getMapaActual());

		if (cristobal.estaVivo() == 0) {
			ReiniciarJuego();
			return 1;
		}

		enviar_comando2(str.charAt(0));

		int posCX = cristobal.getPosX(), posCY = cristobal.getPosY(), posHX = hermana.getPosX(),
				posHY = hermana.getPosY();

		int tipoCeldaH = getMapaActual().getMapa()[posHY][posHX].getTipo(),
				tipoCeldaC = getMapaActual().getMapa()[posCY][posCX].getTipo();

		if ((posCX == (getMapaActual().getNumCol() - 1)) && (posHX == posCX)) {
			getMapaActual().setNivel(getMapaActual().getNivel() + 1);
			Rend.mostrarHistoria(getMapaActual().getNivel());
			Rend.setFlgHistoria(1);
			gestor.CargarMapa(getMapaActual(), cristobal, hermana);
			listaObjetos = gestor.getListaObjetos();
			CargarEnemigos(getMapaActual());
		}

		// Para el primer nivel a cristobal su hilo de tiempo de accion
		// Cristobal
		if (getMapaActual().getNivel() == 1 && tipoCeldaC == 2 && hermana.getPosX() == 2) {
			// if(tipoCeldaC == 2)
			setCasillaCristobal(true);
			controladorAccionesHilo = new ControladorAcciones(this, tiempo.getTiempoSegDuoWSIKDDLL());
			controladorAccionesHilo.start();

		}

		String cadenaImprimible = "";
		if (getMapaActual().isEnemigo() || mapaActual.getNivel() == 2) {// IMPRIMIR
																		// EL
																		// NOMBRE
																		// DEL
																		// ENEMIGO
			enemigo = getListaEnemigos().get(0);
			cadenaImprimible = ((Personaje) (enemigo)).Imprimir();
		}

		if (getMapaActual().isEnemigo() == true && tipoCeldaC == 2) { // Hay
																		// enemigo
																		// y
																		// Cristobal
																		// en C
																		// (lo
																		// del
																		// boton
																		// guardar)
			setCasillaCristobal(true);
			if ((getMapaActual().getNivel() == 0) && cristobal.getPosX() == 11) { // Nivel
																					// 1
																					// Acciones
																					// Trigger
																					// con
																					// Enemigo,
																					// Accion:WSIKDDLL
				controladorAccionesHilo = new ControladorAcciones(this, tiempo.getTiempoSegDuoWSIKDDLL());
				controladorAccionesHilo.start();
			} else if ((getMapaActual().getNivel() == 1)) {// Nivel 2 Acciones
															// Trigger con
															// Enemigo,
															// Accion:WSIKDDLL
				if (cristobal.getPosX() == 5 && hermana.getPosX() == 5) {
					controladorAccionesHilo = new ControladorAcciones(this, tiempo.getTiempoSegDuoIWOELD());
					controladorAccionesHilo.start();
				}
			}
			Rend.mostrarAccionCristobal(getMapaActual().getNivel(), cadenaImprimible);
			Rend.pintar(cristobal.getPosX(), cristobal.getPosY(), hermana.getPosX(),hermana.getPosY(), mapaActual.getNivel(), getListaEnemigos(), aux);
			return 5;
		}

		if (getMapaActual().getNivel() == 2 && tipoCeldaC == 2) {// Segundo
																	// Nivel-Cristobal
																	// accion
																	// singular
			setCasillaCristobal(true);
			controladorAccionesHilo = new ControladorAcciones(this, tiempo.getTiempoSegSingleWQED());
			controladorAccionesHilo.start();		
			Rend.mostrarAccionCristobal(getMapaActual().getNivel(), cadenaImprimible);
			Rend.pintar(cristobal.getPosX(), cristobal.getPosY(), hermana.getPosX(),hermana.getPosY(), mapaActual.getNivel(), getListaEnemigos(), aux);
			return 5;

		}

		if (getMapaActual().getNivel() == 2 && tipoCeldaH == 2) {// Segundo
																	// Nivel-Hermana
																	// Accion
																	// singular
			setCasillaHermana(true);
			controladorAccionesHilo = new ControladorAcciones(this, tiempo.getTiempoSegSingleIUOL());
			controladorAccionesHilo.start();
			Rend.mostrarAccionHermana(cadenaImprimible);
			Rend.pintar(cristobal.getPosX(), cristobal.getPosY(), hermana.getPosX(),hermana.getPosY(), mapaActual.getNivel(), getListaEnemigos(), aux);
			return 5;
		}

		if ((getMapaActual().getNivel() == 2)) { // LA RATA BAJA VIDA
			if ((cristobal.getPosX() >= 11 && cristobal.getPosY() <= 9)
					|| (hermana.getPosX() >= 11 && hermana.getPosY() >= 3)) {
				cristobal.setVida(0);
				if (cristobal.estaVivo() == 0) {
					StopThemThreads();
				}
			}
		}

		if (tipoCeldaC == 1 && tipoCeldaH == 1) { // tipo 1 : 'D'
			setCasillaAccionDuo(true);
			if ((getMapaActual().getNivel() == 0)) { // Nivel 1 Acciones duo
														// Combo:WSIKDDLL
				// controladorAccionesHilo = new
				// ControladorAcciones(this,tiempoWSIKDDLL);
				controladorAccionesHilo = new ControladorAcciones(this, tiempo.getTiempoSegDuoWSIKDDLL());
				controladorAccionesHilo.start();
			} else if ((getMapaActual().getNivel() == 1)) {// Nivel 2 Acciones
															// duo Combos:
															// IWOELD y WUOQEI
															// (2 combos duos)
				// Para la primera accion Duo: IWOELD
				if (cristobal.getPosX() == 5 && hermana.getPosX() == 5) {
					controladorAccionesHilo = new ControladorAcciones(this, tiempo.getTiempoSegDuoIWOELD());
					controladorAccionesHilo.start();
				}
				// Para la segunda accion Duo: WUOQEI
				if (cristobal.getPosX() == 10 && hermana.getPosX() == 10) {
					controladorAccionesHilo = new ControladorAcciones(this, tiempo.getTiempoSegDuoWUOQEI());
					controladorAccionesHilo.start();
				}
			} else if ((getMapaActual().getNivel() == 2)) {// Nivel 3 Acciones
															// duo Combo:
															// LDOEQUOE
				controladorAccionesHilo = new ControladorAcciones(this, tiempo.getTiempoSegDuoLDOEQUOE());
				controladorAccionesHilo.start();
			}
			Rend.mostrarAccionDuo(getMapaActual().getNivel(), cristobal.getPosX(), hermana.getPosX(), cadenaImprimible);
			Rend.pintar(cristobal.getPosX(), cristobal.getPosY(), hermana.getPosX(),hermana.getPosY(), mapaActual.getNivel(), getListaEnemigos(), aux);
			return 5;
		}
		return 5;
	}

	public void FinalizarJuego() {
		setJugando(false);
		Personaje cristobal = new PersonajePrincipal();
		Personaje hermana = new PersonajePrincipal();
		gestor = new GestorMapas();
		setMapaActual(new Mapa());
		gestor.CargarMapa(getMapaActual(), cristobal, hermana);
		listaObjetos = gestor.getListaObjetos();
		Interprete = new InterpreteComandos();
		StopThemThreads();
		MiYouWin ganaste = new MiYouWin();
		cristobal.setVida(10);
	}

	public static ArrayList aPosicionesDuo(JsonElement elemento) {
		ArrayList arr = new ArrayList();
		arr.add(new ArrayList());
		arr.add(new ArrayList());
		JsonObject obj = elemento.getAsJsonObject();
		JsonObject posDuo = obj.getAsJsonObject("posDuo");

		JsonArray cristobal = posDuo.getAsJsonArray("cristobal");
		JsonArray hermana = posDuo.getAsJsonArray("hermana");

		ArrayList c = (ArrayList) arr.get(0);
		ArrayList h = (ArrayList) arr.get(1);

		for (int i = 0; i < cristobal.size(); i++) {// numero de niveles
			c.add(i, new ArrayList());
			h.add(i, new ArrayList());// [nivel i
			ArrayList cArrNiv1 = (ArrayList) c.get(i);// ,new ArrayList());
			ArrayList hArrNiv1 = (ArrayList) h.get(i);// ,new ArrayList());
			JsonArray cJsonNiv1 = cristobal.get(i).getAsJsonArray();
			JsonArray hJsonNiv1 = hermana.get(i).getAsJsonArray();
			for (int j = 0; j < cJsonNiv1.size(); j++) {// numero de PosDuo por
														// nivel
				cArrNiv1.add(j, new ArrayList());
				hArrNiv1.add(j, new ArrayList());

				ArrayList cArrNiv2 = (ArrayList) cArrNiv1.get(j);
				ArrayList hArrNiv2 = (ArrayList) hArrNiv1.get(j);

				JsonArray cJsonNiv2 = cJsonNiv1.get(j).getAsJsonArray();
				JsonArray hJsonNiv2 = hJsonNiv1.get(j).getAsJsonArray();
				for (int k = 0; k < cJsonNiv2.size(); k++) {// posX, posY
					cArrNiv2.add(k, cJsonNiv2.get(k).getAsNumber().intValue());
					hArrNiv2.add(k, hJsonNiv2.get(k).getAsNumber().intValue());
				}
			}
		}

		return arr;
	}

	public static ArrayList aMovimientosDuo(JsonElement elemento) {
		ArrayList arr = new ArrayList();
		JsonObject obj = elemento.getAsJsonObject();
		JsonArray posDuo = obj.get("movDuo").getAsJsonArray();
		for (int i = 0; i < posDuo.size(); i++) {
			arr.add(i, new ArrayList());
			ArrayList arrNivel1 = (ArrayList) arr.get(i);
			arrNivel1.add(0, new ArrayList());
			arrNivel1.add(1, new ArrayList());
			JsonArray crist = posDuo.get(i).getAsJsonObject().get("cristobal").getAsJsonArray();
			JsonArray herm = posDuo.get(i).getAsJsonObject().get("hermana").getAsJsonArray();

			ArrayList arrNivel20 = (ArrayList) arrNivel1.get(0);
			ArrayList arrNivel21 = (ArrayList) arrNivel1.get(1);

			for (int j = 0; j < crist.size(); j++) {// numreo de MovDuo por
													// Nivel
				arrNivel20.add(j, new ArrayList());
				arrNivel21.add(j, new ArrayList());

				ArrayList arrNivel30 = (ArrayList) arrNivel20.get(j);
				ArrayList arrNivel31 = (ArrayList) arrNivel21.get(j);

				JsonArray crist1 = crist.get(j).getAsJsonArray();
				JsonArray herm1 = herm.get(j).getAsJsonArray();
				for (int k = 0; k < crist1.size(); k++) {// numero de
															// movimientos por
															// Duo
					arrNivel30.add(k, new ArrayList());
					arrNivel31.add(k, new ArrayList());

					ArrayList arrNivel40 = (ArrayList) arrNivel30.get(k);
					ArrayList arrNivel41 = (ArrayList) arrNivel31.get(k);

					JsonArray crist2 = crist1.get(k).getAsJsonArray();
					JsonArray herm2 = herm1.get(k).getAsJsonArray();
					for (int l = 0; l < crist2.size(); l++) {
						arrNivel40.add(l, crist2.get(l).getAsNumber().intValue());
						arrNivel41.add(l, herm2.get(l).getAsNumber().intValue());
					}
				}
			}
		}
		return arr;
	}

	/* # */
	public static ArrayList aMovimientosAccion(JsonElement elemento) {
		ArrayList arr = new ArrayList();
		JsonObject obj = elemento.getAsJsonObject();
		JsonArray posDuo = obj.get("movAccion").getAsJsonArray();
		for (int i = 0; i < posDuo.size(); i++) {
			arr.add(i, new ArrayList());
			ArrayList arrNivel1 = (ArrayList) arr.get(i);
			arrNivel1.add(0, new ArrayList());
			arrNivel1.add(1, new ArrayList());
			JsonArray crist = posDuo.get(i).getAsJsonObject().get("cristobal").getAsJsonArray();
			JsonArray herm = posDuo.get(i).getAsJsonObject().get("hermana").getAsJsonArray();

			ArrayList arrNivel20 = (ArrayList) arrNivel1.get(0);
			ArrayList arrNivel21 = (ArrayList) arrNivel1.get(1);

			for (int j = 0; j < crist.size(); j++) {// numreo de MovDuo por
													// Nivel
				arrNivel20.add(j, new ArrayList());
				arrNivel21.add(j, new ArrayList());

				ArrayList arrNivel30 = (ArrayList) arrNivel20.get(j);
				ArrayList arrNivel31 = (ArrayList) arrNivel21.get(j);

				JsonArray crist1 = crist.get(j).getAsJsonArray();
				JsonArray herm1 = herm.get(j).getAsJsonArray();
				for (int k = 0; k < crist1.size(); k++) {// numero de
															// movimientos por
															// Duo
					arrNivel30.add(k, new ArrayList());
					arrNivel31.add(k, new ArrayList());

					ArrayList arrNivel40 = (ArrayList) arrNivel30.get(k);
					ArrayList arrNivel41 = (ArrayList) arrNivel31.get(k);

					JsonArray crist2 = crist1.get(k).getAsJsonArray();
					JsonArray herm2 = herm1.get(k).getAsJsonArray();
					for (int l = 0; l < crist2.size(); l++) {
						arrNivel40.add(l, crist2.get(l).getAsNumber().intValue());
						arrNivel41.add(l, herm2.get(l).getAsNumber().intValue());
					}
				}
			}
		}
		return arr;
	}
	/* # */

	public boolean verficaPosicion(ArrayList posCris, ArrayList posHerm) {
		return (cristobal.getPosX() == (int) posCris.get(0) && cristobal.getPosY() == (int) posCris.get(1)
				&& hermana.getPosX() == (int) posHerm.get(0) && hermana.getPosY() == (int) posHerm.get(1));
	}

	public Personaje getCristobal() {
		return cristobal;
	}

	public void setCristobal(Personaje cristobal) {
		this.cristobal = cristobal;
	}

	public Personaje getHermana() {
		return hermana;
	}

	public void setHermana(Personaje hermana) {
		this.hermana = hermana;
	}

	public Mapa getMapaActual() {
		return mapaActual;
	}

	public boolean isCasillaAccionDuo() {
		return CasillaAccionDuo;
	}

	public void setCasillaAccionDuo(boolean casillaAccionDuo) {
		CasillaAccionDuo = casillaAccionDuo;
	}

	public String getCadenaIngresada() {
		return CadenaIngresada;
	}

	public void setCadenaIngresada(String cadenaIngresada) {
		CadenaIngresada = cadenaIngresada;
	}

	public void setMapaActual(Mapa mapaActual) {
		this.mapaActual = mapaActual;
	}

	public List<Personaje> getListaEnemigos() {
		return listaEnemigos;
	}

	public void setListaEnemigos(List<Personaje> listaEnemigos) {
		this.listaEnemigos = listaEnemigos;
	}

	public boolean isJugando() {
		return jugando;
	}

	public void setJugando(boolean jugando) {
		this.jugando = jugando;
	}

	public boolean isCasillaCristobal() {
		return CasillaCristobal;
	}

	public void setCasillaCristobal(boolean casillaCristobal) {
		CasillaCristobal = casillaCristobal;
	}

	public boolean isCasillaHermana() {
		return CasillaHermana;
	}

	public void setCasillaHermana(boolean casillaHermana) {
		CasillaHermana = casillaHermana;
	}

	// #Mod1-Ini
	public void pausa() {
		int aux[] = new int[2];
		aux[0] = 0;
		aux[1] = 0;
		Rend.pintar(cristobal.getPosX(), cristobal.getPosY(), hermana.getPosX(), hermana.getPosY(),
				mapaActual.getNivel(), getListaEnemigos(), aux);
	}
	// #Mod1-Fin
	public void mostrarSubCadena(){
		
		String clave = "";
		String clave_min = "";		
		if (cont==0)
			subClave = "";
		
		switch (this.mapaActual.getNivel()) {
		case 0:
			clave = "WSIKDDLL";
			clave_min = "wsikddll";				
			break;
		case 1:
			if (cristobal.getPosX() == 5 && hermana.getPosX() == 5) {
				clave = "IWOELD";
				clave_min = "iwoeld";
			} else {
				clave_min = "wuoqei";
				clave = "WUOQEI";
			}
			break;
		case 2:
			clave_min = "ldoequoe";
			clave = "LDOEQUOE";
			break;
		}	
		
		if (CadenaIngresada.charAt(CadenaIngresada.length()-1) == clave.charAt(cont) 
				|| CadenaIngresada.charAt(CadenaIngresada.length()-1) == clave_min.charAt(cont)){
			
			subClave = subClave + CadenaIngresada.charAt(CadenaIngresada.length()-1);														
			Rend.mostrarSubCadena(subClave, cont);
			cont++;
		}
		else{
			cont = 0;
			Rend.mostrarAccionDuo(mapaActual.getNivel(), cristobal.getPosX(), hermana.getPosX(), "");
		}
	}
}

class MiObservadorVentana implements WindowListener {
	Renderizador refVentana;

	public MiObservadorVentana(Renderizador refVentana) {
		this.refVentana = refVentana;
	}

	public void windowActivated(WindowEvent e) {
	}

	public void windowClosed(WindowEvent e) {
	}

	public void windowClosing(WindowEvent e) {		
		if(Program.game.multijugador){
			MiPaquete mp = new MiPaquete();
			mp.tipoEnvio=2;
			mp.cerrar=true;
			Juego.net.client.sendUDP(mp);
		}
		//refVentana.dispose();
		//System.exit(0);
	}

	public void windowDeactivated(WindowEvent e) {
	}

	public void windowDeiconified(WindowEvent e) {
	}

	public void windowIconified(WindowEvent e) {
	}

	public void windowOpened(WindowEvent e) {
	}
}