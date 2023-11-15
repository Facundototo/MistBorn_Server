package com.bakpun.mistborn.eventos;

import com.badlogic.gdx.math.Vector2;
import com.bakpun.mistborn.enums.Movimiento;

public interface EventoInformacionPj {
	void actualizarPosClientes(int id, Vector2 coor);
	void actualizarAnimaClientes(int id, int frameIndex, Movimiento mov,boolean saltando);
}