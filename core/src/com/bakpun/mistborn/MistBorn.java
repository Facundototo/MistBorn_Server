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
		Render.app = this;			//Asigno a app esta clase para hacer el metodo setScreen() en otras pantallas.
		Render.batch = new SpriteBatch();	//SpriteBatch unico.
		Gdx.graphics.setWindowedMode(Config.ANCHO/3,Config.ALTO/2);
		this.setScreen(new PantallaEspera());
		
	}

	public void render () {
		super.render();
	}
	
	public void dispose () {
		hs.desconectar();
		Render.batch.dispose();		//SpriteBatch.
		super.dispose();
	}
}
