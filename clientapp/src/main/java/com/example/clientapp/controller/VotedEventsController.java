package com.example.clientapp.controller;

import com.example.clientapp.dto.ApiResponse;
import com.example.clientapp.dto.EventDTO;
import com.example.clientapp.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/events")
public class VotedEventsController {

    private final UserService userService;

    @GetMapping("/voted")
    public String votedEvents(HttpSession session, Model model) {
        if (session.getAttribute("token") == null) {
            return "redirect:/login?redirect=/events/voted";
        }

        String token = (String) session.getAttribute("token");
        ApiResponse<List<EventDTO>> resp = userService.getVotedEvents(token);

        if (resp != null && resp.getSuccess() && resp.getData() != null) {
            model.addAttribute("events", resp.getData());
        } else {
            model.addAttribute("events", new java.util.ArrayList<>());
            if (resp != null) model.addAttribute("error", resp.getMessage());
        }

        model.addAttribute("username", session.getAttribute("username"));
        model.addAttribute("email", session.getAttribute("email"));

        return "dashboard/voted-events";
    }
}
