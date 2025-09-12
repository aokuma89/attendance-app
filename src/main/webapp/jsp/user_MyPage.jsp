<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <title>マイページ</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style.css">
</head>
<body>
	<jsp:include page="/jsp/base.jsp" />
	
	<div class="container">
	    <h1>マイページ</h1>
	
	    <p>ようこそ、<strong>${user.username}</strong>さん</p>
	
	    <h2>勤務情報</h2>
	    <table>
	        <tr>
	            <th>ユーザー名</th>
	            <td>${user.username}</td>
	        </tr>
	        <tr>
	            <th>勤務開始日</th>
	            <td>${user.startDate}</td>
	        </tr>
	        <tr>
	            <th>ロール</th>
	        <c:if test="${user.role == 'admin'}">
	            <td>管理者</td>
	        </c:if>
	        <c:if test="${user.role == 'fulltime'}">
	            <td>正社員</td>
	        </c:if>
	        <c:if test="${user.role == 'parttime'}">
	            <td>バイトorパート</td>
	        </c:if>
	        </tr>
	        <tr>
            	<th>残有給日数</th>
            	<td>${user.paidLeaveRemaining}日</td>
        	</tr>
	    </table>
	</div>
	
	<script src="${pageContext.request.contextPath}/style.js"></script>
</body>
</html>
