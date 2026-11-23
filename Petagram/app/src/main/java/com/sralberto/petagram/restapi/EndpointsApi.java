package com.sralberto.petagram.restapi;

import com.sralberto.petagram.restapi.modelo.LikeResponse;
import com.sralberto.petagram.restapi.modelo.MediaResponse;
import com.sralberto.petagram.restapi.modelo.UsuariosResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Endpoints del API de Instagram. Retrofit crea la implementación de esta interfaz.
 */
public interface EndpointsApi {

    @GET(ConstantesRestApi.URL_GET_USUARIOS_SANDBOX)
    Call<UsuariosResponse> getUsuariosSandbox(
            @Query(ConstantesRestApi.KEY_ACCESS_TOKEN) String accessToken);

    @GET(ConstantesRestApi.URL_GET_BUSCAR_USUARIO)
    Call<UsuariosResponse> buscarUsuario(
            @Query(ConstantesRestApi.KEY_BUSQUEDA) String usuario,
            @Query(ConstantesRestApi.KEY_ACCESS_TOKEN) String accessToken);

    @GET(ConstantesRestApi.URL_GET_MEDIA_RECIENTE_USUARIO)
    Call<MediaResponse> getMediaReciente(
            @Path(ConstantesRestApi.KEY_USER_ID) String idUsuario,
            @Query(ConstantesRestApi.KEY_ACCESS_TOKEN) String accessToken);

    // Instagram pedía el access_token en el cuerpo del POST
    @FormUrlEncoded
    @POST(ConstantesRestApi.URL_POST_LIKE)
    Call<LikeResponse> darLike(
            @Path(ConstantesRestApi.KEY_MEDIA_ID) String idFoto,
            @Field(ConstantesRestApi.KEY_ACCESS_TOKEN) String accessToken);
}
