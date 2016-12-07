package ru.velkomfood.mysap.synchronizer.model;

import java.math.BigDecimal;

/**
 * Created by dpetrov on 07.12.16.
 */
public class MaterialNode {

    private int matnr;
    private String description;
    private int plant;
    private String baseUom;
    private String priceControl;
    private BigDecimal fixedPrice;
    private BigDecimal weightedPrice;
    private BigDecimal priceUnit;

    public int getMatnr() {
        return matnr;
    }

    public void setMatnr(int matnr) {
        this.matnr = matnr;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPlant() {
        return plant;
    }

    public void setPlant(int plant) {
        this.plant = plant;
    }

    public String getBaseUom() {
        return baseUom;
    }

    public void setBaseUom(String baseUom) {
        this.baseUom = baseUom;
    }

    public String getPriceControl() {
        return priceControl;
    }

    public void setPriceControl(String priceControl) {
        this.priceControl = priceControl;
    }

    public BigDecimal getFixedPrice() {
        return fixedPrice;
    }

    public void setFixedPrice(BigDecimal fixedPrice) {
        this.fixedPrice = fixedPrice;
    }

    public BigDecimal getWeightedPrice() {
        return weightedPrice;
    }

    public void setWeightedPrice(BigDecimal weightedPrice) {
        this.weightedPrice = weightedPrice;
    }

    public BigDecimal getPriceUnit() {
        return priceUnit;
    }

    public void setPriceUnit(BigDecimal priceUnit) {
        this.priceUnit = priceUnit;
    }

}
