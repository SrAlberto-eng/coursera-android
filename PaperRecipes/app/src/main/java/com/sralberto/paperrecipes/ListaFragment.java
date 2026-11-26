package com.sralberto.paperrecipes;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Cuadrícula de recetas con buscador y botón para crear una receta.
 * Se usa sola en celular y como panel izquierdo en tableta.
 */
public class ListaFragment extends Fragment {

    /** La actividad decide si abre el detalle en otra pantalla o en el panel derecho. */
    public interface AlElegirReceta {
        void alElegirReceta(int posicion);
    }

    private static final int COLUMNAS = 2;
    private static final String ESTADO_SELECCION = "seleccion";

    private RecetaAdaptador adaptador;
    private int seleccion = RecyclerView.NO_POSITION;

    public ListaFragment() {
        super(R.layout.fragment_lista);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null) {
            seleccion = savedInstanceState.getInt(ESTADO_SELECCION, RecyclerView.NO_POSITION);
        }

        RecyclerView rvRecetas = view.findViewById(R.id.rv_recetas);
        rvRecetas.setLayoutManager(new GridLayoutManager(requireContext(), COLUMNAS));
        adaptador = new RecetaAdaptador(Recetas.LISTA,
                posicion -> ((AlElegirReceta) requireActivity()).alElegirReceta(posicion));
        rvRecetas.setAdapter(adaptador);
        if (seleccion != RecyclerView.NO_POSITION) {
            adaptador.seleccionar(seleccion);
        }

        view.findViewById(R.id.fab_nueva).setOnClickListener(v ->
                startActivity(new Intent(requireContext(), RegistroActivity.class)));
    }

    /** Marca la tarjeta abierta en el panel de detalle. Solo se usa en tableta. */
    public void seleccionar(int posicion) {
        seleccion = posicion;
        if (adaptador != null) {
            adaptador.seleccionar(posicion);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ESTADO_SELECCION, seleccion);
    }
}
