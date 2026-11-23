package com.sralberto.petagram.restapi;

import com.sralberto.petagram.restapi.modelo.LikeFotoResponse;
import com.sralberto.petagram.restapi.modelo.UsuarioResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Endpoints del web service propio de Petagram.
 */
public interface EndpointsServidor {

    @FormUrlEncoded
    @POST(ConstantesRestApi.URL_POST_REGISTRAR_USUARIO)
    Call<UsuarioResponse> registrarUsuario(
            @Field(ConstantesRestApi.KEY_ID_DISPOSITIVO) String idDispositivo,
            @Field(ConstantesRestApi.KEY_ID_USUARIO_INSTAGRAM) String idUsuarioInstagram);

    // idUsuarioInstagram es el dueño de la foto. idDispositivo es el celular que da el like.
    @FormUrlEncoded
    @POST(ConstantesRestApi.URL_POST_REGISTRAR_LIKE)
    Call<LikeFotoResponse> registrarLike(
            @Field(ConstantesRestApi.KEY_ID_FOTO_INSTAGRAM) String idFotoInstagram,
            @Field(ConstantesRestApi.KEY_ID_USUARIO_INSTAGRAM) String idUsuarioInstagram,
            @Field(ConstantesRestApi.KEY_ID_DISPOSITIVO) String idDispositivo);
}
