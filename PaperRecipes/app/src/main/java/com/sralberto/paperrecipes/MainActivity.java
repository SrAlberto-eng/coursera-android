package com.sralberto.paperrecipes;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

/**
 * Lista de recetas. En celular abre el detalle en DetalleActivity.
 * En tableta (layout-sw600dp) lo muestra en el panel derecho.
 */
public class MainActivity extends AppCompatActivity implements ListaFragment.AlElegirReceta {

    private boolean dosPaneles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dosPaneles = findViewById(R.id.contenedor_detalle) != null;
        if (dosPaneles && savedInstanceState == null) {
            mostrarEnPanel(0);
        }
    }

    @Override
    public void alElegirReceta(int posicion) {
        if (dosPaneles) {
            mostrarEnPanel(posicion);
        } else {
            Intent intent = new Intent(this, DetalleActivity.class);
            intent.putExtra(DetalleActivity.EXTRA_POSICION, posicion);
            startActivity(intent);
        }
    }

    private void mostrarEnPanel(int posicion) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.contenedor_detalle, DetalleFragment.nuevo(posicion, true))
                .commit();

        Fragment lista = getSupportFragmentManager().findFragmentById(R.id.fragmento_lista);
        if (lista instanceof ListaFragment) {
            ((ListaFragment) lista).seleccionar(posicion);
        }
    }
}
