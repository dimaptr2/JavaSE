package ru.velkomfood.fin.data;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by dpetrov on 30.03.2017.
 */
public interface IPrintForm {
    Receipt buildReceipt(long id, BigDecimal totalQuantity, BigDecimal totalAmount, Receipt receipt);
}
