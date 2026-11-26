package com.sralberto.paperrecipes;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Cuadrícula de recetas con buscador y botón para crear una receta.
 * Se usa sola en celular y como panel izquierdo en tableta.
 */
public class ListaFragment extends Fragment {

    /** La actividad decide si abre el detalle en otra pantalla o en el panel derecho. */
    public interface AlElegirReceta {
        void alElegirReceta(long idReceta);
    }

    private static final int COLUMNAS = 2;
    private static final String ESTADO_SELECCION = "seleccion";

    private RecetaAdaptador adaptador;
    private View vacio;
    private TextView tvVacioTitulo;
    private TextView tvVacioMensaje;
    private List<Receta> todas = new ArrayList<>();
    private String consulta = "";
    private long seleccion = RecetaAdaptador.SIN_SELECCION;

    public ListaFragment() {
        super(R.layout.fragment_lista);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null) {
            seleccion = savedInstanceState.getLong(ESTADO_SELECCION, RecetaAdaptador.SIN_SELECCION);
        }

        vacio = view.findViewById(R.id.vacio);
        tvVacioTitulo = view.findViewById(R.id.tv_vacio_titulo);
        tvVacioMensaje = view.findViewById(R.id.tv_vacio_mensaje);

        RecyclerView rvRecetas = view.findViewById(R.id.rv_recetas);
        rvRecetas.setLayoutManager(new GridLayoutManager(requireContext(), COLUMNAS));
        adaptador = new RecetaAdaptador(
                idReceta -> ((AlElegirReceta) requireActivity()).alElegirReceta(idReceta));
        adaptador.seleccionar(seleccion);
        rvRecetas.setAdapter(adaptador);

        EditText etBuscar = view.findViewById(R.id.et_buscar);
        etBuscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                consulta = s.toString();
                mostrarFiltradas();
            }
        });

        view.findViewById(R.id.fab_nueva).setOnClickListener(v ->
                startActivity(new Intent(requireContext(), RegistroActivity.class)));
    }

    @Override
    public void onResume() {
        super.onResume();
        // Al volver del registro o del detalle la lista puede haber cambiado
        recargar();
    }

    /** Vuelve a leer las recetas de la base de datos. */
    public void recargar() {
        if (adaptador == null) {
            return;
        }
        todas = BaseDatosRecetas.obtener(requireContext()).obtenerRecetas();
        mostrarFiltradas();
    }

    private void mostrarFiltradas() {
        List<Receta> visibles = FiltroRecetas.filtrar(todas, consulta);
        adaptador.setRecetas(visibles);

        vacio.setVisibility(visibles.isEmpty() ? View.VISIBLE : View.GONE);
        if (todas.isEmpty()) {
            tvVacioTitulo.setText(R.string.vacio_titulo);
            tvVacioMensaje.setText(R.string.vacio_mensaje);
        } else {
            tvVacioTitulo.setText(R.string.sin_resultados_titulo);
            tvVacioMensaje.setText(R.string.sin_resultados_mensaje);
        }
    }

    /** Marca la tarjeta abierta en el panel de detalle. Solo se usa en tableta. */
    public void seleccionar(long idReceta) {
        seleccion = idReceta;
        if (adaptador != null) {
            adaptador.seleccionar(idReceta);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(ESTADO_SELECCION, seleccion);
    }
}
