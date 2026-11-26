package com.sralberto.paperrecipes;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Cálculos para escalar una receta. No usa nada de Android, así que se prueba con JUnit.
 * Un factor inválido se representa con Double.NaN y en pantalla se muestra como un guion.
 */
public final class Escala {

    public static final String SIN_VALOR = "–";

    private Escala() {
    }

    /** Cantidad de un ingrediente después de aplicar el factor. */
    public static double escalar(double cantidad, double factor) {
        return cantidad * factor;
    }

    /**
     * Factor del cálculo inverso. Si la receta usa 300 ml de leche y hay 450 ml, el factor es 1.5.
     * Devuelve NaN si el ingrediente no tiene cantidad base o la cantidad disponible no es válida.
     */
    public static double factorDesdeDisponible(double cantidadBase, double disponible) {
        if (!(cantidadBase > 0) || !esValido(disponible)) {
            return Double.NaN;
        }
        return disponible / cantidadBase;
    }

    /** Porciones que rinde la receta con el factor aplicado. */
    public static double porciones(int porcionesBase, double factor) {
        return porcionesBase * factor;
    }

    /** Lee lo que escribe el usuario. Acepta punto o coma decimal y rechaza negativos. */
    public static double leerNumero(String texto) {
        if (texto == null) {
            return Double.NaN;
        }
        String limpio = texto.trim().replace(',', '.');
        if (limpio.isEmpty()) {
            return Double.NaN;
        }
        try {
            double valor = Double.parseDouble(limpio);
            return esValido(valor) ? valor : Double.NaN;
        } catch (NumberFormatException e) {
            return Double.NaN;
        }
    }

    public static boolean esValido(double valor) {
        return !Double.isNaN(valor) && !Double.isInfinite(valor) && valor >= 0;
    }

    /** Muestra 2 en lugar de 2.0 y 1.5 en lugar de 1.50. Redondea a dos decimales. */
    public static String formatear(double valor) {
        if (Double.isNaN(valor) || Double.isInfinite(valor)) {
            return SIN_VALOR;
        }
        BigDecimal redondeado = BigDecimal.valueOf(valor)
                .setScale(2, RoundingMode.HALF_UP)
                .stripTrailingZeros();
        // stripTrailingZeros deja 0.00 como "0E-2", por eso el cero se trata aparte
        if (redondeado.signum() == 0) {
            return "0";
        }
        return redondeado.toPlainString();
    }
}
