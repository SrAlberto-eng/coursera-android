package com.sralberto.paperrecipes;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
 * Pantalla de preparación. Ajusta las cantidades de la receta de tres formas:
 * con un factor predefinido, con un multiplicador libre o a partir de lo que se tiene
 * de un ingrediente. Los cálculos están en Escala.
 */
public class PrepararActivity extends AppCompatActivity {

    public static final String EXTRA_ID_RECETA = "id_receta";

    // Mismo orden que los chips de activity_preparar.xml
    private static final int[] CHIPS_FACTOR = {
            R.id.chip_cuarto, R.id.chip_tercio, R.id.chip_mitad,
            R.id.chip_uno, R.id.chip_doble, R.id.chip_triple};
    private static final double[] FACTORES = {0.25, 1.0 / 3.0, 0.5, 1, 2, 3};

    private Receta receta;
    private int ancla = VistasReceta.SIN_ANCLA;
    private int modo;

    private View panelPorciones;
    private View panelMultiplicador;
    private View panelTengo;
    private ChipGroup chipsFactor;
    private EditText etMultiplicador;
    private EditText etTengo;
    private TextView tvEtiquetaTengo;
    private TextView tvRecetaUsa;
    private TextView tvEscala;
    private TextView tvRinde;
    private LinearLayout listaIngredientes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preparar);

        long idReceta = getIntent().getLongExtra(EXTRA_ID_RECETA, Receta.SIN_ID);
        receta = BaseDatosRecetas.obtener(this).obtenerReceta(idReceta);
        if (receta == null) {
            finish();
            return;
        }

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.preparar_titulo, receta.getNombre()));
        toolbar.setNavigationOnClickListener(v -> finish());

        panelPorciones = findViewById(R.id.panel_porciones);
        panelMultiplicador = findViewById(R.id.panel_multiplicador);
        panelTengo = findViewById(R.id.panel_tengo);
        chipsFactor = findViewById(R.id.chips_factor);
        etMultiplicador = findViewById(R.id.et_multiplicador);
        etTengo = findViewById(R.id.et_tengo);
        tvEtiquetaTengo = findViewById(R.id.tv_etiqueta_tengo);
        tvRecetaUsa = findViewById(R.id.tv_receta_usa);
        tvEscala = findViewById(R.id.tv_escala);
        tvRinde = findViewById(R.id.tv_rinde);
        listaIngredientes = findViewById(R.id.lista_ingredientes);

        prepararChipsIngredientes();

        chipsFactor.setOnCheckedStateChangeListener((grupo, elegidos) -> actualizar());
        etMultiplicador.addTextChangedListener(new AlCambiarTexto());
        etTengo.addTextChangedListener(new AlCambiarTexto());

        MaterialButtonToggleGroup grupoModo = findViewById(R.id.grupo_modo);
        grupoModo.addOnButtonCheckedListener((grupo, idBoton, elegido) -> {
            if (elegido) {
                mostrarModo(idBoton);
            }
        });
        mostrarModo(grupoModo.getCheckedButtonId());
    }

    private void mostrarModo(int idBoton) {
        modo = idBoton;
        panelPorciones.setVisibility(idBoton == R.id.btn_modo_porciones ? View.VISIBLE : View.GONE);
        panelMultiplicador.setVisibility(idBoton == R.id.btn_modo_multiplicador ? View.VISIBLE : View.GONE);
        panelTengo.setVisibility(idBoton == R.id.btn_modo_tengo ? View.VISIBLE : View.GONE);
        actualizar();
    }

    /** Calcula el factor del modo activo y vuelve a pintar el resumen y los ingredientes. */
    private void actualizar() {
        double factor;
        int anclaMarcada = VistasReceta.SIN_ANCLA;
        if (modo == R.id.btn_modo_multiplicador) {
            factor = Escala.leerNumero(etMultiplicador.getText().toString());
        } else if (modo == R.id.btn_modo_tengo) {
            factor = factorTengo();
            anclaMarcada = ancla;
        } else {
            factor = factorDelChip(chipsFactor.getCheckedChipId());
        }
        mostrarEscala(factor, anclaMarcada);
    }

    private double factorDelChip(int idChip) {
        for (int i = 0; i < CHIPS_FACTOR.length; i++) {
            if (CHIPS_FACTOR[i] == idChip) {
                return FACTORES[i];
            }
        }
        return Double.NaN;
    }

    private double factorTengo() {
        if (ancla == VistasReceta.SIN_ANCLA) {
            return Double.NaN;
        }
        double base = receta.getIngredientes().get(ancla).getCantidad();
        return Escala.factorDesdeDisponible(base, Escala.leerNumero(etTengo.getText().toString()));
    }

    private void mostrarEscala(double factor, int anclaMarcada) {
        if (Escala.esValido(factor)) {
            tvEscala.setText(getString(R.string.factor, Escala.formatear(factor)));
            double porciones = Escala.porciones(receta.getPorciones(), factor);
            tvRinde.setText(getResources().getQuantityString(R.plurals.porciones,
                    (int) Math.ceil(porciones), Escala.formatear(porciones)));
        } else {
            tvEscala.setText(Escala.SIN_VALOR);
            tvRinde.setText(Escala.SIN_VALOR);
        }
        VistasReceta.llenarIngredientes(listaIngredientes, receta.getIngredientes(), factor, anclaMarcada);
    }

    /**
     * Un chip por cada ingrediente con cantidad. El segundo queda elegido al abrir,
     * igual que la leche en la maqueta, o el primero si la receta solo tiene uno.
     */
    private void prepararChipsIngredientes() {
        ChipGroup chips = findViewById(R.id.chips_ingredientes);
        LayoutInflater inflater = getLayoutInflater();
        List<Ingrediente> ingredientes = receta.getIngredientes();
        int elegido = VistasReceta.SIN_ANCLA;
        for (int i = 0; i < ingredientes.size(); i++) {
            if (!(ingredientes.get(i).getCantidad() > 0)) {
                continue;
            }
            Chip chip = (Chip) inflater.inflate(R.layout.chip_ingrediente, chips, false);
            chip.setId(View.generateViewId());
            chip.setText(ingredientes.get(i).getNombre());
            chip.setTag(i);
            chips.addView(chip);
            if (elegido == VistasReceta.SIN_ANCLA || (i == 1 && elegido == 0)) {
                elegido = i;
                chips.check(chip.getId());
            }
        }
        elegirAncla(elegido);

        chips.setOnCheckedStateChangeListener((grupo, elegidos) -> {
            Chip chip = grupo.findViewById(grupo.getCheckedChipId());
            if (chip != null) {
                elegirAncla((Integer) chip.getTag());
                actualizar();
            }
        });
    }

    /** Cambia el ingrediente de referencia y pone su cantidad base como punto de partida. */
    private void elegirAncla(int indice) {
        ancla = indice;
        if (ancla == VistasReceta.SIN_ANCLA) {
            return;
        }
        Ingrediente referencia = receta.getIngredientes().get(ancla);
        tvEtiquetaTengo.setText(getString(R.string.cantidad_que_tienes, referencia.getUnidad()));
        tvRecetaUsa.setText(getString(R.string.receta_usa, VistasReceta.cantidadConUnidad(referencia, 1)));
        etTengo.setText(Escala.formatear(referencia.getCantidad()));
    }

    /** Recalcula cada vez que cambia el multiplicador o la cantidad disponible. */
    private class AlCambiarTexto implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            actualizar();
        }
    }
}
