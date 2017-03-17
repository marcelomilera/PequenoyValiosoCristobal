package Vista;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import Modelo.EstadoJuego;
import Modelo.Mapa;
import Modelo.PersonajePrincipal;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class MiMenuPrincipal extends JFrame {
	private BufferedImage image;
//	private Juego game;

	public MiMenuPrincipal() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(1024, 768); // Barra de título ocupa 30 pix y bordes ocupan 8
							// pix
		// Centrar la pantalla
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);

		JLayeredPane layeredPane = new JLayeredPane();
		getContentPane().add(layeredPane, BorderLayout.CENTER);

		ImageIcon btnJugarImagen = new ImageIcon("Componentes\\btnJugar.jpg");
		JButton btnNewButton = new JButton(btnJugarImagen);
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				String nombreJugador = JOptionPane.showInputDialog("Ingrese su nombre de jugador");

				try {
					if (nombreJugador.length() > 0) {
						JOptionPane.showMessageDialog(null,
								"Bienvenido al juego " + nombreJugador + " que te diviertas jugando smile emoticon");
						Program.game = new Juego(nombreJugador,false,"");

					}
				} catch (Exception ex) {

				}

			}
		});
		btnNewButton.setBorder(null);
		btnNewButton.setFocusTraversalKeysEnabled(false);
		btnNewButton.setFocusPainted(false);
		btnNewButton.setFocusable(false);
		btnNewButton.setBounds(324, 450, 146, 45);
		layeredPane.add(btnNewButton);
		
		ImageIcon btnMultiImagen = new ImageIcon("Componentes\\btnMultijugador.jpg");
		JButton btnNewButton69 = new JButton(btnMultiImagen);
		btnNewButton69.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				String nombreJugador = JOptionPane.showInputDialog("Ingrese su nombre de jugador");
				String ip ;
				try {
					if (nombreJugador.length() > 0) {
						ip= JOptionPane.showInputDialog("Ingrese el numero de IP al que se desea conectar");
						JOptionPane.showMessageDialog(null,
								"Bienvenido al juego " + nombreJugador + " que te diviertas jugando smile emoticon");
						Program.game = new Juego(nombreJugador,true,ip);
					}
				} catch (Exception ex) {

				}
				
			}
		});
		btnNewButton69.setBorder(null);
		btnNewButton69.setFocusTraversalKeysEnabled(false);
		btnNewButton69.setFocusPainted(false);
		btnNewButton69.setFocusable(false);
		btnNewButton69.setBounds(524, 450, 146, 45);
		layeredPane.add(btnNewButton69);

		ImageIcon btnInstruccionesImagen = new ImageIcon("Componentes\\btnInstrucciones.jpg");
		JButton btnNewButton_1 = new JButton(btnInstruccionesImagen);
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
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
				JOptionPane.showMessageDialog(MiMenuPrincipal.this, instr);
			}
		});
		btnNewButton_1.setBorder(null);
		btnNewButton_1.setFocusPainted(false);
		btnNewButton_1.setFocusTraversalKeysEnabled(false);
		btnNewButton_1.setFocusable(false);
		btnNewButton_1.setBounds(424, 517, 146, 45);
		layeredPane.add(btnNewButton_1);

		ImageIcon btnCargarImagen = new ImageIcon("Componentes\\btnCargar.jpg");
		JButton btnNewButton_2 = new JButton(btnCargarImagen);
		btnNewButton_2.setBorder(null);
		btnNewButton_2.setFocusable(false);
		btnNewButton_2.setFocusPainted(false);
		btnNewButton_2.setFocusTraversalKeysEnabled(false);
		btnNewButton_2.setBounds(424, 584, 146, 45);
		layeredPane.add(btnNewButton_2);

		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				JFileChooser chooser = new JFileChooser();
				String dialogTitle = "Cargar Estado del Juego";
				chooser.setDialogTitle(dialogTitle);
				chooser.setMultiSelectionEnabled(false);
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				int sel = chooser.showOpenDialog(layeredPane);
				if (sel == JFileChooser.APPROVE_OPTION) {
					File selectedFile = chooser.getSelectedFile();
					try {
						FileReader fileReader = new FileReader(selectedFile.getAbsolutePath());
						XStream xstream = new XStream(new DomDriver());
						EstadoJuego pp1 = (EstadoJuego) xstream.fromXML(fileReader);
						PersonajePrincipal cristobal = pp1.getCristobal();
						System.out.println("Nombre Personaje Hermano: " + cristobal.getNombre());
						PersonajePrincipal hermana = pp1.getHermana();
						System.out.println("Nombre Personaje Hermana: " + hermana.getNombre());
						Mapa mapa = pp1.getMapa();
						System.out.println("Nivel mapa: " + mapa.getNivel());

						Program.game = new Juego(cristobal, hermana, mapa);
					} catch (Exception e) {
						e.printStackTrace();
					}

				} else {
					return;
				}
			}
		});

		ImageIcon btnSalirImagen = new ImageIcon("Componentes\\btnSalir.jpg");
		JButton btnNewButton_3 = new JButton(btnSalirImagen);
		btnNewButton_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				String mensaje = "¿Está seguro que desea salir del juego?";
				String titulo = "Salir";
				int confirm = JOptionPane.showConfirmDialog(null, mensaje, titulo, JOptionPane.YES_NO_OPTION);
				if (confirm == JOptionPane.YES_OPTION) {
					MiMenuPrincipal.this.dispose();
				}
			}
		});
		btnNewButton_3.setBorder(null);
		btnNewButton_3.setFocusPainted(false);
		btnNewButton_3.setFocusTraversalKeysEnabled(false);
		btnNewButton_3.setFocusable(false);
		btnNewButton_3.setBounds(424, 646, 146, 45);
		layeredPane.add(btnNewButton_3);

		JLabel lblNewLabel = new JLabel("New label");
		ImageIcon imgThisImg = new ImageIcon("Componentes\\menuPrincipal.jpg");
		lblNewLabel.setIcon(imgThisImg);
		lblNewLabel.setBounds(0, 0, 1024, 768);
		layeredPane.add(lblNewLabel);

		setVisible(true);
	}
}