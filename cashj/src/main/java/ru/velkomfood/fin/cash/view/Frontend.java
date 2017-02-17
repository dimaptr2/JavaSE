package ru.velkomfood.fin.cash.view;

import com.sap.conn.jco.JCoException;
import ru.velkomfood.fin.cash.controller.CacheEngine;
import ru.velkomfood.fin.cash.controller.DbManager;
import ru.velkomfood.fin.cash.controller.SapSniffer;
import ru.velkomfood.fin.cash.model.Receipt;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dpetrov on 10.02.2017.
 */
@WebServlet( urlPatterns = "/cj")
public class Frontend extends HttpServlet {

    private PageGenerator pageGenerator = PageGenerator.getInstance();
    private SapSniffer sapSniffer;
    private CacheEngine cache;
    private DbManager dbManager;

    // setters and getters

    public void setSapSniffer(SapSniffer sapSniffer) {
        this.sapSniffer = sapSniffer;
    }

    public void setCache(CacheEngine cache) {
        this.cache = cache;
    }

    public void setDbManager(DbManager dbManager) {
        this.dbManager = dbManager;
    }

    // Here is a MAIN BUSINESS LOGIC

    // GET request
    @Override
    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws ServletException, IOException {

        Map<String, Object> pageVariables = createPageVariablesMap(request);

        response.getWriter().println(pageGenerator.getPage("index.html", pageVariables));
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);

    }

    // POST request
    @Override
    public void doPost(HttpServletRequest request,
                       HttpServletResponse response) throws ServletException, IOException {

        Map<String, Object> pageVariables = createPageVariablesMap(request);

        response.setContentType("text/html;charset=utf-8");

        String textDate;
        String[] fromDate = String.valueOf(pageVariables.get("from-date")).split("-");

        if (fromDate.length == 3) {
            textDate = createDateParameter(fromDate);
        } else {
            textDate = "";
        }

//        String command = String.valueOf(pageVariables.get("submit"));
        try {
            sapSniffer.readCreditSlips(textDate, textDate);
        } catch (JCoException jcoe) {
            jcoe.printStackTrace();
        }

        cache.buildReceipt();
        cache.calculateReceiptSums(null);

        List<OutputHeader> heads = createOutputHeaders(cache.getReceipts());

        pageVariables.put("heads", heads);

        response.getWriter().println(pageGenerator.getPage("output.html", pageVariables));

    }

    // End of MAIN BUSINESS LOGIC

    // Additional methods

    private static Map<String, Object> createPageVariablesMap(HttpServletRequest request) {

        Map<String, Object> pageVariables = new HashMap<>();

        pageVariables.put("from-date", request.getParameter("from-date"));
        pageVariables.put("submit", request.getParameter("submit"));

        return pageVariables;
    }

    // Create a string parameter
    private static String createDateParameter(String[] values) {
        StringBuilder sb = new StringBuilder(0);
        sb.append(values[0]).append(values[1]).append(values[2]);
        return sb.toString();
    }

    // Build the output list
    private static List<OutputHeader> createOutputHeaders(List<Receipt> receipts) {

        List<OutputHeader> headers = new ArrayList<>();

        receipts.forEach(line -> {
            OutputHeader oh = new OutputHeader();
            oh.setId(line.getId());
            oh.setPostingNumber(String.valueOf(line.getPostingNumber()));
            oh.setPostingDate(line.getPostingDate());
            oh.setDeliveryId(String.valueOf(line.getDeliveryId()));
            oh.setPositionText(line.getPositionText());
            headers.add(oh);
        });

        return headers;
    }

}
