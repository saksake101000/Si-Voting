package com.example.clientapp.service;

import com.example.clientapp.dto.ApiResponse;
import com.example.clientapp.dto.VoteRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VoteService {
    
    private final ApiService apiService;
    
    // Submit vote
    public ApiResponse<Object> submitVote(Long candidateId, Long eventId, String token) {
        VoteRequest voteData = new VoteRequest(candidateId, eventId);

        // Server returns ApiResponse<Vote> (an object). Expect a generic Object here to avoid
        // deserialization errors when the client previously expected a String.
        return apiService.post(
            "/votes",
            voteData,
            token,
            new TypeReference<ApiResponse<Object>>() {}
        );
    }
}
