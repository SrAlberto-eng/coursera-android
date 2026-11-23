package com.sralberto.petagram.presentador;

import com.sralberto.petagram.modelo.CuentaPrincipal;
import com.sralberto.petagram.modelo.IProveedorToken;
import com.sralberto.petagram.modelo.ProveedorTokenFcm;
import com.sralberto.petagram.modelo.UsuarioInstagram;
import com.sralberto.petagram.restapi.ConstantesRestApi;
import com.sralberto.petagram.restapi.EndpointsApi;
import com.sralberto.petagram.restapi.EndpointsServidor;
import com.sralberto.petagram.restapi.adaptador.RestApiAdaptador;
import com.sralberto.petagram.restapi.modelo.UsuarioResponse;
import com.sralberto.petagram.restapi.modelo.UsuariosResponse;
import com.sralberto.petagram.vista.INotificacionesVista;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Registra el dispositivo para recibir notificaciones.
 *
 * 1. Pide el token del dispositivo a Firebase Cloud Messaging.
 * 2. Busca el id de Instagram del usuario principal con users/search.
 * 3. Manda los dos datos a POST /registrar-usuario del web service.
 */
public class NotificacionesPresentador implements INotificacionesPresentador {

    private INotificacionesVista vista;
    private final CuentaPrincipal cuenta;
    private final IProveedorToken proveedorToken;
    private final EndpointsApi endpointsInstagram;
    private final EndpointsServidor endpointsServidor;
    private final List<Call<?>> llamadas = new ArrayList<>();
    private boolean registrando;

    public NotificacionesPresentador(INotificacionesVista vista, CuentaPrincipal cuenta) {
        this(vista, cuenta, new ProveedorTokenFcm(), RestApiAdaptador.obtenerEndpoints(),
                RestApiAdaptador.obtenerEndpointsServidor());
    }

    public NotificacionesPresentador(INotificacionesVista vista, CuentaPrincipal cuenta,
                                     IProveedorToken proveedorToken, EndpointsApi endpointsInstagram,
                                     EndpointsServidor endpointsServidor) {
        this.vista = vista;
        this.cuenta = cuenta;
        this.proveedorToken = proveedorToken;
        this.endpointsInstagram = endpointsInstagram;
        this.endpointsServidor = endpointsServidor;
    }

    @Override
    public void registrarDispositivo() {
        // Evita mandar dos registros si tocan el menú dos veces seguidas
        if (registrando) {
            return;
        }
        registrando = true;
        vista.mostrarRegistrando(true);

        proveedorToken.obtenerToken(new IProveedorToken.AlObtenerToken() {
            @Override
            public void alObtener(String token) {
                if (vista == null) {
                    return;
                }
                if (token == null || token.trim().isEmpty()) {
                    terminar();
                    vista.mostrarErrorToken();
                    return;
                }
                buscarUsuarioPrincipal(token);
            }

            @Override
            public void alFallar() {
                if (vista != null) {
                    terminar();
                    vista.mostrarErrorToken();
                }
            }
        });
    }

    private void buscarUsuarioPrincipal(String token) {
        final String username = cuenta.obtenerUsuario();
        Call<UsuariosResponse> llamada =
                endpointsInstagram.buscarUsuario(username, ConstantesRestApi.ACCESS_TOKEN);
        llamadas.add(llamada);
        llamada.enqueue(new Callback<UsuariosResponse>() {
            @Override
            public void onResponse(Call<UsuariosResponse> call, Response<UsuariosResponse> response) {
                if (vista == null) {
                    return;
                }
                UsuariosResponse cuerpo = response.body();
                if (!response.isSuccessful() || cuerpo == null) {
                    terminar();
                    vista.mostrarErrorDeConexion();
                    return;
                }
                UsuarioInstagram usuario = cuerpo.buscarPorUsername(username);
                if (usuario == null) {
                    terminar();
                    vista.mostrarUsuarioNoEncontrado(username);
                    return;
                }
                registrarEnServidor(token, usuario);
            }

            @Override
            public void onFailure(Call<UsuariosResponse> call, Throwable t) {
                if (vista != null && !call.isCanceled()) {
                    terminar();
                    vista.mostrarErrorDeConexion();
                }
            }
        });
    }

    private void registrarEnServidor(String token, UsuarioInstagram usuario) {
        Call<UsuarioResponse> llamada = endpointsServidor.registrarUsuario(token, usuario.getId());
        llamadas.add(llamada);
        llamada.enqueue(new Callback<UsuarioResponse>() {
            @Override
            public void onResponse(Call<UsuarioResponse> call, Response<UsuarioResponse> response) {
                if (vista == null) {
                    return;
                }
                terminar();
                UsuarioResponse registro = response.body();
                // El servidor regresa lo que guardó. Se revisa que coincida con lo que se mandó.
                boolean guardado = response.isSuccessful() && registro != null
                        && token.equals(registro.getIdDispositivo())
                        && usuario.getId().equals(registro.getIdUsuarioInstagram());
                if (guardado) {
                    vista.mostrarRegistroExitoso(usuario, token);
                } else {
                    vista.mostrarErrorServidor();
                }
            }

            @Override
            public void onFailure(Call<UsuarioResponse> call, Throwable t) {
                if (vista != null && !call.isCanceled()) {
                    terminar();
                    vista.mostrarErrorDeConexion();
                }
            }
        });
    }

    private void terminar() {
        registrando = false;
        vista.mostrarRegistrando(false);
    }

    @Override
    public void detener() {
        vista = null;
        for (Call<?> llamada : llamadas) {
            llamada.cancel();
        }
        llamadas.clear();
    }
}
