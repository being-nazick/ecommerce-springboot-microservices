package com.customer.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.customer.dto.ProductDTO;
import com.customer.entity.Customer;
import com.customer.service.CustomerService;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

	@Autowired
	private CustomerService customerService;
	
	@PostMapping("/addCustomer")
	public ResponseEntity<Customer> addCustomer(@RequestBody Customer customer){
		Customer newCustomer=customerService.addCustomer(customer);
		return ResponseEntity.ok(newCustomer);
	}
	
	@GetMapping("/{customerId}")
	public ResponseEntity<?> getCustomerById(@PathVariable Long customerId){
		Customer customer=customerService.getCustomerById(customerId);
		return customer!=null
				? ResponseEntity.ok(customer)
				:ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer not found");
	}
	
	@PutMapping("/{customerId}")
	public ResponseEntity<?> updateCustomer(@PathVariable Long customerId, @RequestBody Customer customer){
		customer.setCustomerId(customerId);
		Customer updatedCustomer=customerService.updateCustomer(customer);
		return updatedCustomer!=null
				?ResponseEntity.ok(updatedCustomer)
				:ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer details-null");
	}
	
	@GetMapping("/product/{productId}") // Null is returned
	public ResponseEntity<ProductDTO> viewProductById(@PathVariable Long productId){
		ProductDTO product= customerService.viewProductById(productId);
		return ResponseEntity.ok(product);
	}
	
	@GetMapping("/products")
	public ResponseEntity<List<ProductDTO>> viewAllProducts(){
		List<ProductDTO> productList= customerService.viewAllProducts();
		return ResponseEntity.ok(productList);
	}
	
	@GetMapping("/products/vendor/{vendorId}") // Null is returned
	public ResponseEntity<List<ProductDTO>> viewProductsByVendor(@PathVariable Long vendorId){
		List<ProductDTO> products=customerService.viewProductsByVendor(vendorId);
		return ResponseEntity.ok(products);
	}
	
	@DeleteMapping("/{customerId}")
	public ResponseEntity<String> deleteCustomer(@PathVariable Long customerId){
		customerService.deleteCustomer(customerId);
		return ResponseEntity.ok("Customer with "+customerId+" is deleted");
	}
}
