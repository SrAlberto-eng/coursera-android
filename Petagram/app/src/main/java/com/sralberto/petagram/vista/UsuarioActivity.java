package com.sralberto.petagram.vista;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.sralberto.petagram.R;
import com.sralberto.petagram.notificaciones.Notificaciones;
import com.sralberto.petagram.vista.fragment.PerfilMascotaFragment;

/**
 * Fotos recientes de la cuenta que dio like a una foto. Se abre con la acción Ver usuario
 * de la notificación, desde el celular o desde el reloj.
 */
public class UsuarioActivity extends AppCompatActivity {

    public static final String EXTRA_USERNAME = "username";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar barra = getSupportActionBar();
        if (barra != null) {
            barra.setDisplayShowTitleEnabled(false);
            barra.setDisplayHomeAsUpEnabled(true);
        }

        String username = getIntent().getStringExtra(EXTRA_USERNAME);
        if (username == null) {
            finish();
            return;
        }
        Notificaciones.cerrar(this, getIntent());

        // Al girar la pantalla el fragment ya existe y no se vuelve a crear
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.contenedor_usuario, PerfilMascotaFragment.deUsuario(username))
                    .commit();
        }
    }
}
