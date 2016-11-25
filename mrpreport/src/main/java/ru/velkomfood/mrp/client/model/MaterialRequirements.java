package ru.velkomfood.mrp.client.model;

import java.math.BigDecimal;

/**
 * Created by dpetrov on 16.08.2016.
 */
public class MaterialRequirements {

    private String material;
    private String materialDescription;
    private int werks, year, month;
    private String purchaseGroup;
    private String nameOfPurchaseGroup;
    private String baseUnit;

    private BigDecimal stocks;
    private BigDecimal quantity;

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getMaterialDescription() {
        return materialDescription;
    }

    public void setMaterialDescription(String materialDescription) {
        this.materialDescription = materialDescription;
    }

    public int getWerks() {
        return werks;
    }

    public void setWerks(int werks) {
        this.werks = werks;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public String getPurchaseGroup() {
        return purchaseGroup;
    }

    public void setPurchaseGroup(String purchaseGroup) {
        this.purchaseGroup = purchaseGroup;
    }

    public String getNameOfPurchaseGroup() {
        return nameOfPurchaseGroup;
    }

    public void setNameOfPurchaseGroup(String nameOfPurchaseGroup) {
        this.nameOfPurchaseGroup = nameOfPurchaseGroup;
    }

    public String getBaseUnit() {
        return baseUnit;
    }

    public void setBaseUnit(String baseUnit) {
        this.baseUnit = baseUnit;
    }

    public BigDecimal getStocks() {
        return stocks;
    }

    public void setStocks(BigDecimal stocks) {
        this.stocks = stocks;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

}
