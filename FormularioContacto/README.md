# Formulario de contacto

Proyecto del curso de desarrollo de aplicaciones móviles con Android. Hecho en Java y layouts XML.

La app tiene dos activities.

1. **FormularioActivity** pide nombre completo, fecha de nacimiento, teléfono, email y una descripción del contacto.
2. **ConfirmarDatosActivity** muestra los datos capturados. El botón "Editar datos" regresa al formulario con los campos llenos.

## Estructura

```
app/src/main/
├── AndroidManifest.xml
├── java/com/sralberto/formulariocontacto/
│   ├── FormularioActivity.java
│   └── ConfirmarDatosActivity.java
└── res/
    ├── drawable/            ícono
    ├── layout/              activity_formulario.xml, activity_confirmar_datos.xml
    ├── mipmap-anydpi-v26/   ic_launcher.xml
    └── values/              strings, colors, dimens, themes
```

## Requisitos

- Android Studio con Android SDK 36
- minSdk 26
- Librerías `androidx.appcompat:appcompat` y `com.google.android.material:material`
