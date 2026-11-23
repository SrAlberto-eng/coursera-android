package com.sralberto.petagram.vista.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sralberto.petagram.R;
import com.sralberto.petagram.adaptador.FotoAdaptador;
import com.sralberto.petagram.modelo.CuentaPrincipal;
import com.sralberto.petagram.modelo.Foto;
import com.sralberto.petagram.modelo.UsuarioInstagram;
import com.sralberto.petagram.presentador.IPerfilPresentador;
import com.sralberto.petagram.presentador.PerfilPresentador;
import com.sralberto.petagram.vista.IPerfilVista;

import java.util.ArrayList;

/**
 * Perfil del usuario principal: foto, nombre y cuadrícula con sus fotos de Instagram.
 * El usuario se cambia en el menú, en Configurar cuenta.
 */
public class PerfilMascotaFragment extends Fragment implements IPerfilVista {

    /** MainActivity manda este resultado cuando se toca una notificación de like con la app abierta. */
    public static final String RECARGAR = "recargar_perfil";

    private static final int COLUMNAS = 3;

    private IPerfilPresentador presentador;
    private FotoAdaptador adaptador;
    private ImageView imgPerfil;
    private TextView tvNombre;
    private TextView tvUsuario;
    private TextView tvError;
    private View progreso;
    private View contenedorError;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_perfil_mascota, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View vista, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(vista, savedInstanceState);

        imgPerfil = vista.findViewById(R.id.img_perfil);
        tvNombre = vista.findViewById(R.id.tv_nombre_perfil);
        tvUsuario = vista.findViewById(R.id.tv_usuario_perfil);
        progreso = vista.findViewById(R.id.progreso);
        contenedorError = vista.findViewById(R.id.contenedor_error);
        tvError = vista.findViewById(R.id.tv_error);

        adaptador = new FotoAdaptador();
        RecyclerView rvFotos = vista.findViewById(R.id.rv_fotos);
        rvFotos.setLayoutManager(new GridLayoutManager(requireContext(), COLUMNAS));
        rvFotos.setAdapter(adaptador);

        vista.findViewById(R.id.btn_reintentar).setOnClickListener(v -> presentador.cargarPerfil());

        presentador = new PerfilPresentador(this, new CuentaPrincipal(requireContext()));
        presentador.cargarPerfil();

        getParentFragmentManager().setFragmentResultListener(RECARGAR, getViewLifecycleOwner(),
                (clave, datos) -> presentador.cargarPerfil());
    }

    @Override
    public void onResume() {
        super.onResume();
        // Al regresar de Configurar cuenta el usuario principal pudo cambiar
        presentador.recargarSiCambioLaCuenta();
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
            adaptador.setFotos(new ArrayList<>());
        }
    }

    @Override
    public void mostrarUsuario(UsuarioInstagram usuario) {
        tvNombre.setText(usuario.getNombreCompleto());
        tvUsuario.setText(getString(R.string.formato_usuario, usuario.getUsername()));
        Glide.with(this)
                .load(usuario.getUrlFotoPerfil())
                .placeholder(R.drawable.placeholder_foto)
                .dontAnimate()
                .into(imgPerfil);
    }

    @Override
    public void mostrarFotos(ArrayList<Foto> fotos) {
        contenedorError.setVisibility(View.GONE);
        adaptador.setFotos(fotos);
    }

    @Override
    public void mostrarUsuarioNoEncontrado(String username) {
        tvNombre.setText("");
        tvUsuario.setText(getString(R.string.formato_usuario, username));
        Glide.with(this).clear(imgPerfil);
        imgPerfil.setImageResource(R.drawable.placeholder_foto);
        tvError.setText(getString(R.string.perfil_no_encontrado, username));
        contenedorError.setVisibility(View.VISIBLE);
    }

    @Override
    public void mostrarErrorDeConexion() {
        tvError.setText(R.string.error_conexion);
        contenedorError.setVisibility(View.VISIBLE);
    }
}
