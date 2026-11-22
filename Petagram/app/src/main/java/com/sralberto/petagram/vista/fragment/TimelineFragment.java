package com.sralberto.petagram.vista.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sralberto.petagram.R;
import com.sralberto.petagram.adaptador.TimelineAdaptador;
import com.sralberto.petagram.modelo.Foto;
import com.sralberto.petagram.presentador.ITimelinePresentador;
import com.sralberto.petagram.presentador.TimelinePresentador;
import com.sralberto.petagram.vista.ITimelineVista;

import java.util.ArrayList;

/**
 * Pestaña de inicio. Muestra las fotos recientes de los usuarios del Sandbox.
 * Solo dibuja lo que le pide el presentador.
 */
public class TimelineFragment extends Fragment implements ITimelineVista {

    private ITimelinePresentador presentador;
    private TimelineAdaptador adaptador;
    private View progreso;
    private View contenedorError;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_timeline, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View vista, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(vista, savedInstanceState);

        adaptador = new TimelineAdaptador();
        RecyclerView rvTimeline = vista.findViewById(R.id.rv_timeline);
        rvTimeline.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvTimeline.setAdapter(adaptador);

        progreso = vista.findViewById(R.id.progreso);
        contenedorError = vista.findViewById(R.id.contenedor_error);
        TextView tvError = vista.findViewById(R.id.tv_error);
        tvError.setText(R.string.error_conexion);
        vista.findViewById(R.id.btn_reintentar).setOnClickListener(v -> presentador.obtenerTimeline());

        presentador = new TimelinePresentador(this);
        presentador.obtenerTimeline();
    }

    @Override
    public void onDestroyView() {
        presentador.detener();
        super.onDestroyView();
    }

    @Override
    public void mostrarCargando(boolean cargando) {
        progreso.setVisibility(cargando ? View.VISIBLE : View.GONE);
        if (cargando) {
            contenedorError.setVisibility(View.GONE);
        }
    }

    @Override
    public void mostrarFotos(ArrayList<Foto> fotos) {
        contenedorError.setVisibility(View.GONE);
        adaptador.setFotos(fotos);
    }

    @Override
    public void mostrarErrorDeConexion() {
        contenedorError.setVisibility(View.VISIBLE);
    }
}
