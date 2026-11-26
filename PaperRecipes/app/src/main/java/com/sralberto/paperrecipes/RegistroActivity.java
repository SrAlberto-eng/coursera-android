package com.sralberto.paperrecipes;

import android.content.ActivityNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Registro de una receta nueva o edición de una existente. Guarda en SQLite al tocar
 * "Guardar receta". La foto se copia a la carpeta privada de la app.
 */
public class RegistroActivity extends AppCompatActivity {

    public static final String EXTRA_ID_RECETA = "id_receta";

    private static final String ESTADO_FOTO = "foto";
    private static final String ESTADO_FOTO_CAMARA = "foto_camara";

    private long idReceta = Receta.SIN_ID;
    private String fotoOriginal;
    // Foto que se ve en pantalla. Puede ser la original o una nueva que todavía no se guarda.
    private String rutaFoto;
    // Archivo que se le pasó a la cámara mientras está abierta
    private String rutaFotoCamara;

    private EditText etNombre;
    private EditText etDescripcion;
    private EditText etPorciones;
    private ImageView ivFoto;
    private LinearLayout contenedorIngredientes;
    private LinearLayout contenedorPasos;

    private final ActivityResultLauncher<PickVisualMediaRequest> elegirFoto =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), this::alElegirFoto);

    private final ActivityResultLauncher<Uri> tomarFoto =
            registerForActivityResult(new ActivityResultContracts.TakePicture(), this::alTomarFoto);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        etNombre = findViewById(R.id.et_nombre);
        etDescripcion = findViewById(R.id.et_descripcion);
        etPorciones = findViewById(R.id.et_porciones);
        ivFoto = findViewById(R.id.iv_foto);
        contenedorIngredientes = findViewById(R.id.contenedor_ingredientes);
        contenedorPasos = findViewById(R.id.contenedor_pasos);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> cancelar());
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                cancelar();
            }
        });

        idReceta = getIntent().getLongExtra(EXTRA_ID_RECETA, Receta.SIN_ID);
        if (idReceta != Receta.SIN_ID) {
            Receta receta = BaseDatosRecetas.obtener(this).obtenerReceta(idReceta);
            if (receta == null) {
                finish();
                return;
            }
            toolbar.setTitle(R.string.editar_receta);
            fotoOriginal = receta.getFoto();
            rutaFoto = fotoOriginal;
            llenar(receta, savedInstanceState == null);
        } else {
            toolbar.setTitle(R.string.nueva_receta);
            agregarIngrediente(null);
            agregarPaso("");
        }

        if (savedInstanceState != null) {
            rutaFoto = savedInstanceState.getString(ESTADO_FOTO, rutaFoto);
            rutaFotoCamara = savedInstanceState.getString(ESTADO_FOTO_CAMARA);
        }
        mostrarFoto();

        findViewById(R.id.btn_galeria).setOnClickListener(v -> elegirFoto.launch(
                new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build()));
        findViewById(R.id.btn_camara).setOnClickListener(v -> abrirCamara());
        findViewById(R.id.btn_agregar_ingrediente).setOnClickListener(v ->
                agregarIngrediente(null).findViewById(R.id.et_ingrediente).requestFocus());
        findViewById(R.id.btn_agregar_paso).setOnClickListener(v ->
                agregarPaso("").findViewById(R.id.et_paso).requestFocus());
        findViewById(R.id.btn_guardar).setOnClickListener(v -> guardar());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ESTADO_FOTO, rutaFoto);
        outState.putString(ESTADO_FOTO_CAMARA, rutaFotoCamara);
    }

    /** Pone los datos de la receta en los campos. Los campos fijos los restaura Android solo. */
    private void llenar(Receta receta, boolean conCamposFijos) {
        if (conCamposFijos) {
            etNombre.setText(receta.getNombre());
            etDescripcion.setText(receta.getDescripcion());
            etPorciones.setText(String.valueOf(receta.getPorciones()));
        }
        for (Ingrediente ingrediente : receta.getIngredientes()) {
            agregarIngrediente(ingrediente);
        }
        if (receta.getIngredientes().isEmpty()) {
            agregarIngrediente(null);
        }
        for (String paso : receta.getPasos()) {
            agregarPaso(paso);
        }
        if (receta.getPasos().isEmpty()) {
            agregarPaso("");
        }
    }

    // ---------- Ingredientes y pasos ----------

    private View agregarIngrediente(Ingrediente ingrediente) {
        View fila = LayoutInflater.from(this)
                .inflate(R.layout.item_editor_ingrediente, contenedorIngredientes, false);
        EditText etIngrediente = fila.findViewById(R.id.et_ingrediente);
        EditText etCantidad = fila.findViewById(R.id.et_cantidad);
        TextView tvUnidad = fila.findViewById(R.id.tv_unidad);
        if (ingrediente != null) {
            etIngrediente.setText(ingrediente.getNombre());
            etCantidad.setText(Escala.formatear(ingrediente.getCantidad()));
            tvUnidad.setText(ingrediente.getUnidad());
        }
        fila.findViewById(R.id.campo_unidad).setOnClickListener(v -> elegirUnidad(tvUnidad));
        fila.findViewById(R.id.btn_quitar_ingrediente).setOnClickListener(v ->
                contenedorIngredientes.removeView(fila));
        contenedorIngredientes.addView(fila);
        return fila;
    }

    private void elegirUnidad(TextView tvUnidad) {
        String[] unidades = getResources().getStringArray(R.array.unidades);
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.elegir_unidad)
                .setItems(unidades, (dialogo, posicion) -> tvUnidad.setText(unidades[posicion]))
                .show();
    }

    private View agregarPaso(String texto) {
        View fila = LayoutInflater.from(this)
                .inflate(R.layout.item_editor_paso, contenedorPasos, false);
        EditText etPaso = fila.findViewById(R.id.et_paso);
        etPaso.setText(texto);
        fila.findViewById(R.id.btn_quitar_paso).setOnClickListener(v -> {
            contenedorPasos.removeView(fila);
            numerarPasos();
        });
        contenedorPasos.addView(fila);
        numerarPasos();
        return fila;
    }

    private void numerarPasos() {
        for (int i = 0; i < contenedorPasos.getChildCount(); i++) {
            View fila = contenedorPasos.getChildAt(i);
            TextView tvNumero = fila.findViewById(R.id.tv_numero);
            TextView tvEtiqueta = fila.findViewById(R.id.tv_etiqueta_paso);
            tvNumero.setText(String.valueOf(i + 1));
            tvEtiqueta.setText(getString(R.string.pasos_etiqueta, i + 1));
        }
    }

    // ---------- Foto ----------

    private void abrirCamara() {
        File archivo = Fotos.nuevoArchivo(this);
        rutaFotoCamara = archivo.getPath();
        try {
            tomarFoto.launch(Fotos.uriPara(this, archivo));
        } catch (ActivityNotFoundException e) {
            rutaFotoCamara = null;
            avisar(R.string.sin_camara);
        }
    }

    private void alTomarFoto(Boolean tomada) {
        if (Boolean.TRUE.equals(tomada) && Fotos.existe(rutaFotoCamara)) {
            reemplazarFoto(rutaFotoCamara);
        } else {
            Fotos.borrar(rutaFotoCamara);
        }
        rutaFotoCamara = null;
    }

    private void alElegirFoto(Uri uri) {
        if (uri == null) {
            return;
        }
        try {
            reemplazarFoto(Fotos.copiar(this, uri).getPath());
        } catch (IOException | SecurityException e) {
            avisar(R.string.error_foto);
        }
    }

    /** Cambia la foto en pantalla. Si la anterior era nueva y no se había guardado, la borra. */
    private void reemplazarFoto(String nueva) {
        if (esFotoSinGuardar(rutaFoto)) {
            Fotos.borrar(rutaFoto);
        }
        rutaFoto = nueva;
        mostrarFoto();
    }

    private void mostrarFoto() {
        float radio = getResources().getDimension(R.dimen.radio_tarjeta);
        Fotos.mostrar(ivFoto, rutaFoto, radio, radio);
    }

    private boolean esFotoSinGuardar(String ruta) {
        return ruta != null && !ruta.equals(fotoOriginal);
    }

    // ---------- Guardar y salir ----------

    private void guardar() {
        List<String[]> filas = new ArrayList<>();
        for (int i = 0; i < contenedorIngredientes.getChildCount(); i++) {
            View fila = contenedorIngredientes.getChildAt(i);
            filas.add(new String[]{
                    texto(fila.findViewById(R.id.et_ingrediente)),
                    texto(fila.findViewById(R.id.et_cantidad)),
                    texto(fila.findViewById(R.id.tv_unidad))});
        }
        List<String> pasos = new ArrayList<>();
        for (int i = 0; i < contenedorPasos.getChildCount(); i++) {
            pasos.add(texto(contenedorPasos.getChildAt(i).findViewById(R.id.et_paso)));
        }

        String nombre = texto(etNombre);
        String porciones = texto(etPorciones);
        switch (ValidadorReceta.validar(nombre, porciones, filas)) {
            case NOMBRE_VACIO:
                avisar(R.string.error_nombre);
                etNombre.requestFocus();
                return;
            case PORCIONES_INVALIDAS:
                avisar(R.string.error_porciones);
                etPorciones.requestFocus();
                return;
            case SIN_INGREDIENTES:
                avisar(R.string.error_sin_ingredientes);
                return;
            case INGREDIENTE_INCOMPLETO:
                avisar(R.string.error_ingrediente);
                return;
            case VALIDA:
            default:
                break;
        }

        Receta receta = ValidadorReceta.construir(idReceta, nombre, texto(etDescripcion),
                porciones, rutaFoto, filas, pasos);
        BaseDatosRecetas.obtener(this).guardarReceta(receta);
        // La foto anterior ya no se usa
        if (fotoOriginal != null && !fotoOriginal.equals(rutaFoto)) {
            Fotos.borrar(fotoOriginal);
        }
        Toast.makeText(this, R.string.receta_guardada, Toast.LENGTH_SHORT).show();
        finish();
    }

    /** Sale sin guardar. Si se eligió una foto nueva, se borra para no dejar archivos sueltos. */
    private void cancelar() {
        if (esFotoSinGuardar(rutaFoto)) {
            Fotos.borrar(rutaFoto);
        }
        finish();
    }

    private void avisar(int mensaje) {
        Snackbar.make(findViewById(R.id.btn_guardar), mensaje, Snackbar.LENGTH_LONG)
                .setAnchorView(R.id.btn_guardar)
                .show();
    }

    private static String texto(TextView campo) {
        return campo.getText().toString();
    }
}
