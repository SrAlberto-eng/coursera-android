package com.sralberto.lugarestijuana;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

public class Lugar {

    @StringRes
    private final int nombre;
    @StringRes
    private final int descripcion;
    @DrawableRes
    private final int ilustracion;
    @ColorRes
    private final int color;
    private final double latitud;
    private final double longitud;

    public Lugar(@StringRes int nombre, @StringRes int descripcion, @DrawableRes int ilustracion,
                 @ColorRes int color, double latitud, double longitud) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.ilustracion = ilustracion;
        this.color = color;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    @StringRes
    public int getNombre() {
        return nombre;
    }

    @StringRes
    public int getDescripcion() {
        return descripcion;
    }

    @DrawableRes
    public int getIlustracion() {
        return ilustracion;
    }

    @ColorRes
    public int getColor() {
        return color;
    }

    public double getLatitud() {
        return latitud;
    }

    public double getLongitud() {
        return longitud;
    }
}
