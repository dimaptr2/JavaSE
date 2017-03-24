package ru.velkomfood.fin.cash.model;

import java.math.BigDecimal;

/**
 * Created by dpetrov on 13.03.2017.
 */
public class CashDoc {

    private String cajoNumber;
    private int companyCode;
    private int fiscalYear;
    private long postingNumber;
    private String postingDate;
    private String positionText;
    private long deliveryId;
    private BigDecimal amount;

    public String getCajoNumber() {
        return cajoNumber;
    }

    public void setCajoNumber(String cajoNumber) {
        this.cajoNumber = cajoNumber;
    }

    public int getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(int companyCode) {
        this.companyCode = companyCode;
    }

    public int getFiscalYear() {
        return fiscalYear;
    }

    public void setFiscalYear(int fiscalYear) {
        this.fiscalYear = fiscalYear;
    }

    public long getPostingNumber() {
        return postingNumber;
    }

    public void setPostingNumber(long postingNumber) {
        this.postingNumber = postingNumber;
    }

    public String getPostingDate() {
        return postingDate;
    }

    public void setPostingDate(String postingDate) {
        this.postingDate = postingDate;
    }

    public String getPositionText() {
        return positionText;
    }

    public void setPositionText(String positionText) {
        this.positionText = positionText;
    }

    public long getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(long deliveryId) {
        this.deliveryId = deliveryId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CashDoc that = (CashDoc) o;

        if (companyCode != that.companyCode) return false;
        if (fiscalYear != that.fiscalYear) return false;
        if (postingNumber != that.postingNumber) return false;
        if (deliveryId != that.deliveryId) return false;
        if (!cajoNumber.equals(that.cajoNumber)) return false;
        if (!postingDate.equals(that.postingDate)) return false;
        if (!positionText.equals(that.positionText)) return false;
        return amount.equals(that.amount);

    }

    @Override
    public int hashCode() {

        int result = cajoNumber.hashCode();
        result = 31 * result + companyCode;
        result = 31 * result + fiscalYear;
        result = 31 * result + (int) (postingNumber ^ (postingNumber >>> 32));
        result = 31 * result + postingDate.hashCode();
        result = 31 * result + positionText.hashCode();
        result = 31 * result + (int) (deliveryId ^ (deliveryId >>> 32));
        result = 31 * result + amount.hashCode();
        return result;

    }

    @Override
    public String toString() {

        return "CashDoc{" +
                "cajoNumber='" + cajoNumber + '\'' +
                ", companyCode=" + companyCode +
                ", fiscalYear=" + fiscalYear +
                ", postingNumber=" + postingNumber +
                ", postingDate='" + postingDate + '\'' +
                ", positionText='" + positionText + '\'' +
                ", deliveryId=" + deliveryId +
                ", amount=" + amount +
                '}';

    }
}
