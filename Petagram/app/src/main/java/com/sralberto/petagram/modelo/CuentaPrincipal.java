package com.sralberto.petagram.modelo;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Guarda el usuario principal de la app, el que se asigna en Configurar cuenta.
 * El perfil muestra las fotos de este usuario.
 */
public class CuentaPrincipal implements IFuenteUsuario {

    public static final String USUARIO_POR_DEFECTO = "ronny";

    private static final String ARCHIVO = "cuenta_principal";
    private static final String CLAVE_USUARIO = "usuario";

    private final SharedPreferences preferencias;

    public CuentaPrincipal(Context contexto) {
        preferencias = contexto.getApplicationContext()
                .getSharedPreferences(ARCHIVO, Context.MODE_PRIVATE);
    }

    @Override
    public String obtenerUsuario() {
        return preferencias.getString(CLAVE_USUARIO, USUARIO_POR_DEFECTO);
    }

    public void guardarUsuario(String usuario) {
        preferencias.edit().putString(CLAVE_USUARIO, usuario).apply();
    }
}
