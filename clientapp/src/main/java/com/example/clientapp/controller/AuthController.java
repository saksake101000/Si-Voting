package com.example.clientapp.controller;

import com.example.clientapp.dto.ApiResponse;
import com.example.clientapp.dto.AuthResponse;
import com.example.clientapp.service.AuthService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    
    @Value("${google.client-id}")
    private String googleClientId;
    
    @GetMapping("/")
    public String index(Model model, HttpSession session) {
        // Load public events for homepage
        model.addAttribute("hasPublicEvents", false);
        return "index";
    }
    
    @GetMapping("/login")
    public String loginPage(HttpSession session, Model model) {
        // Redirect to dashboard if already logged in
        if (session.getAttribute("token") != null) {
            return "redirect:/dashboard";
        }
        model.addAttribute("googleClientId", googleClientId);
        return "login";
    }
    
    @PostMapping("/login")
    public String login(
            @RequestParam String username,
            @RequestParam String password,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        
        ApiResponse<AuthResponse> response = authService.login(username, password);
        
        if (response.getSuccess() && response.getData() != null) {
            // Save to session
            session.setAttribute("token", response.getData().getToken());
            session.setAttribute("userId", response.getData().getUserId());
            session.setAttribute("username", response.getData().getUsername());
            session.setAttribute("email", response.getData().getEmail());
            
            return "redirect:/dashboard";
        } else {
            redirectAttributes.addFlashAttribute("error", response.getMessage());
            return "redirect:/login";
        }
    }
    
    @GetMapping("/register")
    public String registerPage(HttpSession session, Model model) {
        // Redirect to dashboard if already logged in
        if (session.getAttribute("token") != null) {
            return "redirect:/dashboard";
        }
        model.addAttribute("googleClientId", googleClientId);
        return "register";
    }
    
    @PostMapping("/register")
    public String register(
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        
        ApiResponse<AuthResponse> response = authService.register(username, email, password);
        
        if (response.getSuccess() && response.getData() != null) {
            // Save to session
            session.setAttribute("token", response.getData().getToken());
            session.setAttribute("userId", response.getData().getUserId());
            session.setAttribute("username", response.getData().getUsername());
            session.setAttribute("email", response.getData().getEmail());
            
            return "redirect:/dashboard";
        } else {
            redirectAttributes.addFlashAttribute("error", response.getMessage());
            return "redirect:/register";
        }
    }
    
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
    
    @PostMapping("/login/google")
    @ResponseBody
    public Map<String, Object> googleLogin(
            @RequestBody Map<String, String> request,
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            String credential = request.get("credential");
            
            ApiResponse<AuthResponse> apiResponse = authService.googleLogin(credential);
            
            if (apiResponse.getSuccess() && apiResponse.getData() != null) {
                AuthResponse authData = apiResponse.getData();
                
                // Save to session
                session.setAttribute("token", authData.getToken());
                session.setAttribute("userId", authData.getUserId());
                session.setAttribute("username", authData.getUsername());
                session.setAttribute("email", authData.getEmail());
                
                response.put("success", true);
                response.put("message", "Login dengan Google berhasil");
                response.put("redirectUrl", "/dashboard");
            } else {
                response.put("success", false);
                response.put("message", apiResponse.getMessage());
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Gagal login: " + e.getMessage());
        }
        
        return response;
    }
}
