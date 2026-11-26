package com.sralberto.paperrecipes;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;

/**
 * Registro de una receta nueva. Si llega una posición, muestra la receta para editarla.
 * En esta entrega todavía no guarda: el botón de guardar solo cierra la pantalla.
 */
public class RegistroActivity extends AppCompatActivity {

    public static final String EXTRA_POSICION = "posicion";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        int posicion = getIntent().getIntExtra(EXTRA_POSICION, -1);
        if (posicion >= 0) {
            toolbar.setTitle(R.string.editar_receta);
            if (savedInstanceState == null) {
                llenar(Recetas.LISTA.get(posicion));
            }
        } else {
            toolbar.setTitle(R.string.nueva_receta);
        }

        findViewById(R.id.btn_guardar).setOnClickListener(v -> finish());
    }

    private void llenar(Receta receta) {
        EditText etNombre = findViewById(R.id.et_nombre);
        EditText etDescripcion = findViewById(R.id.et_descripcion);
        EditText etPorciones = findViewById(R.id.et_porciones);
        etNombre.setText(receta.getNombre());
        etDescripcion.setText(receta.getDescripcion());
        etPorciones.setText(String.valueOf(receta.getPorciones()));

        Ingrediente primero = receta.getIngredientes().get(0);
        EditText etIngrediente = findViewById(R.id.et_ingrediente);
        EditText etCantidad = findViewById(R.id.et_cantidad);
        TextView tvUnidad = findViewById(R.id.tv_unidad);
        etIngrediente.setText(primero.getNombre());
        etCantidad.setText(VistasReceta.formatear(primero.getCantidad()));
        tvUnidad.setText(primero.getUnidad());
    }
}
