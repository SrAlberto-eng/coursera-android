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
import com.sralberto.petagram.restapi.ConstantesRestApi;
import com.sralberto.petagram.restapi.adaptador.RestApiAdaptador;
import com.sralberto.petagram.restapi.modelo.RelacionResponse;

import java.io.IOException;
import java.util.Map;

import retrofit2.Response;

/**
 * Recibe los mensajes de Firebase Cloud Messaging.
 *
 * El aviso de un like llega como mensaje solo de datos. Así Android siempre llama a onMessageReceived,
 * aunque la app esté en segundo plano, y la app arma la notificación con sus acciones.
 * Un mensaje con notificación de Firebase se muestra con título y texto, sin acciones.
 */
public class PetagramMessagingService extends FirebaseMessagingService {

    private static final String TAG = "PetagramFCM";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage mensaje) {
        AvisoLike aviso = AvisoLike.desdeDatos(mensaje.getData(), (int) System.currentTimeMillis());
        if (aviso != null) {
            if (aviso.conoceQuienDioLike()) {
                aviso.setLoSigue(consultarSiLoSigue(aviso));
            }
            Notificaciones.mostrarLike(this, aviso);
            return;
        }

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

    // Revisa si la cuenta de este celular ya sigue a la que dio el like, para poner Dar follow o Dar unfollow.
    // onMessageReceived corre fuera del hilo principal, por eso aquí se puede esperar la respuesta.
    private boolean consultarSiLoSigue(AvisoLike aviso) {
        try {
            Response<RelacionResponse> respuesta = RestApiAdaptador.obtenerEndpoints()
                    .getRelacion(aviso.getIdQuien(), ConstantesRestApi.accessTokenDe(aviso.getIdDueno()))
                    .execute();
            RelacionResponse cuerpo = respuesta.body();
            return respuesta.isSuccessful() && cuerpo != null && cuerpo.loSigue();
        } catch (IOException | RuntimeException error) {
            // Sin respuesta se ofrece Dar follow. Si ya lo seguía, el servidor lo deja igual.
            Log.w(TAG, "No se pudo consultar el follow", error);
            return false;
        }
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
