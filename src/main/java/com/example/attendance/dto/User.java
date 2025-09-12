package com.example.attendance.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class User {

    private String username;
    private String password;
    private String role; 
    private boolean enabled;
    private LocalDate startDate;
    private String userId; // 新規追加


    private int paidLeaveRemaining;

    private List<Schedule> schedules = new ArrayList<>();

    public User(String username, String password, String role, boolean enabled, LocalDate startDate) {
        this.username = username;
        this.password = password;
        this.role = role.toLowerCase();
        this.enabled = enabled;
        this.startDate = startDate;

        if ("fulltime".equals(this.role)) {
            this.paidLeaveRemaining = 10;
        } else if ("parttime".equals(this.role)) {
            this.paidLeaveRemaining = 5;
        } else { 
            this.paidLeaveRemaining = 0;
        }
    }
    
    private List<LeaveOvertimeRequest> requests = new ArrayList<>();

    public List<LeaveOvertimeRequest> getRequests() { return requests; }
    public void addRequest(LeaveOvertimeRequest req) { requests.add(req); }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }


    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public boolean isEnabled() { return enabled; }
    public LocalDate getStartDate() { return startDate; }

    public void setPassword(String password) { this.password = password; }
    public void setRole(String role) { this.role = role.toLowerCase(); }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    // --- 有給・スケジュール関連 ---
    public List<Schedule> getSchedules() { return schedules; }
    public void addSchedule(Schedule s) { schedules.add(s); }
    public void removeSchedule(Schedule s) { schedules.remove(s); }

    public int getPaidLeaveRemaining() { return paidLeaveRemaining; }
    public void usePaidLeave() { 
        if (paidLeaveRemaining > 0) paidLeaveRemaining--; 
    }
}
