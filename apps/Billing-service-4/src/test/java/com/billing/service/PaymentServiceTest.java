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
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private BillRepository billRepository;

    @InjectMocks
    private PaymentService paymentService;

    private PaymentDTO testPaymentDTO;
    private Payment testPayment;
    private Bill testBill;

    @BeforeEach
    void setUp() {
        // Setup test data
        testPaymentDTO = new PaymentDTO();
        testPaymentDTO.setBillId(1L);
        testPaymentDTO.setCustomerId(1L);
        testPaymentDTO.setAmount(BigDecimal.valueOf(10300.0));
        testPaymentDTO.setPaymentMethod("CARD");
        testPaymentDTO.setNotes("Test payment");

        testPayment = new Payment();
        testPayment.setPaymentId(1L);
        testPayment.setBillId(1L);
        testPayment.setCustomerId(1L);
        testPayment.setAmount(BigDecimal.valueOf(10300.0));
        testPayment.setPaymentMethod("CARD");
        testPayment.setStatus("COMPLETED");
        testPayment.setTransactionId("TXN-20240829-0001");

        testBill = new Bill();
        testBill.setBillId(1L);
        testBill.setCustomerId(1L);
        testBill.setStatus("PENDING");
        testBill.setTotalAmount(BigDecimal.valueOf(10300.0));
    }

    @Test
    void testProcessPayment_Success() {
        // Arrange
        when(billRepository.findById(1L)).thenReturn(Optional.of(testBill));
        when(paymentRepository.save(any(Payment.class))).thenReturn(testPayment);
        when(billRepository.save(any(Bill.class))).thenReturn(testBill);

        // Act
        PaymentDTO result = paymentService.processPayment(testPaymentDTO);

        // Assert
        assertNotNull(result);
        assertEquals("COMPLETED", result.getStatus());
        assertNotNull(result.getTransactionId());
        verify(billRepository).findById(1L);
        verify(paymentRepository).save(any(Payment.class));
        verify(billRepository).save(any(Bill.class));
    }

    @Test
    void testProcessPayment_BillNotFound() {
        // Arrange
        when(billRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BillNotFoundException.class, () -> {
            paymentService.processPayment(testPaymentDTO);
        });
        verify(billRepository).findById(1L);
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    void testProcessPayment_BillAlreadyPaid() {
        // Arrange
        testBill.setStatus("PAID");
        when(billRepository.findById(1L)).thenReturn(Optional.of(testBill));

        // Act & Assert
        assertThrows(PaymentProcessingException.class, () -> {
            paymentService.processPayment(testPaymentDTO);
        });
        verify(billRepository).findById(1L);
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    void testProcessPayment_AmountMismatch() {
        // Arrange
        testPaymentDTO.setAmount(BigDecimal.valueOf(10000.0)); // Different amount
        when(billRepository.findById(1L)).thenReturn(Optional.of(testBill));

        // Act & Assert
        assertThrows(PaymentProcessingException.class, () -> {
            paymentService.processPayment(testPaymentDTO);
        });
        verify(billRepository).findById(1L);
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    void testProcessPayment_InvalidData() {
        // Arrange
        testPaymentDTO.setBillId(null);

        // Act & Assert
        assertThrows(InvalidPaymentDataException.class, () -> {
            paymentService.processPayment(testPaymentDTO);
        });
        verify(billRepository, never()).findById(any());
    }

    @Test
    void testGetPaymentById_Success() {
        // Arrange
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(testPayment));

        // Act
        PaymentDTO result = paymentService.getPaymentById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getPaymentId());
        verify(paymentRepository).findById(1L);
    }

    @Test
    void testGetPaymentById_NotFound() {
        // Arrange
        when(paymentRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(PaymentNotFoundException.class, () -> {
            paymentService.getPaymentById(1L);
        });
        verify(paymentRepository).findById(1L);
    }

    @Test
    void testGetPaymentById_InvalidId() {
        // Act & Assert
        assertThrows(InvalidPaymentDataException.class, () -> {
            paymentService.getPaymentById(null);
        });
        assertThrows(InvalidPaymentDataException.class, () -> {
            paymentService.getPaymentById(0L);
        });
        verify(paymentRepository, never()).findById(any());
    }

    @Test
    void testGetPaymentsByBillId_Success() {
        // Arrange
        when(paymentRepository.findByBillId(1L)).thenReturn(Arrays.asList(testPayment));

        // Act
        List<PaymentDTO> result = paymentService.getPaymentsByBillId(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getBillId());
        verify(paymentRepository).findByBillId(1L);
    }

    @Test
    void testGetPaymentsByCustomerId_Success() {
        // Arrange
        when(paymentRepository.findByCustomerId(1L)).thenReturn(Arrays.asList(testPayment));

        // Act
        List<PaymentDTO> result = paymentService.getPaymentsByCustomerId(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getCustomerId());
        verify(paymentRepository).findByCustomerId(1L);
    }

    @Test
    void testGetPaymentsByStatus_Success() {
        // Arrange
        when(paymentRepository.findByStatus("COMPLETED")).thenReturn(Arrays.asList(testPayment));

        // Act
        List<PaymentDTO> result = paymentService.getPaymentsByStatus("COMPLETED");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("COMPLETED", result.get(0).getStatus());
        verify(paymentRepository).findByStatus("COMPLETED");
    }

    @Test
    void testGetPaymentsByStatus_InvalidStatus() {
        // Act & Assert
        assertThrows(InvalidPaymentDataException.class, () -> {
            paymentService.getPaymentsByStatus(null);
        });
        assertThrows(InvalidPaymentDataException.class, () -> {
            paymentService.getPaymentsByStatus("");
        });
        verify(paymentRepository, never()).findByStatus(any());
    }

    @Test
    void testRefundPayment_Success() {
        // Arrange
        testPayment.setStatus("COMPLETED");
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(testPayment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(testPayment);
        when(billRepository.findById(1L)).thenReturn(Optional.of(testBill));
        when(billRepository.save(any(Bill.class))).thenReturn(testBill);

        // Act
        PaymentDTO result = paymentService.refundPayment(1L, "Customer request");

        // Assert
        assertNotNull(result);
        assertEquals("REFUNDED", result.getStatus());
        assertTrue(result.getNotes().contains("Refunded"));
        verify(paymentRepository).findById(1L);
        verify(paymentRepository).save(any(Payment.class));
        verify(billRepository).findById(1L);
        verify(billRepository).save(any(Bill.class));
    }

    @Test
    void testRefundPayment_PaymentNotFound() {
        // Arrange
        when(paymentRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(PaymentNotFoundException.class, () -> {
            paymentService.refundPayment(1L, "Customer request");
        });
        verify(paymentRepository).findById(1L);
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    void testRefundPayment_CannotRefund() {
        // Arrange
        testPayment.setStatus("PENDING");
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(testPayment));

        // Act & Assert
        assertThrows(RefundProcessingException.class, () -> {
            paymentService.refundPayment(1L, "Customer request");
        });
        verify(paymentRepository).findById(1L);
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    void testRefundPayment_InvalidData() {
        // Act & Assert
        assertThrows(InvalidPaymentDataException.class, () -> {
            paymentService.refundPayment(null, "Customer request");
        });
        assertThrows(InvalidPaymentDataException.class, () -> {
            paymentService.refundPayment(1L, null);
        });
        assertThrows(InvalidPaymentDataException.class, () -> {
            paymentService.refundPayment(1L, "");
        });
        verify(paymentRepository, never()).findById(any());
    }

    @Test
    void testValidatePaymentData_Success() {
        // Arrange
        when(billRepository.findById(1L)).thenReturn(Optional.of(testBill));
        when(paymentRepository.save(any(Payment.class))).thenReturn(testPayment);
        when(billRepository.save(any(Bill.class))).thenReturn(testBill);

        // Act
        PaymentDTO result = paymentService.processPayment(testPaymentDTO);

        // Assert - Should not throw exception for valid data
        assertNotNull(result);
        verify(billRepository).findById(1L);
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    void testValidatePaymentData_NullPayment() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            paymentService.processPayment(null);
        });
    }

    @Test
    void testValidatePaymentData_InvalidAmount() {
        // Arrange
        testPaymentDTO.setAmount(BigDecimal.valueOf(-100));

        // Act & Assert
        assertThrows(InvalidPaymentDataException.class, () -> {
            paymentService.processPayment(testPaymentDTO);
        });
    }

    @Test
    void testValidatePaymentData_InvalidPaymentMethod() {
        // Arrange
        testPaymentDTO.setPaymentMethod("");

        // Act & Assert
        assertThrows(InvalidPaymentDataException.class, () -> {
            paymentService.processPayment(testPaymentDTO);
        });
    }
} 