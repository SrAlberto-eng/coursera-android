package com.sralberto.petagram;

import androidx.annotation.DrawableRes;

public class Mascota {

    private final int id;
    private final String nombre;
    @DrawableRes
    private final int foto;
    private int raiting;

    public Mascota(int id, String nombre, @DrawableRes int foto, int raiting) {
        this.id = id;
        this.nombre = nombre;
        this.foto = foto;
        this.raiting = raiting;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    @DrawableRes
    public int getFoto() {
        return foto;
    }

    public int getRaiting() {
        return raiting;
    }

    public void setRaiting(int raiting) {
        this.raiting = raiting;
    }

    public void darHueso() {
        raiting++;
    }
}
