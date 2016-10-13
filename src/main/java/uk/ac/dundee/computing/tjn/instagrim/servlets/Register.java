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

@WebServlet(name = "Register", urlPatterns = {"/register"})
public class Register extends HttpServlet {

    Cluster cluster = null;

    @Override
    public void init(ServletConfig config) throws ServletException {
        cluster = CassandraHosts.getCluster();
    }

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

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String password_confirmation = request.getParameter("password_confirmation");
        String first_name = request.getParameter("first_name");
        String last_name = request.getParameter("last_name");
        String email = request.getParameter("email");

        RequestDispatcher rd = request.getRequestDispatcher("/auth/register.jsp");

        if (UserModel.exists(username, cluster)) {
            request.setAttribute("username_taken", true);
            request.setAttribute("message", "Looks like that username is already taken, please try again.");
            rd.forward(request, response);
            return;
        }
        if (!password.equals(password_confirmation)) {
            request.setAttribute("password_mismatch", true);
            request.setAttribute("message", "Passwords do not match, please try again.");
            rd.forward(request, response);
            return;
        }
        UserModel user = new UserModel(username, password, email, first_name, last_name, cluster);
        user.register();
        rd.forward(request, response);
        response.sendRedirect("/Instagrim");
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }
}
