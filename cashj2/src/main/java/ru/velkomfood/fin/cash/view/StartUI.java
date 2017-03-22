package ru.velkomfood.fin.cash.view;

import com.sap.conn.jco.JCoException;
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
 * Created by dpetrov on 10.03.2017.
 */
@WebServlet(urlPatterns = "/cj")
public class StartUI extends HttpServlet {

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
        String buttonCode = "";
        boolean hasError = false;

        Map<String, Object> pageVariables = createPageVariablesMap(request);
        String atDate = String.valueOf(pageVariables.get("atDate"));
        String[] temp = atDate.split(".");

        if (!pageVariables.get("btnOk").equals("")) {
            buttonCode = String.valueOf(pageVariables.get("btnOk"));
        }

        if (!buttonCode.equals("")) {
            try {
                erpRequestor.initSAPconnection();
            } catch (JCoException jex) {
                jex.printStackTrace();
            }
        }

        // Return the response status
        if (hasError) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        } else {
            response.setStatus(HttpServletResponse.SC_OK);
        }

        // Create the response in the JSON format
        response.getWriter().println(pageGenerator.getPage("print.html", pageVariables));

    } // end of doPost

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
