package com.product.feignclients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.product.dto.VendorDTO;

@FeignClient(name="vendor-service")
public interface VendorClient {
	
	@GetMapping("/api/vendors/{vendorId}")
	VendorDTO getVendorById(@PathVariable Long vendorId);
	
	
	

}
