package com.sralberto.petagram.restapi;

/**
 * Nombres de los campos del JSON de Instagram que leen los deserializadores.
 */
public final class JsonKeys {

    public static final String DATA = "data";

    // Usuario
    public static final String USER = "user";
    public static final String USER_ID = "id";
    public static final String USERNAME = "username";
    public static final String FULL_NAME = "full_name";
    public static final String PROFILE_PICTURE = "profile_picture";

    // Foto
    public static final String MEDIA_ID = "id";
    public static final String MEDIA_CREATED_TIME = "created_time";
    public static final String MEDIA_IMAGES = "images";
    public static final String MEDIA_STANDARD_RESOLUTION = "standard_resolution";
    public static final String MEDIA_LOW_RESOLUTION = "low_resolution";
    public static final String MEDIA_URL = "url";
    public static final String MEDIA_LIKES = "likes";
    public static final String MEDIA_LIKES_COUNT = "count";
    public static final String MEDIA_CAPTION = "caption";
    public static final String MEDIA_CAPTION_TEXT = "text";

    private JsonKeys() {
    }
}
