package com.example.sivoting.dto.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventRequest {
    
    @NotBlank(message = "Judul event tidak boleh kosong")
    @Size(max = 200, message = "Judul maksimal 200 karakter")
    private String title;
    
    private String description;
    
    @NotNull(message = "Tanggal mulai tidak boleh kosong")
    private LocalDateTime startDate;
    
    @NotNull(message = "Tanggal akhir tidak boleh kosong")
    private LocalDateTime endDate;
    
    private Boolean isPublic = true;
    
    private Boolean isActive = true;
    
    private List<CandidateDTO> candidates;
}
