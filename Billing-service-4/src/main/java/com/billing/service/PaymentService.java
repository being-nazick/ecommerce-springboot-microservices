package com.billing.service;

import com.billing.dto.PaymentDTO;
import com.billing.entity.Bill;
import com.billing.entity.Payment;
import com.billing.exception.BillNotFoundException;
import com.billing.exception.PaymentNotFoundException;
import com.billing.exception.InvalidPaymentDataException;
import com.billing.exception.PaymentProcessingException;
import com.billing.exception.RefundProcessingException;
import com.billing.repository.BillRepository;
import com.billing.repository.PaymentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private BillRepository billRepository;

    @Transactional
    public PaymentDTO processPayment(PaymentDTO paymentDTO) {
        // Validate input
        if (paymentDTO == null) {
            throw new IllegalArgumentException("Payment data cannot be null");
        }
        
        log.info("Processing payment for bill: {}", paymentDTO.getBillId());
        
        // Validate payment data
        validatePaymentData(paymentDTO);
        
        // Validate bill exists
        Bill bill = billRepository.findById(paymentDTO.getBillId())
                .orElseThrow(() -> new BillNotFoundException("Bill not found with ID: " + paymentDTO.getBillId()));
        
        // Check if bill is already paid
        if ("PAID".equals(bill.getStatus())) {
            throw new PaymentProcessingException("Bill with ID " + paymentDTO.getBillId() + " is already paid");
        }
        
        // Validate payment amount
        if (paymentDTO.getAmount().compareTo(bill.getTotalAmount()) != 0) {
            throw new PaymentProcessingException("Payment amount " + paymentDTO.getAmount() + 
                " does not match bill total " + bill.getTotalAmount());
        }
        
        Payment payment = new Payment();
        payment.setBillId(paymentDTO.getBillId());
        payment.setCustomerId(paymentDTO.getCustomerId());
        payment.setAmount(paymentDTO.getAmount());
        payment.setPaymentMethod(paymentDTO.getPaymentMethod());
        payment.setTransactionId(generateTransactionId());
        payment.setStatus("COMPLETED");
        payment.setPaymentDate(LocalDateTime.now());
        payment.setNotes(paymentDTO.getNotes());
        
        try {
            Payment savedPayment = paymentRepository.save(payment);
            
            // Update bill status to PAID
            bill.setStatus("PAID");
            bill.setPaymentMethod(paymentDTO.getPaymentMethod());
            billRepository.save(bill);
            
            return convertToDTO(savedPayment);
        } catch (Exception e) {
            log.error("Error processing payment: {}", e.getMessage());
            throw new PaymentProcessingException("Failed to process payment: " + e.getMessage(), e);
        }
    }

    public PaymentDTO getPaymentById(Long paymentId) {
        log.info("Fetching payment with ID: {}", paymentId);
        
        if (paymentId == null || paymentId <= 0) {
            throw new InvalidPaymentDataException("Invalid payment ID: " + paymentId);
        }
        
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found with ID: " + paymentId));
        return convertToDTO(payment);
    }

    public List<PaymentDTO> getPaymentsByBillId(Long billId) {
        log.info("Fetching payments for bill: {}", billId);
        
        if (billId == null || billId <= 0) {
            throw new InvalidPaymentDataException("Invalid bill ID: " + billId);
        }
        
        List<Payment> payments = paymentRepository.findByBillId(billId);
        return payments.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public List<PaymentDTO> getPaymentsByCustomerId(Long customerId) {
        log.info("Fetching payments for customer: {}", customerId);
        
        if (customerId == null || customerId <= 0) {
            throw new InvalidPaymentDataException("Invalid customer ID: " + customerId);
        }
        
        List<Payment> payments = paymentRepository.findByCustomerId(customerId);
        return payments.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public List<PaymentDTO> getPaymentsByStatus(String status) {
        log.info("Fetching payments with status: {}", status);
        
        if (status == null || status.trim().isEmpty()) {
            throw new InvalidPaymentDataException("Payment status cannot be null or empty");
        }
        
        validatePaymentStatus(status);
        
        List<Payment> payments = paymentRepository.findByStatus(status);
        return payments.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Transactional
    public PaymentDTO refundPayment(Long paymentId, String reason) {
        log.info("Processing refund for payment: {}", paymentId);
        
        if (paymentId == null || paymentId <= 0) {
            throw new InvalidPaymentDataException("Invalid payment ID: " + paymentId);
        }
        
        if (reason == null || reason.trim().isEmpty()) {
            throw new InvalidPaymentDataException("Refund reason cannot be null or empty");
        }
        
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found with ID: " + paymentId));
        
        // Check if payment can be refunded
        if (!"COMPLETED".equals(payment.getStatus())) {
            throw new RefundProcessingException("Payment with ID " + paymentId + " cannot be refunded. Current status: " + payment.getStatus());
        }
        
        try {
            payment.setStatus("REFUNDED");
            payment.setNotes("Refunded: " + reason);
            
            Payment updatedPayment = paymentRepository.save(payment);
            
            // Update bill status back to PENDING
            Bill bill = billRepository.findById(payment.getBillId())
                    .orElseThrow(() -> new BillNotFoundException("Bill not found with ID: " + payment.getBillId()));
            bill.setStatus("PENDING");
            billRepository.save(bill);
            
            return convertToDTO(updatedPayment);
        } catch (Exception e) {
            log.error("Error processing refund: {}", e.getMessage());
            throw new RefundProcessingException("Failed to process refund: " + e.getMessage(), e);
        }
    }

    private void validatePaymentData(PaymentDTO paymentDTO) {
        if (paymentDTO == null) {
            throw new InvalidPaymentDataException("Payment data cannot be null");
        }
        
        if (paymentDTO.getBillId() == null || paymentDTO.getBillId() <= 0) {
            throw new InvalidPaymentDataException("Valid bill ID is required");
        }
        
        if (paymentDTO.getCustomerId() == null || paymentDTO.getCustomerId() <= 0) {
            throw new InvalidPaymentDataException("Valid customer ID is required");
        }
        
        if (paymentDTO.getAmount() == null || paymentDTO.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidPaymentDataException("Payment amount must be greater than 0");
        }
        
        if (paymentDTO.getPaymentMethod() == null || paymentDTO.getPaymentMethod().trim().isEmpty()) {
            throw new InvalidPaymentDataException("Payment method is required");
        }
    }

    private void validatePaymentStatus(String status) {
        List<String> validStatuses = List.of("PENDING", "COMPLETED", "FAILED", "REFUNDED");
        if (!validStatuses.contains(status.toUpperCase())) {
            throw new InvalidPaymentDataException("Invalid payment status: " + status + ". Valid statuses are: " + validStatuses);
        }
    }

    private String generateTransactionId() {
        // Generate simple transaction ID: TXN-YYYYMMDD-XXXX
        String datePrefix = LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomSuffix = String.format("%04d", (int)(Math.random() * 10000));
        return "TXN-" + datePrefix + "-" + randomSuffix;
    }

    private PaymentDTO convertToDTO(Payment payment) {
        PaymentDTO dto = new PaymentDTO();
        dto.setPaymentId(payment.getPaymentId());
        dto.setBillId(payment.getBillId());
        dto.setCustomerId(payment.getCustomerId());
        dto.setAmount(payment.getAmount());
        dto.setPaymentMethod(payment.getPaymentMethod());
        dto.setTransactionId(payment.getTransactionId());
        dto.setStatus(payment.getStatus());
        dto.setPaymentDate(payment.getPaymentDate());
        dto.setNotes(payment.getNotes());
        dto.setCreatedAt(payment.getCreatedAt());
        dto.setUpdatedAt(payment.getUpdatedAt());
        return dto;
    }
} 