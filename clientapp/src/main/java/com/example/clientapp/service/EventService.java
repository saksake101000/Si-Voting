package com.example.clientapp.service;

import com.example.clientapp.dto.ApiResponse;
import com.example.clientapp.dto.EventDTO;
import com.example.clientapp.dto.EventStatisticsDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EventService {
    
    private final ApiService apiService;
    
    // ========== Public Events ==========
    
    public ApiResponse<List<EventDTO>> getPublicEvents() {
        return apiService.get(
            "/events/public",
            null,
            new TypeReference<ApiResponse<List<EventDTO>>>() {}
        );
    }
    
    public ApiResponse<EventDTO> getEventByCode(String code, String token) {
        return apiService.get(
            "/events/code/" + code,
            token,
            new TypeReference<ApiResponse<EventDTO>>() {}
        );
    }
    
    // ========== Event Management ==========
    
    public ApiResponse<List<EventDTO>> getAllEvents(String token) {
        try {
            String endpoint = "/events/public";
            return apiService.get(endpoint, token, new TypeReference<ApiResponse<List<EventDTO>>>() {});
        } catch (Exception e) {
            return new ApiResponse<>(false, "Gagal mengambil events: " + e.getMessage(), null);
        }
    }
    
    public ApiResponse<List<EventDTO>> getMyEvents(String token) {
        return apiService.get(
            "/events/my",
            token,
            new TypeReference<ApiResponse<List<EventDTO>>>() {}
        );
    }
    
    public ApiResponse<EventDTO> getEventById(Long id, String token) {
        return apiService.get(
            "/events/" + id,
            token,
            new TypeReference<ApiResponse<EventDTO>>() {}
        );
    }
    
    public ApiResponse<EventDTO> createEvent(Map<String, Object> eventData, String token) {
        return apiService.post(
            "/events",
            eventData,
            token,
            new TypeReference<ApiResponse<EventDTO>>() {}
        );
    }
    
    public ApiResponse<EventDTO> updateEvent(Long id, Map<String, Object> eventData, String token) {
        return apiService.put(
            "/events/" + id,
            eventData,
            token,
            new TypeReference<ApiResponse<EventDTO>>() {}
        );
    }
    
    public ApiResponse<String> deleteEvent(Long id, String token) {
        return apiService.delete(
            "/events/" + id,
            token,
            new TypeReference<ApiResponse<String>>() {}
        );
    }
    
    // ========== Event Statistics ==========
    
    public ApiResponse<EventStatisticsDTO> getEventStatistics(Long eventId, String token) {
        return apiService.get(
            "/events/" + eventId + "/statistics",
            token,
            new TypeReference<ApiResponse<EventStatisticsDTO>>() {}
        );
    }
}
