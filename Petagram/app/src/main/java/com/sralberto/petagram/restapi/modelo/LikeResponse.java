package com.sralberto.petagram.restapi.modelo;

import com.google.gson.annotations.SerializedName;

/**
 * Lo que responde POST media/{media-id}/likes. Instagram contestaba {"meta":{"code":200},"data":null}.
 */
public class LikeResponse {

    @SerializedName("meta")
    private Meta meta;

    public boolean esExitoso() {
        return meta != null && meta.code == 200;
    }

    private static class Meta {

        @SerializedName("code")
        private int code;
    }
}
