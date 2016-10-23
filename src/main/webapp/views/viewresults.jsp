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
                <%                    Cluster cluster = CassandraHosts.getCluster();

                    LinkedList<PostStore> posts = (LinkedList) session.getAttribute("results");
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
                    <%
                        StringBuilder sb = new StringBuilder();
                        String[] split_caption = ps.getCaption().split(" ");
                        for (String word : split_caption) {
                            if (word.startsWith("#")) {
                                String url = "<a href='/Instagrim/search/" + word.replaceAll("#", "") + "'>" + word + "</a>";
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
