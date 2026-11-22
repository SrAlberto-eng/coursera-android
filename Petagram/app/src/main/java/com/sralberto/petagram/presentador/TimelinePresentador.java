package com.sralberto.petagram.presentador;

import com.sralberto.petagram.modelo.Foto;
import com.sralberto.petagram.modelo.UsuarioInstagram;
import com.sralberto.petagram.restapi.ConstantesRestApi;
import com.sralberto.petagram.restapi.EndpointsApi;
import com.sralberto.petagram.restapi.adaptador.RestApiAdaptador;
import com.sralberto.petagram.restapi.modelo.MediaResponse;
import com.sralberto.petagram.restapi.modelo.UsuariosResponse;
import com.sralberto.petagram.vista.ITimelineVista;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Arma el timeline con las fotos recientes de todos los usuarios del Sandbox.
 *
 * 1. Pide la lista de usuarios a users/self/follows.
 * 2. Pide las fotos de cada uno a users/{user-id}/media/recent.
 * 3. Cuando terminan todas las peticiones, junta las fotos y las ordena de la más nueva a la más vieja.
 */
public class TimelinePresentador implements ITimelinePresentador {

    private ITimelineVista vista;
    private final EndpointsApi endpoints;
    private final List<Call<?>> llamadas = new ArrayList<>();
    private final ArrayList<Foto> fotos = new ArrayList<>();
    private int peticionesPendientes;

    public TimelinePresentador(ITimelineVista vista) {
        this(vista, RestApiAdaptador.obtenerEndpoints());
    }

    public TimelinePresentador(ITimelineVista vista, EndpointsApi endpoints) {
        this.vista = vista;
        this.endpoints = endpoints;
    }

    @Override
    public void obtenerTimeline() {
        cancelarLlamadas();
        fotos.clear();
        vista.mostrarCargando(true);

        Call<UsuariosResponse> llamada = endpoints.getUsuariosSandbox(ConstantesRestApi.ACCESS_TOKEN);
        llamadas.add(llamada);
        llamada.enqueue(new Callback<UsuariosResponse>() {
            @Override
            public void onResponse(Call<UsuariosResponse> call, Response<UsuariosResponse> response) {
                UsuariosResponse cuerpo = response.body();
                if (!response.isSuccessful() || cuerpo == null || cuerpo.getUsuarios().isEmpty()) {
                    mostrarError();
                    return;
                }
                obtenerFotosDe(cuerpo.getUsuarios());
            }

            @Override
            public void onFailure(Call<UsuariosResponse> call, Throwable t) {
                if (!call.isCanceled()) {
                    mostrarError();
                }
            }
        });
    }

    private void obtenerFotosDe(ArrayList<UsuarioInstagram> usuarios) {
        peticionesPendientes = usuarios.size();
        for (UsuarioInstagram usuario : usuarios) {
            Call<MediaResponse> llamada =
                    endpoints.getMediaReciente(usuario.getId(), ConstantesRestApi.ACCESS_TOKEN);
            llamadas.add(llamada);
            llamada.enqueue(new Callback<MediaResponse>() {
                @Override
                public void onResponse(Call<MediaResponse> call, Response<MediaResponse> response) {
                    MediaResponse cuerpo = response.body();
                    if (response.isSuccessful() && cuerpo != null) {
                        fotos.addAll(cuerpo.getFotos());
                    }
                    terminarPeticion();
                }

                @Override
                public void onFailure(Call<MediaResponse> call, Throwable t) {
                    // Si falla un usuario se muestran las fotos de los demás
                    if (!call.isCanceled()) {
                        terminarPeticion();
                    }
                }
            });
        }
    }

    private void terminarPeticion() {
        peticionesPendientes--;
        if (peticionesPendientes > 0 || vista == null) {
            return;
        }
        if (fotos.isEmpty()) {
            mostrarError();
            return;
        }
        fotos.sort((una, otra) -> Long.compare(otra.getCreadaEn(), una.getCreadaEn()));
        vista.mostrarCargando(false);
        vista.mostrarFotos(new ArrayList<>(fotos));
    }

    private void mostrarError() {
        if (vista != null) {
            vista.mostrarCargando(false);
            vista.mostrarErrorDeConexion();
        }
    }

    @Override
    public void detener() {
        vista = null;
        cancelarLlamadas();
    }

    private void cancelarLlamadas() {
        for (Call<?> llamada : llamadas) {
            llamada.cancel();
        }
        llamadas.clear();
    }
}
