package com.sralberto.paperrecipes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class EscalaTest {

    private static final double DELTA = 0.0001;

    @Test
    public void formatearQuitaCerosSobrantes() {
        assertEquals("2", Escala.formatear(2.0));
        assertEquals("1.5", Escala.formatear(1.50));
        assertEquals("0.33", Escala.formatear(1.0 / 3.0));
        assertEquals("0", Escala.formatear(0.0));
        assertEquals("1500", Escala.formatear(1500));
    }

    @Test
    public void formatearMuestraGuionSiNoHayValor() {
        assertEquals(Escala.SIN_VALOR, Escala.formatear(Double.NaN));
    }

    @Test
    public void escalarMultiplicaLaCantidad() {
        assertEquals(500, Escala.escalar(250, 2), DELTA);
        assertEquals(62.5, Escala.escalar(250, 0.25), DELTA);
    }

    @Test
    public void factorDesdeDisponibleEsElCasoDeLaMaqueta() {
        // La receta usa 300 ml de leche y hay 450 ml
        assertEquals(1.5, Escala.factorDesdeDisponible(300, 450), DELTA);
    }

    @Test
    public void factorDesdeDisponibleRechazaDatosInvalidos() {
        assertTrue(Double.isNaN(Escala.factorDesdeDisponible(0, 450)));
        assertTrue(Double.isNaN(Escala.factorDesdeDisponible(300, -1)));
        assertTrue(Double.isNaN(Escala.factorDesdeDisponible(300, Double.NaN)));
    }

    @Test
    public void porcionesSeMultiplicanPorElFactor() {
        assertEquals(8, Escala.porciones(4, 2), DELTA);
        assertEquals(6, Escala.porciones(4, 1.5), DELTA);
    }

    @Test
    public void leerNumeroAceptaPuntoYComa() {
        assertEquals(1.5, Escala.leerNumero("1.5"), DELTA);
        assertEquals(1.5, Escala.leerNumero("1,5"), DELTA);
        assertEquals(2, Escala.leerNumero("  2 "), DELTA);
    }

    @Test
    public void leerNumeroRechazaTextoVacioNegativoONoNumerico() {
        assertTrue(Double.isNaN(Escala.leerNumero("")));
        assertTrue(Double.isNaN(Escala.leerNumero(null)));
        assertTrue(Double.isNaN(Escala.leerNumero("abc")));
        assertTrue(Double.isNaN(Escala.leerNumero("-3")));
        assertTrue(Double.isNaN(Escala.leerNumero("Infinity")));
    }
}
