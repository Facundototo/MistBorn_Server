package com.bakpun.mistborn.redes;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.EventListener;

import com.badlogic.gdx.math.Vector2;
import com.bakpun.mistborn.enums.Accion;
import com.bakpun.mistborn.enums.InfoPersonaje;
import com.bakpun.mistborn.enums.Movimiento;
import com.bakpun.mistborn.eventos.EventoInformacionPj;
import com.bakpun.mistborn.eventos.EventoReducirVida;
import com.bakpun.mistborn.eventos.Listeners;


public class HiloServidor extends Thread implements EventoInformacionPj,EventoReducirVida, EventListener{
	private DatagramSocket socket;
	public boolean fin = false;
	private int puerto = 7654;
	public static int cantConexiones = 0;
	public static Cliente clientes[] = new Cliente[2];
	public static boolean clientesEncontrados = false, clientesListos = false;
	
	public HiloServidor() {
		try {
			socket = new DatagramSocket(puerto);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		Listeners.agregarListener(this);
	}
	
	
	private void enviarMensaje(String msg, InetAddress ip, int puerto) {
		byte[] data = msg.getBytes(); 
		DatagramPacket dp = new DatagramPacket(data,data.length,ip,puerto);
		try {
			socket.send(dp);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	@Override
	public void run() {
		do {
			byte[] datos = new byte[1024];
			DatagramPacket dp = new DatagramPacket(datos, datos.length);
			try {
				socket.receive(dp);
				procesarMensaje(dp);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}while(!fin);
		
	}
	
	public void fin() {
		fin = true;
		socket.close();
	}

	private void procesarMensaje(DatagramPacket dp) {
		String msg[] = new String(dp.getData()).trim().split("#");	//Transformo el dato del paquete a String y se quitan los espacios con trim().	
		
		switch(msg[0]) { 
		case "conexion":
			if(cantConexiones < clientes.length) {	
				clientes[cantConexiones] = new Cliente(dp.getAddress(),dp.getPort());	//Se crea un cliente nuevo cuando establece la conexion.
				enviarMensaje("OK#" + cantConexiones++ ,dp.getAddress(),dp.getPort());				//Envia al cliente nuevo OK para que este almacene la ip del server.
				if(cantConexiones == clientes.length) {	//Si ya hay 2 clientes encontrados se le envia a los 2 OponenteListo.
					clientesEncontrados = true;			//Este booleano sirve solo para cambiar el texto de la pantalla del Server.	
					enviarMsgClientes("OponenteEncontrado");			
				}
			}
			break;
				
		case "desconectar":		//Este case esta adaptado tanto para la desconexion de PantallaEspera como para la de PantallaSeleccion.
			desconectar();
			break;
			
		case "listo":
			clientes[Integer.valueOf(msg[1])].setListo(true);
			if(clientes[0].getListo() && clientes[1].getListo()) {	//Si los 2 clientes estan listos se envia un mensaje para que empiecen la partida.
				clientesListos = true;
				enviarMsgClientes("EmpiezaPartida");
			}
			break;
			
		case "nolisto":	//Si el cliente quiere volver a elegir el pj.
			clientes[Integer.valueOf(msg[1])].setListo(false);
			break;	
			
		case "seleccion":	//Este case es para informar al otro cliente, de la seleccion que esta poniendo su oponente.
			// Almaceno la seleccion de cada cliente para despues crear la pantallaPvP.
			clientes[Integer.valueOf(msg[1])].setNombrePj(InfoPersonaje.values()[Integer.valueOf(msg[2])].getNombre()); 
			int oponenteId = ((Integer.valueOf(msg[1]) == 0)?1:0);
			enviarMensaje("seleccionOponente#" + msg[2], clientes[oponenteId].getIpCliente(), clientes[oponenteId].getPuerto());
			break;
			
		case "movimiento":
			boolean encontrado = false;
			int i = 0;
			Movimiento mov = Movimiento.QUIETO;		
			do {		//Buscamos el msg y lo guardamos en el enum para hacer mas faciles las comparaciones despues.
				if(msg[1].equals(Movimiento.values()[i].getMovimiento())) {
					mov = Movimiento.values()[i];
					encontrado = true;
				}
				i++;
			}while(!encontrado);
			Listeners.mover(mov, Integer.valueOf(msg[2]));	//LLamamos al evento que va hacia los pj.
			break;
			
		case "accion":
			encontrado = false;
			i = 0;
			Accion accion = Accion.NADA;		
			do {		
				if(msg[1].equals(Accion.values()[i].getAccion())) {
					accion = Accion.values()[i];
					encontrado = true;
				}
				i++;
			}while(!encontrado);
			Listeners.ejecutar(accion, Integer.valueOf(msg[2]));
			break;	
			
		case "poderPeltre":
			Listeners.activarPeltre(Boolean.valueOf(msg[1]), Integer.valueOf(msg[2]));
			break;
			
		case "selecPoder":
			Listeners.seleccionPoder(Integer.valueOf(msg[1]), Integer.valueOf(msg[2]));
			break;	
			
		case "posMouse":
			Listeners.posMouse(Float.valueOf(msg[1]), Float.valueOf(msg[2]), Integer.valueOf(msg[3]));
			break;		
		}
	}


	private void enviarMsgClientes(String msg) {
		enviarMensaje(msg, clientes[0].getIpCliente(), clientes[0].getPuerto());	
		enviarMensaje(msg, clientes[1].getIpCliente(), clientes[1].getPuerto());
	}


	public void desconectar() {
		if(cantConexiones == 1) {	//Caso de PantallaEspera, si la cantConexion es 1 se desconecta solo el cliente[0], ya que cliente[1] no existe porque todavia no lo encontro.
			enviarMensaje("desconexion", clientes[0].getIpCliente(), clientes[0].getPuerto());
			clientes[0] = null;
		}else if (cantConexiones == 2){	//Caso PantallaSeleccion, estan los 2 conectados, si uno se desconecta, el otro tambien porque no tiene oponente,tiene que buscar de nuevo.
			enviarMsgClientes("desconexion");
			clientes[0] = null;		
			clientes[1] = null;
		}
		clientesEncontrados = false;	//Para los 2 casos es false.
		clientesListos = false;
		cantConexiones = 0;				//Para los 2 casos es igual a 0.
		
	}

	@Override
	public void actualizarPosClientes(int id, Vector2 coor) {
		String msg = "posPj#" + String.valueOf(id) + "#" + String.valueOf(coor.x) + "#" + String.valueOf(coor.y);
		enviarMsgClientes(msg);	
	}

	@Override
	public void actualizarAnimaClientes(int id, int frameIndex,Movimiento mov,boolean saltando) {
		String msg = "animaPj#" + String.valueOf(id) + "#" + String.valueOf(frameIndex) + "#" + mov.getMovimiento() + "#" + String.valueOf(saltando);
		enviarMsgClientes(msg);
	}

	@Override
	public void reducirVida(float dano, int idOponente) {
		String msg = "reducir_vida#" + String.valueOf(idOponente) + "#" + String.valueOf(dano);
		enviarMsgClientes(msg);
	}

	@Override
	public void enviarTerminaPelea(int idGanador) {
		enviarMensaje("terminaPelea#" + ((idGanador == 0)?"Ganaste!":"Perdiste"), clientes[0].getIpCliente(), clientes[0].getPuerto());
		enviarMensaje("terminaPelea#" + ((idGanador == 1)?"Ganaste!":"Perdiste"), clientes[1].getIpCliente(), clientes[1].getPuerto());
	}
	
	@Override
	public void actualizarColisionPj(float x, float y, int id, boolean colisionando) {
		enviarMensaje("actColision#" + String.valueOf(x) + "#" + String.valueOf(y) + "#" + String.valueOf(colisionando), clientes[id].getIpCliente(), clientes[id].getPuerto());
	}
	
}
