package ru.velkomfood.fin.cash.logon.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CashDocument {

    private long id;
    private String cajoNumber;
    private String companyId;
    private int year;
    private LocalDate postingDate;
    private String positionText;
    private long deliveryId;
    private BigDecimal amount;

    public CashDocument() { }

    public CashDocument(long id, String cajoNumber,
                        String companyId, int year,
                        LocalDate postingDate,
                        String positionText, long deliveryId,
                        BigDecimal amount) {
        this.id = id;
        this.cajoNumber = cajoNumber;
        this.companyId = companyId;
        this.year = year;
        this.postingDate = postingDate;
        this.positionText = positionText;
        this.deliveryId = deliveryId;
        this.amount = amount;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCajoNumber() {
        return cajoNumber;
    }

    public void setCajoNumber(String cajoNumber) {
        this.cajoNumber = cajoNumber;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public LocalDate getPostingDate() {
        return postingDate;
    }

    public void setPostingDate(LocalDate postingDate) {
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

        CashDocument that = (CashDocument) o;

        if (id != that.id) return false;
        if (!cajoNumber.equals(that.cajoNumber)) return false;
        return companyId.equals(that.companyId);
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + cajoNumber.hashCode();
        result = 31 * result + companyId.hashCode();
        return result;
    }

}
