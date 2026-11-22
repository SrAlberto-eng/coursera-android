package com.sralberto.petagram.restapi.deserializador;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.sralberto.petagram.modelo.UsuarioInstagram;
import com.sralberto.petagram.restapi.JsonKeys;
import com.sralberto.petagram.restapi.modelo.UsuariosResponse;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Convierte el JSON de users/search y users/self/follows en un UsuariosResponse.
 *
 * Ejemplo de lo que llega:
 * { "data": [ { "id": "1002", "username": "ronny", "full_name": "Ronny", "profile_picture": "https://..." } ] }
 */
public class UsuariosDeserializador implements JsonDeserializer<UsuariosResponse> {

    @Override
    public UsuariosResponse deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject raiz = json.getAsJsonObject();
        if (!raiz.has(JsonKeys.DATA)) {
            throw new JsonParseException("La respuesta no trae el arreglo data");
        }

        UsuariosResponse respuesta = new UsuariosResponse();
        respuesta.setUsuarios(deserializarUsuarios(raiz.getAsJsonArray(JsonKeys.DATA)));
        return respuesta;
    }

    private ArrayList<UsuarioInstagram> deserializarUsuarios(JsonArray data) {
        ArrayList<UsuarioInstagram> usuarios = new ArrayList<>();
        for (JsonElement elemento : data) {
            usuarios.add(leerUsuario(elemento.getAsJsonObject()));
        }
        return usuarios;
    }

    /** También lo usa MediaDeserializador para leer el objeto "user" de cada foto. */
    static UsuarioInstagram leerUsuario(JsonObject usuario) {
        return new UsuarioInstagram(
                usuario.get(JsonKeys.USER_ID).getAsString(),
                usuario.get(JsonKeys.USERNAME).getAsString(),
                usuario.get(JsonKeys.FULL_NAME).getAsString(),
                usuario.get(JsonKeys.PROFILE_PICTURE).getAsString());
    }
}
