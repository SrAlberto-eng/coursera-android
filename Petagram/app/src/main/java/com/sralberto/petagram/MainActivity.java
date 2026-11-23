package com.sralberto.petagram;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.FirebaseApp;
import com.sralberto.petagram.modelo.CuentaPrincipal;
import com.sralberto.petagram.modelo.UsuarioInstagram;
import com.sralberto.petagram.notificaciones.Notificaciones;
import com.sralberto.petagram.presentador.INotificacionesPresentador;
import com.sralberto.petagram.presentador.NotificacionesPresentador;
import com.sralberto.petagram.vista.ConfigurarCuentaActivity;
import com.sralberto.petagram.vista.INotificacionesVista;
import com.sralberto.petagram.vista.fragment.PerfilMascotaFragment;

public class MainActivity extends AppCompatActivity implements INotificacionesVista {

    private ViewPager2 viewPager;
    private INotificacionesPresentador presentadorNotificaciones;
    private Snackbar avisoRegistrando;

    // Si se guardó una cuenta nueva, se abre el perfil para ver sus fotos
    private final ActivityResultLauncher<Intent> configurarCuenta = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            resultado -> {
                if (resultado.getResultCode() == RESULT_OK) {
                    viewPager.setCurrentItem(PaginasAdaptador.PAGINA_PERFIL);
                }
            });

    // Android 13 o mayor pide permiso para mostrar notificaciones.
    // El token se registra aunque lo nieguen. El aviso final dice cómo activarlas.
    private final ActivityResultLauncher<String> permisoNotificaciones = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            concedido -> presentadorNotificaciones.registrarDispositivo());

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

        Notificaciones.crearCanal(this);
        presentadorNotificaciones = new NotificacionesPresentador(this, new CuentaPrincipal(this));

        // Al girar la pantalla el intent es el mismo y no se debe volver a mover de pestaña
        if (savedInstanceState == null) {
            abrirPantallaDeNotificacion(getIntent(), false);
        }
    }

    // La app ya estaba abierta y se tocó una notificación
    @Override
    public void onNewIntent(@NonNull Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        abrirPantallaDeNotificacion(intent, true);
    }

    // La notificación de un like trae pantalla=perfil y abre la pestaña del perfil
    private void abrirPantallaDeNotificacion(Intent intent, boolean yaEstabaAbierta) {
        if (!Notificaciones.PANTALLA_PERFIL.equals(intent.getStringExtra(Notificaciones.EXTRA_PANTALLA))) {
            return;
        }
        intent.removeExtra(Notificaciones.EXTRA_PANTALLA);
        viewPager.setCurrentItem(PaginasAdaptador.PAGINA_PERFIL, false);
        if (yaEstabaAbierta) {
            // El perfil pudo cargarse antes del like. Se recarga para mostrar el hueso nuevo.
            getSupportFragmentManager().setFragmentResult(PerfilMascotaFragment.RECARGAR, new Bundle());
        }
    }

    @Override
    protected void onDestroy() {
        presentadorNotificaciones.detener();
        super.onDestroy();
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
        if (id == R.id.action_recibir_notificaciones) {
            recibirNotificaciones();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void recibirNotificaciones() {
        if (FirebaseApp.getApps(this).isEmpty()) {
            mostrarError(R.string.firebase_sin_configurar);
            return;
        }
        boolean pidePermiso = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED;
        if (pidePermiso) {
            permisoNotificaciones.launch(Manifest.permission.POST_NOTIFICATIONS);
        } else {
            presentadorNotificaciones.registrarDispositivo();
        }
    }

    private void abrir(Class<?> pantalla) {
        startActivity(new Intent(this, pantalla));
    }

    @Override
    public void mostrarRegistrando(boolean registrando) {
        if (registrando) {
            avisoRegistrando = Snackbar.make(viewPager, R.string.registrando_dispositivo,
                    Snackbar.LENGTH_INDEFINITE);
            avisoRegistrando.show();
        } else if (avisoRegistrando != null) {
            avisoRegistrando.dismiss();
            avisoRegistrando = null;
        }
    }

    @Override
    public void mostrarRegistroExitoso(UsuarioInstagram usuario, String token) {
        StringBuilder mensaje = new StringBuilder(
                getString(R.string.registro_exitoso, usuario.getUsername()));
        if (!NotificationManagerCompat.from(this).areNotificationsEnabled()) {
            mensaje.append("\n\n").append(getString(R.string.notificaciones_desactivadas));
        }
        mensaje.append("\n\n").append(getString(R.string.token_del_dispositivo)).append('\n').append(token);

        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.notificaciones_activadas)
                .setMessage(mensaje)
                .setPositiveButton(R.string.aceptar, null)
                .setNeutralButton(R.string.copiar_token, (dialogo, boton) -> copiarToken(token))
                .show();
    }

    private void copiarToken(String token) {
        ClipboardManager portapapeles = ContextCompat.getSystemService(this, ClipboardManager.class);
        if (portapapeles != null) {
            portapapeles.setPrimaryClip(ClipData.newPlainText(getString(R.string.token_del_dispositivo), token));
            Toast.makeText(this, R.string.token_copiado, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void mostrarUsuarioNoEncontrado(String username) {
        Snackbar.make(viewPager, getString(R.string.error_registro_usuario, username),
                Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void mostrarErrorToken() {
        mostrarError(R.string.error_token);
    }

    @Override
    public void mostrarErrorServidor() {
        mostrarError(R.string.error_servidor);
    }

    @Override
    public void mostrarErrorDeConexion() {
        mostrarError(R.string.error_conexion_registro);
    }

    private void mostrarError(@StringRes int mensaje) {
        Snackbar.make(viewPager, mensaje, Snackbar.LENGTH_LONG).show();
    }
}
