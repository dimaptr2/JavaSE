package ru.velkomfood.fin.cash.logon.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;



public class CashDocView {

    private StringProperty counter;
    private StringProperty id;
    private StringProperty postingDate;
    private StringProperty positionText;
    private StringProperty deliveryId;
    private StringProperty amount;
    private StringProperty receiptAmount;
    private StringProperty difference;

    public CashDocView() {
        counter = new SimpleStringProperty();
        id = new SimpleStringProperty();
        postingDate = new SimpleStringProperty();
        positionText = new SimpleStringProperty();
        deliveryId = new SimpleStringProperty();
        amount = new SimpleStringProperty();
        receiptAmount = new SimpleStringProperty();
        difference = new SimpleStringProperty();
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

    public String getId() {
        return id.get();
    }

    public StringProperty idProperty() {
        return id;
    }

    public void setId(String id) {
        this.id.set(id);
    }

    public String getPostingDate() {
        return postingDate.get();
    }

    public StringProperty postingDateProperty() {
        return postingDate;
    }

    public void setPostingDate(String postingDate) {
        this.postingDate.set(postingDate);
    }

    public String getPositionText() {
        return positionText.get();
    }

    public StringProperty positionTextProperty() {
        return positionText;
    }

    public void setPositionText(String positionText) {
        this.positionText.set(positionText);
    }

    public String getDeliveryId() {
        return deliveryId.get();
    }

    public StringProperty deliveryIdProperty() {
        return deliveryId;
    }

    public void setDeliveryId(String deliveryId) {
        this.deliveryId.set(deliveryId);
    }

    public String getAmount() {
        return amount.get();
    }

    public StringProperty amountProperty() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount.set(amount);
    }

    public String getReceiptAmount() {
        return receiptAmount.get();
    }

    public StringProperty receiptAmountProperty() {
        return receiptAmount;
    }

    public void setReceiptAmount(String receiptAmount) {
        this.receiptAmount.set(receiptAmount);
    }

    public String getDifference() {
        return difference.get();
    }

    public StringProperty differenceProperty() {
        return difference;
    }

    public void setDifference(String difference) {
        this.difference.set(difference);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CashDocView that = (CashDocView) o;

        if (!counter.equals(that.counter)) return false;
        if (!id.equals(that.id)) return false;
        if (postingDate != null ? !postingDate.equals(that.postingDate) : that.postingDate != null) return false;
        if (positionText != null ? !positionText.equals(that.positionText) : that.positionText != null) return false;
        if (deliveryId != null ? !deliveryId.equals(that.deliveryId) : that.deliveryId != null) return false;
        if (amount != null ? !amount.equals(that.amount) : that.amount != null) return false;
        if (receiptAmount != null ? !receiptAmount.equals(that.receiptAmount) : that.receiptAmount != null)
            return false;
        return difference != null ? difference.equals(that.difference) : that.difference == null;
    }

    @Override
    public int hashCode() {
        int result = counter.hashCode();
        result = 31 * result + id.hashCode();
        result = 31 * result + (postingDate != null ? postingDate.hashCode() : 0);
        result = 31 * result + (positionText != null ? positionText.hashCode() : 0);
        result = 31 * result + (deliveryId != null ? deliveryId.hashCode() : 0);
        result = 31 * result + (amount != null ? amount.hashCode() : 0);
        result = 31 * result + (receiptAmount != null ? receiptAmount.hashCode() : 0);
        result = 31 * result + (difference != null ? difference.hashCode() : 0);
        return result;
    }

}
