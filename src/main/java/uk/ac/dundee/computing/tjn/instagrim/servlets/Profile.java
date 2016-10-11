package uk.ac.dundee.computing.tjn.instagrim.servlets;

import com.datastax.driver.core.Cluster;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import uk.ac.dundee.computing.tjn.instagrim.lib.CassandraHosts;
import uk.ac.dundee.computing.tjn.instagrim.lib.Convertors;
import uk.ac.dundee.computing.tjn.instagrim.models.UserModel;
import uk.ac.dundee.computing.tjn.instagrim.stores.ProfileStore;
import uk.ac.dundee.computing.tjn.instagrim.stores.SessionStore;

@WebServlet(urlPatterns = {"/profile", "/profile/*"})
@MultipartConfig

public class Profile extends HttpServlet {

    private Cluster cluster;

    public Profile() {
        super();
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        this.cluster = CassandraHosts.getCluster();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String args[] = Convertors.SplitRequestPath(request);
        if (args.length > 2) {
            DisplayUser(args[2], request, response);
        } else {
            DisplayCurrentProfile(request, response);
        }
    }

    private void DisplayUser(String username, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher rd = request.getRequestDispatcher("/profile.jsp");
        HttpSession session = request.getSession();
        if (UserModel.Exists(username, cluster)) {
            UserModel user = new UserModel(username, cluster);
            ProfileStore profile = new ProfileStore(user);

            session.setAttribute("Profile", profile);
            rd.forward(request, response);
        } else {
            DisplayError(request, response);
        }
    }

    private void DisplayCurrentProfile(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher rd = request.getRequestDispatcher("/profie.jsp");
        HttpSession session = request.getSession();
        SessionStore sessionStore = (SessionStore) session.getAttribute("LoggedIn");
        DisplayUser(sessionStore.getUsername(), request, response);
    }

    private void DisplayError(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher rd = request.getRequestDispatcher("/profile.jsp");
        HttpSession session = request.getSession();
        session.setAttribute("UserNotFound", true);
        rd.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
