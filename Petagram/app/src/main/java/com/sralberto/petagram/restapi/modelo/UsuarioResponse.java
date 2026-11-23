package com.sralberto.petagram.restapi.modelo;

import com.google.gson.annotations.SerializedName;

/**
 * Lo que responde POST /registrar-usuario: el registro que quedó guardado en la base de datos.
 */
public class UsuarioResponse {

    @SerializedName("id")
    private int id;

    @SerializedName("id_dispositivo")
    private String idDispositivo;

    @SerializedName("id_usuario_instagram")
    private String idUsuarioInstagram;

    public int getId() {
        return id;
    }

    public String getIdDispositivo() {
        return idDispositivo;
    }

    public String getIdUsuarioInstagram() {
        return idUsuarioInstagram;
    }
}
