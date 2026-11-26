package com.sralberto.paperrecipes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.List;

public class FiltroRecetasTest {

    private final List<Receta> recetas = Recetas.EJEMPLOS;

    @Test
    public void consultaVaciaDevuelveTodas() {
        assertEquals(recetas.size(), FiltroRecetas.filtrar(recetas, "").size());
        assertEquals(recetas.size(), FiltroRecetas.filtrar(recetas, "   ").size());
        assertEquals(recetas.size(), FiltroRecetas.filtrar(recetas, null).size());
    }

    @Test
    public void buscaPorNombreSinImportarMayusculas() {
        List<Receta> resultado = FiltroRecetas.filtrar(recetas, "POZOLE");
        assertEquals(1, resultado.size());
        assertEquals("Pozole rojo", resultado.get(0).getNombre());
    }

    @Test
    public void ignoraAcentos() {
        // "Azúcar" aparece en los hotcakes y en el agua de horchata
        List<Receta> resultado = FiltroRecetas.filtrar(recetas, "azucar");
        assertEquals(2, resultado.size());
    }

    @Test
    public void buscaPorIngrediente() {
        List<Receta> resultado = FiltroRecetas.filtrar(recetas, "aguacate");
        assertEquals(1, resultado.size());
        assertEquals("Guacamole", resultado.get(0).getNombre());
    }

    @Test
    public void sinCoincidenciasDevuelveListaVacia() {
        assertTrue(FiltroRecetas.filtrar(recetas, "sushi").isEmpty());
    }
}
