package ru.velkomfood.fin.cash.view;

import ru.velkomfood.fin.cash.controller.ErpRequestor;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dpetrov on 22.03.2017.
 */
@WebServlet("/items")
public class ViewItems extends HttpServlet {

    private PageGenerator pageGenerator = PageGenerator.getInstance();
    private ErpRequestor erpRequestor = ErpRequestor.getInstance();

    @Override
    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws ServletException, IOException {

        Map<String, Object> pageVariables = createPageVariablesMap(request);
        response.getWriter().println(pageGenerator.getPage("index.html", pageVariables));
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);

    } // end of doGet

    @Override
    public void doPost(HttpServletRequest request,
                       HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=utf-8");
    }

    private static Map<String, Object> createPageVariablesMap(HttpServletRequest request) {
        Map<String, Object> pageVariables = new HashMap<>();
        String[] strDate = request.getParameter("atDate").split("-");
        pageVariables.put("year", strDate[2]);
        pageVariables.put("month", strDate[1]);
        pageVariables.put("day", strDate[0]);
        pageVariables.put("atDate", request.getParameter("atDate"));
        pageVariables.put("btnOk", request.getParameter("btnOk"));
        return pageVariables;
    }
}
