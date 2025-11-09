package com.example.sivoting.controller;

import com.example.sivoting.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/files")
@CrossOrigin(origins = "*")
@Tag(name = "Files", description = "Endpoints untuk upload file (gambar kandidat)")
public class FileController {
    
    @Value("${file.upload-dir:uploads}")
    private String uploadDir;
    
    @Value("${server.base-url:http://localhost:8080}")
    private String serverBaseUrl;
    
    @PostMapping("/upload")
    @Operation(
            summary = "Upload file",
            description = "Upload file (gambar) untuk foto kandidat. Maximum size 5MB. File disimpan dengan UUID sebagai nama file."
    )
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // Create upload directory if not exists
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            
            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String fileName = UUID.randomUUID().toString() + fileExtension;
            
            // Save file
            Path filePath = Paths.get(uploadDir, fileName);
            Files.write(filePath, file.getBytes());
            
            // Build response
            Map<String, String> fileInfo = new HashMap<>();
            fileInfo.put("originalFilename", originalFilename);
            fileInfo.put("filename", fileName);
            fileInfo.put("url", serverBaseUrl + "/uploads/" + fileName);
            fileInfo.put("contentType", file.getContentType());
            fileInfo.put("size", String.valueOf(file.getSize()));
            
            return ResponseEntity.ok(ApiResponse.success("File berhasil diupload!", fileInfo));
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Gagal mengupload file: " + e.getMessage()));
        }
    }
}
