package ru.velkomfood.fin.cash.model;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dpetrov on 13.02.2017.
 */
public class Receipt {

    private Long id;
    private String cajoNumber;
    private String companyCode;
    private int year;
    private Long postingNumber;
    private BigDecimal amountReceipt;
    private String partnerName;
    private String documentDate;
    private String postingDate;
    private String documentNumber;
    private String positionText;
    private Long deliveryId;
    private List<ReceiptItem> items;

    public Receipt() {
        items = new ArrayList<>();
    }

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

    public String getPartnerName() {
        return partnerName;
    }

    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName;
    }

    public String getDocumentDate() {
        return documentDate;
    }

    public void setDocumentDate(String documentDate) {
        this.documentDate = documentDate;
    }

    public String getPostingDate() {
        return postingDate;
    }

    public void setPostingDate(String postingDate) {
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

    public List<ReceiptItem> getItems() {
        return items;
    }

    public void setItems(List<ReceiptItem> items) {
        this.items = items;
    }

    // Item's functions

    public void addItem(ReceiptItem item) {
        items.add(item);
    }

    public void deleteItemByIndex(int index) {
        items.remove(index);
    }

    public void deleteItem(ReceiptItem item) {
        int index = items.indexOf(item);
        items.remove(index);
    }

    public void clearItems() {
        if (items != null && !items.isEmpty()) {
            items.clear();
        }
    }

}
