package com.example.sivoting.service;

import com.example.sivoting.dto.auth.AuthResponse;
import com.example.sivoting.dto.auth.LoginRequest;
import com.example.sivoting.dto.auth.RegisterRequest;
import com.example.sivoting.model.User;
import com.example.sivoting.repository.UserRepository;
import com.example.sivoting.security.JwtTokenProvider;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AuthService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private JwtTokenProvider tokenProvider;
    
    @Autowired
    private GoogleAuthService googleAuthService;
    
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Check if username exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username sudah digunakan!");
        }
        
        // Check if email exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email sudah terdaftar!");
        }
        
        // Create new user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        
        User savedUser = userRepository.save(user);
        
        // Generate token
        String token = tokenProvider.generateTokenFromUsername(savedUser.getUsername());
        
        return new AuthResponse(token, savedUser.getId(), savedUser.getUsername(), savedUser.getEmail());
    }
    
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        String token = tokenProvider.generateToken(authentication);
        
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan!"));
        
        return new AuthResponse(token, user.getId(), user.getUsername(), user.getEmail());
    }
    
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan!"));
    }
    
    @Transactional
    public AuthResponse googleLogin(String idTokenString) {
        GoogleIdToken.Payload payload = googleAuthService.verifyGoogleToken(idTokenString);
        
        if (payload == null) {
            throw new RuntimeException("Token Google tidak valid!");
        }
        
        String email = payload.getEmail();
        String name = (String) payload.get("name");
        
        // Check if user exists
        User user = userRepository.findByEmail(email).orElse(null);
        
        if (user == null) {
            // Create new user from Google account
            user = new User();
            user.setEmail(email);
            // Generate username from email
            String username = email.split("@")[0];
            // Make sure username is unique
            String finalUsername = username;
            int counter = 1;
            while (userRepository.existsByUsername(finalUsername)) {
                finalUsername = username + counter;
                counter++;
            }
            user.setUsername(finalUsername);
            // Set random password (user won't use it for Google login)
            user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
            
            user = userRepository.save(user);
        }
        
        // Generate token
        String token = tokenProvider.generateTokenFromUsername(user.getUsername());
        
        return new AuthResponse(token, user.getId(), user.getUsername(), user.getEmail());
    }
}
