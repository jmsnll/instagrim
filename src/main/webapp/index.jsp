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
                    <h1>Welcome to Instagrim!</h1>
                </div>
                <%                    PostModel post = new PostModel();
                    LinkedList<PostStore> posts = post.getMostRecentPosts();
                    if (posts == null) {
                %>
                <p>No posts found!</p>
                <%} else {
                    Iterator<PostStore> iterator = posts.iterator();
                    while (iterator.hasNext()) {
                        PostStore ps = (PostStore) iterator.next();
                %>
                <div class="col-sm-4 text-center">
                    <div class="user-post" style="background-image: url('/Instagrim/image/<%=ps.getPostID()%>')">
                    </div>
                </div>
                <%
                        }
                    }
                %>
            </div>
        </div>
    </body>
</html>
