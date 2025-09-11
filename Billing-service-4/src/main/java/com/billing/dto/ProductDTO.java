package com.billing.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {
    private Long productId;
    private String productMaterial;
    private double productWeight;
    private double productGmPerWeight;
    private int productQuantity;
    private String productUrl;
    private Long vendorId;
    
    // Calculated fields for billing
    private String productName;
    private Double unitPrice;
    
    // Helper method to generate product name for billing
    public String getProductName() {
        if (productName != null && !productName.trim().isEmpty()) {
            return productName;
        }
        // Generate name from material and weight if not provided
        return productMaterial + " Jewelry (" + productWeight + "g)";
    }
    
    // Helper method to calculate unit price based on material and weight
    public Double getUnitPrice() {
        if (unitPrice != null && unitPrice > 0) {
            return unitPrice;
        }
        // Calculate price based on material and weight (jewellery pricing logic)
        double basePrice = calculateBasePrice();
        return basePrice;
    }
    
    private double calculateBasePrice() {
        // Jewellery pricing logic based on material and weight
        double materialMultiplier = getMaterialMultiplier();
        double weightMultiplier = productWeight * productGmPerWeight;
        return materialMultiplier * weightMultiplier;
    }
    
    private double getMaterialMultiplier() {
        // Jewellery material pricing (per gram)
        switch (productMaterial.toLowerCase()) {
            case "gold":
                return 5000.0; // ₹5000 per gram
            case "silver":
                return 80.0;   // ₹80 per gram
            case "platinum":
                return 3500.0; // ₹3500 per gram
            case "diamond":
                return 100000.0; // ₹100000 per gram
            case "ruby":
                return 15000.0;  // ₹15000 per gram
            case "emerald":
                return 12000.0;  // ₹12000 per gram
            case "pearl":
                return 2000.0;   // ₹2000 per gram
            default:
                return 100.0;    // Default for other materials
        }
    }
} 