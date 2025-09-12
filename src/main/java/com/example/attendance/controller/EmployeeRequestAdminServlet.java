package com.example.attendance.controller;

import java.io.IOException;
import java.time.LocalDate;

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

        // 全リクエストを取得
        req.setAttribute("leaveRequests", userDAO.getAllLeaveRequests());
        req.setAttribute("overtimeRequests", userDAO.getAllOvertimeRequests());

        // 申請の有無をセット
        req.setAttribute("hasLeaveRequest", !userDAO.getAllLeaveRequests().isEmpty());
        req.setAttribute("hasOvertimeRequest", !userDAO.getAllOvertimeRequests().isEmpty());

        RequestDispatcher rd = req.getRequestDispatcher("/jsp/admin_menu.jsp");
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

        if ("approve_leave".equals(action)) {
            userDAO.approveLeave(username, date);
            session.setAttribute("successMessage", username + " の有給を承認しました。");
        } else if ("approve_overtime".equals(action)) {
            userDAO.approveOvertime(username, date);
            session.setAttribute("successMessage", username + " の残業を承認しました。");
        }

        resp.sendRedirect(req.getContextPath() + "/employee_request_admin"); // 最新画面にリダイレクト
    }
}
