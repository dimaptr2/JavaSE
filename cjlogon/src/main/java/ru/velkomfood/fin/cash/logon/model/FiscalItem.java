package ru.velkomfood.fin.cash.logon.model;

import java.math.BigDecimal;

public class FiscalItem {

    private int counter;
    private long materialId;
    private String description;
    private BigDecimal quantity;
    private BigDecimal price;
    private int vatRate;

    public FiscalItem() { }

    public FiscalItem(int counter, long materialId,
                      String description, BigDecimal quantity,
                      BigDecimal price, int vatRate) {
        this.counter = counter;
        this.materialId = materialId;
        this.description = description;
        this.quantity = quantity;
        this.price = price;
        this.vatRate = vatRate;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public long getMaterialId() {
        return materialId;
    }

    public void setMaterialId(long materialId) {
        this.materialId = materialId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getVatRate() {
        return vatRate;
    }

    public void setVatRate(int vatRate) {
        this.vatRate = vatRate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FiscalItem that = (FiscalItem) o;

        if (counter != that.counter) return false;
        return materialId == that.materialId;
    }

    @Override
    public int hashCode() {
        int result = counter;
        result = 31 * result + (int) (materialId ^ (materialId >>> 32));
        return result;
    }

}
