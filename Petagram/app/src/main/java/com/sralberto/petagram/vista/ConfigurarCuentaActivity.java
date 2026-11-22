package com.sralberto.petagram.vista;

import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.sralberto.petagram.R;
import com.sralberto.petagram.modelo.CuentaPrincipal;
import com.sralberto.petagram.modelo.UsuarioInstagram;
import com.sralberto.petagram.presentador.ConfigurarCuentaPresentador;
import com.sralberto.petagram.presentador.IConfigurarCuentaPresentador;

/**
 * Pantalla para asignar el usuario principal de la app.
 * El perfil muestra las fotos del usuario que se guarde aquí.
 */
public class ConfigurarCuentaActivity extends AppCompatActivity implements IConfigurarCuentaVista {

    private IConfigurarCuentaPresentador presentador;
    private TextInputLayout tilUsuario;
    private TextInputEditText etUsuario;
    private Button btnGuardar;
    private View progreso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configurar_cuenta);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar barra = getSupportActionBar();
        if (barra != null) {
            barra.setDisplayShowTitleEnabled(false);
            barra.setDisplayHomeAsUpEnabled(true);
        }

        tilUsuario = findViewById(R.id.til_usuario);
        etUsuario = findViewById(R.id.et_usuario);
        btnGuardar = findViewById(R.id.btn_guardar_cuenta);
        progreso = findViewById(R.id.progreso);

        btnGuardar.setOnClickListener(v -> guardar());
        etUsuario.setOnEditorActionListener((v, accion, evento) -> {
            if (accion == EditorInfo.IME_ACTION_DONE) {
                guardar();
                return true;
            }
            return false;
        });

        presentador = new ConfigurarCuentaPresentador(this, new CuentaPrincipal(this));
        if (savedInstanceState == null) {
            presentador.mostrarCuentaActual();
        }
    }

    @Override
    protected void onDestroy() {
        presentador.detener();
        super.onDestroy();
    }

    private void guardar() {
        tilUsuario.setError(null);
        CharSequence texto = etUsuario.getText();
        presentador.guardarCuenta(texto == null ? "" : texto.toString());
    }

    @Override
    public void mostrarUsuarioActual(String username) {
        etUsuario.setText(username);
        etUsuario.setSelection(username.length());
    }

    @Override
    public void mostrarCargando(boolean cargando) {
        progreso.setVisibility(cargando ? View.VISIBLE : View.GONE);
        btnGuardar.setEnabled(!cargando);
    }

    @Override
    public void mostrarErrorUsuarioVacio() {
        tilUsuario.setError(getString(R.string.error_usuario_vacio));
    }

    @Override
    public void mostrarUsuarioNoEncontrado(String username) {
        tilUsuario.setError(getString(R.string.error_usuario_no_encontrado, username));
    }

    @Override
    public void mostrarErrorDeConexion() {
        Snackbar.make(findViewById(R.id.raiz_configurar_cuenta), R.string.error_conexion_cuenta,
                Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void cuentaGuardada(UsuarioInstagram usuario) {
        Toast.makeText(this, getString(R.string.cuenta_guardada, usuario.getUsername()),
                Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }
}
