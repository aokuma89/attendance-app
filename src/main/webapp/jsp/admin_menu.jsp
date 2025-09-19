<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <title>管理者メニュー</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style.css">
</head>
<body>
	<jsp:include page="/jsp/base.jsp" />
	
    <div class="container">
        <h1>管理者メニュー</h1>
        <p>ようこそ, ${user.username}さん (管理者)</p>

        <c:if test="${not empty sessionScope.successMessage}">
            <p class="success-message">
                <c:out value="${sessionScope.successMessage}"/>
            </p>
            <c:remove var="successMessage" scope="session"/>
        </c:if>


		<h2>勤怠履歴</h2>

        <form action="attendance" method="get" class="filter-form">
            <input type="hidden" name="action" value="filter">
            <div>
                <label for="filterUserId">ユーザーID:</label>
                <input type="text" id="filterUserId" name="filterUserId" 
                       value="<c:out value='${param.filterUserId}'/>">
            </div>
            <div>
                <label for="startDate">開始日:</label>
                <input type="date" id="startDate" name="startDate" 
                       value="<c:out value='${param.startDate}'/>">
            </div>
            <div>
                <label for="endDate">終了日:</label>
                <input type="date" id="endDate" name="endDate" 
                       value="<c:out value='${param.endDate}'/>">
            </div>
            <button type="submit" class="button">フィルタ</button>
        </form>

        <c:if test="${not empty errorMessage}">
        	<p class="error-message"><c:out value="${errorMessage}"/></p>
    	</c:if>

        <a href="attendance?action=export_csv&filterUserId=<c:out value="${param.filterUserId}"/>&startDate=<c:out value="${param.startDate}"/>&endDate=<c:out value="${param.endDate}"/>" class="button">勤怠履歴を CSV エクスポート</a>

        
	
	<h2>月別勤怠グラフ</h2>
	<div class="vertical-bar-chart" style="display: flex; align-items: flex-end; gap: 18px; margin-bottom: 30px; overflow-x: auto; max-width: 100%;">
	  <c:forEach var="entry" items="${monthlyWorkingHours}">
	    <div style="display: flex; flex-direction: column; align-items: center;">
	      <span style="font-size: 0.95em; margin-bottom: 4px;">${entry.value}時間</span>
	      <div class="bar" style="height: ${entry.value * 1}px; width: 28px; position: relative;"></div>
	      <span style="font-size: 0.9em; margin-top: 6px; color: #444;">${entry.key}</span>
	    </div>
	  </c:forEach>
	  <c:if test="${empty monthlyWorkingHours}">
	    <span>データがありません。</span>
	  </c:if>
	</div>
	
	
	<h2>月別出勤日数グラフ</h2>
	<div class="vertical-bar-chart" style="display: flex; align-items: flex-end; gap: 18px; margin-bottom: 30px; overflow-x: auto; max-width: 100%;">
	  <c:forEach var="entry" items="${monthlyCheckInCounts}">
	    <div style="display: flex; flex-direction: column; align-items: center;">
	      <span style="font-size: 0.95em; margin-bottom: 4px;">
	        <c:if test="${entry.value != 1}">${entry.value}日</c:if>
	      </span>
	      <div class="bar bar-blue" style="height: ${entry.value * 5}px; width: 28px; position: relative;"></div>
	      <span style="font-size: 0.9em; margin-top: 6px; color: #444;">${entry.key}</span>
	    </div>
	  </c:forEach>
	  <c:if test="${empty monthlyCheckInCounts}">
	    <span>データがありません。</span>
	  </c:if>
	</div>
	
	
	<h2>詳細勤怠履歴</h2>
	<div class="scroll-table de">
	    <table>
	        <thead>
	            <tr>
	                <th>従業員 ID</th>
	                <th>出勤時刻</th>
	                <th>退勤時刻</th>
	                <th>操作</th>
	            </tr>
	        </thead>
	        <tbody>
	            <c:forEach var="att" items="${allAttendanceRecords}">
	                <tr>
	                    <td>${att.userId}</td>
	                    <td>${att.formattedCheckInTime}</td>
	                    <td>${att.formattedCheckOutTime}</td>
	                    <td class="table-actions">
	                        <form action="attendance" method="post" style="display:inline;">
	                            <input type="hidden" name="action" value="delete_manual">
	                            <input type="hidden" name="userId" value="${att.userId}">
	                            <input type="hidden" name="checkInTime" value="${att.isoCheckInTime}">
	                            <input type="hidden" name="checkOutTime" value="${att.isoCheckOutTime}">
	                            <input type="submit" value="削除" class="button danger"
	                                   onclick="return confirm('本当にこの勤怠記録を削除しますか？');">
	                        </form>
	                    </td>
	                </tr>
	            </c:forEach>
	            <c:if test="${empty allAttendanceRecords}">
	                <tr><td colspan="4">データがありません。</td></tr>
	            </c:if>
	        </tbody>
	    </table>
	</div>

    <h2>勤怠記録の手動追加</h2>
    <form action="attendance" method="post">
        <input type="hidden" name="action" value="add_manual">
        <p>
            <label for="manualUserId">ユーザーID:</label>
            <input type="text" id="manualUserId" name="userId" required>
        </p>
        <p>
            <label for="manualCheckInTime">出勤時刻:</label>
            <input type="datetime-local" id="manualCheckInTime" name="checkInTime" required>
        </p>
        <p>
            <label for="manualCheckOutTime">退勤時刻 (任意):</label>
            <input type="datetime-local" id="manualCheckOutTime" name="checkOutTime">
        </p>
        <div class="button-group">
            <input type="submit" value="追加">
        </div>
    </form>
</div>

<script src="${pageContext.request.contextPath}/style.js?v=1"></script>
    
</body>
</html>
