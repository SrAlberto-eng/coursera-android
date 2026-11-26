package com.sralberto.paperrecipes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Receta con sus porciones base, ingredientes y pasos. Las cantidades de los ingredientes
 * son las de la receta completa. Un id de 0 indica que la receta todavía no está guardada.
 */
public class Receta {

    public static final long SIN_ID = 0;

    private final long id;
    private final String nombre;
    private final String descripcion;
    private final int porciones;
    private final String foto;
    private final List<Ingrediente> ingredientes;
    private final List<String> pasos;

    public Receta(long id, String nombre, String descripcion, int porciones, String foto,
                  List<Ingrediente> ingredientes, List<String> pasos) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion == null ? "" : descripcion;
        this.porciones = porciones;
        this.foto = foto;
        this.ingredientes = Collections.unmodifiableList(new ArrayList<>(ingredientes));
        this.pasos = Collections.unmodifiableList(new ArrayList<>(pasos));
    }

    /** Constructor corto para las recetas de ejemplo, que todavía no tienen id ni foto. */
    public Receta(String nombre, String descripcion, int porciones,
                  Ingrediente[] ingredientes, String... pasos) {
        this(SIN_ID, nombre, descripcion, porciones, null,
                Arrays.asList(ingredientes), Arrays.asList(pasos));
    }

    public long getId() {
        return id;
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

    /** Ruta del archivo de la foto dentro de la app, o null si no tiene. */
    public String getFoto() {
        return foto;
    }

    public List<Ingrediente> getIngredientes() {
        return ingredientes;
    }

    public List<String> getPasos() {
        return pasos;
    }
}
