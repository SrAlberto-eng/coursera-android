package com.sralberto.petagram.presentador;

import com.sralberto.petagram.modelo.IFuenteUsuario;
import com.sralberto.petagram.modelo.UsuarioInstagram;
import com.sralberto.petagram.restapi.ConstantesRestApi;
import com.sralberto.petagram.restapi.EndpointsApi;
import com.sralberto.petagram.restapi.adaptador.RestApiAdaptador;
import com.sralberto.petagram.restapi.modelo.MediaResponse;
import com.sralberto.petagram.restapi.modelo.UsuariosResponse;
import com.sralberto.petagram.vista.IPerfilVista;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Muestra el perfil de una cuenta. Por lo general es el usuario principal que se guardó
 * en Configurar cuenta. En UsuarioActivity es la cuenta que dio like a una foto.
 *
 * 1. Busca la cuenta con users/search para conocer su id, nombre y foto de perfil.
 * 2. Pide sus fotos a users/{user-id}/media/recent.
 */
public class PerfilPresentador implements IPerfilPresentador {

    private IPerfilVista vista;
    private final IFuenteUsuario cuenta;
    private final EndpointsApi endpoints;
    private final List<Call<?>> llamadas = new ArrayList<>();
    private String usuarioCargado;

    public PerfilPresentador(IPerfilVista vista, IFuenteUsuario cuenta) {
        this(vista, cuenta, RestApiAdaptador.obtenerEndpoints());
    }

    public PerfilPresentador(IPerfilVista vista, IFuenteUsuario cuenta, EndpointsApi endpoints) {
        this.vista = vista;
        this.cuenta = cuenta;
        this.endpoints = endpoints;
    }

    @Override
    public void cargarPerfil() {
        cancelarLlamadas();
        usuarioCargado = cuenta.obtenerUsuario();
        vista.mostrarCargando(true);

        final String username = usuarioCargado;
        Call<UsuariosResponse> llamada = endpoints.buscarUsuario(username, ConstantesRestApi.ACCESS_TOKEN);
        llamadas.add(llamada);
        llamada.enqueue(new Callback<UsuariosResponse>() {
            @Override
            public void onResponse(Call<UsuariosResponse> call, Response<UsuariosResponse> response) {
                if (vista == null) {
                    return;
                }
                UsuariosResponse cuerpo = response.body();
                if (!response.isSuccessful() || cuerpo == null) {
                    mostrarError();
                    return;
                }
                UsuarioInstagram usuario = cuerpo.buscarPorUsername(username);
                if (usuario == null) {
                    vista.mostrarCargando(false);
                    vista.mostrarUsuarioNoEncontrado(username);
                    return;
                }
                vista.mostrarUsuario(usuario);
                obtenerFotosDe(usuario);
            }

            @Override
            public void onFailure(Call<UsuariosResponse> call, Throwable t) {
                if (!call.isCanceled()) {
                    mostrarError();
                }
            }
        });
    }

    private void obtenerFotosDe(UsuarioInstagram usuario) {
        Call<MediaResponse> llamada = endpoints.getMediaReciente(usuario.getId(), ConstantesRestApi.ACCESS_TOKEN);
        llamadas.add(llamada);
        llamada.enqueue(new Callback<MediaResponse>() {
            @Override
            public void onResponse(Call<MediaResponse> call, Response<MediaResponse> response) {
                if (vista == null) {
                    return;
                }
                MediaResponse cuerpo = response.body();
                if (!response.isSuccessful() || cuerpo == null) {
                    mostrarError();
                    return;
                }
                vista.mostrarCargando(false);
                vista.mostrarFotos(cuerpo.getFotos());
            }

            @Override
            public void onFailure(Call<MediaResponse> call, Throwable t) {
                if (!call.isCanceled()) {
                    mostrarError();
                }
            }
        });
    }

    @Override
    public void recargarSiCambioLaCuenta() {
        if (!cuenta.obtenerUsuario().equals(usuarioCargado)) {
            cargarPerfil();
        }
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
