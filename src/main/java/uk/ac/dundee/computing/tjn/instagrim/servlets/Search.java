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

@WebServlet(name = "Search", urlPatterns = {"/search", "/search/*"})
public class Search extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String args[] = Convertors.SplitRequestPath(request);
        LinkedList<PostStore> results = PostModel.searchPosts(args[2]);
        HttpSession session = request.getSession();
        session.setAttribute("results", results);
        RequestDispatcher rd = request.getRequestDispatcher("/views/viewresults.jsp");
        rd.forward(request, response);
    }
}
