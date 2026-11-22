package com.sralberto.petagram.presentador;

import com.sralberto.petagram.modelo.CuentaPrincipal;
import com.sralberto.petagram.modelo.UsuarioInstagram;
import com.sralberto.petagram.restapi.ConstantesRestApi;
import com.sralberto.petagram.restapi.EndpointsApi;
import com.sralberto.petagram.restapi.adaptador.RestApiAdaptador;
import com.sralberto.petagram.restapi.modelo.UsuariosResponse;
import com.sralberto.petagram.vista.IConfigurarCuentaVista;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Antes de guardar el usuario principal revisa con users/search que la cuenta exista en el Sandbox.
 */
public class ConfigurarCuentaPresentador implements IConfigurarCuentaPresentador {

    private IConfigurarCuentaVista vista;
    private final CuentaPrincipal cuenta;
    private final EndpointsApi endpoints;
    private Call<UsuariosResponse> llamada;

    public ConfigurarCuentaPresentador(IConfigurarCuentaVista vista, CuentaPrincipal cuenta) {
        this(vista, cuenta, RestApiAdaptador.obtenerEndpoints());
    }

    public ConfigurarCuentaPresentador(IConfigurarCuentaVista vista, CuentaPrincipal cuenta,
                                       EndpointsApi endpoints) {
        this.vista = vista;
        this.cuenta = cuenta;
        this.endpoints = endpoints;
    }

    @Override
    public void mostrarCuentaActual() {
        vista.mostrarUsuarioActual(cuenta.obtenerUsuario());
    }

    @Override
    public void guardarCuenta(String usuarioEscrito) {
        final String username = limpiar(usuarioEscrito);
        if (username.isEmpty()) {
            vista.mostrarErrorUsuarioVacio();
            return;
        }
        if (llamada != null) {
            llamada.cancel();
        }
        vista.mostrarCargando(true);

        llamada = endpoints.buscarUsuario(username, ConstantesRestApi.ACCESS_TOKEN);
        llamada.enqueue(new Callback<UsuariosResponse>() {
            @Override
            public void onResponse(Call<UsuariosResponse> call, Response<UsuariosResponse> response) {
                if (vista == null) {
                    return;
                }
                vista.mostrarCargando(false);
                UsuariosResponse cuerpo = response.body();
                if (!response.isSuccessful() || cuerpo == null) {
                    vista.mostrarErrorDeConexion();
                    return;
                }
                UsuarioInstagram usuario = cuerpo.buscarPorUsername(username);
                if (usuario == null) {
                    vista.mostrarUsuarioNoEncontrado(username);
                    return;
                }
                cuenta.guardarUsuario(usuario.getUsername());
                vista.cuentaGuardada(usuario);
            }

            @Override
            public void onFailure(Call<UsuariosResponse> call, Throwable t) {
                if (vista != null && !call.isCanceled()) {
                    vista.mostrarCargando(false);
                    vista.mostrarErrorDeConexion();
                }
            }
        });
    }

    // Acepta "Ronny", " ronny " o "@ronny"
    private String limpiar(String texto) {
        if (texto == null) {
            return "";
        }
        String limpio = texto.trim();
        if (limpio.startsWith("@")) {
            limpio = limpio.substring(1);
        }
        return limpio.toLowerCase(Locale.ROOT);
    }

    @Override
    public void detener() {
        vista = null;
        if (llamada != null) {
            llamada.cancel();
        }
    }
}
