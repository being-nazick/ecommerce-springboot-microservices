package com.billing.controller;

import com.billing.dto.BillDTO;
import com.billing.service.BillService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/bills")
@Slf4j
public class BillController {

    @Autowired
    private BillService billService;

    @PostMapping("/create")
    public ResponseEntity<BillDTO> createBill(@Valid @RequestBody BillDTO billDTO) {
        log.info("Creating new bill for customer: {}", billDTO.getCustomerId());
        BillDTO createdBill = billService.createBill(billDTO);
        return ResponseEntity.ok(createdBill);
    }

    @GetMapping("/{billId}")
    public ResponseEntity<BillDTO> getBillById(@PathVariable Long billId) {
        log.info("Fetching bill with ID: {}", billId);
        BillDTO bill = billService.getBillById(billId);
        return ResponseEntity.ok(bill);
    }

    @GetMapping("/number/{billNumber}")
    public ResponseEntity<BillDTO> getBillByNumber(@PathVariable String billNumber) {
        log.info("Fetching bill with number: {}", billNumber);
        BillDTO bill = billService.getBillByNumber(billNumber);
        return ResponseEntity.ok(bill);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<BillDTO>> getBillsByCustomerId(@PathVariable Long customerId) {
        log.info("Fetching bills for customer: {}", customerId);
        List<BillDTO> bills = billService.getBillsByCustomerId(customerId);
        return ResponseEntity.ok(bills);
    }

    @GetMapping("/vendor/{vendorId}")
    public ResponseEntity<List<BillDTO>> getBillsByVendorId(@PathVariable Long vendorId) {
        log.info("Fetching bills for vendor: {}", vendorId);
        List<BillDTO> bills = billService.getBillsByVendorId(vendorId);
        return ResponseEntity.ok(bills);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<BillDTO>> getBillsByStatus(@PathVariable String status) {
        log.info("Fetching bills with status: {}", status);
        List<BillDTO> bills = billService.getBillsByStatus(status);
        return ResponseEntity.ok(bills);
    }

    @PutMapping("/{billId}/status")
    public ResponseEntity<BillDTO> updateBillStatus(@PathVariable Long billId, @RequestParam String status) {
        log.info("Updating bill {} status to: {}", billId, status);
        
        if (status == null || status.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        BillDTO updatedBill = billService.updateBillStatus(billId, status);
        return ResponseEntity.ok(updatedBill);
    }

    @DeleteMapping("/{billId}")
    public ResponseEntity<String> deleteBill(@PathVariable Long billId) {
        log.info("Deleting bill with ID: {}", billId);
        billService.deleteBill(billId);
        return ResponseEntity.ok("Bill with ID " + billId + " has been deleted successfully");
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Billing Service is running!");
    }
} 