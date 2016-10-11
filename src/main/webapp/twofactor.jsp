<%--
    Document   : twofactor
    Created on : 11-Oct-2016, 02:14:17
    Author     : thms
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <h1>Hello World!</h1>
        <form role="form" method="POST" action="Login">
            <p>Code: <input type="text" name="code" maxlength="6"></p>
            <input type="submit" method="post" class="btn btn-lg btn-success btn-block" value="Submit">
        </form>
    </body>
</html>
