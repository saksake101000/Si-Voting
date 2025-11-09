package com.example.clientapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventDTO {
    private Long id;
    private String code;
    private String title;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean isPublic;
    private Boolean isActive;
    private Long createdBy;
    private String createdByName;
    private List<CandidateDTO> candidates;
    private Long totalVotes;
    private Boolean hasVoted;
    private Long userVoteCandidateId;
    
    // Helper methods for formatted dates
    public String getStartDateFormatted() {
        if (startDate == null) return "-";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");
        return startDate.format(formatter);
    }
    
    public String getEndDateFormatted() {
        if (endDate == null) return "-";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");
        return endDate.format(formatter);
    }
    
    public String getStartDateShort() {
        if (startDate == null) return "-";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
        return startDate.format(formatter);
    }
    
    public String getEndDateShort() {
        if (endDate == null) return "-";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
        return endDate.format(formatter);
    }
    
    public String getTimelineStatus() {
        if (startDate == null || endDate == null) return "Status Tidak Diketahui";
        
        LocalDateTime now = LocalDateTime.now();
        
        if (now.isBefore(startDate)) {
            return "Akan Datang";
        } else if (now.isAfter(endDate)) {
            return "Selesai";
        } else if (Boolean.TRUE.equals(isActive)) {
            return "Sedang Berlangsung";
        } else {
            return "Tidak Aktif";
        }
    }
    
    public String getTimelineBadgeClass() {
        String status = getTimelineStatus();
        switch (status) {
            case "Sedang Berlangsung":
                return "bg-green-100 text-green-700";
            case "Akan Datang":
                return "bg-blue-100 text-blue-700";
            case "Selesai":
                return "bg-gray-100 text-gray-700";
            case "Tidak Aktif":
                return "bg-orange-100 text-orange-700";
            default:
                return "bg-slate-100 text-slate-700";
        }
    }
    
    public String getStartDateInput() {
        if (startDate == null) return "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        return startDate.format(formatter);
    }
    
    public String getEndDateInput() {
        if (endDate == null) return "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        return endDate.format(formatter);
    }
}
