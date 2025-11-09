package com.example.clientapp.service;

import com.example.clientapp.dto.ApiResponse;
import com.example.clientapp.dto.UserProfileDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final ApiService apiService;
    
    // Get current user profile
    public ApiResponse<UserProfileDTO> getUserProfile(String token) {
        return apiService.get(
            "/users/profile",
            token,
            new TypeReference<ApiResponse<UserProfileDTO>>() {}
        );
    }

    // Get current user activities
    public ApiResponse<java.util.List<com.example.clientapp.dto.activity.ActivityItemDTO>> getActivities(String token) {
        return apiService.get(
            "/users/activities",
            token,
            new TypeReference<ApiResponse<java.util.List<com.example.clientapp.dto.activity.ActivityItemDTO>>>() {}
        );
    }

    // Get dashboard stats for current user (typed DTO)
    public ApiResponse<com.example.clientapp.dto.user.DashboardStatsDTO> getDashboardStats(String token) {
        return apiService.get(
            "/users/dashboard-stats",
            token,
            new TypeReference<ApiResponse<com.example.clientapp.dto.user.DashboardStatsDTO>>() {}
        );
    }

    // Get events the current user has voted in
    public ApiResponse<java.util.List<com.example.clientapp.dto.EventDTO>> getVotedEvents(String token) {
        return apiService.get(
            "/users/voted-events",
            token,
            new TypeReference<ApiResponse<java.util.List<com.example.clientapp.dto.EventDTO>>>() {}
        );
    }
    
    // Get user profile by ID
    public ApiResponse<UserProfileDTO> getUserProfileById(Long userId, String token) {
        return apiService.get(
            "/users/" + userId + "/profile",
            token,
            new TypeReference<ApiResponse<UserProfileDTO>>() {}
        );
    }
}
