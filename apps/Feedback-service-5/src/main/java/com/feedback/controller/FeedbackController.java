package com.feedback.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.feedback.entity.Feedback;
import com.feedback.service.FeedbackService;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {
	
	@Autowired
	private FeedbackService feedbackService;
	
	
	//add feedback
	@PostMapping("/add")
	public ResponseEntity<Feedback> addFeedback(@RequestBody Feedback feedback)  {
	      Feedback addedfeedback = feedbackService.addFeedback(feedback);
	      return ResponseEntity.ok(addedfeedback);
	       
	}

    // Get feedback by customer ID
      @GetMapping("/customer/{customerId}")
      public ResponseEntity<List<Feedback>> getFeedbackByCustomerId(@PathVariable Long customerId ){
    	  List<Feedback> feedbackList=feedbackService.getFeedBackByCustomerId(customerId);
    	  return ResponseEntity.ok(feedbackList);
      }
      
      @GetMapping("/getAllfeedbacks")
      public ResponseEntity<List<Feedback>> getAllfeedback(){
    	  return ResponseEntity.ok(feedbackService.getAllFeedback());
      }
}