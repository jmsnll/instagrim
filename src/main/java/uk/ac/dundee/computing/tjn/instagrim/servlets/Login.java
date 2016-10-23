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

@WebServlet(name = "Login", urlPatterns = {"/login", "/login/*"})
public class Login extends HttpServlet {

    private Cluster cluster = null;

    @Override
    public void init(ServletConfig config) throws ServletException {
        cluster = CassandraHosts.getCluster();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getParameter("username") != null) {
            doLogin(request, response);
        } else {
            doTwoFactor(request, response);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        SessionStore sessionStore = (SessionStore) session.getAttribute("LoggedIn");
        if (sessionStore != null && sessionStore.isLoggedIn()) {
            response.sendRedirect("/Instagrim/");
            return;
        } else {
            RequestDispatcher rd = request.getRequestDispatcher("/auth/login.jsp");
            rd.forward(request, response);
        }
    }

    private void doLogin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (!UserModel.exists(username, cluster) || !UserModel.isValidUser(username, password, cluster)) {
            request.setAttribute("login_fail", true);
            request.setAttribute("message", "Invalid username or password, please try again.");
            request.getRequestDispatcher("/auth/login.jsp").forward(request, response);
            return;
        } else {
            HttpSession session = request.getSession();
            SessionStore sessionStore = new SessionStore();
            sessionStore.setUsername(username);
            UserModel user = new UserModel(username, cluster);
            if (user.isTwoFactorEnabled()) {
                sessionStore.setLoggedIn(false);
                session.setAttribute("LoggedIn", sessionStore);
                request.getRequestDispatcher("/auth/twofactor.jsp").forward(request, response);
                return;
            } else {
                sessionStore.setLoggedIn(true);
                session.setAttribute("LoggedIn", sessionStore);
                //request.getRequestDispatcher("/index.jsp").forward(request, response);
                response.sendRedirect("/Instagrim/");
                return;
            }
        }
    }

    private void doTwoFactor(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String code = request.getParameter("code");
        HttpSession session = request.getSession();
        SessionStore sessionStore = (SessionStore) session.getAttribute("LoggedIn");
        UserModel user = new UserModel(sessionStore.getUsername(), cluster);
        if (!user.isValidTwoFactorCode(code)) {
            request.setAttribute("auth_fail", true);
            request.setAttribute("message", "Invalid code, please try again.");
            request.getRequestDispatcher("/auth/twofactor.jsp").forward(request, response);
        } else {
            sessionStore.setLoggedIn(true);
            session.setAttribute("LoggedIn", sessionStore);
            request.getRequestDispatcher("/index.jsp").forward(request, response);

        }
    }
}
