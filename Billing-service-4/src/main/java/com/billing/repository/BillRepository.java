package com.billing.repository;

import com.billing.entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {
    
    List<Bill> findByCustomerId(Long customerId);
    
    List<Bill> findByVendorId(Long vendorId);
    
    List<Bill> findByStatus(String status);
    
    Optional<Bill> findByBillNumber(String billNumber);
    
    @Query("SELECT b FROM Bill b WHERE b.customerId = :customerId AND b.status = :status")
    List<Bill> findByCustomerIdAndStatus(@Param("customerId") Long customerId, @Param("status") String status);
    
    @Query("SELECT b FROM Bill b WHERE b.billDate BETWEEN :startDate AND :endDate")
    List<Bill> findBillsByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
} 