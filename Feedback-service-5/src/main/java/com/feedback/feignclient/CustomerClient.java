package com.feedback.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.feedback.dto.CustomerDTO;

@FeignClient(name = "customer-service")
public interface CustomerClient {

	@GetMapping("/api/customers/{customerId}")
	CustomerDTO getCustomerById(@PathVariable("customerId") Long customerId);

}
