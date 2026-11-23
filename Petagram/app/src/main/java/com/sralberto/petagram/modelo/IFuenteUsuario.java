package com.sralberto.petagram.modelo;

/**
 * Dice de qué cuenta se muestra un perfil. CuentaPrincipal da el usuario de Configurar cuenta.
 * UsuarioActivity da la cuenta que dio like a una foto.
 */
public interface IFuenteUsuario {

    String obtenerUsuario();
}
