package com.bakpun.mistborn.redes;

import java.net.InetAddress;

public class Cliente {	//Clase para almacenar los datos de los clientes que se conectan al server.
	
	private InetAddress ipCliente;
	private int puerto;
	private boolean listo = false;
	
	public Cliente(InetAddress ip, int puerto) {
		this.ipCliente = ip;
		this.puerto = puerto;
	}

	public InetAddress getIpCliente() {
		return ipCliente;
	}

	public int getPuerto() {
		return puerto;
	}
	
	public boolean getListo() {
		return listo;
	}
	
	public void setListo(boolean listo) {
		this.listo = listo;
	}
}
