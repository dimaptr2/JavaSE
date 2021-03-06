package ru.velkomfood.fin.cache.model.DB;

import java.math.BigDecimal;
import java.sql.Date;

/**
 * Created by dpetrov on 12.01.2017.
 */
public class CreditSlip {

    private Long id;
    private String cajoNumber;
    private String companyCode;
    private int year;
    private Long postingNumber;
    private BigDecimal amountReceipt;
    private BigDecimal amountPayments;
    private BigDecimal netAmount;
    private String partnerName;
    private java.sql.Date documentDate;
    private java.sql.Date postingDate;
    private String documentNumber;
    private String positionText;
    private Long deliveryId;
    private java.sql.Date timestamp;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Long getPostingNumber() {
        return postingNumber;
    }

    public void setPostingNumber(Long postingNumber) {
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

    public Long getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(Long deliveryId) {
        this.deliveryId = deliveryId;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

}
