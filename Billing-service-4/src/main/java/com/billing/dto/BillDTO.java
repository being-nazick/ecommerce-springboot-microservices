package com.billing.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BillDTO {
    private Long billId;
    
    @NotNull(message = "Customer ID is required")
    @Min(value = 1, message = "Customer ID must be greater than 0")
    private Long customerId;
    
    @NotNull(message = "Vendor ID is required")
    @Min(value = 1, message = "Vendor ID must be greater than 0")
    private Long vendorId;
    
    private String billNumber;
    private LocalDateTime billDate;
    private BigDecimal subtotal;
    private BigDecimal taxAmount;
    private BigDecimal discountAmount;
    private BigDecimal totalAmount;
    private String status;
    private String paymentMethod;
    private String notes;
    
    @NotEmpty(message = "Bill must contain at least one item")
    @Valid
    private List<BillItemDTO> billItems;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 