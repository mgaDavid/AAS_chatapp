<%-- 
    Document   : response
    Created on : 14/06/2022, 22:04:26
    Author     : osoar
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <jsp:useBean id="userbean" scope="session" class="pt.amc.nameserver.NameServer" />
        <jsp:setProperty name="request" property="name" />
        <h1>Hello, <jsp:getProperty name="userbean" property="name" />!</h1>
    </body>
</html>
