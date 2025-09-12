<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <title>スケジュール管理</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style.css">
</head>
<body>
<jsp:include page="/jsp/header.jsp" />

<div class="container">
    <h1>スケジュール管理（管理者）</h1>

    <c:if test="${not empty sessionScope.successMessage}">
        <p class="success-message">${sessionScope.successMessage}</p>
        <c:remove var="successMessage" scope="session"/>
    </c:if>

    <c:forEach var="u" items="${users}">
    <c:if test="${u.role == 'parttime'}">
        <h2>${u.username} さん</h2>
        <p>残有給日数: ${u.paidLeaveRemaining}日</p>
        <div class="scroll-table">
        <table>
            <thead>
                <tr>
                    <th>日付</th>
                    <th>開始</th>
                    <th>終了</th>
                    <th>承認</th>
                    <th>有給</th>
                    <th>操作</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="s" items="${u.schedules}">
                    <tr>
                        <td>${s.date}</td>
                        <td>${s.startTime}</td>
                        <td>${s.endTime}</td>
                        <td>${s.approved ? 'はい' : 'いいえ'}</td>
                        <td>${s.paidLeave ? 'はい' : 'いいえ'}</td>
                        <td>
                            <c:if test="${!s.approved}">
                                <form action="schedule_admin" method="post" style="display:inline;">
                                    <input type="hidden" name="action" value="approve">
                                    <input type="hidden" name="username" value="${u.username}">
                                    <input type="hidden" name="date" value="${s.date}">
                                    <input type="submit" value="承認" class="attendance-button">
                                </form>
                            </c:if>
                            <c:if test="${!s.paidLeave && u.paidLeaveRemaining > 0}">
                                <form action="schedule_admin" method="post" style="display:inline;">
                                    <input type="hidden" name="action" value="set_paid_leave">
                                    <input type="hidden" name="username" value="${u.username}">
                                    <input type="hidden" name="date" value="${s.date}">
                                    <input type="submit" value="有給を適応" class="attendance-button">
                                </form>
                            </c:if>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty u.schedules}">
                    <tr><td colspan="6">提出スケジュールがありません</td></tr>
                </c:if>
            </tbody>
        </table>
        </div>
        </c:if>
    </c:forEach>
</div>
	<script src="${pageContext.request.contextPath}/style.js"></script>

</body>
</html>
