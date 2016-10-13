<%@page import="uk.ac.dundee.computing.tjn.instagrim.stores.ProfileStore"%>
<%@page import="uk.ac.dundee.computing.tjn.instagrim.stores.SessionStore"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <%@include file="../res/jsp/header.jsp" %>
    <body>
        <%@include file="../res/jsp/navigation.jsp" %>
        <div class="container">
            <h1>Edit Account</h1>
            <form role="form" method="post" action="edit">
                <hr>
                <div class="row">
                    <div class="col-xs-12 col-sm-6 col-md-6">
                        <div class="form-group has-feedback">
                            <input type="text" name="first_name" id="first_name" class="form-control input-lg" placeholder="First Name" tabindex="1">
                        </div>
                    </div>
                    <div class="col-xs-12 col-sm-6 col-md-6">
                        <div class="form-group">
                            <input type="text" name="last_name" id="last_name" class="form-control input-lg" placeholder="Last Name" tabindex="2">
                        </div>
                    </div>
                </div>
                <hr>
                <div class="form-group">
                    <input type="email" name="email" id="email" class="form-control input-lg" placeholder="Email Address" tabindex="3">
                </div>
                <div class="form-group">
                    <input type="email" name="email-confirm" id="email" class="form-control input-lg" placeholder="Confirm Email Address" tabindex="4">
                </div>
                <hr>
                <div class="form-group">
                    <input type="password" name="new-password" id="password" class="form-control input-lg" placeholder="Password" tabindex="5">
                </div>
                <div class="form-group">
                    <input type="password" name="new-password-confirm" id="password" class="form-control input-lg" placeholder="Confirm Password" tabindex="6">
                </div>
                <hr>
                <div class="form-group">
                    <input type="password" name="current-password" id="password" class="form-control input-lg" placeholder="Current Password" tabindex="7">
                </div>
                <div class="row">
                    <div class="col-xs-12 col-md-6"><a href="login.jsp" class="btn btn-primary btn-block btn-lg">Sign In</a></div>
                    <div class="col-xs-12 col-md-6"><input type="submit" method="post" value="Register" class="btn btn-success btn-block btn-lg" tabindex="7"></div>
                </div>
        </div>
    </form>
</body>
</html>
