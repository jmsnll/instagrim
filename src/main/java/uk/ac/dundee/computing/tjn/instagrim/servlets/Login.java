package uk.ac.dundee.computing.tjn.instagrim.servlets;

import com.datastax.driver.core.Cluster;
import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import uk.ac.dundee.computing.tjn.instagrim.lib.CassandraHosts;
import uk.ac.dundee.computing.tjn.instagrim.models.UserModel;
import uk.ac.dundee.computing.tjn.instagrim.stores.SessionStore;

/**
 *
 * @author James Neill
 */
@WebServlet(name = "Login", urlPatterns = {"/login", "/login/*"})
public class Login extends HttpServlet {

    private Cluster cluster = null;

    /**
     *
     * @param config
     *
     * @throws ServletException
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        cluster = CassandraHosts.getCluster();
    }

    /**
     *
     * @param request
     * @param response
     *
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // if the username isn't null
        if (request.getParameter("username") != null) {
            // continue with the normal login
            doLogin(request, response);
        } else {
            // otherwise do the two factor login
            doTwoFactor(request, response);
        }
    }

    /**
     *
     * @param request
     * @param response
     *
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Get the current session and session store
        HttpSession session = request.getSession();
        SessionStore sessionStore = (SessionStore) session.getAttribute("LoggedIn");
        // if the sessionstore isn't null and someone is logged in
        if (sessionStore != null && sessionStore.isLoggedIn()) {
            // redirect them to the homepage
            response.sendRedirect("/Instagrim/");
            return;
        } else {
            // otherwise display the login page
            RequestDispatcher rd = request.getRequestDispatcher("/auth/login.jsp");
            rd.forward(request, response);
        }
    }

    private void doLogin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // get the submitted username and password
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        // is the user doesn't exist or the username & password do not match
        if (!UserModel.exists(username, cluster) || !UserModel.isValidUser(username, password, cluster)) {
            // fail the login, set the error message and return to the login page
            request.setAttribute("login_fail", true);
            request.setAttribute("message", "Invalid username or password, please try again.");
            request.getRequestDispatcher("/auth/login.jsp").forward(request, response);
            return;
        } else {
            // otherwise create a new session store and log the user in
            HttpSession session = request.getSession();
            SessionStore sessionStore = new SessionStore();
            sessionStore.setUsername(username);
            UserModel user = new UserModel(username, cluster);
            // if the user has twofactor authentication enabled
            if (user.isTwoFactorEnabled()) {
                // set logged in to false
                sessionStore.setLoggedIn(false);
                session.setAttribute("LoggedIn", sessionStore);
                // display the two factor page
                request.getRequestDispatcher("/auth/twofactor.jsp").forward(request, response);
                return;
            } else {
                // otherwise set logged in to true
                sessionStore.setLoggedIn(true);
                session.setAttribute("LoggedIn", sessionStore);
                // redirect to the homepage
                response.sendRedirect("/Instagrim/");
                return;
            }
        }
    }

    private void doTwoFactor(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // get the submitted two factor code and sessionstore
        String code = request.getParameter("code");
        HttpSession session = request.getSession();
        SessionStore sessionStore = (SessionStore) session.getAttribute("LoggedIn");
        // load the current user
        UserModel user = new UserModel(sessionStore.getUsername(), cluster);
        // if the code isn't valid
        if (!user.isValidTwoFactorCode(code)) {
            // fail the login, set the error message and return to the two factor authentication page
            request.setAttribute("auth_fail", true);
            request.setAttribute("message", "Invalid code, please try again.");
            request.getRequestDispatcher("/auth/twofactor.jsp").forward(request, response);
        } else {
            // otherwise set loggedin to true and return to the homepage
            sessionStore.setLoggedIn(true);
            session.setAttribute("LoggedIn", sessionStore);
            request.getRequestDispatcher("/index.jsp").forward(request, response);

        }
    }
}
