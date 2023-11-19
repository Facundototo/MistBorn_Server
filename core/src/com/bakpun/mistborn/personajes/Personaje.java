package com.bakpun.mistborn.personajes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.World;
import com.bakpun.mistborn.box2d.Box2dConfig;
import com.bakpun.mistborn.box2d.Colision;
import com.bakpun.mistborn.box2d.ColisionMouse;
import com.bakpun.mistborn.box2d.Fisica;
import com.bakpun.mistborn.elementos.Animacion;
import com.bakpun.mistborn.elementos.Imagen;
import com.bakpun.mistborn.enums.Accion;
import com.bakpun.mistborn.enums.Movimiento;
import com.bakpun.mistborn.enums.OpcionAcero;
import com.bakpun.mistborn.enums.Spawn;
import com.bakpun.mistborn.enums.TipoAudio;
import com.bakpun.mistborn.enums.TipoPersonaje;
import com.bakpun.mistborn.enums.TipoPoder;
import com.bakpun.mistborn.enums.UserData;
import com.bakpun.mistborn.eventos.EventoEntradasPj;
import com.bakpun.mistborn.eventos.EventoGestionMonedas;
import com.bakpun.mistborn.eventos.EventoReducirVida;
import com.bakpun.mistborn.eventos.EventoUtilizarPoderes;
import com.bakpun.mistborn.eventos.Listeners;
import com.bakpun.mistborn.io.Entradas;
import com.bakpun.mistborn.poderes.Acero;
import com.bakpun.mistborn.poderes.Peltre;
import com.bakpun.mistborn.poderes.Poder;
import com.bakpun.mistborn.redes.HiloServidor;
import com.bakpun.mistborn.utiles.Config;
import com.bakpun.mistborn.utiles.Recursos;
import com.bakpun.mistborn.utiles.Render;

public abstract class Personaje implements EventoReducirVida,EventoGestionMonedas,EventoEntradasPj,EventoUtilizarPoderes{
	
	private float velocidadX = 15f, impulsoY = 20f;
	
	private float vida = 100f;
	
	private Animacion animacionQuieto,animacionCorrer;
	private Imagen spr;
	private Entradas entradas;
	private Body pj;
	private Vector2 movimiento, posMouse;
	private Fisica f;
	private Colision c;
	private ColisionMouse cm;
	private TextureRegion saltos[] = new TextureRegion[3];
	private String animacionSaltos[] = new String[3];
	private String animacionEstados[] = new String[2];
	protected Poder poderes[];
	private int monedas;
	private TipoPersonaje tipoPj;
	private Spawn spawn;
	private Movimiento mov = Movimiento.QUIETO;
	private Accion accion;
	
	private boolean saltar,puedeMoverse,estaCorriendo,estaQuieto,apuntando,disparando,primerSalto,segundoSalto,caidaSalto,correrDerecha,correrIzquierda;
	private boolean reproducirSonidoCorrer,flagEmpujar,flagPeltre;
	private float duracionQuieto = 0.2f,duracionCorrer = 0.15f,tiempoMonedas = 0f,tiempoEmpuje = 0f;
	private int seleccion = 0, id = -1;
	
	public Personaje(String rutaPj,String[] animacionSaltos,String[] animacionEstados,World mundo,Entradas entradas,Colision c,OrthographicCamera cam,Spawn spawn,TipoPersonaje tipoPj, int id) {
		this.animacionSaltos = animacionSaltos;
		this.animacionEstados = animacionEstados;
		this.spawn = spawn;
		this.c = c;
		this.entradas = entradas;
		this.poderes = new Poder[(tipoPj == TipoPersonaje.NACIDO_BRUMA)?3:1];
		this.tipoPj = tipoPj;
		this.monedas = 10;	//Monedas iniciales 10.
		this.id = id; 
		movimiento = new Vector2();
		posMouse = new Vector2();
		f = new Fisica();
		cm = new ColisionMouse(mundo,cam);
 		spr = new Imagen(rutaPj);
 		spr.setEscalaBox2D(12);
		crearAnimaciones();
		crearBody(mundo);
		crearPoderes(mundo,cam,c); 	//Si es oponente no se crean los poderes.
		Listeners.agregarListener(this);
	}
	
	protected abstract void crearPoderes(World mundo,OrthographicCamera cam,Colision c);
	
	private void crearBody(World mundo) {
		f.setBody(BodyType.DynamicBody,new Vector2((spawn == Spawn.IZQUIERDA)?(Config.ANCHO/4)/Box2dConfig.PPM:(Config.ANCHO/1.5f)/Box2dConfig.PPM,5));
		f.createPolygon(spr.getTexture().getWidth()/Box2dConfig.PPM, spr.getTexture().getHeight()/Box2dConfig.PPM);		//Uso la clase Fisica.
		f.setFixture(f.getPolygon(), 60, 0, 0);
		pj = mundo.createBody(f.getBody());
		pj.createFixture(f.getFixture());
		pj.setUserData(UserData.PJ);	//ID para la colision.
		pj.setFixedRotation(true);		//Para que el body no rote.
		f.getPolygon().dispose();
	}

	private void updateAnimacion(float delta) {		//Este metodo updatea que frame de la animacion se va a mostrar actualmente,lo llamo en draw().

		animacionQuieto.update(delta);
		animacionCorrer.update(delta);
	}
	
	public void draw(float delta) {		
		
		updateAnimacion(delta);
		
		calcularGolpe();	
		calcularAcciones();	//Activa o desactiva las acciones del pj en base al input.
		calcularSalto();	//Calcula el salto con la gravedad.
		calcularMovimiento();	//Calcula el movimiento.
		
		pj.setLinearVelocity(movimiento);	//Aplico al pj velocidad lineal, tanto para correr como para saltar.
		spr.setPosicion(pj.getPosition().x, pj.getPosition().y);	//Le digo al Sprite que se ponga en la posicion del body.
		
		if(HiloServidor.clientesEncontrados) {
			if(this.vida <= 0) {Listeners.terminarPelea((this.id == 0)?1:0);}
			Listeners.actualizarPosClientes(this.id, this.pj.getPosition());
			animar();	//Animacion del pj.
			//aumentarEnergia(delta);	//Aumento de los poderes.
			quemarPoder();	//Seleccion de poderes. Y demas acciones respecto a los mismos
		}
		if(flagEmpujar) {empujarme(delta);}		//Cuando se reduce la vida tambien se genera un impulso para que no sigan pegados los pjs.
		
		reproducirSFX();	//Efectos de sonido.
	}
	
	private void calcularGolpe() {
		if(c.isPjsChocando() && accion == Accion.GOLPE) {		//Si estan juntos y uno toca click se reduce la vida del oponente.
			Listeners.reducirVidaPj(20, ((this.id == 0)?1:0));
		}
	}

	private void aumentarEnergia(float delta) {
		this.tiempoMonedas += delta;	
		/*Inconvenientes de hacer esto: Cuando se termina la energia pero vos seguis manteniendo el disparo 
		con Acero, las monedas se disparan pero se caen al no haber energia, pasa esto porque se va aumentando y 
		permite que haya un minimo de energia para el disparo cuando la condicion del disparo es que energia > 0*/
		for (int i = 0; i < poderes.length; i++) {	
			if(poderes[i].getEnergia() < 100) {
				Listeners.aumentarPoderPj(this.tipoPj, poderes[i].getTipoPoder(), 0.05f);
			}
			if(this.tiempoMonedas > 2) { //Es el cooldown que tiene la regeneracion de las monedas.
				if(this.monedas < 10) {		//Si las monedas son 10 no se aumentan pero si se resetea el contador.
					Listeners.aumentarMonedas();
				}
				this.tiempoMonedas = 0;
			}
		}
	}

	private void quemarPoder() {
		/*if(tipoPj == TipoPersonaje.NACIDO_BRUMA){		//Si es nacido de la bruma, puede seleccionar los poderes.
			if(entradas.isPrimerPoder()) {seleccion = 0;}
			else if(entradas.isSegundoPoder()) {seleccion = 1;}
			else if(Gdx.input.isKeyJustPressed(Keys.R) || ((Peltre)poderes[2]).isPoderActivo()) {poderes[2].quemar();}
		}*/
		if(flagPeltre){aumentarVelocidad();}		
		else {reducirVelocidad();}
		// Este if le sirve tanto al nacido de la bruma como a atraedor y lanzamonedas.
		if(accion == Accion.DISPARANDO && tipoPj != TipoPersonaje.VIOLENTO) {poderes[seleccion].quemar();} 
		
		// Chequea todo el tiempo calcularFuerzas() porque lo que pasa es que todo lo de Disparo no se puede chequear en Acero.
		if(poderes[seleccion].getTipoPoder() == TipoPoder.ACERO) {
			//poderes[seleccion].getDisparo().calcularFuerzas(disparando);	
			if(accion == Accion.TOCA_X) {		//Si la seleccion es Acero y toca la X, se cambia la opcion.
				((Acero)poderes[seleccion]).cambiarOpcion();	//Lo casteo porque cambiarOpcion() es propia de Acero.
			}
		}
		//Si el poder seleccionado es hierro o acero pero con la opcion de empujar, se dibuja el puntero.
		if((poderes[seleccion].getTipoPoder() == TipoPoder.HIERRO) || (poderes[seleccion].getTipoPoder() == TipoPoder.ACERO && ((Acero)poderes[seleccion]).getOpcion() == OpcionAcero.EMPUJE)) {
			cm.dibujar(pj.getPosition(), posMouse);
			Listeners.actualizarColisionPj(cm.getPuntoColision().x, cm.getPuntoColision().y, this.id, cm.isColision());
		}
		
	}

	private void calcularAcciones() {
		//RECORDATORIO: Esto de ladoDerecho y de cambiarle las teclas es para probar las colisiones sin utilizar redes.	
		correrDerecha = (entradas.isIrDerD());
		correrIzquierda = (entradas.isIrIzqA());
		puedeMoverse = (correrDerecha != correrIzquierda);	//Si el jugador toca las 2 teclas a la vez no va a poder moverse.
		estaQuieto = ((!correrDerecha == !correrIzquierda) || !puedeMoverse && this.c.isPuedeSaltar(pj));
		estaCorriendo = ((correrDerecha || correrIzquierda) && puedeMoverse && this.c.isPuedeSaltar(pj));
		primerSalto = (movimiento.y > impulsoY - 8 && movimiento.y <= impulsoY);
		segundoSalto = (movimiento.y > 0 && movimiento.y <= impulsoY - 8);
		caidaSalto = (movimiento.y < 0);
		apuntando =	(entradas.isBotonDer()); 	//este booleano se utiliza en el metodo drawLineaDisparo().
		disparando = (entradas.isBotonIzq() && apuntando);	
	}

	//Metodo que administra los sonidos de los pj.
	private void reproducirSFX() {
		if(estaCorriendo) {
			if(!reproducirSonidoCorrer) {
				Render.audio.pjCorriendo.play(Render.audio.getVolumen(TipoAudio.SONIDO));
				reproducirSonidoCorrer = true;
			}
		}else {
			if(reproducirSonidoCorrer) {
				Render.audio.pjCorriendo.stop();
				reproducirSonidoCorrer = false;
			}
		}
	}

	private void animar() {
		if(c.isPuedeSaltar(pj)) {
			if(mov == Movimiento.QUIETO) {
				if(!c.isPuedeSaltar(pj)) {		//si estaQuieto pero salta, hace la animacion de salto.
					spr.draw(saltos[0]);
				}else {
					spr.draw(animacionQuieto.getCurrentFrame());
					Listeners.actualizarAnimaClientes(this.id, animacionQuieto.getCurrentFrameIndex(),mov,false);
				}
			}else if(mov == Movimiento.DERECHA || mov == Movimiento.IZQUIERDA) { 	//Si esta corriendo muestra el fotograma actual de la animacionCorrer.
				spr.draw(animacionCorrer.getCurrentFrame());
				Listeners.actualizarAnimaClientes(this.id, animacionCorrer.getCurrentFrameIndex(),mov,false);
			}
		}else {
			if(primerSalto) {		
				spr.draw(saltos[0]);		
				Listeners.actualizarAnimaClientes(this.id, 0,mov,true);
			}else if(segundoSalto) {		//saltos[] contiene las diferentes texturas, se van cambiando en base a la altura, o a la caida,
				spr.draw(saltos[1]);		//por eso no lo hice con la clase Animacion, porque no es constante esto.
				Listeners.actualizarAnimaClientes(this.id, 1,mov,true);
			}else if(caidaSalto) {
				spr.draw(saltos[2]);
				Listeners.actualizarAnimaClientes(this.id, 2,mov,true);
			}
		}
		if(mov != Movimiento.QUIETO) {			//cuando mov no es QUIETO va a poder flipearse el pj, porque sino se queda mirando para un lado que no es.
			spr.flip((mov == Movimiento.DERECHA)?false:true);
		}
	}
	
	public void dispose() {
		spr.getTexture().dispose();
		f.dispose();
		cm.dispose();
	}
	
	private void empujarme(float delta) {
		tiempoEmpuje += delta;
		if(tiempoEmpuje <= 0.3f) {
			pj.setLinearVelocity(2, 15);
		}else {
			tiempoEmpuje = 0f;
			flagEmpujar = false;
		}
		
	}
	
	private void crearAnimaciones() {
		animacionQuieto = new Animacion();
		animacionQuieto.create(animacionEstados[0], 4,1, duracionQuieto);
		animacionCorrer = new Animacion();
		animacionCorrer.create(animacionEstados[1], 4,1, duracionCorrer);	
		for (int i = 0; i < saltos.length; i++) {
			saltos[i] = new TextureRegion(new Texture((i==0)?animacionSaltos[i]:(i==1)?animacionSaltos[i]:animacionSaltos[i]));
		}
		
	}
	private void calcularSalto() {
		if(saltar) {	//Si poniamos mov == Movimiento.Salto no nos andaba del todo bien porque se superponia rapidamente con el QUIETO ahora funciona.
			movimiento.y = impulsoY;
			saltar = false;
		}else {
			movimiento.y = pj.getLinearVelocity().y;	//Esto hace que actue junto a la gravedad del mundo.
		}
	}
	private void calcularMovimiento() {
		if(mov == Movimiento.DERECHA) {
			movimiento.x = velocidadX;
		} else if (mov == Movimiento.IZQUIERDA){
			movimiento.x = -velocidadX;
		}else if(mov == Movimiento.QUIETO){//Esto para que no se quede deslizando.
			movimiento.x = 0;
		}		
	}
	public float getX() {
		return this.pj.getPosition().x;
	}
	public float getY() {
		return this.pj.getPosition().y;
	}
	public Body getBody() {
		return this.pj;
	}
	
	public Entradas getInput() {
		return this.entradas;
	}
	public void aplicarFuerza(Vector2 movimiento) {	//Metodo que se usa en la clase Disparo para los poderes, Acero y Hierro.
		pj.setLinearVelocity(movimiento);
	}
	public void aumentarVelocidad() {		//Metodo que se usa en el poder Peltre.
		velocidadX = 30;	
		impulsoY = 30;	//x2 es mucho.
	}
	
	public void reducirVelocidad() { 	//Metodo que se usa en el poder Peltre.
		velocidadX = 15f;		//Se vuelven a las velocidades iniciales.
		impulsoY = 20f;
	}
	
	@Override
	public void reducirVida(float dano, int idOponente) {
		if(this.id == idOponente){
			this.vida -= dano;
			flagEmpujar = true;
		}
	}
	
	@Override
	public void restarMonedas() {
		this.monedas--;
	}
	@Override
	public void aumentarMonedas() {
		this.monedas++;
	}
	
	@Override
	public void mover(Movimiento movimiento, int id) {
		if(this.id == id) {		//Se chequea el id del pj.
			if((movimiento == Movimiento.SALTO) && (c.isPuedeSaltar(pj))) {	//Si es Salto y esta en el piso se activa la flag saltar.
				saltar = true;
			}else {
				this.mov = movimiento;
			}
		}
	}
	
	@Override
	public void activarPeltre(boolean activo, int id) {
		if(this.id == id) {
			this.flagPeltre = activo;
		}
	}
	
	@Override
	public void seleccionPoder(int selecPoder,int id) {
		if(this.id == id) {
			this.seleccion = selecPoder;
		}
	}
	
	@Override
	public void posMouse(float x, float y,int id) {
		if(this.id == id) {
			this.posMouse.set(x, y);
		}
	}
	
	@Override
	public void ejecutar(Accion accion, int id) {
		if(this.id == id) {
			this.accion = accion;
		}
	}
	
	public int getCantMonedas() {
		return this.monedas;
	}
	public ColisionMouse getColisionMouse() {
		return this.cm;
	}
	public TipoPersonaje getTipo() {
		return this.tipoPj;
	}
	
}
