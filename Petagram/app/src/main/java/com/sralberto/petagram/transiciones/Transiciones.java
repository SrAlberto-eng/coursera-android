package com.sralberto.petagram.transiciones;

import android.transition.Explode;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionSet;
import android.view.Gravity;
import android.view.animation.DecelerateInterpolator;

/**
 * Transiciones entre pantallas hechas en Java. Las que están hechas en XML viven en res/transition.
 * Cada pantalla pone la suya en onCreate con getWindow().setEnterTransition().
 * Al regresar, Android reproduce la misma transición al revés.
 */
public final class Transiciones {

    private static final long DURACION = 500;

    private Transiciones() {
    }

    /** Favoritas. Las tarjetas salen disparadas desde el centro de la pantalla hacia su lugar. */
    public static Transition explotar() {
        Explode explotar = new Explode();
        explotar.setDuration(DURACION);
        explotar.setInterpolator(new DecelerateInterpolator());
        return sinBarrasDelSistema(explotar);
    }

    /** Configurar cuenta. La pantalla baja desde arriba mientras aparece poco a poco. */
    public static Transition bajarDesdeArriba() {
        TransitionSet conjunto = new TransitionSet()
                .addTransition(new Slide(Gravity.TOP))
                .addTransition(new Fade())
                .setOrdering(TransitionSet.ORDERING_TOGETHER)
                .setDuration(DURACION)
                .setInterpolator(new DecelerateInterpolator());
        return sinBarrasDelSistema(conjunto);
    }

    // La barra de estado y la de navegación se quedan quietas mientras se mueve el contenido
    private static Transition sinBarrasDelSistema(Transition transicion) {
        transicion.excludeTarget(android.R.id.statusBarBackground, true);
        transicion.excludeTarget(android.R.id.navigationBarBackground, true);
        return transicion;
    }
}
