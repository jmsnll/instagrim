package uk.ac.dundee.computing.tjn.instagrim.servlets;

import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import uk.ac.dundee.computing.tjn.instagrim.lib.CassandraHosts;
import uk.ac.dundee.computing.tjn.instagrim.lib.Convertors;
import uk.ac.dundee.computing.tjn.instagrim.models.UserModel;
import uk.ac.dundee.computing.tjn.instagrim.stores.SessionStore;

@WebServlet(name = "Account", urlPatterns = {"/account", "/account/*"})
public class Account extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String args[] = Convertors.SplitRequestPath(request);
        RequestDispatcher rd = request.getRequestDispatcher("/index.jsp");
        switch (args.length) {
            case 2:
                rd = request.getRequestDispatcher("/accounts/edit.jsp");
                break;
            case 3:
                if (args[2].equals("settings")) {
                    rd = request.getRequestDispatcher("/accounts/settings.jsp");
                }
                if (args[2].equals("edit")) {
                    rd = request.getRequestDispatcher("/accounts/edit.jsp");
                }
                break;
        }
        rd.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        SessionStore sessionStore = (SessionStore) request.getAttribute("LoggedIn");
        if (sessionStore != null && sessionStore.isLoggedIn()) {
            UserModel user = new UserModel(sessionStore.getUsername(), CassandraHosts.getCluster());
            if (user.isTwoFactorEnabled()) {
                user.disableTwoFactor();
            } else {
                user.enableTwoFactor();
                request.setAttribute("base32secret", user.getBase32secret());
            }
        }
    }
}
