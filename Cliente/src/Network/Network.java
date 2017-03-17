package Network;

import java.io.IOException;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import Vista.FinalizarJuego;
import Vista.Program;


public class Network extends Listener {

	public Client client;
	public String ip = "localhost";
	int port = 27960;
	private FinalizarJuego ventanaFinalizar=null;
	
	public void connect(){
		client = new Client();
		client.getKryo().register(MiPaquete.class);	
		client.addListener(this);
		
		client.start();
		try {
			client.connect(5000, ip, port, port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void received(Connection c, Object o){
		//
		try {
			MiPaquete p = (MiPaquete) o;
			System.out.println(p.tipoEnvio);
			
			//System.out.println(p.cerrar);
			}
		catch (Exception e){}
		
		if(o instanceof MiPaquete){
			MiPaquete packet = (MiPaquete) o;
			switch(packet.tipoEnvio){
			//PRESIONAR CARACTERES
			case 0:
				char comando = packet.caracter;
				Program.game.recibirKeyPressed(comando);
				System.out.println(comando);
				break;
			//PARA QUE ESPERE AL OTRO JUGADOR (MULTIJUGADOR)
			case 1:
				int cantJug=packet.cantJugadores;
				if(cantJug<2){
					Program.conectado=false;
				}else if(cantJug==2){
					Program.conectado=true;
					Program.game.Rend.controlador.setContadorSeg(1000);
				}else if(cantJug>2){
					if(Program.conectado==false)
						System.exit(0);
				}
				
				break;
			//PARA QUE CUANDO CIERRE UN JUGADOR CIERRE EL OTRO
			case 2:
				if(packet.cerrar == true) {
					//Program.game.Rend.CerrarVentana();
					System.exit(0);
				}
				break;
			//PARA SABER QUE CLIENTE ES CRISTOBAL Y QUE CLIENTE ES CRISTINA
			case 3:
				if(o!=null){					
					boolean ch =packet.chooser;
					Program.game.recibirChooser(ch);
					System.out.println("Establecio Cristobal:");
				}
				break;
			//PARA FINALIZAR EL JUEGO EN CASO ESTE EN MULTIJADOR
			case 4:
				if(packet.finalizarJuego == true){
					ventanaFinalizar = new FinalizarJuego();
					ventanaFinalizar.setVisible(true);
				}
				if(packet.cerrarFinalizarJuego == true){
					System.exit(0);
				}
				if(packet.continuarJugando==true){
					ventanaFinalizar.ct.Stop();
					ventanaFinalizar.dispose();
				}
				break;
			//#MOD2 - Inicio
			case 5:
				if(packet.cheatUsed==true)
					Program.game.cheatUsed = true;
				break;
			//#MOD2 - Fin
			}			
		} 		
	}
}
