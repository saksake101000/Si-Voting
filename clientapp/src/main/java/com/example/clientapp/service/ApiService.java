package com.example.clientapp.service;

import com.example.clientapp.dto.ApiResponse;
import com.example.clientapp.dto.AuthResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class ApiService {
    
    @Value("${api.base-url}")
    private String apiBaseUrl;
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    public ApiService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.findAndRegisterModules();
    }
    
    // Register user
    public ApiResponse<AuthResponse> register(String username, String email, String password) {
        try {
            String url = apiBaseUrl + "/auth/register";
            
            Map<String, String> request = new HashMap<>();
            request.put("username", username);
            request.put("email", email);
            request.put("password", password);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, headers);
            
            ResponseEntity<String> response = restTemplate.exchange(
                url, HttpMethod.POST, entity, String.class
            );
            
            return objectMapper.readValue(
                response.getBody(), 
                new TypeReference<ApiResponse<AuthResponse>>() {}
            );
        } catch (Exception e) {
            log.error("Register error", e);
            return new ApiResponse<>(false, "Registrasi gagal: " + e.getMessage(), null);
        }
    }
    
    // Login user
    public ApiResponse<AuthResponse> login(String username, String password) {
        try {
            String url = apiBaseUrl + "/auth/login";
            
            Map<String, String> request = new HashMap<>();
            request.put("username", username);
            request.put("password", password);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, headers);
            
            ResponseEntity<String> response = restTemplate.exchange(
                url, HttpMethod.POST, entity, String.class
            );
            
            return objectMapper.readValue(
                response.getBody(), 
                new TypeReference<ApiResponse<AuthResponse>>() {}
            );
        } catch (Exception e) {
            log.error("Login error", e);
            return new ApiResponse<>(false, "Login gagal: Username atau password salah", null);
        }
    }
    
    // Get with Authorization
    public <T> ApiResponse<T> get(String endpoint, String token, TypeReference<ApiResponse<T>> typeRef) {
        try {
            String url = apiBaseUrl + endpoint;
            
            HttpHeaders headers = new HttpHeaders();
            if (token != null && !token.isEmpty()) {
                headers.set("Authorization", "Bearer " + token);
            }
            
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            
            ResponseEntity<String> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, String.class
            );
            
            return objectMapper.readValue(response.getBody(), typeRef);
        } catch (Exception e) {
            log.error("GET error: " + endpoint, e);
            return new ApiResponse<>(false, "Request gagal: " + e.getMessage(), null);
        }
    }
    
    // Post with Authorization
    public <T, R> ApiResponse<R> post(String endpoint, T body, String token, TypeReference<ApiResponse<R>> typeRef) {
        try {
            String url = apiBaseUrl + endpoint;
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            if (token != null && !token.isEmpty()) {
                headers.set("Authorization", "Bearer " + token);
            }
            
            HttpEntity<T> entity = new HttpEntity<>(body, headers);
            
            ResponseEntity<String> response = restTemplate.exchange(
                url, HttpMethod.POST, entity, String.class
            );
            
            return objectMapper.readValue(response.getBody(), typeRef);
        } catch (Exception e) {
            log.error("POST error: " + endpoint, e);
            return new ApiResponse<>(false, "Request gagal: " + e.getMessage(), null);
        }
    }
    
    // Put with Authorization
    public <T, R> ApiResponse<R> put(String endpoint, T body, String token, TypeReference<ApiResponse<R>> typeRef) {
        try {
            String url = apiBaseUrl + endpoint;
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            if (token != null && !token.isEmpty()) {
                headers.set("Authorization", "Bearer " + token);
            }
            
            HttpEntity<T> entity = new HttpEntity<>(body, headers);
            
            ResponseEntity<String> response = restTemplate.exchange(
                url, HttpMethod.PUT, entity, String.class
            );
            
            return objectMapper.readValue(response.getBody(), typeRef);
        } catch (Exception e) {
            log.error("PUT error: " + endpoint, e);
            return new ApiResponse<>(false, "Request gagal: " + e.getMessage(), null);
        }
    }
    
    // Delete with Authorization
    public <T> ApiResponse<T> delete(String endpoint, String token, TypeReference<ApiResponse<T>> typeRef) {
        try {
            String url = apiBaseUrl + endpoint;
            
            HttpHeaders headers = new HttpHeaders();
            if (token != null && !token.isEmpty()) {
                headers.set("Authorization", "Bearer " + token);
            }
            
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            
            ResponseEntity<String> response = restTemplate.exchange(
                url, HttpMethod.DELETE, entity, String.class
            );
            
            return objectMapper.readValue(response.getBody(), typeRef);
        } catch (Exception e) {
            log.error("DELETE error: " + endpoint, e);
            return new ApiResponse<>(false, "Request gagal: " + e.getMessage(), null);
        }
    }
}
