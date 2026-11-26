package com.sralberto.paperrecipes;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.util.List;

/**
 * Lista de recetas. En celular abre el detalle en DetalleActivity.
 * En tableta (layout-sw600dp) lo muestra en el panel derecho.
 */
public class MainActivity extends AppCompatActivity
        implements ListaFragment.AlElegirReceta, DetalleFragment.AlBorrarReceta {

    private boolean dosPaneles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dosPaneles = findViewById(R.id.contenedor_detalle) != null;
        if (dosPaneles && savedInstanceState == null) {
            mostrarPrimera();
        }
    }

    @Override
    public void alElegirReceta(long idReceta) {
        if (dosPaneles) {
            mostrarEnPanel(idReceta);
        } else {
            Intent intent = new Intent(this, DetalleActivity.class);
            intent.putExtra(DetalleActivity.EXTRA_ID_RECETA, idReceta);
            startActivity(intent);
        }
    }

    /** Solo pasa en tableta: la receta del panel se borró, así que se muestra otra. */
    @Override
    public void alBorrarReceta() {
        ListaFragment lista = lista();
        if (lista != null) {
            lista.recargar();
        }
        mostrarPrimera();
    }

    private void mostrarPrimera() {
        List<Receta> recetas = BaseDatosRecetas.obtener(this).obtenerRecetas();
        if (!recetas.isEmpty()) {
            mostrarEnPanel(recetas.get(0).getId());
            return;
        }
        Fragment detalle = getSupportFragmentManager().findFragmentById(R.id.contenedor_detalle);
        if (detalle != null) {
            getSupportFragmentManager().beginTransaction().remove(detalle).commit();
        }
    }

    private void mostrarEnPanel(long idReceta) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.contenedor_detalle, DetalleFragment.nuevo(idReceta, true))
                .commit();

        ListaFragment lista = lista();
        if (lista != null) {
            lista.seleccionar(idReceta);
        }
    }

    private ListaFragment lista() {
        Fragment fragmento = getSupportFragmentManager().findFragmentById(R.id.fragmento_lista);
        return fragmento instanceof ListaFragment ? (ListaFragment) fragmento : null;
    }
}
