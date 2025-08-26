package com.feedback.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.feedback.dto.ProductDTO;

@FeignClient(name = "product-service")
public interface ProductClient {

	@GetMapping("/api/products/{productId}")
	ProductDTO getProductById(@PathVariable("productId") Long productId);

}
