package com.sralberto.paperrecipes;

import java.util.Arrays;
import java.util.List;

/**
 * Recetas de ejemplo (datos dummy). BaseDatosRecetas las guarda en SQLite la primera vez
 * que se crea la base, así la app arranca con algo que probar.
 */
public final class Recetas {

    public static final List<Receta> EJEMPLOS = Arrays.asList(
            new Receta("Hotcakes", "Esponjosos, para un domingo sin prisa.", 4,
                    new Ingrediente[]{
                            new Ingrediente("Harina de trigo", 250, "g"),
                            new Ingrediente("Leche", 300, "ml"),
                            new Ingrediente("Huevo", 2, "pza"),
                            new Ingrediente("Azúcar", 30, "g"),
                            new Ingrediente("Polvo para hornear", 10, "g"),
                            new Ingrediente("Mantequilla derretida", 40, "g")
                    },
                    "Mezcla la harina, el azúcar y el polvo para hornear en un tazón.",
                    "Agrega la leche, el huevo y la mantequilla, y bate hasta integrar.",
                    "Cocina la mezcla en un sartén caliente hasta que dore de ambos lados."),
            new Receta("Guacamole", "Fresco y rápido, listo en minutos.", 2,
                    new Ingrediente[]{
                            new Ingrediente("Aguacate", 2, "pza"),
                            new Ingrediente("Jitomate", 1, "pza"),
                            new Ingrediente("Cebolla", 0.25, "pza"),
                            new Ingrediente("Cilantro", 10, "g"),
                            new Ingrediente("Limón", 1, "pza")
                    },
                    "Machaca el aguacate en un tazón.",
                    "Pica el jitomate, la cebolla y el cilantro, y mézclalos con el aguacate.",
                    "Agrega el jugo de limón y sal al gusto."),
            new Receta("Chilaquiles rojos", "Crujientes, con salsa de chile guajillo.", 4,
                    new Ingrediente[]{
                            new Ingrediente("Tortillas en triángulos", 500, "g"),
                            new Ingrediente("Chile guajillo", 4, "pza"),
                            new Ingrediente("Jitomate", 2, "pza"),
                            new Ingrediente("Ajo", 1, "diente"),
                            new Ingrediente("Crema", 100, "ml"),
                            new Ingrediente("Queso fresco", 100, "g"),
                            new Ingrediente("Cebolla", 0.5, "pza")
                    },
                    "Hierve los chiles y el jitomate, y licúalos con el ajo.",
                    "Fríe las tortillas hasta que estén crujientes.",
                    "Baña las tortillas con la salsa y sirve con crema, queso y cebolla."),
            new Receta("Pozole rojo", "Para compartir en una reunión grande.", 10,
                    new Ingrediente[]{
                            new Ingrediente("Maíz pozolero", 1, "kg"),
                            new Ingrediente("Carne de cerdo", 1.5, "kg"),
                            new Ingrediente("Chile guajillo", 6, "pza"),
                            new Ingrediente("Chile ancho", 2, "pza"),
                            new Ingrediente("Ajo", 4, "diente"),
                            new Ingrediente("Cebolla", 1, "pza"),
                            new Ingrediente("Orégano", 5, "g"),
                            new Ingrediente("Lechuga", 1, "pza"),
                            new Ingrediente("Rábano", 6, "pza")
                    },
                    "Cuece la carne con el maíz, la cebolla y el ajo.",
                    "Licúa los chiles hidratados y agrégalos al caldo.",
                    "Sirve con lechuga, rábano y orégano."),
            new Receta("Pizza margarita", "Clásica, con albahaca fresca.", 1,
                    new Ingrediente[]{
                            new Ingrediente("Masa para pizza", 250, "g"),
                            new Ingrediente("Salsa de tomate", 80, "ml"),
                            new Ingrediente("Mozzarella", 125, "g"),
                            new Ingrediente("Albahaca", 5, "g"),
                            new Ingrediente("Aceite de oliva", 10, "ml"),
                            new Ingrediente("Sal", 2, "g")
                    },
                    "Extiende la masa y cúbrela con la salsa.",
                    "Agrega la mozzarella y hornea a 250 °C por 10 minutos.",
                    "Termina con albahaca y un hilo de aceite de oliva."),
            new Receta("Agua de horchata", "Fresca, para acompañar la comida.", 8,
                    new Ingrediente[]{
                            new Ingrediente("Arroz", 200, "g"),
                            new Ingrediente("Agua", 2, "l"),
                            new Ingrediente("Leche evaporada", 360, "ml"),
                            new Ingrediente("Azúcar", 150, "g"),
                            new Ingrediente("Canela", 1, "raja")
                    },
                    "Remoja el arroz con la canela durante 4 horas.",
                    "Licúa el arroz con parte del agua y cuela la mezcla.",
                    "Agrega el resto del agua, la leche y el azúcar, y sirve con hielo.")
    );

    private Recetas() {
    }
}
