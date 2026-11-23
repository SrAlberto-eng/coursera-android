package com.sralberto.petagram.notificaciones;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;

import com.sralberto.petagram.R;

/**
 * Canal de notificaciones de Petagram. Android 8 o mayor lo pide para mostrar cualquier notificación.
 */
public final class Notificaciones {

    private Notificaciones() {
    }

    public static void crearCanal(Context contexto) {
        NotificationChannel canal = new NotificationChannel(
                contexto.getString(R.string.id_canal_notificaciones),
                contexto.getString(R.string.canal_notificaciones),
                NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager manager = contexto.getSystemService(NotificationManager.class);
        if (manager != null) {
            manager.createNotificationChannel(canal);
        }
    }
}
