package com.sralberto.petagram.vista.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.sralberto.petagram.R;
import com.sralberto.petagram.adaptador.TimelineAdaptador;
import com.sralberto.petagram.modelo.Foto;
import com.sralberto.petagram.presentador.ILikePresentador;
import com.sralberto.petagram.presentador.ITimelinePresentador;
import com.sralberto.petagram.presentador.LikePresentador;
import com.sralberto.petagram.presentador.TimelinePresentador;
import com.sralberto.petagram.vista.ILikeVista;
import com.sralberto.petagram.vista.ITimelineVista;

import java.util.ArrayList;

/**
 * Pestaña de inicio. Muestra las fotos recientes de los usuarios del Sandbox
 * y deja darles like con el hueso blanco. Solo dibuja lo que le piden los presentadores.
 */
public class TimelineFragment extends Fragment implements ITimelineVista, ILikeVista {

    private ITimelinePresentador presentador;
    private ILikePresentador presentadorLike;
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

        presentadorLike = new LikePresentador(this);
        adaptador = new TimelineAdaptador(foto -> presentadorLike.darLike(foto));
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
        presentadorLike.detener();
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

    @Override
    public void mostrarEnviandoLike(Foto foto, boolean enviando) {
        adaptador.setEnviando(foto, enviando);
    }

    @Override
    public void mostrarLikeConAviso(Foto foto) {
        mostrarLike(foto, R.string.like_con_aviso);
    }

    @Override
    public void mostrarLikeSinDispositivos(Foto foto) {
        mostrarLike(foto, R.string.like_sin_dispositivos);
    }

    @Override
    public void mostrarLikeSinAviso(Foto foto) {
        mostrarLike(foto, R.string.like_sin_aviso);
    }

    @Override
    public void mostrarErrorLike() {
        avisar(getString(R.string.error_like));
    }

    // Instagram ya guardó el like. La foto trae el conteo nuevo.
    private void mostrarLike(Foto foto, @StringRes int mensaje) {
        adaptador.actualizar(foto);
        avisar(getString(mensaje, foto.getUsuario().getUsername()));
    }

    private void avisar(String mensaje) {
        View vista = getView();
        if (vista != null) {
            Snackbar.make(vista, mensaje, Snackbar.LENGTH_LONG).show();
        }
    }
}
