package uk.ac.dundee.computing.tjn.instagrim.servlets;

import com.datastax.driver.core.Cluster;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import uk.ac.dundee.computing.tjn.instagrim.lib.CassandraHosts;
import uk.ac.dundee.computing.tjn.instagrim.lib.Convertors;
import uk.ac.dundee.computing.tjn.instagrim.models.CommentModel;
import uk.ac.dundee.computing.tjn.instagrim.models.PostModel;
import uk.ac.dundee.computing.tjn.instagrim.models.UserModel;
import uk.ac.dundee.computing.tjn.instagrim.stores.ProfileStore;
import uk.ac.dundee.computing.tjn.instagrim.stores.SessionStore;

/**
 *
 * @author James Neill
 */
@WebServlet(urlPatterns = {"/profile", "/profile/*"})
@MultipartConfig

public class Profile extends HttpServlet {

    private Cluster cluster;

    /**
     *
     */
    public Profile() {
        super();
    }

    /**
     *
     * @param config
     *
     * @throws ServletException
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        this.cluster = CassandraHosts.getCluster();
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
        String args[] = Convertors.SplitRequestPath(request);
        switch (args.length) {
            // instagrim/profile
            case 2:
                displayCurrentProfile(request, response);
                break;
            // instagrim/profile/<username>
            case 3:
                if (args[2].equals("create")) {
                    createProfile(request, response);
                } else {
                    displayUser(args[2], request, response);
                }
                break;
            // instagrim/profile/<username>/<postid>
            case 4:
                displayPost(UUID.fromString(args[3]), request, response);
                break;
        }
    }

    private void createProfile(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // get the current session and request dispatcher for the create profile page
        RequestDispatcher rd = request.getRequestDispatcher("/profiles/createprofile.jsp");
        HttpSession session = request.getSession();
        SessionStore sessionStore = (SessionStore) request.getAttribute("LoggedIn");
        // if currently logged in
        if (sessionStore.isLoggedIn()) {
            // get the user's information and store it in the session
            UserModel user = new UserModel(sessionStore.getUsername(), cluster);
            session.setAttribute("user", user);
        }
        // forward the request and response
        rd.forward(request, response);
    }

    private void displayPost(UUID postID, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // get the current session and request dispatcher for the view post page
        RequestDispatcher rd = request.getRequestDispatcher("/views/viewpost.jsp");
        HttpSession session = request.getSession();
        // if the post exists
        if (PostModel.exists(postID)) {
            // get the post's information and store it in the session
            PostModel post = new PostModel(postID);
            session.setAttribute("post", post);
            // forward the request and response
            rd.forward(request, response);
        } else {
            // otherwise display an error
            displayError(request, response);
        }
    }

    private void displayUser(String username, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // get the current session and request dispatcher for the view profile page
        RequestDispatcher rd = request.getRequestDispatcher("/profiles/viewprofile.jsp");
        HttpSession session = request.getSession();
        // if the profile requested exists
        if (UserModel.exists(username, cluster)) {
            // load the information and store it in the session
            UserModel user = new UserModel(username, cluster);
            ProfileStore profile = new ProfileStore(user);
            session.setAttribute("Profile", profile);
            // forward the request and response
            rd.forward(request, response);
        } else {
            // otherwise display an error
            displayError(request, response);
        }
    }

    private void displayCurrentProfile(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // get the current session and request dispatcher for the view profile page
        RequestDispatcher rd = request.getRequestDispatcher("/profiles/viewprofile.jsp");
        HttpSession session = request.getSession();
        // get the session store
        SessionStore sessionStore = (SessionStore) session.getAttribute("LoggedIn");
        response.sendRedirect("/Instagrim/profile/" + sessionStore.getUsername());
    }

    private void displayError(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // get the current session and request dispatcher for the view profile page
        RequestDispatcher rd = request.getRequestDispatcher("/profiles/viewprofile.jsp");
        HttpSession session = request.getSession();
        // set the user not found attribute to true
        session.setAttribute("UserNotFound", true);
        // forward the request and response
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
        String args[] = Convertors.SplitRequestPath(request);
        if (args.length == 4) {
            // Get the current session and session store
            HttpSession session = request.getSession();
            SessionStore sessionStore = (SessionStore) session.getAttribute("LoggedIn");
            // get the caption from the page
            String caption = request.getParameter("comment");
            // get the username of the currently logged in user
            String username = sessionStore.getUsername();
            // /Instagrim/profile/<username>/<uuid>
            UUID postID = UUID.fromString(args[3]);

            // Create a new comment
            CommentModel commentModel = new CommentModel(cluster);
            commentModel.addComment(postID, username, caption);
            response.sendRedirect("/Instagrim/profile/" + username + "/" + postID);
        } else {
            // foreach Part part in the request
            for (Part part : request.getParts()) {
                // Get the type of the content and an input stream
                String type = part.getContentType();
                InputStream is = request.getPart(part.getName()).getInputStream();
                int i = is.available();

                // Get the current session and session store
                HttpSession session = request.getSession();
                SessionStore sessionStore = (SessionStore) session.getAttribute("LoggedIn");
                // if someone is currently logged in
                if (sessionStore.isLoggedIn()) {
                    // get their username
                    UserModel user = new UserModel(sessionStore.getUsername(), CassandraHosts.getCluster());
                    // if there are bytes available
                    if (i > 0) {
                        // read the bytes and store them in b
                        byte[] b = new byte[i + 1];
                        is.read(b);
                        user.setProfilePicture(sessionStore.getUsername(), b, type, i);
                        // Close the input stream
                        is.close();
                    }
                }
                response.sendRedirect("/Instagrim/profile/");
            }
        }
    }
}
