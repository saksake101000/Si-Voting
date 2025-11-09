package com.example.clientapp.controller;

import com.example.clientapp.dto.ApiResponse;
import com.example.clientapp.service.VoteService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/votes")
@RequiredArgsConstructor
public class VoteController {
    
    private final VoteService voteService;
    
    @PostMapping
    public String submitVote(
            @RequestParam Long candidateId,
            @RequestParam Long eventId,
            @RequestParam String eventCode,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        
        if (session.getAttribute("token") == null) {
            return "redirect:/login?redirect=/events/" + eventCode;
        }
        
    String token = (String) session.getAttribute("token");
    ApiResponse<Object> response = voteService.submitVote(candidateId, eventId, token);
        
        if (response.getSuccess()) {
            redirectAttributes.addFlashAttribute("success", "Vote berhasil! Terima kasih atas partisipasinya.");
        } else {
            redirectAttributes.addFlashAttribute("error", response.getMessage());
        }
        
        return "redirect:/events/" + eventCode;
    }
}
