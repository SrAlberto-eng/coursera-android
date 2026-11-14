package com.sralberto.petagram;

import androidx.annotation.DrawableRes;

public class Foto {

    @DrawableRes
    private final int imagen;
    private final int raiting;

    public Foto(@DrawableRes int imagen, int raiting) {
        this.imagen = imagen;
        this.raiting = raiting;
    }

    @DrawableRes
    public int getImagen() {
        return imagen;
    }

    public int getRaiting() {
        return raiting;
    }
}
