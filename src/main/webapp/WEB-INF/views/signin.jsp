<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<html>
<head>
    <title>Signin</title>
</head>
<body>
<form action="/j_spring_security_check" method="post">
    <input type="text" placeholder="email" name="j_username"/>
    <input type="password" placeholder="password" name="j_password"/>
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
    <input type="submit" value="Signin"/>
</form>
<a href="/oauth2/daum_authorization_code">Daum으로 로그인</a><br/>
<a href="/oauth2/facebook_authorization_code">Facebook으로 로그인</a><br/>
</body>
</html>