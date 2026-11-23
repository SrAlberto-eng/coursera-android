package com.sralberto.petagram.restapi.modelo;

import com.google.gson.annotations.SerializedName;

/**
 * Lo que responde users/{user-id}/relationship. Instagram contestaba, por ejemplo,
 * {"meta":{"code":200},"data":{"outgoing_status":"follows","target_user_is_private":false}}.
 */
public class RelacionResponse {

    @SerializedName("meta")
    private Meta meta;

    @SerializedName("data")
    private Data data;

    public boolean esExitoso() {
        return meta != null && meta.code == 200 && data != null;
    }

    /** Si la cuenta del token sigue a la otra cuenta. */
    public boolean loSigue() {
        return data != null && "follows".equals(data.outgoingStatus);
    }

    private static class Meta {

        @SerializedName("code")
        private int code;
    }

    private static class Data {

        @SerializedName("outgoing_status")
        private String outgoingStatus;
    }
}
