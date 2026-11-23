package com.sralberto.petagram.modelo;

/**
 * Una foto publicada en Instagram. Sale de users/{user-id}/media/recent.
 * Los likes se muestran como huesos, igual que el raiting de las mascotas.
 */
public class Foto {

    private final String id;
    private final UsuarioInstagram usuario;
    private final String urlFoto;
    private final String urlMiniatura;
    private final String texto;
    private int likes;
    // Segundos desde 1970, como el created_time de Instagram
    private final long creadaEn;

    public Foto(String id, UsuarioInstagram usuario, String urlFoto, String urlMiniatura,
                String texto, int likes, long creadaEn) {
        this.id = id;
        this.usuario = usuario;
        this.urlFoto = urlFoto;
        this.urlMiniatura = urlMiniatura;
        this.texto = texto;
        this.likes = likes;
        this.creadaEn = creadaEn;
    }

    public String getId() {
        return id;
    }

    public UsuarioInstagram getUsuario() {
        return usuario;
    }

    /** Foto grande (standard_resolution) para el timeline. */
    public String getUrlFoto() {
        return urlFoto;
    }

    /** Foto mediana (low_resolution) para la cuadrícula del perfil. */
    public String getUrlMiniatura() {
        return urlMiniatura;
    }

    public String getTexto() {
        return texto;
    }

    public int getLikes() {
        return likes;
    }

    /** Se llama cuando Instagram confirma el like que se dio desde la app. */
    public void sumarLike() {
        likes++;
    }

    public long getCreadaEn() {
        return creadaEn;
    }
}
