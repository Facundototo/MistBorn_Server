package com.bakpun.mistborn;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.bakpun.mistborn.pantallas.PantallaEspera;
import com.bakpun.mistborn.redes.HiloServidor;
import com.bakpun.mistborn.utiles.Config;
import com.bakpun.mistborn.utiles.Render;

public class MistBorn extends Game {

	HiloServidor hs;
	
	public void create() {
		hs = new HiloServidor();
		hs.start();
		//Render.audio = new Audio();
		Render.app = this;			//Asigno a app esta clase para hacer el metodo setScreen() en otras pantallas.
		Render.batch = new SpriteBatch();	//SpriteBatch unico.
		//Render.audio.cancionMenu.play();
		//Render.audio.cancionMenu.setLooping(true);
		Gdx.graphics.setWindowedMode(Config.ANCHO,Config.ALTO);
		this.setScreen(new PantallaEspera());
		
	}

	public void render () {
		super.render();
	}
	
	public void dispose () {
		Render.batch.dispose();		//SpriteBatch.
		Render.audio.dispose();
		super.dispose();
	}
}