package com.meli.coupons.service;

import com.meli.coupons.controller.exception.BusinessException;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class CouponService implements ICouponService{

    public List<String> calculate(Map<String, Float> items, Float amount){
        int n = items.size();
        String[] itemIds = items.keySet().toArray(new String[0]);
        Float[] prices = items.values().toArray(new Float[0]);

        int maxAmount = Math.round(amount); // Convert to integer.

        // Initialize and fill the dp table
        float[][] dp = initializeDpTable(n, maxAmount);
        fillDpTable(dp, n, maxAmount, prices);

        // Reconstruct the solution
        return reconstructSolution(dp, n, maxAmount, itemIds, prices);
    }



    // Initializes the 2D dp table
    private float[][] initializeDpTable(int n, int maxAmount) {
        return new float[n + 1][maxAmount + 1];
    }


    // Fills the dp table using dynamic programming
    public void fillDpTable(float[][] dp, int n, int maxAmount, Float[] prices) {
        for (int i = 1; i <= n; i++) {
            int price = Math.round(prices[i - 1]); // Convert the current amount to an integer.
            for (int j = 0; j <= maxAmount; j++) {
                if (j < price) {
                    dp[i][j] = dp[i - 1][j]; // Cannot take the item.
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i - 1][j - price] + price); // Maximum between taking or not taking the item.
                }
            }
        }
    }


    // Reconstructs the solution from the dp table
    public List<String> reconstructSolution(float[][] dp, int n, int maxAmount, String[] itemIds, Float[] prices) {
        LinkedHashMap<String, Float> selectedItems = new LinkedHashMap<>();
        int remaining = maxAmount;

        for (int i = n; i > 0; i--) {
            int price = Math.round(prices[i - 1]);
            // Check if this item was selected and is different
            if (dp[i][remaining] != dp[i - 1][remaining] && !selectedItems.containsKey(itemIds[i - 1])) {
                selectedItems.put(itemIds[i - 1], prices[i - 1]); // Include this item.
                remaining -= price; // Subtract the amount from the remaining amount.
            }
        }

        if(selectedItems.isEmpty()){
            throw new BusinessException("Not enough money to buy any item");
        }

        return List.copyOf(selectedItems.keySet()); // Convert to an immutable list.
    }


}
