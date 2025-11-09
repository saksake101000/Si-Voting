package com.example.sivoting.service;

import com.example.sivoting.dto.user.UserProfileResponse;
import com.example.sivoting.model.User;
import com.example.sivoting.repository.EventRepository;
import com.example.sivoting.repository.UserRepository;
import com.example.sivoting.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private EventRepository eventRepository;
    
    @Autowired
    private VoteRepository voteRepository;
    
    @Autowired
    private AuthService authService;
    
    public UserProfileResponse getCurrentUserProfile() {
        User currentUser = authService.getCurrentUser();
        
        Integer totalEventsCreated = eventRepository.countEventsByUser(currentUser.getId());
        Integer totalVotes = voteRepository.countVotesByUser(currentUser.getId());
        
        return new UserProfileResponse(
                currentUser.getId(),
                currentUser.getUsername(),
                currentUser.getEmail(),
                totalEventsCreated,
                totalVotes
        );
    }
    
    public UserProfileResponse getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan!"));
        
        Integer totalEventsCreated = eventRepository.countEventsByUser(userId);
        Integer totalVotes = voteRepository.countVotesByUser(userId);
        
        return new UserProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                totalEventsCreated,
                totalVotes
        );
    }
}
