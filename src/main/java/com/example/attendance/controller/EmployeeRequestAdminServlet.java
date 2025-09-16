package com.example.attendance.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.example.attendance.dao.UserDAO;
import com.example.attendance.dto.User;

@WebServlet("/employee_request_admin")
public class EmployeeRequestAdminServlet extends HttpServlet {

    private UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        User currentUser = (User) session.getAttribute("user");

        if (currentUser == null || !"admin".equals(currentUser.getRole())) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        List<User> users = new ArrayList<>(userDAO.getAllUsers());
        req.setAttribute("users", users);

        RequestDispatcher rd = req.getRequestDispatcher("/jsp/employee_request_admin.jsp");
        rd.forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        HttpSession session = req.getSession(false);
        User currentUser = (User) session.getAttribute("user");

        if (currentUser == null || !"admin".equals(currentUser.getRole())) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        String username = req.getParameter("username");
        String action = req.getParameter("action");
        LocalDate date = LocalDate.parse(req.getParameter("date"));

        User user = userDAO.findByUsername(username);
        if (user != null) {
            switch (action) {
                case "approve_leave":
                    user.getRequests().forEach(r -> {
                        if (r.getDate().equals(date) && r.isPaidLeaveRequested() && !r.isPaidLeaveApproved()) {
                            if (user.getPaidLeaveRemaining() > 0) {
                                r.setPaidLeaveApproved(true);
                                user.usePaidLeave();
                                session.setAttribute("successMessage", username + " の有給を承認しました。残り日数: " + user.getPaidLeaveRemaining() + "日");
                            } else {
                                session.setAttribute("errorMessage", username + " は有給残日数が0のため承認できません。");
                            }
                        }
                    });
                    break;

                case "reject_leave":
                    boolean removed = user.getRequests().removeIf(r ->
                            r.getDate().equals(date) && r.isPaidLeaveRequested() && !r.isPaidLeaveApproved()
                    );
                    if (removed) {
                        session.setAttribute("successMessage", username + " の有給を却下しました。");
                    }
                    break;

                case "approve_overtime":
                    user.getRequests().forEach(r -> {
                        if (r.getDate().equals(date) && r.isOvertimeRequested() && !r.isOvertimeApproved()) {
                            r.setOvertimeApproved(true);
                            session.setAttribute("successMessage", username + " の残業を承認しました。");
                        }
                    });
                    break;

                case "reject_overtime":
                    user.getRequests().forEach(r -> {
                        if (r.getDate().equals(date) && r.isOvertimeRequested() && !r.isOvertimeApproved() && !r.isOvertimeRejected()) {
                            r.setOvertimeRejected(true);
                            session.setAttribute("successMessage", username + " の残業申請を却下しました。");
                        }
                    });
                    break;
            }
        }

        resp.sendRedirect(req.getContextPath() + "/employee_request_admin");
    }

}