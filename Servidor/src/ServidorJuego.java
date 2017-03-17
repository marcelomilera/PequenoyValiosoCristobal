import java.io.IOException;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;


public class ServidorJuego extends Listener {

	static Server server;
	static final int port = 27960;
	int cont = 0;
	
	public static void main(String[] args) throws IOException{
		server = new Server();
		server.getKryo().register(MiPaquete.class);		
		server.bind(port, port);
		server.start();
		server.addListener(new ServidorJuego());
		System.out.println("The server is ready");
	}
	
	//Cuando alguien se conecta se ejecuta conected
	public void connected(Connection c){
		cont++;
		System.out.println("Conectado");
		MiPaquete packet = new MiPaquete();
		packet.tipoEnvio=1;
		packet.cantJugadores=cont;
		server.sendToAllTCP(packet);
		
		if(cont==1){
			//Chooser temp =new Chooser();
			//temp.Cristobal=true;			
			MiPaquete mp = new MiPaquete();
			mp.tipoEnvio=3;
			mp.chooser=true;
			server.sendToAllTCP(mp);
			System.out.println("Primero");
		}
		if(cont==2){
			MiPaquete mp = new MiPaquete();
			mp.tipoEnvio=3;
			mp.chooser=false;
			server.sendToAllTCP(mp);
			System.out.println("Segundo");
		}
		System.out.println("Conectado");
	}
	
	public void received(Connection c, Object o){
		if(o instanceof MiPaquete){	
			MiPaquete packet = (MiPaquete) o;
			server.sendToAllTCP(packet);
		}
	}
	
	public void disconnected(Connection c){
		System.out.println("Connection dropped.");
		--cont;
	}
}
