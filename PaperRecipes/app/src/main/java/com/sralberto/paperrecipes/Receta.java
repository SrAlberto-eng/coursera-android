package com.sralberto.paperrecipes;

import java.util.Arrays;
import java.util.List;

/**
 * Receta con sus porciones base, ingredientes y pasos.
 */
public class Receta {

    private final String nombre;
    private final String descripcion;
    private final int porciones;
    private final List<Ingrediente> ingredientes;
    private final List<String> pasos;

    public Receta(String nombre, String descripcion, int porciones,
                  Ingrediente[] ingredientes, String... pasos) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.porciones = porciones;
        this.ingredientes = Arrays.asList(ingredientes);
        this.pasos = Arrays.asList(pasos);
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public int getPorciones() {
        return porciones;
    }

    public List<Ingrediente> getIngredientes() {
        return ingredientes;
    }

    public List<String> getPasos() {
        return pasos;
    }
}
