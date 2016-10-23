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

/**
 *
 * @author James Neill
 */
@WebServlet(name = "Account", urlPatterns = {"/account", "/account/*"})
public class Account extends HttpServlet {

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
        // split the request path
        String args[] = Convertors.SplitRequestPath(request);
        RequestDispatcher rd = request.getRequestDispatcher("/index.jsp");
        // switch according to the length of the array
        switch (args.length) {
            // /Instagrim/account/
            case 2:
                rd = request.getRequestDispatcher("/accounts/edit.jsp");
                break;
            // /Instagrim/account/*
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
        // Gets the current session store attribute 'LoggedIn'
        SessionStore sessionStore = (SessionStore) request.getAttribute("LoggedIn");
        // if the session store isn't null and someone is currently logged in
        if (sessionStore != null && sessionStore.isLoggedIn()) {
            // Get current user's information.
            UserModel user = new UserModel(sessionStore.getUsername(), CassandraHosts.getCluster());
            // if they have two factor enabled
            if (user.isTwoFactorEnabled()) {
                // disable it
                user.disableTwoFactor();
            } else {
                // otherwise enable it
                user.enableTwoFactor();
                // and set the 'base32secret' to their two-factor authentication key
                request.setAttribute("base32secret", user.getBase32secret());
            }
        }
    }
}
