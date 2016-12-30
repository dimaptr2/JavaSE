package ru.velkomfood.fin.cache.model;

import java.math.BigDecimal;
import java.sql.Date;

/**
 * Created by dpetrov on 29.12.2016.
 */
public class CashJournal {

    private String cajoNumber;
    private String companyCode;
    private int year;
    private int postingNumber;
    private BigDecimal amountReceipt;
    private BigDecimal amountPayments;
    private BigDecimal netAmount;
    private String partnerName;
    private java.sql.Date documentDate;
    private java.sql.Date postingDate;
    private String documentNumber;
    private String positionText;
    private long deliveryId;

    public String getCajoNumber() {
        return cajoNumber;
    }

    public void setCajoNumber(String cajoNumber) {
        this.cajoNumber = cajoNumber;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getPostingNumber() {
        return postingNumber;
    }

    public void setPostingNumber(int postingNumber) {
        this.postingNumber = postingNumber;
    }

    public BigDecimal getAmountReceipt() {
        return amountReceipt;
    }

    public void setAmountReceipt(BigDecimal amountReceipt) {
        this.amountReceipt = amountReceipt;
    }

    public BigDecimal getAmountPayments() {
        return amountPayments;
    }

    public void setAmountPayments(BigDecimal amountPayments) {
        this.amountPayments = amountPayments;
    }

    public BigDecimal getNetAmount() {
        return netAmount;
    }

    public void setNetAmount(BigDecimal netAmount) {
        this.netAmount = netAmount;
    }

    public String getPartnerName() {
        return partnerName;
    }

    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName;
    }

    public Date getDocumentDate() {
        return documentDate;
    }

    public void setDocumentDate(Date documentDate) {
        this.documentDate = documentDate;
    }

    public Date getPostingDate() {
        return postingDate;
    }

    public void setPostingDate(Date postingDate) {
        this.postingDate = postingDate;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
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

}
