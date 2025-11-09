package com.example.sivoting.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "votes", 
    indexes = {
        @Index(name = "idx_user_id", columnList = "userId"),
        @Index(name = "idx_candidate_id", columnList = "candidateId"),
        @Index(name = "idx_event_id", columnList = "eventId")
    },
    uniqueConstraints = {
        @UniqueConstraint(name = "unique_user_event", columnNames = {"userId", "eventId"})
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vote {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "User ID tidak boleh kosong")
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @NotNull(message = "Candidate ID tidak boleh kosong")
    @Column(name = "candidate_id", nullable = false)
    private Long candidateId;
    
    @NotNull(message = "Event ID tidak boleh kosong")
    @Column(name = "event_id", nullable = false)
    private Long eventId;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id", insertable = false, updatable = false)
    private Candidate candidate;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", insertable = false, updatable = false)
    private Event event;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
