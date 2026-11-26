package com.sralberto.paperrecipes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.List;

/**
 * Pantalla de preparación con sus tres modos. En esta entrega cada modo muestra el ejemplo
 * de la maquetación con un factor fijo. El cálculo con lo que escribe el usuario llega después.
 */
public class PrepararActivity extends AppCompatActivity {

    public static final String EXTRA_POSICION = "posicion";

    // Factores de ejemplo de las pantallas 05 y 06 de la maqueta
    private static final double FACTOR_PORCIONES = 2;
    private static final double FACTOR_MULTIPLICADOR = 1.5;
    private static final double FACTOR_TENGO = 1.5;
    // En la maqueta el ingrediente de referencia es la leche, el segundo de los hotcakes
    private static final int ANCLA_EJEMPLO = 1;

    private Receta receta;
    private int ancla;
    private View panelPorciones;
    private View panelMultiplicador;
    private View panelTengo;
    private TextView tvEscala;
    private TextView tvRinde;
    private LinearLayout listaIngredientes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preparar);

        receta = Recetas.LISTA.get(getIntent().getIntExtra(EXTRA_POSICION, 0));
        ancla = Math.min(ANCLA_EJEMPLO, receta.getIngredientes().size() - 1);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.preparar_titulo, receta.getNombre()));
        toolbar.setNavigationOnClickListener(v -> finish());

        panelPorciones = findViewById(R.id.panel_porciones);
        panelMultiplicador = findViewById(R.id.panel_multiplicador);
        panelTengo = findViewById(R.id.panel_tengo);
        tvEscala = findViewById(R.id.tv_escala);
        tvRinde = findViewById(R.id.tv_rinde);
        listaIngredientes = findViewById(R.id.lista_ingredientes);

        prepararPanelTengo();

        MaterialButtonToggleGroup grupoModo = findViewById(R.id.grupo_modo);
        grupoModo.addOnButtonCheckedListener((grupo, idBoton, elegido) -> {
            if (elegido) {
                mostrarModo(idBoton);
            }
        });
        mostrarModo(grupoModo.getCheckedButtonId());
    }

    /** Muestra el panel del modo elegido y su ejemplo de escala. */
    private void mostrarModo(int idBoton) {
        panelPorciones.setVisibility(idBoton == R.id.btn_modo_porciones ? View.VISIBLE : View.GONE);
        panelMultiplicador.setVisibility(idBoton == R.id.btn_modo_multiplicador ? View.VISIBLE : View.GONE);
        panelTengo.setVisibility(idBoton == R.id.btn_modo_tengo ? View.VISIBLE : View.GONE);

        if (idBoton == R.id.btn_modo_multiplicador) {
            mostrarEscala(FACTOR_MULTIPLICADOR, VistasReceta.SIN_ANCLA);
        } else if (idBoton == R.id.btn_modo_tengo) {
            mostrarEscala(FACTOR_TENGO, ancla);
        } else {
            mostrarEscala(FACTOR_PORCIONES, VistasReceta.SIN_ANCLA);
        }
    }

    private void mostrarEscala(double factor, int anclaMarcada) {
        tvEscala.setText(getString(R.string.factor, VistasReceta.formatear(factor)));
        double porciones = receta.getPorciones() * factor;
        tvRinde.setText(getResources().getQuantityString(R.plurals.porciones,
                (int) Math.ceil(porciones), VistasReceta.formatear(porciones)));
        VistasReceta.llenarIngredientes(listaIngredientes, receta.getIngredientes(), factor, anclaMarcada);
    }

    /** Un chip por ingrediente y el ejemplo de cantidad disponible del ingrediente de referencia. */
    private void prepararPanelTengo() {
        ChipGroup chips = findViewById(R.id.chips_ingredientes);
        LayoutInflater inflater = getLayoutInflater();
        List<Ingrediente> ingredientes = receta.getIngredientes();
        for (int i = 0; i < ingredientes.size(); i++) {
            Chip chip = (Chip) inflater.inflate(R.layout.chip_ingrediente, chips, false);
            chip.setId(View.generateViewId());
            chip.setText(ingredientes.get(i).getNombre());
            chips.addView(chip);
            if (i == ancla) {
                chips.check(chip.getId());
            }
        }

        Ingrediente referencia = ingredientes.get(ancla);
        TextView tvEtiqueta = findViewById(R.id.tv_etiqueta_tengo);
        EditText etTengo = findViewById(R.id.et_tengo);
        TextView tvRecetaUsa = findViewById(R.id.tv_receta_usa);
        tvEtiqueta.setText(getString(R.string.cantidad_que_tienes, referencia.getUnidad()));
        etTengo.setText(VistasReceta.formatear(referencia.getCantidad() * FACTOR_TENGO));
        tvRecetaUsa.setText(getString(R.string.receta_usa, VistasReceta.cantidadConUnidad(referencia, 1)));
    }
}
