package com.bakpun.mistborn.objetos;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.World;
import com.bakpun.mistborn.box2d.Box2dConfig;
import com.bakpun.mistborn.box2d.Fisica;
import com.bakpun.mistborn.elementos.Animacion;
import com.bakpun.mistborn.elementos.Imagen;
import com.bakpun.mistborn.enums.UserData;
import com.bakpun.mistborn.utiles.Recursos;

public class Plataforma {

	private Imagen spr;
	private Animacion anima;
	private Fisica f;
	private Body plataforma;
	
	
	public Plataforma(boolean esChica,Vector2 posXY,World mundo) {
		spr = new Imagen((esChica)?Recursos.PLATAFORMA_CHICA:Recursos.PLATAFORMA_GRANDE);	//Cargo el Sprite con la textura chica o grande,depende.
		spr.setEscalaBox2D(16);
		
		//Creo el body.
		f = new Fisica();									
		f.setBody(BodyType.KinematicBody, posXY);
		f.createPolygon((esChica)?50/Box2dConfig.PPM:80/Box2dConfig.PPM,(esChica)?15/Box2dConfig.PPM:24/Box2dConfig.PPM);
		f.setFixture(f.getPolygon(), 2, 0.5f, 0);
		plataforma = mundo.createBody(f.getBody());
		plataforma.createFixture(f.getFixture());
		plataforma.setUserData(UserData.SALTO_P);
		//Creo animacion.
		anima = new Animacion();
		anima.create((esChica)?Recursos.ANIMACION_PLATAFORMA_CHICA:Recursos.ANIMACION_PLATAFORMA_GRANDE, 3, 1, 0.6f);
	}
	
	public void draw(float delta) {
		//Update animacion.
		anima.update(delta);
		//Posicion el sprite en el body.
		spr.setPosicion(plataforma.getPosition().x, plataforma.getPosition().y);
		//dibujo sprite con la animacion que sigue.
		spr.draw(anima.getCurrentFrame());
	}
	
	public void dispose() {
		spr.getTexture().dispose();
		f.dispose();
	}
	
	
}
