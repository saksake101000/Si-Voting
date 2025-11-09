package com.example.clientapp.dto;

import lombok.Data;
import java.util.List;

@Data
public class CreateEventRequest {
    private String title;
    private String description;
    private String startDate;
    private String endDate;
    private Boolean isPublic;
    private List<CandidateRequest> candidates;
    
    @Data
    public static class CandidateRequest {
        private String name;
        private String description;
        private String photoUrl;
    }
}
