package com.sralberto.petagram.restapi;

/**
 * Rutas del API de Instagram v1 que usa Petagram.
 *
 * El API v1 y su modo Sandbox ya no existen. Las respuestas se simulan con archivos JSON
 * que tienen el mismo formato y las mismas rutas, y viven en la carpeta Petagram/api del repo.
 * Para usar el API real bastaría con cambiar ROOT_URL por https://api.instagram.com/v1/
 * y ACCESS_TOKEN por un token válido.
 */
public final class ConstantesRestApi {

    public static final String VERSION = "v1/";
    public static final String ROOT_URL =
            "https://raw.githubusercontent.com/SrAlberto-eng/coursera-android/main/Petagram/api/" + VERSION;

    // El servidor simulado no revisa el token, pero cada petición lo manda como lo pedía Instagram
    public static final String ACCESS_TOKEN = "petagram.sandbox";

    public static final String KEY_ACCESS_TOKEN = "access_token";
    public static final String KEY_BUSQUEDA = "q";
    public static final String KEY_USER_ID = "user-id";

    // Usuarios que aceptaron la invitación al Sandbox de la app
    public static final String URL_GET_USUARIOS_SANDBOX = "users/self/follows";
    // Busca una cuenta por su nombre de usuario
    public static final String URL_GET_BUSCAR_USUARIO = "users/search";
    // Fotos recientes de una cuenta
    public static final String URL_GET_MEDIA_RECIENTE_USUARIO = "users/{" + KEY_USER_ID + "}/media/recent";

    private ConstantesRestApi() {
    }
}
