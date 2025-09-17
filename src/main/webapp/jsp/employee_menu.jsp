<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> 
<!DOCTYPE html> 
<html lang="ja"> 
<head> 
    <meta charset="UTF-8"> 
    <title>従業員メニュー</title> 
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style.css">
</head> 
<body> 
	<jsp:include page="/jsp/base.jsp" />
    <div class="container"> 
        <h1>従業員メニュー</h1> 
        <p class="namep">ようこそ, ${user.username}さん</p> 
 
        <c:if test="${not empty sessionScope.successMessage}"> 
            <p class="success-message"><c:out value="${sessionScope.successMessage}"/></p> 
            <c:remove var="successMessage" scope="session"/> 
        </c:if> 
        
        <c:if test="${not empty errorMessage}">
            <p class="error-message" style="color:red;"><c:out value="${errorMessage}"/></p>
        </c:if> 
 
        <!-- 出退勤ボタン -->
        <div class="attendance-buttons">
		    <form action="${pageContext.request.contextPath}/attendance" method="post" class="attendance-form">
		        <input type="hidden" name="action" value="check_in">
		        <input type="submit" value="出勤" class="attendance-btn check-in">
		    </form>
		    <form action="${pageContext.request.contextPath}/attendance" method="post" class="attendance-form">
		        <input type="hidden" name="action" value="check_out">
		        <input type="submit" value="退勤" class="attendance-btn check-out">
		    </form>
		</div>

        <h2>あなたの勤怠履歴</h2> 
        <table> 
            <thead> 
                <tr> 
                    <th>出勤時刻</th> 
                    <th>退勤時刻</th>
                </tr> 
            </thead> 
            <tbody> 
                <c:forEach var="att" items="${attendanceRecords}"> 
                    <tr> 
                        <td>${att.formattedCheckInTime}</td>
                        <td>${att.formattedCheckOutTime}</td>
                    </tr> 
                </c:forEach> 
                <c:if test="${empty attendanceRecords}"> 
                    <tr><td colspan="2">勤怠記録がありません。</td></tr> 
                </c:if> 
            </tbody> 
        </table> 
        
        <c:if test="${user.role == 'fulltime'}">

        <h2>各種申請</h2>
        
        <form action="${pageContext.request.contextPath}/employee_request" method="post" class="request-form">
            <input type="hidden" name="action" value="leave">
            <p>
                <label for="leaveDate">有給申請日:</label>
                <input type="date" id="leaveDate" name="date" required>
            </p>
            <button type="submit" class="button">有給申請</button>
        </form>

        <form action="${pageContext.request.contextPath}/employee_request" method="post" class="request-form">
		    <input type="hidden" name="action" value="overtime">
		    <p>
		        <label for="overtimeDate" class="overtime-label">残業日:</label>
		        <input type="date" id="overtimeDate" name="date" required>
		    </p>
		    <p>
		        <label for="hours">残業時間 (時間単位):</label>
		        <input type="number" id="hours" name="hours" min="0.5" step="0.5" required>
		    </p>
		    <button type="submit" class="button">残業申請</button>
		</form>
		
		

        <!-- 申請履歴表示 -->
        <h2>申請履歴</h2>
        <table>
            <thead>
                <tr>
                    <th>申請日</th>
                    <th>申請種別</th>
                    <th>申請時間</th>
                    <th>状態</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="req" items="${user.requests}">
                    <tr>
                        <td>${req.date}</td>
                        <td>
                            <c:choose>
                                <c:when test="${req.paidLeaveRequested}">有給</c:when>
                                <c:when test="${req.overtimeRequested}">残業</c:when>
                                <c:otherwise>不明</c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <c:if test="${req.overtimeRequested}">${req.overtimeHours}</c:if>
                        </td>
                        <td>
                            <c:choose>
                                <c:when test="${req.paidLeaveRequested}">
                                    <c:if test="${req.paidLeaveApproved}">承認</c:if>
                                    <c:if test="${req.paidLeaveRejected}">却下</c:if>
                                    <c:if test="${not req.paidLeaveApproved and not req.paidLeaveRejected}">未承認</c:if>
                                </c:when>
                                <c:when test="${req.overtimeRequested}">
                                    <c:if test="${req.overtimeApproved}">承認</c:if>
                                    <c:if test="${req.overtimeRejected}">却下</c:if>
                                    <c:if test="${not req.overtimeApproved and not req.overtimeRejected}">未承認</c:if>
                                </c:when>
                                <c:otherwise>不明</c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty user.requests}">
                    <tr><td colspan="4">申請履歴がありません。</td></tr>
                </c:if>
            </tbody>
        </table>
        </c:if>
        <!-- 申請履歴表示ここまで -->

    </div> 
    
    <script src="${pageContext.request.contextPath}/style.js"></script>
</body> 
</html>