package com.sralberto.formulariocontacto;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Activity 2. Muestra los datos capturados para que el usuario los confirme.
 */
public class ConfirmarDatosActivity extends AppCompatActivity {
    public static final String EXTRA_NOMBRE = "nombre";
    public static final String EXTRA_FECHA = "fecha";
    public static final String EXTRA_TELEFONO = "telefono";
    public static final String EXTRA_EMAIL = "email";
    public static final String EXTRA_DESCRIPCION = "descripcion";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmar_datos);

        // getIntent() regresa el Intent con el que se abrió esta activity
        Intent intent = getIntent();

        TextView textoNombre = findViewById(R.id.texto_nombre);
        TextView textoFecha = findViewById(R.id.texto_fecha);
        TextView textoTelefono = findViewById(R.id.texto_telefono);
        TextView textoEmail = findViewById(R.id.texto_email);
        TextView textoDescripcion = findViewById(R.id.texto_descripcion);

        textoNombre.setText(intent.getStringExtra(EXTRA_NOMBRE));
        textoFecha.setText(getString(R.string.dato_fecha, intent.getStringExtra(EXTRA_FECHA)));
        textoTelefono.setText(getString(R.string.dato_telefono, intent.getStringExtra(EXTRA_TELEFONO)));
        textoEmail.setText(getString(R.string.dato_email, intent.getStringExtra(EXTRA_EMAIL)));
        textoDescripcion.setText(getString(R.string.dato_descripcion, intent.getStringExtra(EXTRA_DESCRIPCION)));

        // finish() cierra esta activity
        Button botonEditar = findViewById(R.id.boton_editar);
        botonEditar.setOnClickListener(v -> finish());
    }
}
