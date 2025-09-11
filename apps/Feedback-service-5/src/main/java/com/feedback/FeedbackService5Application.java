package com.feedback;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class FeedbackService5Application {

	public static void main(String[] args) {
		SpringApplication.run(FeedbackService5Application.class, args);
	}

}
