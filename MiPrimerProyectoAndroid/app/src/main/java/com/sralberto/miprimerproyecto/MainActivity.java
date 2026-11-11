package com.sralberto.miprimerproyecto;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Android elige el layout, los textos y los tamaños según
        // el idioma, el tamaño y la orientación del dispositivo.
        setContentView(R.layout.activity_main);

        Button botonEntrar = findViewById(R.id.boton_entrar);
        botonEntrar.setOnClickListener(v ->
                Toast.makeText(this, R.string.mensaje_entrar, Toast.LENGTH_SHORT).show());
    }
}
