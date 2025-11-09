package com.example.sivoting.controller;

import com.example.sivoting.dto.ApiResponse;
import com.example.sivoting.dto.user.UserProfileResponse;
import com.example.sivoting.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
@Tag(name = "Users", description = "Endpoints untuk manajemen user dan profile")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/profile")
    @Operation(
            summary = "Get profile user yang login",
            description = "Mengambil profile dan statistik user yang sedang login (total events created, total votes).",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<ApiResponse<UserProfileResponse>> getCurrentUserProfile() {
        try {
            UserProfileResponse profile = userService.getCurrentUserProfile();
            return ResponseEntity.ok(ApiResponse.success(profile));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/{id}/profile")
    @Operation(
            summary = "Get profile user by ID",
            description = "Mengambil profile dan statistik user berdasarkan user ID.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<ApiResponse<UserProfileResponse>> getUserProfile(@PathVariable Long id) {
        try {
            UserProfileResponse profile = userService.getUserProfile(id);
            return ResponseEntity.ok(ApiResponse.success(profile));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
