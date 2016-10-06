package uk.ac.dundee.computing.tjn.instagrim.servlets;

import com.datastax.driver.core.Cluster;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import uk.ac.dundee.computing.tjn.instagrim.lib.CassandraHosts;
import uk.ac.dundee.computing.tjn.instagrim.models.User;
import uk.ac.dundee.computing.tjn.instagrim.stores.SessionStore;

@WebServlet(name = "Login", urlPatterns = {"/Login", "/Login/*"})
public class Login extends HttpServlet {

    Cluster cluster = null;

    @Override
    public void init(ServletConfig config) throws ServletException {
        // TODO Auto-generated method stub
        cluster = CassandraHosts.getCluster();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        boolean isValid = User.isValidUser(username, password, cluster);
        HttpSession session = request.getSession();
        System.out.println("Session in servlet " + session);
        if (isValid) {
            SessionStore ss = new SessionStore();
            ss.setLoggedIn(true);
            ss.setUsername(username);

            session.setAttribute("LoggedIn", ss);
            System.out.println("Session in servlet " + session);
            RequestDispatcher rd = request.getRequestDispatcher("index.jsp");
            rd.forward(request, response);

        } else {
            response.sendRedirect("/Instagrim/login.jsp");
        }

    }
}
