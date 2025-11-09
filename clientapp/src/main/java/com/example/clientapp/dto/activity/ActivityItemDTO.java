package com.example.clientapp.dto.activity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityItemDTO {
    private String type;
    private String message;
    private LocalDateTime timestamp;
    private Long eventId;
    private String eventTitle;
    private Long candidateId;
    private String candidateName;
}
