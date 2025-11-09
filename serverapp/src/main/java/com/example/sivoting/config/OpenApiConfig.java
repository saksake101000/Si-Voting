package com.example.sivoting.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "SI-VOTING API",
                version = "1.0.0",
                description = """
                        # SI-VOTING API Documentation
                        
                        Sistem Informasi Voting/Pemilihan berbasis web yang memungkinkan user untuk:
                        - Membuat event voting dengan multiple candidates
                        - Vote untuk kandidat pilihan
                        - Melihat hasil voting secara real-time
                        - Mengelola event (create, update, delete)
                        - Autentikasi dengan JWT atau Google OAuth
                        
                        ## Authentication
                        Sebagian besar endpoint memerlukan autentikasi JWT. Untuk menggunakan:
                        1. Register atau Login untuk mendapatkan token
                        2. Klik tombol "Authorize" di atas
                        3. Masukkan token dengan format: `Bearer {token}`
                        4. Klik "Authorize" dan "Close"
                        
                        ## Base URL
                        - Development: `http://localhost:8080/api`
                        
                        ## Features
                        - ✅ User Authentication (JWT + Google OAuth)
                        - ✅ Event Management (CRUD)
                        - ✅ Voting System dengan validation
                        - ✅ Event Statistics
                        - ✅ File Upload untuk foto kandidat
                        - ✅ Real-time vote counting
                        """,
                contact = @Contact(
                        name = "SI-VOTING Team",
                        email = "support@sivoting.com",
                        url = "https://github.com/yourusername/sivoting"
                ),
                license = @License(
                        name = "MIT License",
                        url = "https://opensource.org/licenses/MIT"
                )
        ),
        servers = {
                @Server(
                        url = "http://localhost:8080",
                        description = "Local Development Server"
                ),
                @Server(
                        url = "https://api.sivoting.com",
                        description = "Production Server"
                )
        },
        security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
        name = "bearerAuth",
        description = "JWT Authentication. Format: `Bearer {token}`",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}
