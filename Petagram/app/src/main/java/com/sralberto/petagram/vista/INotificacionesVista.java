package com.sralberto.petagram.vista;

import com.sralberto.petagram.modelo.UsuarioInstagram;

/**
 * Lo que el presentador le puede pedir a la pantalla al activar las notificaciones.
 */
public interface INotificacionesVista {

    void mostrarRegistrando(boolean registrando);

    void mostrarRegistroExitoso(UsuarioInstagram usuario, String token);

    void mostrarUsuarioNoEncontrado(String username);

    void mostrarErrorToken();

    void mostrarErrorServidor();

    void mostrarErrorDeConexion();
}
