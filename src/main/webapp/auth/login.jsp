<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
    <%@include file="../res/jsp/header.jsp" %>
    <body>
        <div class="container">
            <h1 id="title-big" class="text-center"><a href="/Instagrim">Instagrim</a></h1>
            <br>
            <div class="row">
                <div class="col-xs-12 col-sm-8 col-md-6 col-sm-offset-2 col-md-offset-3">
                    <form role="form" method="post" action="login">
                        <%
                            boolean login_fail = false;
                            String message = "";
                            if (request.getAttribute("login_fail") != null) {
                                login_fail = (boolean) request.getAttribute("login_fail");
                            }
                            if (request.getAttribute("message") != null) {
                                message = (String) request.getAttribute("message");
                            }
                            if (login_fail) {
                        %>
                        <div class="alert alert-danger alert-dismissable">
                            <button type="button" class="close" data-dismiss="alert" aria-hidden="true">×</button>
                            <p class="text-center"><%=message%></p>
                        </div>
                        <%
                            }
                        %>
                        <fieldset>
                            <hr>
                            <div class="form-group has-feedback">
                                <input type="text" name="username" id="username" class="form-control input-lg" placeholder="Username or Email Address">
                                <i class="fa fa-user form-control-feedback"></i>
                            </div>
                            <div class="form-group has-feedback">
                                <input type="password" name="password" id="password" class="form-control input-lg" placeholder="Password">
                                <i class="fa fa-lock form-control-feedback">
                                </i>
                            </div>
                            <hr style="clear: both">
                            <div class="row">
                                <div class="col-xs-6 col-sm-6 col-md-6">
                                    <input type="submit" method="post" class="btn btn-lg btn-success btn-block" value="Sign In">
                                </div>
                                <div class="col-xs-6 col-sm-6 col-md-6">
                                    <a href="register" class="btn btn-lg btn-primary btn-block">Register</a>
                                </div>
                            </div>
                        </fieldset>
                    </form>
                </div>
            </div>
        </div>
    </body>
</html>