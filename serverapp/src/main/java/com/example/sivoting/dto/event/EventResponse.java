package com.example.sivoting.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventResponse {
    
    private Long id;
    private String code;
    private String title;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean isPublic;
    private Boolean isActive;
    private Long createdBy;
    private String createdByName;
    private List<CandidateDTO> candidates;
    private Long totalVotes;
    private Boolean hasVoted;
    private Long userVoteCandidateId;
}
