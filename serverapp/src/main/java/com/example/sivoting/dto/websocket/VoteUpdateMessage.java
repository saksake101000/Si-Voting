package com.example.sivoting.dto.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoteUpdateMessage {
    private Long eventId;
    private Long candidateId;
    private String candidateName;
    private Long voteCount;
    private Long totalVotes;
    private String action; // "VOTE_ADDED", "EVENT_UPDATED", "EVENT_DELETED"
}
