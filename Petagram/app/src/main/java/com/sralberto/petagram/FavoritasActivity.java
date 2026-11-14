package com.sralberto.petagram;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;

public class FavoritasActivity extends AppCompatActivity {

    public static final int TOTAL_FAVORITAS = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favoritas);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar barra = getSupportActionBar();
        if (barra != null) {
            barra.setDisplayShowTitleEnabled(false);
            // La flecha regresa a la pantalla que dice parentActivityName en el manifest
            barra.setDisplayHomeAsUpEnabled(true);
        }

        RecyclerView rvFavoritas = findViewById(R.id.rv_favoritas);
        rvFavoritas.setLayoutManager(new LinearLayoutManager(this));
        rvFavoritas.setAdapter(new MascotaAdaptador(crearFavoritas()));
    }

    // Datos fijos mientras la app no guarda cuáles mascotas te gustaron
    private ArrayList<Mascota> crearFavoritas() {
        ArrayList<Mascota> favoritas = new ArrayList<>();
        favoritas.add(new Mascota("Ronny", R.drawable.mascota_ronny, 3));
        favoritas.add(new Mascota("Catty", R.drawable.mascota_catty, 5));
        favoritas.add(new Mascota("Kira", R.drawable.mascota_kira, 2));
        favoritas.add(new Mascota("Toby", R.drawable.mascota_toby, 4));
        favoritas.add(new Mascota("Luna", R.drawable.mascota_luna, 1));
        return favoritas;
    }
}
