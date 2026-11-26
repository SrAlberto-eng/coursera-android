package com.sralberto.paperrecipes;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Búsqueda de la lista. Encuentra la receta por su nombre o por uno de sus ingredientes,
 * sin importar mayúsculas ni acentos. Así "azucar" encuentra "Azúcar".
 */
public final class FiltroRecetas {

    private FiltroRecetas() {
    }

    public static List<Receta> filtrar(List<Receta> recetas, String consulta) {
        String buscado = normalizar(consulta);
        if (buscado.isEmpty()) {
            return new ArrayList<>(recetas);
        }
        List<Receta> resultado = new ArrayList<>();
        for (Receta receta : recetas) {
            if (coincide(receta, buscado)) {
                resultado.add(receta);
            }
        }
        return resultado;
    }

    private static boolean coincide(Receta receta, String buscado) {
        if (normalizar(receta.getNombre()).contains(buscado)) {
            return true;
        }
        for (Ingrediente ingrediente : receta.getIngredientes()) {
            if (normalizar(ingrediente.getNombre()).contains(buscado)) {
                return true;
            }
        }
        return false;
    }

    static String normalizar(String texto) {
        if (texto == null) {
            return "";
        }
        String sinAcentos = Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return sinAcentos.toLowerCase(Locale.ROOT).trim();
    }
}
