package com.sralberto.petagram;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;

public class AcercaDeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Su transición no está aquí. La pone el tema Theme.Petagram.AcercaDe desde el manifest.
        setContentView(R.layout.activity_acerca_de);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar barra = getSupportActionBar();
        if (barra != null) {
            barra.setDisplayShowTitleEnabled(false);
            barra.setDisplayHomeAsUpEnabled(true);
        }
    }

    // La flecha de regresar cierra la pantalla con su transición de salida, igual que el botón de atrás
    @Override
    public boolean onSupportNavigateUp() {
        supportFinishAfterTransition();
        return true;
    }
}
