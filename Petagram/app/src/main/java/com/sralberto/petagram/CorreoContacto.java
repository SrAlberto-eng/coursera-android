package com.sralberto.petagram;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Manda el comentario del formulario de Contacto a la cuenta de Gmail configurada en local.properties.
 * Hace una conexión de red, así que no se debe llamar desde el hilo principal.
 */
public class CorreoContacto {

    private static final String SERVIDOR = "smtp.gmail.com";
    private static final String PUERTO = "587";

    public static boolean estaConfigurado() {
        return !BuildConfig.CORREO_USUARIO.isEmpty() && !BuildConfig.CORREO_CLAVE.isEmpty();
    }

    public static void enviar(String nombre, String correo, String mensaje) throws MessagingException {
        Properties configuracion = new Properties();
        configuracion.put("mail.smtp.host", SERVIDOR);
        configuracion.put("mail.smtp.port", PUERTO);
        configuracion.put("mail.smtp.auth", "true");
        configuracion.put("mail.smtp.starttls.enable", "true");
        configuracion.put("mail.smtp.ssl.protocols", "TLSv1.2");

        Session sesion = Session.getInstance(configuracion, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(BuildConfig.CORREO_USUARIO, BuildConfig.CORREO_CLAVE);
            }
        });

        MimeMessage correoNuevo = new MimeMessage(sesion);
        correoNuevo.setFrom(new InternetAddress(BuildConfig.CORREO_USUARIO));
        correoNuevo.setRecipients(Message.RecipientType.TO, BuildConfig.CORREO_USUARIO);
        // Al darle "Responder" en Gmail, la respuesta le llega a quien escribió el comentario
        correoNuevo.setReplyTo(InternetAddress.parse(correo));
        correoNuevo.setSubject("Comentario de " + nombre + " en Petagram", "UTF-8");
        correoNuevo.setText(mensaje + "\n\n" + nombre + "\n" + correo, "UTF-8");

        Transport.send(correoNuevo);
    }
}
