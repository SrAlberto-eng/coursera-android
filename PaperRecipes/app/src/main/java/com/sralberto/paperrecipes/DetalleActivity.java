package com.sralberto.paperrecipes;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Pantalla de detalle en celular. Solo aloja a DetalleFragment.
 */
public class DetalleActivity extends AppCompatActivity {

    public static final String EXTRA_ID_RECETA = "id_receta";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle);

        if (savedInstanceState == null) {
            long idReceta = getIntent().getLongExtra(EXTRA_ID_RECETA, Receta.SIN_ID);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.contenedor, DetalleFragment.nuevo(idReceta, false))
                    .commit();
        }
    }
}
