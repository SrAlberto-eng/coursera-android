package com.sralberto.petagram.presentador;

public interface IPerfilPresentador {

    void cargarPerfil();

    /** Vuelve a cargar solo si en Configurar cuenta se eligió otro usuario. */
    void recargarSiCambioLaCuenta();

    void detener();
}
