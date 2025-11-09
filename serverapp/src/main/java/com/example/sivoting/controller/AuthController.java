package com.example.sivoting.controller;

import com.example.sivoting.dto.ApiResponse;
import com.example.sivoting.dto.GoogleLoginRequest;
import com.example.sivoting.dto.auth.AuthResponse;
import com.example.sivoting.dto.auth.LoginRequest;
import com.example.sivoting.dto.auth.RegisterRequest;
import com.example.sivoting.service.AuthService;
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

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
@Tag(name = "Authentication", description = "Endpoints untuk autentikasi user (register, login, OAuth)")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @PostMapping("/register")
    @Operation(
            summary = "Register user baru",
            description = "Membuat akun user baru dengan username, email, dan password. Username dan email harus unique."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Registrasi berhasil",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Username atau email sudah digunakan"
            )
    })
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        try {
            AuthResponse response = authService.register(request);
            return ResponseEntity.ok(ApiResponse.success("Registrasi berhasil!", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PostMapping("/login")
    @Operation(
            summary = "Login user",
            description = "Autentikasi user dengan username dan password. Mengembalikan JWT token yang valid 24 jam."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Login berhasil",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Username atau password salah"
            )
    })
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        try {
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(ApiResponse.success("Login berhasil!", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Username atau password salah!"));
        }
    }
    
    @GetMapping("/me")
    @Operation(
            summary = "Get current user info",
            description = "Mengambil informasi user yang sedang login berdasarkan JWT token.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "User found",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Token invalid atau expired"
            )
    })
    public ResponseEntity<ApiResponse<Object>> getCurrentUser() {
        try {
            var user = authService.getCurrentUser();
            return ResponseEntity.ok(ApiResponse.success(user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PostMapping("/google")
    @Operation(
            summary = "Login dengan Google OAuth",
            description = "Autentikasi user menggunakan Google credential token. Jika user belum terdaftar, akan otomatis dibuat akun baru."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Login dengan Google berhasil",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Google token invalid atau gagal verify"
            )
    })
    public ResponseEntity<ApiResponse<AuthResponse>> googleLogin(@RequestBody GoogleLoginRequest request) {
        try {
            AuthResponse response = authService.googleLogin(request.getCredential());
            return ResponseEntity.ok(ApiResponse.success("Login dengan Google berhasil!", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Login dengan Google gagal: " + e.getMessage()));
        }
    }
}
