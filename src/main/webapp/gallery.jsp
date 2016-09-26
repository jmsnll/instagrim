<%@page import="java.util.*"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="uk.ac.dundee.computing.tjn.instagrim.stores.*" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Instagrim</title>
        <link rel="stylesheet" type="text/css" href="/Instagrim/Styles.css" />
    </head>
    <body>
        <header>

            <h1>InstaGrim ! </h1>
            <h2>Your world in Black and White</h2>
        </header>

        <nav>
            <ul>
                <li class="nav"><a href="/Instagrim/upload.jsp">Upload</a></li>
                <li class="nav"><a href="/Instagrim/Images/majed">Sample Images</a></li>
            </ul>
        </nav>

        <article>
            <h1>Your Images</h1>
            <%
                LinkedList<Image> listImages = (LinkedList<Image>) request.getAttribute("Images");
                if (listImages == null) {
            %>
            <p>No Images found</p>
            <%
            } else {
                Iterator<Image> iterator;
                iterator = listImages.iterator();
                while (iterator.hasNext()) {
                    Image image = (Image) iterator.next();

            %>
            <a href="/Instagrim/Image/<%=image.getSUUID()%>" ><img src="/Instagrim/Thumb/<%=image.getSUUID()%>"></a><br/><%

                    }
                }
                %>
        </article>
        <footer>
            <ul>
                <li class="footer"><a href="/Instagrim">Home</a></li>
            </ul>
        </footer>
    </body>
</html>
