package com.product.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.product.entity.Product;
import com.product.exception.ProductNotFoundException;
import com.product.service.ProductService;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://localhost:5173")
public class ProductController {

	@Autowired
	private ProductService productService;

	@PostMapping("/addProduct")
	public ResponseEntity<?> addProduct(@RequestBody Product product) {
		Product newProduct = productService.addProduct(product);
		if (newProduct != null) {
			return ResponseEntity.ok(newProduct);
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not added");
	}

	@PutMapping("/{productId}")
	public ResponseEntity<?> updateProduct(@PathVariable Long productId, @RequestBody Product productDetails) {
		try {
			Product updatedProduct = productService.updateProduct(productId, productDetails);
			return ResponseEntity.ok(updatedProduct);
		} catch (ProductNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product with id: " + productId + " not found");
		}
	}

	@GetMapping("/getAll")
	public ResponseEntity<List<Product>> getAll() {
		List<Product> productList = productService.getAllProducts();
		return ResponseEntity.ok(productList);
	}

	@GetMapping("/{productId}")
	public ResponseEntity<?> getProductById(@PathVariable Long productId) {
		Product product = productService.getProductById(productId);
		if (product != null) {
			return ResponseEntity.ok(product);
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
	}

	@GetMapping("/vendor/{vendorId}")
	public ResponseEntity<List<Product>> getProductsByVendor(@PathVariable Long vendorId) {
		List<Product> products = productService.getProductsByVendor(vendorId);
		return ResponseEntity.ok(products);
	}

	@DeleteMapping("/{productId}")
	public ResponseEntity<String> deleteProduct(@PathVariable Long productId) {
		try {
			productService.deleteProduct(productId);
			return ResponseEntity.ok("Product deleted successfully");
		} catch (ProductNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product with id: " + productId + " not found");
		}

	}

}
