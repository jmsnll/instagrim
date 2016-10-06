<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <meta name="description" content="Instagrim">
        <meta name="author" content="James Neill">

        <title>Instagrim - Login</title>

        <link href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">
        <link href="res/css/style.css" rel="stylesheet">

        <script type="text/javascript" src="https://use.fontawesome.com/0ba8b6a4e2.js"></script>
        <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js" integrity="sha384-Tc5IQib027qvyjSMfHjOMaLkfuWVxZxUPnCJA7l2mCWNIpG9mGCD8wGNIcPD7Txa" crossorigin="anonymous"></script>
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js"></script>
    </head>
    <body>
        <div class="container">
            <div class="row">
                <div class="col-xs-12 col-sm-8 col-md-6 col-sm-offset-2 col-md-offset-3">
                    <form role="form" method="post" action="Login">
                        <fieldset>
                            <h2>Please Sign In</h2>
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
                                    <a href="register.jsp" class="btn btn-lg btn-primary btn-block">Register</a>
                                </div>
                            </div>
                        </fieldset>
                    </form>
                </div>
            </div>
        </div>
    </body>
</html>