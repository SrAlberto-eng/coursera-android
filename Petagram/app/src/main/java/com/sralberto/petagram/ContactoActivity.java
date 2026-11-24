package com.sralberto.petagram;

import android.os.Bundle;
import android.transition.TransitionInflater;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import javax.mail.MessagingException;

public class ContactoActivity extends AppCompatActivity {

    private static final String TAG = "ContactoActivity";

    private TextInputLayout tilNombre;
    private TextInputLayout tilCorreo;
    private TextInputLayout tilMensaje;

    private TextInputEditText etNombre;
    private TextInputEditText etCorreo;
    private TextInputEditText etMensaje;

    private Button btnEnviar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Transición en XML, en res/transition/deslizar_derecha.xml
        getWindow().setEnterTransition(
                TransitionInflater.from(this).inflateTransition(R.transition.deslizar_derecha));
        setContentView(R.layout.activity_contacto);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar barra = getSupportActionBar();
        if (barra != null) {
            barra.setDisplayShowTitleEnabled(false);
            barra.setDisplayHomeAsUpEnabled(true);
        }

        tilNombre = findViewById(R.id.til_nombre);
        tilCorreo = findViewById(R.id.til_correo);
        tilMensaje = findViewById(R.id.til_mensaje);

        etNombre = findViewById(R.id.et_nombre);
        etCorreo = findViewById(R.id.et_correo);
        etMensaje = findViewById(R.id.et_mensaje);

        btnEnviar = findViewById(R.id.btn_enviar);
        btnEnviar.setOnClickListener(v -> enviarComentario());
    }

    private void enviarComentario() {
        if (!formularioValido()) {
            return;
        }
        if (!CorreoContacto.estaConfigurado()) {
            Toast.makeText(this, R.string.correo_sin_configurar, Toast.LENGTH_LONG).show();
            return;
        }

        String nombre = texto(etNombre);
        String correo = texto(etCorreo);
        String mensaje = texto(etMensaje);

        btnEnviar.setEnabled(false);
        btnEnviar.setText(R.string.enviando);

        // Android no deja usar internet en el hilo principal, así que el envío va en otro hilo
        new Thread(() -> {
            try {
                CorreoContacto.enviar(nombre, correo, mensaje);
                runOnUiThread(this::comentarioEnviado);
            } catch (MessagingException e) {
                Log.e(TAG, "No se pudo enviar el correo", e);
                runOnUiThread(this::errorAlEnviar);
            }
        }).start();
    }

    private void comentarioEnviado() {
        Toast.makeText(this, R.string.comentario_enviado, Toast.LENGTH_LONG).show();
        supportFinishAfterTransition();
    }

    private void errorAlEnviar() {
        btnEnviar.setEnabled(true);
        btnEnviar.setText(R.string.enviar_comentario);
        Toast.makeText(this, R.string.error_envio, Toast.LENGTH_LONG).show();
    }

    private boolean formularioValido() {
        boolean nombreValido = validarRequerido(tilNombre, etNombre, R.string.error_nombre);
        boolean mensajeValido = validarRequerido(tilMensaje, etMensaje, R.string.error_mensaje);

        boolean correoValido = Patterns.EMAIL_ADDRESS.matcher(texto(etCorreo)).matches();
        tilCorreo.setError(correoValido ? null : getString(R.string.error_correo));

        return nombreValido && correoValido && mensajeValido;
    }

    private boolean validarRequerido(TextInputLayout campo, TextInputEditText entrada, @StringRes int error) {
        boolean lleno = !texto(entrada).isEmpty();
        campo.setError(lleno ? null : getString(error));
        return lleno;
    }

    private static String texto(TextInputEditText entrada) {
        return entrada.getText() == null ? "" : entrada.getText().toString().trim();
    }

    // La flecha de regresar cierra la pantalla con su transición de salida, igual que el botón de atrás
    @Override
    public boolean onSupportNavigateUp() {
        supportFinishAfterTransition();
        return true;
    }
}
