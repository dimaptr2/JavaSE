package ru.velkomfood.mysap.mysql.model;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by dpetrov on 12.07.16.
 */
public class MrpItemsEntity {

    private int plant;
    private String matnr;
    private String mrpDate;
    private String baseUnit;
    private String purchaseGroup;

    private String per_segmt;
    private BigDecimal pquantity;
    private BigDecimal squantity;
    private BigDecimal availquantity;

    public int getPlant() {
        return plant;
    }

    public void setPlant(int plant) {
        this.plant = plant;
    }

    public String getMatnr() {
        return matnr;
    }

    public void setMatnr(String matnr) {
        this.matnr = matnr;
    }

    public String getMrpDate() {
        return mrpDate;
    }

    public void setMrpDate(String mrpDate) {
        this.mrpDate = mrpDate;
    }

    public String getBaseUnit() {
        return baseUnit;
    }

    public void setBaseUnit(String baseUnit) {
        this.baseUnit = baseUnit;
    }

    public String getPurchaseGroup() {
        return purchaseGroup;
    }

    public void setPurchaseGroup(String purchaseGroup) {
        this.purchaseGroup = purchaseGroup;
    }

    public String getPer_segmt() {
        return per_segmt;
    }

    public void setPer_segmt(String per_segmt) {
        this.per_segmt = per_segmt;
    }

    public BigDecimal getPquantity() {
        return pquantity;
    }

    public void setPquantity(BigDecimal pquantity) {
        this.pquantity = pquantity;
    }

    public BigDecimal getSquantity() {
        return squantity;
    }

    public void setSquantity(BigDecimal squantity) {
        this.squantity = squantity;
    }

    public BigDecimal getAvailquantity() {
        return availquantity;
    }

    public void setAvailquantity(BigDecimal availquantity) {
        this.availquantity = availquantity;
    }
}
