package com.sralberto.petagram.restapi.modelo;

import com.sralberto.petagram.modelo.UsuarioInstagram;

import java.util.ArrayList;

/**
 * Respuesta de users/search y de users/self/follows ya convertida por UsuariosDeserializador.
 */
public class UsuariosResponse {

    private ArrayList<UsuarioInstagram> usuarios = new ArrayList<>();

    public ArrayList<UsuarioInstagram> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(ArrayList<UsuarioInstagram> usuarios) {
        this.usuarios = usuarios;
    }

    /**
     * La búsqueda de Instagram regresa coincidencias parecidas, no solo la exacta.
     * Aquí se toma la cuenta cuyo username es igual al que se escribió.
     */
    public UsuarioInstagram buscarPorUsername(String username) {
        for (UsuarioInstagram usuario : usuarios) {
            if (usuario.getUsername().equalsIgnoreCase(username)) {
                return usuario;
            }
        }
        return null;
    }
}
