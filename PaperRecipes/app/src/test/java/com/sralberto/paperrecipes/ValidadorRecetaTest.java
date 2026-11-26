package com.sralberto.paperrecipes;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ValidadorRecetaTest {

    private static final List<String[]> FILAS_COMPLETAS = Arrays.asList(
            new String[]{"Tortillas en triángulos", "500", "g"},
            new String[]{"Chile guajillo", "4", "pza"});

    @Test
    public void recetaCompletaEsValida() {
        assertEquals(ValidadorReceta.Resultado.VALIDA,
                ValidadorReceta.validar("Chilaquiles rojos", "4", FILAS_COMPLETAS));
    }

    @Test
    public void nombreVacioNoEsValido() {
        assertEquals(ValidadorReceta.Resultado.NOMBRE_VACIO,
                ValidadorReceta.validar("  ", "4", FILAS_COMPLETAS));
    }

    @Test
    public void porcionesDebenSerMayoresACero() {
        assertEquals(ValidadorReceta.Resultado.PORCIONES_INVALIDAS,
                ValidadorReceta.validar("Chilaquiles", "0", FILAS_COMPLETAS));
        assertEquals(ValidadorReceta.Resultado.PORCIONES_INVALIDAS,
                ValidadorReceta.validar("Chilaquiles", "", FILAS_COMPLETAS));
    }

    @Test
    public void necesitaAlMenosUnIngrediente() {
        List<String[]> soloVacias = Collections.singletonList(new String[]{"", "", "g"});
        assertEquals(ValidadorReceta.Resultado.SIN_INGREDIENTES,
                ValidadorReceta.validar("Chilaquiles", "4", soloVacias));
    }

    @Test
    public void ingredienteSinCantidadEstaIncompleto() {
        List<String[]> filas = Collections.singletonList(new String[]{"Crema", "", "ml"});
        assertEquals(ValidadorReceta.Resultado.INGREDIENTE_INCOMPLETO,
                ValidadorReceta.validar("Chilaquiles", "4", filas));
    }

    @Test
    public void construirIgnoraFilasYPasosVacios() {
        List<String[]> filas = Arrays.asList(
                new String[]{"Crema", "100", "ml"},
                new String[]{"", "", "g"});
        Receta receta = ValidadorReceta.construir(Receta.SIN_ID, " Chilaquiles ", "", "4", null,
                filas, Arrays.asList("Fríe las tortillas.", "  "));
        assertEquals("Chilaquiles", receta.getNombre());
        assertEquals(4, receta.getPorciones());
        assertEquals(1, receta.getIngredientes().size());
        assertEquals(100, receta.getIngredientes().get(0).getCantidad(), 0.0001);
        assertEquals(1, receta.getPasos().size());
    }
}
