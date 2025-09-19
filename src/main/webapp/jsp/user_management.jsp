<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <title>ユーザー管理</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style.css">
</head>
<body>
<jsp:include page="/jsp/base.jsp" />

<div class="container">
    <h1>ユーザー管理</h1>
    <p>ようこそ, ${user.username}さん (管理者)</p>

    <c:if test="${not empty sessionScope.successMessage}">
        <p class="success-message"><c:out value="${sessionScope.successMessage}"/></p>
        <c:remove var="successMessage" scope="session"/>
    </c:if>
    <c:if test="${not empty errorMessage}">
        <p class="error-message"><c:out value="${errorMessage}"/></p>
    </c:if>

    <h2>ユーザー追加/編集</h2>

    <c:choose>
        <c:when test="${userToEdit != null}">
            <form action="users" method="post" class="user-form">
                <input type="hidden" name="action" value="update">
                <input type="hidden" name="username" value="${userToEdit.username}">

                <label for="username">ユーザーID:</label>
                <input type="text" id="username" name="username"
                       value="${userToEdit.username}" readonly required>

                <label for="password">パスワード:</label>
				<input type="password" id="password" name="password"placeholder="変更する場合は入力してください">

                <label for="role">役割:</label>
				<select id="role" name="role" required>
				    <option value="admin" <c:if test="${userToEdit.role == 'admin'}">selected</c:if>>管理者</option>
				    <option value="fulltime" <c:if test="${userToEdit.role == 'fulltime'}">selected</c:if>>正社員</option>
				    <option value="parttime" <c:if test="${userToEdit.role == 'parttime'}">selected</c:if>>パート・バイト</option>
				</select>


                <p>
                    <label for="enabled">アカウント有効:</label>
                    <input type="checkbox" id="enabled" name="enabled" value="true"
                           <c:if test="${userToEdit.enabled}">checked</c:if>>
                </p>

                <div class="button-group">
                    <input type="submit" value="更新">

                    <form action="users" method="post" style="display:inline;">
                        <input type="hidden" name="action" value="reset_password">
                        <input type="hidden" name="username" value="${userToEdit.username}">
                        <input type="hidden" name="newPassword" value="password">
                    </form>
                </div>
            </form>
        </c:when>
        <c:otherwise>
            <form action="users" method="post" class="user-form">
                <input type="hidden" name="action" value="add">

                <label for="username">ユーザーID:</label>
                <input type="text" id="username" name="username" required>

                <label for="password">パスワード:</label>
                <input type="password" id="password" name="password" required>

                <label for="role">役割:</label>
				<select id="role" name="role" required>
				    <option value="fulltime">正社員</option>
				    <option value="parttime">パート・バイト</option>
                    <option value="admin">管理者</option>
				</select>

                <p>
                    <label for="enabled">アカウント有効:</label>
                    <input type="checkbox" id="enabled" name="enabled" value="true" checked>
                </p>

                <div class="button-group">
                    <input type="submit" value="追加">
                </div>
            </form>
        </c:otherwise>
    </c:choose>

    <h2>既存ユーザー</h2>
    <table>
        <thead>
        <tr>
            <th>ユーザーID</th>
            <th>役割</th>
            <th>入社日</th>
            <th>有効</th>
            <th>操作</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="u" items="${users}">
            <tr>
                <td>${u.username}</td>
                <td>${u.role}</td>
                <td>${u.startDate}</td>
                <td>
                    <form action="users" method="post" style="display:inline;">
                        <input type="hidden" name="action" value="toggle_enabled">
                        <input type="hidden" name="username" value="${u.username}">
                        <input type="hidden" name="enabled" value="${!u.enabled}">
                        <input type="submit"
                               value="<c:choose><c:when test='${u.enabled}'>無効化</c:when><c:otherwise>有効化</c:otherwise></c:choose>"
                               class="button <c:choose><c:when test='${u.enabled}'>danger</c:when><c:otherwise>secondary</c:otherwise></c:choose>"
                               onclick="return confirm('本当にこのユーザーを<c:choose><c:when test='${u.enabled}'>無効</c:when><c:otherwise>有効</c:otherwise></c:choose>にしますか？');">
                    </form>
                </td>
                <td class="table-actions">
                    <a href="users?action=edit&username=${u.username}" class="button">編集</a>
                    <form action="users" method="post" style="display:inline;">
                        <input type="hidden" name="action" value="delete">
                        <input type="hidden" name="username" value="${u.username}">
                        <input type="submit" value="削除" class="button danger"
                               onclick="return confirm('本当にこのユーザーを削除しますか？');">
                    </form>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty users}">
            <tr><td colspan="4">ユーザーがいません。</td></tr>
        </c:if>
        </tbody>
    </table>
</div>

<script src="${pageContext.request.contextPath}/style.js"></script>

</body>
</html>
