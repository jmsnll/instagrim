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
            <p>Two factor authentication is enabled. Please scan the barcode below in your authenticator app.</p>
            <img src='https://chart.googleapis.com/chart?cht=qr&chl=<%=user.getBase32secret()%>&chs=180x180&choe=UTF-8&chld=L|2' alt=''>
            <p>Or enter your key: <em><%=user.getBase32secret()%></em></p>
            <form action="account" method="POST">
                <input type="button" value="Enable">
            </form>
            <%
            } else {
            %>
            <p>Two factor authentication is disabled.</p>
            <form action="account" method="POST">
                <input type="button" value="Enable">
            </form>
            <%
                    }
                }
            %>
        </form>
    </body>
</html>
