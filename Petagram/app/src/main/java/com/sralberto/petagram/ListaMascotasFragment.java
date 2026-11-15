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

    private BaseDatosMascotas baseDatos;
    private ArrayList<Mascota> mascotas;
    private MascotaAdaptador adaptador;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragment_lista_mascotas, container, false);

        baseDatos = new BaseDatosMascotas(requireContext());
        mascotas = crearMascotas();
        adaptador = new MascotaAdaptador(mascotas, baseDatos);

        RecyclerView rvMascotas = vista.findViewById(R.id.rv_mascotas);
        rvMascotas.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvMascotas.setAdapter(adaptador);

        return vista;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Se lee al abrir la app y al regresar de favoritas, donde también se pueden dar huesos
        cargarRaitingsGuardados();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        baseDatos.close();
    }

    private void cargarRaitingsGuardados() {
        for (Mascota guardada : baseDatos.obtenerFavoritas()) {
            for (Mascota mascota : mascotas) {
                if (mascota.getId() == guardada.getId()) {
                    mascota.setRaiting(guardada.getRaiting());
                }
            }
        }
        adaptador.notifyDataSetChanged();
    }

    private ArrayList<Mascota> crearMascotas() {
        ArrayList<Mascota> lista = new ArrayList<>();
        lista.add(new Mascota(1, "Catty", R.drawable.mascota_catty, 0));
        lista.add(new Mascota(2, "Ronny", R.drawable.mascota_ronny, 0));
        lista.add(new Mascota(3, "Luna", R.drawable.mascota_luna, 0));
        lista.add(new Mascota(4, "Max", R.drawable.mascota_max, 0));
        lista.add(new Mascota(5, "Kira", R.drawable.mascota_kira, 0));
        lista.add(new Mascota(6, "Toby", R.drawable.mascota_toby, 0));
        return lista;
    }
}
