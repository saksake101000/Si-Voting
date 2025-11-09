package com.example.clientapp.controller;

import com.example.clientapp.service.FileService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {
    
    private final FileService fileService;
    
    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadFile(@RequestParam("file") MultipartFile file, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String token = (String) session.getAttribute("token");
            String fileUrl = fileService.uploadFile(file, token);
            
            if (fileUrl != null) {
                Map<String, String> data = new HashMap<>();
                data.put("url", fileUrl);
                
                response.put("success", true);
                response.put("message", "File uploaded successfully");
                response.put("data", data);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Failed to upload file");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error uploading file: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
