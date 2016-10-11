<nav class="navbar navbar-inverse navbar-fixed-top" role="navigation">
    <div class="container">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="/Instagrim/" id="title">Instagrim</a>
        </div>
        <div class="collapse navbar-collapse navbar-right" id="bs-example-navbar-collapse-1">
            <ul class="nav navbar-nav">
                <li class="dropdown">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false"><i class="fa fa-circle-o" aria-hidden="true"></i></a>
                    <ul class="dropdown-menu">
                        <%
                            SessionStore currentSession = (SessionStore) session.getAttribute("LoggedIn");
                            ProfileStore profile = (ProfileStore) session.getAttribute("Profile");
                            if (currentSession != null && currentSession.isLoggedIn()) {
                        %>
                        <li>
                        <li><a href="">My Account</a>
                        </li>
                        <li role="separator" class="divider"></li>
                        <li><a href="#">Settings</a>
                        </li>
                        <li role="separator" class="divider"></li>
                        <li><a href="#">Logout</a>
                        </li>
                        <%} else {
                        %>
                        <li><a href="/Instagrim/login.jsp">Sign In</a>
                        </li>
                        <li><a href="/Instagrim/register.jsp">Register</a>
                        </li>
                        <%
                            }
                        %>
                    </ul>
                </li>
                <li class="dropdown" id="notifications">
                    <a href="" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false"><i class="fa fa-bell-o" aria-hidden="true"></i></a>
                    <ul class="dropdown-menu">

                    </ul>
                </li>
                <li>
                    <a href="/Instagrim/upload.jsp"><l class="fa fa-plus-square-o"></l></a>
                </li>
            </ul>
        </div>
    </div>
</nav>