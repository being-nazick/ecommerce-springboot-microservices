package com.vendor.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.vendor.entity.Vendor;
import com.vendor.service.VendorService;



@RestController
@RequestMapping("/api/vendors")
public class VendorController {
	
	@Autowired
	private VendorService vendorService;
	
	@PostMapping("/add")
	public ResponseEntity<Vendor> addVendor(@RequestBody Vendor vendor) {
		Vendor newVendor = vendorService.addVendor(vendor);
		return ResponseEntity.ok(newVendor);
	}
	
	@PutMapping("/{vendorId}")
	public ResponseEntity<?> updateVendor (@PathVariable Long vendorId, @RequestBody Vendor vendor) {
		Vendor updatedVendor=vendorService.updateVendor(vendor);
		if(updatedVendor!=null) {
			return ResponseEntity.ok(updatedVendor);
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Vendor not found");
	}
	
	@GetMapping("/{vendorId}")
	public ResponseEntity<Vendor> getVendorById (@PathVariable Long vendorId) {
		Vendor vendor = vendorService.getVendorById(vendorId);
		return ResponseEntity.ok(vendor);
	}
	
	@GetMapping("/getAll")
	public ResponseEntity<List<Vendor>> getAllVendors() {
		List<Vendor> vendorList= vendorService.getAllVendors();
		return ResponseEntity.ok(vendorList);
	}
	
	@DeleteMapping("/{vendorId}")
	public ResponseEntity<String> deleteVendor(@PathVariable Long vendorId){
		vendorService.deleteVendor(vendorId);
		return ResponseEntity.ok("Vendor deleted successfully");
	}

}
