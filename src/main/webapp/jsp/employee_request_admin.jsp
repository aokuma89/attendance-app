<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <title>残業・有給管理</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style.css">
</head>
<body>
<jsp:include page="/jsp/base.jsp" />

<div class="container">
    <h1>残業・有給管理（管理者）</h1>

    <c:if test="${not empty sessionScope.successMessage}">
        <p class="success-message">${sessionScope.successMessage}</p>
        <c:remove var="successMessage" scope="session"/>
    </c:if>

    <c:forEach var="u" items="${users}">
        <c:if test="${u.role == 'fulltime'}">
            <h2>${u.username} さん</h2>
            <p>残有給日数: ${u.paidLeaveRemaining}日</p>

            <!-- 残業申請 -->
            <h3>残業申請</h3>
            <div class="scroll-table">
            <table>
                <thead>
                    <tr>
                        <th>申請日</th>
                        <th>残業申請時間</th>
                        <th>承認</th>
                        <th>操作</th>
                    </tr>
                </thead>
                <tbody>
                    <c:set var="hasOvertime" value="false"/>
                    <c:forEach var="r" items="${u.requests}">
                        <c:if test="${r.overtimeRequested}">
                            <c:set var="hasOvertime" value="true"/>
                            <tr>
                                <td>${r.date}</td>
                                <td>${r.overtimeHours}</td>
                                <td>${r.overtimeApproved ? '承認済' : '未承認'}</td>
                                <td>
                                    <c:if test="${!r.overtimeApproved}">
                                        <form action="employee_request_admin" method="post" style="display:inline;">
                                            <input type="hidden" name="action" value="approve_overtime">
                                            <input type="hidden" name="username" value="${u.username}">
                                            <input type="hidden" name="date" value="${r.date}">
                                            <input type="submit" value="承認" class="attendance-button">
                                        </form>
                                    </c:if>
                                </td>
                            </tr>
                        </c:if>
                    </c:forEach>
                    <c:if test="${not hasOvertime}">
                        <tr><td colspan="4">残業申請はありません</td></tr>
                    </c:if>
                </tbody>
            </table>
            </div>

            <!-- 有給申請 -->
			<h3>有給申請</h3>
			<div class="scroll-table">
			<table>
			    <thead>
			        <tr>
			            <th>申請日</th>
			            <th>承認</th>
			            <th>操作</th>
			        </tr>
			    </thead>
			    <tbody>
			        <c:set var="hasLeave" value="false"/>
			        <c:forEach var="r" items="${u.requests}">
			            <c:if test="${r.paidLeaveRequested}">
			                <c:set var="hasLeave" value="true"/>
			                <tr>
			                    <td>${r.date}</td>
			                    <td>
			                        <c:choose>
			                            <c:when test="${r.paidLeaveApproved}">承認済</c:when>
			                            <c:when test="${r.paidLeaveRejected}">却下済</c:when>
			                            <c:otherwise>未承認</c:otherwise>
			                        </c:choose>
			                    </td>
			                    <td>
			                        <c:if test="${!r.paidLeaveApproved && !r.paidLeaveRejected}">
			                            <!-- 承認ボタン -->
			                            <form action="employee_request_admin" method="post" style="display:inline;">
			                                <input type="hidden" name="action" value="approve_leave">
			                                <input type="hidden" name="username" value="${u.username}">
			                                <input type="hidden" name="date" value="${r.date}">
			                                <input type="submit" value="承認" class="attendance-button">
			                            </form>
			                            <!-- 却下ボタン -->
			                            <form action="employee_request_admin" method="post" style="display:inline;">
			                                <input type="hidden" name="action" value="reject_leave">
			                                <input type="hidden" name="username" value="${u.username}">
			                                <input type="hidden" name="date" value="${r.date}">
			                                <input type="submit" value="却下" class="button danger">
			                            </form>
			                        </c:if>
			                    </td>
			                </tr>
			            </c:if>
			        </c:forEach>
			        <c:if test="${not hasLeave}">
			            <tr><td colspan="3">有給申請はありません</td></tr>
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
