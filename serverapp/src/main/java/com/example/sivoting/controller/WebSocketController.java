package com.example.sivoting.controller;

import com.example.sivoting.dto.websocket.VoteUpdateMessage;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@Hidden // Hide from Swagger docs
public class WebSocketController {
    
    /**
     * Test endpoint for WebSocket
     * Client sends message to /app/vote
     * Server broadcasts to /topic/votes
     */
    @MessageMapping("/vote")
    @SendTo("/topic/votes")
    public VoteUpdateMessage handleVoteMessage(VoteUpdateMessage message) {
        return message;
    }
}
