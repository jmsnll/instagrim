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
                    <form role="form" method="post" action="register">
                        <%
                            boolean username_taken = false;
                            boolean password_mismatch = false;
                            String message = "";
                            if (request.getAttribute("username_taken") != null) {
                                username_taken = (boolean) request.getAttribute("username_taken");
                            }
                            if (request.getAttribute("password_mismatch") != null) {
                                password_mismatch = (boolean) request.getAttribute("password_mismatch");
                            }
                            if (request.getAttribute("message") != null) {
                                message = (String) request.getAttribute("message");
                            }
                            if (username_taken || password_mismatch) {
                        %>
                        <div class="alert alert-danger alert-dismissable">
                            <button type="button" class="close" data-dismiss="alert" aria-hidden="true">×</button>
                            <strong>Oh snap!</strong> <%=message%>
                        </div>
                        <%
                            }
                        %>
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
                        <div class="form-group">
                            <input type="text" name="username" id="username" class="form-control input-lg" placeholder="Username" tabindex="3">
                        </div>
                        <div class="form-group">
                            <input type="email" name="email" id="email" class="form-control input-lg" placeholder="Email Address" tabindex="4">
                        </div>
                        <div class="row">
                            <div class="col-xs-12 col-sm-6 col-md-6">
                                <div class="form-group">
                                    <input type="password" name="password" id="password" class="form-control input-lg" placeholder="Password" tabindex="5">
                                </div>
                            </div>
                            <div class="col-xs-12 col-sm-6 col-md-6">
                                <div class="form-group">
                                    <input type="password" name="password_confirmation" id="password_confirmation" class="form-control input-lg" placeholder="Confirm Password" tabindex="6">
                                </div>
                            </div>
                        </div>
                        <hr>
                        <div class="row">
                            <div class="col-xs-12 col-md-6"><a href="login.jsp" class="btn btn-primary btn-block btn-lg">Sign In</a></div>
                            <div class="col-xs-12 col-md-6"><input type="submit" method="post" value="Register" class="btn btn-success btn-block btn-lg" tabindex="7"></div>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </body>
</html>
