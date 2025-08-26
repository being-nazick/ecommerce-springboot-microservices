package com.customer.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.customer.repository.CustomerRepository;
import com.customer.dto.ProductDTO;
import com.customer.entity.Customer;
import com.customer.exception.CustomerNotFoundException;
import com.customer.feignclients.ProductClient;

@Service
public class CustomerService {

	@Autowired
	private CustomerRepository customerRepo;
	
	@Autowired
	private ProductClient productClient;
	
	
	public Customer addCustomer(Customer customer) {
		return customerRepo.save(customer);
	}
	
	public Customer getCustomerById(Long customerId) {
		return customerRepo.findById(customerId)
				.orElseThrow(()-> new CustomerNotFoundException("Customer with "+customerId+" is not found"));
	}
	
	public Customer updateCustomer(Customer customer) {
		if(customerRepo.existsById(customer.getCustomerId())) {
			return customerRepo.save(customer);
		}
		else {
			throw new CustomerNotFoundException("Customer with "+customer.getCustomerId()+" is not found");
		}
	}
	
	public void deleteCustomer(Long customerId) {
		if(customerRepo.existsById(customerId)) {
			customerRepo.deleteById(customerId);
		}
		else {
			throw new CustomerNotFoundException("Customer with "+customerId+" is not found");
		}
	}
	
	public ProductDTO viewProductById(Long productId) {
		return productClient.getProductById(productId);
	}
	
	public List<ProductDTO> viewAllProducts(){
		return productClient.getAllProducts();
	}
	
	public List<ProductDTO> viewProductsByVendor(Long vendorId){
		return productClient.getProductsByVendor(vendorId);
	}
}

