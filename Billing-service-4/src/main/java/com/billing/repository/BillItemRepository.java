package com.billing.repository;

import com.billing.entity.BillItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BillItemRepository extends JpaRepository<BillItem, Long> {
    
    List<BillItem> findByBill_BillId(Long billId);
    
    List<BillItem> findByProductId(Long productId);
} 