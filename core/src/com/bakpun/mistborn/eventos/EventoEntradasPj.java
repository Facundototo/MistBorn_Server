package com.bakpun.mistborn.eventos;

import java.util.EventListener;

import com.bakpun.mistborn.enums.Accion;
import com.bakpun.mistborn.enums.Movimiento;

public interface EventoEntradasPj extends EventListener{
	void mover(Movimiento movimiento, int id);
	void ejecutar(Accion accion, int id);
}
