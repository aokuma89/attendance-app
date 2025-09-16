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
import com.example.attendance.dto.Schedule;
import com.example.attendance.dto.User;

@WebServlet("/schedule_admin")
public class ScheduleAdminServlet extends HttpServlet {

    private UserDAO userDAO = new UserDAO();
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User currentUser = (User) session.getAttribute("user");

        if (currentUser == null || !"admin".equals(currentUser.getRole())) {
            resp.sendRedirect("/login.jsp");
            return;
        }

        req.setAttribute("users", userDAO.getAllUsers());
        RequestDispatcher rd = req.getRequestDispatcher("/jsp/schedule_admin.jsp");
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

        String action = req.getParameter("action");
        String username = req.getParameter("username");
        LocalDate date = LocalDate.parse(req.getParameter("date"));

        User user = userDAO.findByUsername(username);
        if (user != null) {
            for (Schedule s : user.getSchedules()) {
                if (s.getDate().equals(date)) {
                    if ("approve".equals(action)) {
                        s.setApproved(true);
                        session.setAttribute("successMessage", username + " のスケジュールを承認しました。");
                    } else if ("set_paid_leave".equals(action)) {
                        if (!s.isPaidLeave() && user.getPaidLeaveRemaining() > 0) {
                            s.setPaidLeave(true);
                            user.usePaidLeave();
                            session.setAttribute("successMessage", username + " のスケジュールを有給にしました。");
                        }
                    }
                    break;
                }
            }
        }

        resp.sendRedirect(req.getContextPath() + "/schedule_admin");
    }


}
