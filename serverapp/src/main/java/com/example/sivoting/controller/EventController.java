package com.example.sivoting.controller;

import com.example.sivoting.dto.ApiResponse;
import com.example.sivoting.dto.event.EventRequest;
import com.example.sivoting.dto.event.EventResponse;
import com.example.sivoting.dto.event.EventStatisticsResponse;
import com.example.sivoting.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = "*")
@Tag(name = "Events", description = "Endpoints untuk manajemen event voting (CRUD, statistics)")
public class EventController {
    
    @Autowired
    private EventService eventService;
    
    @PostMapping
    @Operation(
            summary = "Create event baru",
            description = "Membuat event voting baru dengan kandidat-kandidat. Memerlukan autentikasi.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<ApiResponse<EventResponse>> createEvent(@Valid @RequestBody EventRequest request) {
        try {
            EventResponse response = eventService.createEvent(request);
            return ResponseEntity.ok(ApiResponse.success("Event berhasil dibuat!", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/public")
    @Operation(
            summary = "Get semua event public",
            description = "Mengambil daftar semua event yang bersifat public. Tidak memerlukan autentikasi."
    )
    public ResponseEntity<ApiResponse<List<EventResponse>>> getPublicEvents() {
        try {
            List<EventResponse> events = eventService.getPublicEvents();
            return ResponseEntity.ok(ApiResponse.success(events));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/my")
    @Operation(
            summary = "Get event milik user",
            description = "Mengambil daftar event yang dibuat oleh user yang sedang login.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<ApiResponse<List<EventResponse>>> getMyEvents() {
        try {
            List<EventResponse> events = eventService.getMyEvents();
            return ResponseEntity.ok(ApiResponse.success(events));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/code/{code}")
    public ResponseEntity<ApiResponse<EventResponse>> getEventByCode(@PathVariable String code) {
        try {
            EventResponse event = eventService.getEventByCode(code);
            return ResponseEntity.ok(ApiResponse.success(event));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EventResponse>> getEventById(@PathVariable Long id) {
        try {
            EventResponse event = eventService.getEventById(id);
            return ResponseEntity.ok(ApiResponse.success(event));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EventResponse>> updateEvent(
            @PathVariable Long id,
            @Valid @RequestBody EventRequest request) {
        try {
            EventResponse response = eventService.updateEvent(id, request);
            return ResponseEntity.ok(ApiResponse.success("Event berhasil diupdate!", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteEvent(@PathVariable Long id) {
        try {
            eventService.deleteEvent(id);
            return ResponseEntity.ok(ApiResponse.success("Event berhasil dihapus!", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/{id}/statistics")
    @Operation(
            summary = "Get statistik event",
            description = "Mengambil statistik lengkap event termasuk vote count per kandidat dan persentase. Hanya bisa diakses oleh pembuat event.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<ApiResponse<EventStatisticsResponse>> getEventStatistics(@PathVariable Long id) {
        try {
            EventStatisticsResponse statistics = eventService.getEventStatistics(id);
            return ResponseEntity.ok(ApiResponse.success(statistics));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PostMapping("/{id}/toggle-status")
    public ResponseEntity<ApiResponse<EventResponse>> toggleEventStatus(@PathVariable Long id) {
        try {
            EventResponse response = eventService.toggleEventStatus(id);
            return ResponseEntity.ok(ApiResponse.success("Status event berhasil diubah!", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/{id}/voting-allowed")
    public ResponseEntity<ApiResponse<Boolean>> isVotingAllowed(@PathVariable Long id) {
        try {
            boolean allowed = eventService.isVotingAllowed(id);
            return ResponseEntity.ok(ApiResponse.success(allowed));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
