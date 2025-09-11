package com.billing.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BillItemDTO {
    private Long billItemId;
    
    @NotNull(message = "Product ID is required")
    @Min(value = 1, message = "Product ID must be greater than 0")
    private Long productId;
    
    @NotBlank(message = "Product name is required")
    private String productName;
    
    @NotBlank(message = "Product material is required")
    private String productMaterial;
    
    @NotNull(message = "Product weight is required")
    @Min(value = 0, message = "Product weight must be non-negative")
    private Double productWeight;
    
    @NotNull(message = "Gm per weight is required")
    @Min(value = 0, message = "Gm per weight must be non-negative")
    private Double productGmPerWeight;
    
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
    
    @NotNull(message = "Unit price is required")
    @Min(value = 0, message = "Unit price must be non-negative")
    private BigDecimal unitPrice;
    
    @NotNull(message = "Total price is required")
    @Min(value = 0, message = "Total price must be non-negative")
    private BigDecimal totalPrice;
    
    private String description;
} 