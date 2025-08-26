package com.feedback.dto;

public class ProductDTO {

	private Long productId;
	private String productMaterial;
	private double productWeight;
	private double productGmPerWeight;
	private int productQuantity;
	private String productUrl;

	private Long vendorId;

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public String getProductMaterial() {
		return productMaterial;
	}

	public void setProductMaterial(String productMaterial) {
		this.productMaterial = productMaterial;
	}

	public double getProductWeight() {
		return productWeight;
	}

	public void setProductWeight(double productWeight) {
		this.productWeight = productWeight;
	}

	public double getProductGmPerWeight() {
		return productGmPerWeight;
	}

	public void setProductGmPerWeight(double productGmPerWeight) {
		this.productGmPerWeight = productGmPerWeight;
	}

	public int getProductQuantity() {
		return productQuantity;
	}

	public void setProductQuantity(int productQuantity) {
		this.productQuantity = productQuantity;
	}

	public String getProductUrl() {
		return productUrl;
	}

	public void setProductUrl(String productUrl) {
		this.productUrl = productUrl;
	}

	public Long getVendorId() {
		return vendorId;
	}

	public void setVendorId(Long vendorId) {
		this.vendorId = vendorId;
	}

}
