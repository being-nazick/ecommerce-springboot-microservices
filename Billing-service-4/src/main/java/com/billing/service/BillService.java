package com.billing.service;

import com.billing.dto.BillDTO;
import com.billing.dto.BillItemDTO;
import com.billing.dto.CustomerDTO;
import com.billing.dto.ProductDTO;
import com.billing.entity.Bill;
import com.billing.entity.BillItem;
import com.billing.entity.Payment;
import com.billing.exception.BillNotFoundException;
import com.billing.exception.CustomerNotFoundException;
import com.billing.exception.InvalidBillDataException;
import com.billing.exception.BillStatusException;
import com.billing.feignclients.CustomerClient;
import com.billing.feignclients.ProductClient;
import com.billing.repository.BillItemRepository;
import com.billing.repository.BillRepository;
import com.billing.repository.PaymentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BillService {

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private BillItemRepository billItemRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private CustomerClient customerClient;

    @Autowired
    private ProductClient productClient;

    @Transactional
    public BillDTO createBill(BillDTO billDTO) {
        log.info("Creating new bill for customer: {}", billDTO.getCustomerId());
        
        // Validate input data
        validateBillData(billDTO);
        
        // Validate customer exists
        CustomerDTO customer = customerClient.getCustomerById(billDTO.getCustomerId());
        if (customer == null) {
            throw new CustomerNotFoundException("Customer not found with ID: " + billDTO.getCustomerId());
        }

        Bill bill = new Bill();
        bill.setCustomerId(billDTO.getCustomerId());
        bill.setVendorId(billDTO.getVendorId());
        bill.setBillNumber(generateBillNumber());
        bill.setBillDate(LocalDateTime.now());
        bill.setStatus("PENDING");
        bill.setNotes(billDTO.getNotes());

        // Calculate totals with jewellery-specific logic
        calculateBillTotals(bill, billDTO.getBillItems());
        
        Bill savedBill = billRepository.save(bill);
        
        // Save bill items
        if (billDTO.getBillItems() != null) {
            List<BillItem> billItems = billDTO.getBillItems().stream()
                    .map(itemDTO -> createBillItem(itemDTO, savedBill))
                    .collect(Collectors.toList());
            billItemRepository.saveAll(billItems);
            savedBill.setBillItems(billItems);
        }

        return convertToDTO(savedBill);
    }

    public BillDTO getBillById(Long billId) {
        log.info("Fetching bill with ID: {}", billId);
        if (billId == null || billId <= 0) {
            throw new InvalidBillDataException("Invalid bill ID: " + billId);
        }
        
        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new BillNotFoundException("Bill not found with ID: " + billId));
        return convertToDTO(bill);
    }

    public BillDTO getBillByNumber(String billNumber) {
        log.info("Fetching bill with number: {}", billNumber);
        if (billNumber == null || billNumber.trim().isEmpty()) {
            throw new InvalidBillDataException("Bill number cannot be null or empty");
        }
        
        Bill bill = billRepository.findByBillNumber(billNumber)
                .orElseThrow(() -> new BillNotFoundException("Bill not found with number: " + billNumber));
        return convertToDTO(bill);
    }

    public List<BillDTO> getBillsByCustomerId(Long customerId) {
        log.info("Fetching bills for customer: {}", customerId);
        if (customerId == null || customerId <= 0) {
            throw new InvalidBillDataException("Invalid customer ID: " + customerId);
        }
        
        List<Bill> bills = billRepository.findByCustomerId(customerId);
        return bills.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public List<BillDTO> getBillsByVendorId(Long vendorId) {
        log.info("Fetching bills for vendor: {}", vendorId);
        if (vendorId == null || vendorId <= 0) {
            throw new InvalidBillDataException("Invalid vendor ID: " + vendorId);
        }
        
        List<Bill> bills = billRepository.findByVendorId(vendorId);
        return bills.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public List<BillDTO> getBillsByStatus(String status) {
        log.info("Fetching bills with status: {}", status);
        if (status == null || status.trim().isEmpty()) {
            throw new InvalidBillDataException("Status cannot be null or empty");
        }
        
        validateBillStatus(status);
        
        List<Bill> bills = billRepository.findByStatus(status);
        return bills.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Transactional
    public BillDTO updateBillStatus(Long billId, String status) {
        log.info("Updating bill {} status to: {}", billId, status);
        
        if (billId == null || billId <= 0) {
            throw new InvalidBillDataException("Invalid bill ID: " + billId);
        }
        
        if (status == null || status.trim().isEmpty()) {
            throw new InvalidBillDataException("Status cannot be null or empty");
        }
        
        validateBillStatus(status);
        
        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new BillNotFoundException("Bill not found with ID: " + billId));
        
        // Check if status transition is valid
        validateStatusTransition(bill.getStatus(), status);
        
        bill.setStatus(status);
        Bill updatedBill = billRepository.save(bill);
        return convertToDTO(updatedBill);
    }

    @Transactional
    public void deleteBill(Long billId) {
        log.info("Deleting bill with ID: {}", billId);
        
        if (billId == null || billId <= 0) {
            throw new InvalidBillDataException("Invalid bill ID: " + billId);
        }
        
        if (!billRepository.existsById(billId)) {
            throw new BillNotFoundException("Bill not found with ID: " + billId);
        }
        
        // Check if bill can be deleted (e.g., not paid)
        Bill bill = billRepository.findById(billId).orElse(null);
        if (bill != null && "PAID".equals(bill.getStatus())) {
            throw new BillStatusException("Cannot delete a paid bill with ID: " + billId);
        }
        
        billRepository.deleteById(billId);
    }

    private void validateBillData(BillDTO billDTO) {
        if (billDTO == null) {
            throw new InvalidBillDataException("Bill data cannot be null");
        }
        
        if (billDTO.getCustomerId() == null || billDTO.getCustomerId() <= 0) {
            throw new InvalidBillDataException("Valid customer ID is required");
        }
        
        if (billDTO.getVendorId() == null || billDTO.getVendorId() <= 0) {
            throw new InvalidBillDataException("Valid vendor ID is required");
        }
        
        if (billDTO.getBillItems() == null || billDTO.getBillItems().isEmpty()) {
            throw new InvalidBillDataException("Bill must contain at least one item");
        }
    }

    private void validateBillStatus(String status) {
        List<String> validStatuses = List.of("PENDING", "PAID", "CANCELLED");
        if (!validStatuses.contains(status.toUpperCase())) {
            throw new BillStatusException("Invalid bill status: " + status + ". Valid statuses are: " + validStatuses);
        }
    }

    private void validateStatusTransition(String currentStatus, String newStatus) {
        if ("PAID".equals(currentStatus) && !"CANCELLED".equals(newStatus)) {
            throw new BillStatusException("Cannot change status from PAID to " + newStatus);
        }
        
        if ("CANCELLED".equals(currentStatus)) {
            throw new BillStatusException("Cannot change status of a cancelled bill");
        }
    }

    private String generateBillNumber() {
        // Generate simple bill number: BILL-YYYYMMDD-XXXX
        String datePrefix = LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomSuffix = String.format("%04d", (int)(Math.random() * 10000));
        return "BILL-" + datePrefix + "-" + randomSuffix;
    }

    private void calculateBillTotals(Bill bill, List<BillItemDTO> items) {
        if (items == null || items.isEmpty()) {
            bill.setSubtotal(BigDecimal.ZERO);
            bill.setTaxAmount(BigDecimal.ZERO);
            bill.setDiscountAmount(BigDecimal.ZERO);
            bill.setTotalAmount(BigDecimal.ZERO);
            return;
        }

        BigDecimal subtotal = items.stream()
                .map(item -> item.getTotalPrice() != null ? item.getTotalPrice() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        bill.setSubtotal(subtotal.setScale(2, RoundingMode.HALF_UP));
        
        // Calculate tax (GST for jewellery - 3% for gold, 5% for other items)
        BigDecimal taxRate = calculateJewelleryTaxRate(items);
        bill.setTaxAmount(subtotal.multiply(taxRate).setScale(2, RoundingMode.HALF_UP));
        
        // Apply discount if any (jewellery business logic)
        bill.setDiscountAmount(calculateJewelleryDiscount(subtotal, items));
        
        // Calculate total
        bill.setTotalAmount(subtotal.add(bill.getTaxAmount()).subtract(bill.getDiscountAmount()).setScale(2, RoundingMode.HALF_UP));
    }

    private BigDecimal calculateJewelleryTaxRate(List<BillItemDTO> items) {
        // Check if any item is gold (GST 3%) or other materials (GST 5%)
        boolean hasGold = items.stream()
                .anyMatch(item -> item.getProductMaterial() != null && 
                        item.getProductMaterial().toLowerCase().contains("gold"));
        
        return hasGold ? new BigDecimal("0.03") : new BigDecimal("0.05");
    }

    private BigDecimal calculateJewelleryDiscount(BigDecimal subtotal, List<BillItemDTO> items) {
        BigDecimal discount = BigDecimal.ZERO;
        
        // Volume discount for jewellery
        int totalQuantity = items.stream().mapToInt(BillItemDTO::getQuantity).sum();
        
        if (totalQuantity >= 5) {
            discount = subtotal.multiply(new BigDecimal("0.10")); // 10% discount for 5+ items
        } else if (totalQuantity >= 3) {
            discount = subtotal.multiply(new BigDecimal("0.05")); // 5% discount for 3+ items
        }
        
        // Special discount for precious metals
        boolean hasPreciousMetal = items.stream()
                .anyMatch(item -> {
                    String material = item.getProductMaterial() != null ? item.getProductMaterial().toLowerCase() : "";
                    return material.contains("platinum") || material.contains("diamond");
                });
        
        if (hasPreciousMetal) {
            discount = discount.add(subtotal.multiply(new BigDecimal("0.02"))); // Additional 2% for precious metals
        }
        
        return discount.setScale(2, RoundingMode.HALF_UP);
    }

    private BillItem createBillItem(BillItemDTO itemDTO, Bill bill) {
        if (itemDTO.getProductId() == null || itemDTO.getProductId() <= 0) {
            throw new InvalidBillDataException("Valid product ID is required for bill item");
        }
        
        if (itemDTO.getQuantity() <= 0) {
            throw new InvalidBillDataException("Quantity must be greater than 0");
        }
        
        // Get product details from Product service
        ProductDTO product = productClient.getProductById(itemDTO.getProductId());
        if (product == null) {
            throw new InvalidBillDataException("Product not found with ID: " + itemDTO.getProductId());
        }
        
        // Calculate unit price if not provided
        BigDecimal unitPrice = itemDTO.getUnitPrice();
        if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) <= 0) {
            unitPrice = BigDecimal.valueOf(product.getUnitPrice());
        }
        
        if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidBillDataException("Valid unit price is required for product: " + product.getProductId());
        }
        
        // Calculate total price
        BigDecimal totalPrice = unitPrice.multiply(BigDecimal.valueOf(itemDTO.getQuantity())).setScale(2, RoundingMode.HALF_UP);
        
        BillItem item = new BillItem();
        item.setBill(bill);
        item.setProductId(itemDTO.getProductId());
        item.setProductName(product.getProductName());
        item.setProductMaterial(product.getProductMaterial());
        item.setProductWeight(product.getProductWeight());
        item.setProductGmPerWeight(product.getProductGmPerWeight());
        item.setQuantity(itemDTO.getQuantity());
        item.setUnitPrice(unitPrice.setScale(2, RoundingMode.HALF_UP));
        item.setTotalPrice(totalPrice);
        item.setDescription(itemDTO.getDescription() != null ? itemDTO.getDescription() : 
                          product.getProductMaterial() + " jewellery item");
        return item;
    }

    private BillDTO convertToDTO(Bill bill) {
        BillDTO dto = new BillDTO();
        dto.setBillId(bill.getBillId());
        dto.setCustomerId(bill.getCustomerId());
        dto.setVendorId(bill.getVendorId());
        dto.setBillNumber(bill.getBillNumber());
        dto.setBillDate(bill.getBillDate());
        dto.setSubtotal(bill.getSubtotal());
        dto.setTaxAmount(bill.getTaxAmount());
        dto.setDiscountAmount(bill.getDiscountAmount());
        dto.setTotalAmount(bill.getTotalAmount());
        dto.setStatus(bill.getStatus());
        dto.setPaymentMethod(bill.getPaymentMethod());
        dto.setNotes(bill.getNotes());
        dto.setCreatedAt(bill.getCreatedAt());
        dto.setUpdatedAt(bill.getUpdatedAt());
        
        if (bill.getBillItems() != null) {
            List<BillItemDTO> itemDTOs = bill.getBillItems().stream()
                    .map(this::convertToItemDTO)
                    .collect(Collectors.toList());
            dto.setBillItems(itemDTOs);
        }
        
        return dto;
    }

    private BillItemDTO convertToItemDTO(BillItem item) {
        BillItemDTO dto = new BillItemDTO();
        dto.setBillItemId(item.getBillItemId());
        dto.setProductId(item.getProductId());
        dto.setProductName(item.getProductName());
        dto.setProductMaterial(item.getProductMaterial());
        dto.setProductWeight(item.getProductWeight());
        dto.setProductGmPerWeight(item.getProductGmPerWeight());
        dto.setQuantity(item.getQuantity());
        dto.setUnitPrice(item.getUnitPrice());
        dto.setTotalPrice(item.getTotalPrice());
        dto.setDescription(item.getDescription());
        return dto;
    }
} 