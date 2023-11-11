package com.bakpun.mistborn.pantallas;

import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.bakpun.mistborn.elementos.SkinFreeTypeLoader;
import com.bakpun.mistborn.enums.Fuente;
import com.bakpun.mistborn.io.Entradas;
import com.bakpun.mistborn.redes.HiloServidor;
import com.bakpun.mistborn.utiles.Config;
import com.bakpun.mistborn.utiles.Render;

public class PantallaEspera implements Screen {
	
	private Stage stage;
	private Skin skin;
	private Label espera, cantidadJug;
	private Table tabla;
	private Entradas entradas;

	@Override
	public void show() {
		entradas = new Entradas();
		Gdx.input.setInputProcessor(entradas);
		stage = new Stage(new FillViewport(Config.ANCHO, Config.ALTO));
		skin = SkinFreeTypeLoader.cargar();
		tabla = new Table();
		tabla.setFillParent(true);
		cantidadJug = new Label("", Fuente.PIXELMENU.getStyle(skin));
		espera = new Label("", Fuente.PIXELMENU.getStyle(skin));
		espera.addAction(Actions.forever(Actions.sequence(
				Actions.fadeIn(1f),Actions.fadeOut(1f),		
			    Actions.delay(0.4f)
				)));
		
		tabla.add(espera).center().row();
		tabla.add(cantidadJug).center();
		stage.addActor(tabla);
		
		
	}

	@Override
	public void render(float delta) {
		Render.limpiarPantalla(0,0,0);;
		
		if(HiloServidor.clientesEncontrados){espera.setText("Eleccion de Personajes");}	//Informacion para saber en que fase estan los clientes.
		else {espera.setText("Buscando Jugadores...");}
		
		if(HiloServidor.clientesListos){
			Render.app.setScreen(new PantallaPvP(HiloServidor.clientes[0].getNombrePj(), HiloServidor.clientes[1].getNombrePj(),2));
		}
	
		cantidadJug.setText(HiloServidor.cantConexiones);
		
		stage.act();
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height);
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		stage.dispose();
		
	}
	
	
	
}
