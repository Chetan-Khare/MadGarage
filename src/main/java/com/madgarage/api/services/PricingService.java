package com.madgarage.api.services;

import com.madgarage.api.model.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PricingService {

    private final SystemSettingService systemSettingService;

    /**
     * Calculates the garage-discounted price using a tiered system.
     * Reads thresholds and percentages from system_settings via cache.
     * Returns the original price if the product is non-wholesale.
     */
    public double calculateGaragePrice(Product product) {
        if (!product.isWholesale()) {
            return product.getPrice();
        }

        double price = product.getPrice();
        double discountPct;

        // If product has a custom individual discount, use it primarily
        if (product.getDiscountPercentage() != null && product.getDiscountPercentage() > 0) {
            discountPct = product.getDiscountPercentage();
        } else {
            // Fallback to tiered system
            double lowPct = systemSettingService.getSettingDouble("GARAGE_DISCOUNT_LOW_PERCENT", 5.0);
            double midThreshold = systemSettingService.getSettingDouble("GARAGE_DISCOUNT_MID_THRESHOLD", 10000.0);
            double midPct = systemSettingService.getSettingDouble("GARAGE_DISCOUNT_MID_PERCENT", 3.0);
            double highThreshold = systemSettingService.getSettingDouble("GARAGE_DISCOUNT_HIGH_THRESHOLD", 50000.0);
            double highPct = systemSettingService.getSettingDouble("GARAGE_DISCOUNT_HIGH_PERCENT", 1.0);

            if (price >= highThreshold) {
                discountPct = highPct;
            } else if (price >= midThreshold) {
                discountPct = midPct;
            } else {
                discountPct = lowPct;
            }
        }

        double discounted = price * (1.0 - discountPct / 100.0);
        return Math.round(discounted * 100.0) / 100.0;
    }
}
