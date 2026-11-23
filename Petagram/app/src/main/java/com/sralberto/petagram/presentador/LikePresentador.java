package com.sralberto.petagram.presentador;

import com.sralberto.petagram.modelo.Foto;
import com.sralberto.petagram.modelo.IProveedorToken;
import com.sralberto.petagram.modelo.ProveedorTokenFcm;
import com.sralberto.petagram.restapi.ConstantesRestApi;
import com.sralberto.petagram.restapi.EndpointsApi;
import com.sralberto.petagram.restapi.EndpointsServidor;
import com.sralberto.petagram.restapi.adaptador.RestApiAdaptador;
import com.sralberto.petagram.restapi.modelo.LikeFotoResponse;
import com.sralberto.petagram.restapi.modelo.LikeResponse;
import com.sralberto.petagram.vista.ILikeVista;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Da like a una foto del timeline.
 *
 * 1. Manda el like a Instagram con POST media/{media-id}/likes.
 * 2. Pide el token de este dispositivo a Firebase Cloud Messaging.
 * 3. Registra el like en POST /registrar-like del web service, que avisa al dueño de la foto.
 */
public class LikePresentador implements ILikePresentador {

    private ILikeVista vista;
    private final EndpointsApi endpointsInstagram;
    private final EndpointsServidor endpointsServidor;
    private final IProveedorToken proveedorToken;
    private final List<Call<?>> llamadas = new ArrayList<>();
    // Fotos con un like en camino, para no mandar dos si tocan el hueso dos veces seguidas
    private final Set<String> enviando = new HashSet<>();

    public LikePresentador(ILikeVista vista) {
        this(vista, RestApiAdaptador.obtenerEndpoints(), RestApiAdaptador.obtenerEndpointsServidor(),
                new ProveedorTokenFcm());
    }

    public LikePresentador(ILikeVista vista, EndpointsApi endpointsInstagram,
                           EndpointsServidor endpointsServidor, IProveedorToken proveedorToken) {
        this.vista = vista;
        this.endpointsInstagram = endpointsInstagram;
        this.endpointsServidor = endpointsServidor;
        this.proveedorToken = proveedorToken;
    }

    @Override
    public void darLike(Foto foto) {
        if (vista == null || !enviando.add(foto.getId())) {
            return;
        }
        vista.mostrarEnviandoLike(foto, true);

        Call<LikeResponse> llamada = endpointsInstagram.darLike(foto.getId(), ConstantesRestApi.ACCESS_TOKEN);
        llamadas.add(llamada);
        llamada.enqueue(new Callback<LikeResponse>() {
            @Override
            public void onResponse(Call<LikeResponse> call, Response<LikeResponse> response) {
                LikeResponse cuerpo = response.body();
                if (response.isSuccessful() && cuerpo != null && cuerpo.esExitoso()) {
                    foto.sumarLike();
                    registrarEnServidor(foto);
                } else {
                    terminar(foto, () -> vista.mostrarErrorLike());
                }
            }

            @Override
            public void onFailure(Call<LikeResponse> call, Throwable t) {
                if (!call.isCanceled()) {
                    terminar(foto, () -> vista.mostrarErrorLike());
                }
            }
        });
    }

    private void registrarEnServidor(Foto foto) {
        proveedorToken.obtenerToken(new IProveedorToken.AlObtenerToken() {
            @Override
            public void alObtener(String token) {
                if (token == null || token.trim().isEmpty()) {
                    terminar(foto, () -> vista.mostrarLikeSinAviso(foto));
                    return;
                }
                mandarAlServidor(foto, token);
            }

            @Override
            public void alFallar() {
                terminar(foto, () -> vista.mostrarLikeSinAviso(foto));
            }
        });
    }

    private void mandarAlServidor(Foto foto, String token) {
        if (vista == null) {
            return;
        }
        Call<LikeFotoResponse> llamada =
                endpointsServidor.registrarLike(foto.getId(), foto.getUsuario().getId(), token);
        llamadas.add(llamada);
        llamada.enqueue(new Callback<LikeFotoResponse>() {
            @Override
            public void onResponse(Call<LikeFotoResponse> call, Response<LikeFotoResponse> response) {
                LikeFotoResponse cuerpo = response.body();
                if (!response.isSuccessful() || cuerpo == null || !foto.getId().equals(cuerpo.getIdFotoInstagram())) {
                    terminar(foto, () -> vista.mostrarLikeSinAviso(foto));
                } else if (cuerpo.getNotificacionesEnviadas() > 0) {
                    terminar(foto, () -> vista.mostrarLikeConAviso(foto));
                } else if (cuerpo.getDispositivos() == 0) {
                    terminar(foto, () -> vista.mostrarLikeSinDispositivos(foto));
                } else {
                    terminar(foto, () -> vista.mostrarLikeSinAviso(foto));
                }
            }

            @Override
            public void onFailure(Call<LikeFotoResponse> call, Throwable t) {
                if (!call.isCanceled()) {
                    terminar(foto, () -> vista.mostrarLikeSinAviso(foto));
                }
            }
        });
    }

    private void terminar(Foto foto, Runnable mostrarResultado) {
        enviando.remove(foto.getId());
        if (vista != null) {
            vista.mostrarEnviandoLike(foto, false);
            mostrarResultado.run();
        }
    }

    @Override
    public void detener() {
        vista = null;
        for (Call<?> llamada : llamadas) {
            llamada.cancel();
        }
        llamadas.clear();
        enviando.clear();
    }
}
