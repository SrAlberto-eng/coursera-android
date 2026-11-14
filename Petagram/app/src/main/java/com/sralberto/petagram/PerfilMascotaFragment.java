package com.sralberto.petagram;

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

import java.util.ArrayList;

public class PerfilMascotaFragment extends Fragment {

    private static final int COLUMNAS = 3;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragment_perfil_mascota, container, false);

        Mascota mascota = new Mascota("Ronny", R.drawable.mascota_ronny, 0);

        ImageView imgPerfil = vista.findViewById(R.id.img_perfil);
        imgPerfil.setImageResource(mascota.getFoto());

        TextView tvNombre = vista.findViewById(R.id.tv_nombre_perfil);
        tvNombre.setText(mascota.getNombre());

        RecyclerView rvFotos = vista.findViewById(R.id.rv_fotos);
        rvFotos.setLayoutManager(new GridLayoutManager(requireContext(), COLUMNAS));
        rvFotos.setAdapter(new FotoAdaptador(crearFotos(mascota)));

        return vista;
    }

    private ArrayList<Foto> crearFotos(Mascota mascota) {
        int[] raitings = {5, 0, 3, 10, 2, 3, 7, 1, 4, 6, 2, 8};

        ArrayList<Foto> fotos = new ArrayList<>();
        for (int raiting : raitings) {
            fotos.add(new Foto(mascota.getFoto(), raiting));
        }
        return fotos;
    }
}
