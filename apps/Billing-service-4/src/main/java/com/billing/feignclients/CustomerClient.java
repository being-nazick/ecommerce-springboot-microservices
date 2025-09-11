package com.billing.feignclients;

import com.billing.dto.CustomerDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "Customer-service")
public interface CustomerClient {
    
    @GetMapping("/api/customers/{customerId}")
    CustomerDTO getCustomerById(@PathVariable Long customerId);
} 