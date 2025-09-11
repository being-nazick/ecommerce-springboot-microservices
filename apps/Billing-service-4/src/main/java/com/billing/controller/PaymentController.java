package com.billing.controller;

import com.billing.dto.PaymentDTO;
import com.billing.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
@Slf4j
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/process")
    public ResponseEntity<PaymentDTO> processPayment(@Valid @RequestBody PaymentDTO paymentDTO) {
        log.info("Processing payment for bill: {}", paymentDTO.getBillId());
        PaymentDTO processedPayment = paymentService.processPayment(paymentDTO);
        return ResponseEntity.ok(processedPayment);
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentDTO> getPaymentById(@PathVariable Long paymentId) {
        log.info("Fetching payment with ID: {}", paymentId);
        PaymentDTO payment = paymentService.getPaymentById(paymentId);
        return ResponseEntity.ok(payment);
    }

    @GetMapping("/bill/{billId}")
    public ResponseEntity<List<PaymentDTO>> getPaymentsByBillId(@PathVariable Long billId) {
        log.info("Fetching payments for bill: {}", billId);
        List<PaymentDTO> payments = paymentService.getPaymentsByBillId(billId);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<PaymentDTO>> getPaymentsByCustomerId(@PathVariable Long customerId) {
        log.info("Fetching payments for customer: {}", customerId);
        List<PaymentDTO> payments = paymentService.getPaymentsByCustomerId(customerId);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<PaymentDTO>> getPaymentsByStatus(@PathVariable String status) {
        log.info("Fetching payments with status: {}", status);
        List<PaymentDTO> payments = paymentService.getPaymentsByStatus(status);
        return ResponseEntity.ok(payments);
    }

    @PostMapping("/{paymentId}/refund")
    public ResponseEntity<PaymentDTO> refundPayment(@PathVariable Long paymentId, @RequestParam String reason) {
        log.info("Processing refund for payment: {}", paymentId);
        
        if (reason == null || reason.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        PaymentDTO refundedPayment = paymentService.refundPayment(paymentId, reason);
        return ResponseEntity.ok(refundedPayment);
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Payment Service is running!");
    }
} 