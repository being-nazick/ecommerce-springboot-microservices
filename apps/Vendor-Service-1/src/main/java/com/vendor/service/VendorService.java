package com.vendor.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vendor.entity.Vendor;
import com.vendor.exception.VendorNotFoundException;
import com.vendor.repository.VendorRepository;

@Service
public class VendorService {

	@Autowired
	private VendorRepository vendorRepo;

	public Vendor addVendor(Vendor vendor) {
		return vendorRepo.save(vendor);
	}

	public Vendor getVendorById(Long vendorId) {
		return vendorRepo.findById(vendorId)
				.orElseThrow(() -> new VendorNotFoundException("Vendor with id " + vendorId + " is not found"));
	}

	public List<Vendor> getAllVendors() {
		return vendorRepo.findAll();
	}

	public void deleteVendor(Long vendorId) {
		if (vendorRepo.existsById(vendorId)) {
			vendorRepo.deleteById(vendorId);
		} else {
			throw new VendorNotFoundException("Vendor with id " + vendorId + " is not found");
		}
	}
	
	public Vendor updateVendor(Vendor vendor) {
		if(vendorRepo.existsById(vendor.getVendorId())) {
			return vendorRepo.save(vendor);
		}
		else {
			throw new VendorNotFoundException("Vendor is not found");
		}
		
	}

}
