package com.example.sivoting.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventStatisticsResponse {
    private Long eventId;
    private String title;
    private String code;
    private Integer totalVotes;
    private Integer totalCandidates;
    private Integer totalParticipants;
    private List<CandidateStatistics> candidateStatistics;
    private Boolean isActive;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CandidateStatistics {
        private Long candidateId;
        private String name;
        private String photoUrl;
        private Integer voteCount;
        private Double percentage;
    }
}
