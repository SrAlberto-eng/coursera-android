package com.sralberto.petagram.notificaciones;

import android.content.Intent;

import java.util.Map;

/**
 * Datos de la notificación de un like. Salen del mensaje de FCM y viajan como extras en las
 * acciones de la notificación, para poder volver a dibujarla después de dar follow o unfollow.
 */
public class AvisoLike {

    // Claves de los datos que manda el servidor en /registrar-like
    static final String DATO_TIPO = "tipo";
    static final String TIPO_LIKE = "like";
    static final String DATO_ID_FOTO = "id_foto_instagram";
    static final String DATO_ID_DUENO = "id_usuario_instagram";
    static final String DATO_ID_QUIEN = "id_usuario_like";
    static final String DATO_USERNAME_QUIEN = "username_like";

    // Extras que agrega la app
    static final String EXTRA_ID_NOTIFICACION = "id_notificacion";
    static final String EXTRA_LO_SIGUE = "lo_sigue";
    static final String EXTRA_ESTADO = "estado";

    private final int idNotificacion;
    private final String idFoto;
    // Dueño de la foto. Es la cuenta registrada en este celular.
    private final String idDueno;
    // Cuenta que dio el like. Es null si su celular no se registró en el servidor.
    private final String idQuien;
    private final String usernameQuien;
    private boolean loSigue;
    // Resultado del último follow o unfollow, por ejemplo "Ya sigues a @kira."
    private String estado;

    private AvisoLike(int idNotificacion, String idFoto, String idDueno, String idQuien, String usernameQuien) {
        this.idNotificacion = idNotificacion;
        this.idFoto = idFoto;
        this.idDueno = idDueno;
        this.idQuien = idQuien;
        this.usernameQuien = usernameQuien;
    }

    /** Regresa null si el mensaje de FCM no es de un like. */
    public static AvisoLike desdeDatos(Map<String, String> datos, int idNotificacion) {
        if (!TIPO_LIKE.equals(datos.get(DATO_TIPO)) || datos.get(DATO_ID_DUENO) == null) {
            return null;
        }
        return new AvisoLike(idNotificacion, datos.get(DATO_ID_FOTO), datos.get(DATO_ID_DUENO),
                datos.get(DATO_ID_QUIEN), datos.get(DATO_USERNAME_QUIEN));
    }

    /** Regresa null si el intent no viene de una notificación de like. */
    public static AvisoLike desdeIntent(Intent intent) {
        if (intent.getStringExtra(DATO_ID_DUENO) == null) {
            return null;
        }
        AvisoLike aviso = new AvisoLike(intent.getIntExtra(EXTRA_ID_NOTIFICACION, 0),
                intent.getStringExtra(DATO_ID_FOTO), intent.getStringExtra(DATO_ID_DUENO),
                intent.getStringExtra(DATO_ID_QUIEN), intent.getStringExtra(DATO_USERNAME_QUIEN));
        aviso.loSigue = intent.getBooleanExtra(EXTRA_LO_SIGUE, false);
        aviso.estado = intent.getStringExtra(EXTRA_ESTADO);
        return aviso;
    }

    public Intent guardarEn(Intent intent) {
        return intent
                .putExtra(EXTRA_ID_NOTIFICACION, idNotificacion)
                .putExtra(DATO_ID_FOTO, idFoto)
                .putExtra(DATO_ID_DUENO, idDueno)
                .putExtra(DATO_ID_QUIEN, idQuien)
                .putExtra(DATO_USERNAME_QUIEN, usernameQuien)
                .putExtra(EXTRA_LO_SIGUE, loSigue)
                .putExtra(EXTRA_ESTADO, estado);
    }

    /** Se sabe qué cuenta dio el like y no es el mismo dueño de la foto. */
    public boolean conoceQuienDioLike() {
        return idQuien != null && usernameQuien != null && !idQuien.equals(idDueno);
    }

    public int getIdNotificacion() {
        return idNotificacion;
    }

    public String getIdFoto() {
        return idFoto;
    }

    public String getIdDueno() {
        return idDueno;
    }

    public String getIdQuien() {
        return idQuien;
    }

    public String getUsernameQuien() {
        return usernameQuien;
    }

    public boolean isLoSigue() {
        return loSigue;
    }

    public void setLoSigue(boolean loSigue) {
        this.loSigue = loSigue;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
