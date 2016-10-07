<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <meta name="description" content="Instagrim">
        <meta name="author" content="James Neill">

        <title>Instagrim - Register</title>

        <link href="https://fonts.googleapis.com/css?family=Pacifico" rel="stylesheet">
        <link href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">
        <link href="res/css/style.css" rel="stylesheet">

        <script type="text/javascript" src="https://use.fontawesome.com/0ba8b6a4e2.js"></script>
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js"></script>
        <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js" integrity="sha384-Tc5IQib027qvyjSMfHjOMaLkfuWVxZxUPnCJA7l2mCWNIpG9mGCD8wGNIcPD7Txa" crossorigin="anonymous"></script>
    </head>
    <body>
        <div class="container">
            <div class="row">
                <div class="col-xs-12 col-sm-8 col-md-6 col-sm-offset-2 col-md-offset-3">
                    <form role="form" method="post" action="Register">
                        <%
                            boolean username_taken = false;
                            boolean password_mismatch = false;
                            String message = "";
                            if (request.getAttribute("username_taken") != null) {
                                username_taken = (boolean) request.getAttribute("username_taken");
                            }
                            if (request.getAttribute("password_mismatch") != null) {
                                username_taken = (boolean) request.getAttribute("password_mismatch");
                            }
                            if (request.getAttribute("message") != null) {
                                message = (String) request.getAttribute("message");
                            }
                            if (username_taken) {
                        %>
                        <div class="alert alert-danger alert-dismissable">
                            <button type="button" class="close" data-dismiss="alert" aria-hidden="true">×</button>
                            <strong>Oh snap!</strong> <%=message%>
                        </div>
                        <%
                            }
                        %>
                        <h2>Please Sign Up <small>It's free and always will be.</small></h2>
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
                        <%
                            if (username_taken) {
                        %>
                        <div class="form-group">
                            ALREADY TAKEN: <input type="text" name="username" id="username" class="form-control input-lg" placeholder="Username" tabindex="3">
                        </div>
                        <%
                        } else {
                        %>
                        <div class="form-group">
                            <input type="text" name="username" id="username" class="form-control input-lg" placeholder="Username" tabindex="3">
                        </div>
                        <%}%>
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
