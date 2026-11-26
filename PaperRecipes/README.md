# PaperRecipes

Aplicación de recetas que ajusta las cantidades de los ingredientes según las porciones que se van a preparar o según lo que se tiene de un ingrediente. Resuelve el problema planteado en la primera actividad. En las cocinas, las recetas suelen estar en papel y escritas para un número fijo de porciones, así que cada ajuste se calcula a mano. Esto lleva a preparar de más y a servir porciones distintas, que son dos causas del desperdicio de alimentos.

Está hecha en Java y XML a partir de la [maquetación en Figma](https://www.figma.com/design/1ohfl8bw4XY85NCGQbt2jV). Las recetas se guardan en el teléfono con SQLite y la app funciona sin internet.

## Ciclo completo para probarla

La app abre con seis recetas de ejemplo, que son datos dummy cargados la primera vez que se crea la base de datos. Con ellas se puede recorrer todo el flujo:

1. En la lista, busca "azucar" o "aguacate". La búsqueda encuentra recetas por nombre o por ingrediente, sin importar acentos ni mayúsculas.
2. Toca el botón Nueva receta, escribe el nombre, las porciones y al menos un ingrediente. Puedes agregar una foto con la galería o la cámara, más ingredientes y los pasos. Al guardar, la receta aparece arriba en la lista.
3. Abre la receta y toca Preparar esta receta.
4. En el modo Porciones elige un factor, como 2x, y las cantidades cambian al momento.
5. En el modo Multiplicador escribe cualquier factor, como 1.5.
6. En el modo Tengo… elige un ingrediente y escribe cuánto tienes. La app calcula el factor para el resto. Con los hotcakes, 450 ml de leche dan 1.5x y 6 porciones.
7. Regresa al detalle, edita la receta con el lápiz y bórrala con el bote de basura.

## Cómo funciona por dentro

`BaseDatosRecetas` guarda cada receta en tres tablas, `receta`, `ingrediente` y `paso`. Las dos últimas se borran solas con la receta gracias a `ON DELETE CASCADE`. Las cantidades se guardan para la receta completa y se escalan al mostrarlas, así que la base nunca cambia al preparar una receta.

La lógica que no depende de Android está en clases aparte y tiene pruebas unitarias:

- `Escala` hace los cálculos de factor, cantidades, porciones y cálculo inverso.
- `FiltroRecetas` hace la búsqueda por nombre o ingrediente.
- `ValidadorReceta` revisa el registro antes de guardar y arma la receta.

Las fotos se copian a la carpeta privada de la app y la base guarda solo su ruta. La cámara escribe la foto a través de un `FileProvider`, así que la app no pide permiso de cámara ni de almacenamiento.

## Varias pantallas, modo oscuro e idiomas

La lista y el detalle son fragmentos. En celular cada uno ocupa toda la pantalla, y en tableta los layouts de `layout-sw600dp` los acomodan lado a lado. Los colores del modo oscuro están en `values-night/colors.xml` y los textos en inglés en `values-en/strings.xml`, así que Android los elige según la configuración del teléfono.

## Estructura

```
app/src/main/java/com/sralberto/paperrecipes/
├─ InicioActivity.java      Logotipo al abrir la app
├─ MainActivity.java        Lista en celular, lista y detalle en tableta
├─ ListaFragment.java       Buscador y cuadrícula de recetas
├─ DetalleFragment.java     Foto, ingredientes, pasos, editar y borrar
├─ DetalleActivity.java     Aloja el detalle en celular
├─ RegistroActivity.java    Nueva receta o edición, con foto
├─ PrepararActivity.java    Los tres modos de preparación
├─ BaseDatosRecetas.java    SQLite
├─ Escala.java              Cálculos de escala
├─ FiltroRecetas.java       Búsqueda
├─ ValidadorReceta.java     Validación del registro
├─ Fotos.java               Archivos de foto y carga con Glide
├─ RecetaAdaptador.java     Tarjetas del RecyclerView
├─ VistasReceta.java        Listas de ingredientes y pasos
├─ Receta.java, Ingrediente.java
└─ Recetas.java             Recetas de ejemplo
app/src/test/java/com/sralberto/paperrecipes/
└─ EscalaTest, FiltroRecetasTest, ValidadorRecetaTest
```

## Cómo correrla

Abre la carpeta `PaperRecipes` en Android Studio, deja que sincronice Gradle y córrela en un emulador o en un teléfono con Android 8.0 o superior. Para ver la versión de tableta usa un emulador de tableta, como Pixel Tablet.

Las pruebas se corren con clic derecho en la carpeta `app/src/test` y Run Tests, o con `gradlew test` en la terminal.

Para volver a los datos de ejemplo, borra los datos de la app en Ajustes o desinstálala.

La tipografía Roboto Mono va incluida en `res/font` y se distribuye con la licencia SIL Open Font License. El texto de la licencia está en `licencias/`.
