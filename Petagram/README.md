# Petagram

Proyecto del curso de desarrollo de aplicaciones móviles con Android. Hecho en Java y layouts XML.

Módulos elaborados del curso

1. **Módulo 1.** Identidad con Material Design a partir del logotipo de Puppy.
2. **Módulo 2.** Petagram, una lista de mascotas a las que les puedes dar huesos, más una pantalla con las 5 favoritas.
3. **Módulo 6.** Menús y Fragments. Pestañas con ViewPager, perfil de la mascota, formulario de Contacto que manda correo con JavaMail y pantalla de Acerca de.
4. **Módulo 8.** Base de datos. Las 5 favoritas se guardan en SQLite y cambian cada vez que le das un hueso a una mascota.

## Módulo 2 - Capturas

| Lista de mascotas | 5 mascotas favoritas |
|---|---|
| ![Lista de mascotas](docs/lista.png) | ![Favoritas](docs/favoritas.png) |

## Identidad

![Paleta](docs/paleta.png)

| Rol | Color | Del logo | Dónde se usa |
|---|---|---|---|
| Primary | `#66DDE3` | Fondo cian | Toolbar y fondo del ícono de la app |
| Dark primary | `#1B8D93` | El mismo cian, más oscuro | Barra de estado |
| Accent | `#E83B35` | Collar y techo de la casita | Botón flotante de la cámara |
| Texto e íconos | `#3C2F33` | Letras "Puppy" y piso | Título, huella, estrella, nombres |
| Raiting | `#DCBE68` | Placa del collar | Hueso dorado |
| Fondo | `#EEFBFC` | Cian muy claro | Fondo de las pantallas |


## Módulo 6 - Capturas

| Inicio | Perfil de la mascota | Menú |
|---|---|---|
| ![Inicio](docs/modulo6_inicio.png) | ![Perfil](docs/modulo6_perfil.png) | ![Menú](docs/modulo6_menu.png) |

| Contacto | Acerca de |
|---|---|
| ![Contacto](docs/modulo6_contacto.png) | ![Acerca de](docs/modulo6_acercade.png) |

## Módulo 8 - Capturas

| Lista con huesos | Últimas 5 favoritas |
|---|---|
| ![Lista](docs/modulo8_lista.png) | ![Favoritas](docs/modulo8_favoritas.png) |

## Módulo 8 - Modelo de base de datos

```mermaid
erDiagram
    mascota {
        INTEGER id UK "único por mascota"
        TEXT nombre
        TEXT foto "nombre del drawable, por ejemplo mascota_catty"
        INTEGER raiting "huesos que lleva"
    }
```

Es una sola tabla con los mismos campos que la clase `Mascota`. La foto se guarda por nombre porque el número de `R.drawable` puede cambiar cada vez que compilas.

Para saber cuáles son las más recientes se usa el `rowid`, un número que SQLite le da a cada fila y que crece con cada inserción. Al darle un hueso a una mascota, su fila se borra y se vuelve a insertar, así queda como la más reciente. Después se borran las filas que no estén entre las 5 más nuevas.

## Estructura

```
app/src/main/
├── AndroidManifest.xml
├── java/com/sralberto/petagram/
│   ├── MainActivity.java            barra, pestañas, menú y botón flotante
│   ├── PaginasAdaptador.java        qué Fragment va en cada pestaña
│   ├── ListaMascotasFragment.java   lista de mascotas
│   ├── PerfilMascotaFragment.java   perfil de Ronny
│   ├── FavoritasActivity.java       5 favoritas
│   ├── ContactoActivity.java        formulario de contacto
│   ├── CorreoContacto.java          envío con JavaMail
│   ├── AcercaDeActivity.java        bio del desarrollador
│   ├── BaseDatosMascotas.java       tabla mascota en SQLite
│   ├── Mascota.java / Foto.java     entidades
│   └── MascotaAdaptador.java / FotoAdaptador.java
└── res/
    ├── drawable/            mascotas, huesos, huella, estrella, cámara, íconos de pestañas
    ├── layout/              activities, fragments, items, toolbar, action_view_favoritas
    ├── menu/                menu_principal.xml
    ├── mipmap-anydpi-v26/   ic_launcher.xml
    └── values/              colors, themes, strings, dimens
```
