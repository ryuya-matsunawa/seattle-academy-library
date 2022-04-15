<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false"%>
<%@ page contentType="text/html; charset=utf8"%>
<!doctype html>
<html>
<head>
<meta name="description" content="社内の書籍検索や貸出を行うことができます。" />
<meta name="robots" content="noindex,nofollow" />
<meta http-equiv="content-type" content="text/html" charset="utf-8" />
<title>アカウントの作成｜シアトルライブラリ｜シアトルコンサルティング株式会社</title>
<link href="<c:url value="/resources/css/reset.css" />" rel="stylesheet" type="text/css">
<link href="https://fonts.googleapis.com/css?family=Noto+Sans+JP" rel="stylesheet">
<link href="<c:url value="/resources/css/default.css" />" rel="stylesheet" type="text/css">
<link href="<c:url value="/resources/css/login.css" />" rel="stylesheet" type="text/css">
</head>
<body>
    <div class="wrapper">
        <main>
            <div class="authorization_head">
                <img class="mark" src="resources/img/logo.png" />
                <div class="logo">Seattle Library</div>
            </div>
            <div class="authorization">
                <div class="authorization_form">
                    <form method="post" action="createAccount">
                        <div class="title">アカウントの作成</div>
                        <label class="label">メールアドレス</label> <input type="email" class="input" id="email" name="email" autocomplete="off" required> <label class="label">パスワード</label> <input type="password" class="input" id="password" name="password" required> <label class="label">パスワード（確認用）</label> <input type="password" class="input" id="passwordForCheck" name="passwordForCheck" required> <input type="submit" class="button primary" value="作成する">
                   <c:if test="${!empty errorPassword}">
                        <p class="error">${errorPassword}</p>
                   
                   </c:if>
                   
                 
                   
                   
                    </form>
                    
                </div>
                <div class="authorization_navi">
                    <label class="authorization_text">すでにアカウントをお持ちですか？</label>
                    <form method="get" action="<%=request.getContextPath()%>/">
                        <a class="authorization_link marker" href="javascript:void(0)" onclick="this.parentNode.submit()">ログイン</a>
                    </form>
                </div>
            </div>
        </main>
        <footer>
            <div class="copyright">© 2019 Seattle Consulting Co., Ltd. All rights reserved.</div>
        </footer>
    </div>
</body>
</html>