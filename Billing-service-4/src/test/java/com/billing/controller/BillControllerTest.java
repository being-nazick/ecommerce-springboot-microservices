package com.billing.controller;

import com.billing.dto.BillDTO;
import com.billing.dto.BillItemDTO;
import com.billing.service.BillService;
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
class BillControllerTest {

    @Mock
    private BillService billService;

    @InjectMocks
    private BillController billController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private BillDTO testBillDTO;
    private BillItemDTO testItemDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(billController).build();
        objectMapper = new ObjectMapper();
        // Configure ObjectMapper to handle LocalDateTime
        objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

        // Setup test data
        testItemDTO = new BillItemDTO();
        testItemDTO.setProductId(1L);
        testItemDTO.setQuantity(2);
        testItemDTO.setUnitPrice(BigDecimal.valueOf(5000.0));
        testItemDTO.setTotalPrice(BigDecimal.valueOf(10000.0));

        testBillDTO = new BillDTO();
        testBillDTO.setBillId(1L);
        testBillDTO.setCustomerId(1L);
        testBillDTO.setVendorId(1L);
        testBillDTO.setBillNumber("BILL-20240829-0001");
        testBillDTO.setBillDate(LocalDateTime.now());
        testBillDTO.setStatus("PENDING");
        testBillDTO.setSubtotal(BigDecimal.valueOf(10000.0));
        testBillDTO.setTaxAmount(BigDecimal.valueOf(300.0));
        testBillDTO.setTotalAmount(BigDecimal.valueOf(10300.0));
        testBillDTO.setBillItems(Arrays.asList(testItemDTO));
    }

    @Test
    void testCreateBill_Success() throws Exception {
        // Arrange
        when(billService.createBill(any(BillDTO.class))).thenReturn(testBillDTO);

        // Act & Assert
        mockMvc.perform(post("/api/bills/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testBillDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.billId").value(1))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.billNumber").value("BILL-20240829-0001"));

        verify(billService).createBill(any(BillDTO.class));
    }

    @Test
    void testGetBillById_Success() throws Exception {
        // Arrange
        when(billService.getBillById(1L)).thenReturn(testBillDTO);

        // Act & Assert
        mockMvc.perform(get("/api/bills/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.billId").value(1))
                .andExpect(jsonPath("$.customerId").value(1));

        verify(billService).getBillById(1L);
    }

    @Test
    void testGetBillByNumber_Success() throws Exception {
        // Arrange
        when(billService.getBillByNumber("BILL-20240829-0001")).thenReturn(testBillDTO);

        // Act & Assert
        mockMvc.perform(get("/api/bills/number/BILL-20240829-0001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.billNumber").value("BILL-20240829-0001"));

        verify(billService).getBillByNumber("BILL-20240829-0001");
    }

    @Test
    void testGetBillsByCustomerId_Success() throws Exception {
        // Arrange
        List<BillDTO> bills = Arrays.asList(testBillDTO);
        when(billService.getBillsByCustomerId(1L)).thenReturn(bills);

        // Act & Assert
        mockMvc.perform(get("/api/bills/customer/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].customerId").value(1))
                .andExpect(jsonPath("$[0].billId").value(1));

        verify(billService).getBillsByCustomerId(1L);
    }

    @Test
    void testGetBillsByVendorId_Success() throws Exception {
        // Arrange
        List<BillDTO> bills = Arrays.asList(testBillDTO);
        when(billService.getBillsByVendorId(1L)).thenReturn(bills);

        // Act & Assert
        mockMvc.perform(get("/api/bills/vendor/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].vendorId").value(1));

        verify(billService).getBillsByVendorId(1L);
    }

    @Test
    void testGetBillsByStatus_Success() throws Exception {
        // Arrange
        List<BillDTO> bills = Arrays.asList(testBillDTO);
        when(billService.getBillsByStatus("PENDING")).thenReturn(bills);

        // Act & Assert
        mockMvc.perform(get("/api/bills/status/PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("PENDING"));

        verify(billService).getBillsByStatus("PENDING");
    }

    @Test
    void testUpdateBillStatus_Success() throws Exception {
        // Arrange
        testBillDTO.setStatus("PAID");
        when(billService.updateBillStatus(1L, "PAID")).thenReturn(testBillDTO);

        // Act & Assert
        mockMvc.perform(put("/api/bills/1/status")
                .param("status", "PAID"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PAID"));

        verify(billService).updateBillStatus(1L, "PAID");
    }

    @Test
    void testDeleteBill_Success() throws Exception {
        // Arrange
        doNothing().when(billService).deleteBill(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/bills/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Bill with ID 1 has been deleted successfully"));

        verify(billService).deleteBill(1L);
    }

    @Test
    void testHealthCheck() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/bills/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("Billing Service is running!"));
    }

    @Test
    void testCreateBill_ValidationError() throws Exception {
        // Arrange - Invalid bill data
        testBillDTO.setCustomerId(null);

        // Act & Assert
        mockMvc.perform(post("/api/bills/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testBillDTO)))
                .andExpect(status().isBadRequest());

        verify(billService, never()).createBill(any(BillDTO.class));
    }

    @Test
    void testUpdateBillStatus_MissingStatus() throws Exception {
        // Act & Assert
        mockMvc.perform(put("/api/bills/1/status"))
                .andExpect(status().isBadRequest());

        verify(billService, never()).updateBillStatus(any(), any());
    }
} 