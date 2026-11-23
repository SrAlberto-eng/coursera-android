package com.sralberto.petagram.presentador;

import com.sralberto.petagram.modelo.Foto;

public interface ILikePresentador {

    void darLike(Foto foto);

    /** Se llama cuando la vista se destruye. Cancela las peticiones que sigan en curso. */
    void detener();
}
