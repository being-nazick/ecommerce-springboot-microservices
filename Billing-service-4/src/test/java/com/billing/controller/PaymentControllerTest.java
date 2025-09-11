package com.billing.controller;

import com.billing.dto.PaymentDTO;
import com.billing.service.PaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private PaymentController paymentController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private PaymentDTO testPaymentDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(paymentController).build();
        objectMapper = new ObjectMapper();
        // Configure ObjectMapper to handle LocalDateTime
        objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

        // Setup test data
        testPaymentDTO = new PaymentDTO();
        testPaymentDTO.setPaymentId(1L);
        testPaymentDTO.setBillId(1L);
        testPaymentDTO.setCustomerId(1L);
        testPaymentDTO.setAmount(BigDecimal.valueOf(10300.0));
        testPaymentDTO.setPaymentMethod("CARD");
        testPaymentDTO.setStatus("COMPLETED");
        testPaymentDTO.setTransactionId("TXN-20240829-0001");
        testPaymentDTO.setPaymentDate(LocalDateTime.now());
    }

    @Test
    void testProcessPayment_Success() throws Exception {
        // Arrange
        when(paymentService.processPayment(any(PaymentDTO.class))).thenReturn(testPaymentDTO);

        // Act & Assert
        mockMvc.perform(post("/api/payments/process")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testPaymentDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentId").value(1))
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.transactionId").value("TXN-20240829-0001"));

        verify(paymentService).processPayment(any(PaymentDTO.class));
    }

    @Test
    void testGetPaymentById_Success() throws Exception {
        // Arrange
        when(paymentService.getPaymentById(1L)).thenReturn(testPaymentDTO);

        // Act & Assert
        mockMvc.perform(get("/api/payments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentId").value(1))
                .andExpect(jsonPath("$.billId").value(1));

        verify(paymentService).getPaymentById(1L);
    }

    @Test
    void testGetPaymentsByBillId_Success() throws Exception {
        // Arrange
        List<PaymentDTO> payments = Arrays.asList(testPaymentDTO);
        when(paymentService.getPaymentsByBillId(1L)).thenReturn(payments);

        // Act & Assert
        mockMvc.perform(get("/api/payments/bill/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].billId").value(1))
                .andExpect(jsonPath("$[0].paymentId").value(1));

        verify(paymentService).getPaymentsByBillId(1L);
    }

    @Test
    void testGetPaymentsByCustomerId_Success() throws Exception {
        // Arrange
        List<PaymentDTO> payments = Arrays.asList(testPaymentDTO);
        when(paymentService.getPaymentsByCustomerId(1L)).thenReturn(payments);

        // Act & Assert
        mockMvc.perform(get("/api/payments/customer/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].customerId").value(1));

        verify(paymentService).getPaymentsByCustomerId(1L);
    }

    @Test
    void testGetPaymentsByStatus_Success() throws Exception {
        // Arrange
        List<PaymentDTO> payments = Arrays.asList(testPaymentDTO);
        when(paymentService.getPaymentsByStatus("COMPLETED")).thenReturn(payments);

        // Act & Assert
        mockMvc.perform(get("/api/payments/status/COMPLETED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("COMPLETED"));

        verify(paymentService).getPaymentsByStatus("COMPLETED");
    }

    @Test
    void testRefundPayment_Success() throws Exception {
        // Arrange
        testPaymentDTO.setStatus("REFUNDED");
        when(paymentService.refundPayment(1L, "Customer request")).thenReturn(testPaymentDTO);

        // Act & Assert
        mockMvc.perform(post("/api/payments/1/refund")
                .param("reason", "Customer request"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REFUNDED"));

        verify(paymentService).refundPayment(1L, "Customer request");
    }

    @Test
    void testHealthCheck() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/payments/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("Payment Service is running!"));
    }

    @Test
    void testProcessPayment_ValidationError() throws Exception {
        // Arrange - Invalid payment data
        testPaymentDTO.setBillId(null);

        // Act & Assert
        mockMvc.perform(post("/api/payments/process")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testPaymentDTO)))
                .andExpect(status().isBadRequest());

        verify(paymentService, never()).processPayment(any(PaymentDTO.class));
    }

    @Test
    void testRefundPayment_MissingReason() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/payments/1/refund"))
                .andExpect(status().isBadRequest());

        verify(paymentService, never()).refundPayment(any(), any());
    }

    @Test
    void testRefundPayment_EmptyReason() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/payments/1/refund")
                .param("reason", ""))
                .andExpect(status().isBadRequest());

        verify(paymentService, never()).refundPayment(any(), any());
    }
} 