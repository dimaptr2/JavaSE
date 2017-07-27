package ru.velkomfood.fin.cash.logon.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class FiscalItemView {

    private StringProperty counter;
    private StringProperty materialId;
    private StringProperty description;
    private StringProperty quantity;
    private StringProperty price;
    private StringProperty vatRate;

    public FiscalItemView(String counter, String materialId,
                          String description, String quantity,
                          String price, String vatRate) {
        this.counter = new SimpleStringProperty(counter);
        this.materialId = new SimpleStringProperty(materialId);
        this.description = new SimpleStringProperty(description);
        this.quantity = new SimpleStringProperty(quantity);
        this.price = new SimpleStringProperty(price);
        this.vatRate = new SimpleStringProperty(vatRate);
    }

    public String getCounter() {
        return counter.get();
    }

    public StringProperty counterProperty() {
        return counter;
    }

    public void setCounter(String counter) {
        this.counter.set(counter);
    }

    public String getMaterialId() {
        return materialId.get();
    }

    public StringProperty materialIdProperty() {
        return materialId;
    }

    public void setMaterialId(String materialId) {
        this.materialId.set(materialId);
    }

    public String getDescription() {
        return description.get();
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public String getQuantity() {
        return quantity.get();
    }

    public StringProperty quantityProperty() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity.set(quantity);
    }

    public String getPrice() {
        return price.get();
    }

    public StringProperty priceProperty() {
        return price;
    }

    public void setPrice(String price) {
        this.price.set(price);
    }

    public String getVatRate() {
        return vatRate.get();
    }

    public StringProperty vatRateProperty() {
        return vatRate;
    }

    public void setVatRate(String vatRate) {
        this.vatRate.set(vatRate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FiscalItemView that = (FiscalItemView) o;

        return counter.equals(that.counter);
    }

    @Override
    public int hashCode() {
        return counter.hashCode();
    }

}
