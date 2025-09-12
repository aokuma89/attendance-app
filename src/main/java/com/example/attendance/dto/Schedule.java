package com.example.attendance.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Schedule {
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean approved;  
    private boolean paidLeave;

    public Schedule(LocalDate date, LocalTime startTime, LocalTime endTime) {
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.approved = false;
        this.paidLeave = false;
    }
    
    public String getFormattedStartTime() {
        return startTime.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    public String getFormattedEndTime() {
        return endTime.format(DateTimeFormatter.ofPattern("HH:mm"));
    }


    public LocalDate getDate() { return date; }
    public LocalTime getStartTime() { return startTime; }
    public LocalTime getEndTime() { return endTime; }
    public boolean isApproved() { return approved; }
    public boolean isPaidLeave() { return paidLeave; }

    public void setApproved(boolean approved) { this.approved = approved; }
    public void setPaidLeave(boolean paidLeave) { this.paidLeave = paidLeave; }
}
