<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<div class="header">
  <!-- ハンバーガーメニュー -->
  <button class="hamburger" aria-label="メニュー" aria-controls="nav-menu" aria-expanded="false">
    <span class="hamburger__line"></span>
    <span class="hamburger__line"></span>
    <span class="hamburger__line"></span>
  </button>

  <!-- ナビゲーションメニュー -->
  <nav id="nav-menu" class="nav" aria-hidden="true">
    <ul class="nav__list">
      <c:choose>
        <c:when test="${user.role == 'admin'}">
          <li class="nav__item"><a href="attendance?action=filter" class="nav__link">勤怠管理</a></li>
          <li class="nav__item"><a href="users?action=list" class="nav__link">ユーザー管理</a></li>
          <li class="nav__item"><a href="${pageContext.request.contextPath}/schedule_admin" class="nav__link">スケジュール管理</a></li>
          <li class="nav__item"><a href="logout" class="nav__link">ログアウト</a></li>
        </c:when>
        <c:otherwise>
          <li class="nav__item"><a href="${pageContext.request.contextPath}/attendance?action=check_in" class="nav__link">勤怠画面</a></li>
          <li class="nav__item"><a href="${pageContext.request.contextPath}/mypage" class="nav__link">マイページ</a></li>
          <c:if test="${user.role == 'parttime'}">
          	<li class="nav__item"><a href="${pageContext.request.contextPath}/schedule" class="nav__link">シフトスケジュール提出</a></li>
          </c:if>
          <li class="nav__item"><a href="logout" class="nav__link">ログアウト</a></li>
        </c:otherwise>
      </c:choose>
    </ul>
  </nav>
</div>
