package com.example.attendance.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Attendance {
    private String userId; 
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;

    
	// --- Getter Setter ---
    public Attendance(String userId) { this.userId = userId; }
 
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public LocalDateTime getCheckInTime() { return checkInTime; }
    public void setCheckInTime(LocalDateTime checkInTime) { this.checkInTime = checkInTime; }

    public LocalDateTime getCheckOutTime() { return checkOutTime; }
    public void setCheckOutTime(LocalDateTime checkOutTime) { this.checkOutTime = checkOutTime; }

    public String getFormattedCheckInTime() {
        return checkInTime != null ? checkInTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "";
    }
    public String getFormattedCheckOutTime() {
        return checkOutTime != null ? checkOutTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "";
    }
    
    public String getIsoCheckInTime() {
        return checkInTime != null ? checkInTime.toString() : "";
    }

    public String getIsoCheckOutTime() {
        return checkOutTime != null ? checkOutTime.toString() : "";
    }
}
