package com.sralberto.paperrecipes;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Muestra el logotipo un momento y abre la lista de recetas.
 */
public class InicioActivity extends AppCompatActivity {

    private static final long DURACION_MS = 1500;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable abrirLista = () -> {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);
        handler.postDelayed(abrirLista, DURACION_MS);
    }

    @Override
    protected void onDestroy() {
        // Si el usuario sale antes de tiempo, no se abre la lista
        handler.removeCallbacks(abrirLista);
        super.onDestroy();
    }
}
