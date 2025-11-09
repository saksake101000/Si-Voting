package com.example.clientapp.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {
    
    @Value("${api.base-url}")
    private String apiBaseUrl;
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * Upload file to backend server
     * @param file MultipartFile to upload
     * @param token JWT token for authentication
     * @return File URL if successful, null otherwise
     */
    public String uploadFile(MultipartFile file, String token) {
        try {
            String url = apiBaseUrl + "/files/upload";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            
            if (token != null && !token.isEmpty()) {
                headers.set("Authorization", "Bearer " + token);
            }
            
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", file.getResource());
            
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            
            ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                String.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                // Parse JSON response to get URL
                JsonNode root = objectMapper.readTree(response.getBody());
                if (root.has("data") && root.get("data").has("url")) {
                    String fileUrl = root.get("data").get("url").asText();
                    log.info("File uploaded successfully: {}", fileUrl);
                    return fileUrl;
                }
            }
            
            log.error("File upload failed: Invalid response");
            return null;
        } catch (Exception e) {
            log.error("File upload error", e);
            return null;
        }
    }
}
