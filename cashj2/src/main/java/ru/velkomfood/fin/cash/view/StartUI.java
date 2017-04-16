package ru.velkomfood.fin.cash.view;

import com.google.gson.Gson;
import com.sap.conn.jco.JCoException;
import drvfr_print.jpos.res.JVelPrinter;
import ru.velkomfood.fin.cash.controller.ErpRequestor;
import ru.velkomfood.fin.cash.controller.PrintEngine;
import ru.velkomfood.fin.cash.model.CashDoc;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dpetrov on 10.03.2017.
 */
@WebServlet(urlPatterns = "/cj")
public class StartUI extends HttpServlet {

    private PageGenerator pageGenerator = PageGenerator.getInstance();
    private ErpRequestor erpRequestor = ErpRequestor.getInstance();
    private PrintEngine printEngine = PrintEngine.getInstance();
    private List<CashDoc> result;

    @Override
    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws ServletException, IOException {

        Map<String, Object> pageVariables = createPageVariablesMap(request);
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().println(pageGenerator.getPage("index.html", pageVariables));

    } // end of doGet

    @Override
    public void doPost(HttpServletRequest request,
                       HttpServletResponse response) throws ServletException, IOException {

//        response.setContentType("text/html;charset=utf-8");
        response.setContentType("application/json;charset=utf-8");
        String buttonCode = "";
        boolean hasError = false;

        Map<String, Object> pageVariables = createPageVariablesMap(request);
        String atDate = String.valueOf(pageVariables.get("atDate"));

        // Get values from buttons
        if (pageVariables.get("btnRead") != null) {
            buttonCode = String.valueOf(pageVariables.get("btnRead"));
        }

        if (pageVariables.get("btnHistory") != null) {
            buttonCode = String.valueOf(pageVariables.get("btnHistory"));
        }

        if (pageVariables.get("btnZ") != null) {
            buttonCode = String.valueOf(pageVariables.get("btnZ"));
        }

        switch (buttonCode) {
            case "READ":
                try {
                    erpRequestor.initSAPconnection();
                    erpRequestor.getCashDocs(atDate);
                    result = erpRequestor.getHeads();
                    if (result == null || result.isEmpty()) {
                        hasError = true;
                    }
                } catch (JCoException jex) {
                    jex.printStackTrace();
                }
                break;
            case "HISTORY":
                break;
            case "ZREPORT":
                try {
                    printEngine.openDevice();
                    JVelPrinter printer = printEngine.getPrinter();
                    printer.connect();
                    printer.cancelCurReceipt();
                    printer.reportZ();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }

        // Return the response status
        if (hasError) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        } else {
            response.setStatus(HttpServletResponse.SC_OK);
        }

        Gson gson = new Gson();
        String json = gson.toJson(result);
        // Create the response in the JSON format
        response.getWriter().println(json);
//        String pageName = "index.html";
        // response in the HTML form
//        response.getWriter().println(pageGenerator.getPage("index.html", pageVariables));

    } // end of doPost

    private Map<String, Object> createPageVariablesMap(HttpServletRequest request) {
        Map<String, Object> pageVariables = new HashMap<>();
        String[] strDate = request.getParameter("atDate").split("-");
        pageVariables.put("year", strDate[2]);
        pageVariables.put("month", strDate[1]);
        pageVariables.put("day", strDate[0]);
        pageVariables.put("atDate", request.getParameter("atDate"));
        pageVariables.put("btnRead", request.getParameter("btnRead"));
        pageVariables.put("btnHistory", request.getParameter("btnHistory"));
        pageVariables.put("btnZ", request.getParameter("btnZ"));
        pageVariables.put("result", result);
        return pageVariables;
    }

}
