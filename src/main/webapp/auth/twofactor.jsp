<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
    <%@include file="../res/jsp/header.jsp" %>
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