package com.sralberto.paperrecipes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.util.List;

/**
 * Llena las listas de ingredientes y pasos que comparten el detalle y la pantalla de preparación.
 */
public final class VistasReceta {

    /** Índice que indica que ningún ingrediente se marca como referencia. */
    public static final int SIN_ANCLA = -1;

    private VistasReceta() {
    }

    /** "500 g" para un ingrediente escalado. Si el factor no es válido muestra un guion. */
    public static String cantidadConUnidad(Ingrediente ingrediente, double factor) {
        return Escala.formatear(Escala.escalar(ingrediente.getCantidad(), factor))
                + " " + ingrediente.getUnidad();
    }

    /**
     * Agrega un renglón por ingrediente con su cantidad multiplicada por el factor.
     * El ingrediente en la posición ancla se pinta en terracota.
     */
    public static void llenarIngredientes(ViewGroup contenedor, List<Ingrediente> ingredientes,
                                          double factor, int ancla) {
        contenedor.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(contenedor.getContext());
        int colorAncla = ContextCompat.getColor(contenedor.getContext(), R.color.primario);
        for (int i = 0; i < ingredientes.size(); i++) {
            Ingrediente ingrediente = ingredientes.get(i);
            View renglon = inflater.inflate(R.layout.item_ingrediente, contenedor, false);
            TextView tvNombre = renglon.findViewById(R.id.tv_ingrediente);
            TextView tvCantidad = renglon.findViewById(R.id.tv_cantidad);
            tvNombre.setText(ingrediente.getNombre());
            tvCantidad.setText(cantidadConUnidad(ingrediente, factor));
            if (i == ancla) {
                tvNombre.setTextColor(colorAncla);
                tvCantidad.setTextColor(colorAncla);
            }
            contenedor.addView(renglon);
        }
    }

    public static void llenarPasos(ViewGroup contenedor, List<String> pasos) {
        contenedor.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(contenedor.getContext());
        for (int i = 0; i < pasos.size(); i++) {
            View renglon = inflater.inflate(R.layout.item_paso, contenedor, false);
            TextView tvNumero = renglon.findViewById(R.id.tv_numero);
            TextView tvTexto = renglon.findViewById(R.id.tv_paso);
            tvNumero.setText(String.valueOf(i + 1));
            tvTexto.setText(pasos.get(i));
            contenedor.addView(renglon);
        }
    }
}
