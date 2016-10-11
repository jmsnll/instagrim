package uk.ac.dundee.computing.tjn.instagrim.servlets;

import com.datastax.driver.core.Cluster;
import java.io.IOException;
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

@WebServlet(name = "Login", urlPatterns = {"/Login", "/Login/*"})
public class Login extends HttpServlet {

    Cluster cluster = null;

    @Override
    public void init(ServletConfig config) throws ServletException {
        // TODO Auto-generated method stub
        cluster = CassandraHosts.getCluster();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (!UserModel.Exists(username, cluster) || !UserModel.isValidUser(username, password, cluster)) {
            request.setAttribute("login_fail", true);
            request.setAttribute("message", "Invalid username or password, please try again.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        } else {
            HttpSession session = request.getSession();
            SessionStore sessionStore = new SessionStore();
            sessionStore.setUsername(username);
            UserModel user = new UserModel(username, cluster);
            if (user.isTwoFactorEnabled()) {
                sessionStore.setLoggedIn(false);
                session.setAttribute("LoggedIn", sessionStore);
                request.getRequestDispatcher("/twofactor.jsp").forward(request, response);
                return;
            } else {
                sessionStore.setLoggedIn(true);
                session.setAttribute("LoggedIn", sessionStore);
                request.getRequestDispatcher("index.jsp").forward(request, response);
                return;
            }
        }
    }
}
