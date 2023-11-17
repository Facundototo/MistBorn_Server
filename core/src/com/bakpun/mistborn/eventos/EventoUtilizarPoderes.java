package com.bakpun.mistborn.eventos;

public interface EventoUtilizarPoderes {
	void activarPeltre(boolean activo,int id);
	void seleccionPoder(int selecPoder,int id);
	void posMouse(float x, float y,int id);
}
