package Vista;

import Controlador.*;
import Modelo.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import javax.imageio.ImageIO;
import javax.swing.*;
import com.thoughtworks.xstream.XStream;
import Modelo.Celda;
import Modelo.Mapa;
import Modelo.Personaje;
import Network.MiPaquete;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Scanner;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

public class Renderizador extends JFrame {	
	private JPanel contentPane, panelMenu;
	private BufferedImage imgFondo[], imgHistoria[];
	private BufferStrategy bufferStrategy;
	private ImageIcon imgCristobal[], imgHermana[];
	private JLabel imgCris[], imgHerm[];
	private JButton btnGuardar;
	private JTextArea txtMensaje;
	private JLabel lblMensaje;
	private JLabel lblNombreJugador;
	private JLabel lblVidaJugador;
	private MiPanel panelMapa;
	private MyFileListener myFileListener = new MyFileListener(this);
	private int flgHistoria = 1;
	private ImageIcon imgObjVaca, imgObjPasto, imgObjPiedra, imgObjHueco, imgObjPared, imgEnemigos[][];
	private JLabel imgEnem[][];
	private String[][] fileEnem = { { "Componentes\\EscarabajoStandBy.gif" }, { "Componentes\\EscarabajoStandBy.gif" },{ "Componentes\\RataStandBy.gif" } };
	private List<JLabel> listaImgObjetos = null;
	private int dirC = 0;			
	private int dirH = 0;	
	
	private DibujoTiempo canvas;
	public ControladorTiempoHilos controlador;
	public int mostrarHistoria=-1;
	private GridBagConstraints gbcCristobal = new GridBagConstraints();
	private GridBagConstraints gbcHermana = new GridBagConstraints();
	private GridBagConstraints gbcEnemigo = new GridBagConstraints();
	private JButton btnFinalizar;
	
	private void DetenerHilos(Juego juego) {
		juego.hiloAccion.interrupt();
		juego.hiloDuo.interrupt();
		juego.hiloEnemAct.Stop();
		juego.controladorAccionesHilo.Stop();
	}

	public Renderizador(Juego juego) {
		Tiempo tiempo = new Tiempo();
		setResizable(false);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(1024 + 8 + 8 + 200, 768 + 30 + 8); // Barra de título ocupa 30
													// pix y bordes ocupan 8 pix

		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize(); // Centrar
																		// la
																		// pantalla
		this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);

		contentPane = new JPanel();

		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		try {			
			imgCristobal = new ImageIcon[5];
			imgHermana = new ImageIcon[5];
			imgCris = new JLabel[5];
			imgHerm = new JLabel[5];

			for (int i = 0; i < 5; i++) {
				imgCristobal[i] = new ImageIcon("Componentes/Cristobal" + i + ".gif");
				imgHermana[i] = new ImageIcon("Componentes/Cristina" + i + ".gif");

				imgCris[i] = new JLabel(imgCristobal[i]);
				imgHerm[i] = new JLabel(imgHermana[i]);
			}

			imgObjVaca = new ImageIcon("Componentes\\VacaStandBy.gif");
			imgObjPasto = new ImageIcon("Componentes\\Pasto.gif");
			imgObjPiedra = new ImageIcon("Componentes\\Roca.gif");
			imgObjHueco = new ImageIcon("Componentes\\Hueco.gif");
			imgObjPared = new ImageIcon("Componentes\\Pared.gif");

			cargarEnemigos();
			
			imgHistoria = new BufferedImage[4];
			for (int i = 0; i < 4; i++) {
				imgHistoria[i] = ImageIO.read(new File("Componentes\\Historia" + (i+1) + ".jpg"));
			}
			
			imgFondo = new BufferedImage[3];
			for(int i=0; i<3; i++)
				imgFondo[i] = ImageIO.read(new File("Componentes\\FondoNivel" + i + ".jpg"));

		} catch (Exception ex) {}

		panelMapa = new MiPanel();
		panelMapa.addMouseListener(new MouseAdapter() {// modificaciones del
														// laboratorio 3
			@Override
			public void mouseClicked(MouseEvent e) {
				getTxtMensaje().setText("");

				GridBagConstraints gbc_label = new GridBagConstraints();
				gbc_label.getClass();
				int x = e.getX();
				int y = e.getY();

				x = x / 64;
				y = y / 64;

				char tipoCelda = juego.getMapaActual().getMapa()[y][x].getCelda();
				String cad = "Posicion X:   " + x + "\nPosicion Y:   " + y + "\nPersonaje:    ";

				if ((e.getClickCount() == 2) && (e.getButton() == e.BUTTON1)) {

					if (x == juego.getCristobal().getPosX() && y == juego.getCristobal().getPosY()) {
						txtMensaje.append(cad + "Cristobal");
						getTxtMensaje().setVisible(true);
					} else if (x == juego.getHermana().getPosX() && y == juego.getHermana().getPosY()) {
						txtMensaje.append(cad + "Hermana de Cristobal");
						getTxtMensaje().setVisible(true);
					} else if (tipoCelda == 'v' || tipoCelda == 'V') {// vaca
						txtMensaje.append(cad + "Vaca");
						getTxtMensaje().setVisible(true);
					} else if (tipoCelda == 'p' || tipoCelda == 'P') {// pasto
						txtMensaje.append(cad + "Pasto");
						getTxtMensaje().setVisible(true);
					} else if (tipoCelda == 'D') {// duo
						txtMensaje.append(cad + "Terreno accion duo");
						getTxtMensaje().setVisible(true);
					} else if (tipoCelda == 'C') {// accion
						txtMensaje.append(cad + "Terreno accion especial");
						getTxtMensaje().setVisible(true);
					} else if (tipoCelda == 'S') {// terreno a
						txtMensaje.append(cad + "Terreno tipo A");
						getTxtMensaje().setVisible(true);
					} else if (tipoCelda == 'N') {// terreno b
						txtMensaje.append(cad + "Terreno tipo B");
						getTxtMensaje().setVisible(true);
					} else if (tipoCelda == ' ') {// vacio
						txtMensaje.append(cad + "VACIO");
						getTxtMensaje().setVisible(true);
					} else if (tipoCelda == 'g') {// obstac a
						txtMensaje.append(cad + "Obstaulo A");
						getTxtMensaje().setVisible(true);
					} else if (tipoCelda == 'n') {// obst b
						txtMensaje.append(cad + "Obstaculo B");
						getTxtMensaje().setVisible(true);
					} else if (tipoCelda == 'p') {// paso alto
						txtMensaje.append(cad + "Paso Alto");
						getTxtMensaje().setVisible(true);
					} else if (tipoCelda == 'h' || tipoCelda == 'H') {// hueco
						txtMensaje.append(cad + "Hueco");
						getTxtMensaje().setVisible(true);
					}
				} else if (e.getButton() == e.BUTTON3) {
					if (tipoCelda == 'D') // Si es tipo de celda accion duo
						mostrarAccionDuo(juego.getMapaActual().getNivel(), x, y, "");

					else if (tipoCelda == 'C') { // Si es tipo de celda accion
													// especial
						if (y > 7) // Si esta en el terreno de Cristobal
							mostrarAccionCristobal(juego.getMapaActual().getNivel(), "");
						else
							mostrarAccionHermana("");
					}
				}
			}
		});

		// Se añade el panel de la izquierda
		contentPane.add(panelMapa, BorderLayout.WEST);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[16];
		gbl_panel.rowHeights = new int[12];
		gbl_panel.columnWeights = new double[] {};
		gbl_panel.rowWeights = new double[] {};
		for (int i = 0; i < 12; i++) {
			gbl_panel.rowHeights[i] = 64;
		}
		for (int i = 0; i < 16; i++) {
			gbl_panel.columnWidths[i] = 64;
		}
		panelMapa.setLayout(gbl_panel);

		setPanelMenu(new JPanel());
		getPanelMenu().setBackground(Color.LIGHT_GRAY);
		getPanelMenu().setOpaque(true);
		contentPane.add(getPanelMenu(), BorderLayout.CENTER);
		getPanelMenu().setLayout(null);

		btnGuardar = new JButton("Ayuda");
		btnGuardar.setFocusTraversalKeysEnabled(false);
		btnGuardar.setFocusable(false);
		btnGuardar.setFocusPainted(false);
		btnGuardar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(juego.hiloEnemAct!=null)juego.hiloEnemAct.Stop();
				if(juego.controladorAccionesHilo!=null)juego.controladorAccionesHilo.Stop();
				File archivo = null;
				FileReader fr = null;
				BufferedReader br = null;
				String[] instr = new String[50];
				try {
					archivo = new File("Archivos de texto auxiliares\\Instrucciones.txt");
					fr = new FileReader(archivo);
					br = new BufferedReader(fr);

					String linea;
					int fila = 0;
					while ((linea = br.readLine()) != null) {
						instr[fila] = linea;
						fila++;
						instr[fila] = "\n";
					}
					instr[fila] = "";
				} catch (Exception e1) {
					e1.printStackTrace();
				} finally {
					try {
						if (null != fr) {
							fr.close();
						}
					} catch (Exception e2) {
						e2.printStackTrace();
					}
				}
				JOptionPane.showMessageDialog(Renderizador.this, instr);
				juego.hiloEnemAct = new HiloEnemigo(juego, juego.Rend);				
				juego.hiloEnemAct.start();
				if(juego.getMapaActual().getNivel() == 0 && juego.getCristobal().getPosX() == 5 && juego.getHermana().getPosX() == 5){
					juego.controladorAccionesHilo = new ControladorAcciones(juego, tiempo.getTiempoSegDuoWSIKDDLL());
					juego.controladorAccionesHilo.start();
				}
				else if(juego.getMapaActual().getNivel() == 0 && juego.getCristobal().getPosX() == 11){
					juego.controladorAccionesHilo = new ControladorAcciones(juego, tiempo.getTiempoSegSingleWDEWW());
					juego.controladorAccionesHilo.start();
				}
				else if(juego.getMapaActual().getNivel() == 1 && juego.getCristobal().getPosX() == 5 && juego.getHermana().getPosX() == 5){
					juego.controladorAccionesHilo = new ControladorAcciones(juego, tiempo.getTiempoSegDuoIWOELD());
					juego.controladorAccionesHilo.start();
				}
				else if(juego.getMapaActual().getNivel() == 1 && juego.getCristobal().getPosX() == 3){
					juego.controladorAccionesHilo = new ControladorAcciones(juego, tiempo.getTiempoSegSingleQWEASD());
					juego.controladorAccionesHilo.start();
				}
				else if(juego.getMapaActual().getNivel() == 1 && juego.getCristobal().getPosX() == 10 && juego.getHermana().getPosX() == 10){
					juego.controladorAccionesHilo = new ControladorAcciones(juego, tiempo.getTiempoSegDuoWUOQEI());
					juego.controladorAccionesHilo.start();
				}
				else if(juego.getMapaActual().getNivel() == 2 && juego.getCristobal().getPosX() == 9 && juego.getHermana().getPosX() == 9){
					juego.controladorAccionesHilo = new ControladorAcciones(juego, tiempo.getTiempoSegDuoLDOEQUOE());
					juego.controladorAccionesHilo.start();
				}
				else if(juego.getMapaActual().getNivel() == 2 && juego.getCristobal().getPosX() == 3){
					juego.controladorAccionesHilo = new ControladorAcciones(juego, tiempo.getTiempoSegSingleWQED());
					juego.controladorAccionesHilo.start();
				}
				else if(juego.getMapaActual().getNivel() == 2 && juego.getHermana().getPosX() == 3){
					juego.controladorAccionesHilo = new ControladorAcciones(juego, tiempo.getTiempoSegSingleIUOL());
					juego.controladorAccionesHilo.start();
				}
			}
		});
		btnGuardar.setBounds(55, 550, 112, 23);
		btnGuardar.setLocation(55, 550);
		panelMenu.add(btnGuardar);

		JButton btnNewButton = new JButton("Guardar");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if(juego.hiloEnemAct!=null)juego.hiloEnemAct.Stop();
				if(juego.controladorAccionesHilo!=null)juego.controladorAccionesHilo.Stop();
				JFileChooser chooser = new JFileChooser();
				String dialogTitle = "Guardar Estado del Juego";

				chooser.setDialogTitle(dialogTitle);
				chooser.setMultiSelectionEnabled(false);
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

				int sel = chooser.showOpenDialog(panelMapa);
				if (sel == JFileChooser.APPROVE_OPTION) {
					File selectedFile = chooser.getSelectedFile();

					try {
						XStream xs = new XStream();

						String nombreArchivo = selectedFile.getAbsolutePath();
						String revisarNombre = nombreArchivo.substring(nombreArchivo.length() - 4);
						if (revisarNombre.compareTo(".xml") != 0 && revisarNombre.compareTo(".XML") != 0) {
							nombreArchivo = nombreArchivo + ".xml";
						}
						FileWriter fw = new FileWriter(nombreArchivo);
						EstadoJuego objEstadoJuego = new EstadoJuego();
						objEstadoJuego.setCristobal((PersonajePrincipal) juego.getCristobal());
						objEstadoJuego.setHermana((PersonajePrincipal) juego.getHermana());
						objEstadoJuego.setMapa((Mapa) juego.getMapaActual());
						fw.write(xs.toXML(objEstadoJuego));

						fw.close();
					} catch (IOException e1) {
						System.out.println(e1.toString());
					}
				}
				juego.hiloEnemAct = new HiloEnemigo(juego, juego.Rend);
				juego.hiloEnemAct.start();
				if(juego.getMapaActual().getNivel() == 0 && juego.getCristobal().getPosX() == 5 && juego.getHermana().getPosX() == 5){
					juego.controladorAccionesHilo = new ControladorAcciones(juego, tiempo.getTiempoSegDuoWSIKDDLL());
					juego.controladorAccionesHilo.start();
				}
				else if(juego.getMapaActual().getNivel() == 0 && juego.getCristobal().getPosX() == 11){
					juego.controladorAccionesHilo = new ControladorAcciones(juego, tiempo.getTiempoSegSingleWDEWW());
					juego.controladorAccionesHilo.start();
				}
				else if(juego.getMapaActual().getNivel() == 1 && juego.getCristobal().getPosX() == 5 && juego.getHermana().getPosX() == 5){
					juego.controladorAccionesHilo = new ControladorAcciones(juego, tiempo.getTiempoSegDuoIWOELD());
					juego.controladorAccionesHilo.start();
				}
				else if(juego.getMapaActual().getNivel() == 1 && juego.getCristobal().getPosX() == 3){
					juego.controladorAccionesHilo = new ControladorAcciones(juego, tiempo.getTiempoSegSingleQWEASD());
					juego.controladorAccionesHilo.start();
				}
				else if(juego.getMapaActual().getNivel() == 1 && juego.getCristobal().getPosX() == 10 && juego.getHermana().getPosX() == 10){
					juego.controladorAccionesHilo = new ControladorAcciones(juego, tiempo.getTiempoSegDuoWUOQEI());
					juego.controladorAccionesHilo.start();
				}
				else if(juego.getMapaActual().getNivel() == 2 && juego.getCristobal().getPosX() == 9 && juego.getHermana().getPosX() == 9){
					juego.controladorAccionesHilo = new ControladorAcciones(juego, tiempo.getTiempoSegDuoLDOEQUOE());
					juego.controladorAccionesHilo.start();
				}
				else if(juego.getMapaActual().getNivel() == 2 && juego.getCristobal().getPosX() == 3){
					juego.controladorAccionesHilo = new ControladorAcciones(juego, tiempo.getTiempoSegSingleWQED());
					juego.controladorAccionesHilo.start();
				}
				else if(juego.getMapaActual().getNivel() == 2 && juego.getHermana().getPosX() == 3){
					juego.controladorAccionesHilo = new ControladorAcciones(juego, tiempo.getTiempoSegSingleIUOL());
					juego.controladorAccionesHilo.start();
				}
			}
		});
		btnNewButton.setFocusPainted(false);
		btnNewButton.setFocusTraversalKeysEnabled(false);
		btnNewButton.setFocusable(false);

		btnNewButton.setBounds(55, 584, 112, 23);
		getPanelMenu().add(btnNewButton);

		// Barra de informacion
		JLabel lblB = new JLabel("BARRA DE INFORMACI\u00D3N");
		lblB.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 15));
		lblB.setBounds(1, 11, 203, 14);
		getPanelMenu().add(lblB);

		JLabel lblNombre = new JLabel("Nombre: ");
		lblNombre.setBounds(10, 95, 57, 14);
		getPanelMenu().add(lblNombre);

		JLabel lblVida = new JLabel("Vida: ");
		lblVida.setBounds(27, 122, 40, 14);
		getPanelMenu().add(lblVida);

		setTxtMensaje(new JTextArea());
		getTxtMensaje().setFocusTraversalKeysEnabled(false);
		getTxtMensaje().setFocusable(false);
		getTxtMensaje().setBounds(10, 160, 194, 361);
		getPanelMenu().add(getTxtMensaje());
		getTxtMensaje().setColumns(10);

		lblMensaje = new JLabel("Mensaje: ");
		lblMensaje.setBounds(10, 147, 73, 14);
		getPanelMenu().add(lblMensaje);

		setLblNombreJugador(new JLabel(""));
		getLblNombreJugador().setBounds(64, 95, 140, 14);
		getPanelMenu().add(getLblNombreJugador());

		setLblVidaJugador(new JLabel(""));
		getLblVidaJugador().setBounds(62, 122, 142, 14);
		getPanelMenu().add(getLblVidaJugador());

		JButton btnVolverMenu = new JButton("Volver Menu");
		btnVolverMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				juego.StopThemThreads();				
			}
		});
		btnVolverMenu.setFocusPainted(false);
		btnVolverMenu.setFocusTraversalKeysEnabled(false);
		btnVolverMenu.setFocusable(false);
		btnVolverMenu.setBounds(55, 618, 113, 23);
		getPanelMenu().add(btnVolverMenu);

		
		
		controlador = new ControladorTiempoHilos(1000, this);
		canvas = new DibujoTiempo(controlador);
		canvas.setBackground(Color.LIGHT_GRAY);
		canvas.setBounds(64, 58, 95, 23);
		panelMenu.add(canvas);
		
		controlador.setCanvas(canvas);				
		
		JLabel lblTiempo = new JLabel("Tiempo:");
		lblTiempo.setBounds(10, 67, 46, 14);
		panelMenu.add(lblTiempo);
		
		//#Mod1
		JButton btnPausa = new JButton("Pausa");
		btnPausa.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (juego.pausaKeyListener){
					juego.pausaKeyListener = false;
				}
				else{
				//Detener hilos (si están corriendo)
				if(juego.hiloEnemAct!=null) 
					juego.hiloEnemAct.Stop();
				if(juego.controladorAccionesHilo!=null)
					juego.controladorAccionesHilo.Stop();
				
				//Pausar juego (impedir ingreso de comandos)
				juego.pausaKeyListener = true;
				}
				if (btnPausa.getText().equals("Pausa"))
					btnPausa.setText("Reanudar");
				else
					btnPausa.setText("Pausa");
				juego.pausa();
			}
		});				
		btnPausa.setFocusPainted(false);
		btnPausa.setFocusTraversalKeysEnabled(false);
		btnPausa.setFocusable(false);
		btnPausa.setBounds(55, 652, 112, 23);
		panelMenu.add(btnPausa);
		
		btnFinalizar = new JButton("Finalizar");
		btnFinalizar.setFocusable(false);
		btnFinalizar.setFocusTraversalKeysEnabled(false);
		btnFinalizar.setFocusPainted(false);
		if(juego.multijugador == false)
			btnFinalizar.setVisible(false);
		btnFinalizar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MiPaquete mp = new MiPaquete();
				mp.tipoEnvio=4;
				mp.finalizarJuego = true;
				Juego.net.client.sendUDP(mp);
			}
		});
		btnFinalizar.setBounds(55, 696, 112, 23);
		panelMenu.add(btnFinalizar);
		//#Mod1-Fin
		setVisible(true);
		
		createBufferStrategy(2);
		bufferStrategy = getBufferStrategy();
		Graphics graphics = bufferStrategy.getDrawGraphics();
		graphics.drawString("" + controlador.getContadorSeg(), 20, 20);
		
		controlador.start();
	}
	
	public void paint(Graphics g) {		        
		super.paint(g);
		if(mostrarHistoria>=0){						
			mostrarHistoria=-1;
			super.paint(g);
		}
		else
		if (dirC > 0 || dirH > 0) {
			
			Point p = imgCris[dirC].getLocation(), pH = imgHerm[dirH].getLocation();
			int x = p.x, y = p.y, xA = 0, yA = 0;
			int xH = pH.x, yH = pH.y, xHA = 0, yHA = 0;

			switch (dirC) {
			case 1:				
				yA = -4;
				break;
			case 2:			
				xA = 4;
				break;
			case 3:				
				yA = 4;
				break;
			case 4:				
				xA = -4;
				break;
			}
			switch (dirH) {
			case 1:				
				yHA = -4;
				break;
			case 2:				
				xHA = 4;
				break;
			case 3:				
				yHA = 4;
				break;
			case 4:				
				xHA = -4;
				break;
			}
			for (int i = 0; i < 16; i++) {
				imgCris[dirC].setLocation(x, y);
				imgHerm[dirH].setLocation(xH, yH);

				panelMapa.add(imgCris[dirC]);
				panelMapa.add(imgHerm[dirH]);
				x += xA;
				y += yA;
				xH += xHA;
				yH += yHA;
				super.paint(g);
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
				}
			}				
		}					
		dirC = 0;
		dirH = 0;
		/*Graphics2D g2 = null;
		try {
            //obtenemos uno de los buffers para dibujar
            g2 = (Graphics2D) bufferStrategy.getDrawGraphics();
            
            paint(g2);
           
		}
		catch(Exception e){
		
        } 
        finally {
            g2.dispose();
        }
        //pintamos el buffer en pantalla
        bufferStrategy.show();
        */
	}

	public void pintar(int posXCristobal, int posYCristobal, int posXHermana, int posYHermana, int nivel,
			List<Personaje> ListaEnemigos, int dir[]) {
		
		dirC = dir[0];
		dirH = dir[1];
		for (int i = 0; i < 5; i++) {
			panelMapa.remove(imgCris[i]);
			panelMapa.remove(imgHerm[i]);
		}
		if (dirC==0 && dirH == 0){
			gbcHermana.gridx = posXHermana;
			gbcHermana.gridy = posYHermana;
			gbcCristobal.gridx = posXCristobal;
			gbcCristobal.gridy = posYCristobal;
		}
		
		panelMapa.add(imgCris[dirC], gbcCristobal);
		panelMapa.add(imgHerm[dirH], gbcHermana);
		gbcHermana.gridx = posXHermana;
		gbcHermana.gridy = posYHermana;
		gbcCristobal.gridx = posXCristobal;
		gbcCristobal.gridy = posYCristobal;
		


		if (fileEnem[nivel].length != 0)
			for (int i = 0; i < imgEnem[nivel].length; i++) {
				panelMapa.remove(imgEnem[nivel][i]);
			}

		if (ListaEnemigos != null && imgEnem[nivel] != null) {
			for (int i = 0; i < imgEnem[nivel].length; i++) {
				Enemigo enemigo = (Enemigo) ListaEnemigos.get(i);
				if (enemigo.isActivo()) {
					gbcEnemigo.gridx = enemigo.getPosY();
					gbcEnemigo.gridy = enemigo.getPosX();
					gbcEnemigo.gridheight = enemigo.getDimensionY();
					gbcEnemigo.gridwidth = enemigo.getDimensionX();
					panelMapa.add(imgEnem[nivel][i], gbcEnemigo);
					break;
				}
			}
		}

		if (nivel == 2)
			apareceRata((Enemigo) ListaEnemigos.get(0));

		// Deben cargarse las imágenes antes de mostrar la ventana
		setVisible(true);
		// Antes de crear la estrategia se debe hacer visible la ventana
		//createBufferStrategy(2);
		// La estrategia se debe obtener una vez creada para poder usarla
		// en el paint. Si se obtiene en el paint se producirá parpadeo
		//bufferStrategy = getBufferStrategy();		
		repaint();
	}

	public void mostrarHistoria(int nivel) {
		mostrarHistoria = nivel;
		
		if (nivel > 0) {
			panelMapa.removeAll();
		}
		
		try {			
			panelMapa.ponerFondo(imgHistoria[mostrarHistoria]);
		} catch (Exception ex) {}
		
		// Deben cargarse las imágenes antes de mostrar la ventana
		setVisible(true);
		// Antes de crear la estrategia se debe hacer visible la ventana

		//createBufferStrategy(1);
		// La estrategia se debe obtener una vez creada para poder usarla
		// en el paint. Si se obtiene en el paint se producirá parpadeo
		//bufferStrategy = getBufferStrategy();
		repaint();
	}

	public void pintarObjetos(List<Objeto> listaObjetos, int numNivel) {

		listaImgObjetos = new ArrayList<JLabel>();

		if (numNivel == 0) {
			for (Objeto objeto : listaObjetos) {
				if (objeto.getNombre() == "vaca") {
					JLabel imgPasto = new JLabel(imgObjPasto);
					JLabel imgVaca = new JLabel(imgObjVaca);
					listaImgObjetos.add(imgVaca);
					listaImgObjetos.add(imgPasto);
					GridBagConstraints gbcObjeto = new GridBagConstraints();
					// Defino posicion de Cristobal
					gbcObjeto.gridx = objeto.getPosX();
					gbcObjeto.gridy = objeto.getPosY();
					gbcObjeto.gridheight = 1;
					gbcObjeto.gridwidth = 1;
					// Agrego a cristobal en el grid
					panelMapa.add(imgVaca, gbcObjeto);
					panelMapa.add(imgPasto, gbcObjeto);
				}
			}
		}

		if (numNivel == 1) {
			for (Objeto objeto : listaObjetos) {
				if (objeto.getNombre() == "piedra") {
					JLabel imgPiedra = new JLabel(imgObjPiedra);
					listaImgObjetos.add(imgPiedra);
					GridBagConstraints gbcObjeto = new GridBagConstraints();
					// Defino posicion de Cristobal
					gbcObjeto.gridx = objeto.getPosX();
					gbcObjeto.gridy = objeto.getPosY();
					gbcObjeto.gridheight = 1;
					gbcObjeto.gridwidth = 1;
					// Agrego a cristobal en el grid
					panelMapa.add(imgPiedra, gbcObjeto);
				}
				if (objeto.getNombre() == "hueco") {
					JLabel imgHueco = new JLabel(imgObjHueco);
					listaImgObjetos.add(imgHueco);
					GridBagConstraints gbcObjeto = new GridBagConstraints();
					// Defino posicion de Cristobal
					gbcObjeto.gridx = objeto.getPosX();
					gbcObjeto.gridy = objeto.getPosY();
					gbcObjeto.gridheight = 1;
					gbcObjeto.gridwidth = 1;
					// Agrego a cristobal en el grid
					panelMapa.add(imgHueco, gbcObjeto);
				}
			}
		}

		if (numNivel == 2) {
			for (Objeto objeto : listaObjetos) {
				if (objeto.getNombre() == "pared") {
					JLabel imgPared = new JLabel(imgObjPared);
					listaImgObjetos.add(imgPared);
					GridBagConstraints gbcObjeto = new GridBagConstraints();
					// Defino posicion de Cristobal
					gbcObjeto.gridx = objeto.getPosX();
					gbcObjeto.gridy = objeto.getPosY();
					gbcObjeto.gridheight = 1;
					gbcObjeto.gridwidth = 1;
					// Agrego a cristobal en el grid
					panelMapa.add(imgPared, gbcObjeto);
				}
				if (objeto.getNombre() == "hueco") {
					JLabel imgHueco = new JLabel(imgObjHueco);
					listaImgObjetos.add(imgHueco);
					GridBagConstraints gbcObjeto = new GridBagConstraints();
					// Defino posicion de Cristobal
					gbcObjeto.gridx = objeto.getPosX();
					gbcObjeto.gridy = objeto.getPosY();
					gbcObjeto.gridheight = 1;
					gbcObjeto.gridwidth = 1;
					// Agrego a cristobal en el grid
					panelMapa.add(imgHueco, gbcObjeto);
				}
			}
		}

		for (Objeto objeto : listaObjetos) {
			if (objeto.getNombre() == "pasto") {
				JLabel imgPasto = new JLabel(imgObjPasto);
				listaImgObjetos.add(imgPasto);
				GridBagConstraints gbcObjeto = new GridBagConstraints();
				// Defino posicion de Cristobal
				gbcObjeto.gridx = objeto.getPosX();
				gbcObjeto.gridy = objeto.getPosY();
				gbcObjeto.gridheight = 1;
				gbcObjeto.gridwidth = 1;
				// Agrego a cristobal en el grid
				panelMapa.add(imgPasto, gbcObjeto);
			}

		}

		// Deben cargarse las imágenes antes de mostrar la ventana
		setVisible(true);
		// Antes de crear la estrategia se debe hacer visible la ventana

		//createBufferStrategy(2);
		// La estrategia se debe obtener una vez creada para poder usarla
		// en el paint. Si se obtiene en el paint se producirá parpadeo
		//bufferStrategy = getBufferStrategy();
		// repaint();
	}

	public void apareceRata(Enemigo rata) {
		GridBagConstraints gbcRata = new GridBagConstraints();
		gbcRata.gridx = rata.getPosY();
		gbcRata.gridy = rata.getPosX();
		gbcRata.gridheight = rata.getDimensionY();
		gbcRata.gridwidth = rata.getDimensionX();
		panelMapa.add(imgEnem[2][0], gbcRata);
	}

	public void cargarEnemigos() {
		try {
			imgEnem = new JLabel[3][];
			imgEnemigos = new ImageIcon[3][];
			for (int i = 0; i < 3; i++) {
				if (fileEnem[i].length != 0) {
					imgEnem[i] = new JLabel[fileEnem[i].length];
					imgEnemigos[i] = new ImageIcon[fileEnem[i].length];
					for (int j = 0; j < fileEnem[i].length; j++) {
						imgEnemigos[i][j] = new ImageIcon(fileEnem[i][j]);
						imgEnem[i][j] = new JLabel(imgEnemigos[i][j]);
					}
				} else {
					imgEnem[i] = null;
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	public void mostrarSubCadena(String cadenaImprimible, int n) {
		if (cadenaImprimible.length()==1)
			txtMensaje.append("\nCadena Ingresada : ");
		txtMensaje.append(""+cadenaImprimible.charAt(n));
	}
	
	public void mostrarAccionDuo(int nivel, int posCX, int posHX, String cadenaImprimible) {
		String clave = "";
		getTxtMensaje().setText("");
		switch (nivel) {
		case 0:
			clave = "WSIKDDLL";
			break;
		case 1:
			if (posCX == 5 && posHX == 5)
				clave = "IWOELD";
			else
				clave = "WUOQEI";
			break;
		case 2:
			clave = "LDOEQUOE";
			break;
		}
		txtMensaje.append(cadenaImprimible + "Ingrese la secuencia: " + clave + "\nLuego presione Enter");
	}

	public void mostrarAccionCristobal(int nivel, String cadenaImprimible) {
		// Limpio el texto que hay en el cuadro
		getTxtMensaje().setText("");
		switch (nivel) {
		case 0:
			txtMensaje.append(cadenaImprimible + "Ingrese la secuencia: WDEWW\nLuego presione Enter");
			getTxtMensaje().setVisible(true);
			break;
		case 1:
			txtMensaje.append(cadenaImprimible + "Ingrese la secuencia: QWEASD\nLuego presione Enter");
			getTxtMensaje().setVisible(true);
			break;
		case 2:
			txtMensaje.append(cadenaImprimible + "Ingrese la secuencia: WQED\nLuego presione Enter");
			getTxtMensaje().setVisible(true);
			break;
		}
	}

	public void mostrarAccionHermana(String cadenaImprimible) {
		// Limpio el texto que hay en el cuadro
		getTxtMensaje().setText("");
		txtMensaje.append(cadenaImprimible + "Ingrese la secuencia: IUOL\nLuego presione Enter");
		getTxtMensaje().setVisible(true);
	}

	public void CambiarFondo(int nivel) {
		try {		
			panelMapa.ponerFondo(imgFondo[nivel]);
		} catch (Exception ex) {
		}
	}

	public JTextArea getTxtMensaje() {
		return txtMensaje;
	}

	public void setTxtMensaje(JTextArea jTextField) {
		this.txtMensaje = jTextField;
	}

	public JLabel getLblNombreJugador() {
		return lblNombreJugador;
	}

	public void setLblNombreJugador(JLabel lblNombreJugador) {
		this.lblNombreJugador = lblNombreJugador;
	}

	public JLabel getLblVidaJugador() {
		return lblVidaJugador;
	}

	public void setLblVidaJugador(JLabel lblVidaJugador) {
		this.lblVidaJugador = lblVidaJugador;
	}

	public JPanel getPanelMenu() {
		return panelMenu;
	}

	public void setPanelMenu(JPanel panelMenu) {
		this.panelMenu = panelMenu;
	}

	public int getFlgHistoria() {
		return flgHistoria;
	}

	public void setFlgHistoria(int flgHistoria) {
		this.flgHistoria = flgHistoria;
	}
	
	public void mostrarPausa(boolean pausa) {
		// Limpio el texto que hay en el cuadro
		getTxtMensaje().setText("");
		if (pausa)
			txtMensaje.append("JUEGO REANUDADO");
		else
			txtMensaje.append("JUEGO PAUSADO");
		getTxtMensaje().setVisible(true);
		
	}
	public void CerrarVentana() {
		this.dispose();
		System.exit( 0 );
	}
}

class MyFileListener implements ActionListener {
	private JFileChooser chooser = new JFileChooser();
	private JFrame frame;

	public MyFileListener(JFrame frame) {
		this.frame = frame;
	}

	public void actionPerformed(ActionEvent ae) {
		String textButton = ae.getActionCommand();
		String dialogTitle = "Abrir un fichero";

		if (textButton.equals("Guardar"))
			dialogTitle = "Guardar un fichero";

		chooser.setDialogTitle(dialogTitle);
		chooser.setMultiSelectionEnabled(false);
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

		int sel = chooser.showOpenDialog(frame);
		if (sel == JFileChooser.APPROVE_OPTION) {
			File selectedFile = chooser.getSelectedFile();
			JOptionPane.showMessageDialog(frame, selectedFile.getAbsolutePath());
		} else {
			return;
		}
	}
		
	
}
