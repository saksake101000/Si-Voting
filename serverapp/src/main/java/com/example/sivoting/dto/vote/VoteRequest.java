package com.example.sivoting.dto.vote;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoteRequest {
    
    @NotNull(message = "Candidate ID tidak boleh kosong")
    private Long candidateId;
    
    @NotNull(message = "Event ID tidak boleh kosong")
    private Long eventId;
}
