package com.example.clientapp.service;

import com.example.clientapp.dto.ApiResponse;
import com.example.clientapp.dto.AuthResponse;
import com.example.clientapp.dto.UserProfileDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final ApiService apiService;
    
    /**
     * Register new user
     */
    public ApiResponse<AuthResponse> register(String username, String email, String password) {
        return apiService.register(username, email, password);
    }
    
    /**
     * Login user
     */
    public ApiResponse<AuthResponse> login(String username, String password) {
        return apiService.login(username, password);
    }
    
    /**
     * Get current authenticated user info
     */
    public ApiResponse<UserProfileDTO> getCurrentUser(String token) {
        return apiService.get(
            "/auth/me",
            token,
            new TypeReference<ApiResponse<UserProfileDTO>>() {}
        );
    }
    
    /**
     * Google OAuth login
     */
    public ApiResponse<AuthResponse> googleLogin(String credential) {
        Map<String, String> request = new HashMap<>();
        request.put("credential", credential);
        
        return apiService.post(
            "/auth/google",
            request,
            null,
            new TypeReference<ApiResponse<AuthResponse>>() {}
        );
    }
}
