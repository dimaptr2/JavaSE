package ru.velkomfood.mrp.server.model;

import java.math.BigDecimal;

/**
 * Created by dpetrov on 29.11.16.
 */
public class Stock {

    private int material;
    private String description;
    private int storagePlace;
    private BigDecimal freeStock;
    private BigDecimal qualityStock;
    private BigDecimal blockedStock;

    public int getMaterial() {
        return material;
    }

    public void setMaterial(int material) {
        this.material = material;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getStoragePlace() {
        return storagePlace;
    }

    public void setStoragePlace(int storagePlace) {
        this.storagePlace = storagePlace;
    }

    public BigDecimal getFreeStock() {
        return freeStock;
    }

    public void setFreeStock(BigDecimal freeStock) {
        this.freeStock = freeStock;
    }

    public BigDecimal getQualityStock() {
        return qualityStock;
    }

    public void setQualityStock(BigDecimal qualityStock) {
        this.qualityStock = qualityStock;
    }

    public BigDecimal getBlockedStock() {
        return blockedStock;
    }

    public void setBlockedStock(BigDecimal blockedStock) {
        this.blockedStock = blockedStock;
    }

}
