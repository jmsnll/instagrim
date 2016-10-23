<%@page import="uk.ac.dundee.computing.tjn.instagrim.stores.PostStore"%>
<%@page import="uk.ac.dundee.computing.tjn.instagrim.stores.ProfileStore"%>
<%@page import="uk.ac.dundee.computing.tjn.instagrim.models.PostModel"%>
<%@page import="uk.ac.dundee.computing.tjn.instagrim.stores.SessionStore"%>
<%@page import="com.datastax.driver.core.Cluster"%>
<%@page import="uk.ac.dundee.computing.tjn.instagrim.lib.CassandraHosts"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.LinkedList"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
    <%@ include file="../res/jsp/header.jsp" %>
    <body>
        <%@ include file="../res/jsp/navigation.jsp" %>
        <div class="container">
            <div class="row">
                <div class="col-lg-12 text-center user-profile">
                    <div class="col-lg-4 text-center">
                        <%                    SessionStore sessionStore = (SessionStore) request.getAttribute("LoggedIn");
                            ProfileStore profileStore = (ProfileStore) request.getAttribute("Profile");%>
                    </div>
                    <div class="col-lg-8 text-center">
                        <h1><%=profile.getUsername()%></h1>
                        <h3><%=profile.getFirstName()%> <%=profile.getLastName()%></h3>
                    </div>
                </div> <%Cluster cluster = CassandraHosts.getCluster();
                    PostModel post = new PostModel();
                    LinkedList<PostStore> posts = post.getUsersPosts(profile.getUsername());
                    if (posts == null) {
                %>
                <p>No posts found!</p>
                <%} else {
                    Iterator<PostStore> iterator = posts.iterator();
                    while (iterator.hasNext()) {
                        PostStore ps = (PostStore) iterator.next();
                %>
                <div class="col-sm-4 text-center">
                    <a href="/Instagrim/profile/<%=profile.getUsername()%>/<%=ps.getPostID()%>">Direct Link</a>
                    <div class="user-post" style="background-image: url('/Instagrim/image/<%=ps.getPostID()%>')">
                    </div>
                    <%
                        StringBuilder sb = new StringBuilder();
                        String[] split_caption = ps.getCaption().split(" ");
                        for (String word : split_caption) {
                            if (word.startsWith("#")) {
                                String url = "<a href='/Instagrim/search/" + word.replaceAll("#", "") + "'>" + word + "</a>";
                                word = url;
                            }
                            if (word.startsWith("@")) {
                                String url = "<a href='/Instagrim/profile/" + word.replaceAll("@", "") + "'>" + word + "</a>";
                                word = url;
                            }
                            sb.append(word + " ");
                        }
                    %>
                    <%=sb.toString()%>
                </div>
                <%
                        }
                    }
                %>
            </div>
        </div>
    </body>
</html>
