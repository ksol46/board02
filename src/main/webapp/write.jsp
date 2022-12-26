<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<meta name="viewport" content="width=device-width, initial-scale=1.0" />
<title>Insert title here</title>
<link rel="stylesheet" href="./css/style.css" />
</head>
<body>
	<div class="board_wrap">
      <div class="board_title">
        <strong>자유게시판</strong>
        <p>자유게시판 입니다.</p>
      </div>
      <div class="board_write_wrap">
        <form name="frm" method="post" action="insert">
        <div class="board_write">
          <div class="title">
            <dl>
              <dt>제목</dt>						<!-- db에서 테이블 만들 때 용량을 정해졌기 때문에 한계를 걸어둔다. -->
              <dd><input type="text" name="title" maxlength="50" placeholder="제목 입력" /></dd>
            </dl>
          </div>
          <div class="info">
            <dl>
              <dt>글쓴이</dt>
              				<!-- getparameter로 name으로 불러옴 -->
              <dd><input type="text" name="user_id" maxlength="10" placeholder="글쓴이 입력" /></dd>
            </dl>
          </div>
          <div class="cont">
            <textarea name="content" placeholder="내용 입력"></textarea>
          </div>
        </form>
        </div>
        <div class="bt_wrap">
          <a href="#" onclick="chkForm(); return false;" class="on">등록</a>
          <a href="list">취소</a>
        </div>
      </div>
    </div>
      <script>
    	<c:if test="${error != null}">
    		alert("${error}");
    	</c:if>
    </script>
    <script type="text/javascript" src="./script.js"></script>
</body>
</html>