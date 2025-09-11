package com.billing.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDTO {
    private Long paymentId;
    
    @NotNull(message = "Bill ID is required")
    @Min(value = 1, message = "Bill ID must be greater than 0")
    private Long billId;
    
    @NotNull(message = "Customer ID is required")
    @Min(value = 1, message = "Customer ID must be greater than 0")
    private Long customerId;
    
    @NotNull(message = "Amount is required")
    @Min(value = 0, message = "Amount must be non-negative")
    private BigDecimal amount;
    
    @NotBlank(message = "Payment method is required")
    private String paymentMethod;
    
    private String transactionId;
    private String status;
    private LocalDateTime paymentDate;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 