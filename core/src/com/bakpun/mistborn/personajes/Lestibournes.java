package com.bakpun.mistborn.personajes;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.bakpun.mistborn.box2d.Colision;
import com.bakpun.mistborn.enums.Spawn;
import com.bakpun.mistborn.enums.TipoPersonaje;
import com.bakpun.mistborn.io.Entradas;
import com.bakpun.mistborn.poderes.Hierro;
import com.bakpun.mistborn.utiles.Recursos;

public final class Lestibournes extends Personaje{

	public Lestibournes(World mundo, Entradas entradas,Colision c,OrthographicCamera cam,Spawn spawn,int id) {
		super(Recursos.PERSONAJE_VIN,Recursos.SALTOS_VIN,Recursos.ANIMACIONES_ESTADOS_VIN, mundo, entradas, c,cam,spawn,TipoPersonaje.ATRAEDOR,id);
	}

	@Override
	protected void crearPoderes(World mundo,OrthographicCamera cam,Colision c) {
		super.poderes[0] = new Hierro(mundo,this,cam,c);
	}

}
