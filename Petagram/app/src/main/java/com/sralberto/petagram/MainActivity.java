package com.sralberto.petagram;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Mascota> mascotas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // La Toolbar del layout funciona como la barra de acción de la activity
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar barra = getSupportActionBar();
        if (barra != null) {
            // El título lo muestra el TextView centrado de toolbar.xml
            barra.setDisplayShowTitleEnabled(false);
        }

        inicializarListaMascotas();

        RecyclerView rvMascotas = findViewById(R.id.rv_mascotas);
        // El LayoutManager decide cómo se acomodan las tarjetas. Aquí, una debajo de otra.
        rvMascotas.setLayoutManager(new LinearLayoutManager(this));
        rvMascotas.setAdapter(new MascotaAdaptador(mascotas));

        FloatingActionButton fabCamara = findViewById(R.id.fab_camara);
        fabCamara.setOnClickListener(v ->
                Snackbar.make(v, R.string.subir_foto_pronto, Snackbar.LENGTH_SHORT).show());
    }

    /** DataSet: las mascotas que muestra la lista. Todas empiezan sin huesos. */
    private void inicializarListaMascotas() {
        mascotas = new ArrayList<>();
        mascotas.add(new Mascota("Catty", R.drawable.mascota_catty, 0));
        mascotas.add(new Mascota("Ronny", R.drawable.mascota_ronny, 0));
        mascotas.add(new Mascota("Luna", R.drawable.mascota_luna, 0));
        mascotas.add(new Mascota("Max", R.drawable.mascota_max, 0));
        mascotas.add(new Mascota("Kira", R.drawable.mascota_kira, 0));
        mascotas.add(new Mascota("Toby", R.drawable.mascota_toby, 0));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_principal, menu);

        // Un item con actionLayout no llama a onOptionsItemSelected.
        // Por eso el clic se escucha directo en su vista.
        View estrella = menu.findItem(R.id.action_favoritas).getActionView();
        if (estrella != null) {
            TextView contador = estrella.findViewById(R.id.tv_contador_favoritas);
            contador.setText(String.valueOf(FavoritasActivity.TOTAL_FAVORITAS));
            estrella.setOnClickListener(v -> abrirFavoritas());
        }
        return true;
    }

    private void abrirFavoritas() {
        startActivity(new Intent(this, FavoritasActivity.class));
    }
}
