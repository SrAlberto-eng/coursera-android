package com.sralberto.petagram.vista;

import com.sralberto.petagram.modelo.Foto;

/**
 * Lo que el presentador le puede pedir a la pantalla al dar like a una foto.
 * En los tres primeros casos Instagram ya guardó el like y la foto trae el conteo nuevo.
 */
public interface ILikeVista {

    /** Desactiva el hueso de la foto mientras se manda el like. */
    void mostrarEnviandoLike(Foto foto, boolean enviando);

    /** El servidor guardó el like y le mandó la notificación al dueño de la foto. */
    void mostrarLikeConAviso(Foto foto);

    /** El servidor guardó el like, pero el dueño no tiene un celular registrado. */
    void mostrarLikeSinDispositivos(Foto foto);

    /** No se pudo registrar el like en el servidor o no se pudo mandar la notificación. */
    void mostrarLikeSinAviso(Foto foto);

    /** Instagram no guardó el like. */
    void mostrarErrorLike();
}
