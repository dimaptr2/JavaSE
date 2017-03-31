package ru.velkomfood.fin.data;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by dpetrov on 29.03.2017.
 */
public class PrintFormat implements IPrintForm {

    @Override
    public Receipt buildReceipt(long id, BigDecimal totalQuantity, BigDecimal totalAmount, Receipt receipt) {

        if (receipt.getItems() != null && !receipt.getItems().isEmpty()) {
            double denominator = totalQuantity.doubleValue();
            if (!totalAmount.equals(new BigDecimal(0.00))) {
                double amount = totalAmount.doubleValue();
                for (ReceiptItem ri: receipt.getItems()) {
                    double itemQuantity = ri.getQuantity().doubleValue();
                    if (denominator != 0.00) {
                        double proportion =  itemQuantity / denominator;
                        double sum = amount * proportion;
                        BigDecimal totalSum = new BigDecimal(sum);
                        ri.setAmount(totalSum);
                    }
                }
            }
        }

        return receipt;
    }

}
