package com.sralberto.lugarestijuana;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.appbar.MaterialToolbar;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.CopyrightOverlay;
import org.osmdroid.views.overlay.Marker;

import java.io.File;

/**
 * Muestra en un mapa de OpenStreetMap la ubicación del lugar elegido con un puntero personalizado.
 */
public class MapaActivity extends AppCompatActivity {

    public static final String EXTRA_POSICION = "posicion";

    private static final double ZOOM = 17.0;
    // Medidas del puntero en dp. El círculo de la ilustración va dentro de la cabeza del pin.
    private static final int ANCHO_PUNTERO = 48;
    private static final int ALTO_PUNTERO = 62;
    private static final int LADO_ILUSTRACION = 32;
    private static final int MARGEN_ILUSTRACION = 8;

    private Lugar lugar;
    private MapView mapa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // osmdroid se configura antes de inflar el MapView
        configurarOsmdroid();
        setContentView(R.layout.activity_mapa);

        int posicion = getIntent().getIntExtra(EXTRA_POSICION, -1);
        if (posicion < 0 || posicion >= Lugares.LISTA.size()) {
            finish();
            return;
        }
        lugar = Lugares.LISTA.get(posicion);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar barra = getSupportActionBar();
        if (barra != null) {
            barra.setDisplayShowTitleEnabled(false);
            barra.setDisplayHomeAsUpEnabled(true);
        }
        TextView tvTitulo = findViewById(R.id.tv_titulo);
        tvTitulo.setText(lugar.getNombre());

        mapa = findViewById(R.id.mapa);
        mostrarLugar();
    }

    private void configurarOsmdroid() {
        Configuration.getInstance().load(this, getSharedPreferences("osmdroid", Context.MODE_PRIVATE));
        // Los servidores de OpenStreetMap piden que cada app se identifique
        Configuration.getInstance().setUserAgentValue(getPackageName());
        // Los mosaicos se guardan en la caché de la app, así no hace falta permiso de almacenamiento
        File carpeta = new File(getCacheDir(), "osmdroid");
        Configuration.getInstance().setOsmdroidBasePath(carpeta);
        Configuration.getInstance().setOsmdroidTileCache(new File(carpeta, "mosaicos"));
    }

    private void mostrarLugar() {
        GeoPoint ubicacion = new GeoPoint(lugar.getLatitud(), lugar.getLongitud());

        mapa.setTileSource(TileSourceFactory.MAPNIK);
        mapa.setMultiTouchControls(true);
        IMapController control = mapa.getController();
        control.setZoom(ZOOM);
        control.setCenter(ubicacion);

        Marker marcador = new Marker(mapa);
        marcador.setPosition(ubicacion);
        marcador.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marcador.setIcon(crearPuntero());
        marcador.setTitle(getString(lugar.getNombre()));
        marcador.setSnippet(getString(lugar.getDescripcion()));
        mapa.getOverlays().add(marcador);
        // Crédito que pide OpenStreetMap por usar sus mapas
        mapa.getOverlays().add(new CopyrightOverlay(this));
        marcador.showInfoWindow();
    }

    /**
     * Pinta ic_puntero con el color del lugar y le pone la ilustración del lugar dentro de un círculo.
     */
    private Drawable crearPuntero() {
        int ancho = dp(ANCHO_PUNTERO);
        int alto = dp(ALTO_PUNTERO);
        int lado = dp(LADO_ILUSTRACION);
        int x = (ancho - lado) / 2;
        int y = dp(MARGEN_ILUSTRACION);
        float centroX = ancho / 2f;
        float centroY = y + lado / 2f;

        Bitmap bitmap = Bitmap.createBitmap(ancho, alto, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Drawable pin = ContextCompat.getDrawable(this, R.drawable.ic_puntero).mutate();
        pin.setTint(ContextCompat.getColor(this, lugar.getColor()));
        pin.setBounds(0, 0, ancho, alto);
        pin.draw(canvas);

        Paint blanco = new Paint(Paint.ANTI_ALIAS_FLAG);
        blanco.setColor(ContextCompat.getColor(this, R.color.blanco));
        canvas.drawCircle(centroX, centroY, lado / 2f + dp(2), blanco);

        Path circulo = new Path();
        circulo.addCircle(centroX, centroY, lado / 2f, Path.Direction.CW);
        canvas.save();
        canvas.clipPath(circulo);
        Drawable ilustracion = ContextCompat.getDrawable(this, lugar.getIlustracion());
        ilustracion.setBounds(x, y, x + lado, y + lado);
        ilustracion.draw(canvas);
        canvas.restore();

        return new BitmapDrawable(getResources(), bitmap);
    }

    private int dp(int valor) {
        return Math.round(valor * getResources().getDisplayMetrics().density);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mapa != null) {
            mapa.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mapa != null) {
            mapa.onPause();
        }
    }
}
