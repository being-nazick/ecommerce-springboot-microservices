package com.billing.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "bill_items")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BillItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long billItemId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bill_id")
    private Bill bill;
    
    private Long productId;
    private String productName;
    private String productMaterial;
    private double productWeight;
    private double productGmPerWeight;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private String description;
} 