package ru.velkomfood.fin.cash.view;

import com.sap.conn.jco.JCoException;
import drvfr_print.jpos.res.JVelPrinter;
import ru.velkomfood.fin.cash.controller.ErpRequestor;
import ru.velkomfood.fin.cash.controller.PrintEngine;
import ru.velkomfood.fin.cash.model.CashDoc;
import ru.velkomfood.fin.cash.model.ResultView;
import ru.velkomfood.fin.data.Receipt;
import ru.velkomfood.fin.data.ReceiptItem;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dpetrov on 22.03.2017.
 */
@WebServlet("/items")
public class ViewItems extends HttpServlet {

    private PageGenerator pageGenerator = PageGenerator.getInstance();
    private ErpRequestor erpRequestor = ErpRequestor.getInstance();
    private PrintEngine printEngine = PrintEngine.getInstance();
    private CashDoc cashDoc;
    private Receipt receipt;
    private List<ResultView> result;

    @Override
    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws ServletException, IOException {

        Map<String, Object> pageVariables = createPageVariablesMap(request);
        response.setContentType("text/html;charset=utf-8");

        String txtPosNumb = String.valueOf(pageVariables.get("pNumb"));
        long docNumber = Long.parseLong(txtPosNumb);
        String txtDelivery = String.valueOf(pageVariables.get("delId"));
        long deliveryNumber = Long.parseLong(txtDelivery);

        // Build all data collections
        if (docNumber != 0 && deliveryNumber != 0) {

            cashDoc = chooseCashDocument(docNumber);

            try {
                receipt = erpRequestor.getReceiptPrintingForm(deliveryNumber);
                receipt.setActualAmount(cashDoc.getAmount());
            } catch (JCoException e) {
                e.printStackTrace();
            }
            String bottomPrice = "";
            List<ReceiptItem> its = receipt.getItems();
            buildOutput(its);
        } // docs

        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().println(pageGenerator.getPage("receipt.html", pageVariables));

    } // end of doGet

    @Override
    public void doPost(HttpServletRequest request,
                       HttpServletResponse response) throws ServletException, IOException {

        Map<String, Object> pageVariables = createPageVariablesMap(request);
        response.setContentType("application/json;charset=utf-8");

        String buttonCode = String.valueOf(pageVariables.get("cmdPrint"));

        if (buttonCode.equals("PRINT")) {
            if (receipt != null) {
                try {
                    printEngine.openDevice();
                    JVelPrinter printer = printEngine.getPrinter();
                    printer.connect();
                    printer.printReceipt(receipt);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    // Add variables for the HTML templates
    // Any tag in the template will be replaced by the content of the template variable, respectively.
    private Map<String, Object> createPageVariablesMap(HttpServletRequest request) {
        Map<String, Object> pageVariables = new HashMap<>();
        pageVariables.put("pNumb", request.getParameter("pNumb"));
        pageVariables.put("delId", request.getParameter("delId"));
        pageVariables.put("cmdPrint", request.getParameter("cmdPrint"));
        pageVariables.put("cashDoc", cashDoc);
        pageVariables.put("receipt", receipt);
        pageVariables.put("result", result);
        return pageVariables;
    }

    // Choose only one cash document
    private CashDoc chooseCashDocument(long docNumber) {
        CashDoc refDoc = null;
        for (CashDoc d: erpRequestor.getHeads()) {
            long key = d.getPostingNumber();
            if (key == docNumber) {
                refDoc = d;
            }
        }
        return refDoc;
    }

    private void buildOutput(List<ReceiptItem> items) {

        if (result == null) {
            result = new ArrayList<>();
        } else if (result != null && !result.isEmpty()) {
            result.clear();
        }

        for (ReceiptItem ri: items) {
            ResultView rv = new ResultView();
            rv.setMaterialName(ri.getMaterialName());
            rv.setQuantity(String.valueOf(ri.getQuantity()));
            BigDecimal amount = ri.getAmount();
            BigDecimal quantity = ri.getQuantity();
            BigDecimal price = amount.multiply(quantity);
            price = price.setScale(2, BigDecimal.ROUND_DOWN);
            rv.setAmount(String.valueOf(price));
            result.add(rv);
        }

    }

}
