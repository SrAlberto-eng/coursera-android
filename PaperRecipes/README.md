# PaperRecipes

Aplicación de recetas que ajusta las cantidades de los ingredientes según las porciones que se van a preparar. Esta entrega es la codificación de vistas en Java y XML a partir de la [maquetación en Figma](https://www.figma.com/design/1ohfl8bw4XY85NCGQbt2jV). Todavía no tiene funcionalidad. Las recetas son datos de ejemplo y los botones de guardar y borrar solo cierran la pantalla.

## Flujo de pantallas

La app abre con el logotipo y pasa sola a la lista de recetas. Desde ahí se puede crear una receta con el botón flotante o tocar una tarjeta para ver su detalle. El detalle lleva a editar la receta, a borrarla con una confirmación o a la pantalla de preparación. La preparación tiene tres modos, por porciones, con un multiplicador libre y con el cálculo inverso a partir de lo que se tiene de un ingrediente. Cada modo muestra el ejemplo de la maqueta.

Estas son las pantallas y su archivo de layout:

| Maqueta | Pantalla | Layout |
|---|---|---|
| 01 | Inicio | `activity_inicio.xml` |
| 02 | Lista de recetas | `fragment_lista.xml` |
| 03 | Detalle de receta | `fragment_detalle.xml` |
| 04 | Registro de receta | `activity_registro.xml` |
| 05 y 06 | Preparación | `activity_preparar.xml` |
| 09 | Tableta, lista y detalle lado a lado | `layout-sw600dp/` |

## Varias pantallas, modo oscuro e idiomas

La lista y el detalle son fragmentos. En celular cada uno ocupa toda la pantalla, y en tableta los layouts de `layout-sw600dp` los acomodan lado a lado. Los colores del modo oscuro están en `values-night/colors.xml` y los textos en inglés en `values-en/strings.xml`, así que Android los elige según la configuración del teléfono.

## Estructura

```
app/src/main/java/com/sralberto/paperrecipes/
├─ InicioActivity.java      Logotipo al abrir la app
├─ MainActivity.java        Lista en celular, lista y detalle en tableta
├─ ListaFragment.java       Buscador y cuadrícula de recetas
├─ DetalleFragment.java     Foto, ingredientes y pasos
├─ DetalleActivity.java     Aloja el detalle en celular
├─ RegistroActivity.java    Nueva receta o edición
├─ PrepararActivity.java    Modos de preparación y resumen de escala
├─ RecetaAdaptador.java     Tarjetas del RecyclerView
├─ VistasReceta.java        Llena las listas de ingredientes y pasos
├─ Receta.java, Ingrediente.java
└─ Recetas.java             Recetas de ejemplo
```

## Cómo correrla

Abre la carpeta `PaperRecipes` en Android Studio, deja que sincronice Gradle y córrela en un emulador o en un teléfono con Android 8.0 o superior. Para ver la versión de tableta usa un emulador de tableta, como Pixel Tablet.

La tipografía Roboto Mono va incluida en `res/font` y se distribuye con la licencia SIL Open Font License. El texto de la licencia está en `licencias/`.
