package com.billing.feignclients;

import com.billing.dto.ProductDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "Product-service")
public interface ProductClient {
    
    @GetMapping("/api/products/{productId}")
    ProductDTO getProductById(@PathVariable Long productId);
    
    @GetMapping("/api/products")
    List<ProductDTO> getAllProducts();
} 