package ru.velkomfood.mysap.mysql.model;

import java.math.BigDecimal;

/**
 * Created by dpetrov on 14.07.16.
 */
public class MrpTotalsEntity {

    private String matnr;
    private int plant;
    private int year;
    private int month;
    private String baseUnit;
    private String currency;
    private BigDecimal quantity;
    private BigDecimal weightedPriceOfStock;
    private BigDecimal fixedPriceOfStock;

    public String getMatnr() {
        return matnr;
    }

    public void setMatnr(String matnr) {
        this.matnr = matnr;
    }

    public int getPlant() {
        return plant;
    }

    public void setPlant(int plant) {
        this.plant = plant;
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

    public String getBaseUnit() {
        return baseUnit;
    }

    public void setBaseUnit(String baseUnit) {
        this.baseUnit = baseUnit;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getWeightedPriceOfStock() {
        return weightedPriceOfStock;
    }

    public void setWeightedPriceOfStock(BigDecimal weightedPriceOfStock) {
        this.weightedPriceOfStock = weightedPriceOfStock;
    }

    public BigDecimal getFixedPriceOfStock() {
        return fixedPriceOfStock;
    }

    public void setFixedPriceOfStock(BigDecimal fixedPriceOfStock) {
        this.fixedPriceOfStock = fixedPriceOfStock;
    }

}
