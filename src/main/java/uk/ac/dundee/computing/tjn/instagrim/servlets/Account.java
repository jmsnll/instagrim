package uk.ac.dundee.computing.tjn.instagrim.servlets;

import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import uk.ac.dundee.computing.tjn.instagrim.lib.Convertors;

@WebServlet(name = "Account", urlPatterns = {"/account", "/account/settings"})
public class Account extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String args[] = Convertors.SplitRequestPath(request);
        RequestDispatcher rd = request.getRequestDispatcher("/404.jsp");
        switch (args.length) {
            case 2:
                rd = request.getRequestDispatcher("/account/editaccount.jsp");
                break;
            case 3:
                if (args[2].equals("settings")) {
                    rd = request.getRequestDispatcher("/account/settings.jsp");
                }
                break;
        }
        rd.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

}
