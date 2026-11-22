package com.sralberto.petagram.presentador;

public interface ITimelinePresentador {

    void obtenerTimeline();

    /** Se llama cuando la vista se destruye. Cancela las peticiones que sigan en curso. */
    void detener();
}
