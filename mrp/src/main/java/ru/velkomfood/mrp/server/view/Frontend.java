package ru.velkomfood.mrp.server.view;

import ru.velkomfood.mrp.server.controller.DbReader;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by dpetrov on 19.11.16.
 */

@WebServlet(urlPatterns = "/mrp")
public class Frontend extends HttpServlet {

    // Return a new page (GET)
    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws ServletException, IOException {

        Map<String, Object> pageVariables = createPageVariablesMap(request);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=utf-8");
        response.getWriter().println(PageGenerator.getInstance().getPage("output.html", pageVariables));
        response.setStatus(HttpServletResponse.SC_OK);

    }

    // Get parameters and make a post-request (POST)
    public void doPost(HttpServletRequest request,
                       HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("text/html;charset=utf-8");
        response.setCharacterEncoding("UTF-8");

        int plant = Integer.parseInt(request.getParameter("plant"));
        Map<String, Object> pageVariables = createPageVariablesMap(request);

        Map<String, List<Integer>> pers = buildSearchingPeriods(pageVariables.get("period"));
        List<String> tabHeader = buildHeaderOfTable(pers);

        // Connect to the database
        DbReader dbReader = new DbReader("/home/dpetrov/sapdata/mrp");

        try {

            dbReader.openConnection();
            if (dbReader.connectionIsValid()) {
                dbReader.setPlant(plant);
                dbReader.setPurchaseGroup(request.getParameter("purGroup"));
                dbReader.setPeriods(pers);
                dbReader.searchAllData();
            }
            dbReader.closeDbConnection();

        } catch (SQLException sqe) {
            sqe.printStackTrace();
        }

        pageVariables.put("tabHeader", tabHeader);
        response.getWriter().println(PageGenerator.getInstance().getPage("output.html", pageVariables));

    }

    // PRIVATE SECTION

    // Build a header of html table
    private List<String> buildHeaderOfTable(Map<String, List<Integer>> peri) {

        List<String> header = new ArrayList<>();

        for (Map.Entry<String, List<Integer>> entry: peri.entrySet()) {
            List<Integer> values = entry.getValue();
            for (int value: values) {
                String temp = "";
                switch (value) {
                    case 1:
                        temp = "Январь";
                        break;
                    case 2:
                        temp = "Февраль";
                        break;
                    case 3:
                        temp = "Март";
                        break;
                    case 4:
                        temp = "Апрель";
                        break;
                    case 5:
                        temp = "Май";
                        break;
                    case 6:
                        temp = "Июнь";
                        break;
                    case 7:
                        temp = "Июль";
                        break;
                    case 8:
                        temp = "Август";
                        break;
                    case 9:
                        temp = "Сентябрь";
                        break;
                    case 10:
                        temp = "Октябрь";
                        break;
                    case 11:
                        temp = "Ноябрь";
                        break;
                    case 12:
                        temp = "Декабрь";
                        break;
                }
                String row = temp + " " + entry.getKey();
                header.add(row);
            }
        }

        return header;

    }

    private Map<String, Object> createPageVariablesMap(HttpServletRequest request) {

        Map<String, Object> pageVariables = new HashMap<>();

        pageVariables.put("period", request.getParameter("period"));
        pageVariables.put("plant", request.getParameter("plant"));
        pageVariables.put("purGroup", request.getParameter("purGroup"));

        return pageVariables;
    }

    // Build searching periods from current month to next six months.
    private static Map<String, List<Integer>> buildSearchingPeriods(Object dat) {

        Map<String, List<Integer>> periods = new LinkedHashMap<>();  // Map with the keeping of order

        String[] temp = String.valueOf(dat).split("-");

        String key1 = temp[0];
        String key2 = String.valueOf(Integer.parseInt(key1) + 1);

        int month = Integer.parseInt(temp[1]);
        int next = 1;
        List<Integer> p1 = new ArrayList<>();
        List<Integer> p2 = new ArrayList<>();

        for (int i = 0; i <= 5; i++) {
            if (month >= 13) {
                p2.add(next);
                periods.put(key2, p2);
                next++;
            } else {
                p1.add(month);
                periods.put(key1, p1);
            }
            month++;
        }

        return periods;
    }

}
