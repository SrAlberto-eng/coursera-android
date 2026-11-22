package com.sralberto.petagram.modelo;

/**
 * Cuenta de Instagram tal como la regresa el API: users/search, users/self/follows
 * y el objeto "user" de cada foto.
 */
public class UsuarioInstagram {

    private final String id;
    private final String username;
    private final String nombreCompleto;
    private final String urlFotoPerfil;

    public UsuarioInstagram(String id, String username, String nombreCompleto, String urlFotoPerfil) {
        this.id = id;
        this.username = username;
        this.nombreCompleto = nombreCompleto;
        this.urlFotoPerfil = urlFotoPerfil;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public String getUrlFotoPerfil() {
        return urlFotoPerfil;
    }
}
