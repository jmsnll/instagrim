<%@page import="uk.ac.dundee.computing.tjn.instagrim.stores.ProfileStore"%>
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
    <%@ include file="../res/jsp/header.jsp" %>
    <body>
        <%@ include file="../res/jsp/navigation.jsp" %>
        <div class="container">
            <div class="row">
                <div class="col-lg-12 text-center user-profile">
                    <div class="col-lg-4 text-center">
                        <form method="POST" enctype="multipart/form-data" action="../profile">
                            File to upload: <input type="file" name="profile-pic"><br/>

                            <br/>
                            <input type="submit" value="Press"> to upload the file!
                        </form>
                    </div>
                    <div class="col-lg-8 text-center">

                    </div>
                </div>
                <div class="col-sm-4 text-center">
                    <div class="user-post" style="background-color:red;">
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>
