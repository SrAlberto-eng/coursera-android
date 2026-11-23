package com.sralberto.petagram.restapi.modelo;

import com.google.gson.annotations.SerializedName;

/**
 * Lo que responde POST /registrar-like: el like que quedó guardado y a cuántos celulares se avisó.
 */
public class LikeFotoResponse {

    @SerializedName("id")
    private int id;

    @SerializedName("id_foto_instagram")
    private String idFotoInstagram;

    @SerializedName("id_usuario_instagram")
    private String idUsuarioInstagram;

    @SerializedName("id_dispositivo")
    private String idDispositivo;

    // Celulares registrados con la cuenta dueña de la foto
    @SerializedName("dispositivos")
    private int dispositivos;

    @SerializedName("notificaciones_enviadas")
    private int notificacionesEnviadas;

    public int getId() {
        return id;
    }

    public String getIdFotoInstagram() {
        return idFotoInstagram;
    }

    public String getIdUsuarioInstagram() {
        return idUsuarioInstagram;
    }

    public String getIdDispositivo() {
        return idDispositivo;
    }

    public int getDispositivos() {
        return dispositivos;
    }

    public int getNotificacionesEnviadas() {
        return notificacionesEnviadas;
    }
}
