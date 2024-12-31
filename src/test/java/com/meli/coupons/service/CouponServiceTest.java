package com.meli.coupons.service;

import com.meli.coupons.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

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

        // The optimal combination is to select items Item1 (5) and Item4 (10),
        // because their total value (15) is the maximum without exceeding the available amount of 10.
        float totalValue = 0;
        for (String item : selectedItems) {
            totalValue += items.get(item);
        }

        assertEquals(10f, totalValue);  // The total of the selected values should be 10
    }

    @Test
    public void testAmountZero() {
        Map<String, Float> items = new HashMap<>();
        items.put("Item1", 5f);
        items.put("Item2", 12f);
        items.put("Item3", 7f);

        Float amount = 0f;

        // If the amount is 0, no items should be selected.
        assertThrows(BusinessException.class, () -> couponService.calculate(items, amount), "Not enough money to buy any item");
    }

    @Test
    public void testAllItemsExceedAmount() {
        Map<String, Float> items = new HashMap<>();
        items.put("Item1", 25f);
        items.put("Item2", 30f);
        items.put("Item3", 40f);

        Float amount = 20f;

        // All items have a value higher than the available amount (20),
        // so no items should be selected.
        assertThrows(BusinessException.class, () -> couponService.calculate(items, amount), "Not enough money to buy any item");

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

        // The most valuable item that can be selected within the amount 7 is either of the two items priced at 7.
        // In this case, the method is expected to select one of the items priced at 7.
        float totalValue = 0;
        for (String item : selectedItems) {
            totalValue += items.get(item);
        }

        assertEquals(14f, totalValue);  // The total of the selected values should be 14
    }
    @Test
    public void testFillDpTableBasic() {
        Float[] prices = {5f, 12f, 7f, 10f, 20f, 8f, 15f, 9f};
        int maxAmount = 15;
        int n = prices.length;
        float[][] dp = new float[n + 1][maxAmount + 1];  // Create the dp table

        couponService.fillDpTable(dp, n, maxAmount, prices);

        // The dp table should be correctly filled with the maximum sum of prices without exceeding the amount.
        // We check some key values in the table.

        // For i=1 (first item), the only way to take the item priced at 5 is to add it
        assertEquals(5, dp[1][5]); // When taking the first item, the maximum value at 5 should be 5.

        // For j=10, we can take the items priced at 5 and 10, the maximum would be 15
        assertEquals(5, dp[2][10]); // The items priced at 5 and 10 are selected.

        // For j=15, we can take items priced at 5 and 7, summing 12, but cannot include the item priced at 12
        assertEquals(12, dp[3][15]); // The item priced at 12 is selected for a maximum sum of 12.

        // For j=15, we should consider items priced at 5, 7, 8, etc., and the maximum sum should be 15
        assertEquals(15, dp[4][15]); // In this case, the selected items sum to 15.

        // Verify that the maximum value at 15 is achieved with the items that have the highest values.
        assertEquals(15, dp[8][15]); // The maximum value when selecting the items should be 15.
    }

    @Test
    public void testFillDpTableNoItems() {
        Float[] prices = {};
        int maxAmount = 10;
        int n = prices.length;
        float[][] dp = new float[n + 1][maxAmount + 1];  // Create the dp table

        couponService.fillDpTable(dp, n, maxAmount, prices);

        // If there are no items, all values in the dp table should be 0.
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
        float[][] dp = new float[n + 1][maxAmount + 1];  // Create the dp table

        couponService.fillDpTable(dp, n, maxAmount, prices);

        // If there is only one item priced at 5, the maximum value for an amount of 5 will be 5
        assertEquals(5, dp[1][5]);

        // If the amount is greater than 5, the value should remain 5 as there is only one item.
        assertEquals(5, dp[1][6]);
        assertEquals(5, dp[1][10]);
    }

    @Test
    public void testFillDpTableItemExceedsAmount() {
        Float[] prices = {25f};
        int maxAmount = 20;
        int n = prices.length;
        float[][] dp = new float[n + 1][maxAmount + 1];  // Create the dp table

        couponService.fillDpTable(dp, n, maxAmount, prices);

        // If the item is priced at 25 and the maximum amount is 20, the item cannot be taken.
        // All values should be 0 as there is no way to include the item.
        for (int j = 0; j <= maxAmount; j++) {
            assertEquals(0, dp[1][j]);
        }
    }

    @Test
    public void testFillDpTableComplex() {
        Float[] prices = {5f, 12f, 7f, 10f, 20f, 8f, 15f, 9f};
        int maxAmount = 10;
        int n = prices.length;
        float[][] dp = new float[n + 1][maxAmount + 1];  // Create the dp table

        couponService.fillDpTable(dp, n, maxAmount, prices);

        // Check intermediate values in the dp table.

        // With 10 units, we can choose the item priced at 10 and get the maximum of 10.
        assertEquals(10, dp[4][10]);

        // With 9 units, we can choose items priced at 5 and 8 for a total of 9.
        assertEquals(9, dp[8][9]);

        // With 6 units, we can take the item priced at 5 for a total value of 5.
        assertEquals(5, dp[4][6]);

        // Check the final value, the maximum value for the maximum amount (10)
        assertEquals(10, dp[8][10]);  // This should be the maximum value achievable with 10 units.
    }

    @Test
    public void testReconstructSolutionBasic() {
        // Prepare input data
        Float[] prices = {100f, 210f, 220f, 80f, 90f};
        String[] itemIds = {"MLA1", "MLA2", "MLA3", "MLA4", "MLA5"};
        int maxAmount = 500;
        int n = prices.length;

        // Create dp table already filled
        float[][] dp = new float[n + 1][maxAmount + 1];

        couponService.fillDpTable(dp, n, maxAmount, prices);

        // Call the reconstruction method
        List<String> selectedItems = couponService.reconstructSolution(dp, n, maxAmount, itemIds, prices);

        // Verify that the selected items are correct
        assertEquals(4, selectedItems.size()); // There should be 3 items selected
        assertTrue(selectedItems.contains("MLA1")); // It should include the item priced at 5
        assertTrue(selectedItems.contains("MLA3")); // It should include the item priced at 10
        assertTrue(selectedItems.contains("MLA4")); // It should include the item priced at 10
        assertTrue(selectedItems.contains("MLA5")); // It should include the item priced at 15
    }

    @Test
    public void testReconstructSolutionEmpty() {
        // Prepare input data
        Float[] prices = {5f, 12f, 7f, 10f};
        String[] itemIds = {"MLA1", "MLA2", "MLA3", "MLA4"};
        int maxAmount = 0; // Available amount is 0
        int n = prices.length;

        // Create dp table already filled
        float[][] dp = new float[n + 1][maxAmount + 1];

        couponService.fillDpTable(dp, n, maxAmount, prices);

        // Call the reconstruction method
        assertThrows(BusinessException.class, () -> couponService.reconstructSolution(dp, n, maxAmount, itemIds, prices), "Not enough money to buy any item");
    }

    @Test
    public void testReconstructSolutionSingleItem() {
        // Prepare input data
        Float[] prices = {10f};
        String[] itemIds = {"MLA1"};
        int maxAmount = 10; // Only one item and the amount matches
        int n = prices.length;

        // Create dp table already filled
        float[][] dp = new float[n + 1][maxAmount + 1];

        couponService.fillDpTable(dp, n, maxAmount, prices);

        // Call the reconstruction method
        List<String> selectedItems = couponService.reconstructSolution(dp, n, maxAmount, itemIds, prices);

        // Verify that the only item was selected
        assertEquals(1, selectedItems.size());
        assertTrue(selectedItems.contains("MLA1"));
    }

    @Test
    public void testReconstructSolutionNoItemSelected() {
        // Prepare input data
        Float[] prices = {15f, 12f, 18f};
        String[] itemIds = {"MLA1", "MLA2", "MLA3"};
        int maxAmount = 10; // No item can be selected since the amount is too low
        int n = prices.length;

        // Create dp table already filled
        float[][] dp = new float[n + 1][maxAmount + 1];

        couponService.fillDpTable(dp, n, maxAmount, prices);

        // Call the reconstruction method
        assertThrows(BusinessException.class, () -> couponService.reconstructSolution(dp, n, maxAmount, itemIds, prices), "Not enough money to buy any item");
    }

    @Test
    void testReconstructSolutionPreventsDuplicateItems() {
        // Prepare input data
        Float[] prices = {100f, 210f, 220f, 80f, 90f};
        String[] itemIds = {"MLA1", "MLA2", "MLA3", "MLA4", "MLA5"};
        int maxAmount = 500;
        int n = prices.length;

        // Create dp table already filled
        float[][] dp = new float[n + 1][maxAmount + 1];

        couponService.fillDpTable(dp, n, maxAmount, prices);

        // Call the reconstruction method
        List<String> selectedItems = couponService.reconstructSolution(dp, n, maxAmount, itemIds, prices);

        Set<String> uniqueItems = new HashSet<>(selectedItems);  // Convert the list to a set

        // Verify that the selected items are correct
        assertEquals(selectedItems.size(), uniqueItems.size());
    }
}