package com.sralberto.petagram.transiciones;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;

/**
 * Transición entre las pestañas de MainActivity. Mientras se desliza, la pestaña que sale
 * se hace más chica y transparente y la que entra crece hasta su tamaño normal.
 *
 * posicion vale 0 para la pestaña que se ve completa, -1 para la de la izquierda y 1 para la de la derecha.
 */
public class TransformadorZoom implements ViewPager2.PageTransformer {

    private static final float ESCALA_MINIMA = 0.85f;
    private static final float TRANSPARENCIA_MINIMA = 0.4f;

    @Override
    public void transformPage(@NonNull View pagina, float posicion) {
        float distancia = Math.min(Math.abs(posicion), 1f);
        float escala = 1f - (1f - ESCALA_MINIMA) * distancia;
        pagina.setScaleX(escala);
        pagina.setScaleY(escala);
        pagina.setAlpha(1f - (1f - TRANSPARENCIA_MINIMA) * distancia);
    }
}
