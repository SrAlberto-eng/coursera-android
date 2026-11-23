package com.sralberto.petagram.notificaciones;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.StringRes;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.core.content.ContextCompat;

import com.sralberto.petagram.MainActivity;
import com.sralberto.petagram.R;
import com.sralberto.petagram.restapi.ConstantesRestApi;
import com.sralberto.petagram.vista.UsuarioActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Canal y notificaciones de Petagram. Android 8 o mayor pide un canal para mostrar cualquier notificación.
 */
public final class Notificaciones {

    // La notificación de un like abre MainActivity con pantalla=perfil
    public static final String EXTRA_PANTALLA = "pantalla";
    public static final String PANTALLA_PERFIL = "perfil";

    // Posición de cada acción, para darle a su PendingIntent un requestCode distinto
    private static final int ACCION_VER_MI_PERFIL = 0;
    private static final int ACCION_FOLLOW = 1;
    private static final int ACCION_VER_USUARIO = 2;

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

    /**
     * Notificación de un like con tres acciones: Ver mi perfil, Dar follow o Dar unfollow y Ver usuario.
     * Las dos últimas solo aparecen si se sabe qué cuenta dio el like.
     * Si ya existe una notificación con el mismo id, la reemplaza sin volver a sonar.
     */
    public static void mostrarLike(Context contexto, AvisoLike aviso) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && ContextCompat.checkSelfPermission(contexto, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        crearCanal(contexto);

        String texto = aviso.conoceQuienDioLike()
                ? contexto.getString(R.string.like_texto_de, aviso.getUsernameQuien())
                : contexto.getString(R.string.like_texto);
        if (aviso.getEstado() != null) {
            texto = texto + " " + aviso.getEstado();
        }

        PendingIntent verMiPerfil = abrirPerfil(contexto, aviso);
        List<NotificationCompat.Action> acciones = new ArrayList<>();
        acciones.add(accion(contexto, R.drawable.ic_accion_perfil, R.string.accion_ver_mi_perfil, verMiPerfil));
        if (aviso.conoceQuienDioLike()) {
            int textoFollow = aviso.isLoSigue() ? R.string.accion_dar_unfollow : R.string.accion_dar_follow;
            acciones.add(accion(contexto, R.drawable.ic_accion_seguir, textoFollow, cambiarFollow(contexto, aviso)));
            acciones.add(accion(contexto, R.drawable.ic_accion_usuario, R.string.accion_ver_usuario,
                    abrirUsuario(contexto, aviso)));
        }

        NotificationCompat.Builder constructor = new NotificationCompat.Builder(contexto,
                contexto.getString(R.string.id_canal_notificaciones))
                .setSmallIcon(R.drawable.ic_huella)
                .setColor(ContextCompat.getColor(contexto, R.color.colorAccent))
                .setContentTitle(contexto.getString(R.string.like_titulo))
                .setContentText(texto)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(texto))
                .setCategory(NotificationCompat.CATEGORY_SOCIAL)
                .setAutoCancel(true)
                // Al actualizarla después de un follow ya no vuelve a sonar ni a vibrar
                .setOnlyAlertOnce(true)
                .setContentIntent(verMiPerfil);
        for (NotificationCompat.Action accion : acciones) {
            constructor.addAction(accion);
        }
        // En un reloj con Wear OS las mismas acciones aparecen como botones debajo de la notificación
        constructor.extend(new NotificationCompat.WearableExtender().addActions(acciones));

        NotificationManagerCompat.from(contexto).notify(aviso.getIdNotificacion(), constructor.build());
    }

    /** Quita la notificación cuando una de sus acciones abre una pantalla. */
    public static void cerrar(Context contexto, Intent intent) {
        int idNotificacion = intent.getIntExtra(AvisoLike.EXTRA_ID_NOTIFICACION, 0);
        if (idNotificacion != 0) {
            NotificationManagerCompat.from(contexto).cancel(idNotificacion);
        }
    }

    private static NotificationCompat.Action accion(Context contexto, int icono, @StringRes int texto,
                                                    PendingIntent alTocar) {
        return new NotificationCompat.Action.Builder(icono, contexto.getString(texto), alTocar).build();
    }

    // Ver mi perfil abre MainActivity en la pestaña del perfil de la cuenta de este celular
    private static PendingIntent abrirPerfil(Context contexto, AvisoLike aviso) {
        Intent intent = aviso.guardarEn(new Intent(contexto, MainActivity.class))
                .putExtra(EXTRA_PANTALLA, PANTALLA_PERFIL)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return PendingIntent.getActivity(contexto, codigo(aviso, ACCION_VER_MI_PERFIL), intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
    }

    // Dar follow o unfollow no abre ninguna pantalla. SeguirReceiver llama al endpoint y actualiza la notificación.
    private static PendingIntent cambiarFollow(Context contexto, AvisoLike aviso) {
        String accion = aviso.isLoSigue() ? ConstantesRestApi.ACCION_UNFOLLOW : ConstantesRestApi.ACCION_FOLLOW;
        Intent intent = aviso.guardarEn(new Intent(contexto, SeguirReceiver.class))
                .putExtra(SeguirReceiver.EXTRA_ACCION, accion);
        return PendingIntent.getBroadcast(contexto, codigo(aviso, ACCION_FOLLOW), intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
    }

    // Ver usuario abre UsuarioActivity. Al regresar se llega a MainActivity.
    private static PendingIntent abrirUsuario(Context contexto, AvisoLike aviso) {
        Intent intent = aviso.guardarEn(new Intent(contexto, UsuarioActivity.class))
                .putExtra(UsuarioActivity.EXTRA_USERNAME, aviso.getUsernameQuien());
        return TaskStackBuilder.create(contexto)
                .addNextIntentWithParentStack(intent)
                .getPendingIntent(codigo(aviso, ACCION_VER_USUARIO),
                        PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
    }

    // Android distingue los PendingIntent por su requestCode. Si dos fueran iguales, se mezclarían sus extras.
    private static int codigo(AvisoLike aviso, int accion) {
        return aviso.getIdNotificacion() * 3 + accion;
    }
}
