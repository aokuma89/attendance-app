package com.example.attendance.controller;

import java.io.IOException;
import java.time.LocalDate;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.example.attendance.dao.UserDAO;
import com.example.attendance.dto.LeaveOvertimeRequest;
import com.example.attendance.dto.User;

@WebServlet("/employee_request")
public class EmployeeRequestServlet extends HttpServlet {
    private UserDAO userDAO = new UserDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User currentUser = (User) session.getAttribute("user");

        if (currentUser == null) {
            resp.sendRedirect("login.jsp");
            return;
        }

        String action = req.getParameter("action");
        String dateStr = req.getParameter("date");
        String hoursStr = req.getParameter("hours");

        LocalDate date = LocalDate.parse(dateStr);

        if ("leave".equals(action)) {
            LeaveOvertimeRequest request = new LeaveOvertimeRequest(date, currentUser.getUsername());
            request.setPaidLeaveRequested(true);
            currentUser.addRequest(request);
            session.setAttribute("successMessage", "申請が送信されました。");
            resp.sendRedirect(req.getContextPath() + "/jsp/employee_menu.jsp");
            return;
        } else if ("overtime".equals(action) && hoursStr != null) {
            double hours = Double.parseDouble(hoursStr);
            int year = date.getYear();
            int month = date.getMonthValue();
            double monthlyTotal = currentUser.getMonthlyOvertimeHours(year, month);
            if (monthlyTotal + hours > 45.0) {
                LeaveOvertimeRequest request = new LeaveOvertimeRequest(date, currentUser.getUsername());
                request.setOvertimeRequested(true);
                request.setOvertimeHours(hours);
                request.setOvertimeRejected(true);
                currentUser.addRequest(request);
                req.setAttribute("errorMessage", "残業申請時間が月45時間を超えています。申請は却下されました。");
                req.getRequestDispatcher("/jsp/employee_menu.jsp").forward(req, resp);
                return;
            } else {
                LeaveOvertimeRequest request = new LeaveOvertimeRequest(date, currentUser.getUsername());
                request.requestOvertime(hours);
                currentUser.addRequest(request);
                session.setAttribute("successMessage", "申請が送信されました。");
                resp.sendRedirect(req.getContextPath() + "/jsp/employee_menu.jsp");
                return;
            }
        }

        System.out.println("=== デバッグ: 申請後のユーザー情報 ===");
        System.out.println("ユーザー: " + currentUser.getUsername());
        currentUser.getRequests().forEach(r -> {
            System.out.println("日付: " + r.getDate() +
                               ", 有給: " + r.isPaidLeaveRequested() +
                               ", 有給承認済: " + r.isPaidLeaveApproved() +
                               ", 残業: " + r.isOvertimeRequested() +
                               ", 残業承認済: " + r.isOvertimeApproved() +
                               ", 残業時間: " + r.getOvertimeHours() +
                               ", 申請者ID: " + r.getUserId());
        });
        System.out.println("=== デバッグここまで ===");
    }
}