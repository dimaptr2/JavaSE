package ru.velkomfood.visual.mrp.model.entities;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by dpetrov on 28.07.16.
 */
public class FinalTotals {

    private String material;
    private String matDescription;
    private int plant;
    private int year;
    private int month;
    private String purchaseGroup;
    private String nameOfPurGroup;
    private String baseUnit;

    // Quantities per month
    private BigDecimal currentStock;
    private BigDecimal requirements;

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
        return nameOfPurGroup;
    }

    public void setNameOfPurGroup(String nameOfPurGroup) {
        this.nameOfPurGroup = nameOfPurGroup;
    }

    public String getBaseUnit() {
        return baseUnit;
    }

    public void setBaseUnit(String baseUnit) {
        this.baseUnit = baseUnit;
    }

    public BigDecimal getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(BigDecimal currentStock) {
        this.currentStock = currentStock;
    }

    public BigDecimal getRequirements() {
        return requirements;
    }

    public void setRequirements(BigDecimal requirements) {
        this.requirements = requirements;
    }
}
