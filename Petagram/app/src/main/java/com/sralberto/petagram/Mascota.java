package com.sralberto.petagram;

import androidx.annotation.DrawableRes;

/**
 * Entidad Mascota. Es lo que muestra cada tarjeta de la lista.
 */
public class Mascota {

    private final String nombre;
    @DrawableRes
    private final int foto;   // id de la imagen, por ejemplo R.drawable.mascota_catty
    private int raiting;      // cuántos huesos le han dado

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

    /** Suma un hueso al raiting. */
    public void darHueso() {
        raiting++;
    }
}
