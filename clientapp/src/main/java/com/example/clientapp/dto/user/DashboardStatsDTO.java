package com.example.clientapp.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardStatsDTO {
    private Integer myEvents;
    private Integer activeEvents;
    private Integer eventsWithVotes;
    private Integer participationRate;
}
