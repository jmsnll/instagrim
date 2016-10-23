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
@WebServlet(name = "Register", urlPatterns = {"/register"})
public class Register extends HttpServlet {

    Cluster cluster = null;

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
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        SessionStore sessionStore = (SessionStore) session.getAttribute("LoggedIn");
        if (sessionStore != null && sessionStore.isLoggedIn()) {
            response.sendRedirect("/Instagrim/");
            return;
        } else {
            RequestDispatcher rd = request.getRequestDispatcher("/auth/register.jsp");
            rd.forward(request, response);
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // get all of the submitted information
        String username = request.getParameter("username").toLowerCase();
        String password = request.getParameter("password");
        String password_confirmation = request.getParameter("password_confirmation");
        String first_name = request.getParameter("first_name");
        String last_name = request.getParameter("last_name");
        String email = request.getParameter("email").toLowerCase();

        RequestDispatcher rd = request.getRequestDispatcher("/auth/register.jsp");

        // if the user already exists
        if (UserModel.exists(username, cluster)) {
            // set the error message return to the page
            request.setAttribute("username_taken", true);
            request.setAttribute("message", "Looks like that username is already taken, please try again.");
            rd.forward(request, response);
            return;
        }
        // if the password doesn't match the one in the database
        if (!password.equals(password_confirmation)) {
            // set the error message return to the page
            request.setAttribute("password_mismatch", true);
            request.setAttribute("message", "Passwords do not match, please try again.");
            rd.forward(request, response);
            return;
        }
        // otherwise create a new user with the information provided
        UserModel user = new UserModel(username, password, email, first_name, last_name, cluster);
        // register them
        user.register();
        // return to the homepage
        response.sendRedirect("/Instagrim");
    }
}
