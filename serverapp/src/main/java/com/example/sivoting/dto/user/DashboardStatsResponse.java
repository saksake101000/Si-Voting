package com.example.sivoting.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardStatsResponse {
    private Integer myEvents;
    private Integer activeEvents;
    private Integer eventsWithVotes;
    private Integer participationRate; // percent 0-100
}
