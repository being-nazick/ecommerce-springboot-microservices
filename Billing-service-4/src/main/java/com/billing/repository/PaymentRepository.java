package com.billing.repository;

import com.billing.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    List<Payment> findByBillId(Long billId);
    
    List<Payment> findByCustomerId(Long customerId);
    
    List<Payment> findByStatus(String status);
    
    List<Payment> findByTransactionId(String transactionId);
} 