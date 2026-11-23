package com.sralberto.petagram.restapi;

/**
 * Rutas del API de Instagram v1 y del web service propio que usa Petagram.
 *
 * El API v1 y su modo Sandbox ya no existen. El web service de Petagram simula ese API en la ruta /v1
 * con las mismas rutas y el mismo formato. Las cuentas y las fotos salen de los JSON de la carpeta
 * Petagram/api del repo, y los likes que se dan desde la app se guardan en su base de datos.
 * Para usar el API real bastaría con cambiar ROOT_URL por https://api.instagram.com/v1/
 * y ACCESS_TOKEN por un token válido.
 */
public final class ConstantesRestApi {

    // Web service propio en Cloudflare Workers. Su código está en Petagram/servidor.
    public static final String ROOT_URL_SERVIDOR = "https://petagram-ws.sralberto.workers.dev/";

    public static final String VERSION = "v1/";
    public static final String ROOT_URL = ROOT_URL_SERVIDOR + VERSION;

    // El servidor simulado no revisa el token, pero cada petición lo manda como lo pedía Instagram
    public static final String ACCESS_TOKEN = "petagram.sandbox";

    public static final String KEY_ACCESS_TOKEN = "access_token";
    public static final String KEY_BUSQUEDA = "q";
    public static final String KEY_USER_ID = "user-id";
    public static final String KEY_MEDIA_ID = "media-id";
    public static final String KEY_ACCION = "action";
    public static final String ACCION_FOLLOW = "follow";
    public static final String ACCION_UNFOLLOW = "unfollow";

    // Usuarios que aceptaron la invitación al Sandbox de la app
    public static final String URL_GET_USUARIOS_SANDBOX = "users/self/follows";
    // Busca una cuenta por su nombre de usuario
    public static final String URL_GET_BUSCAR_USUARIO = "users/search";
    // Fotos recientes de una cuenta
    public static final String URL_GET_MEDIA_RECIENTE_USUARIO = "users/{" + KEY_USER_ID + "}/media/recent";
    // Da like a una foto
    public static final String URL_POST_LIKE = "media/{" + KEY_MEDIA_ID + "}/likes";
    // Consulta, da o quita el follow a una cuenta
    public static final String URL_RELACION = "users/{" + KEY_USER_ID + "}/relationship";

    // Endpoints propios de Petagram
    public static final String URL_POST_REGISTRAR_USUARIO = "registrar-usuario";
    public static final String URL_POST_REGISTRAR_LIKE = "registrar-like";
    public static final String KEY_ID_DISPOSITIVO = "id_dispositivo";
    public static final String KEY_ID_USUARIO_INSTAGRAM = "id_usuario_instagram";
    public static final String KEY_ID_FOTO_INSTAGRAM = "id_foto_instagram";

    private ConstantesRestApi() {
    }

    /**
     * Token de una cuenta en particular. En el API real cada cuenta tenía su propio access_token.
     * Follow y unfollow lo necesitan para saber qué cuenta sigue a cuál.
     */
    public static String accessTokenDe(String idUsuario) {
        return ACCESS_TOKEN + "." + idUsuario;
    }
}
