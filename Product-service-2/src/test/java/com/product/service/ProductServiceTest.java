package com.product.service;

import com.product.dto.VendorDTO;
import com.product.entity.Product;
import com.product.exception.ProductNotFoundException;
import com.product.feignclients.VendorClient;
import com.product.repository.ProductRepository;
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
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private VendorClient vendorClient;

    @InjectMocks
    private ProductService productService;

    private Product testProduct;
    private VendorDTO testVendorDTO;

    @BeforeEach
    void setUp() {
        // Setup test data
        testProduct = new Product();
        testProduct.setProductId(1L);
        testProduct.setProductMaterial("gold");
        testProduct.setProductWeight(10.0);
        testProduct.setProductGmPerWeight(1.0);
        testProduct.setProductQuantity(5);
        testProduct.setProductUrl("http://example.com/product1");
        testProduct.setVendorId(1L);

        testVendorDTO = new VendorDTO();
        testVendorDTO.setVendorID(1L);
        testVendorDTO.setName("Gold Jewellers");
        testVendorDTO.setMailId("info@goldjewellers.com");
        testVendorDTO.setPhoneNo("1234567890");
    }

    @Test
    void testAddProduct_Success() {
        // Arrange
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // Act
        Product result = productService.addProduct(testProduct);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getProductId());
        assertEquals("gold", result.getProductMaterial());
        assertEquals(10.0, result.getProductWeight());
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void testGetProductById_Success() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        // Act
        Product result = productService.getProductById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getProductId());
        assertEquals("gold", result.getProductMaterial());
        verify(productRepository).findById(1L);
    }

    @Test
    void testGetProductById_NotFound() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ProductNotFoundException.class, () -> {
            productService.getProductById(1L);
        });
        verify(productRepository).findById(1L);
    }

    @Test
    void testGetAllProducts_Success() {
        // Arrange
        Product product2 = new Product();
        product2.setProductId(2L);
        product2.setProductMaterial("silver");
        product2.setProductWeight(15.0);
        product2.setProductGmPerWeight(1.0);
        product2.setProductQuantity(3);
        product2.setProductUrl("http://example.com/product2");
        product2.setVendorId(1L);

        List<Product> products = Arrays.asList(testProduct, product2);
        when(productRepository.findAll()).thenReturn(products);

        // Act
        List<Product> result = productService.getAllProducts();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("gold", result.get(0).getProductMaterial());
        assertEquals("silver", result.get(1).getProductMaterial());
        verify(productRepository).findAll();
    }

    @Test
    void testGetProductsByVendor_Success() {
        // Arrange
        Product product2 = new Product();
        product2.setProductId(2L);
        product2.setProductMaterial("platinum");
        product2.setProductWeight(8.0);
        product2.setProductGmPerWeight(1.0);
        product2.setProductQuantity(2);
        product2.setProductUrl("http://example.com/product2");
        product2.setVendorId(1L);

        List<Product> products = Arrays.asList(testProduct, product2);
        when(productRepository.findByVendorId(1L)).thenReturn(products);

        // Act
        List<Product> result = productService.getProductsByVendor(1L);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getVendorId());
        assertEquals(1L, result.get(1).getVendorId());
        verify(productRepository).findByVendorId(1L);
    }

    @Test
    void testUpdateProduct_Success() {
        // Arrange
        Product updatedProduct = new Product();
        updatedProduct.setProductId(1L);
        updatedProduct.setProductMaterial("white gold");
        updatedProduct.setProductWeight(12.0);
        updatedProduct.setProductGmPerWeight(1.0);
        updatedProduct.setProductQuantity(7);
        updatedProduct.setProductUrl("http://example.com/product1-updated");
        updatedProduct.setVendorId(1L);

        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

        // Act
        Product result = productService.updateProduct(1L, updatedProduct);

        // Assert
        assertNotNull(result);
        assertEquals("white gold", result.getProductMaterial());
        assertEquals(12.0, result.getProductWeight());
        assertEquals(7, result.getProductQuantity());
        verify(productRepository).findById(1L);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void testUpdateProduct_NotFound() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ProductNotFoundException.class, () -> {
            productService.updateProduct(1L, testProduct);
        });
        verify(productRepository).findById(1L);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void testDeleteProduct_Success() {
        // Arrange
        when(productRepository.existsById(1L)).thenReturn(true);
        doNothing().when(productRepository).deleteById(1L);

        // Act
        productService.deleteProduct(1L);

        // Assert
        verify(productRepository).existsById(1L);
        verify(productRepository).deleteById(1L);
    }

    @Test
    void testDeleteProduct_NotFound() {
        // Arrange
        when(productRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        assertThrows(ProductNotFoundException.class, () -> {
            productService.deleteProduct(1L);
        });
        verify(productRepository).existsById(1L);
        verify(productRepository, never()).deleteById(any());
    }

    @Test
    void testGetAllProducts_EmptyList() {
        // Arrange
        when(productRepository.findAll()).thenReturn(Arrays.asList());

        // Act
        List<Product> result = productService.getAllProducts();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(productRepository).findAll();
    }

    @Test
    void testGetProductsByVendor_EmptyList() {
        // Arrange
        when(productRepository.findByVendorId(1L)).thenReturn(Arrays.asList());

        // Act
        List<Product> result = productService.getProductsByVendor(1L);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(productRepository).findByVendorId(1L);
    }

    @Test
    void testGetProductById_InvalidId() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            productService.getProductById(null);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            productService.getProductById(0L);
        });
        verify(productRepository, never()).findById(any());
    }

    @Test
    void testGetProductsByVendor_InvalidId() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            productService.getProductsByVendor(null);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            productService.getProductsByVendor(0L);
        });
        verify(productRepository, never()).findByVendorId(any());
    }
} 