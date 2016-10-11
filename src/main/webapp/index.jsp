<%@page import="uk.ac.dundee.computing.tjn.instagrim.models.PostModel"%>
<%@page import="com.datastax.driver.core.Cluster"%>
<%@page import="uk.ac.dundee.computing.tjn.instagrim.lib.CassandraHosts"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.LinkedList"%>
<%@page import="uk.ac.dundee.computing.tjn.instagrim.stores.ImageStore"%>
<%@page import="uk.ac.dundee.computing.tjn.instagrim.stores.SessionStore"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
    <%@include file="res/jsp/header.jsp" %>
    <body>
        <nav class="navbar navbar-inverse navbar-fixed-top" role="navigation">
            <div class="container">
                <div class="navbar-header">
                    <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1">
                        <span class="sr-only">Toggle navigation</span>
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                    </button>
                    <a class="navbar-brand" href="/Instagrim/" id="title">Instagrim</a>
                </div>
                <div class="collapse navbar-collapse navbar-right" id="bs-example-navbar-collapse-1">
                    <ul class="nav navbar-nav">
                        <li class="dropdown">
                            <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false"><i class="fa fa-circle-o" aria-hidden="true"></i></a>
                            <ul class="dropdown-menu">
                                <%
                                    SessionStore sessionStore = (SessionStore) session.getAttribute("LoggedIn");
                                    if (sessionStore != null && sessionStore.isLoggedIn()) {
                                %>
                                <li>
                                <li><a href="">My Account</a>
                                </li>
                                <li role="separator" class="divider"></li>
                                <li><a href="#">Settings</a>
                                </li>
                                <li role="separator" class="divider"></li>
                                <li><a href="#">Logout</a>
                                </li>
                                <%                                } else {
                                %>
                                <li><a href="login.jsp">Sign In</a>
                                </li>
                                <li><a href="register.jsp">Register</a>
                                </li>
                                <%
                                    }
                                %>
                            </ul>
                        </li>
                        <li class="dropdown" id="notifications">
                            <a href="" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false"><i class="fa fa-bell-o" aria-hidden="true"></i></a>
                            <ul class="dropdown-menu">

                            </ul>
                        </li>
                        <li>
                            <a href="upload.jsp"><l class="fa fa-plus-square-o"></l></a>
                        </li>
                    </ul>
                </div>
            </div>
        </nav>
        <div class="container">
            <div class="row">
                <div class="col-lg-12 text-center user-profile">
                    <h1>Welcome to Instagrim!</h1>
                </div>
                <%
                    Cluster cluster = CassandraHosts.getCluster();
                    PostModel post = new PostModel(cluster);
                    LinkedList<ImageStore> images = post.getMostRecentImages();
                    if (images == null) {
                %>
                <p>No posts found!</p>
                <%} else {
                    Iterator<ImageStore> iterator = images.iterator();
                    while (iterator.hasNext()) {
                        ImageStore is = (ImageStore) iterator.next();
                %>
                <div class="col-sm-4 text-center">
                    <div class="user-post" style="background-image: url('/Instagrim/Image/<%=is.getID()%>')">
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
