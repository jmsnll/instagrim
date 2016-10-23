package uk.ac.dundee.computing.tjn.instagrim.servlets;

import java.io.IOException;
import java.io.InputStream;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
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
        HttpSession session = request.getSession();
        SessionStore sessionStore = (SessionStore) session.getAttribute("LoggedIn");
        if (sessionStore != null && sessionStore.isLoggedIn()) {
            for (Part part : request.getParts()) {
                String type = part.getContentType();

                InputStream is = request.getPart(part.getName()).getInputStream();
                int i = is.available();
                if (i > 0) {
                    byte[] b = new byte[i + 1];
                    is.read(b);
                    String username = sessionStore.getUsername();
                    UserModel user = new UserModel(username, CassandraHosts.getCluster());
                    user.setProfilePicture(username, b, type, b.length);

                    is.close();
                }
                RequestDispatcher rd = request.getRequestDispatcher("/profile/");
                rd.forward(request, response);
            }
        }
//        // if the session store isn't null and someone is currently logged in
//        if (sessionStore != null && sessionStore.isLoggedIn()) {
//            // Get current user's information.
//            UserModel user = new UserModel(sessionStore.getUsername(), CassandraHosts.getCluster());
//            // if they have two factor enabled
//            if (user.isTwoFactorEnabled()) {
//                // disable it
//                user.disableTwoFactor();
//            } else {
//                // otherwise enable it
//                user.enableTwoFactor();
//                // and set the 'base32secret' to their two-factor authentication key
//                request.setAttribute("base32secret", user.getBase32secret());
//            }
//        }
    }
}
