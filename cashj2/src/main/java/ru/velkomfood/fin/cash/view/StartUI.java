package ru.velkomfood.fin.cash.view;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dpetrov on 10.03.2017.
 */
@WebServlet(urlPatterns = "/cj")
public class StartUI extends HttpServlet {

    private PageGenerator pageGenerator = PageGenerator.getInstance();

    @Override
    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws ServletException, IOException {

        Map<String, Object> pageVariables = createPageVariablesMap(request);
        response.getWriter().println(pageGenerator.getPage("index.html", pageVariables));
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);

    }

    @Override
    public void doPost(HttpServletRequest request,
                       HttpServletResponse response) throws ServletException, IOException {

        Map<String, Object> pageVariables = createPageVariablesMap(request);
        String atDate = String.valueOf(pageVariables.get("at-date"));

        if (!pageVariables.get("btn-read").equals("")) {
            String btnRead = String.valueOf(pageVariables.get("btn-read"));
        }

        if (!pageVariables.get("btn-history").equals("")) {
            String btnHistory = String.valueOf(pageVariables.get("btn-history"));
        }

    }

    private static Map<String, Object> createPageVariablesMap(HttpServletRequest request) {
        Map<String, Object> pageVariables = new HashMap<>();
        pageVariables.put("at-date", request.getParameter("at-date"));
        pageVariables.put("btn-read", request.getParameter("btn-read"));
        pageVariables.put("btn-history", request.getParameter("btn-history"));
        return pageVariables;
    }

}
