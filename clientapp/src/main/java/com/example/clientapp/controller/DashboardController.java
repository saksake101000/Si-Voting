package com.example.clientapp.controller;

import com.example.clientapp.dto.ApiResponse;
import com.example.clientapp.dto.EventDTO;
import com.example.clientapp.service.EventService;
import com.example.clientapp.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class DashboardController {
    
    private final EventService eventService;
    private final UserService userService;
    
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (session.getAttribute("token") == null) {
            return "redirect:/login";
        }
        String token = (String) session.getAttribute("token");

        // Try fetching dashboard stats from backend (organizer-focused)
        com.example.clientapp.dto.user.DashboardStatsDTO stats = new com.example.clientapp.dto.user.DashboardStatsDTO();
        boolean statsLoaded = false;
        if (token != null) {
            com.example.clientapp.dto.ApiResponse<com.example.clientapp.dto.user.DashboardStatsDTO> statsResp = userService.getDashboardStats(token);
            if (statsResp != null && statsResp.getSuccess() && statsResp.getData() != null) {
                stats = statsResp.getData();
                statsLoaded = true;
            }
        }

        // Fallback: compute stats client-side if backend endpoint fails
        if (!statsLoaded) {
            ApiResponse<List<EventDTO>> publicEventsResponse = eventService.getPublicEvents();

            int myEvents = 0;
            int activeEvents = 0;
            int eventsWithVotes = 0;

            if (publicEventsResponse.getSuccess() && publicEventsResponse.getData() != null) {
                List<EventDTO> allEvents = publicEventsResponse.getData();
                myEvents = allEvents.size();

                for (EventDTO event : allEvents) {
                    if (Boolean.TRUE.equals(event.getIsActive())) {
                        activeEvents++;
                    }
                    if (event.getTotalVotes() != null && event.getTotalVotes() > 0) {
                        eventsWithVotes++;
                    }
                }
            }

            int participationRate = 0;
            if (myEvents > 0) {
                participationRate = (int) Math.round((eventsWithVotes * 100.0) / myEvents);
            }

            stats = new com.example.clientapp.dto.user.DashboardStatsDTO(myEvents, activeEvents, eventsWithVotes, participationRate);
        }
        
        model.addAttribute("stats", stats);
        model.addAttribute("username", session.getAttribute("username"));
        model.addAttribute("email", session.getAttribute("email"));
        
    // Load recent activities for the authenticated user
    if (token != null) {
            com.example.clientapp.dto.ApiResponse<java.util.List<com.example.clientapp.dto.activity.ActivityItemDTO>> actResp =
                    userService.getActivities(token);
            if (actResp != null && actResp.getSuccess() && actResp.getData() != null) {
                model.addAttribute("activities", actResp.getData());
            } else {
                model.addAttribute("activities", new java.util.ArrayList<>());
            }
        } else {
            model.addAttribute("activities", new java.util.ArrayList<>());
        }
        
        return "dashboard/manage";
    }
    
    @GetMapping("/dashboard/join")
    public String joinEvents(HttpSession session, Model model) {
        if (session.getAttribute("token") == null) {
            return "redirect:/login";
        }
        
        String token = (String) session.getAttribute("token");
        
        // Load all public events
        ApiResponse<List<EventDTO>> response = eventService.getAllEvents(token);
        
        if (response.getSuccess() && response.getData() != null) {
            List<EventDTO> allEvents = response.getData();
            
            // Separate active and history events
            List<EventDTO> activeEvents = new java.util.ArrayList<>();
            List<EventDTO> historyEvents = new java.util.ArrayList<>();
            
            for (EventDTO event : allEvents) {
                if (Boolean.TRUE.equals(event.getIsPublic())) {
                    if (Boolean.TRUE.equals(event.getIsActive())) {
                        activeEvents.add(event);
                    } else {
                        historyEvents.add(event);
                    }
                }
            }

            // Events the user has already voted in
            List<EventDTO> votedEvents = new java.util.ArrayList<>();
            for (EventDTO event : allEvents) {
                if (Boolean.TRUE.equals(event.getHasVoted())) {
                    votedEvents.add(event);
                }
            }

            model.addAttribute("votedEvents", votedEvents);
            
            model.addAttribute("events", activeEvents);
            model.addAttribute("historyEvents", historyEvents);
        } else {
            model.addAttribute("events", new java.util.ArrayList<>());
            model.addAttribute("historyEvents", new java.util.ArrayList<>());
        }
        
        model.addAttribute("username", session.getAttribute("username"));
        model.addAttribute("email", session.getAttribute("email"));
        
        return "dashboard/join";
    }
    
    @GetMapping("/events/join")
    public String joinEventByCode(@RequestParam String code, HttpSession session, RedirectAttributes redirectAttributes) {
        if (session.getAttribute("token") == null) {
            redirectAttributes.addFlashAttribute("error", "Silakan login terlebih dahulu");
            return "redirect:/login?redirect=/dashboard/join";
        }
        
        if (code == null || code.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Kode event tidak boleh kosong");
            return "redirect:/dashboard/join";
        }
        
        // Redirect to event detail page by code
        return "redirect:/events/" + code.trim().toUpperCase();
    }
    
    @GetMapping("/my-events")
    public String myEvents(HttpSession session, Model model) {
        if (session.getAttribute("token") == null) {
            return "redirect:/login";
        }
        
        String token = (String) session.getAttribute("token");
        ApiResponse<List<EventDTO>> response = eventService.getMyEvents(token);
        
        if (response.getSuccess()) {
            model.addAttribute("events", response.getData());
        }
        
        model.addAttribute("username", session.getAttribute("username"));
        model.addAttribute("email", session.getAttribute("email"));
        
        return "dashboard/my-events";
    }
    
    @GetMapping("/create-event")
    public String createEventPage(HttpSession session, Model model) {
        if (session.getAttribute("token") == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("username", session.getAttribute("username"));
        model.addAttribute("email", session.getAttribute("email"));
        model.addAttribute("session", session);
        
        return "dashboard/create-event";
    }
    
    @GetMapping("/events/{code}")
    public String eventDetail(@PathVariable String code, HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        ApiResponse<EventDTO> response = eventService.getEventByCode(code, token);
        
        if (response.getSuccess() && response.getData() != null) {
            EventDTO event = response.getData();
            model.addAttribute("event", event);
            model.addAttribute("candidates", event.getCandidates());
            model.addAttribute("totalVotes", event.getTotalVotes());
            model.addAttribute("hasVoted", event.getHasVoted());
            model.addAttribute("userVoteCandidateId", event.getUserVoteCandidateId());
            
            // Calculate candidate votes map
            if (event.getCandidates() != null) {
                java.util.Map<Long, Long> candidateVotes = new java.util.HashMap<>();
                event.getCandidates().forEach(c -> {
                    Long voteCount = (c.getVoteCount() != null) ? c.getVoteCount() : 0L;
                    if (c.getId() != null) {
                        candidateVotes.put(c.getId(), voteCount);
                    }
                });
                model.addAttribute("candidateVotes", candidateVotes);
            }
            
            if (session.getAttribute("username") != null) {
                model.addAttribute("username", session.getAttribute("username"));
                model.addAttribute("email", session.getAttribute("email"));
            }

                // Try to load event statistics (only available to organizer)
                try {
                    com.example.clientapp.dto.ApiResponse<com.example.clientapp.dto.EventStatisticsDTO> statsResp =
                            eventService.getEventStatistics(event.getId(), token);
                    if (statsResp != null && statsResp.getSuccess() && statsResp.getData() != null) {
                        com.example.clientapp.dto.EventStatisticsDTO stats = statsResp.getData();
                        model.addAttribute("totalVoters", stats.getTotalVoters());
                        model.addAttribute("totalVotes", stats.getTotalVotes());
                        Integer pr = stats.getParticipationRate() != null ? (int) Math.round(stats.getParticipationRate()) : null;
                        model.addAttribute("participationRate", pr);
                    } 
                } catch (Exception ex) {
                }
            
            return "dashboard/event-detail";
        } else {
            model.addAttribute("error", "Event tidak ditemukan atau Anda tidak memiliki akses");
            return "redirect:/dashboard/join?error=" + (response.getMessage() != null ? response.getMessage() : "Event tidak ditemukan");
        }
    }
    
    @PostMapping("/events/{id}/delete")
    public String deleteEvent(
            @PathVariable Long id,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        
        if (session.getAttribute("token") == null) {
            return "redirect:/login";
        }
        
        String token = (String) session.getAttribute("token");
        ApiResponse<String> response = eventService.deleteEvent(id, token);
        
        if (response.getSuccess()) {
            redirectAttributes.addFlashAttribute("success", "Event berhasil dihapus!");
        } else {
            redirectAttributes.addFlashAttribute("error", response.getMessage());
        }
        
        return "redirect:/my-events";
    }
    
    @GetMapping("/dashboard/settings")
    public String settings(HttpSession session, Model model) {
        if (session.getAttribute("token") == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("username", session.getAttribute("username"));
        model.addAttribute("email", session.getAttribute("email"));
        
        return "dashboard/settings";
    }
}
