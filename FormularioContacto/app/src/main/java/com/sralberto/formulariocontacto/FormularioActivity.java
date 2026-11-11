package com.sralberto.formulariocontacto;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.DatePicker;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Locale;

/**
 * Activity 1. Formulario donde el usuario captura los datos del contacto.
 */
public class FormularioActivity extends AppCompatActivity {

    private TextInputLayout tilNombre;
    private TextInputLayout tilTelefono;
    private TextInputLayout tilEmail;
    private TextInputLayout tilDescripcion;

    private TextInputEditText etNombre;
    private TextInputEditText etTelefono;
    private TextInputEditText etEmail;
    private TextInputEditText etDescripcion;

    private DatePicker selectorFecha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario);

        tilNombre = findViewById(R.id.til_nombre);
        tilTelefono = findViewById(R.id.til_telefono);
        tilEmail = findViewById(R.id.til_email);
        tilDescripcion = findViewById(R.id.til_descripcion);

        etNombre = findViewById(R.id.et_nombre);
        etTelefono = findViewById(R.id.et_telefono);
        etEmail = findViewById(R.id.et_email);
        etDescripcion = findViewById(R.id.et_descripcion);

        selectorFecha = findViewById(R.id.selector_fecha);
        // Se valida que la fecha de nacimiento no sea futura
        selectorFecha.setMaxDate(System.currentTimeMillis());

        Button botonSiguiente = findViewById(R.id.boton_siguiente);
        botonSiguiente.setOnClickListener(v -> irAConfirmacion());
    }

    private void irAConfirmacion() {
        if (!validar()) {
            return;
        }

        // El Intent dice a qué activity ir.
        Intent intent = new Intent(this, ConfirmarDatosActivity.class);
        intent.putExtra(ConfirmarDatosActivity.EXTRA_NOMBRE, texto(etNombre));
        intent.putExtra(ConfirmarDatosActivity.EXTRA_FECHA, fechaSeleccionada());
        intent.putExtra(ConfirmarDatosActivity.EXTRA_TELEFONO, texto(etTelefono));
        intent.putExtra(ConfirmarDatosActivity.EXTRA_EMAIL, texto(etEmail));
        intent.putExtra(ConfirmarDatosActivity.EXTRA_DESCRIPCION, texto(etDescripcion));
        startActivity(intent);

        // Aquí no se llama a finish(). Esta activity se queda en la pila con los campos del formulario llenos
    }

    /** Fecha del DatePicker en formato dd/mm/aaaa. */
    private String fechaSeleccionada() {
        int dia = selectorFecha.getDayOfMonth();
        int mes = selectorFecha.getMonth() + 1;
        int year = selectorFecha.getYear();
        return String.format(Locale.getDefault(), "%02d/%02d/%d", dia, mes, year);
    }

    private boolean validar() {
        boolean nombreOk = validarRequerido(tilNombre, etNombre, R.string.error_nombre);
        boolean telefonoOk = validarRequerido(tilTelefono, etTelefono, R.string.error_telefono);
        boolean descripcionOk = validarRequerido(tilDescripcion, etDescripcion, R.string.error_descripcion);

        boolean emailOk = Patterns.EMAIL_ADDRESS.matcher(texto(etEmail)).matches();
        tilEmail.setError(emailOk ? null : getString(R.string.error_email));

        return nombreOk && telefonoOk && emailOk && descripcionOk;
    }

    private boolean validarRequerido(TextInputLayout campo, TextInputEditText entrada, int mensajeError) {
        boolean lleno = !texto(entrada).isEmpty();
        // setError muestra el mensaje debajo del campo.
        campo.setError(lleno ? null : getString(mensajeError));
        return lleno;
    }

    private static String texto(TextInputEditText entrada) {
        return entrada.getText() == null ? "" : entrada.getText().toString().trim();
    }
}
