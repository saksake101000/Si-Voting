package com.example.sivoting.controller;

import com.example.sivoting.dto.ApiResponse;
import com.example.sivoting.dto.vote.VoteRequest;
import com.example.sivoting.model.Vote;
import com.example.sivoting.service.VoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/votes")
@CrossOrigin(origins = "*")
@Tag(name = "Votes", description = "Endpoints untuk voting")
public class VoteController {
    
    @Autowired
    private VoteService voteService;
    
    @PostMapping
    @Operation(
            summary = "Submit vote",
            description = """
                    Submit vote untuk kandidat dalam suatu event. Validasi:
                    - Event harus aktif
                    - Waktu voting harus dalam periode startDate - endDate
                    - User hanya bisa vote 1x per event
                    - Kandidat harus terdaftar dalam event
                    """,
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<ApiResponse<Vote>> submitVote(@Valid @RequestBody VoteRequest request) {
        try {
            Vote vote = voteService.submitVote(request);
            return ResponseEntity.ok(ApiResponse.success("Vote berhasil!", vote));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
