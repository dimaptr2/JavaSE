package ru.velkomfood.visual.mrp.view;

import ru.velkomfood.visual.mrp.bdb.entities.MrpPrimaryKey;
import ru.velkomfood.visual.mrp.controller.MySqlConnector;
import ru.velkomfood.visual.mrp.controller.PageGenerator;
import ru.velkomfood.visual.mrp.model.entities.FinalTotals;
import ru.velkomfood.visual.mrp.model.entities.MaterialEntity;
import ru.velkomfood.visual.mrp.model.entities.Months;
import ru.velkomfood.visual.mrp.model.entities.PurchaseGroup;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;


/**
 * Created by dpetrov on 28.07.16.
 */

@WebServlet(name = "Frontend", urlPatterns = "/mrp")
public class Frontend extends HttpServlet {

    private String page = "";

    private MySqlConnector connector;
    private Map<String, PurchaseGroup> purchaseGroups;
    private List<MaterialEntity> materials;
    private PageGenerator pageGenerator;

    public void setConnector(MySqlConnector connector) throws SQLException {

        this.connector = connector;
        if (connector != null) connector.initConnection();

    }

    public void setPageGenerator(PageGenerator pageGenerator) {
        this.pageGenerator = pageGenerator;
    }

    // Output page
    @Override
    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws ServletException, IOException {

        response.setStatus(HttpServletResponse.SC_OK);
        Map<String, Object> pageVariables = createPageVariablesMap(request);

        response.getWriter().println(pageGenerator.getPage("index.html", pageVariables));

        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);

    }


    // Post method (get values from form)
    @Override
    public void doPost(HttpServletRequest request,
                       HttpServletResponse response) throws ServletException, IOException {

        int year;
        Map<String, String> params = new HashMap<>();
        List<FinalTotals> outputs = new ArrayList<>();

        params.put("fromMonth", request.getParameter("fromMonth"));
        params.put("fromMatnr", request.getParameter("fromMatnr"));
        params.put("toMatnr", request.getParameter("toMatnr"));
        params.put("purGrp", request.getParameter("purGrp"));

        if (!params.get("fromMonth").equals("")) {
            String[] temp = params.get("fromMonth").split("-");
            year = Integer.parseInt(temp[0]);
        } else {
            year = Calendar.getInstance().get(Calendar.YEAR);
        }

        List<Months> periods = calculateObservablePeriod(params);
        connector.setCurrentYear(year);

        try {

            purchaseGroups = connector.fetchPurchaseGroups();
            materials = connector.fetchMaterialsList(params);

            char symbol = 's';

        } catch (SQLException sqex) {

            sqex.printStackTrace();

        }

        Map<String, Object> pageVariables = new HashMap<>();

        pageVariables.put("year", year);
        pageVariables.put("totals", outputs);

        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);

        if (outputs.isEmpty()) {
            page = "notfound.html";
        } else {
            page = "output.html";
        }

        response.getWriter().println(PageGenerator.getInstance().getPage(page, pageVariables));

    } // do post

    // Create page variables hash
    private static Map<String, Object> createPageVariablesMap(HttpServletRequest request) {

        Map<String, Object> pageVariables = new HashMap<>();

        pageVariables.put("fromMonth", request.getParameter("fromMonth"));
        pageVariables.put("fromMatnr", request.getParameter("fromMatnr"));
        pageVariables.put("toMatnr", request.getParameter("toMatnr"));
        pageVariables.put("purGrp", request.getParameter("purGrp"));

        return pageVariables;
    }


    private List<Months> calculateObservablePeriod(Map<String, String> parameters) {

        String[] temp = parameters.get("fromMonth").split("-");
        int counter = Integer.parseInt(temp[1]);

        if (counter == 0) counter = 1;

        List<Months> periods = new ArrayList<>();

        for (int i = 0; i <= 5; i++) {
            if (counter > 12) break;
            Months m = new Months();
            m.setId(counter);
            m.setName(getNameOfPeriod(counter));
            periods.add(m);
            counter++;
        }

        return periods;
    }

    // Build names of periods
    private String  getNameOfPeriod(int counter) {

        String period = "";

            switch (counter) {
                case 1:
                    period = "Январь";
                    break;
                case 2:
                    period = "Февраль";
                    break;
                case 3:
                    period = "Март";
                    break;
                case 4:
                    period = "Апрель";
                    break;
                case 5:
                    period = "Май";
                    break;
                case 6:
                    period = "Июнь";
                    break;
                case 7:
                    period = "Июль";
                    break;
                case 8:
                    period = "Август";
                    break;
                case 9:
                    period = "Сентябрь";
                    break;
                case 10:
                    period = "Октябрь";
                    break;
                case 11:
                    period = "Ноябрь";
                    break;
                case 12:
                    period = "Декабрь";
                    break;
            }

            return period;

    }

}
