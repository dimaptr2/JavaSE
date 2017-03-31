package ru.velkomfood.fin.cash.view;

import ru.velkomfood.fin.cash.controller.ErpRequestor;
import ru.velkomfood.fin.cash.model.CashDoc;
import ru.velkomfood.fin.cash.model.ResultView;
import ru.velkomfood.fin.data.PrintFormat;
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
    private CashDoc cashDoc;
    private Receipt receipt;
    private List<ResultView> result = new ArrayList<>();

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
            double sum = cashDoc.getAmount().doubleValue();
            receipt = erpRequestor.getReceipts().get(deliveryNumber);
//            PrintFormat pf = new PrintFormat();
            // Distribute the amount to positions
            BigDecimal totalQuantity = calculateQuantity(receipt.getItems());
            double tq = totalQuantity.doubleValue();
            if (tq != 0.00) {
                for (ReceiptItem ri: receipt.getItems()) {
                    double  currentQuantity = ri.getQuantity().doubleValue();
                    double itemAmount = sum * ( currentQuantity / tq );
                    BigDecimal bd = new BigDecimal(itemAmount, MathContext.DECIMAL64)
                            .setScale(2, BigDecimal.ROUND_UP);
                    ri.setAmount(bd);
                }
            }
            buildOutput(receipt.getItems());
        } // docs

        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().println(pageGenerator.getPage("receipt.html", pageVariables));

    } // end of doGet

    @Override
    public void doPost(HttpServletRequest request,
                       HttpServletResponse response) throws ServletException, IOException {

        Map<String, Object> pageVariables = createPageVariablesMap(request);
        response.setContentType("application/json;charset=utf-8");

    }

    // Add variables for the HTML templates
    // Any tag in the template will be replaced by the content of the template variable, respectively.
    private Map<String, Object> createPageVariablesMap(HttpServletRequest request) {
        Map<String, Object> pageVariables = new HashMap<>();
        pageVariables.put("pNumb", request.getParameter("pNumb"));
        pageVariables.put("delId", request.getParameter("delId"));
        pageVariables.put("cashDoc", cashDoc);
        pageVariables.put("receipt", receipt);
        pageVariables.put("result", result);
        return pageVariables;
    }

    // Alpha transformation (add initial zeroes to the number (value))
    private String transformNumber(String value) {

        StringBuilder sb = new StringBuilder(0);
        final int MAX_LENGTH = 10;
        int range = MAX_LENGTH - value.length();

        if (range > 0) {
            for (int i = 1; i <= range; i++) {
                sb.append("0");
            }
            sb.append(value);
        } else {
            sb.append("not");
        }

        return sb.toString();
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

    // Calculate the total quantity in kilograms
    private BigDecimal calculateQuantity(List<ReceiptItem> items) {
        BigDecimal value = new BigDecimal(0.00);
        for (ReceiptItem ri: items) {
            value = value.add(ri.getQuantity());
        }
        return value;
    }

    private void buildOutput(List<ReceiptItem> items) {

        if (!result.isEmpty()) {
            result.clear();
        }

        for (ReceiptItem ri: items) {
            ResultView rv = new ResultView();
            rv.setMaterialName(ri.getMaterialName());
            rv.setQuantity(String.valueOf(ri.getQuantity()));
            rv.setAmount(String.valueOf(ri.getAmount()));
            result.add(rv);
        }

    }


}
