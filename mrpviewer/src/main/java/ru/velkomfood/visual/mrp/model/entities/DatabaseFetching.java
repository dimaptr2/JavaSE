package ru.velkomfood.visual.mrp.model.entities;

import java.math.BigDecimal;

/**
 * Created by dpetrov on 29.07.16.
 */

public class DatabaseFetching {

    private String material;
    private String matDescription;
    private int plant;
    private int year;
    private int month;
    private String purchaseGroup;
    private String NameOfPurGroup;
    private String baseUnit;

    private BigDecimal stock;
    private BigDecimal quantity;

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getMatDescription() {
        return matDescription;
    }

    public void setMatDescription(String matDescription) {
        this.matDescription = matDescription;
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

    public String getPurchaseGroup() {
        return purchaseGroup;
    }

    public void setPurchaseGroup(String purchaseGroup) {
        this.purchaseGroup = purchaseGroup;
    }

    public String getNameOfPurGroup() {
        return NameOfPurGroup;
    }

    public void setNameOfPurGroup(String nameOfPurGroup) {
        NameOfPurGroup = nameOfPurGroup;
    }

    public String getBaseUnit() {
        return baseUnit;
    }

    public void setBaseUnit(String baseUnit) {
        this.baseUnit = baseUnit;
    }

    public BigDecimal getStock() {
        return stock;
    }

    public void setStock(BigDecimal stock) {
        this.stock = stock;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

}
