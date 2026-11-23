package com.sralberto.petagram.modelo;

import com.google.firebase.messaging.FirebaseMessaging;

/**
 * Pide el token del dispositivo a Firebase Cloud Messaging.
 */
public class ProveedorTokenFcm implements IProveedorToken {

    @Override
    public void obtenerToken(AlObtenerToken alObtenerToken) {
        FirebaseMessaging firebase;
        try {
            firebase = FirebaseMessaging.getInstance();
        } catch (IllegalStateException sinConfiguracion) {
            // Pasa cuando la app se compiló sin google-services.json
            alObtenerToken.alFallar();
            return;
        }
        firebase.getToken().addOnCompleteListener(tarea -> {
            if (tarea.isSuccessful()) {
                alObtenerToken.alObtener(tarea.getResult());
            } else {
                // Falla sin Google Play Services o sin conexión
                alObtenerToken.alFallar();
            }
        });
    }
}
