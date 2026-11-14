package com.sralberto.petagram;

import androidx.annotation.DrawableRes;

public class Mascota {

    private final String nombre;
    @DrawableRes
    private final int foto;
    private int raiting;

    public Mascota(String nombre, @DrawableRes int foto, int raiting) {
        this.nombre = nombre;
        this.foto = foto;
        this.raiting = raiting;
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

    public void darHueso() {
        raiting++;
    }
}
