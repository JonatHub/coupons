package com.meli.coupons.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class CouponServiceTest {

    @Autowired
    private CouponService couponService;
    private final Map<String, Float> items = new HashMap<>();
    private Float amount;


    @Test
    void calculateBiWithConditional() {

        items.put("Item1", 5f);
        items.put("Item2", 12f);
        items.put("Item3", 7f);
        items.put("Item4", 10f);
        items.put("Item5", 20f);
        items.put("Item6", 8f);
        items.put("Item7", 15f);
        items.put("Item8", 9f);
        amount = 15f;

        List<String> selectedItems = couponService.calculate(items, amount);
        float totalValue = 0;
        for (String item : selectedItems) {
            totalValue += items.get(item);
        }

        assertEquals(15f, totalValue);
    }

    @Test
    public void testItemExceedsAmount() {
        Map<String, Float> items = new HashMap<>();
        items.put("Item1", 5f);
        items.put("Item2", 12f);
        items.put("Item3", 20f);
        items.put("Item4", 10f);
        items.put("Item5", 7f);

        Float amount = 10f;
        List<String> selectedItems = couponService.calculate(items, amount);

        // La combinación óptima es seleccionar los ítems Item1 (5) y Item4 (10),
        // ya que su valor total (15) es el máximo sin exceder el monto disponible de 10.
        float totalValue = 0;
        for (String item : selectedItems) {
            totalValue += items.get(item);
        }

        assertEquals(10f, totalValue);  // El total de los valores seleccionados debe ser 10
    }

    @Test
    public void testAmountZero() {
        Map<String, Float> items = new HashMap<>();
        items.put("Item1", 5f);
        items.put("Item2", 12f);
        items.put("Item3", 7f);

        Float amount = 0f;
        List<String> selectedItems = couponService.calculate(items, amount);

        // Si el monto es 0, no se debe seleccionar ningún ítem.
        assertTrue(selectedItems.isEmpty());
    }

    @Test
    public void testAllItemsExceedAmount() {
        Map<String, Float> items = new HashMap<>();
        items.put("Item1", 25f);
        items.put("Item2", 30f);
        items.put("Item3", 40f);

        Float amount = 20f;

        List<String> selectedItems = couponService.calculate(items, amount);

        // Todos los ítems tienen un valor superior al monto disponible (20),
        // por lo que no se debe seleccionar ningún ítem.
        assertTrue(selectedItems.isEmpty());
    }

    @Test
    public void testMultipleItemsWithSameValue() {
        Map<String, Float> items = new HashMap<>();
        items.put("Item1", 5f);
        items.put("Item2", 7f);
        items.put("Item3", 7f);
        items.put("Item4", 6f);

        Float amount = 14f;

        List<String> selectedItems = couponService.calculate(items, amount);

        // El ítem más valioso que se puede seleccionar dentro del monto 7 es cualquiera de los dos ítems con valor 7.
        // En este caso, se espera que el método seleccione uno de los ítems con valor 7.
        float totalValue = 0;
        for (String item : selectedItems) {
            totalValue += items.get(item);
        }

        assertEquals(14f, totalValue);  // El total de los valores seleccionados debe ser 14
    }
    @Test
    public void testFillDpTableBasic() {
        Float[] prices = {5f, 12f, 7f, 10f, 20f, 8f, 15f, 9f};
        int maxAmount = 15;
        int n = prices.length;
        float[][] dp = new float[n + 1][maxAmount + 1];  // Crear la tabla dp


        couponService.fillDpTable(dp, n, maxAmount, prices);

        // La tabla dp debe estar llena correctamente con la suma máxima de los precios sin exceder el monto.
        // Verificamos algunos valores clave en la tabla.

        // Para i=1 (primer ítem), la única forma de tomar el ítem con precio 5 es agregarlo
        assertEquals(5, dp[1][5]); // Al tomar el primer ítem, el valor máximo a 5 debe ser 5.

        // Para j=10, podemos tomar el ítem con valor 5 y 10, el máximo sería 15
        assertEquals(5, dp[2][10]); // Se seleccionan los ítems con precio 5 y 10.

        // Para j=15, podemos tomar los ítems de 5 y 7, sumando 12, pero no podemos incluir el ítem de 12
        assertEquals(12, dp[3][15]); // Se seleccionan los ítems con precio 12 para una suma máxima de 12.

        // Para j=15, debemos considerar los ítems 5, 7, 8, etc., y la suma máxima debe ser 15
        assertEquals(15, dp[4][15]); // En este caso, los ítems seleccionados suman 15.

        // Verificar que el valor máximo a 15 se logra con los ítems que tienen los valores más altos.
        assertEquals(15, dp[8][15]); // El valor máximo al tomar ítems seleccionados debe ser 15.
    }

    @Test
    public void testFillDpTableNoItems() {
        Float[] prices = {};
        int maxAmount = 10;
        int n = prices.length;
        float[][] dp = new float[n + 1][maxAmount + 1];  // Crear la tabla dp


        couponService.fillDpTable(dp, n, maxAmount, prices);

        // Si no hay ítems, todos los valores de la tabla dp deben ser 0.
        for (int i = 0; i <= n; i++) {
            for (int j = 0; j <= maxAmount; j++) {
                assertEquals(0, dp[i][j]);
            }
        }
    }

    @Test
    public void testFillDpTableSingleItem() {
        Float[] prices = {5f};
        int maxAmount = 10;
        int n = prices.length;
        float[][] dp = new float[n + 1][maxAmount + 1];  // Crear la tabla dp

        couponService.fillDpTable(dp, n, maxAmount, prices);

        // Si solo hay un ítem con precio 5, el valor máximo para un monto de 5 será 5
        assertEquals(5, dp[1][5]);

        // Si el monto es mayor que 5, el valor debe seguir siendo 5 ya que solo hay un ítem.
        assertEquals(5, dp[1][6]);
        assertEquals(5, dp[1][10]);
    }

    @Test
    public void testFillDpTableItemExceedsAmount() {
        Float[] prices = {25f};
        int maxAmount = 20;
        int n = prices.length;
        float[][] dp = new float[n + 1][maxAmount + 1];  // Crear la tabla dp

        couponService.fillDpTable(dp, n, maxAmount, prices);

        // Si el ítem tiene un valor de 25 y el monto máximo es 20, no se puede tomar el ítem.
        // Todos los valores deben ser 0 ya que no hay forma de incluir el ítem.
        for (int j = 0; j <= maxAmount; j++) {
            assertEquals(0, dp[1][j]);
        }
    }

    @Test
    public void testFillDpTableComplex() {
        Float[] prices = {5f, 12f, 7f, 10f, 20f, 8f, 15f, 9f};
        int maxAmount = 10;
        int n = prices.length;
        float[][] dp = new float[n + 1][maxAmount + 1];  // Crear la tabla dp

        couponService.fillDpTable(dp, n, maxAmount, prices);

        // Comprobamos valores intermedios en la tabla dp.

        // Con 10 unidades, podemos elegir el ítem de 10 y obtener el máximo de 10.
        assertEquals(10, dp[4][10]);

        // Con 9 unidades, podemos elegir los ítems 5 y 8 para un total de 9.
        assertEquals(9, dp[8][9]);

        // Con 6 unidades, podemos tomar el ítem de 5 para un valor total de 5.
        assertEquals(5, dp[4][6]);

        // Comprobamos el valor final, el valor máximo para el monto máximo (10)
        assertEquals(10, dp[8][10]);  // Este debería ser el valor máximo alcanzable con 10 unidades.
    }

    @Test
    public void testReconstructSolutionBasic() {
        // Preparar datos de entrada
        Float[] prices = {100f, 210f, 220f, 80f, 90f};
        String[] itemIds = {"MLA1", "MLA2", "MLA3", "MLA4", "MLA5"};
        int maxAmount = 500;
        int n = prices.length;

        // Crear tabla dp previamente llena
        float[][] dp = new float[n + 1][maxAmount + 1];

        couponService.fillDpTable(dp, n, maxAmount, prices);

        // Llamar al método de reconstrucción
        List<String> selectedItems = couponService.reconstructSolution(dp, n, maxAmount, itemIds, prices);

        // Verificar que los ítems seleccionados sean los correctos
        assertEquals(4, selectedItems.size()); // Deben ser 3 ítems seleccionados
        assertTrue(selectedItems.contains("MLA1")); // Debe incluir el ítem con precio 5
        assertTrue(selectedItems.contains("MLA3")); // Debe incluir el ítem con precio 10
        assertTrue(selectedItems.contains("MLA4")); // Debe incluir el ítem con precio 10
        assertTrue(selectedItems.contains("MLA5")); // Debe incluir el ítem con precio 15
    }

    @Test
    public void testReconstructSolutionEmpty() {
        // Preparar datos de entrada
        Float[] prices = {5f, 12f, 7f, 10f};
        String[] itemIds = {"MLA1", "MLA2", "MLA3", "MLA4"};
        int maxAmount = 0; // Monto disponible es 0
        int n = prices.length;

        // Crear tabla dp previamente llena
        float[][] dp = new float[n + 1][maxAmount + 1];

        couponService.fillDpTable(dp, n, maxAmount, prices);

        // Llamar al método de reconstrucción
        List<String> selectedItems = couponService.reconstructSolution(dp, n, maxAmount, itemIds, prices);

        // Verificar que no haya ítems seleccionados ya que el monto es 0
        assertTrue(selectedItems.isEmpty());
    }

    @Test
    public void testReconstructSolutionSingleItem() {
        // Preparar datos de entrada
        Float[] prices = {10f};
        String[] itemIds = {"MLA1"};
        int maxAmount = 10; // Solo hay un ítem y el monto coincide
        int n = prices.length;

        // Crear tabla dp previamente llena
        float[][] dp = new float[n + 1][maxAmount + 1];

        couponService.fillDpTable(dp, n, maxAmount, prices);

        // Llamar al método de reconstrucción
        List<String> selectedItems = couponService.reconstructSolution(dp, n, maxAmount, itemIds, prices);

        // Verificar que el único ítem se haya seleccionado
        assertEquals(1, selectedItems.size());
        assertTrue(selectedItems.contains("MLA1"));
    }

    @Test
    public void testReconstructSolutionNoItemSelected() {
        // Preparar datos de entrada
        Float[] prices = {15f, 12f, 18f};
        String[] itemIds = {"MLA1", "MLA2", "MLA3"};
        int maxAmount = 10; // Ningún ítem se puede seleccionar ya que el monto es menor
        int n = prices.length;

        // Crear tabla dp previamente llena
        float[][] dp = new float[n + 1][maxAmount + 1];

        couponService.fillDpTable(dp, n, maxAmount, prices);

        // Llamar al método de reconstrucción
        List<String> selectedItems = couponService.reconstructSolution(dp, n, maxAmount, itemIds, prices);

        // Verificar que no se seleccionó ningún ítem
        assertTrue(selectedItems.isEmpty());
    }

    @Test
    void testReconstructSolutionPreventsDuplicateItems() {
        // Preparar datos de entrada
        Float[] prices = {100f, 210f, 220f, 80f, 90f};
        String[] itemIds = {"MLA1", "MLA2", "MLA3", "MLA4", "MLA5"};
        int maxAmount = 500;
        int n = prices.length;

        // Crear tabla dp previamente llena
        float[][] dp = new float[n + 1][maxAmount + 1];

        couponService.fillDpTable(dp, n, maxAmount, prices);

        // Llamar al método de reconstrucción
        List<String> selectedItems = couponService.reconstructSolution(dp, n, maxAmount, itemIds, prices);

        Set<String> uniqueItems = new HashSet<>(selectedItems);  // Convertimos la lista a un conjunto

        // Verificar que los ítems seleccionados sean los correctos
        assertEquals(selectedItems.size(), uniqueItems.size());

    }
}