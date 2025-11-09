package com.example.sivoting.controller;

import com.example.sivoting.dto.ApiResponse;
import com.example.sivoting.dto.activity.ActivityItem;
import com.example.sivoting.model.Candidate;
import com.example.sivoting.model.Event;
import com.example.sivoting.model.Vote;
import com.example.sivoting.service.AuthService;
import com.example.sivoting.repository.CandidateRepository;
import com.example.sivoting.repository.EventRepository;
import com.example.sivoting.repository.VoteRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "User related endpoints")
public class UserActivityController {

    @Autowired
    private AuthService authService;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private com.example.sivoting.service.EventService eventService;

    @GetMapping("/activities")
    public ResponseEntity<ApiResponse<List<ActivityItem>>> getCurrentUserActivities() {
        try {
            var currentUser = authService.getCurrentUser();
            Long userId = currentUser.getId();

            List<ActivityItem> activities = new ArrayList<>();

            // Events created by user
            List<Event> events = eventRepository.findByCreatedBy(userId);
            for (Event e : events) {
                ActivityItem item = new ActivityItem();
                item.setType("EVENT_CREATED");
                item.setMessage("Created event: " + e.getTitle());
                item.setTimestamp(e.getCreatedAt());
                item.setEventId(e.getId());
                item.setEventTitle(e.getTitle());
                activities.add(item);
            }

            // Votes by user
            List<Vote> votes = voteRepository.findByUserId(userId);
            for (Vote v : votes) {
                ActivityItem item = new ActivityItem();
                item.setType("VOTED");

                // try to get candidate name
                Optional<Candidate> opt = candidateRepository.findById(v.getCandidateId());
                String candName = opt.map(Candidate::getName).orElse("Candidate #" + v.getCandidateId());

                item.setMessage("Voted for " + candName + " in event #" + v.getEventId());
                item.setTimestamp(v.getCreatedAt());
                item.setEventId(v.getEventId());
                item.setCandidateId(v.getCandidateId());
                item.setCandidateName(candName);
                activities.add(item);
            }

            // sort by timestamp desc
            List<ActivityItem> sorted = activities.stream()
                    .sorted(Comparator.comparing(ActivityItem::getTimestamp, Comparator.nullsLast(Comparator.reverseOrder())))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(ApiResponse.success("Loaded activities", sorted));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Gagal mengambil activities: " + e.getMessage()));
        }
    }

    @GetMapping("/voted-events")
    public ResponseEntity<ApiResponse<List<com.example.sivoting.dto.event.EventResponse>>> getVotedEvents() {
        try {
            var currentUser = authService.getCurrentUser();
            Long userId = currentUser.getId();

            List<Vote> votes = voteRepository.findByUserId(userId);
            java.util.Set<Long> eventIds = votes.stream().map(Vote::getEventId).collect(Collectors.toSet());

            List<com.example.sivoting.dto.event.EventResponse> events = eventIds.stream()
                    .map(id -> eventService.getEventById(id))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(ApiResponse.success("Loaded voted events", events));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Gagal mengambil voted events: " + e.getMessage()));
        }
    }

    @GetMapping("/dashboard-stats")
    public ResponseEntity<ApiResponse<com.example.sivoting.dto.user.DashboardStatsResponse>> getDashboardStats() {
        try {
            var currentUser = authService.getCurrentUser();
            Long userId = currentUser.getId();

            List<Event> myEvents = eventRepository.findByCreatedBy(userId);
            int myEventsCount = myEvents.size();

            int activeEventsCount = 0;
            int eventsWithVotes = 0;

            for (Event e : myEvents) {
                if (Boolean.TRUE.equals(e.getIsActive())) {
                    activeEventsCount++;
                }
                Long votes = voteRepository.countByEventId(e.getId());
                if (votes != null && votes > 0) {
                    eventsWithVotes++;
                }
            }

            int participationRate = 0;
            if (myEventsCount > 0) {
                participationRate = (int) Math.round((eventsWithVotes * 100.0) / myEventsCount);
            }

            com.example.sivoting.dto.user.DashboardStatsResponse resp =
                    new com.example.sivoting.dto.user.DashboardStatsResponse(myEventsCount, activeEventsCount, eventsWithVotes, participationRate);

            return ResponseEntity.ok(ApiResponse.success("Loaded dashboard stats", resp));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Gagal mengambil dashboard stats: " + e.getMessage()));
        }
    }
}
