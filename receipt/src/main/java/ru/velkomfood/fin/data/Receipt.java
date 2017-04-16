package ru.velkomfood.fin.data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dpetrov on 29.03.2017.
 */
public class Receipt {

    private long id;
    private long actualAmount;
    // Items
    private List<ReceiptItem> items;

    public Receipt() {
        items = new ArrayList<>();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getActualAmount() {
        return actualAmount;
    }

    public void setActualAmount(BigDecimal actualAmount) {
        this.actualAmount = actualAmount.longValue();
    }

    public List<ReceiptItem> getItems() {
        return items;
    }

    public void clearItems() {
        if (!items.isEmpty()) {
            items.clear();
        }
    }

    public void addItem(ReceiptItem ri) {
        items.add(ri);
    }

    public void removeItem(ReceiptItem ri) {
        items.remove(ri);
    }

    public void removeItemByIndex(int index) {
        items.remove(index);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Receipt receipt = (Receipt) o;

        if (id != receipt.id) return false;
        return items != null ? items.equals(receipt.items) : receipt.items == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (items != null ? items.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Receipt{" +
                "id=" + id +
                ", items=" + items +
                '}';
    }

}
