package com.example.sivoting.service;

import com.example.sivoting.dto.event.CandidateDTO;
import com.example.sivoting.dto.event.EventRequest;
import com.example.sivoting.dto.event.EventResponse;
import com.example.sivoting.dto.event.EventStatisticsResponse;
import com.example.sivoting.model.Candidate;
import com.example.sivoting.model.Event;
import com.example.sivoting.model.User;
import com.example.sivoting.model.Vote;
import com.example.sivoting.repository.CandidateRepository;
import com.example.sivoting.repository.EventRepository;
import com.example.sivoting.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EventService {
    
    @Autowired
    private EventRepository eventRepository;
    
    @Autowired
    private CandidateRepository candidateRepository;
    
    @Autowired
    private VoteRepository voteRepository;
    
    @Autowired
    private AuthService authService;
    
    @Transactional
    public EventResponse createEvent(EventRequest request) {
        User currentUser = authService.getCurrentUser();
        
        // Generate unique code
        String code = generateUniqueCode();
        
        // Create event
        Event event = new Event();
        event.setCode(code);
        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setStartDate(request.getStartDate());
        event.setEndDate(request.getEndDate());
        event.setIsPublic(request.getIsPublic() != null ? request.getIsPublic() : true);
        event.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        event.setCreatedBy(currentUser.getId());
        
        Event savedEvent = eventRepository.save(event);
        
        // Add candidates
        if (request.getCandidates() != null && !request.getCandidates().isEmpty()) {
            for (CandidateDTO candidateDTO : request.getCandidates()) {
                Candidate candidate = new Candidate();
                candidate.setName(candidateDTO.getName());
                candidate.setDescription(candidateDTO.getDescription());
                candidate.setPhotoUrl(candidateDTO.getPhotoUrl());
                candidate.setEventId(savedEvent.getId());
                candidateRepository.save(candidate);
            }
        }
        
        return mapToEventResponse(savedEvent, currentUser.getId());
    }
    
    public EventResponse getEventByCode(String code) {
        Event event = eventRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Event tidak ditemukan dengan kode: " + code));
        
        Long userId = null;
        try {
            User currentUser = authService.getCurrentUser();
            userId = currentUser.getId();
        } catch (Exception e) {
            // User not authenticated
        }
        
        return mapToEventResponse(event, userId);
    }
    
    public EventResponse getEventById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event tidak ditemukan!"));
        
        User currentUser = authService.getCurrentUser();
        return mapToEventResponse(event, currentUser.getId());
    }
    
    public List<EventResponse> getPublicEvents() {
        List<Event> events = eventRepository.findByIsPublicTrueOrderByStartDateDesc();
        
        Long userId = null;
        try {
            User currentUser = authService.getCurrentUser();
            userId = currentUser.getId();
        } catch (Exception e) {
            // User not authenticated
        }
        
        Long finalUserId = userId;
        return events.stream()
                .map(event -> mapToEventResponse(event, finalUserId))
                .collect(Collectors.toList());
    }
    
    public List<EventResponse> getMyEvents() {
        User currentUser = authService.getCurrentUser();
        List<Event> events = eventRepository.findByCreatedBy(currentUser.getId());
        
        return events.stream()
                .map(event -> mapToEventResponse(event, currentUser.getId()))
                .collect(Collectors.toList());
    }
    
    @Transactional
    public EventResponse updateEvent(Long id, EventRequest request) {
        User currentUser = authService.getCurrentUser();
        
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event tidak ditemukan!"));
        
        // Check if user is the creator
        if (!event.getCreatedBy().equals(currentUser.getId())) {
            throw new RuntimeException("Anda tidak memiliki akses untuk mengedit event ini!");
        }
        
        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setStartDate(request.getStartDate());
        event.setEndDate(request.getEndDate());
        event.setIsPublic(request.getIsPublic() != null ? request.getIsPublic() : event.getIsPublic());
        event.setIsActive(request.getIsActive() != null ? request.getIsActive() : event.getIsActive());
        
        Event updatedEvent = eventRepository.save(event);
        
        return mapToEventResponse(updatedEvent, currentUser.getId());
    }
    
    @Transactional
    public void deleteEvent(Long id) {
        User currentUser = authService.getCurrentUser();
        
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event tidak ditemukan!"));
        
        // Check if user is the creator
        if (!event.getCreatedBy().equals(currentUser.getId())) {
            throw new RuntimeException("Anda tidak memiliki akses untuk menghapus event ini!");
        }
        
        eventRepository.delete(event);
    }
    
    private String generateUniqueCode() {
        String code;
        do {
            code = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (eventRepository.findByCode(code).isPresent());
        return code;
    }
    
    private EventResponse mapToEventResponse(Event event, Long userId) {
        EventResponse response = new EventResponse();
        response.setId(event.getId());
        response.setCode(event.getCode());
        response.setTitle(event.getTitle());
        response.setDescription(event.getDescription());
        response.setStartDate(event.getStartDate());
        response.setEndDate(event.getEndDate());
        response.setIsPublic(event.getIsPublic());
        response.setIsActive(event.getIsActive());
        response.setCreatedBy(event.getCreatedBy());
        
        // Get candidates with vote counts
        List<Candidate> candidates = candidateRepository.findByEventId(event.getId());
        Map<Long, Long> voteCounts = getVoteCountsByEvent(event.getId());
        
        List<CandidateDTO> candidateDTOs = candidates.stream()
                .map(candidate -> {
                    CandidateDTO dto = new CandidateDTO();
                    dto.setId(candidate.getId());
                    dto.setName(candidate.getName());
                    dto.setDescription(candidate.getDescription());
                    dto.setPhotoUrl(candidate.getPhotoUrl());
                    dto.setVoteCount(voteCounts.getOrDefault(candidate.getId(), 0L));
                    return dto;
                })
                .collect(Collectors.toList());
        
        response.setCandidates(candidateDTOs);
        response.setTotalVotes(voteRepository.countByEventId(event.getId()));
        
        // Check if user has voted
        if (userId != null) {
            Optional<Vote> userVote = voteRepository.findByUserIdAndEventId(userId, event.getId());
            response.setHasVoted(userVote.isPresent());
            if (userVote.isPresent()) {
                response.setUserVoteCandidateId(userVote.get().getCandidateId());
            }
        } else {
            response.setHasVoted(false);
        }
        
        return response;
    }
    
    private Map<Long, Long> getVoteCountsByEvent(Long eventId) {
        List<Object[]> results = voteRepository.countVotesByCandidateForEvent(eventId);
        Map<Long, Long> voteCounts = new HashMap<>();
        
        for (Object[] result : results) {
            Long candidateId = (Long) result[0];
            Long count = (Long) result[1];
            voteCounts.put(candidateId, count);
        }
        
        return voteCounts;
    }
    
    // Get event statistics with candidate vote counts and percentages
    public EventStatisticsResponse getEventStatistics(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event tidak ditemukan!"));
        
        User currentUser = authService.getCurrentUser();
        if (!event.getCreatedBy().equals(currentUser.getId())) {
            throw new RuntimeException("Anda tidak memiliki akses untuk melihat statistik event ini!");
        }
        
        List<Candidate> candidates = candidateRepository.findByEventId(eventId);
        Map<Long, Long> voteCounts = getVoteCountsByEvent(eventId);
        
        Long totalVotes = voteRepository.countByEventId(eventId);
        Integer totalParticipants = voteRepository.countUniqueVotersByEvent(eventId);
        
        List<EventStatisticsResponse.CandidateStatistics> candidateStats = candidates.stream()
                .map(candidate -> {
                    Long voteCount = voteCounts.getOrDefault(candidate.getId(), 0L);
                    Double percentage = totalVotes > 0 ? (voteCount * 100.0 / totalVotes) : 0.0;
                    
                    return new EventStatisticsResponse.CandidateStatistics(
                            candidate.getId(),
                            candidate.getName(),
                            candidate.getPhotoUrl(),
                            voteCount.intValue(),
                            Math.round(percentage * 100.0) / 100.0
                    );
                })
                .sorted((a, b) -> b.getVoteCount().compareTo(a.getVoteCount()))
                .collect(Collectors.toList());
        
        return new EventStatisticsResponse(
                event.getId(),
                event.getTitle(),
                event.getCode(),
                totalVotes.intValue(),
                candidates.size(),
                totalParticipants,
                candidateStats,
                event.getIsActive()
        );
    }
    
    // Toggle event active status
    @Transactional
    public EventResponse toggleEventStatus(Long eventId) {
        User currentUser = authService.getCurrentUser();
        
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event tidak ditemukan!"));
        
        if (!event.getCreatedBy().equals(currentUser.getId())) {
            throw new RuntimeException("Anda tidak memiliki akses untuk mengubah status event ini!");
        }
        
        event.setIsActive(!event.getIsActive());
        Event updatedEvent = eventRepository.save(event);
        
        return mapToEventResponse(updatedEvent, currentUser.getId());
    }
    
    // Check if voting is allowed (within time period)
    public boolean isVotingAllowed(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event tidak ditemukan!"));
        
        LocalDateTime now = LocalDateTime.now();
        return event.getIsActive() 
                && now.isAfter(event.getStartDate()) 
                && now.isBefore(event.getEndDate());
    }
}
