package com.sralberto.lugarestijuana;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Los 4 lugares más concurridos de Tijuana.
 */
public final class Lugares {

    public static final List<Lugar> LISTA = Collections.unmodifiableList(Arrays.asList(
            new Lugar(R.string.lugar_revolucion, R.string.descripcion_revolucion,
                    R.drawable.lugar_revolucion, R.color.lugar_revolucion, 32.53635, -117.03701),
            new Lugar(R.string.lugar_garita, R.string.descripcion_garita,
                    R.drawable.lugar_garita, R.color.lugar_garita, 32.54333, -117.02972),
            new Lugar(R.string.lugar_cecut, R.string.descripcion_cecut,
                    R.drawable.lugar_cecut, R.color.lugar_cecut, 32.53000, -117.02389),
            new Lugar(R.string.lugar_playas, R.string.descripcion_playas,
                    R.drawable.lugar_playas, R.color.lugar_playas, 32.53423, -117.12278)));

    private Lugares() {
    }
}
