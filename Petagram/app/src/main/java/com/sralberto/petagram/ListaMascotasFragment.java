package com.sralberto.petagram;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ListaMascotasFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragment_lista_mascotas, container, false);

        RecyclerView rvMascotas = vista.findViewById(R.id.rv_mascotas);
        rvMascotas.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvMascotas.setAdapter(new MascotaAdaptador(crearMascotas()));

        return vista;
    }

    private ArrayList<Mascota> crearMascotas() {
        ArrayList<Mascota> mascotas = new ArrayList<>();
        mascotas.add(new Mascota("Catty", R.drawable.mascota_catty, 0));
        mascotas.add(new Mascota("Ronny", R.drawable.mascota_ronny, 0));
        mascotas.add(new Mascota("Luna", R.drawable.mascota_luna, 0));
        mascotas.add(new Mascota("Max", R.drawable.mascota_max, 0));
        mascotas.add(new Mascota("Kira", R.drawable.mascota_kira, 0));
        mascotas.add(new Mascota("Toby", R.drawable.mascota_toby, 0));
        return mascotas;
    }
}
