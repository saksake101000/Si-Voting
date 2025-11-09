package com.example.sivoting.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "candidates", indexes = {
    @Index(name = "idx_event_id", columnList = "eventId")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Candidate {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Nama kandidat tidak boleh kosong")
    @Size(max = 100, message = "Nama kandidat maksimal 100 karakter")
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "photo_url")
    private String photoUrl;
    
    @NotNull(message = "Event ID tidak boleh kosong")
    @Column(name = "event_id", nullable = false)
    private Long eventId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", insertable = false, updatable = false)
    private Event event;
    
    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Vote> votes = new ArrayList<>();
}
