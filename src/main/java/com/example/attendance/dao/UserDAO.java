package com.example.attendance.dao;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.example.attendance.dto.LeaveOvertimeRequest;
import com.example.attendance.dto.Schedule;
import com.example.attendance.dto.User;

public class UserDAO {

    private static final Map<String, User> users = new HashMap<>();

    static {
        users.put("admin1", new User("admin1", hashPassword("adminpass"), "admin", true, LocalDate.of(2022, 1, 1)));
        users.put("employee1", new User("employee1", hashPassword("password"), "fulltime", true, LocalDate.of(2023, 4, 1)));
        users.put("parttimer1", new User("parttimer1", hashPassword("password"), "parttime", true, LocalDate.of(2023, 6, 15)));
    }

    public User findByUsername(String username) {
        return users.get(username);
    }

    public boolean verifyPassword(String username, String password) {
        User user = findByUsername(username);
        return user != null && user.isEnabled() && user.getPassword().equals(hashPassword(password));
    }

    public Collection<User> getAllUsers() {
        return users.values();
    }

    public void addUser(User user) {
        users.put(user.getUsername(), user);
    }

    public void updateUser(User user) {
        User existing = users.get(user.getUsername());
        if (existing != null) {
            existing.setEnabled(user.isEnabled());
            existing.setRole(user.getRole());
            existing.setStartDate(user.getStartDate());
        }
    }

    public void deleteUser(String username) {
        users.remove(username);
    }

    public void resetPassword(String username, String newPassword) {
        User user = users.get(username);
        if (user != null) {
            try {
                java.lang.reflect.Field passwordField = User.class.getDeclaredField("password");
                passwordField.setAccessible(true);
                passwordField.set(user, hashPassword(newPassword));
            } catch (Exception e) {
                throw new RuntimeException("パスワード更新失敗", e);
            }
        }
    }

    public void toggleUserEnabled(String username, boolean enabled) {
        User user = users.get(username);
        if (user != null) {
            user.setEnabled(enabled);
        }
    }

    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public void submitSchedule(String username, Schedule schedule) {
        User user = users.get(username);
        if (user != null) {
            user.addSchedule(schedule);

            while (user.getSchedules().size() > 10) {
                user.getSchedules().remove(0);
            }
        }
    }


    public void approveSchedule(String username, LocalDate date) {
        User user = users.get(username);
        if (user != null) {
            for (Schedule s : user.getSchedules()) {
                if (s.getDate().equals(date)) {
                    s.setApproved(true);
                    break;
                }
            }
        }
    }

    public void setPaidLeave(String username, LocalDate date) {
        User user = users.get(username);
        if (user != null) {
            for (Schedule s : user.getSchedules()) {
                if (s.getDate().equals(date) && !s.isPaidLeave() && user.getPaidLeaveRemaining() > 0) {
                    s.setPaidLeave(true);
                    user.usePaidLeave();
                    break;
                }
            }
        }
    }

    public int getPaidLeaveRemaining(String username) {
        User user = users.get(username);
        return user != null ? user.getPaidLeaveRemaining() : 0;
    }
    
    public void submitLeaveRequest(String username, LocalDate date) {
        User user = users.get(username);
        if (user != null && "fulltime".equals(user.getRole())) {
            LeaveOvertimeRequest req = new LeaveOvertimeRequest(date);
            req.setPaidLeaveRequested(true);
            user.addRequest(req);
        }
    }

    public void submitOvertimeRequest(String username, LocalDate date, double hours) {
        User user = users.get(username);
        if (user != null && "fulltime".equals(user.getRole())) {
            LeaveOvertimeRequest req = new LeaveOvertimeRequest(date);
            req.requestOvertime(hours); // 残業時間をセット
            user.addRequest(req);
        }
    }

    public void approveLeave(String username, LocalDate date) {
        User user = users.get(username);
        if (user != null) {
            for (LeaveOvertimeRequest r : user.getRequests()) {
                if (r.getDate().equals(date) && r.isPaidLeaveRequested() && !r.isPaidLeaveApproved()
                        && user.getPaidLeaveRemaining() > 0) {
                    r.setPaidLeaveApproved(true);
                    user.usePaidLeave();
                    break;
                }
            }
        }
    }

    public void approveOvertime(String username, LocalDate date) {
        User user = users.get(username);
        if (user != null) {
            for (LeaveOvertimeRequest r : user.getRequests()) {
                if (r.getDate().equals(date) && r.isOvertimeRequested() && !r.isOvertimeApproved()) {
                    r.setOvertimeApproved(true);
                    break;
                }
            }
        }
    }
    
    public Collection<LeaveOvertimeRequest> getAllLeaveRequests() {
        return users.values().stream()
                .flatMap(u -> u.getRequests().stream()
                        .peek(r -> r.setUserId(u.getUsername())) // 表示用にUserID付与
                        .filter(LeaveOvertimeRequest::isPaidLeaveRequested))
                .collect(Collectors.toList());
    }

    public Collection<LeaveOvertimeRequest> getAllOvertimeRequests() {
        return users.values().stream()
                .flatMap(u -> u.getRequests().stream()
                        .peek(r -> r.setUserId(u.getUsername())) // 表示用にUserID付与
                        .filter(LeaveOvertimeRequest::isOvertimeRequested))
                .collect(Collectors.toList());
    }

}
