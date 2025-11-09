package com.example.sivoting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SivotingApplication {

	public static void main(String[] args) {
		SpringApplication.run(SivotingApplication.class, args);
		System.out.println("===============================================");
		System.out.println("SI-VOTING Backend Server is running on port 8080");
		System.out.println("API Documentation: http://localhost:8080/api/docs/ui");
		System.out.println("===============================================");
	}

}
