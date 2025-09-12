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



@WebServlet("/employee_request")
public class EmployeeRequestServlet extends HttpServlet {
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

        // ユーザー全員を取得（requestsを含む）
        req.setAttribute("users", userDAO.getAllUsers());

        RequestDispatcher rd = req.getRequestDispatcher("/jsp/admin_menu.jsp"); // admin_menu.jsp に表示
        rd.forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        HttpSession session = req.getSession(false);
        User currentUser = (User) session.getAttribute("user");

        if (currentUser == null || !"fulltime".equals(currentUser.getRole())) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        String action = req.getParameter("action");
        LocalDate date = LocalDate.parse(req.getParameter("date"));

        if ("leave".equals(action)) {
            userDAO.submitLeaveRequest(currentUser.getUsername(), date);
            session.setAttribute("successMessage", "有給申請を提出しました。");
        } else if ("overtime".equals(action)) {
            double hours = Double.parseDouble(req.getParameter("hours"));
            userDAO.submitOvertimeRequest(currentUser.getUsername(), date, hours);
            session.setAttribute("successMessage", hours + "時間の残業申請を提出しました。");
        }

        // セッションの user を更新しておく
        session.setAttribute("user", userDAO.findByUsername(currentUser.getUsername()));

        resp.sendRedirect(req.getContextPath() + "/attendance");
    }
}
