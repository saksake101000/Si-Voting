package com.example.sivoting.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "events", indexes = {
    @Index(name = "idx_code", columnList = "code"),
    @Index(name = "idx_is_active", columnList = "isActive"),
    @Index(name = "idx_dates", columnList = "startDate,endDate"),
    @Index(name = "idx_created_by", columnList = "createdBy")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Kode event tidak boleh kosong")
    @Size(min = 6, max = 12, message = "Kode event harus antara 6-12 karakter")
    @Column(nullable = false, unique = true, length = 12)
    private String code;
    
    @NotBlank(message = "Judul event tidak boleh kosong")
    @Size(max = 200, message = "Judul maksimal 200 karakter")
    @Column(nullable = false, length = 200)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @NotNull(message = "Tanggal mulai tidak boleh kosong")
    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;
    
    @NotNull(message = "Tanggal akhir tidak boleh kosong")
    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;
    
    @Column(name = "is_public", nullable = false)
    private Boolean isPublic = true;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @NotNull(message = "Pembuat event tidak boleh kosong")
    @Column(name = "created_by", nullable = false)
    private Long createdBy;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Candidate> candidates = new ArrayList<>();
    
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Vote> votes = new ArrayList<>();
}
