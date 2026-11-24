package com.sralberto.petagram;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.sralberto.petagram.transiciones.Transiciones;

import java.util.ArrayList;

public class FavoritasActivity extends AppCompatActivity {

    private BaseDatosMascotas baseDatos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Transición en Java. Las favoritas explotan desde el centro al entrar.
        getWindow().setEnterTransition(Transiciones.explotar());
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

    // La flecha de regresar cierra la pantalla con su transición de salida, igual que el botón de atrás
    @Override
    public boolean onSupportNavigateUp() {
        supportFinishAfterTransition();
        return true;
    }
}
