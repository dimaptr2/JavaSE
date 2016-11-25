package ru.velkomfood.mysap.mysql.model;

import java.math.BigDecimal;

/**
 * Created by dpetrov on 14.07.16.
 */
public class StocksEntity {

    private int werks;
    private String matnr;
    private String availDate;
    private String purchaseGroup;
    private String baseUnit;
    private BigDecimal safetyStock;
    private BigDecimal plantStock;

    public int getWerks() {
        return werks;
    }

    public void setWerks(int werks) {
        this.werks = werks;
    }

    public String getMatnr() {
        return matnr;
    }

    public void setMatnr(String matnr) {
        this.matnr = matnr;
    }

    public String getAvailDate() {
        return availDate;
    }

    public void setAvailDate(String availDate) {
        this.availDate = availDate;
    }

    public String getPurchaseGroup() {
        return purchaseGroup;
    }

    public void setPurchaseGroup(String purchaseGroup) {
        this.purchaseGroup = purchaseGroup;
    }

    public String getBaseUnit() {
        return baseUnit;
    }

    public void setBaseUnit(String baseUnit) {
        this.baseUnit = baseUnit;
    }

    public BigDecimal getSafetyStock() {
        return safetyStock;
    }

    public void setSafetyStock(BigDecimal safetyStock) {
        this.safetyStock = safetyStock;
    }

    public BigDecimal getPlantStock() {
        return plantStock;
    }

    public void setPlantStock(BigDecimal plantStock) {
        this.plantStock = plantStock;
    }

}
