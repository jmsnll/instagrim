<%@page import="uk.ac.dundee.computing.tjn.instagrim.lib.CassandraHosts"%>
<%@page import="uk.ac.dundee.computing.tjn.instagrim.models.UserModel"%>
<%@page import="uk.ac.dundee.computing.tjn.instagrim.stores.ProfileStore"%>
<%@page import="uk.ac.dundee.computing.tjn.instagrim.stores.SessionStore"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <%@include file="../res/jsp/header.jsp" %>
    <body>
        <%@include file="../res/jsp/navigation.jsp" %>
        <h1>Settings</h1>
        <form role="form" method="post" action="account">
            <%                SessionStore sessionStore = (SessionStore) request.getAttribute("LoggedIn");
                UserModel user;
                if (sessionStore != null && sessionStore.isLoggedIn()) {
                    user = new UserModel(sessionStore.getUsername(), CassandraHosts.getCluster());
                    if (user.isTwoFactorEnabled()) {
            %>


            <%
                    }
                }
            %>
        </form>
    </body>
</html>
