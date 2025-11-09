package com.example.clientapp.controller;

import com.example.clientapp.dto.ApiResponse;
import com.example.clientapp.dto.CreateEventRequest;
import com.example.clientapp.dto.EventDTO;
import com.example.clientapp.service.EventService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {
    
    private final EventService eventService;
    
    @PostMapping
    public String createEvent(
            @ModelAttribute CreateEventRequest request,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        
        if (session.getAttribute("token") == null) {
            return "redirect:/login";
        }
        
        try {
            Map<String, Object> eventData = new HashMap<>();
            eventData.put("title", request.getTitle());
            eventData.put("description", request.getDescription());
            eventData.put("startDate", request.getStartDate());
            eventData.put("endDate", request.getEndDate());
            Boolean isPublic = (request.getIsPublic() != null) ? request.getIsPublic() : true;
            eventData.put("isPublic", isPublic);
            
            // Build candidates array
            List<Map<String, String>> candidates = new ArrayList<>();
            if (request.getCandidates() != null && !request.getCandidates().isEmpty()) {
                for (CreateEventRequest.CandidateRequest candidate : request.getCandidates()) {
                    Map<String, String> candidateMap = new HashMap<>();
                    candidateMap.put("name", candidate.getName());
                    if (candidate.getDescription() != null && !candidate.getDescription().isEmpty()) {
                        candidateMap.put("description", candidate.getDescription());
                    }
                    if (candidate.getPhotoUrl() != null && !candidate.getPhotoUrl().isEmpty()) {
                        candidateMap.put("photoUrl", candidate.getPhotoUrl());
                    }
                    candidates.add(candidateMap);
                }
            }
            eventData.put("candidates", candidates);
            
            String token = (String) session.getAttribute("token");
            ApiResponse<EventDTO> response = eventService.createEvent(eventData, token);
            
            if (response.getSuccess() && response.getData() != null) {
                redirectAttributes.addFlashAttribute("success", "Event berhasil dibuat!");
                return "redirect:/events/" + response.getData().getCode();
            } else {
                redirectAttributes.addFlashAttribute("error", response.getMessage());
                return "redirect:/create-event";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Gagal membuat event: " + e.getMessage());
            return "redirect:/create-event";
        }
    }
    
    @GetMapping("/edit/{id}")
    public String editEventPage(@PathVariable Long id, HttpSession session, Model model) {
        if (session.getAttribute("token") == null) {
            return "redirect:/login";
        }
        
        String token = (String) session.getAttribute("token");
        ApiResponse<EventDTO> response = eventService.getEventById(id, token);
        
        if (response.getSuccess() && response.getData() != null) {
            model.addAttribute("event", response.getData());
            model.addAttribute("username", session.getAttribute("username"));
            model.addAttribute("email", session.getAttribute("email"));
            return "dashboard/edit-event";
        } else {
            return "redirect:/my-events";
        }
    }
    
    @PostMapping("/{id}/edit")
    public String updateEvent(
            @PathVariable Long id,
            @ModelAttribute CreateEventRequest request,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        
        if (session.getAttribute("token") == null) {
            return "redirect:/login";
        }
        
        try {
            Map<String, Object> eventData = new HashMap<>();
            eventData.put("title", request.getTitle());
            eventData.put("description", request.getDescription());
            eventData.put("startDate", request.getStartDate());
            eventData.put("endDate", request.getEndDate());
            Boolean isPublic = (request.getIsPublic() != null) ? request.getIsPublic() : true;
            eventData.put("isPublic", isPublic);
            
            // Build candidates array if provided
            if (request.getCandidates() != null && !request.getCandidates().isEmpty()) {
                List<Map<String, String>> candidates = new ArrayList<>();
                for (CreateEventRequest.CandidateRequest candidate : request.getCandidates()) {
                    Map<String, String> candidateMap = new HashMap<>();
                    candidateMap.put("name", candidate.getName());
                    if (candidate.getDescription() != null && !candidate.getDescription().isEmpty()) {
                        candidateMap.put("description", candidate.getDescription());
                    }
                    if (candidate.getPhotoUrl() != null && !candidate.getPhotoUrl().isEmpty()) {
                        candidateMap.put("photoUrl", candidate.getPhotoUrl());
                    }
                    candidates.add(candidateMap);
                }
                eventData.put("candidates", candidates);
            }
            
            String token = (String) session.getAttribute("token");
            ApiResponse<EventDTO> response = eventService.updateEvent(id, eventData, token);
            
            if (response.getSuccess() && response.getData() != null) {
                redirectAttributes.addFlashAttribute("success", "Event berhasil diupdate!");
                return "redirect:/events/" + response.getData().getCode();
            } else {
                redirectAttributes.addFlashAttribute("error", response.getMessage());
                return "redirect:/events/edit/" + id;
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Gagal mengupdate event: " + e.getMessage());
            return "redirect:/events/edit/" + id;
        }
    }
}