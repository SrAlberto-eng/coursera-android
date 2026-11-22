package com.sralberto.petagram.restapi.modelo;

import com.sralberto.petagram.modelo.Foto;

import java.util.ArrayList;

/**
 * Respuesta de users/{user-id}/media/recent ya convertida por MediaDeserializador.
 */
public class MediaResponse {

    private ArrayList<Foto> fotos = new ArrayList<>();

    public ArrayList<Foto> getFotos() {
        return fotos;
    }

    public void setFotos(ArrayList<Foto> fotos) {
        this.fotos = fotos;
    }
}
