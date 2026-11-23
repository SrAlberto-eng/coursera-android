package com.sralberto.petagram.notificaciones;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.sralberto.petagram.R;
import com.sralberto.petagram.restapi.ConstantesRestApi;
import com.sralberto.petagram.restapi.adaptador.RestApiAdaptador;
import com.sralberto.petagram.restapi.modelo.RelacionResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Acción Dar follow o Dar unfollow de la notificación de un like. Funciona igual si se toca en el
 * celular o en el reloj. La cuenta de este celular sigue o deja de seguir a la cuenta que dio el like.
 * Al terminar, vuelve a dibujar la notificación con la acción contraria y el resultado.
 */
public class SeguirReceiver extends BroadcastReceiver {

    public static final String EXTRA_ACCION = "accion";

    @Override
    public void onReceive(Context contexto, Intent intent) {
        AvisoLike aviso = AvisoLike.desdeIntent(intent);
        String accion = intent.getStringExtra(EXTRA_ACCION);
        if (aviso == null || !aviso.conoceQuienDioLike() || accion == null) {
            return;
        }

        Context app = contexto.getApplicationContext();
        // goAsync deja que el receiver espere la respuesta del servidor
        PendingResult pendiente = goAsync();
        RestApiAdaptador.obtenerEndpoints()
                .cambiarRelacion(aviso.getIdQuien(), ConstantesRestApi.accessTokenDe(aviso.getIdDueno()), accion)
                .enqueue(new Callback<RelacionResponse>() {
                    @Override
                    public void onResponse(Call<RelacionResponse> call, Response<RelacionResponse> response) {
                        RelacionResponse cuerpo = response.body();
                        if (response.isSuccessful() && cuerpo != null && cuerpo.esExitoso()) {
                            aviso.setLoSigue(cuerpo.loSigue());
                            int estado = cuerpo.loSigue() ? R.string.estado_ya_sigues : R.string.estado_ya_no_sigues;
                            aviso.setEstado(app.getString(estado, aviso.getUsernameQuien()));
                        } else {
                            aviso.setEstado(app.getString(R.string.estado_error_follow));
                        }
                        terminar(app, aviso, pendiente);
                    }

                    @Override
                    public void onFailure(Call<RelacionResponse> call, Throwable t) {
                        aviso.setEstado(app.getString(R.string.estado_error_follow));
                        terminar(app, aviso, pendiente);
                    }
                });
    }

    private void terminar(Context contexto, AvisoLike aviso, PendingResult pendiente) {
        Notificaciones.mostrarLike(contexto, aviso);
        pendiente.finish();
    }
}
