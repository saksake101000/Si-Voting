package com.example.sivoting.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@Slf4j
public class GoogleAuthService {
    
    @Value("${google.client-id}")
    private String googleClientId;
    
    public GoogleIdToken.Payload verifyGoogleToken(String idTokenString) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(),
                    JacksonFactory.getDefaultInstance())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();
            
            GoogleIdToken idToken = verifier.verify(idTokenString);
            
            if (idToken != null) {
                return idToken.getPayload();
            } else {
                log.error("Invalid Google ID token");
                return null;
            }
        } catch (Exception e) {
            log.error("Error verifying Google token", e);
            return null;
        }
    }
}
