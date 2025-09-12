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
	<jsp:include page="/jsp/header.jsp" />
    <div class="container"> 
        <h1>従業員メニュー</h1> 
        <p class="namep">ようこそ, ${user.username}さん</p> 
 
        <c:if test="${not empty sessionScope.successMessage}"> 
            <p class="success-message"><c:out value="${sessionScope.successMessage}"/></p> 
            <c:remove var="successMessage" scope="session"/> 
        </c:if> 
 
        <!-- 出退勤ボタン -->
        <div class="attendance-buttons">
		    <form action="attendance" method="post" class="attendance-form">
		        <input type="hidden" name="action" value="check_in">
		        <input type="submit" value="出勤" class="attendance-btn check-in">
		    </form>
		    <form action="attendance" method="post" class="attendance-form">
		        <input type="hidden" name="action" value="check_out">
		        <input type="submit" value="退勤" class="attendance-btn check-out">
		    </form>
		</div>
		
        <!-- 勤怠履歴 -->
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
                        <td>${att.isoCheckInTime}</td>
                        <td>${att.isoCheckOutTime}</td>
                    </tr> 
                </c:forEach> 
                <c:if test="${empty attendanceRecords}"> 
                    <tr><td colspan="2">勤怠記録がありません。</td></tr> 
                </c:if> 
            </tbody> 
        </table> 

        <!-- 申請フォーム -->
        <h2>各種申請</h2>
        
        <!-- 有給申請 -->
        <form action="employee_request" method="post" class="request-form">
            <input type="hidden" name="action" value="leave">
            <p>
                <label for="leaveDate">有給申請日:</label>
                <input type="date" id="leaveDate" name="date" required>
            </p>
            <button type="submit">有給申請</button>
        </form>

        <!-- 残業申請 -->
        <form action="employee_request" method="post" class="request-form">
		    <input type="hidden" name="action" value="overtime">
		    <p>
		        <label for="overtimeDate">残業日:</label>
		        <input type="date" id="overtimeDate" name="date" required>
		    </p>
		    <p>
		        <label for="hours">残業時間 (時間単位):</label>
		        <input type="number" id="hours" name="hours" min="0.5" step="0.5" required>
		    </p>
		    <button type="submit">残業申請</button>
		</form>

    </div> 
    
    <script src="${pageContext.request.contextPath}/style.js"></script>
</body> 
</html>
