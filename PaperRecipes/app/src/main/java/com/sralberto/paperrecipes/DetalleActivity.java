package com.sralberto.paperrecipes;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Pantalla de detalle en celular. Solo aloja a DetalleFragment.
 */
public class DetalleActivity extends AppCompatActivity {

    public static final String EXTRA_POSICION = "posicion";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle);

        if (savedInstanceState == null) {
            int posicion = getIntent().getIntExtra(EXTRA_POSICION, 0);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.contenedor, DetalleFragment.nuevo(posicion, false))
                    .commit();
        }
    }
}
