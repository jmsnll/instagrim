<%@page import="uk.ac.dundee.computing.tjn.instagrim.stores.CommentStore"%>
<%@page import="java.util.UUID"%>
<%@page import="uk.ac.dundee.computing.tjn.instagrim.models.CommentModel"%>
<%@page import="uk.ac.dundee.computing.tjn.instagrim.stores.ProfileStore"%>
<%@page import="uk.ac.dundee.computing.tjn.instagrim.models.PostModel"%>
<%@page import="com.datastax.driver.core.Cluster"%>
<%@page import="uk.ac.dundee.computing.tjn.instagrim.lib.CassandraHosts"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.LinkedList"%>
<%@page import="uk.ac.dundee.computing.tjn.instagrim.stores.PostStore"%>
<%@page import="uk.ac.dundee.computing.tjn.instagrim.stores.SessionStore"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
    <%@ include file="../res/jsp/header.jsp" %>
    <body>
        <div class="container">
            <%@ include file="../res/jsp/navigation.jsp" %>
            <%            PostModel post = (PostModel) session.getAttribute("post");
                CommentModel commentModel = new CommentModel(CassandraHosts.getCluster());
                if (post != null) {
                    LinkedList<CommentStore> comments = commentModel.getComments(post.getPostID());

            %>
            <div class="col-sm-8 text-center">
                <div class="user-post" style="background-image: url('/Instagrim/image/<%=post.getPostID()%>')">
                </div>
                <div class="comments">
                    <%
                        if (comments.size() == 0) {
                    %><p>No comments found!<%
                    } else {
                        for (CommentStore comment : comments) {
                        %>
                    <div>
                        <p><%=comment.getUsername()%></p>
                        <p><%=comment.getPosted().toString()%></p>
                        <p><%=comment.getCaption()%></p>
                    </div>
                    <%
                        }
                    %>
                    <%
                        }
                    %>
                    <form role="form" method="post">
                        <hr>
                        <div class="form-group">
                            <input type="text" name="comment" id="comment" class="form-control input-md" placeholder="Comment" tabindex="1">
                        </div>
                        <div class="row">
                            <div class="col-xs-12 col-md-12"><input type="submit" method="post" value="Post" class="btn btn-success btn-block btn-md" tabindex="2"></div>
                        </div>
                    </form>
                </div>
            </div>
            <%
                }
            %>
        </div>
    </body>
</html>
