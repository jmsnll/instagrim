<%@page import="uk.ac.dundee.computing.tjn.instagrim.stores.PostStore"%>
<%@page import="uk.ac.dundee.computing.tjn.instagrim.stores.ProfileStore"%>
<%@page import="uk.ac.dundee.computing.tjn.instagrim.models.PostModel"%>
<%@page import="com.datastax.driver.core.Cluster"%>
<%@page import="uk.ac.dundee.computing.tjn.instagrim.lib.CassandraHosts"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.LinkedList"%>
<%@page import="uk.ac.dundee.computing.tjn.instagrim.stores.SessionStore"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
    <%@include file="res/jsp/header.jsp" %>
    <body>
        <%@include file="res/jsp/navigation.jsp" %>
        <div class="container">
            <div class="row">
                <div class="col-lg-12 text-center user-profile">
                    <h1>Create a new post</h1>
                    <form method="POST" enctype="multipart/form-data" action="image">
                        <p>Image: <input type="file" name="file"></p>
                        <p>Caption: <input type="text" name="caption"></p>
                        <input type="submit" value="Post">
                    </form>
                </div>
            </div>
        </div>
    </body>
</html>