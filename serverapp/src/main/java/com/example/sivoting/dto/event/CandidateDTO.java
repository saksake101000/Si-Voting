package com.example.sivoting.dto.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CandidateDTO {
    
    private Long id;
    
    @NotBlank(message = "Nama kandidat tidak boleh kosong")
    @Size(max = 100, message = "Nama kandidat maksimal 100 karakter")
    private String name;
    
    private String description;
    
    private String photoUrl;
    
    private Long voteCount;
}
