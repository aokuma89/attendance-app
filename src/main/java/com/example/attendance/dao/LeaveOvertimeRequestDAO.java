package com.example.attendance.dao;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.example.attendance.dto.LeaveOvertimeRequest;

public class LeaveOvertimeRequestDAO {
    private static final List<LeaveOvertimeRequest> requests = new CopyOnWriteArrayList<>();

    public void save(LeaveOvertimeRequest request) {
        requests.add(request);
    }

    public List<LeaveOvertimeRequest> findAll() {
        return new CopyOnWriteArrayList<>(requests);
    }

    public List<LeaveOvertimeRequest> findByUserId(String userId) {
        return requests.stream()
                .filter(r -> r.getUserId().equals(userId))
                .toList();
    }
}
