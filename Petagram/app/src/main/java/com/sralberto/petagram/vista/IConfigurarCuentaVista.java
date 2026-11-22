package com.sralberto.petagram.vista;

import com.sralberto.petagram.modelo.UsuarioInstagram;

/**
 * Lo que el presentador le puede pedir a la pantalla de Configurar cuenta.
 */
public interface IConfigurarCuentaVista {

    void mostrarUsuarioActual(String username);

    void mostrarCargando(boolean cargando);

    void mostrarErrorUsuarioVacio();

    void mostrarUsuarioNoEncontrado(String username);

    void mostrarErrorDeConexion();

    void cuentaGuardada(UsuarioInstagram usuario);
}
