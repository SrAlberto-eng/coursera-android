package com.sralberto.petagram.notificaciones;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.sralberto.petagram.MainActivity;
import com.sralberto.petagram.R;

import java.util.Map;

/**
 * Recibe los mensajes de Firebase Cloud Messaging cuando la app está abierta.
 * Con la app en segundo plano, Android muestra la notificación por su cuenta
 * y al tocarla abre MainActivity con los datos del mensaje como extras.
 * Esta clase hace lo mismo para que la app se comporte igual en los dos casos.
 */
public class PetagramMessagingService extends FirebaseMessagingService {

    private static final String TAG = "PetagramFCM";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage mensaje) {
        String titulo = getString(R.string.app_name);
        String texto = "";

        RemoteMessage.Notification notificacion = mensaje.getNotification();
        if (notificacion != null) {
            if (notificacion.getTitle() != null) {
                titulo = notificacion.getTitle();
            }
            if (notificacion.getBody() != null) {
                texto = notificacion.getBody();
            }
        }
        mostrarNotificacion(titulo, texto, mensaje.getData());
    }

    // Firebase cambia el token cuando reinstalan la app o borran sus datos.
    // Para volver a registrarlo basta con tocar otra vez Recibir notificaciones.
    @Override
    public void onNewToken(@NonNull String token) {
        Log.d(TAG, "Token nuevo: " + token);
    }

    private void mostrarNotificacion(String titulo, String texto, Map<String, String> datos) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Notificaciones.crearCanal(this);

        // Si la app ya está abierta, MainActivity recibe los datos en onNewIntent
        Intent abrirApp = new Intent(this, MainActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        for (Map.Entry<String, String> dato : datos.entrySet()) {
            abrirApp.putExtra(dato.getKey(), dato.getValue());
        }
        // Cada notificación usa su propio número para que no se mezclen sus datos
        int idNotificacion = (int) System.currentTimeMillis();
        PendingIntent alTocar = PendingIntent.getActivity(this, idNotificacion, abrirApp,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        Notification aviso = new NotificationCompat.Builder(this, getString(R.string.id_canal_notificaciones))
                .setSmallIcon(R.drawable.ic_huella)
                .setColor(ContextCompat.getColor(this, R.color.colorAccent))
                .setContentTitle(titulo)
                .setContentText(texto)
                .setAutoCancel(true)
                .setContentIntent(alTocar)
                .build();
        NotificationManagerCompat.from(this).notify(idNotificacion, aviso);
    }
}
