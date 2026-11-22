package com.sralberto.lugarestijuana;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;

/**
 * Menú con los 4 lugares. Al tocar uno se abre MapaActivity.
 */
public class MainActivity extends AppCompatActivity {

    private static final int COLUMNAS = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar barra = getSupportActionBar();
        if (barra != null) {
            barra.setDisplayShowTitleEnabled(false);
        }
        TextView tvTitulo = findViewById(R.id.tv_titulo);
        tvTitulo.setText(R.string.app_name);

        RecyclerView rvLugares = findViewById(R.id.rv_lugares);
        rvLugares.setLayoutManager(new GridLayoutManager(this, COLUMNAS));
        rvLugares.setAdapter(new LugarAdaptador(Lugares.LISTA, this::abrirMapa));
    }

    private void abrirMapa(int posicion) {
        Intent intent = new Intent(this, MapaActivity.class);
        intent.putExtra(MapaActivity.EXTRA_POSICION, posicion);
        startActivity(intent);
    }
}
