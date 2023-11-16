package com.bakpun.mistborn.eventos;

import java.util.ArrayList;
import java.util.EventListener;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.bakpun.mistborn.enums.Accion;
import com.bakpun.mistborn.enums.Movimiento;
import com.bakpun.mistborn.enums.TipoPersonaje;
import com.bakpun.mistborn.enums.TipoPoder;

public class Listeners {

	private static ArrayList<EventListener> listeners = new ArrayList<EventListener>();
	
	public static void agregarListener(EventListener listener) { 	//Anade las clases que tengan eventos.
		listeners.add(listener);
	}
	
	public static void crearBarraHUD(String ruta,Color color,TipoPoder tipo) {		//Este evento va a llamar al metodo de Hud para crear los marcos del poder que se cree.
		for (EventListener listener : listeners) {
			if(listener instanceof EventoCrearBarra) {
				((EventoCrearBarra)listener).crearBarra(ruta,color,tipo);
			}
		}
	}
	
	public static void reducirVidaPj(float dano, int idOponente) {		//Evento que se utiliza en Hud y Personaje.
		for (EventListener listener : listeners) {
			if(listener instanceof EventoReducirVida) {
				((EventoReducirVida)listener).reducirVida(dano,idOponente);
			}
		}
	}
	
	public static void reducirPoderPj(TipoPersonaje tipoPj,TipoPoder tipoPoder,float energia) {
		for (EventListener listener : listeners) {
			if(listener instanceof EventoGestionPoderes)
				((EventoGestionPoderes)listener).reducirPoder(tipoPj, tipoPoder, energia);
		}
	}
	
	public static void aumentarPoderPj(TipoPersonaje tipoPj,TipoPoder tipoPoder,float energia) {
		for (EventListener listener : listeners) {
			if(listener instanceof EventoGestionPoderes)
				((EventoGestionPoderes)listener).aumentarPoder(tipoPj, tipoPoder, energia);
		}
	}
	
	public static void restarMonedas() {
		for (EventListener listener : listeners) {
			if(listener instanceof EventoGestionMonedas)
			((EventoGestionMonedas)listener).restarMonedas();
		}
	}

	public static void aumentarMonedas() {
		for (EventListener listener : listeners) {
			if(listener instanceof EventoGestionMonedas)
			((EventoGestionMonedas)listener).aumentarMonedas();
		}
	}
	
	public static void setDuracion(int segundo) {
		for (EventListener listener : listeners) {
			if(listener instanceof EventoSetDuracionPeltre)
			((EventoSetDuracionPeltre)listener).setDuracion(segundo);
		}
	}
	
	public static void mover(Movimiento movimiento,int id) {
		for (EventListener listener : listeners) {
			if(listener instanceof EventoEntradasPj)
			((EventoEntradasPj)listener).mover(movimiento,id);
		}
	}
	
	public static void ejecutar(Accion accion,int id) {
		for (EventListener listener : listeners) {
			if(listener instanceof EventoEntradasPj)
			((EventoEntradasPj)listener).ejecutar(accion,id);
		}
	}
	
	public static void actualizarPosClientes(int id, Vector2 coor) {
		for (EventListener listener : listeners) {
			if(listener instanceof EventoInformacionPj)
			((EventoInformacionPj)listener).actualizarPosClientes(id,coor);
		}
	}
	
	public static void actualizarAnimaClientes(int id, int frameIndex,Movimiento mov,boolean saltando) {
		for (EventListener listener : listeners) {
			if(listener instanceof EventoInformacionPj)
			((EventoInformacionPj)listener).actualizarAnimaClientes(id,frameIndex,mov,saltando);
		}
	}
}
