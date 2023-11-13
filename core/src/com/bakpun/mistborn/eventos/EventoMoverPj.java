package com.bakpun.mistborn.eventos;

import java.util.EventListener;

import com.bakpun.mistborn.enums.Movimiento;

public interface EventoMoverPj extends EventListener{
	void mover(Movimiento movimiento, int id);
}
