package com.sralberto.petagram.vista;

import com.sralberto.petagram.modelo.Foto;
import com.sralberto.petagram.modelo.UsuarioInstagram;

import java.util.ArrayList;

/**
 * Lo que el presentador le puede pedir a la pantalla del perfil.
 */
public interface IPerfilVista {

    void mostrarCargando(boolean cargando);

    void mostrarUsuario(UsuarioInstagram usuario);

    void mostrarFotos(ArrayList<Foto> fotos);

    void mostrarUsuarioNoEncontrado(String username);

    void mostrarErrorDeConexion();
}
