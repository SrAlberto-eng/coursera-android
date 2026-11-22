package com.sralberto.petagram.vista;

import com.sralberto.petagram.modelo.Foto;

import java.util.ArrayList;

/**
 * Lo que el presentador le puede pedir a la pantalla del timeline.
 */
public interface ITimelineVista {

    void mostrarCargando(boolean cargando);

    void mostrarFotos(ArrayList<Foto> fotos);

    void mostrarErrorDeConexion();
}
