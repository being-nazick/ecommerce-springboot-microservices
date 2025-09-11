package com.billing.service;

import com.billing.dto.BillDTO;
import com.billing.dto.BillItemDTO;
import com.billing.dto.CustomerDTO;
import com.billing.dto.ProductDTO;
import com.billing.entity.Bill;
import com.billing.entity.BillItem;
import com.billing.exception.BillNotFoundException;
import com.billing.exception.BillStatusException;
import com.billing.exception.CustomerNotFoundException;
import com.billing.exception.InvalidBillDataException;
import com.billing.feignclients.CustomerClient;
import com.billing.feignclients.ProductClient;
import com.billing.repository.BillItemRepository;
import com.billing.repository.BillRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
// import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BillServiceTest {

    @Mock
    private BillRepository billRepository;

    @Mock
    private BillItemRepository billItemRepository;

    @Mock
    private CustomerClient customerClient;

    @Mock
    private ProductClient productClient;

    @InjectMocks
    private BillService billService;

    private BillDTO testBillDTO;
    private CustomerDTO testCustomerDTO;
    private ProductDTO testProductDTO;
    private Bill testBill;
    private BillItem testBillItem;

    @BeforeEach
    void setUp() {
        // Setup test data
        testCustomerDTO = new CustomerDTO();
        testCustomerDTO.setCustomerId(1L);
        testCustomerDTO.setFirstName("John");
        testCustomerDTO.setLastName("Doe");

        testProductDTO = new ProductDTO();
        testProductDTO.setProductId(1L);
        testProductDTO.setProductMaterial("gold");
        testProductDTO.setProductWeight(10.0);
        testProductDTO.setProductGmPerWeight(1.0);

        BillItemDTO testItemDTO = new BillItemDTO();
        testItemDTO.setProductId(1L);
        testItemDTO.setQuantity(2);
        testItemDTO.setUnitPrice(BigDecimal.valueOf(5000.0));
        testItemDTO.setTotalPrice(BigDecimal.valueOf(10000.0));

        testBillDTO = new BillDTO();
        testBillDTO.setCustomerId(1L);
        testBillDTO.setVendorId(1L);
        testBillDTO.setBillItems(Arrays.asList(testItemDTO));

        testBill = new Bill();
        testBill.setBillId(1L);
        testBill.setBillNumber("BILL-20240829-0001");
        testBill.setCustomerId(1L);
        testBill.setVendorId(1L);
        testBill.setStatus("PENDING");
        testBill.setSubtotal(BigDecimal.valueOf(10000.0));
        testBill.setTaxAmount(BigDecimal.valueOf(300.0));
        testBill.setTotalAmount(BigDecimal.valueOf(10300.0));

        testBillItem = new BillItem();
        testBillItem.setBillItemId(1L);
        testBillItem.setProductId(1L);
        testBillItem.setQuantity(2);
        testBillItem.setUnitPrice(BigDecimal.valueOf(5000.0));
        testBillItem.setTotalPrice(BigDecimal.valueOf(10000.0));
    }

    @Test
    void testCreateBill_Success() {
        // Arrange
        when(customerClient.getCustomerById(1L)).thenReturn(testCustomerDTO);
        when(billRepository.save(any(Bill.class))).thenReturn(testBill);
        when(productClient.getProductById(1L)).thenReturn(testProductDTO);
        when(billItemRepository.saveAll(anyList())).thenReturn(Arrays.asList(testBillItem));

        // Act
        BillDTO result = billService.createBill(testBillDTO);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getBillId());
        assertEquals("PENDING", result.getStatus());
        verify(customerClient).getCustomerById(1L);
        verify(billRepository).save(any(Bill.class));
        verify(billItemRepository).saveAll(anyList());
    }

    @Test
    void testCreateBill_CustomerNotFound() {
        // Arrange
        when(customerClient.getCustomerById(1L)).thenReturn(null);

        // Act & Assert
        assertThrows(CustomerNotFoundException.class, () -> {
            billService.createBill(testBillDTO);
        });
        verify(customerClient).getCustomerById(1L);
        verify(billRepository, never()).save(any(Bill.class));
    }

    @Test
    void testCreateBill_InvalidData() {
        // Arrange
        testBillDTO.setCustomerId(null);

        // Act & Assert
        assertThrows(InvalidBillDataException.class, () -> {
            billService.createBill(testBillDTO);
        });
        verify(customerClient, never()).getCustomerById(any());
    }

    @Test
    void testGetBillById_Success() {
        // Arrange
        when(billRepository.findById(1L)).thenReturn(Optional.of(testBill));

        // Act
        BillDTO result = billService.getBillById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getBillId());
        verify(billRepository).findById(1L);
    }

    @Test
    void testGetBillById_NotFound() {
        // Arrange
        when(billRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BillNotFoundException.class, () -> {
            billService.getBillById(1L);
        });
        verify(billRepository).findById(1L);
    }

    @Test
    void testGetBillById_InvalidId() {
        // Act & Assert
        assertThrows(InvalidBillDataException.class, () -> {
            billService.getBillById(null);
        });
        assertThrows(InvalidBillDataException.class, () -> {
            billService.getBillById(0L);
        });
        verify(billRepository, never()).findById(any());
    }

    @Test
    void testGetBillByNumber_Success() {
        // Arrange
        when(billRepository.findByBillNumber("BILL-20240829-0001")).thenReturn(Optional.of(testBill));

        // Act
        BillDTO result = billService.getBillByNumber("BILL-20240829-0001");

        // Assert
        assertNotNull(result);
        assertEquals("BILL-20240829-0001", result.getBillNumber());
        verify(billRepository).findByBillNumber("BILL-20240829-0001");
    }

    @Test
    void testGetBillByNumber_NotFound() {
        // Arrange
        when(billRepository.findByBillNumber("BILL-20240829-0001")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BillNotFoundException.class, () -> {
            billService.getBillByNumber("BILL-20240829-0001");
        });
        verify(billRepository).findByBillNumber("BILL-20240829-0001");
    }

    @Test
    void testGetBillsByCustomerId_Success() {
        // Arrange
        when(billRepository.findByCustomerId(1L)).thenReturn(Arrays.asList(testBill));

        // Act
        List<BillDTO> result = billService.getBillsByCustomerId(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getCustomerId());
        verify(billRepository).findByCustomerId(1L);
    }

    @Test
    void testGetBillsByStatus_Success() {
        // Arrange
        when(billRepository.findByStatus("PENDING")).thenReturn(Arrays.asList(testBill));

        // Act
        List<BillDTO> result = billService.getBillsByStatus("PENDING");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("PENDING", result.get(0).getStatus());
        verify(billRepository).findByStatus("PENDING");
    }

    @Test
    void testUpdateBillStatus_Success() {
        // Arrange
        when(billRepository.findById(1L)).thenReturn(Optional.of(testBill));
        when(billRepository.save(any(Bill.class))).thenReturn(testBill);

        // Act
        BillDTO result = billService.updateBillStatus(1L, "PAID");

        // Assert
        assertNotNull(result);
        verify(billRepository).findById(1L);
        verify(billRepository).save(any(Bill.class));
    }

    @Test
    void testDeleteBill_Success() {
        // Arrange
        testBill.setStatus("PENDING");
        when(billRepository.existsById(1L)).thenReturn(true);
        when(billRepository.findById(1L)).thenReturn(Optional.of(testBill));

        // Act
        billService.deleteBill(1L);

        // Assert
        verify(billRepository).existsById(1L);
        verify(billRepository).findById(1L);
        verify(billRepository).deleteById(1L);
    }

    @Test
    void testDeleteBill_PaidBill() {
        // Arrange
        testBill.setStatus("PAID");
        when(billRepository.existsById(1L)).thenReturn(true);
        when(billRepository.findById(1L)).thenReturn(Optional.of(testBill));

        // Act & Assert
        assertThrows(BillStatusException.class, () -> {
            billService.deleteBill(1L);
        });
        verify(billRepository).existsById(1L);
        verify(billRepository).findById(1L);
        verify(billRepository, never()).deleteById(any());
    }

    @Test
    void testCalculateBillTotals_GoldMaterial() {
        // Arrange
        BillItemDTO goldItem = new BillItemDTO();
        goldItem.setProductId(1L);
        goldItem.setQuantity(2);
        goldItem.setUnitPrice(BigDecimal.valueOf(5000.0));
        goldItem.setTotalPrice(BigDecimal.valueOf(10000.0));
        goldItem.setProductMaterial("gold"); // Set material for tax calculation
        testBillDTO.setBillItems(Arrays.asList(goldItem));

        when(customerClient.getCustomerById(1L)).thenReturn(testCustomerDTO);
        when(productClient.getProductById(1L)).thenReturn(testProductDTO);
        when(billRepository.save(any(Bill.class))).thenReturn(testBill);
        when(billItemRepository.saveAll(anyList())).thenReturn(Arrays.asList(testBillItem));

        // Act
        BillDTO result = billService.createBill(testBillDTO);

        // Assert
        assertNotNull(result);
        // Gold should have 3% GST
        assertEquals(0, result.getSubtotal().compareTo(BigDecimal.valueOf(10000.0)));
        assertEquals(0, result.getTaxAmount().compareTo(BigDecimal.valueOf(300.0)));
    }

    @Test
    void testCalculateBillTotals_VolumeDiscount() {
        // Arrange
        BillItemDTO item1 = new BillItemDTO();
        item1.setProductId(1L);
        item1.setQuantity(2);
        item1.setUnitPrice(BigDecimal.valueOf(5000.0));
        item1.setTotalPrice(BigDecimal.valueOf(10000.0)); // 2 * 5000 = 10000
        item1.setProductMaterial("silver"); // Set material for tax calculation
        
        BillItemDTO item2 = new BillItemDTO();
        item2.setProductId(2L);
        item2.setQuantity(1);
        item2.setUnitPrice(BigDecimal.valueOf(5000.0));
        item2.setTotalPrice(BigDecimal.valueOf(5000.0)); // 1 * 5000 = 5000
        item2.setProductMaterial("silver"); // Set material for tax calculation
        
        testBillDTO.setBillItems(Arrays.asList(item1, item2));

        when(customerClient.getCustomerById(1L)).thenReturn(testCustomerDTO);
        when(productClient.getProductById(1L)).thenReturn(testProductDTO);
        when(productClient.getProductById(2L)).thenReturn(testProductDTO);
        when(billRepository.save(any(Bill.class))).thenReturn(testBill);
        when(billItemRepository.saveAll(anyList())).thenReturn(Arrays.asList(testBillItem));

        // Act
        BillDTO result = billService.createBill(testBillDTO);

        // Assert
        assertNotNull(result);
        // 3 total quantity should have 5% volume discount
        assertEquals(0, result.getSubtotal().compareTo(BigDecimal.valueOf(15000.0)));
        // Should have discount (5% of 15000 = 750)
        assertEquals(0, result.getDiscountAmount().compareTo(BigDecimal.valueOf(750.0)));
    }
} 