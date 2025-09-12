package com.example.attendance.dto;

import java.time.LocalDate;

public class LeaveOvertimeRequest {
    private LocalDate date;
    private String userId; // ★追加: 申請者ID

    // 有給関連
    private boolean paidLeaveRequested = false;
    private boolean paidLeaveApproved = false;

    // 残業関連
    private boolean overtimeRequested = false;
    private boolean overtimeApproved = false;
    private double overtimeHours; // 残業時間（時間単位）

    public LeaveOvertimeRequest(LocalDate date) {
        this.date = date;
    }

    // --- getter/setter ---
    public LocalDate getDate() { return date; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public boolean isPaidLeaveRequested() { return paidLeaveRequested; }
    public boolean isPaidLeaveApproved() { return paidLeaveApproved; }
    public boolean isOvertimeRequested() { return overtimeRequested; }
    public boolean isOvertimeApproved() { return overtimeApproved; }
    public double getOvertimeHours() { return overtimeHours; }

    public void setPaidLeaveRequested(boolean requested) { this.paidLeaveRequested = requested; }
    public void setPaidLeaveApproved(boolean approved) { this.paidLeaveApproved = approved; }
    public void setOvertimeRequested(boolean requested) { this.overtimeRequested = requested; }
    public void setOvertimeApproved(boolean approved) { this.overtimeApproved = approved; }
    public void setOvertimeHours(double hours) { this.overtimeHours = hours; }

    // --- メソッド ---
    public void requestPaidLeave() { this.paidLeaveRequested = true; }
    public void approvePaidLeave() { this.paidLeaveApproved = true; }
    public void requestOvertime(double hours) {
        this.overtimeRequested = true;
        this.overtimeHours = hours;
    }
    public void approveOvertime() { this.overtimeApproved = true; }
}
