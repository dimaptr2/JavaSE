package ru.velkomfood.visual.mrp.view;

import ru.velkomfood.visual.mrp.controller.MySqlConnector;
import ru.velkomfood.visual.mrp.controller.PageGenerator;
import ru.velkomfood.visual.mrp.model.entities.MaterialEntity;
import ru.velkomfood.visual.mrp.model.entities.PurchaseGroup;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by dpetrov on 05.08.16.
 */

@WebServlet(name = "SearchHelper", urlPatterns = "/sh")
public class SearchHelper extends HttpServlet {

    private String page = "";

    private MySqlConnector connector;
    private PageGenerator pageGenerator;

    public void setConnector(MySqlConnector connector) {
        this.connector = connector;
    }

    public void setPageGenerator(PageGenerator pageGenerator) {
        this.pageGenerator = pageGenerator;
    }

    // Basic request's processing ...
    @Override
    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws ServletException, IOException {

    }

    @Override
    public void doPost(HttpServletRequest request,
                       HttpServletResponse response) throws ServletException, IOException {

    }

    // end of request's processing section

    private List<PurchaseGroup> getPurchaseGroups() throws SQLException {
        return connector.fetchPurchaseGroups();
    }

    private List<MaterialEntity> getListOfMaterials() throws SQLException {
        return connector.fetchMaterialsList();
    }

}
