<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <%@include file="res/jsp/header.jsp" %>
    <body>
        <h1>Create a post:</h1>
        <form method="POST" enctype="multipart/form-data" action="image">
            <p>Image: <input type="file" name="file"></p>
            <p>Caption: <input type="text" name="caption"></p>
            <input type="submit" value="Post">
        </form>
    </body>
</html>
