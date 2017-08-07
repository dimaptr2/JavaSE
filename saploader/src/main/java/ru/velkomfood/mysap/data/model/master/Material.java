package ru.velkomfood.mysap.data.model.master;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.math.BigDecimal;

@DatabaseTable(tableName = "materials")
public class Material implements Serializable {

    @DatabaseField(id = true, dataType = DataType.BIG_INTEGER)
    private long id;
    @DatabaseField(dataType = DataType.STRING, width = 50)
    private String description;
    @DatabaseField(dataType = DataType.STRING, width = 3)
    private String uom;
    @DatabaseField(columnName = "price_unit", dataType = DataType.BIG_DECIMAL)
    private BigDecimal priceUnit;
    @DatabaseField(dataType = DataType.BIG_DECIMAL)
    private BigDecimal cost;
    @DatabaseField(columnName = "vat_rate", dataType = DataType.INTEGER)
    private int vatRate;

    public Material() { }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUom() {
        return uom;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }

    public BigDecimal getPriceUnit() {
        return priceUnit;
    }

    public void setPriceUnit(BigDecimal priceUnit) {
        this.priceUnit = priceUnit;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public int getVatRate() {
        return vatRate;
    }

    public void setVatRate(int vatRate) {
        this.vatRate = vatRate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Material material = (Material) o;

        return id == material.id;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

}

