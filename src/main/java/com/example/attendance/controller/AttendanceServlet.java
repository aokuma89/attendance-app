package com.example.attendance.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.example.attendance.dao.AttendanceDAO;
import com.example.attendance.dao.UserDAO;
import com.example.attendance.dto.Attendance;
import com.example.attendance.dto.User;

@WebServlet("/attendance")
public class AttendanceServlet extends HttpServlet {
    private final AttendanceDAO attendanceDAO = new AttendanceDAO();
    private final UserDAO userDAO = new UserDAO(); // ★ UserDAO を追加

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User user = (User) session.getAttribute("user");

        if (user == null) {
            resp.sendRedirect("/login.jsp");
            return;
        }

        String message = (String) session.getAttribute("successMessage");
        if (message != null) {
            req.setAttribute("successMessage", message);
            session.removeAttribute("successMessage");
        }

        String action = req.getParameter("action");
        
        if ("export_csv".equals(action) && "admin".equals(user.getRole())) {
            List<Attendance> records = getFilteredRecords(req);

            resp.setContentType("text/csv; charset=UTF-8");
            resp.setHeader("Content-Disposition", "attachment; filename=attendance.csv");

            try (var writer = resp.getWriter()) {
                writer.println("ユーザーID,出勤時刻,退勤時刻,勤務時間(時間)");

                for (Attendance att : records) {
                    String userId = att.getUserId();
                    String checkIn = att.getCheckInTime() != null ? att.getCheckInTime().toString() : "";
                    String checkOut = att.getCheckOutTime() != null ? att.getCheckOutTime().toString() : "";
                    long hours = (att.getCheckInTime() != null && att.getCheckOutTime() != null)
                            ? ChronoUnit.HOURS.between(att.getCheckInTime(), att.getCheckOutTime())
                            : 0;

                    writer.printf("%s,%s,%s,%d%n", userId, checkIn, checkOut, hours);
                }
            }
            return;
        }

        if ("admin".equals(user.getRole())) {
            List<Attendance> allRecords;
            if ("filter".equals(action)) {
                allRecords = getFilteredRecords(req);
            } else {
                allRecords = attendanceDAO.findAll();
            }

            req.setAttribute("allAttendanceRecords", allRecords);
            
            boolean hasLeaveRequest = false;
            boolean hasOvertimeRequest = false;

            Collection<User> allUsers = userDAO.getAllUsers(); 

            for (User u : allUsers) {
                if (u.getRequests() != null) {
                    if (u.getRequests().stream().anyMatch(r -> r.isPaidLeaveRequested())) {
                        hasLeaveRequest = true;
                    }
                    if (u.getRequests().stream().anyMatch(r -> r.isOvertimeRequested())) {
                        hasOvertimeRequest = true;
                    }
                }
            }

            req.setAttribute("users", allUsers);
            req.setAttribute("hasLeaveRequest", hasLeaveRequest);
            req.setAttribute("hasOvertimeRequest", hasOvertimeRequest);

            Map<String, Long> totalHoursByUser = allRecords.stream()
                    .collect(Collectors.groupingBy(
                            Attendance::getUserId,
                            Collectors.summingLong(att -> {
                                if (att.getCheckInTime() != null && att.getCheckOutTime() != null) {
                                    return ChronoUnit.HOURS.between(att.getCheckInTime(), att.getCheckOutTime());
                                }
                                return 0L;
                            })
                    ));
            req.setAttribute("totalHoursByUser", totalHoursByUser);
            req.setAttribute("monthlyWorkingHours", attendanceDAO.getMonthlyWorkingHours(null));
            req.setAttribute("monthlyCheckInCounts", attendanceDAO.getMonthlyCheckInCounts(null));

            RequestDispatcher rd = req.getRequestDispatcher("/jsp/admin_menu.jsp");
            rd.forward(req, resp);
        } else {
            List<Attendance> attendanceRecords = attendanceDAO.findByUserId(user.getUsername());
            req.setAttribute("attendanceRecords", attendanceRecords);
            RequestDispatcher rd = req.getRequestDispatcher("/jsp/employee_menu.jsp");
            rd.forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User user = (User) session.getAttribute("user");

        if (user == null) {
            resp.sendRedirect("/login.jsp");
            return;
        }

        String action = req.getParameter("action");

        try {
            switch (action) {
                case "check_in":
                    attendanceDAO.checkIn(user.getUsername());
                    session.setAttribute("successMessage", "出勤を記録しました。");
                    break;
                case "check_out":
                    attendanceDAO.checkOut(user.getUsername());
                    session.setAttribute("successMessage", "退勤を記録しました。");
                    break;
                case "add_manual":
                    handleAddManual(req, session);
                    break;
                case "update_manual":
                    handleUpdateManual(req, session);
                    break;
                case "delete_manual":
                    handleDeleteManual(req, session);
                    break;
                default:
                    session.setAttribute("errorMessage", "不明なアクションです。");
            }
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("errorMessage", "処理中にエラーが発生しました: " + e.getMessage());
        }

        if ("admin".equals(user.getRole())) {
            resp.sendRedirect("attendance?action=filter&filterUserId="
                    + (req.getParameter("filterUserId") != null ? req.getParameter("filterUserId") : "")
                    + "&startDate=" + (req.getParameter("startDate") != null ? req.getParameter("startDate") : "")
                    + "&endDate=" + (req.getParameter("endDate") != null ? req.getParameter("endDate") : ""));
        } else {
            resp.sendRedirect("attendance");
        }
    }

    private List<Attendance> getFilteredRecords(HttpServletRequest req) {
        String filterUserId = req.getParameter("filterUserId");
        String startDateStr = req.getParameter("startDate");
        String endDateStr = req.getParameter("endDate");

        LocalDate startDate = null;
        LocalDate endDate = null;
        try {
            if (startDateStr != null && !startDateStr.isEmpty()) startDate = LocalDate.parse(startDateStr);
            if (endDateStr != null && !endDateStr.isEmpty()) endDate = LocalDate.parse(endDateStr);
        } catch (DateTimeParseException e) {
            req.setAttribute("errorMessage", "日付の形式が不正です。");
        }

        return attendanceDAO.findFilteredRecords(filterUserId, startDate, endDate);
    }

    private void handleAddManual(HttpServletRequest req, HttpSession session) {
        try {
            String userId = req.getParameter("userId");
            LocalDateTime checkIn = LocalDateTime.parse(req.getParameter("checkInTime"));
            LocalDateTime checkOut = req.getParameter("checkOutTime") != null && !req.getParameter("checkOutTime").isEmpty()
                    ? LocalDateTime.parse(req.getParameter("checkOutTime"))
                    : null;
            attendanceDAO.addManualAttendance(userId, checkIn, checkOut);
            session.setAttribute("successMessage", "勤怠記録を手動で追加しました。");
        } catch (DateTimeParseException e) {
            session.setAttribute("errorMessage", "日付/時刻の形式が不正です。");
        }
    }

    private void handleUpdateManual(HttpServletRequest req, HttpSession session) {
        String userId = req.getParameter("userId");
        LocalDateTime oldCheckIn = LocalDateTime.parse(req.getParameter("oldCheckInTime"));
        LocalDateTime oldCheckOut = req.getParameter("oldCheckOutTime") != null && !req.getParameter("oldCheckOutTime").isEmpty()
                ? LocalDateTime.parse(req.getParameter("oldCheckOutTime"))
                : null;
        LocalDateTime newCheckIn = LocalDateTime.parse(req.getParameter("newCheckInTime"));
        LocalDateTime newCheckOut = req.getParameter("newCheckOutTime") != null && !req.getParameter("newCheckOutTime").isEmpty()
                ? LocalDateTime.parse(req.getParameter("newCheckOutTime"))
                : null;

        if (attendanceDAO.updateManualAttendance(userId, oldCheckIn, oldCheckOut, newCheckIn, newCheckOut)) {
            session.setAttribute("successMessage", "勤怠記録を手動で更新しました。");
        } else {
            session.setAttribute("errorMessage", "勤怠記録の更新に失敗しました。");
        }
    }

    private void handleDeleteManual(HttpServletRequest req, HttpSession session) {
        String userId = req.getParameter("userId");
        LocalDateTime checkIn = LocalDateTime.parse(req.getParameter("checkInTime"));
        LocalDateTime checkOut = req.getParameter("checkOutTime") != null && !req.getParameter("checkOutTime").isEmpty()
                ? LocalDateTime.parse(req.getParameter("checkOutTime"))
                : null;

        if (attendanceDAO.deleteManualAttendance(userId, checkIn, checkOut)) {
            session.setAttribute("successMessage", "勤怠記録を削除しました。");
        } else {
            session.setAttribute("errorMessage", "勤怠記録の削除に失敗しました。");
        }
    }
}
