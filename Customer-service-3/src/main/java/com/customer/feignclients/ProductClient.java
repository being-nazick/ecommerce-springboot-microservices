package com.customer.feignclients;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.customer.dto.ProductDTO;

@FeignClient(name="product-service")
public interface ProductClient {
	
	@GetMapping("/api/products/{productId}")
	ProductDTO getProductById(@PathVariable Long productId);
	
	@GetMapping("/api/products/vendor/{vendorId}")
	List<ProductDTO> getProductsByVendor(@PathVariable Long vendorId);
	
	@GetMapping("/api/products/getAll")
	List<ProductDTO> getAllProducts();

}
