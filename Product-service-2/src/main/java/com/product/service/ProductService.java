package com.product.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.product.repository.ProductRepository;
import com.product.entity.Product;
import com.product.exception.ProductNotFoundException;
// import com.product.feignclients.VendorClient;

@Service
public class ProductService {

	@Autowired
	private ProductRepository productRepository;
	
	// @Autowired
	// private VendorClient vendorClient;

	public Product addProduct(Product product) {
		return productRepository.save(product);
	}

	public Product updateProduct(Long productId, Product productDetails) {
		Product product = productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException());
		product.setProductMaterial(productDetails.getProductMaterial());
		product.setProductWeight(productDetails.getProductWeight());
		product.setProductGmPerWeight(productDetails.getProductGmPerWeight());
		product.setProductQuantity(productDetails.getProductQuantity());
		return productRepository.save(product);
	}

	public Product getProductById(Long productId) {
		Product product = productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException());
		return product;
	}

	public List<Product> getAllProducts() {
		return productRepository.findAll();
	}

	public List<Product> getProductsByVendor(Long vendorId) {
		return productRepository.findByVendorId(vendorId);
	}

	public void deleteProduct(Long productId) {
		if (productRepository.existsById(productId)) {
			productRepository.deleteById(productId);
		} else {
			throw new ProductNotFoundException();
		}
	}
}
