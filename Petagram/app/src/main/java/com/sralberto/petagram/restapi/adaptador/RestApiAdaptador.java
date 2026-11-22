package com.sralberto.petagram.restapi.adaptador;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sralberto.petagram.restapi.ConstantesRestApi;
import com.sralberto.petagram.restapi.EndpointsApi;
import com.sralberto.petagram.restapi.deserializador.MediaDeserializador;
import com.sralberto.petagram.restapi.deserializador.UsuariosDeserializador;
import com.sralberto.petagram.restapi.modelo.MediaResponse;
import com.sralberto.petagram.restapi.modelo.UsuariosResponse;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Arma la conexión con Retrofit. Toda la app usa la misma instancia de EndpointsApi.
 */
public final class RestApiAdaptador {

    private static EndpointsApi endpoints;

    private RestApiAdaptador() {
    }

    public static synchronized EndpointsApi obtenerEndpoints() {
        if (endpoints == null) {
            endpoints = establecerConexionRestApiInstagram(construirGsonDeserializadores());
        }
        return endpoints;
    }

    private static EndpointsApi establecerConexionRestApiInstagram(Gson gson) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ConstantesRestApi.ROOT_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        return retrofit.create(EndpointsApi.class);
    }

    // Gson usa estos deserializadores en lugar de mapear el JSON campo por campo
    private static Gson construirGsonDeserializadores() {
        return new GsonBuilder()
                .registerTypeAdapter(MediaResponse.class, new MediaDeserializador())
                .registerTypeAdapter(UsuariosResponse.class, new UsuariosDeserializador())
                .create();
    }
}
