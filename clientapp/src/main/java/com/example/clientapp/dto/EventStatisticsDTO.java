package com.example.clientapp.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class EventStatisticsDTO {
    private Long eventId;
    private String eventTitle;
    private Integer totalVoters;
    private Integer totalVotes;
    private Double participationRate;
    private Map<Long, Integer> candidateVotes;
    private Map<Long, String> candidateNames;

    @JsonProperty("title")
    @SuppressWarnings("unused")
    private void setTitle(String title) {
        this.eventTitle = title;
    }

    @JsonProperty("totalParticipants")
    @SuppressWarnings("unused")
    private void setTotalParticipants(Integer totalParticipants) {
        this.totalVoters = totalParticipants;
    }

    @JsonProperty("candidateStatistics")
    @SuppressWarnings("unused")
    private void setCandidateStatistics(List<CandidateStatistics> stats) {
        if (stats == null) return;
        if (this.candidateVotes == null) this.candidateVotes = new HashMap<>();
        if (this.candidateNames == null) this.candidateNames = new HashMap<>();
        for (CandidateStatistics cs : stats) {
            if (cs == null) continue;
            if (cs.getCandidateId() != null) {
                this.candidateNames.put(cs.getCandidateId(), cs.getName());
                Integer v = Objects.requireNonNullElse(cs.getVoteCount(), 0);
                this.candidateVotes.put(cs.getCandidateId(), v);
            }
        }
    }

    // Nested class to match backend candidate statistics structure
    @Data
    public static class CandidateStatistics {
        private Long candidateId;
        private String name;
        private String photoUrl;
        private Integer voteCount;
        private Double percentage;
    }
}
