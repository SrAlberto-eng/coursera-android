package com.sralberto.petagram;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;

public class FavoritasActivity extends AppCompatActivity {

    private BaseDatosMascotas baseDatos;

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

        baseDatos = new BaseDatosMascotas(this);
        ArrayList<Mascota> favoritas = baseDatos.obtenerFavoritas();

        RecyclerView rvFavoritas = findViewById(R.id.rv_favoritas);
        rvFavoritas.setLayoutManager(new LinearLayoutManager(this));
        rvFavoritas.setAdapter(new MascotaAdaptador(favoritas, baseDatos));

        TextView tvSinFavoritas = findViewById(R.id.tv_sin_favoritas);
        tvSinFavoritas.setVisibility(favoritas.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onDestroy() {
        baseDatos.close();
        super.onDestroy();
    }
}
