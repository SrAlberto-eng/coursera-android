package com.sralberto.petagram.restapi.deserializador;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.sralberto.petagram.modelo.Foto;
import com.sralberto.petagram.modelo.UsuarioInstagram;
import com.sralberto.petagram.restapi.JsonKeys;
import com.sralberto.petagram.restapi.modelo.MediaResponse;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Convierte el JSON de users/{user-id}/media/recent en un MediaResponse.
 *
 * De cada foto solo se leen los campos que usa la app:
 * id, user, images.standard_resolution.url, images.low_resolution.url,
 * caption.text, likes.count y created_time.
 */
public class MediaDeserializador implements JsonDeserializer<MediaResponse> {

    @Override
    public MediaResponse deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject raiz = json.getAsJsonObject();
        if (!raiz.has(JsonKeys.DATA)) {
            throw new JsonParseException("La respuesta no trae el arreglo data");
        }

        MediaResponse respuesta = new MediaResponse();
        respuesta.setFotos(deserializarFotos(raiz.getAsJsonArray(JsonKeys.DATA)));
        return respuesta;
    }

    private ArrayList<Foto> deserializarFotos(JsonArray data) {
        ArrayList<Foto> fotos = new ArrayList<>();
        for (JsonElement elemento : data) {
            fotos.add(leerFoto(elemento.getAsJsonObject()));
        }
        return fotos;
    }

    private Foto leerFoto(JsonObject media) {
        String id = media.get(JsonKeys.MEDIA_ID).getAsString();
        UsuarioInstagram usuario = UsuariosDeserializador.leerUsuario(media.getAsJsonObject(JsonKeys.USER));

        JsonObject imagenes = media.getAsJsonObject(JsonKeys.MEDIA_IMAGES);
        String urlFoto = leerUrl(imagenes, JsonKeys.MEDIA_STANDARD_RESOLUTION);
        String urlMiniatura = leerUrl(imagenes, JsonKeys.MEDIA_LOW_RESOLUTION);

        int likes = media.getAsJsonObject(JsonKeys.MEDIA_LIKES).get(JsonKeys.MEDIA_LIKES_COUNT).getAsInt();
        // Instagram manda la fecha como texto, por ejemplo "1795248900"
        long creadaEn = media.get(JsonKeys.MEDIA_CREATED_TIME).getAsLong();

        return new Foto(id, usuario, urlFoto, urlMiniatura, leerTexto(media), likes, creadaEn);
    }

    private String leerUrl(JsonObject imagenes, String resolucion) {
        return imagenes.getAsJsonObject(resolucion).get(JsonKeys.MEDIA_URL).getAsString();
    }

    // Una foto sin descripción trae "caption": null
    private String leerTexto(JsonObject media) {
        JsonElement caption = media.get(JsonKeys.MEDIA_CAPTION);
        if (caption == null || caption.isJsonNull()) {
            return "";
        }
        return caption.getAsJsonObject().get(JsonKeys.MEDIA_CAPTION_TEXT).getAsString();
    }
}
