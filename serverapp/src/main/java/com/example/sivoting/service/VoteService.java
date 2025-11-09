package com.example.sivoting.service;

import com.example.sivoting.dto.vote.VoteRequest;
import com.example.sivoting.dto.websocket.VoteUpdateMessage;
import com.example.sivoting.model.Candidate;
import com.example.sivoting.model.Event;
import com.example.sivoting.model.User;
import com.example.sivoting.model.Vote;
import com.example.sivoting.repository.CandidateRepository;
import com.example.sivoting.repository.EventRepository;
import com.example.sivoting.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class VoteService {
    
    @Autowired
    private VoteRepository voteRepository;
    
    @Autowired
    private EventRepository eventRepository;
    
    @Autowired
    private CandidateRepository candidateRepository;
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    @Transactional
    public Vote submitVote(VoteRequest request) {
        User currentUser = authService.getCurrentUser();
        
        // Check if event exists
        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new RuntimeException("Event tidak ditemukan!"));
        
        // Check if event is active
        if (!event.getIsActive()) {
            throw new RuntimeException("Event tidak aktif!");
        }
        
        // Check voting time period
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(event.getStartDate())) {
            throw new RuntimeException("Voting belum dimulai!");
        }
        if (now.isAfter(event.getEndDate())) {
            throw new RuntimeException("Waktu voting telah berakhir!");
        }
        
        // Check if candidate exists and belongs to the event
        Candidate candidate = candidateRepository.findById(request.getCandidateId())
                .orElseThrow(() -> new RuntimeException("Kandidat tidak ditemukan!"));
        
        if (!candidate.getEventId().equals(request.getEventId())) {
            throw new RuntimeException("Kandidat tidak terdaftar dalam event ini!");
        }
        
        // Check if user has already voted in this event
        if (voteRepository.existsByUserIdAndEventId(currentUser.getId(), request.getEventId())) {
            throw new RuntimeException("Anda sudah memberikan suara di event ini!");
        }
        
        // Create vote
        Vote vote = new Vote();
        vote.setUserId(currentUser.getId());
        vote.setCandidateId(request.getCandidateId());
        vote.setEventId(request.getEventId());
        
        Vote savedVote = voteRepository.save(vote);
        
        // Broadcast vote update via WebSocket
        broadcastVoteUpdate(event, candidate);
        
        return savedVote;
    }
    
    private void broadcastVoteUpdate(Event event, Candidate candidate) {
        try {
            Long voteCount = voteRepository.countByCandidateId(candidate.getId());
            Long totalVotes = voteRepository.countByEventId(event.getId());
            
            VoteUpdateMessage message = new VoteUpdateMessage(
                    event.getId(),
                    candidate.getId(),
                    candidate.getName(),
                    voteCount,
                    totalVotes,
                    "VOTE_ADDED"
            );
            
            // Broadcast to all subscribers of this event
            messagingTemplate.convertAndSend("/topic/event/" + event.getId(), message);
        } catch (Exception e) {
            // Log error but don't fail the vote
            System.err.println("Error broadcasting vote update: " + e.getMessage());
        }
    }
    
    public boolean hasUserVoted(Long userId, Long eventId) {
        return voteRepository.existsByUserIdAndEventId(userId, eventId);
    }
}
