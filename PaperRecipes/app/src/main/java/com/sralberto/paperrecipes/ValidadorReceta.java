package com.sralberto.paperrecipes;

import java.util.ArrayList;
import java.util.List;

/**
 * Revisa lo que se escribió en el registro antes de guardar y arma la receta.
 * Cada fila de ingrediente llega como {nombre, cantidad, unidad}.
 * Las filas y los pasos vacíos se ignoran.
 */
public final class ValidadorReceta {

    public enum Resultado {
        VALIDA,
        NOMBRE_VACIO,
        PORCIONES_INVALIDAS,
        SIN_INGREDIENTES,
        INGREDIENTE_INCOMPLETO
    }

    private static final int NOMBRE = 0;
    private static final int CANTIDAD = 1;
    private static final int UNIDAD = 2;

    private ValidadorReceta() {
    }

    public static Resultado validar(String nombre, String porciones, List<String[]> filas) {
        if (vacio(nombre)) {
            return Resultado.NOMBRE_VACIO;
        }
        if (leerPorciones(porciones) <= 0) {
            return Resultado.PORCIONES_INVALIDAS;
        }
        int completas = 0;
        for (String[] fila : filas) {
            if (filaVacia(fila)) {
                continue;
            }
            double cantidad = Escala.leerNumero(fila[CANTIDAD]);
            if (vacio(fila[NOMBRE]) || !(cantidad > 0)) {
                return Resultado.INGREDIENTE_INCOMPLETO;
            }
            completas++;
        }
        return completas == 0 ? Resultado.SIN_INGREDIENTES : Resultado.VALIDA;
    }

    /** Arma la receta. Llamar solo cuando validar() devolvió VALIDA. */
    public static Receta construir(long id, String nombre, String descripcion, String porciones,
                                   String foto, List<String[]> filas, List<String> pasos) {
        List<Ingrediente> ingredientes = new ArrayList<>();
        for (String[] fila : filas) {
            if (!filaVacia(fila)) {
                ingredientes.add(new Ingrediente(fila[NOMBRE].trim(),
                        Escala.leerNumero(fila[CANTIDAD]), fila[UNIDAD].trim()));
            }
        }
        List<String> pasosLimpios = new ArrayList<>();
        for (String paso : pasos) {
            if (!vacio(paso)) {
                pasosLimpios.add(paso.trim());
            }
        }
        return new Receta(id, nombre.trim(), descripcion == null ? "" : descripcion.trim(),
                leerPorciones(porciones), foto, ingredientes, pasosLimpios);
    }

    static int leerPorciones(String texto) {
        if (vacio(texto)) {
            return 0;
        }
        try {
            return Integer.parseInt(texto.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static boolean filaVacia(String[] fila) {
        return vacio(fila[NOMBRE]) && vacio(fila[CANTIDAD]);
    }

    private static boolean vacio(String texto) {
        return texto == null || texto.trim().isEmpty();
    }
}
