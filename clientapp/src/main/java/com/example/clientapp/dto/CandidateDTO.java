package com.example.clientapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CandidateDTO {
    private Long id;
    private String name;
    private String description;
    private String photoUrl;
    private Long voteCount;
}
