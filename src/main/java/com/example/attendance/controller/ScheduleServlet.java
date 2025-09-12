package com.example.attendance.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;

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

@WebServlet("/schedule")
public class ScheduleServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("user") : null;

        if (currentUser == null) {
        	resp.sendRedirect("/login.jsp");
            return;
        }

        req.setAttribute("schedules", currentUser.getSchedules());
        RequestDispatcher rd = req.getRequestDispatcher("/jsp/schedule_submit.jsp");
        rd.forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        HttpSession session = req.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("user") : null;

        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        if (!"parttime".equals(currentUser.getRole())) {
            session.setAttribute("errorMessage", "スケジュール提出はバイト・パートのみ可能です。");
            resp.sendRedirect(req.getContextPath() + "/mypage");
            return;
        }

        String action = req.getParameter("action");

        if ("submit".equals(action)) {
            try {
                LocalDate date = LocalDate.parse(req.getParameter("date"));
                LocalTime startTime = LocalTime.parse(req.getParameter("startTime"));
                LocalTime endTime = LocalTime.parse(req.getParameter("endTime"));

                Schedule newSchedule = new Schedule(date, startTime, endTime);

                // 直接 addSchedule ではなく DAO 経由にする
                UserDAO userDAO = new UserDAO(); // DAO インスタンス生成
                userDAO.submitSchedule(currentUser.getUsername(), newSchedule); // ここで 10件以上なら削除もされる

                session.setAttribute("successMessage", "スケジュールを提出しました。");
            } catch (Exception e) {
                session.setAttribute("errorMessage", "入力値に不備があります。");
            }
        }

        resp.sendRedirect(req.getContextPath() + "/schedule");
    }

}
