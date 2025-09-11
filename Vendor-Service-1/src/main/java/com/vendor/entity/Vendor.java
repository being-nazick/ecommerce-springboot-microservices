package com.vendor.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Vendor {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long vendorId;
	private String name;
	private String mailId;
	private String phoneNo;
	private String password;
	
	
	public Long getVendorId() {
		return vendorId;
	}
	
}
