<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <title>スケジュール提出</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style.css">
</head>
<body>
<jsp:include page="/jsp/base.jsp" />

<div class="container">
    <h1>スケジュール提出</h1>
    <p>ようこそ, ${user.username}さん</p>

    <c:if test="${not empty sessionScope.successMessage}">
        <p class="success-message">${sessionScope.successMessage}</p>
        <c:remove var="successMessage" scope="session"/>
    </c:if>

    <c:if test="${user.role == 'parttime'}">
    <div class="input-box">
        <form action="schedule" method="post" class="schedule-form">
            <input type="hidden" name="action" value="submit">
            <label>日付:</label>
            <input type="date" name="date" required>

            <label>開始時間:</label>
            <input type="time" name="startTime" required>

            <label>終了時間:</label>
            <input type="time" name="endTime" required>

            <input type="submit" value="提出" class="attendance-button">
        </form>
    </div>
    </c:if>

    <c:if test="${user.role != 'parttime'}">
        <p style="color:red;">※正社員・管理者はスケジュール提出できません</p>
    </c:if>

    <h2>提出済みスケジュール</h2>
    <div class="scroll-table">
    <table>
        <thead>
            <tr>
                <th>日付</th>
                <th>開始</th>
                <th>終了</th>
                <th>承認済み</th>
                <th>有給</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="s" items="${user.schedules}">
                <tr>
                    <td>${s.date}</td>
                    <td>${s.startTime}</td>
                    <td>${s.endTime}</td>
                    <td><c:out value="${s.approved ? 'はい' : 'いいえ'}"/></td>
                    <td><c:out value="${s.paidLeave ? 'はい' : 'いいえ'}"/></td>
                </tr>
            </c:forEach>
            <c:if test="${empty user.schedules}">
                <tr><td colspan="5">提出済みスケジュールがありません</td></tr>
            </c:if>
        </tbody>
    </table>
    </div>
</div>

<script src="${pageContext.request.contextPath}/style.js"></script>
</body>
</html>
