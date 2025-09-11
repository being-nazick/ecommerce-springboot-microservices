package com.customer.service;

import com.customer.dto.ProductDTO;
import com.customer.entity.Customer;
import com.customer.exception.CustomerNotFoundException;
import com.customer.feignclients.ProductClient;
import com.customer.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ProductClient productClient;

    @InjectMocks
    private CustomerService customerService;

    private Customer testCustomer;
    private ProductDTO testProductDTO;

    @BeforeEach
    void setUp() {
        // Setup test data
        testCustomer = new Customer();
        testCustomer.setCustomerId(1L);
        testCustomer.setFirstName("John");
        testCustomer.setLastName("Doe");
        testCustomer.setEmailId("john.doe@example.com");
        testCustomer.setPhoneNo("1234567890");
        testCustomer.setPassword("password123");

        testProductDTO = new ProductDTO();
        // ProductDTO only has productId field in Customer service
    }

    @Test
    void testAddCustomer_Success() {
        // Arrange
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);

        // Act
        Customer result = customerService.addCustomer(testCustomer);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getCustomerId());
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void testGetCustomerById_Success() {
        // Arrange
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));

        // Act
        Customer result = customerService.getCustomerById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getCustomerId());
        assertEquals("John", result.getFirstName());
        verify(customerRepository).findById(1L);
    }

    @Test
    void testGetCustomerById_NotFound() {
        // Arrange
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CustomerNotFoundException.class, () -> {
            customerService.getCustomerById(1L);
        });
        verify(customerRepository).findById(1L);
    }

    @Test
    void testUpdateCustomer_Success() {
        // Arrange
        Customer updatedCustomer = new Customer();
        updatedCustomer.setCustomerId(1L);
        updatedCustomer.setFirstName("Jane");
        updatedCustomer.setLastName("Doe");
        updatedCustomer.setEmailId("jane.doe@example.com");

        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(customerRepository.save(any(Customer.class))).thenReturn(updatedCustomer);

        // Act
        Customer result = customerService.updateCustomer(updatedCustomer);

        // Assert
        assertNotNull(result);
        assertEquals("Jane", result.getFirstName());
        verify(customerRepository).findById(1L);
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void testUpdateCustomer_NotFound() {
        // Arrange
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CustomerNotFoundException.class, () -> {
            customerService.updateCustomer(testCustomer);
        });
        verify(customerRepository).findById(1L);
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void testDeleteCustomer_Success() {
        // Arrange
        when(customerRepository.existsById(1L)).thenReturn(true);
        doNothing().when(customerRepository).deleteById(1L);

        // Act
        customerService.deleteCustomer(1L);

        // Assert
        verify(customerRepository).existsById(1L);
        verify(customerRepository).deleteById(1L);
    }

    @Test
    void testDeleteCustomer_NotFound() {
        // Arrange
        when(customerRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        assertThrows(CustomerNotFoundException.class, () -> {
            customerService.deleteCustomer(1L);
        });
        verify(customerRepository).existsById(1L);
        verify(customerRepository, never()).deleteById(any());
    }

    @Test
    void testViewProductById_Success() {
        // Arrange
        when(productClient.getProductById(1L)).thenReturn(testProductDTO);

        // Act
        ProductDTO result = customerService.viewProductById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getProductId());
        verify(productClient).getProductById(1L);
    }

    @Test
    void testViewAllProducts_Success() {
        // Arrange
        ProductDTO product2 = new ProductDTO();
        
        List<ProductDTO> products = Arrays.asList(testProductDTO, product2);
        when(productClient.getAllProducts()).thenReturn(products);

        // Act
        List<ProductDTO> result = customerService.viewAllProducts();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(productClient).getAllProducts();
    }

    @Test
    void testViewProductsByVendor_Success() {
        // Arrange
        ProductDTO product2 = new ProductDTO();
        
        List<ProductDTO> products = Arrays.asList(testProductDTO, product2);
        when(productClient.getProductsByVendor(1L)).thenReturn(products);

        // Act
        List<ProductDTO> result = customerService.viewProductsByVendor(1L);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(productClient).getProductsByVendor(1L);
    }

    @Test
    void testViewProductById_ProductNotFound() {
        // Arrange
        when(productClient.getProductById(1L)).thenReturn(null);

        // Act
        ProductDTO result = customerService.viewProductById(1L);

        // Assert
        assertNull(result);
        verify(productClient).getProductById(1L);
    }

    @Test
    void testViewAllProducts_EmptyList() {
        // Arrange
        when(productClient.getAllProducts()).thenReturn(Arrays.asList());

        // Act
        List<ProductDTO> result = customerService.viewAllProducts();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(productClient).getAllProducts();
    }

    @Test
    void testViewProductsByVendor_EmptyList() {
        // Arrange
        when(productClient.getProductsByVendor(1L)).thenReturn(Arrays.asList());

        // Act
        List<ProductDTO> result = customerService.viewProductsByVendor(1L);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(productClient).getProductsByVendor(1L);
    }
} 