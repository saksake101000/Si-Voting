package com.example.clientapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ClientappApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClientappApplication.class, args);
		System.out.println("===============================================");
		System.out.println("SI-VOTING Client App is running on port 8081");
		System.out.println("Open your browser: http://localhost:8081");
		System.out.println("===============================================");
	}

}
