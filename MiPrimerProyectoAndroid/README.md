# Mi primer proyecto Android

Proyecto del curso de desarrollo de aplicaciones móviles con Android. Hecho en Java y layouts XML.

La app muestra una pantalla de bienvenida con un botón. 

1. Soporta cuatro idiomas: español (por defecto), inglés, francés y alemán.
2. Usa como fondo una imagen nine-patch.
3. Se adapta a distintos tamaños de pantalla y a las orientaciones vertical y horizontal

## 1. Idiomas

| Carpeta | Idioma | Título | Botón |
|---|---|---|---|
| `values/` | Español (default) | Bienvenido | Entrar |
| `values-en/` | Inglés | Welcome | Enter |
| `values-fr/` | Francés | Bienvenue | Entrer |
| `values-de/` | Alemán | Willkommen | Betreten |

## 2. Fondo nine-patch

El archivo es `res/drawable-xxhdpi/fondo.9.png`. Es la imagen original con un borde de 1 px donde unas líneas negras indican qué partes se pueden estirar.

## 3. Especificaciones de las pantallas

| Carpeta | Cuándo se usa | Título |
|---|---|---|
| `values/` | Teléfono en vertical | 40sp |
| `values-land/` | Teléfono en horizontal | 34sp |
| `values-sw600dp/` | Tablet (cualquier orientación) | 64sp |

## Estructura

```
app/src/main/
├── AndroidManifest.xml
├── java/com/sralberto/miprimerproyecto/MainActivity.java
└── res/
    ├── drawable/              boton_borde.xml, ícono
    ├── drawable-xxhdpi/       fondo.9.png
    ├── layout/                activity_main.xml
    ├── mipmap-anydpi-v26/     ic_launcher.xml
    ├── values/                strings (es), dimens, colors, themes
    ├── values-en/ values-fr/ values-de/
    ├── values-land/ values-sw600dp/
    └── xml/                   locales_config.xml
```
