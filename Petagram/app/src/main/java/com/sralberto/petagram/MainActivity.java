package com.sralberto.petagram;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.sralberto.petagram.vista.ConfigurarCuentaActivity;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 viewPager;

    // Si se guardó una cuenta nueva, se abre el perfil para ver sus fotos
    private final ActivityResultLauncher<Intent> configurarCuenta = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            resultado -> {
                if (resultado.getResultCode() == RESULT_OK) {
                    viewPager.setCurrentItem(PaginasAdaptador.PAGINA_PERFIL);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar barra = getSupportActionBar();
        if (barra != null) {
            // El título lo muestra el TextView centrado de toolbar.xml
            barra.setDisplayShowTitleEnabled(false);
        }

        configurarPestanas();

        FloatingActionButton fabCamara = findViewById(R.id.fab_camara);
        fabCamara.setOnClickListener(v ->
                Snackbar.make(v, R.string.subir_foto_pronto, Snackbar.LENGTH_SHORT).show());
    }

    private void configurarPestanas() {
        viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(new PaginasAdaptador(this));

        TabLayout tabs = findViewById(R.id.tabs);
        new TabLayoutMediator(tabs, viewPager, (pestana, posicion) -> {
            if (posicion == PaginasAdaptador.PAGINA_TIMELINE) {
                pestana.setIcon(R.drawable.ic_inicio);
                pestana.setContentDescription(R.string.pestana_timeline);
            } else if (posicion == PaginasAdaptador.PAGINA_LISTA) {
                pestana.setIcon(R.drawable.ic_huella);
                pestana.setContentDescription(R.string.pestana_lista);
            } else {
                pestana.setIcon(R.drawable.ic_perfil_mascota);
                pestana.setContentDescription(R.string.pestana_perfil);
            }
        }).attach();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_principal, menu);

        // Un item con actionLayout no llama a onOptionsItemSelected.
        // Por eso el clic se escucha directo en su vista.
        View estrella = menu.findItem(R.id.action_favoritas).getActionView();
        if (estrella != null) {
            TextView contador = estrella.findViewById(R.id.tv_contador_favoritas);
            contador.setText(String.valueOf(BaseDatosMascotas.MAXIMO_FAVORITAS));
            estrella.setOnClickListener(v -> abrir(FavoritasActivity.class));
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_contacto) {
            abrir(ContactoActivity.class);
            return true;
        }
        if (id == R.id.action_acerca_de) {
            abrir(AcercaDeActivity.class);
            return true;
        }
        if (id == R.id.action_configurar_cuenta) {
            configurarCuenta.launch(new Intent(this, ConfigurarCuentaActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void abrir(Class<?> pantalla) {
        startActivity(new Intent(this, pantalla));
    }
}
