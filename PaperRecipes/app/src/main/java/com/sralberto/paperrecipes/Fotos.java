package com.sralberto.paperrecipes;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Fotos de las recetas. Se guardan como archivos en la carpeta privada de la app
 * y la base de datos solo guarda su ruta.
 */
public final class Fotos {

    private static final String CARPETA = "fotos";
    // Debe coincidir con android:authorities del FileProvider en el manifiesto
    private static final String AUTORIDAD = ".archivos";

    private Fotos() {
    }

    /** Archivo nuevo y vacío para una foto, por ejemplo para la cámara. */
    public static File nuevoArchivo(Context contexto) {
        File carpeta = new File(contexto.getFilesDir(), CARPETA);
        if (!carpeta.exists()) {
            carpeta.mkdirs();
        }
        return new File(carpeta, "receta_" + System.currentTimeMillis() + ".jpg");
    }

    /** Uri que la app de cámara puede usar para escribir en el archivo. */
    public static Uri uriPara(Context contexto, File archivo) {
        return FileProvider.getUriForFile(contexto, contexto.getPackageName() + AUTORIDAD, archivo);
    }

    /** Copia una foto elegida de la galería a la carpeta de la app. */
    public static File copiar(Context contexto, Uri origen) throws IOException {
        File destino = nuevoArchivo(contexto);
        try (InputStream entrada = contexto.getContentResolver().openInputStream(origen);
             OutputStream salida = new FileOutputStream(destino)) {
            if (entrada == null) {
                throw new IOException("No se pudo abrir la foto");
            }
            byte[] bloque = new byte[8192];
            int leidos;
            while ((leidos = entrada.read(bloque)) != -1) {
                salida.write(bloque, 0, leidos);
            }
        }
        return destino;
    }

    public static boolean existe(String ruta) {
        return ruta != null && new File(ruta).exists();
    }

    public static void borrar(String ruta) {
        if (ruta != null) {
            //noinspection ResultOfMethodCallIgnored
            new File(ruta).delete();
        }
    }

    /**
     * Muestra la foto si existe. Si no, oculta el ImageView para que se vea el ícono de cubiertos.
     * Las esquinas se redondean con Glide para que coincidan con el recuadro.
     */
    public static void mostrar(ImageView imagen, String ruta, float radioArriba, float radioAbajo) {
        if (!existe(ruta)) {
            Glide.with(imagen).clear(imagen);
            imagen.setVisibility(View.GONE);
            return;
        }
        imagen.setVisibility(View.VISIBLE);
        Glide.with(imagen)
                .load(new File(ruta))
                .transform(new CenterCrop(),
                        new GranularRoundedCorners(radioArriba, radioArriba, radioAbajo, radioAbajo))
                .into(imagen);
    }
}
