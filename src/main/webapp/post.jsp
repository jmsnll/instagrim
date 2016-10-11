<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <h1>Create a post:</h1>
        <form method="POST" enctype="multipart/form-data" action="Image">
            <p>Image: <input type="file" name="file"></p>
            <p>Caption: <input type="text" name="caption"></p>
            <input type="submit" value="Post">
        </form>
    </body>
</html>
