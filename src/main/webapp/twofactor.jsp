<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <meta name="description" content="Instagrim">
        <meta name="author" content="James Neill">

        <title>Two-Step - Instagrim</title>

        <link href="https://fonts.googleapis.com/css?family=Pacifico" rel="stylesheet">
        <link href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">
        <link href="res/css/style.css" rel="stylesheet">

        <script type="text/javascript" src="https://use.fontawesome.com/0ba8b6a4e2.js"></script>
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js"></script>
        <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js" integrity="sha384-Tc5IQib027qvyjSMfHjOMaLkfuWVxZxUPnCJA7l2mCWNIpG9mGCD8wGNIcPD7Txa" crossorigin="anonymous"></script>
    </head>
    <body>
        <div class="container">
            <h1 id="title-big" class="text-center"><a href="/Instagrim">Instagrim</a></h1>
            <br>
            <div class="row">
                <div class="col-xs-4 col-sm-4 col-md-4 col-sm-offset-4 col-md-offset-4">
                    <form role="form" method="post" action="login">
                        <fieldset>
                            <hr>
                            <div class="form-group has-feedback">
                                <input autocomplete="off" type="text" name="code" id="code" maxlength="6" class="form-control input-lg" placeholder="2-Factor Code">
                                <i class="fa fa-lock form-control-feedback"></i>
                            </div>
                            <hr style="clear: both">
                            <div class="row">
                                <div class="col-xs-12 col-sm-12 col-md-12">
                                    <input type="submit" method="post" class="btn btn-lg btn-success btn-block" value="Submit">
                                </div>
                            </div>
                        </fieldset>
                    </form>
                </div>
            </div>
        </div>
    </body>
</html>