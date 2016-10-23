package uk.ac.dundee.computing.tjn.instagrim.servlets;

import java.io.IOException;
import java.util.LinkedList;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import uk.ac.dundee.computing.tjn.instagrim.lib.Convertors;
import uk.ac.dundee.computing.tjn.instagrim.models.PostModel;
import uk.ac.dundee.computing.tjn.instagrim.stores.PostStore;

/**
 *
 * @author James Neill
 */
@WebServlet(name = "Search", urlPatterns = {"/search", "/search/*"})
public class Search extends HttpServlet {

    /**
     *
     * @param request
     * @param response
     *
     * @throws ServletException
     * @throws IOException
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // get the request path
        // /Instagrim/search/<query>
        String args[] = Convertors.SplitRequestPath(request);
        // search the posts using the search parameters
        LinkedList<PostStore> results = PostModel.searchPosts(args[2]);
        // get the current session
        HttpSession session = request.getSession();
        // store the results in the session
        session.setAttribute("results", results);
        // forward to the results page
        RequestDispatcher rd = request.getRequestDispatcher("/views/viewresults.jsp");
        rd.forward(request, response);
    }
}
