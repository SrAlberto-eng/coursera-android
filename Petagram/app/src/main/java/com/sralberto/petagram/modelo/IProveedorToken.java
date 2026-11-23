package com.sralberto.petagram.modelo;

/**
 * Entrega el token que identifica al dispositivo para recibir notificaciones.
 */
public interface IProveedorToken {

    interface AlObtenerToken {
        void alObtener(String token);

        void alFallar();
    }

    void obtenerToken(AlObtenerToken alObtenerToken);
}
