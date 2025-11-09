package com.example.sivoting.dto.activity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityItem {
    // type: EVENT_CREATED, VOTED
    private String type;
    private String message;
    private LocalDateTime timestamp;

    // optional fields
    private Long eventId;
    private String eventTitle;
    private Long candidateId;
    private String candidateName;
}
