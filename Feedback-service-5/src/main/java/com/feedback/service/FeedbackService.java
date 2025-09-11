package com.feedback.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.feedback.repository.FeedbackRepository;
import com.feedback.entity.Feedback;
import com.feedback.exception.FeedbackException;
// import com.feedback.feignclient.CustomerClient;
// import com.feedback.feignclient.ProductClient;

@Service
public class FeedbackService {
	
	@Autowired
	private FeedbackRepository feedbackRepository;
	
	// @Autowired
	// private ProductClient productClient;
	
	// @Autowired
	// private CustomerClient customerClient;
	
	public Feedback addFeedback(Feedback feedback) {
		
//		CustomerDTO customer=customerClient.getCustomerById(feedback.getCustomerId());
//		if(customer == null) {
//			throw new RuntimeException("Customer not found");
//		}
		
//		ProductDTO product=productClient.getProductById(productId);
//		if(product==null){
//			throw new RuntimeException("Product not found");
//		}
//		Feedback feedback= new Feedback();
//		feedback.setCustomerId(customerId);
//		feedback.setCustomerName(customer.getFirstName()+" "+customer.getLastName());
//		feedback.setProductName(product.getProductName);
//		feedback.setFeedback(feedbackContent);
		return feedbackRepository.save(feedback);
	}
	
	public List<Feedback> getFeedBackByCustomerId(Long customerId){
		List<Feedback> feedbackList=feedbackRepository.findByCustomerId(customerId);
		if(feedbackList.isEmpty()) {
			throw new FeedbackException("List is empty");
		}
		return feedbackList;
	}

	public List<Feedback> getAllFeedback(){
		return feedbackRepository.findAll();
	}
	
	public Feedback getFeedbackById(Long feedbackId) {
		return feedbackRepository.findById(feedbackId)
				.orElseThrow(()-> new FeedbackException("Feedback noyt found"));
	}
}
